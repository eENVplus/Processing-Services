package it.sinergis.ep09.flooding.wps;

import it.sinergis.ep09.flooding.procedure.DemProcedure;
import it.sinergis.ep09.flooding.procedure.GeologyProcedure;
import it.sinergis.ep09.utils.Constants;
import it.sinergis.ep09.utils.GdalOperation;
import it.sinergis.ep09.utils.GeoserverUtils;
import it.sinergis.ep09.utils.ProcessUtils;
import it.sinergis.ep09.utils.ProjectProperties;
import it.sinergis.utils.WnsNotification;

import java.io.File;

import org.apache.log4j.Logger;
import org.geoserver.catalog.Catalog;
import org.geoserver.wps.WPSException;
import org.geoserver.wps.jts.SpringBeanProcessFactory;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;
import org.opengis.filter.Filter;
import org.opengis.util.ProgressListener;

@DescribeProcess(title = "computeFloodProneBaseMap", description = "compute flood prone base map")
public class ComputeFloodProneBaseMap extends SpringBeanProcessFactory {
	
	private static final Logger LOGGER = Logger.getLogger(ComputeFloodProneBaseMap.class);
	
	private Catalog catalog;
	
	public ComputeFloodProneBaseMap(Catalog catalog) {
		super(Constants.EENV_TITLE, Constants.EENV_NAMESPACE, ComputeFloodProneBaseMap.class);
		this.catalog = catalog;
	}
	
	/*
	 * wps per la ComputeFloodProneBase map (vedi wps_compute_flood_prone_base.png per i passi che vengono eseguiti)
	 */
	@DescribeResult(name = "result", description = "output result")
	public String execute(
			@DescribeParameter(name = "dem", description = "dem raster", min = 0)
			GridCoverage2D dem,
			@DescribeParameter(name = "demFilePath", description = "path for the dem raster, if the raster is already calculated", min = 0)
			String demFilePath,
			@DescribeParameter(name = "geology", description = "geology ")
			SimpleFeatureCollection geologyLayer,
			@DescribeParameter(name = "valueFieldForRasterizeGeology", description = "field for rasterize geology layer, example: ID__GEOLOG")
			String valueFieldForRasterizeGeology,
			@DescribeParameter(name = "geologyfilter", min = 0, description = "The filter to apply at geologyLayer")
			Filter geologyFilter, @DescribeParameter(name = "boundingBox", description = "area to analyse", min = 0)
			ReferencedEnvelope boundingBox,
			@DescribeParameter(name = "floodingStyle", description = "style for flooding prone base map", min = 0)
			String floodingStyle,
			@DescribeParameter(name = "emailAddress", description = "email address for notification", min = 0)
			String emailAddress, ProgressListener progressListener) throws Exception {
		
		checkInputParameter(dem, demFilePath, geologyLayer, valueFieldForRasterizeGeology, boundingBox);
		
		WnsNotification notification = null;
		String idUtente = null;
		if (emailAddress != null && !emailAddress.equals("")) {
			notification = new WnsNotification();
			
			//registrazione utente sul wns
			idUtente = notification.registerSingleUser(emailAddress, emailAddress,ProjectProperties.loadByName(Constants.WNS_URL));
		}
		
		progressListener.started();
		
		String geologyOutputFile = null;
		String outputLandcover = null;
		String floodPotential = null;
		String lowProneAreaFile = null;
		GeologyProcedure geologyProcedure = null;
		
		try {
			//DEM SLOPE
			LOGGER.debug("inizio procedura sul dem");
			String projwin = boundingBox.getMinX() + " " + boundingBox.getMaxY() + " " + boundingBox.getMaxX() + " "
					+ boundingBox.getMinY();
			DemProcedure demProcedure = new DemProcedure(dem, projwin);
			if (demFilePath != null) {
				if (new File(demFilePath).exists()) {
					dem = ProcessUtils.getGridCoverageFromFile(new File(demFilePath),
							boundingBox.getCoordinateReferenceSystem());
				}
				else {
					throw new WPSException("dem file " + demFilePath + " does not exist");
				}
				LOGGER.debug("fine prima procedura dem " + demFilePath);
			}
			else {
				demFilePath = demProcedure.executeFirstProcedure();
				LOGGER.debug("fine prima procedura dem " + demFilePath);
			}
			progressListener.progress(30);
			
			//GEOLOGY
			LOGGER.debug("inizio prima procedura sul geology");
			geologyProcedure = new GeologyProcedure(geologyLayer, valueFieldForRasterizeGeology, dem, demFilePath,
					geologyFilter, boundingBox, progressListener);
			geologyOutputFile = geologyProcedure.executeProcedure();
			LOGGER.debug("fine prima procedura sul geology");
			
			progressListener.progress(60);
			
			//DEM CALCOLO LOW PRONE AREA
			if (geologyProcedure.alluviumClass != null) {
				//seconda procedura dem 
				LOGGER.debug("inizio procedura per il calcolo della low prone area");
				lowProneAreaFile = demProcedure.executeSecondProcedure(demFilePath, geologyProcedure.alluviumClass);
				LOGGER.debug("fine procedura per il calcolo della low prone area");
			}
			else {
				LOGGER.error("alluviumClassFile is null");
				throw new WPSException("error while calculating procedure, alluviumClassFile is null");
			}
			progressListener.progress(80);
			
			//COMBINE POTENTIAL CLASS
			floodPotential = combinePotentialClass(lowProneAreaFile, geologyOutputFile);
		}
		catch (Exception e) {
			LOGGER.error("errore nel calcolo della procedura", e);
			throw new WPSException("error while calculating procedure ", e);
		}
		finally {
			if (geologyOutputFile != null) {
				ProcessUtils.deleteTmpFile(geologyOutputFile);
			}
			if (outputLandcover != null) {
				ProcessUtils.deleteTmpFile(outputLandcover);
			}
			if (geologyProcedure != null && geologyProcedure.alluviumClass != null) {
				ProcessUtils.deleteTmpFile(geologyProcedure.alluviumClass);
			}
			if (lowProneAreaFile != null) {
				ProcessUtils.deleteTmpFile(lowProneAreaFile);
			}
			if (demFilePath != null) {
				ProcessUtils.deleteTmpFile(demFilePath);
			}
		}
		
		if (floodPotential != null) {
			floodPotential = GeoserverUtils.publisherLayerOnGeoserver(floodPotential, boundingBox, dem
					.getRenderedImage().getWidth(), dem.getRenderedImage().getHeight(), floodingStyle, catalog,
					ProjectProperties.loadByNameFromMessages(Constants.FLOODING_BASE_MAP), ProjectProperties
							.loadByName("ws_flooding"));
			
			progressListener.complete();
			
			if (notification != null && idUtente != null) {
				//inviare notifica con path del file generato
				notification.sendNotification(idUtente, floodPotential,
						ProjectProperties.loadByNameFromMessages("short_msg"),ProjectProperties.loadByName(Constants.WNS_URL));
				notification.unRegister(idUtente,ProjectProperties.loadByName(Constants.WNS_URL));
			}
		}
		
		return floodPotential;
		
	}
	
