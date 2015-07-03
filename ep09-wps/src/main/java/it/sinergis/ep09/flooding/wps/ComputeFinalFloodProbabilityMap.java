package it.sinergis.ep09.flooding.wps;

import it.sinergis.ep09.utils.Constants;
import it.sinergis.ep09.utils.GdalOperation;
import it.sinergis.ep09.utils.GeoserverUtils;
import it.sinergis.ep09.utils.ProcessUtils;
import it.sinergis.ep09.utils.ProjectProperties;
import it.sinergis.utils.WnsNotification;

import org.apache.log4j.Logger;
import org.geoserver.catalog.Catalog;
import org.geoserver.wps.WPSException;
import org.geoserver.wps.jts.SpringBeanProcessFactory;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;
import org.opengis.util.ProgressListener;

@DescribeProcess(title = "computeFinalFloodProbabilityMap", description = "compute final flood probability map")
public class ComputeFinalFloodProbabilityMap extends SpringBeanProcessFactory {
	
	private static final Logger LOGGER = Logger.getLogger(ComputeFinalFloodProbabilityMap.class);
	
	private Catalog catalog;
	
	public ComputeFinalFloodProbabilityMap(Catalog catalog) {
		super(Constants.EENV_TITLE, Constants.EENV_NAMESPACE, ComputeFinalFloodProbabilityMap.class);
		this.catalog = catalog;
	}
	
	/*
	 * wps per il calcolo della mappa finale: 1) A+B tra i raster generato nel primo wps (ComputeFloodProneBaseMap) e il
	 * raster generato nel secondo (ComputeTopographicIndexMap)
	 */
	@DescribeResult(name = "result", description = "output result")
	public String execute(@DescribeParameter(name = "floodProneBaseMap", description = "flood base map raster")
	GridCoverage2D floodBaseMap,
			@DescribeParameter(name = "floodTopographicIndexMap", description = "flood topographic index map raster")
			GridCoverage2D floodTopographicIndexMap,
			@DescribeParameter(name = "floodingStyle", description = "style for flooding map", min = 0)
			String floodingStyle,
			@DescribeParameter(name = "emailAddress", description = "email address for notification", min = 0)
			String emailAddress, ProgressListener progressListener) throws Exception {
		
		checkInputParameter(floodBaseMap, floodTopographicIndexMap);
		
		WnsNotification notification = null;
		String idUtente = null;
		if (emailAddress != null && !emailAddress.equals("")) {
			notification = new WnsNotification();
			
			//registrazione utente sul wns
			idUtente = notification.registerSingleUser(emailAddress, emailAddress,
					ProjectProperties.loadByName(Constants.WNS_URL));
		}
		
		progressListener.started();
		
		String floodBaseMapFile = null;
		String floodTpiMapFileFinal = null;
		String floodTpiMapFile = null;
		String floodingFinalMapFile = null;
		
		try {
			
			//cerco o se non lo trovo riscrivo il raster su fs
			floodBaseMapFile = GeoserverUtils.getObjPath(catalog, floodBaseMap);
			floodTpiMapFile = GeoserverUtils.getObjPath(catalog, floodTopographicIndexMap);
			
			floodingFinalMapFile = sumTpiBaseMap(floodBaseMapFile, floodTpiMapFile);
			
			progressListener.progress(50);
			
			floodTpiMapFileFinal = normalizeRaster(floodingFinalMapFile);
			
			progressListener.progress(80);
			
		}
		catch (Exception e) {
			LOGGER.error("errore nel calcolo della procedura", e);
			throw new WPSException("error while calculating procedure ", e);
		}
		
		String getMapRequest = null;
		try {
			if (floodingFinalMapFile != null) {
				ReferencedEnvelope refEnvelope = new ReferencedEnvelope(floodBaseMap.getEnvelope());
				getMapRequest = GeoserverUtils.publisherLayerOnGeoserver(floodingFinalMapFile, refEnvelope,
						floodBaseMap.getRenderedImage().getWidth(), floodBaseMap.getRenderedImage().getHeight(),
						floodingStyle, catalog,
						ProjectProperties.loadByNameFromMessages(Constants.FLOODING_FINAL_PROBABILITY_MAP),
						ProjectProperties.loadByName("ws_flooding"));
				
				progressListener.complete();
				
				if (notification != null && idUtente != null) {
					//inviare notifica con path del file generato
					notification.sendNotification(idUtente, getMapRequest,
							ProjectProperties.loadByNameFromMessages("short_msg"),
							ProjectProperties.loadByName(Constants.WNS_URL));
					notification.unRegister(idUtente, ProjectProperties.loadByName(Constants.WNS_URL));
				}
			}
		}
		catch (Exception e) {
			LOGGER.error("errore nel caricamento su geoserver di " + floodingFinalMapFile, e);
			throw new WPSException("error while uploading raster file on Geoserver", e);
		}
		finally {
			if (floodingFinalMapFile != null && notification == null) {
				ProcessUtils.deleteTmpFile(floodingFinalMapFile);
			}
		}
		return getMapRequest;
	}
	
	private String normalizeRaster(String floodingFinalMapFile) {
		LOGGER.debug("inizio calcolo mappa finale");
		String floodTpiMapFileFinal = ProjectProperties.loadByName(Constants.TMP_PATH) + Constants.FLOODING_RASTER
				+ "_floodTpiMapFileFinal_" + System.currentTimeMillis() + Constants.TIF_EXTENSION;
		String strInputLayer = " -A " + floodingFinalMapFile;
		if (ProjectProperties.loadByName(Constants.IS_WIN) != null
				&& ProjectProperties.loadByName(Constants.IS_WIN).equals("true")) {
			strInputLayer = " -A \"" + floodingFinalMapFile + "\"";
		}
		GdalOperation.gdalCalc(strInputLayer, floodTpiMapFileFinal, "(A==-1)*0");
		LOGGER.debug("fine calcolo mappa finale " + floodingFinalMapFile);
		return floodTpiMapFileFinal;
	}
	
	private String sumTpiBaseMap(String floodBaseMapFile, String floodTpiMapFile) {
		//somma tra la mappa tpi e la base
		LOGGER.debug("inizio somma tpi map " + floodTpiMapFile + " e " + floodBaseMapFile);
		String floodingFinalMapFile = ProjectProperties.loadByName(Constants.TMP_PATH) + Constants.FLOODING_RASTER
				+ "_probabilityMap_" + System.currentTimeMillis() + Constants.TIF_EXTENSION;
		String strInputLayer = " -A " + floodBaseMapFile + " -B " + floodTpiMapFile;
		if (ProjectProperties.loadByName(Constants.IS_WIN) != null
				&& ProjectProperties.loadByName(Constants.IS_WIN).equals("true")) {
			strInputLayer = " -A \"" + floodBaseMapFile + "\" -B \"" + floodTpiMapFile + "\"";
		}
		GdalOperation.gdalCalc(strInputLayer, floodingFinalMapFile, "(A+B)");
		LOGGER.debug("fine somma " + floodingFinalMapFile);
		return floodingFinalMapFile;
	}
	
	private void checkInputParameter(GridCoverage2D floodRaster1, GridCoverage2D floodRaster2) {
		if (floodRaster1 == null && floodRaster2 == null) {
			throw new WPSException(ProjectProperties.loadByNameFromMessages("missing_flooding_raster"));
		}
	}
	
}
