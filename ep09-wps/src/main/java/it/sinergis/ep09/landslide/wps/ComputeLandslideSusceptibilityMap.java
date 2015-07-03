package it.sinergis.ep09.landslide.wps;

import it.sinergis.ep09.landslide.procedure.DemProcedure;
import it.sinergis.ep09.landslide.procedure.GeologyProcedure;
import it.sinergis.ep09.landslide.procedure.LandCoverProcedure;
import it.sinergis.ep09.landslide.procedure.Procedure;
import it.sinergis.ep09.utils.Constants;
import it.sinergis.ep09.utils.GeoserverUtils;
import it.sinergis.ep09.utils.ProcessUtils;
import it.sinergis.ep09.utils.ProjectProperties;
import it.sinergis.utils.WnsNotification;

import java.io.File;
import java.util.List;

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
import org.opengis.util.ProgressListener;

@DescribeProcess(title = "computeLandslideSusceptibilityMap", description = "compute landslide susceptibility map")
public class ComputeLandslideSusceptibilityMap extends SpringBeanProcessFactory {
	
	private static final Logger LOGGER = Logger.getLogger(ComputeLandslideSusceptibilityMap.class);
	
	private Catalog catalog;
	private Procedure procedure;
	
	public ComputeLandslideSusceptibilityMap(Catalog catalog) {
		super(Constants.EENV_TITLE, Constants.EENV_NAMESPACE, ComputeLandslideSusceptibilityMap.class);
		this.catalog = catalog;
	}
	
