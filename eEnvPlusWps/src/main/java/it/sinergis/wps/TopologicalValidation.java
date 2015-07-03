package it.sinergis.wps;

import it.sinergis.utils.Constants;
import it.sinergis.utils.DbUtils;
import it.sinergis.utils.EnvPlusUtils;
import it.sinergis.utils.Layer;
import it.sinergis.utils.ReadFromConfig;
import it.sinergis.utils.ShapeUtils;
import it.sinergis.utils.TopologicalValidationUtils;

import java.io.Serializable;
import java.util.Map;

import org.apache.log4j.Logger;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.catalog.StoreInfo;
import org.geoserver.wps.WPSException;
import org.geoserver.wps.gs.GeoServerProcess;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;
import org.opengis.util.ProgressListener;

@DescribeProcess(title = "topologicalValidation", description = "Validazione topologica")
public class TopologicalValidation implements GeoServerProcess {
	
	private static final Logger LOGGER = Logger.getLogger(TopologicalValidation.class);
	
	private Catalog catalog;
	
	private String resultTableName = null;
	private long systemCurrentMillis;
	private String lowerLayerName = null;
	private String upperLayerName = null;
	
	private String workspace = null;
	private String store = null;
	private String schema = null;
	
	public TopologicalValidation(Catalog catalog) {
		this.catalog = catalog;
		this.workspace = ReadFromConfig.loadByName(Constants.GEOSERVER_WORKSPACE);
		this.store = ReadFromConfig.loadByName(Constants.GEOSERVER_DATASTORE);
	}
	
	@DescribeResult(name = "result", description = "output result")
	public SimpleFeatureCollection execute(
			@DescribeParameter(name = "featuresLowerLevel", description = "polygon features type of lower level")
			SimpleFeatureCollection lowerFeatures,
			@DescribeParameter(name = "featuresUpperLevel", description = "polygon features type of upper level")
			SimpleFeatureCollection upperFeatures,
			@DescribeParameter(name = "validationType", description = "validation type: 1 for upper-lower validation, 2 for holes validation, 3 for lower validation", min = 0)
			String validationType, ProgressListener progressListener) throws Exception {
		
		//metodo che controlla l'input
		if (!checkInput(lowerFeatures, upperFeatures, validationType)) {
			throw new WPSException("errore nel recupero dell'input");
		}
		
		progressListener.started();
		systemCurrentMillis = System.currentTimeMillis();
		
		DataStoreInfo storeInfo = catalog.getDataStoreByName(workspace, store);
		Map<String, Serializable> params = storeInfo.getConnectionParameters();
		schema = (String) params.get("schema");
		
		resultTableName = "result" + systemCurrentMillis;
		
		Layer resultLayer = new Layer(schema, resultTableName);
		
		if (!DbUtils.createResultTable(storeInfo, resultLayer)) {
			throw new Exception("errore nella connessione al database: ");
		}
		
		//import in postgis le lower e le upper (se non ho richiesto solo la validazione di primo livello)
		lowerLayerName = EnvPlusUtils.importInPostgisFeatures(lowerFeatures, catalog, workspace, store,
				systemCurrentMillis);
		if (validationType == null || validationType.indexOf(Constants.HOLES_VALIDATION) != -1
				|| validationType.indexOf(Constants.UPPER_LOWER_VALIDATION) != -1) {
			upperLayerName = EnvPlusUtils.importInPostgisFeatures(upperFeatures, catalog, workspace, store,
					systemCurrentMillis);
		}
		
		Layer lowerLayer = new Layer(schema, lowerLayerName);
		Layer upperLayer = new Layer(schema, upperLayerName);
		
		if (validationType == null) {
			TopologicalValidationUtils.callTopologicalFunction(lowerFeatures, upperFeatures, null, lowerLayer, upperLayer, resultLayer);
		}
		else {
			String[] validationTypeArray = validationType.split(",");
			for (String type : validationTypeArray) {
				if (type != null && !type.equals("")) {
					TopologicalValidationUtils.callTopologicalFunction(lowerFeatures, upperFeatures, type, lowerLayer, upperLayer, resultLayer);
				}
			}
		}
		
		//recupero i dati restituiti
		try {
			return getResult(storeInfo, progressListener, resultTableName);
		}
		catch (Exception e) {
			LOGGER.error("errore nel recupero del risultato", e);
		}
		finally {
			DbUtils.deleteTable(schema + ".\"" + lowerLayerName + "\"");
			if (upperLayerName != null) {
				DbUtils.deleteTable(schema + ".\"" + upperLayerName + "\"");
			}
			
			//DbUtils.deleteTable(schema + ".\"" + resultTableName + "\"");
			//DbUtils.closeConnection();
		}
		return null;
	}
	
//	private void callTopologicalFunction(SimpleFeatureCollection lowerFeatures, SimpleFeatureCollection upperFeatures,
//			String validationType) {
//		
//		//1) low level polygons overlapping themselves
//		if (validationType == null || validationType.equals(Constants.LOWER_VALIDATION)) {
//			try {
//				returnOverlapsGeometryForLowLevelPolygons(lowerFeatures);
//			}
//			catch (Exception e) {
//				LOGGER.error("errore nel controllo dei poligoni a basso livello ", e);
//				e.printStackTrace();
//			}
//		}
//		
//		//2) low level polygons contenuti in high level polygons (comuni non sbordano fuori dalla provincia)
//		if (validationType == null || validationType.equals(Constants.UPPER_LOWER_VALIDATION)) {
//			try {
//				returnIfLowerGeometryIsContainsInUpperPolygons(lowerFeatures, upperFeatures);
//			}
//			catch (Exception e) {
//				LOGGER.error("errore nel controllo dei poligoni a basso livello con quelli di alto livello ", e);
//				e.printStackTrace();
//			}
//		}
//		
//		//holes left by low level polygons (surface belonging to high level polygon not belonging to low level ones)
//		if (validationType == null || validationType.equals(Constants.HOLES_VALIDATION)) {
//			try {
//				returnIfLowerGeometryHasHoles(lowerFeatures, upperFeatures);
//			}
//			catch (Exception e) {
//				LOGGER.error("errore nel controllo della presenza di buchi ", e);
//				e.printStackTrace();
//			}
//		}
//		
//	}
//	
	/*
	 * metodo che controlla i dati di input
	 */
	private boolean checkInput(SimpleFeatureCollection lowerFeatures, SimpleFeatureCollection upperFeatures,
			String validationType) {
		TopologicalValidationUtils.checkInputFeatureCollection(lowerFeatures, "featuresLowerLevel");
		TopologicalValidationUtils.checkInputFeatureCollection(upperFeatures, "featuresUpperLevel");
		
		if (validationType == null) {
			return true;
		}
		else {
			String[] validationTypeArray = validationType.split(",");
			for (String type : validationTypeArray) {
				if (!type.equals(Constants.HOLES_VALIDATION) && !type.equals(Constants.LOWER_VALIDATION)
						&& !type.equals(Constants.UPPER_LOWER_VALIDATION)) {
					throw new WPSException("validationtype: " + type + " is not valid ");
				}
			}
		}
		return true;
	}
	
