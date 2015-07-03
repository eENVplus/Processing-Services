package it.sinergis.ep02;

import it.sinergis.ep02.utils.Constants;
import it.sinergis.ep02.utils.FileUtils;
import it.sinergis.ep02.utils.ProjectProperties;
import it.sinergis.utils.ProcessUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

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
public class UploadFileServlet extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8050017857623874785L;
	
	private static final Logger LOGGER = Logger.getLogger(UploadFileServlet.class);
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		long time = System.currentTimeMillis();
		
		response.setContentType("text/html;charset=UTF-8");
		String requestURL = request.getRequestURL().toString();
		if (requestURL.indexOf(request.getServletPath()) != -1) {
			requestURL = requestURL.substring(0, requestURL.indexOf(request.getServletPath()));
		}
		
		// parametri in input
		final Part fileXslPart = request.getPart("fileXls");
		//final Part fileXlsData = request.getPart("fileXlsData");
		final Part fileShpPart = request.getPart("fileShp");
		final String regions = request.getParameter("regions");
		
		try {
			checkInput(fileXslPart, fileShpPart, regions);
		}
		catch (Exception e1) {
			response.getWriter().write(ProjectProperties.loadByNameFromMessages("missing_parameters"));
			LOGGER.error("parametri mancanti in input ", e1);
			e1.printStackTrace();
			return;
		}
		
		Path tempDir = Files.createTempDirectory("eenvplus_temp_");
		
		String uploadPath = tempDir + File.separator + regions + File.separator;
		String exportPath = ProjectProperties.loadByName(Constants.EXPORT_PATH);
		
		if (FileUtils.uploadFile(fileXslPart, uploadPath) && FileUtils.uploadFile(fileShpPart, uploadPath)) {
			String shapeFileName = FileUtils.getFirstFileInFolderWithExtension(uploadPath, ".shp");
			
			String zoneLoad = ProjectProperties.loadByName(Constants.PYTHON_EXEC) + " ";
			zoneLoad += ProjectProperties.loadByName(Constants.ISPRA_PY_PATH) + File.separator;
			zoneLoad += "zone_loader.py ";
			zoneLoad += "-a load -r " + regions + " ";
			zoneLoad += "--xls " + uploadPath + FileUtils.getFileNameByPart(fileXslPart) + " ";
			zoneLoad += "--shp " + uploadPath + shapeFileName + " ";
			zoneLoad += "-s " + ProjectProperties.loadByName(Constants.POSTGRES_SERVER) + " ";
			zoneLoad += "-u " + ProjectProperties.loadByName(Constants.POSTGRES_USER) + " ";
			zoneLoad += "-p " + ProjectProperties.loadByName(Constants.POSTGRES_PASSWORD) + " ";
			zoneLoad += "-db " + ProjectProperties.loadByName(Constants.POSTGRES_DB) + " ";
			
			String xmlGenerator = ProjectProperties.loadByName(Constants.PYTHON_EXEC) + " ";
			xmlGenerator += ProjectProperties.loadByName(Constants.ISPRA_PY_PATH) + File.separator;
			xmlGenerator += "xml_generator.py ";
			xmlGenerator += "-r " + regions + " ";
			xmlGenerator += "-o " + exportPath + time + ".gml ";
			xmlGenerator += "-t " + ProjectProperties.loadByName(Constants.TEMPLATE_PATH) + " ";
			xmlGenerator += "-s " + ProjectProperties.loadByName(Constants.POSTGRES_SERVER) + " ";
			xmlGenerator += "-u " + ProjectProperties.loadByName(Constants.POSTGRES_USER) + " ";
			xmlGenerator += "-p " + ProjectProperties.loadByName(Constants.POSTGRES_PASSWORD) + " ";
			xmlGenerator += "-db " + ProjectProperties.loadByName(Constants.POSTGRES_DB) + " ";
			
			LOGGER.info(zoneLoad);
			LOGGER.info(xmlGenerator);
			
			ProcessUtils p = new ProcessUtils();
			
			try {
				String resultZoneLoad = p.exec(zoneLoad, true);
				String resultXmlGenerator = p.exec(xmlGenerator, true);
				String reportFile = exportPath + "report_" + time + ".txt";
				
				//scrivo il report
				FileUtils.writeReportFile(resultZoneLoad + resultXmlGenerator, reportFile);
				
				PrintWriter out = response.getWriter();
				writeResponse(out, requestURL, "report_" + time + ".txt", time + "");
				
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	
	private void writeResponse(PrintWriter response, String requestURL, String reportName, String gmlName) {
		// then write the data of the response
		response.println("<html lang=\"en\">" + "<head><title>Eenvplus</title></head>");
		
		// then write the data of the response
		response.println("<body  bgcolor=\"#ffffff\" style=\"text-align: center;\">"
				+ "<img src=\"img/logoEnv.png\" /><br>" + "<form method=\"get\" action=\"" + requestURL + "/export/"
				+ reportName + "\">"
				+ "<p>For report click this button</p><button type=\"submit\" target=\"_blank\">View</button>"
				+ "</form>");
		
		response.println("<body  bgcolor=\"#ffffff\">" + "<form method=\"get\" action=\"" + requestURL + "/export/"
				+ gmlName + ".gml\">" + "<p>For gml click this button</p><button type=\"submit\">View</button>"
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
	private void checkInput(Part fileXlsPart, Part fileShpPart, String regions) throws Exception {
		LOGGER.debug("regions " + regions);
		if (regions == null || regions.equals("")) {
			throw new Exception(ProjectProperties.loadByNameFromMessages("empty_regions"));
		}
		
		if (fileXlsPart == null) {
			throw new Exception(ProjectProperties.loadByNameFromMessages("empty_xls"));
		}
		
		if (fileShpPart == null) {
			throw new Exception(ProjectProperties.loadByNameFromMessages("empty_shp"));
		}
	}
	
	// inner class, generic extension filter
	public class GenericExtFilter implements FilenameFilter {
		
		private String ext;
		
		public GenericExtFilter(String ext) {
			this.ext = ext;
		}
		
		public boolean accept(File dir, String name) {
			return (name.endsWith(ext));
		}
	}
	
}
