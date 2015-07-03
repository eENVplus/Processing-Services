package it.sinergis.ep09.flooding.procedure;

import java.io.File;

import it.sinergis.ep09.utils.Constants;
import it.sinergis.ep09.utils.GdalOperation;
import it.sinergis.ep09.utils.ProcessUtils;
import it.sinergis.ep09.utils.ProjectProperties;

import org.apache.log4j.Logger;
import org.geoserver.wps.WPSException;
import org.geotools.coverage.grid.GridCoverage2D;

public class DemProcedure {
	
	private static final Logger LOGGER = Logger.getLogger(DemProcedure.class);
	
	public GridCoverage2D inputDem;
	public String boundingBox;
	
	public DemProcedure(GridCoverage2D dem, String boundingBox) {
		this.inputDem = dem;
		this.boundingBox = boundingBox;
	}
	
	public String executeFirstProcedure() {
		//se il calcolo del raster dem non è già stato fatto  eseguo la procedura
		
		String rasterPathFile = null;
		String slopeOutputFile = null;
		String slopeSelectionOutputFile = null;
		
		try {
			rasterPathFile = ProjectProperties.loadByName(Constants.TMP_PATH) + inputDem.getName() + "_"+System.currentTimeMillis()
					+ Constants.TIF_EXTENSION;
			//scrivo il raster su file in modo da poter usare gdal
			LOGGER.debug("inizio scrittura raster " + inputDem.getName() + " in " + rasterPathFile);
			boolean resultWrite = ProcessUtils.writeRasterSuFile(rasterPathFile, inputDem);
			LOGGER.debug("fine scrittura raster " + inputDem.getName() + " in " + rasterPathFile);
			
			//rasterPathFile = GeoserverUtils.getRasterPath(catalog, inputDem);
			
			//1) slope
			if (resultWrite) {
				slopeOutputFile = slope(rasterPathFile);
			}
			
			//2) slope selection
			if (slopeOutputFile != null) {
				slopeSelectionOutputFile = slopeSelection(slopeOutputFile);
			}
			
		}
		catch (Exception e) {
			throw new WPSException(e.getMessage());
		}
		finally {
			if (slopeOutputFile != null) {
				ProcessUtils.deleteTmpFile(slopeOutputFile);
			}
			if (rasterPathFile != null) {
				ProcessUtils.deleteTmpFile(rasterPathFile);
			}
		}
		
		return slopeSelectionOutputFile;
	}
	
	/*
	 * slope
	 * C:\Program Files\QGIS Dufour\bin\gdaldem slope "C:\temp\dem3_1406722080212.tif" C:\temp\dem3_slope_1406722080282.tif
	 */
	private String slope(String inputFile) {
		LOGGER.debug("inizio slope raster " + inputFile);
		String slopeOutputFile = ProjectProperties.loadByName(Constants.TMP_PATH) + inputDem.getName() + "_"
				+ Constants.SLOPE + "_" + System.currentTimeMillis() + Constants.TIF_EXTENSION;
		GdalOperation.slope(inputFile, slopeOutputFile);
		LOGGER.debug("fine slope " + slopeOutputFile);
		return slopeOutputFile;
	}
	
	/*
	 * slope selection
	 * python "C:\Program Files\QGIS Dufour\bin\gdal_calc.py" -A C:\temp\dem3_slope_1406722080282.tif --outfile=C:\temp\dem3_slopeSelection_1406722080476.tif --calc="A<=7 " --NoDataValue -1
	 */
	private String slopeSelection(String inputFile) {
		LOGGER.debug("inizio slope selection raster " + inputFile);
		String strInputLayer = " -A " + inputFile;
		String slopeSelectionOutputFile = ProjectProperties.loadByName(Constants.TMP_PATH) + inputDem.getName() + "_"
				+ Constants.SLOPE_SELECTION + "_" + System.currentTimeMillis() + Constants.TIF_EXTENSION;
		GdalOperation.gdalCalc(strInputLayer, slopeSelectionOutputFile,
				"A" + ProjectProperties.loadByName(Constants.SLOPE_SELECTION));
		LOGGER.debug("fine slope selection" + slopeSelectionOutputFile);
		return slopeSelectionOutputFile;
	}
	
