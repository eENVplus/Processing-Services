package it.sinergis.ep09.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.geoserver.wps.WPSException;

public class GdalOperation {
	
	private static String GDALDEM = "gdaldem";
	private static String SLOPE = "slope";
	
	private static String GDAL_TRANSLATE = "gdal_translate";
	private static String FLOAT = "Float32";
	private static String GTIFF = "GTiff";
	
	private static String GDAL_CALC = "gdal_calc.py";
	
	private static String GDAL_POLIGONIZE = "gdal_polygonize.py";
	
	private static final Logger LOGGER = Logger.getLogger(GdalOperation.class);
	
	/*
	 * @param inputDem path dem in input
	 * @return outputDem path dem in output
	 */
	public static String slope(String inputDem, String outputDem) {
		
		try {
			//gdaldem slope input_dem output_slope_map" 
			String command = ProjectProperties.loadByName("gdal_path") + GDALDEM + " " + SLOPE + " ";
			
			if (ProjectProperties.loadByName(Constants.IS_WIN) != null
					&& ProjectProperties.loadByName(Constants.IS_WIN).equals("true")) {
				command += "\"" + inputDem + "\" " + outputDem;
			}
			else {
				command += inputDem + " " + outputDem;
			}
			
			LOGGER.debug("comando eseguito " + command);
			ProcessUtils.exec(command, false);
		}
		catch (Exception e) {
			LOGGER.error("errore nel calcolo dello slope per il dem " + inputDem, e);
			e.printStackTrace();
			throw new WPSException("errore during slope for file " + inputDem, e);
		}
		return outputDem;
	}
	
	public static String tpi(String inputRaster, String outputRaster) {
		try {
			
			//gdaldem TPI input output" 
			String command = ProjectProperties.loadByName("gdal_path") + GDALDEM + " TPI ";
			
			if (ProjectProperties.loadByName(Constants.IS_WIN) != null
					&& ProjectProperties.loadByName(Constants.IS_WIN).equals("true")) {
				command += "\"" + inputRaster + "\" " + outputRaster;
			}
			else {
				command += inputRaster + " " + outputRaster;
			}
			LOGGER.debug("comando eseguito " + command);
			ProcessUtils.exec(command, false);
		}
		catch (Exception e) {
			LOGGER.error("errore nel calcolo del TPI per il raster " + inputRaster, e);
			e.printStackTrace();
			throw new WPSException("errore during TPI for file " + inputRaster, e);
		}
		return outputRaster;
	}
	
	/*
	 * esegue gdal_translate
	 * @param inputDem path dem in input
	 * @return outputDem path dem in output
	 */
	public static String translateToFloat(String inputRaster, String outputRaster) {
		
		try {
			// gdal_translate -ot float32 input_file output_file" 
			String command = ProjectProperties.loadByName("gdal_path") + GDAL_TRANSLATE + " -ot " + FLOAT + " "
					+ inputRaster + " " + outputRaster;
			
			LOGGER.debug("comando eseguito " + command);
			ProcessUtils.exec(command, false);
		}
		catch (Exception e) {
			LOGGER.error("errore nella traslazione del " + inputRaster, e);
			e.printStackTrace();
			throw new WPSException("errore during translate for file " + inputRaster, e);
		}
		return outputRaster;
	}
	
	/*
	 * esegue gdal_translate per clip
	 */
	public static String translateForClip(String inputRaster, String outputRaster, String boundingBox) {
		try {
			String command = ProjectProperties.loadByName("gdal_path") + GDAL_TRANSLATE + " -projwin " + boundingBox
					+ " -of GTiff ";
			
			if (ProjectProperties.loadByName(Constants.IS_WIN) != null
					&& ProjectProperties.loadByName(Constants.IS_WIN).equals("true")) {
				command += "\"" + inputRaster + "\" " + outputRaster;
			}
			else {
				command += inputRaster + " " + outputRaster;
			}
			LOGGER.debug("comando eseguito " + command);
			ProcessUtils.exec(command, false);
		}
		catch (Exception e) {
			LOGGER.error("errore nella traslazione per il clip del " + inputRaster, e);
			e.printStackTrace();
			throw new WPSException("errore during translate for file " + inputRaster, e);
		}
		return outputRaster;
	}
	
	/*
	 * esegue gdal_translate per normalizzare il raster
	 * @param inputDem path raster in input
	 * @return outputDem path raster in output
	 */
	public static String translateToNormalize(String inputRaster, String outputRaster, String srs) {
		try {
			// gdal_translate -of GTiff -a_srs EPSG:4326 file1.tif file2.tif
			String command = ProjectProperties.loadByName("gdal_path") + GDAL_TRANSLATE + " --config GDAL_DATA \""
					+ ProjectProperties.loadByName("gdal_data") + "\" -of " + GTIFF + " -a_srs " + srs + " "
					+ inputRaster + " " + outputRaster;
			
			LOGGER.debug("comando eseguito " + command);
			ProcessUtils.exec(command, false);
		}
		catch (Exception e) {
			LOGGER.error("errore nella traslazione del " + inputRaster, e);
			e.printStackTrace();
			throw new WPSException("errore during translate for file " + inputRaster, e);
		}
		return outputRaster;
	}
	