	/*
	 * metodo che controlla che le feature collection in input non siano nulle e siano dei poligoni
	 */
//	private void checkInputFeatureCollection(SimpleFeatureCollection features, String featureType) {
//		if (features == null) {
//			throw new WPSException(featureType + " null");
//		}
//		if (!MultiPolygon.class.isAssignableFrom(features.getSchema().getGeometryDescriptor().getType().getBinding())
//				&& !Polygon.class.isAssignableFrom(features.getSchema().getGeometryDescriptor().getType().getBinding())) {
//			throw new WPSException(featureType + " is not multiPolygon or Polygon");
//		}
//	}
	
//	/*
//	 * es: controllo se ci sono delle provincie che si sovrappongono
//	 */
//	private void returnOverlapsGeometryForLowLevelPolygons(SimpleFeatureCollection lowerFeatures) throws Exception {
//		//eseguo l'operazione topologica
//		String geom = lowerFeatures.getSchema().getGeometryDescriptor().getLocalName();
//		String query = "insert into " + schema + ".\"" + resultTableName
//				+ "\" (geometry_type, result_geom,operation_type) select ST_dimension(foo.geom), foo.geom, "
//				+ Constants.LOWER_VALIDATION + " from (select distinct st_intersection(a1." + geom + ", a2." + geom
//				+ ") as geom from " + schema + ".\"" + lowerLayerName + "\" a1 inner join " + schema + ".\""
//				+ lowerLayerName + "\" a2 on st_overlaps(a1." + geom + ", a2." + geom + ")) as foo";
//		LOGGER.debug("query " + query);
//		DbUtils.executeQuery(query);
//	}
	