	/*
	 * calcola la flood prone base map
	 * python "C:\Program Files\QGIS Dufour\bin\gdal_calc.py" -A C:\temp\dem3_MergeClassLow_1406722127236.tif -B C:\temp\geology_new_Times_1406722111485.tif --outfile=C:\temp\flooding_1406722134392.tif --calc="A+B " --NoDataValue -1
	 */
	private String combinePotentialClass(String lowProneAreaFile, String geologyOutputFile) {
		LOGGER.debug("inizio combine potential class, file in input " + lowProneAreaFile + " , " + geologyOutputFile);
		String strInputLayer = " -A " + lowProneAreaFile + " -B " + geologyOutputFile;
		String combinePotentialClass = ProjectProperties.loadByName(Constants.TMP_PATH) + Constants.FLOODING_RASTER
				+ "_" + System.currentTimeMillis() + Constants.TIF_EXTENSION;
		GdalOperation.gdalCalc(strInputLayer, combinePotentialClass, "A+B");
		LOGGER.debug("fine combine potential class, file generato " + combinePotentialClass);
		return combinePotentialClass;
	}
	
	/*
	 * controlla i parametri in input
	 */
	private void checkInputParameter(GridCoverage2D dem, String demFilePath, SimpleFeatureCollection geologyLayer,
			String valueFieldForRasterizeGeology, ReferencedEnvelope boundingBox) {
		if (dem == null && demFilePath == null) {
			throw new WPSException(ProjectProperties.loadByNameFromMessages("missing_dem"));
		}
		if (dem != null) {
			LOGGER.debug("dem in input: " + dem.getName());
		}
		LOGGER.debug("path del raster dem se è gia' stato calcolato: " + demFilePath);
		
		//GEOLOGY
		if (geologyLayer == null) {
			throw new WPSException(ProjectProperties.loadByNameFromMessages("missing_geologyLayer"));
		}
		LOGGER.debug("geology layer; " + geologyLayer.getSchema().getTypeName());
		
		if (valueFieldForRasterizeGeology == null || valueFieldForRasterizeGeology.equals("")) {
			throw new WPSException(ProjectProperties.loadByNameFromMessages("missing_rasterize_parameter")
					+ " geology layer");
		}
		LOGGER.debug("parameter for rasterize geology: " + valueFieldForRasterizeGeology);
		
		if (boundingBox == null || boundingBox.getCoordinateReferenceSystem() == null) {
			throw new WPSException(ProjectProperties.loadByNameFromMessages("missing_bbox"));
		}
		LOGGER.debug("boundingBox " + boundingBox.toString());
	}
	
}