	/*
	 * esegue gdal_calc
	 * @param inputDem path dem in input
	 * @return outputDem path dem in output
	 */
	public static String divide(String inputRaster, String outputRaster, String divide) {
		try {
			String command = Constants.PYTHON;
			if (ProjectProperties.loadByName("gdal_path") != null
					&& !ProjectProperties.loadByName("gdal_path").equals("")) {
				command += " " + ProjectProperties.loadByName("gdal_path") + GDAL_CALC;
			}
			else {
				command += " " + GDAL_CALC;
			}
			//python gdal_calc.py -A inputRaster --output outputRaster --calc=\input_raster/14" 
			command += " -A " + inputRaster + " --outfile=" + outputRaster + " --calc=A/" + divide;
			
			LOGGER.debug("comando eseguito " + command);
			ProcessUtils.exec(command, false);
		}
		catch (Exception e) {
			LOGGER.error("errore nella divisione del " + inputRaster, e);
			e.printStackTrace();
			throw new WPSException("errore during divide for file " + inputRaster, e);
		}
		return outputRaster;
	}
	
	/*
	 * esegue gdal_calc
	 * @param inputDem path dem in input
	 * @return outputDem path dem in output
	 */
	public static String winDivide(String inputRaster, String outputRaster, String divide) {
		try {
			//python gdal_calc.py -A inputRaster --output outputRaster --calc=\input_raster/14" 
			String command = Constants.PYTHON + " \"" + ProjectProperties.loadByName("gdal_path") + GDAL_CALC
					+ "\" -A " + inputRaster + " --outfile=" + outputRaster + " --calc=\"A/" + divide + "\"";
			
			LOGGER.debug("comando eseguito " + command);
			ProcessUtils.exec(command, false);
		}
		catch (Exception e) {
			LOGGER.error("errore nella divisione del " + inputRaster, e);
			e.printStackTrace();
			throw new WPSException("errore during divide for file " + inputRaster, e);
		}
		return outputRaster;
	}
	
	/*
	 * esegue gdal_calc per il calcolo del rasterCalculator per ubuntu
	 */
	public static String rasterCalculator(String inputGeologyRaster, String inputDemRaster,
			String inputLandcoverRaster, String outputRaster) {
		String command = Constants.PYTHON;
		try {
			if (ProjectProperties.loadByName("gdal_path") != null
					&& !ProjectProperties.loadByName("gdal_path").equals("")) {
				command += " " + ProjectProperties.loadByName("gdal_path") + GDAL_CALC + "";
			}
			else {
				command += " " + GDAL_CALC;
			}
			
			//python gdal_calc.py -A DEMRaster -B GEOLOGYRaster -C LANDCOVERRaster --output outputRaster --calc=LANDCOVER*0.375 + DEM*0.3125 + GEOLOGY*0.3125" 
			command += " -A " + inputDemRaster + " -B " + inputGeologyRaster + " -C " + inputLandcoverRaster
					+ " --outfile=" + outputRaster + " --calc=C*"
					+ ProjectProperties.loadByName(Constants.MOLTIPLICATORE_LANDCOVER) + " + A*"
					+ ProjectProperties.loadByName(Constants.MOLTIPLICATORE_DEM) + " + B*"
					+ ProjectProperties.loadByName(Constants.MOLTIPLICATORE_GEOLOGY) + " ";
			
			LOGGER.debug("comando eseguito " + command);
			ProcessUtils.exec(command, false);
		}
		catch (Exception e) {
			LOGGER.error("errore nel calcolo del raster calculator, comando eseguito " + command, e);
			e.printStackTrace();
			throw new WPSException("errore during raster calculator", e);
		}
		return outputRaster;
	}
	
