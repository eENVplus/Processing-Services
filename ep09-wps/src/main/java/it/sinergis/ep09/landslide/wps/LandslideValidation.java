package it.sinergis.ep09.landslide.wps;

import it.sinergis.ep09.utils.Constants;
import it.sinergis.ep09.utils.GeoserverUtils;
import it.sinergis.ep09.utils.ProcessUtils;
import it.sinergis.ep09.utils.ProjectProperties;

import org.apache.log4j.Logger;
import org.geoserver.catalog.Catalog;
import org.geoserver.wps.WPSException;
import org.geoserver.wps.jts.SpringBeanProcessFactory;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;

@DescribeProcess(title = "landslideValidation", description = "landslide validation")
public class LandslideValidation extends SpringBeanProcessFactory {
	
	private static final Logger LOGGER = Logger.getLogger(LandslideValidation.class);
	
	private Catalog catalog;
	
	public LandslideValidation(Catalog catalog) {
		super(Constants.EENV_TITLE, Constants.EENV_NAMESPACE, LandslideValidation.class);

		this.catalog = catalog;
	}
	
	/*
	 * wps per la memorizzazione della mappa di suscettibilità, move dal ws tmp in un altro definito in configurazione
	 */
	@DescribeResult(name = "result", description = "output result")
	public String execute(
			@DescribeParameter(name = "susceptibilityMap", description = "susceptibility Map for validation")
			GridCoverage2D susceptibilityMap,
			@DescribeParameter(name = "validationSet", description = "Zone polygon features for which to compute statistics")
			SimpleFeatureCollection validationSet) {
		
		//		RasterZonalStatistics zonalStatistics = new RasterZonalStatistics();
		//		SimpleFeatureCollection sf = zonalStatistics.execute(susceptibilityMap, null, validationSet, null);
		
		//cerco o se non lo trovo riscrivo il raster su fs
		String response = null;
		try {
			LOGGER.debug("inizio recupero raster");
			String raster = GeoserverUtils.getObjPath(catalog, susceptibilityMap);
			LOGGER.debug("fine recupero raster");
			
			LOGGER.debug("inizio recupero vector layer");
			String sfs = GeoserverUtils.getObjPath(catalog, validationSet);
			LOGGER.debug("fine recupero vector layer");
			
			String scriptPath = ProjectProperties.loadByName("zonal_stat_script_path");
			
			String command = Constants.PYTHON + " \"" + scriptPath + "\" \"" + sfs + "\" \"" + raster + "\"";
			if ("false".equals(ProjectProperties.loadByName(Constants.IS_WIN))) {
				//se siamo sotto linux tolgo le virgolette
				command = Constants.PYTHON + " " + scriptPath + " " + sfs + " " + raster;
			}
			
			LOGGER.debug("comando eseguito " + command);
			response = ProcessUtils.exec(command, true);
			
		}
		catch (Exception e) {
			LOGGER.error("errore nella validazione del landslide ",e);
			e.printStackTrace();
			throw new WPSException("error landslide validation");
		}
		
		return response;
	}
	
}
