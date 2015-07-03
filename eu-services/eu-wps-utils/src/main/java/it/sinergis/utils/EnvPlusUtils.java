package it.sinergis.utils;

import org.apache.log4j.Logger;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.ProjectionPolicy;
import org.geoserver.wps.gs.ImportProcess;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class EnvPlusUtils {
	private static final Logger LOGGER = Logger.getLogger(EnvPlusUtils.class);
	
	public static String importInPostgisFeatures(SimpleFeatureCollection features, Catalog catalog, String workspace,
			String store, String systemCurrentMillis, String epsg) throws Exception {
		LOGGER.debug("inizio import in postgis di "+features.getSchema().getTypeName());
		ImportProcess importProcess = new ImportProcess(catalog);
		CoordinateReferenceSystem coordinateReferenceSystem = features.getBounds().getCoordinateReferenceSystem();
		if (coordinateReferenceSystem == null) {
			try {
				coordinateReferenceSystem = CRS.decode("EPSG:"
						+ epsg);
			}
			catch (NoSuchAuthorityCodeException e) {
				LOGGER.error(
						"errore nel set del sistema di riferimento EPSG:"
								+ epsg, e);
				e.printStackTrace();
			}
			catch (FactoryException e) {
				LOGGER.error(
						"errore nel set del sistema di riferimento EPSG:"
								+ epsg, e);
				e.printStackTrace();
			}
		}
		String layerName = importProcess.execute(features, null, workspace, store,
				((SimpleFeatureType) features.getSchema()).getTypeName() + systemCurrentMillis,
				coordinateReferenceSystem, ProjectionPolicy.FORCE_DECLARED, null);
		
		LayerInfo layerInfo = catalog.getLayerByName(layerName);
		catalog.remove(layerInfo);
		
		if (layerName.indexOf(":") != -1) {
			layerName = layerName.substring(layerName.indexOf(":") + 1);
		}
		LOGGER.debug("fine import in postgis, il nome della tabella importata e' "+layerName);
		return layerName;
	}
	
	
	
	
}