	/*
	 * clip del raster -> dava errore di dimensione diversa tra raster
	 */
	private String clip(String inputFile) {
//		if (rasterPathFile != null) {
//			LOGGER.debug("inizio clip raster " + rasterPathFile);
//			clipRasterFile = ProjectProperties.loadByName(Constants.TMP_PATH) + inputDem.getName() + "_"
//					+ Constants.SLOPE_SELECTION + "_" + System.currentTimeMillis() + Constants.TIF_EXTENSION;
//			slopeOutputFile = GdalOperation.translateForClip(rasterPathFile, clipRasterFile, boundingBox);
//			LOGGER.debug("fine clip " + slopeOutputFile);
//		}
		return null;
	}
	
	public String executeSecondProcedure(String slopeSelectionOutputFile, String alluviumFile) {
		String lowProneAreaOutputFile = null;
		String mergeClassLowFile = null;
		
		try {
			
			//select low prone area
			if (slopeSelectionOutputFile != null) {
				lowProneAreaOutputFile = selectLowProneArea(slopeSelectionOutputFile, alluviumFile);
			}
			
			//4) merge class low
			if (lowProneAreaOutputFile != null) {
				mergeClassLowFile = mergeClassLow(lowProneAreaOutputFile, getDemName(slopeSelectionOutputFile));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new WPSException(e.getMessage());
		}
		finally {
			if (lowProneAreaOutputFile != null) {
				ProcessUtils.deleteTmpFile(lowProneAreaOutputFile);
			}
		}
		
		return mergeClassLowFile;
	}
	
	/*
	 * select low prone area
	 * python "C:\Program Files\QGIS Dufour\bin\gdal_calc.py" -A C:\temp\dem3_slopeSelection_1406722080476.tif -B C:\temp\geology_new_alluviumClass_1406722096796.tif --outfile=C:\temp\dem3_lowProneArea_1406722119611.tif --calc="(A==0)*B " --NoDataValue -1
	 */
	private String selectLowProneArea(String slopeSelectionOutputFile, String alluviumFile) {
		LOGGER.debug("inizio select low prone area");
		String strInputLayer = " -A " + slopeSelectionOutputFile + " -B " + alluviumFile;
		String lowProneAreaOutputFile = ProjectProperties.loadByName(Constants.TMP_PATH) + getDemName(slopeSelectionOutputFile) + "_"
				+ Constants.LOW_PRONE_AREA + "_" + System.currentTimeMillis() + Constants.TIF_EXTENSION;
		GdalOperation.gdalCalc(strInputLayer, lowProneAreaOutputFile,
				"(A" + ProjectProperties.loadByName(Constants.LOW_PRONE_AREA) + ")*B");
		LOGGER.debug("fine select low prone area, file generato "+lowProneAreaOutputFile);
		return lowProneAreaOutputFile;
	}
	
	/*
	 * merge class low
	 */
	private String mergeClassLow(String inputFile, String demName) {
		LOGGER.debug("inizio merge class low");
		String strInputLayer = " -A " + inputFile;
		String mergeClassLowFile = ProjectProperties.loadByName(Constants.TMP_PATH) + demName
				+ "_MergeClassLow_" + System.currentTimeMillis() + Constants.TIF_EXTENSION;
		GdalOperation.gdalCalc(strInputLayer, mergeClassLowFile, "(A>=1)*1");
		LOGGER.debug("fine merge class low, files generato: " + mergeClassLowFile);
		return mergeClassLowFile;
	}
	
	/*
	 * nel caso in cui prendo il file dal fs leggo il nome del file, altrimenti uso quello che arriva dal dem in input
	 */
	private String getDemName(String fileOutput) {
		if (inputDem == null || inputDem.getName() == null) {
			File f = new File(fileOutput);
			String demName = f.getName();
			if (f.getName().indexOf(".")!=-1) {
				return demName.substring(0, demName.indexOf("."));
			}
		} 
		return inputDem.getName().toString();
	}
}