	/*
	 * wps per la landslide susceptibility map (vedi wps_compute_landslide_susceptibility.jpg per i passi che vengono
	 * eseguiti)
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
			@DescribeParameter(name = "reclassifyGeology", description = "id and susceptibility parameters for reclassify geology layer separate by |", min = 0, collectionType = String.class)
			List<String> reclassifyGeology,
			@DescribeParameter(name = "landcover", description = "landcover ")
			SimpleFeatureCollection landCoverLayer,
			@DescribeParameter(name = "valueFieldForRasterizeLandcover", description = "field for rasterize landcover layer")
			String valueFieldForRasterizeLandcover,
			@DescribeParameter(name = "reclassifyLandcover", description = "id and susceptibility parameters for reclassify landcover layer separate by |", min = 0, collectionType = String.class)
			List<String> reclassifyLandcover,
			@DescribeParameter(name = "boundingBox", description = "area to analyse")
			ReferencedEnvelope boundingBox,
			@DescribeParameter(name = "landslideStyle", description = "style for landslide susceptibility map", min = 0)
			String landslideStyle,
			@DescribeParameter(name = "emailAddress", description = "email address for notification", min = 0)
			String emailAddress, ProgressListener progressListener) throws Exception {
		
		checkInputParameter(dem, demFilePath, geologyLayer, valueFieldForRasterizeGeology, reclassifyGeology,
				landCoverLayer, valueFieldForRasterizeLandcover, reclassifyLandcover, boundingBox);
		
		WnsNotification notification = null;
		String idUtente = null;
		if (emailAddress != null && !emailAddress.equals("")) {
			notification = new WnsNotification();
			
			//registrazione utente sul wns
			idUtente = notification.registerSingleUser(emailAddress, emailAddress,
					ProjectProperties.loadByName(Constants.WNS_URL));
		}
		
		progressListener.started();
		
		String landslideRaster = null;
		String outputGeology = null;
		String outputLandcover = null;
		try {
			//DEM
			LOGGER.debug("inizio procedura sul dem");
			DemProcedure demProcedure = new DemProcedure(dem);
			if (demFilePath != null) {
				if (new File(demFilePath).exists()) {
					dem = ProcessUtils.getGridCoverageFromFile(new File(demFilePath),
							boundingBox.getCoordinateReferenceSystem());
				}
				else {
					throw new WPSException("dem file " + demFilePath + " does not exist");
				}
				
			}
			else {
				demFilePath = demProcedure.executeProcedure();
				LOGGER.debug("fine procedura dem " + demFilePath);
			}
			
			progressListener.progress(25);
			
			//GEOLOGY
			LOGGER.debug("inizio procedura su geology");
			GeologyProcedure geologyProcedure = new GeologyProcedure(geologyLayer, valueFieldForRasterizeGeology,
					reclassifyGeology, dem, boundingBox.toBounds(boundingBox.getCoordinateReferenceSystem()),
					progressListener);
			outputGeology = geologyProcedure.executeProcedure();
			LOGGER.debug("fine procedura su geology " + outputGeology);
			
			progressListener.progress(50);
			
			//LANDCOVER
			LOGGER.debug("inizio procedura su landcover");
			LandCoverProcedure landCoverProcedure = new LandCoverProcedure(landCoverLayer,
					valueFieldForRasterizeLandcover, reclassifyLandcover, dem, boundingBox.toBounds(boundingBox
							.getCoordinateReferenceSystem()), progressListener);
			outputLandcover = landCoverProcedure.executeProcedure();
			LOGGER.debug("fine procedura su landcover " + outputLandcover);
			
			progressListener.progress(75);
			
			procedure = new Procedure();
			LOGGER.debug("inizio raster calculator");
			landslideRaster = procedure.rasterCalculator(demFilePath, outputGeology, outputLandcover);
			LOGGER.debug("fine raster calculator " + landslideRaster);
			
			progressListener.progress(90);
			
		}
		catch (Exception e) {
			LOGGER.error("errore nel calcolo della procedura", e);
			throw new WPSException("error while calculating procedure ", e);
		}
		finally {
			if (outputGeology != null) {
				ProcessUtils.deleteTmpFile(outputGeology);
			}
			if (outputLandcover != null) {
				ProcessUtils.deleteTmpFile(outputLandcover);
			}
		}
		
		if (landslideRaster != null) {
			landslideRaster = GeoserverUtils.publisherLayerOnGeoserver(landslideRaster, boundingBox, dem
					.getRenderedImage().getWidth(), dem.getRenderedImage().getHeight(), landslideStyle, catalog,
					Constants.LANDSLIDE_RASTER, ProjectProperties.loadByName(Constants.GEOSERVER_WS_TEMP));
			
			progressListener.complete();
			
			if (notification != null && idUtente != null) {
				//inviare notifica con path del file generato
				notification.sendNotification(idUtente, landslideRaster,
						ProjectProperties.loadByNameFromMessages("short_msg"),
						ProjectProperties.loadByName(Constants.WNS_URL));
				notification.unRegister(idUtente, ProjectProperties.loadByName(Constants.WNS_URL));
			}
			
		}
		
		return landslideRaster;
		
	}
	
	private void checkInputParameter(GridCoverage2D dem, String demFilePath, SimpleFeatureCollection geologyLayer,
			String valueFieldForRasterizeGeology, List<String> geologyReclassify,
			SimpleFeatureCollection landCoverLayer, String valueFieldForRasterizeLandcover,
			List<String> landcoverReclassify, ReferencedEnvelope boundingBox) {
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
		
		if (geologyReclassify == null) {
			throw new WPSException(ProjectProperties.loadByNameFromMessages("missing_reclassify_parameter")
					+ " geology layer");
		}
		LOGGER.debug("geology reclassify: " + geologyReclassify.toString());
		
		//LANDCOVER
		if (landCoverLayer == null) {
			throw new WPSException(ProjectProperties.loadByNameFromMessages("missing_landcoverLayer"));
		}
		LOGGER.debug("landcover layer: " + landCoverLayer.getSchema().getTypeName());
		
		if (valueFieldForRasterizeLandcover == null || valueFieldForRasterizeLandcover.equals("")) {
			throw new WPSException(ProjectProperties.loadByNameFromMessages("missing_rasterize_parameter")
					+ " lancover layer");
		}
		LOGGER.debug("parameter for rasterize landcover: " + valueFieldForRasterizeLandcover);
		
		if (landcoverReclassify == null) {
			throw new WPSException(ProjectProperties.loadByNameFromMessages("missing_reclassify_parameter")
					+ " lancover layer");
		}
		LOGGER.debug("geology reclassify: " + landcoverReclassify.toString());
		
		if (boundingBox == null || boundingBox.getCoordinateReferenceSystem() == null) {
			throw new WPSException(ProjectProperties.loadByNameFromMessages("missing_bbox"));
		}
		LOGGER.debug("boundingBox " + boundingBox.toString());
	}
	
}