	/*
	 * es: controllo se ci sono dei comuni che escono dalla provincia
	 */
//	private void returnIfLowerGeometryIsContainsInUpperPolygons(SimpleFeatureCollection lowerFeatures,
//			SimpleFeatureCollection upperFeatures) throws Exception {
//		//recupero come si chiama il campo geometria
//		String geomLower = lowerFeatures.getSchema().getGeometryDescriptor().getLocalName();
//		String geomUpper = upperFeatures.getSchema().getGeometryDescriptor().getLocalName();
//		
//		String buffer = ReadFromConfig.loadByName(Constants.TOLERANCE);
//		//recupero il/i campo/i che fa/fanno da primary key
//		String fid = DbUtils.getFieldPrimaryKey(upperLayerName, schema);
//		
//		//eseguo l'operazione topologica
//		String operationQuery = "select ST_dimension(foo.geom), foo.geom, " + Constants.UPPER_LOWER_VALIDATION
//				+ " from ((select ST_Difference(a2." + geomUpper + ", a1." + geomLower + ") as geom " + " from "
//				+ schema + ".\"" + lowerLayerName + "\" a1 inner join " + schema + ".\"" + upperLayerName
//				+ "\" a2 on st_overlaps(a1." + geomLower + ", a2." + geomUpper + ")" + " where a2.\"" + fid + "\" in "
//				+ " (select \"" + fid + "\" from " + schema + ".\"" + upperLayerName + "\" a2 where not exists"
//				+ " (select 1 from " + schema + ".\"" + lowerLayerName + "\" a1 where ST_Contains(ST_buffer(a1."
//				+ geomLower + ", " + buffer + "), a2." + geomUpper + ") )))" + " union " + " (select " + geomUpper
//				+ " from " + schema + ".\"" + upperLayerName + "\" where ST_disjoint(" + geomUpper
//				+ ", (SELECT ST_Union(" + geomLower + ") As geom FROM " + schema + ".\"" + lowerLayerName + "\")))"
//				+ "union " + "(select " + geomUpper + " from " + schema + ".\"" + upperLayerName
//				+ "\" where ST_touches(" + geomUpper + ", (SELECT ST_Union(" + geomLower + ") As geom FROM " + schema
//				+ ".\"" + lowerLayerName + "\")))) as foo";
//		LOGGER.debug("query " + operationQuery);
//		
//		String insertIntoQuery = "insert into " + schema + ".\"" + resultTableName
//				+ "\" (geometry_type,result_geom,operation_type) " + operationQuery;
//		LOGGER.debug("query " + insertIntoQuery);
//		
//		DbUtils.executeQuery(insertIntoQuery);
//	}
	
	/*
	 * es: controllo se la provincia è coperta completamente dai comuni
	 */
//	private void returnIfLowerGeometryHasHoles(SimpleFeatureCollection lowerFeatures,
//			SimpleFeatureCollection upperFeatures) {
//		//recupero come si chiama il campo geometria
//		String geomLower = lowerFeatures.getSchema().getGeometryDescriptor().getLocalName();
//		String geomUpper = upperFeatures.getSchema().getGeometryDescriptor().getLocalName();
//		//eseguo l'operazione topologica
//		String operationQuery = "select ST_dimension(foo.geom), foo.geom, " + Constants.HOLES_VALIDATION
//				+ " from (select ST_difference(ST_union(a2." + geomLower + "), ST_union(a1." + geomUpper
//				+ ")) as geom from " + schema + ".\"" + upperLayerName + "\" a1, " + schema + ".\"" + lowerLayerName
//				+ "\" a2 where ST_intersects(a1." + geomUpper + ", a2." + geomLower + ")) as foo";
//		
//		LOGGER.debug("query " + operationQuery);
//		
//		String insertIntoQuery = "insert into " + schema + ".\"" + resultTableName
//				+ "\" (geometry_type,result_geom,operation_type) " + operationQuery;
//		LOGGER.debug("query " + insertIntoQuery);
//		
//		DbUtils.executeQuery(insertIntoQuery);
//		
//	}
	
	/*
	 * recupero il risultato dalla feature-collection
	 */
	public SimpleFeatureCollection getResult(StoreInfo storeInfo, ProgressListener progressListener,
			String resultTableName) throws Exception {
		DataStore dataStore = DataStoreFinder.getDataStore(storeInfo.getConnectionParameters());
		SimpleFeatureStore resultFeatureSourceDataStore = (SimpleFeatureStore) dataStore
				.getFeatureSource(resultTableName);
		
		SimpleFeatureCollection resultFeatures = resultFeatureSourceDataStore.getFeatures();
		
		//per la scrittura dello shapefile
		Boolean writeShp = Boolean.parseBoolean(ReadFromConfig.loadByName(Constants.WRITE_SHP));
		if (writeShp != null && writeShp) {
			LOGGER.debug("inizio scrittura shapefile ");
			ShapeUtils.writeFeaturesInShapefile(ReadFromConfig.loadByName(Constants.SHP_PATH_TOPOLOGICAL_VALIDATION),
					resultFeatures, resultFeatureSourceDataStore);
		}
		
		if (progressListener != null) {
			progressListener.progress(100);
			progressListener.complete();
		}
		if (resultFeatures.size() > 0) {
			return resultFeatures;
		}
		else {
			throw new Exception("nessun errore trovato ");
		}
		
	}
}
