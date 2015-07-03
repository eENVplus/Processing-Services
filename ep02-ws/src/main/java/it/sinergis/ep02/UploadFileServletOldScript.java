package it.sinergis.ep02;

import it.sinergis.ep02.utils.Constants;
import it.sinergis.ep02.utils.FileUtils;
import it.sinergis.ep02.utils.ProjectProperties;
import it.sinergis.utils.ProcessUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.log4j.Logger;

@WebServlet(name = "UploadFileServlet", urlPatterns = { "/upload" })
@MultipartConfig
public class UploadFileServletOldScript extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8050017857623874785L;
	
	private static final Logger LOGGER = Logger.getLogger(UploadFileServletOldScript.class);
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		
		response.setContentType("text/html;charset=UTF-8");
		String requestURL = request.getRequestURL().toString();
		if (requestURL.indexOf(request.getServletPath())!=-1) {
			requestURL = requestURL.substring(0, requestURL.indexOf(request.getServletPath()));
		}
		
		// parametri in input
		final Part fileXslPart = request.getPart("fileXls");
		final Part fileXlsData = request.getPart("fileXlsData");
		final Part fileShpPart = request.getPart("fileShp");
		final String regions = request.getParameter("regions");
		
		try {
			checkInput(fileXslPart, fileShpPart, fileXlsData, regions);
		}
		catch (Exception e1) {
			response.getWriter().write(ProjectProperties.loadByNameFromMessages("missing_parameters"));
			LOGGER.error("parametri mancanti in input ", e1);
			e1.printStackTrace();
			return;
		}
		
		long time = System.currentTimeMillis();
		
		String uploadPath = ProjectProperties.loadByName(Constants.PATH_PY_DATA) + File.separator + regions
				+ File.separator;
		if (FileUtils.uploadFile(fileXslPart, uploadPath) && FileUtils.uploadFile(fileShpPart, uploadPath)
				&& FileUtils.uploadFile(fileXlsData, uploadPath)) {
			
			ProcessUtils p = new ProcessUtils();
			try {
				
				//import nel db
				String resultImport = p.exec("python " + ProjectProperties.loadByName(Constants.ISPRA_PY_PATH)
						+ File.separator + "importer.py", true);
				
				String reportFile = ProjectProperties.loadByName(Constants.EXPORT_PATH) + "report_" + time + ".txt";
				if (resultImport != null && !resultImport.equals("")) {
					//scrivo il report
					FileUtils.writeReportFile(resultImport, reportFile);
				}
				
				//creazione file xml
				p.execArrayStr(new String[] { "python",
						ProjectProperties.loadByName(Constants.ISPRA_PY_PATH) + File.separator + "exporter.py",
						 ProjectProperties.loadByName(Constants.EXPORT_PATH) + File.separator + time + ".xml" }, true);
				
				PrintWriter out = response.getWriter();
				writeResponse(out,requestURL, "report_" + time + ".txt", time+"");
			}
			catch (Exception e) {
				LOGGER.error("errore nell'esecuzione degli script py", e);
				e.printStackTrace();
			}
			finally {
					try {
						//cancellazione dei file uploded
						LOGGER.info("cancellazione files");
						File files = new File(ProjectProperties.loadByName(Constants.PATH_PY_DATA) + File.separator);
						LOGGER.debug("FILES " + files.getAbsolutePath() + " " + files.getPath());
						LOGGER.debug("FILES length " + files.listFiles().length);
						if (files != null && files.listFiles() != null) {
							for (File file : files.listFiles()) {
								FileUtils.delete(file);
							}
						}
					}
					catch (Exception e) {
						LOGGER.error("errore nella cancellazione dei files", e);
						e.printStackTrace();
					}
				try {
					//cancellazione tabelle dal db
					LOGGER.info("cancellazione tabelle");
					p.execArrayStr(
							new String[] {
									"python",
									ProjectProperties.loadByName(Constants.ISPRA_PY_PATH) + File.separator
											+ "deleteTable.py" }, true);
				}
				catch (Exception e) {
					LOGGER.error("errore nella cancellazione delle tabelle", e);
					e.printStackTrace();
				}
			}
		}
	}
	
	private void writeResponse(PrintWriter response, String requestURL, String reportName, String gmlName) {
		// then write the data of the response
		response.println("<html lang=\"en\">" + "<head><title>Eenvplus</title></head>");
		
		// then write the data of the response
		response.println("<body  bgcolor=\"#ffffff\" style=\"text-align: center;\">"
				+ "<img src=\"img/logoEnv.png\" /><br>"
				+ "<form method=\"get\" action=\""+requestURL + "/export/"+reportName+"\">"
				+ "<p>For report click this button</p><button type=\"submit\" target=\"_blank\">View</button>"
				+ "</form>");
		
		response.println("<body  bgcolor=\"#ffffff\">"
				+ "<form method=\"get\" action=\""+requestURL + "/export/"+gmlName+".xml\">"
				+ "<p>For gml click this button</p><button type=\"submit\">View</button>"
				+ "</form>");
	}
	
	/**
	 * metodo che controlla l'input
	 * 
	 * @param path
	 * @param fileXlsPart
	 * @param fileShpPart
	 * @param regions
	 * @throws Exception
	 */
	private void checkInput(Part fileXlsPart, Part fileShpPart, Part fileXlsData, String regions)
			throws Exception {
		LOGGER.debug("regions " + regions);
		if (regions == null || regions.equals("")) {
			throw new Exception(ProjectProperties.loadByNameFromMessages("empty_regions"));
		}
		
		if (fileXlsPart == null) {
			throw new Exception(ProjectProperties.loadByNameFromMessages("empty_xls"));
		}
		
		if (fileXlsData == null) {
			throw new Exception(ProjectProperties.loadByNameFromMessages("empty_xls"));
		}
		
		if (fileShpPart == null) {
			throw new Exception(ProjectProperties.loadByNameFromMessages("empty_shp"));
		}
	}
}
