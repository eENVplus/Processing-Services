package it.sinergis.ep09.landslide.wps;

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import it.geosolutions.geoserver.rest.encoder.GSResourceEncoder.ProjectionPolicy;
import it.sinergis.ep09.utils.Constants;
import it.sinergis.ep09.utils.ProcessUtils;
import it.sinergis.ep09.utils.ProjectProperties;

import java.io.File;
import java.net.MalformedURLException;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.log4j.Logger;
import org.geoserver.catalog.Catalog;
import org.geoserver.wps.WPSException;
import org.geoserver.wps.gs.GeoServerProcess;
import org.geoserver.wps.jts.SpringBeanProcessFactory;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;

@DescribeProcess(title = "storeSusceptibilityMap", description = "store susceptibilityMap")
public class StoreSusceptibilityMap extends SpringBeanProcessFactory {
	
	private static final Logger LOGGER = Logger.getLogger(StoreSusceptibilityMap.class);
	
	private Catalog catalog;
	
	public StoreSusceptibilityMap(Catalog catalog) {
		super(Constants.EENV_TITLE, Constants.EENV_NAMESPACE, StoreSusceptibilityMap.class);
		
		this.catalog = catalog;
	}
	
	/*
	 * wps per la memorizzazione della mappa di suscettibilità, move dal ws tmp in un altro definito in configurazione
	 */
	@DescribeResult(name = "result", description = "output result")
	public String execute(
			@DescribeParameter(name = "susceptibilityMap", description = "susceptibility Map for storing", min = 0)
			GridCoverage2D susceptibilityMap) throws MalformedURLException {
		String outputRasterFile = null;
		try {
			
			if (susceptibilityMap.getName() == null) {
				throw new WPSException("layer specificato inesistente " + susceptibilityMap.getName());
			}
			
			GeoServerRESTPublisher publisher = ProcessUtils.getRestPublisher();
			GeoServerRESTReader reader = ProcessUtils.getRestReader();
			
			//creazione se non c'e' del nuovo ws
			String storeWs = ProjectProperties.loadByName(Constants.GEOSERVER_WS_STORE_MAP);
			boolean wsCreated = true;
			if (catalog.getWorkspaceByName(storeWs) == null) {
				wsCreated = publisher.createWorkspace(storeWs);
			}
			
			if (wsCreated) {
				String fileName = susceptibilityMap.getName() + "__" + System.currentTimeMillis();
				outputRasterFile = ProjectProperties.loadByName(Constants.TMP_PATH) + fileName
						+ Constants.TIF_EXTENSION;
				//scrittura su fs
				boolean writeResult = ProcessUtils.writeRasterSuFile(outputRasterFile, susceptibilityMap);
				
				if (writeResult) {
					String srs = ProcessUtils.getEpsgFromCoordinateReferenceSystem(susceptibilityMap
							.getCoordinateReferenceSystem());
					
					//recupero dello stile del layer di partenza
					String style = "raster";
					if (reader.getLayer(susceptibilityMap.getName().toString()) != null) {
						style = reader.getLayer(susceptibilityMap.getName().toString()).getDefaultStyle();
						LOGGER.debug("stile del layer "+style);
					} 
					
					//cancellazione dal ws temporaneo su geoserver
					boolean resultRemove = publisher.removeCoverageStore(ProjectProperties
							.loadByName(Constants.GEOSERVER_WS_TEMP), susceptibilityMap.getName().toString(), true);
					if (resultRemove) {
						
						//pubblicazione su geoserver
						boolean publishResult = publisher.publishGeoTIFF(storeWs, susceptibilityMap.getName()
								.toString(), susceptibilityMap.getName().toString(), new File(outputRasterFile), srs,
								ProjectionPolicy.FORCE_DECLARED, style, null);
						if (!publishResult) {
							throw new WPSException("errore during publishing raster " + susceptibilityMap.getName()
									+ " in ws " + ProjectProperties.loadByName(Constants.GEOSERVER_WS_TEMP));
						}
						
					}
					else {
						throw new WPSException("errore during removing raster " + susceptibilityMap.getName()
								+ " in ws " + ProjectProperties.loadByName(Constants.GEOSERVER_WS_STORE_MAP));
					}
				}
				else {
					throw new WPSException("errore during writing raster " + susceptibilityMap.getName());
				}
			}
			else {
				throw new WPSException("errore during creating ws " + storeWs);
			}
			
			return HttpStatus.SC_OK + "";
		}
		catch (Exception e) {
			LOGGER.error("errore nella memorizzazione del layer " + susceptibilityMap.getName(), e);
			throw new WPSException("errore during storing raster " + susceptibilityMap.getName(), e);
		}
		finally {
			if (outputRasterFile != null) {
				ProcessUtils.deleteTmpFile(outputRasterFile);
			}
		}
	}
}
