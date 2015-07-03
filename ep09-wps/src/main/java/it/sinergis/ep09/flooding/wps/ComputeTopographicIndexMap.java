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

@DescribeProcess(title = "computeTopographicIndexMap", description = "compute topographic index map")
public class ComputeTopographicIndexMap extends SpringBeanProcessFactory {
	
	private static final Logger LOGGER = Logger.getLogger(ComputeTopographicIndexMap.class);
	
	private Catalog catalog;
	
	public ComputeTopographicIndexMap(Catalog catalog) {
		super(Constants.EENV_TITLE, Constants.EENV_NAMESPACE, ComputeTopographicIndexMap.class);
		this.catalog = catalog;
	}
	
	/*
	 * wps per il calcolo della tpopgraphic index map, passi eseguiti: 1) tpi di gdal 2) il raster assumerà valore 1 se
	 * ha valore >= threshold altrimenti 0
	 */
	@DescribeResult(name = "result", description = "output result")
	public String execute(@DescribeParameter(name = "floodBaseRaster", description = "flood base raster", min = 0)
	GridCoverage2D floodBase, @DescribeParameter(name = "threshold", description = "threshold")
	String threshold, @DescribeParameter(name = "floodingStyle", description = "style for flooding map", min = 0)
	String floodingStyle,
			@DescribeParameter(name = "emailAddress", description = "email address for notification", min = 0)
			String emailAddress, ProgressListener progressListener) throws Exception {
		
		checkInputParameter(floodBase, threshold);
		
		WnsNotification notification = null;
		String idUtente = null;
		if (emailAddress != null && !emailAddress.equals("")) {
			notification = new WnsNotification();
			
			//registrazione utente sul wns
			idUtente = notification.registerSingleUser(emailAddress, emailAddress,
					ProjectProperties.loadByName(Constants.WNS_URL));
		}
		
		progressListener.started();
		
		String floodingBaseFile = null;
		String tpiFile = null;
		String thresholdFile = null;
		
		try {
			
			//cerco o se non lo trovo riscrivo il raster su fs
			floodingBaseFile = GeoserverUtils.getObjPath(catalog, floodBase);
			
			//TPI
			LOGGER.debug("inizio calcolo TPI");
			tpiFile = ProjectProperties.loadByName(Constants.TMP_PATH) + floodBase.getName() + "_TPI_"
					+ System.currentTimeMillis() + Constants.TIF_EXTENSION;
			GdalOperation.tpi(floodingBaseFile, tpiFile);
			LOGGER.debug("fine calcolo TPI");
			
			progressListener.progress(50);
			
			LOGGER.debug("inizio reclassify with threshold " + threshold);
			String strInputLayer = " -A " + tpiFile;
			thresholdFile = ProjectProperties.loadByName(Constants.TMP_PATH) + floodBase.getName() + "_threshold_"
					+ System.currentTimeMillis() + Constants.TIF_EXTENSION;
			GdalOperation.gdalCalc(strInputLayer, thresholdFile, "(A>=" + threshold + ")*1+(A<" + threshold + ")*0");
			LOGGER.debug("fine reclassify with threshold " + threshold);
			
			progressListener.progress(80);
			
		}
		catch (Exception e) {
			LOGGER.error("errore nel calcolo della procedura", e);
			throw new WPSException("error while calculating procedure ", e);
		}
		finally {
			if (tpiFile != null) {
				ProcessUtils.deleteTmpFile(tpiFile);
			}
		}
		
		String getMapRequest = null;
		try {
			if (thresholdFile != null) {
				ReferencedEnvelope refEnvelope = new ReferencedEnvelope(floodBase.getEnvelope());
				getMapRequest = GeoserverUtils.publisherLayerOnGeoserver(thresholdFile, refEnvelope, floodBase
						.getRenderedImage().getWidth(), floodBase.getRenderedImage().getHeight(), floodingStyle,
						catalog, ProjectProperties.loadByNameFromMessages(Constants.FLOODING_TPI_MAP),
						ProjectProperties.loadByName("ws_flooding"));
				
				progressListener.complete();
			}
			if (notification != null && idUtente != null) {
				//inviare notifica con path del file generato
				notification.sendNotification(idUtente, getMapRequest,
						ProjectProperties.loadByNameFromMessages("short_msg"),
						ProjectProperties.loadByName(Constants.WNS_URL));
				notification.unRegister(idUtente, ProjectProperties.loadByName(Constants.WNS_URL));
			}
		}
		catch (Exception e) {
			LOGGER.error("errore nel caricamento su geoserver di " + thresholdFile, e);
			throw new WPSException("error while uploading raster file on Geoserver", e);
		}
		
		return getMapRequest;
	}
	
	private void checkInputParameter(GridCoverage2D floodRaster, String threshold) {
		if (floodRaster == null && floodRaster == null) {
			throw new WPSException(ProjectProperties.loadByNameFromMessages("missing_flooding_raster"));
		}
		
		if (threshold == null || threshold.equals("")) {
			throw new WPSException(ProjectProperties.loadByNameFromMessages("missing_threshold_parameter"));
		}
		LOGGER.debug("parameter for threshold: " + threshold);
	}
	
}
