package it.sinergis.ep09.landslide.procedure;

import it.sinergis.ep09.utils.Constants;
import it.sinergis.ep09.utils.GdalOperation;
import it.sinergis.ep09.utils.ProcessUtils;
import it.sinergis.ep09.utils.ProjectProperties;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.geoserver.wps.WPSException;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.process.raster.RangeLookupProcess;
import org.jaitools.numeric.Range;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.ProgressListener;

public class DemProcedure extends Procedure {
	
	private static final Logger LOGGER = Logger.getLogger(DemProcedure.class);
	
	public GridCoverage2D inputDem;
	public ProgressListener progressListener;
	
	public DemProcedure(GridCoverage2D dem) {
		this.inputDem = dem;
	}
	
	public String executeProcedure() {
		//se il calcolo del raster dem non è già stato fatto  eseguo la procedura
		
		String rasterPathFile = null;
		String slopeOutputFile = null;
		String reclassifyRasterFile = null;
		String translateOutputRaster = null;
		String demFilePath = null;
		
		try {
			rasterPathFile = ProjectProperties.loadByName(Constants.TMP_PATH) + inputDem.getName()
					+ Constants.TIF_EXTENSION;
			//scrivo il raster su file in modo da poter usare gdal
			LOGGER.debug("inizio scrittura raster " + inputDem.getName() + " in " + rasterPathFile);
			boolean resultWrite = ProcessUtils.writeRasterSuFile(rasterPathFile, inputDem);
			LOGGER.debug("fine scrittura raster " + inputDem.getName() + " in " + rasterPathFile);
			
			//1) slope
			if (resultWrite) {
				LOGGER.debug("inizio slope raster " + rasterPathFile);
				slopeOutputFile = ProjectProperties.loadByName(Constants.TMP_PATH) + inputDem.getName() + "_"
						+ Constants.SLOPE + "_" + System.currentTimeMillis() + Constants.TIF_EXTENSION;
				slopeOutputFile = GdalOperation.slope(rasterPathFile, slopeOutputFile);
				LOGGER.debug("fine slope " + slopeOutputFile);
			}
			
			//2) reclassify
			if (slopeOutputFile != null) {
				LOGGER.debug("inizio reclassify raster " + slopeOutputFile);
				GridCoverage2D rasterReclassify = reclassify(new File(slopeOutputFile), 0, getRangeList(),
						getOutputRange(), inputDem.getCoordinateReferenceSystem(), progressListener);
				reclassifyRasterFile = ProjectProperties.loadByName(Constants.TMP_PATH) + inputDem.getName() + "_"
						+ Constants.RECLASSIFY + "_" + System.currentTimeMillis() + Constants.TIF_EXTENSION;
				ProcessUtils.writeRasterSuFile(reclassifyRasterFile, rasterReclassify);
				
				LOGGER.debug("fine reclassify raster " + reclassifyRasterFile);
			}
			
			//3) float
			if (reclassifyRasterFile != null) {
				LOGGER.debug("inizio translate raster " + reclassifyRasterFile);
				translateOutputRaster = ProjectProperties.loadByName(Constants.TMP_PATH) + inputDem.getName() + "_"
						+ Constants.FLOAT + "_" + System.currentTimeMillis() + Constants.TIF_EXTENSION;
				GdalOperation.translateToFloat(reclassifyRasterFile, translateOutputRaster);
				LOGGER.debug("fine translate raster " + translateOutputRaster);
			}
			
			//4) divide
			if (translateOutputRaster != null) {
				LOGGER.debug("inizio divide raster " + translateOutputRaster);
				demFilePath = ProjectProperties.loadByName(Constants.TMP_PATH) + inputDem.getName() + "_"
						+ Constants.DIVIDE + "_" + System.currentTimeMillis() + Constants.TIF_EXTENSION;
				if (ProjectProperties.loadByName(Constants.IS_WIN) != null
						&& ProjectProperties.loadByName(Constants.IS_WIN).equals("true")) {
					GdalOperation.winDivide(translateOutputRaster, demFilePath,
							ProjectProperties.loadByName(Constants.DEM_DIVISORE));
				}
				else {
					GdalOperation.divide(translateOutputRaster, demFilePath,
							ProjectProperties.loadByName(Constants.DEM_DIVISORE));
				}
				
				LOGGER.debug("fine divide raster " + demFilePath);
			}
		}
		catch (Exception e) {
			LOGGER.error("error for dem procedure ", e);
			throw new WPSException(e.getMessage());
		}
		finally {
			if (rasterPathFile != null) {
				ProcessUtils.deleteTmpFile(rasterPathFile);
			}
			if (slopeOutputFile != null) {
				ProcessUtils.deleteTmpFile(slopeOutputFile);
			}
			if (reclassifyRasterFile != null) {
				ProcessUtils.deleteTmpFile(reclassifyRasterFile);
			}
			if (translateOutputRaster != null) {
				ProcessUtils.deleteTmpFile(translateOutputRaster);
			}
		}
		
		return demFilePath;
	}
	
	public GridCoverage2D reclassify(File file, Integer band, List<Range> ranges, int[] outputRange,
			CoordinateReferenceSystem coords, ProgressListener listener) {
		RangeLookupProcess rangeLookupProcess = new RangeLookupProcess();
		
		GridCoverage2D rasterReclassify = rangeLookupProcess.execute(
				ProcessUtils.getGridCoverageFromFile(file, coords), band, ranges, outputRange, null, listener);
		
		return rasterReclassify;
		
	}
	
	/*
	 * recupera la lista dei range dal file di configurazione (es: from_1 to_1; from_2 to_2;... from_n to_n;)
	 */
	public static List<Range> getRangeList() {
		List<Range> rangeList = new ArrayList<Range>();
		
		String rangeDem = ProjectProperties.loadByName(Constants.RANGE_DEM);
		if (rangeDem != null && !rangeDem.equals("")) {
			//splitto per ;
			String[] commaSplitRangeList = rangeDem.split(";");
			if (commaSplitRangeList != null && !commaSplitRangeList.equals("")) {
				for (String spaceRange : commaSplitRangeList) {
					//splitto per spazi
					String[] spaceSplitRangeList = spaceRange.split(" ");
					
					if (spaceSplitRangeList != null && !spaceSplitRangeList.equals("")
							&& spaceSplitRangeList[0] != null && spaceSplitRangeList[1] != null) {
						Integer firstRange = Integer.parseInt(spaceSplitRangeList[0]);
						Integer secondRange = Integer.parseInt(spaceSplitRangeList[1]);
						Range<Integer> r = new Range<Integer>(firstRange, true, secondRange, false);
						rangeList.add(r);
					}
				}
			}
		}
		return rangeList;
	}
	
	/*
	 * metodo che restituisce i valori di output del dem (es: 1;2;3;---n;)
	 */
	public static int[] getOutputRange() {
		String outputRangeConfig = ProjectProperties.loadByName(Constants.OUPUT_DEM_RANGE);
		
		if (outputRangeConfig != null && !outputRangeConfig.equals("")) {
			//splitto per ;
			String[] commaSplitRangeList = outputRangeConfig.split(";");
			int[] outputRangeList = new int[commaSplitRangeList.length];
			
			for (int i = 0; i < commaSplitRangeList.length; i++) {
				String range = commaSplitRangeList[i];
				if (range != null && !range.equals("")) {
					int ouputRange = Integer.parseInt(range);
					outputRangeList[i] = ouputRange;
				}
			}
			return outputRangeList;
		}
		return null;
	}
	
}