	/*
	 * esegue gdal_calc per il calcolo del rasterCalculator
	 */
	public static String winRasterCalculator(String inputGeologyRaster, String inputDemRaster,
			String inputLandcoverRaster, String outputRaster) {
		String command = "";
		try {
			//python gdal_calc.py -A DEMRaster -B GEOLOGYRaster -C LANDCOVERRaster --output outputRaster --calc=LANDCOVER*0.375 + DEM*0.3125 + GEOLOGY*0.3125" 
			command = Constants.PYTHON + " \"" + ProjectProperties.loadByName("gdal_path") + GDAL_CALC + "\" -A "
					+ inputDemRaster + " -B " + inputGeologyRaster + " -C " + inputLandcoverRaster + " --outfile="
					+ outputRaster + " --calc=\"C*" + ProjectProperties.loadByName(Constants.MOLTIPLICATORE_LANDCOVER)
					+ " + A*" + ProjectProperties.loadByName(Constants.MOLTIPLICATORE_DEM) + " + B*"
					+ ProjectProperties.loadByName(Constants.MOLTIPLICATORE_GEOLOGY) + " \"";
			
			LOGGER.debug("comando eseguito " + command);
			ProcessUtils.exec(command, false);
		}
		catch (Exception e) {
			LOGGER.error("errore nel calcolo del raster calculator, comando eseguito " + command, e);
			e.printStackTrace();
			throw new WPSException("errore during raster calculator", e);
		}
		return outputRaster;
	}
	
	public static String gdalCalc(String strInputLayer, String outputRaster, String calc) {
		String command = Constants.PYTHON;
		try {
			if (ProjectProperties.loadByName(Constants.IS_WIN) != null
					&& ProjectProperties.loadByName(Constants.IS_WIN).equals("true")) {
				command += " \"" + ProjectProperties.loadByName("gdal_path") + GDAL_CALC + "\"" + strInputLayer
						+ " --outfile=" + outputRaster + " --calc=\"" + calc + " \" --NoDataValue -1";
				
				LOGGER.debug("comando eseguito " + command);
				ProcessUtils.exec(command, false);
			}
			else {
				if (ProjectProperties.loadByName("gdal_path") != null
						&& !ProjectProperties.loadByName("gdal_path").equals("")) {
					command += " " + ProjectProperties.loadByName("gdal_path") + GDAL_CALC + "";
				}
				else {
					command += " " + GDAL_CALC;
				}
				
				command += strInputLayer + " --outfile=" + outputRaster + " --calc=" + calc + " --NoDataValue -1";
				
				LOGGER.debug("comando eseguito " + command);
				ProcessUtils.exec(command, false);
				
			}
		}
		catch (Exception e) {
			LOGGER.error("errore nel calcolo del raster calculator, comando eseguito " + command, e);
			e.printStackTrace();
			throw new WPSException("errore during raster calculator", e);
		}
		return outputRaster;
	}
	
	public static String gdalPolygonize(String inputRaster, String vectorName) {
		String command = Constants.PYTHON;
		
		String pathFile = ProjectProperties.loadByName(Constants.TMP_PATH)
				+ ProjectProperties.loadByName(Constants.TMP_SHAPE) + vectorName + ".shp";
		String result = "";
		if (ProjectProperties.loadByName(Constants.IS_WIN) != null
				&& ProjectProperties.loadByName(Constants.IS_WIN).equals("true")) {
			try {
				command += " \"" + ProjectProperties.loadByName("gdal_path") + GDAL_POLIGONIZE + "\" " + "\""
						+ inputRaster + "\"  -f \"ESRI Shapefile\" \"" + pathFile;
				
				LOGGER.info("comando eseguito " + command);
				System.out.println("GDAL: " + command);
				result = ProcessUtils.exec(command, false);
			}
			catch (Exception e) {
				LOGGER.error("errore nella conversione del raster in vettoriale, comando eseguito " + command, e);
				e.printStackTrace();
				throw new WPSException("errore during raster conversion", e);
			}
		}
		else {
			List<String> arrayCmd = new ArrayList<String>();
			String[] cmdStr = null;
			try {
				arrayCmd.add(Constants.PYTHON);
				if (ProjectProperties.loadByName("gdal_path") != null
						&& !ProjectProperties.loadByName("gdal_path").equals("")) {
					arrayCmd.add(ProjectProperties.loadByName("gdal_path") + GDAL_POLIGONIZE);
				}
				else {
					arrayCmd.add(GDAL_POLIGONIZE);
				}
				arrayCmd.add(inputRaster);
				arrayCmd.add("-f");
				arrayCmd.add("ESRI\\ Shapefile");
				arrayCmd.add(pathFile);
				
				cmdStr = arrayCmd.toArray(new String[arrayCmd.size()]);
				LOGGER.info("comando eseguito " + Arrays.toString(cmdStr));
				System.out.println("GDAL: " + Arrays.toString(cmdStr));;
				
				result = ProcessUtils.execArrayString(cmdStr, false);
			}
			catch (Exception e) {
				LOGGER.error("errore nella conversione del raster in vettoriale, comando eseguito " +  Arrays.toString(cmdStr), e);
				e.printStackTrace();
				throw new WPSException("errore during raster conversion", e);
			}
			
			try {
				result = ShapeUtils.zipShpResult(pathFile);
			}
			catch (Exception e) {
				LOGGER.error("errore nella creazione dello zip dello shape", e);
				e.printStackTrace();
				throw new WPSException("errore during creation shapefile zip", e);
			}
		}
		return result;
	}
}
