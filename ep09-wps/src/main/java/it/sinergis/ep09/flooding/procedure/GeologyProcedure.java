package it.sinergis.ep09.flooding.procedure;

import it.sinergis.ep09.utils.Constants;
import it.sinergis.ep09.utils.GdalOperation;
import it.sinergis.ep09.utils.ProcessUtils;
import it.sinergis.ep09.utils.ProjectProperties;

import org.apache.log4j.Logger;
import org.geoserver.wps.WPSException;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.process.vector.QueryProcess;
import org.geotools.process.vector.VectorToRasterProcess;
import org.opengis.filter.Filter;
import org.opengis.geometry.BoundingBox;
import org.opengis.util.ProgressListener;

public class GeologyProcedure {
	
	private static final Logger LOGGER = Logger.getLogger(GeologyProcedure.class);
	
	public SimpleFeatureCollection geologyLayer;
	public GridCoverage2D dem;
	public String demFilePath;
	public String valueFieldForRasterize;
	public BoundingBox boundingBox;
	public Filter filter;
	public String alluviumClass;
	
	public ProgressListener progressListener;
	
	public GeologyProcedure(SimpleFeatureCollection geologyLayer, String valueFieldForRasterize, GridCoverage2D dem,
			String demFilePath, Filter filter, BoundingBox boundingBox, ProgressListener progressListener) {
		this.geologyLayer = geologyLayer;
		this.dem = dem;
		this.demFilePath = demFilePath;
		this.valueFieldForRasterize = valueFieldForRasterize;
		this.filter = filter;
		this.boundingBox = boundingBox;
	}
	
	public String executeProcedure() {
		
		String selectHighProneAreaFile = null;
		String mergeClassHighFile = null;
		String timesFile = null;
		alluviumClass = null;
		
		try {
			//check fields layer
			ProcessUtils.checkFieldLayer(geologyLayer, valueFieldForRasterize);
			
			//1) selection deposits
			if (filter != null) {
				LOGGER.debug("inizio selection deposits");
				QueryProcess query = new QueryProcess();
				SimpleFeatureCollection layer = query.execute(geologyLayer, null, filter);
				geologyLayer = new DefaultFeatureCollection(layer);
				LOGGER.debug("fine selection deposits");
			}
			
			if (geologyLayer != null && geologyLayer.size() > 0) {
				//2) vector_to_raster
				alluviumClass = vectorToRaster();
				
				//3) select high prone area
				selectHighProneAreaFile = selectHighProneArea(alluviumClass);
				
				//4) merge class high
				mergeClassHighFile = mergeClassHigh(selectHighProneAreaFile);
				
				//5) times
				timesFile = times(mergeClassHighFile);
				
			}
			else {
				LOGGER.error("nessuna features geology: " + geologyLayer.getSchema().getTypeName());
				throw new WPSException("geology layer is empty");
			}
		}
		catch (Exception e) {
			if (alluviumClass != null) {
				ProcessUtils.deleteTmpFile(alluviumClass);
			}
			throw new WPSException(e.getMessage());
		}
		finally {
			if (selectHighProneAreaFile != null) {
				ProcessUtils.deleteTmpFile(selectHighProneAreaFile);
			}
			if (mergeClassHighFile != null) {
				ProcessUtils.deleteTmpFile(mergeClassHighFile);
			}
		}
		
		return timesFile;
		
	}
	
	/*
	 * gs:VectorToRasterProcess
	 */
	private String vectorToRaster() {
		LOGGER.debug("inizio vector to raster");
		VectorToRasterProcess vectorToRaster = new VectorToRasterProcess();
		GridCoverage2D rasterGeology = vectorToRaster.execute(geologyLayer, dem.getRenderedImage().getWidth(), dem
				.getRenderedImage().getHeight(), geologyLayer.getSchema().getTypeName(), valueFieldForRasterize,
				boundingBox, progressListener);
		alluviumClass = ProjectProperties.loadByName(Constants.TMP_PATH) + geologyLayer.getSchema().getTypeName() + "_"
				+ Constants.ALLUVIUM_CLASS + "_" + System.currentTimeMillis() + Constants.TIF_EXTENSION;
		ProcessUtils.writeRasterSuFile(alluviumClass, rasterGeology);
		LOGGER.debug("fine vector to raster " + alluviumClass);
		return alluviumClass;
	}
	
	/*
	 * select high prone area python "C:\Program Files\QGIS Dufour\bin\gdal_calc.py" -A
	 * C:\temp\dem3_slopeSelection_1406722080476.tif -B C:\temp\geology_new_alluviumClass_1406722096796.tif
	 * --outfile=C:\temp\geology_new_HighProneArea_1406722096822.tif --calc="(A==1)*B " --NoDataValue -1
	 */
	private String selectHighProneArea(String alluviumClass2) {
		LOGGER.debug("inizio select high prone area");
		String strInputLayer = " -A " + demFilePath + " -B " + alluviumClass;
		String selectHighProneAreaFile = ProjectProperties.loadByName(Constants.TMP_PATH)
				+ geologyLayer.getSchema().getTypeName() + "_HighProneArea_" + System.currentTimeMillis()
				+ Constants.TIF_EXTENSION;
		GdalOperation.gdalCalc(strInputLayer, selectHighProneAreaFile, "(A==1)*B");
		LOGGER.debug("fine select high prone area, files generato: " + selectHighProneAreaFile);
		return selectHighProneAreaFile;
	}
	
	/*
	 * merge class high python "C:\Program Files\QGIS Dufour\bin\gdal_calc.py" -A
	 * C:\temp\geology_new_HighProneArea_1406722096822.tif
	 * --outfile=C:\temp\geology_new_MergeClassHigh_1406722104159.tif --calc="(A>=1)*1 " --NoDataValue -1
	 */
	private String mergeClassHigh(String selectHighProneAreaFile) {
		LOGGER.debug("inizio merge class high");
		String strInputLayer = " -A " + selectHighProneAreaFile;
		String mergeClassHighFile = ProjectProperties.loadByName(Constants.TMP_PATH)
				+ geologyLayer.getSchema().getTypeName() + "_MergeClassHigh_" + System.currentTimeMillis()
				+ Constants.TIF_EXTENSION;
		GdalOperation.gdalCalc(strInputLayer, mergeClassHighFile, "(A>=1)*1");
		LOGGER.debug("fine merge class high, files generato: " + mergeClassHighFile);
		return mergeClassHighFile;
	}
	
	/*
	 * times python "C:\Program Files\QGIS Dufour\bin\gdal_calc.py" -A
	 * C:\temp\geology_new_MergeClassHigh_1406722104159.tif --outfile=C:\temp\geology_new_Times_1406722111485.tif
	 * --calc="A*10 " --NoDataValue -1
	 */
	private String times(String mergeClassHighFile) {
		LOGGER.debug("inizio times");
		String strInputLayer = " -A " + mergeClassHighFile;
		String timesFile = ProjectProperties.loadByName(Constants.TMP_PATH) + geologyLayer.getSchema().getTypeName()
				+ "_Times_" + System.currentTimeMillis() + Constants.TIF_EXTENSION;
		GdalOperation.gdalCalc(strInputLayer, timesFile, "A*10");
		LOGGER.debug("fine times, files generato: " + timesFile);
		return timesFile;
	}
	
}
