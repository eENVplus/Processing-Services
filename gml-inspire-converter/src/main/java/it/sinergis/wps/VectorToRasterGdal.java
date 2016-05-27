package it.sinergis.wps;

import it.sinergis.utils.Constants;
import it.sinergis.utils.ProjectPropertiesUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.geoserver.catalog.Catalog;
import org.geoserver.wps.WPSException;
import org.geoserver.wps.jts.SpringBeanProcessFactory;
import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;
import org.opengis.util.ProgressListener;

@DescribeProcess(title = "vectorToRasterGdal", description = "Service to convert raster layer into a vector layer using gdal_polygonize")
public class VectorToRasterGdal extends SpringBeanProcessFactory {
	
	private Catalog catalog;
	private Properties properties = null;
	
	private static String GDAL_POLIGONIZE = "gdal_polygonize.py";
	
	private static final Logger LOGGER = Logger.getLogger(VectorToRasterGdal.class);
	
	public VectorToRasterGdal(Catalog catalog) {
		super(Constants.EENV_TITLE, Constants.EENV_NAMESPACE, VectorToRasterGdal.class);
		this.catalog = catalog;
	}
	
	@DescribeResult(name = "result", description = "output result")
	public String execute(
			@DescribeParameter(name = "inputRaster", description = "path of raster layer") String inputRaster,
			@DescribeParameter(name = "outputVector", description = "path of vector layer") String outputVector,
			//			@DescribeParameter(name = "outputFormat", description = "format of output vector layer", min = 0) String format,
			ProgressListener progressListener) {
		
		String command = Constants.PYTHON;
		String result;
		try {
			if (ProjectPropertiesUtils.loadByName(Constants.IS_WIN) != null
					&& ProjectPropertiesUtils.loadByName(Constants.IS_WIN).equals("true")) {
				command += " \"" + ProjectPropertiesUtils.loadByName(Constants.GDAL_PATH) + GDAL_POLIGONIZE + "\" "
						+ "\"" + inputRaster + "\" \"" + outputVector + "\"";
				
				LOGGER.debug("comando eseguito " + command);
				result = exec(command, false);
			}
			else {
				if (ProjectPropertiesUtils.loadByName(Constants.GDAL_PATH) != null
						&& !ProjectPropertiesUtils.loadByName(Constants.GDAL_PATH).equals("")) {
					command += " " + ProjectPropertiesUtils.loadByName(Constants.GDAL_PATH) + GDAL_POLIGONIZE + "";
				}
				else {
					command += " " + GDAL_POLIGONIZE;
				}
				command += " " + inputRaster + " " + outputVector;
				LOGGER.debug("comando eseguito " + command);
				result = exec(command, false);
				
			}
		}
		catch (Exception e) {
			LOGGER.error("errore nella conversione del raster in vettoriale, comando eseguito " + command, e);
			e.printStackTrace();
			throw new WPSException("errore during raster conversion", e);
		}
		return result;
	}
	
	public String exec(String command, boolean getOutput) throws Exception {
		
		Runtime run = Runtime.getRuntime();
		Process pp = run.exec(command);
		
		StringBuffer response = new StringBuffer();
		BufferedReader streamReader = null;
		if (getOutput) {
			//se il processo restituisce in output delle informazioni e non solo degli errori (vedi zonal_stat.py)
			streamReader = new BufferedReader(new InputStreamReader(pp.getInputStream()));
		}
		else {
			streamReader = new BufferedReader(new InputStreamReader(pp.getErrorStream()));
		}
		
		for (String line; (line = streamReader.readLine()) != null;) {
			LOGGER.debug("log di gdal " + line);
			if (!getOutput && (line.toLowerCase().contains("warning") || !line.toLowerCase().contains("error"))) {
				continue;
			}
			response.append(line);
		}
		if (!getOutput) {
			if (!response.toString().equals("")) {
				LOGGER.error("errore di gdal " + response.toString());
				throw new Exception(response.toString());
			}
		}
		
		return response.toString();
	}
	
}
