package it.sinergis.utils;

import org.apache.log4j.Logger;
import org.geoserver.wps.WPSException;
import org.geotools.data.simple.SimpleFeatureCollection;

import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

public class TopologicalValidationUtils {
	
	private static final Logger LOGGER = Logger.getLogger(TopologicalValidationUtils.class);


	/*
	 * metodo che controlla che le feature collection in input non siano nulle e siano dei poligoni
	 */
	public static void checkInputFeatureCollection(SimpleFeatureCollection features, String featureType) {
		if (features == null) {
			throw new WPSException(featureType + " null");
		}
		if (!MultiPolygon.class.isAssignableFrom(features.getSchema().getGeometryDescriptor().getType().getBinding())
				&& !Polygon.class.isAssignableFrom(features.getSchema().getGeometryDescriptor().getType().getBinding())) {
			throw new WPSException(featureType + " is not multiPolygon or Polygon");
		}
	}
	
	public static void callTopologicalFunction(SimpleFeatureCollection lowerFeatures, SimpleFeatureCollection upperFeatures,
			String validationType, Layer lowerLayer, Layer upperLayer, Layer resultLayer) throws WPSException{
		
		//1) low level polygons overlapping themselves
		if (validationType == null || validationType.equals(Constants.LOWER_VALIDATION)) {
			try {
				returnOverlapsGeometryForLowLevelPolygons(lowerFeatures,lowerLayer,resultLayer);
			}
			catch (Exception e) {
				String msg = "errore nel controllo dei poligoni a basso livello ";
				LOGGER.error(msg, e);
				e.printStackTrace();
				throw new WPSException(msg+e.getMessage());
			}
		}
		
		//2) low level polygons contenuti in high level polygons (comuni non sbordano fuori dalla provincia)
		if (validationType == null || validationType.equals(Constants.UPPER_LOWER_VALIDATION)) {
			try {
				returnIfLowerGeometryIsContainsInUpperPolygons(lowerFeatures, upperFeatures, lowerLayer, upperLayer, resultLayer);
			}
			catch (Exception e) {
				String msg = "errore nel controllo dei poligoni a basso livello con quelli di alto livello ";
				LOGGER.error(msg, e);
				e.printStackTrace();
				throw new WPSException(msg+e.getMessage());
			}
		}
		
		//holes left by low level polygons (surface belonging to high level polygon not belonging to low level ones)
		if (validationType == null || validationType.equals(Constants.HOLES_VALIDATION)) {
			try {
				returnIfLowerGeometryHasHoles(lowerFeatures, upperFeatures, lowerLayer, upperLayer, resultLayer);
			}
			catch (Exception e) {
				String msg = "errore nel controllo della presenza di buchi ";
				LOGGER.error(msg, e);
				e.printStackTrace();
				throw new WPSException(msg+e.getMessage());
			}
		}
		
	}
	
	/*
	 * es: controllo se ci sono delle provincie che si sovrappongono
	 */
	private static void returnOverlapsGeometryForLowLevelPolygons(SimpleFeatureCollection lowerFeatures,Layer lowerLayer, Layer resultLayer) throws Exception {
		//eseguo l'operazione topologica
		String geom = lowerFeatures.getSchema().getGeometryDescriptor().getLocalName();
		String query = "insert into " + resultLayer.getSchema() + ".\"" + resultLayer.getName()
				+ "\" (geometry_type, result_geom,operation_type) select ST_dimension(foo.geom), foo.geom, "
				+ Constants.LOWER_VALIDATION + " from (select distinct st_intersection(a1." + geom + ", a2." + geom
				+ ") as geom from " + lowerLayer.getSchema() + ".\"" + lowerLayer.getName() + "\" a1 inner join " + lowerLayer.getSchema() + ".\""
				+ lowerLayer.getName() + "\" a2 on st_overlaps(a1." + geom + ", a2." + geom + ")) as foo";
		LOGGER.debug("query " + query);
		DbUtils.executeQuery(query);
	}
	
	
	/*
	 * es: controllo se ci sono dei comuni che escono dalla provincia
	 */
	private static void returnIfLowerGeometryIsContainsInUpperPolygons(SimpleFeatureCollection lowerFeatures,
			SimpleFeatureCollection upperFeatures, Layer lowerLayer, Layer upperLayer, Layer resultLayer) throws Exception {
			//recupero come si chiama il campo geometria
			String geomLower = lowerFeatures.getSchema().getGeometryDescriptor().getLocalName();
			String geomUpper = upperFeatures.getSchema().getGeometryDescriptor().getLocalName();
			//
			String buffer = ReadFromConfig.loadByName(Constants.TOLERANCE);
			//recupero il/i campo/i che fa/fanno da primary key
			String fid = DbUtils.getFieldPrimaryKey(upperLayer.getName(), upperLayer.getSchema());
			
			//eseguo l'operazione topologica
			String operationQuery = "select ST_dimension(foo.geom), foo.geom, " + Constants.UPPER_LOWER_VALIDATION
					+ " from ((select ST_Difference(a2." + geomUpper + ", a1." + geomLower + ") as geom " + " from "
					+ lowerLayer.getSchema() + ".\"" + lowerLayer.getName() + "\" a1 inner join " + upperLayer.getSchema() + ".\"" + upperLayer.getName()
					+ "\" a2 on st_overlaps(a1." + geomLower + ", a2." + geomUpper + ")" + " where a2.\"" + fid + "\" in "
					+ " (select \"" + fid + "\" from " + upperLayer.getSchema() + ".\"" + upperLayer.getName() + "\" a2 where not exists"
					+ " (select 1 from " + lowerLayer.getSchema() + ".\"" + lowerLayer.getName() + "\" a1 where ST_Contains(ST_buffer(a1."
					+ geomLower + ", " + buffer + "), a2." + geomUpper + ") )))" + " union " + " (select " + geomUpper
					+ " from " + upperLayer.getSchema() + ".\"" + upperLayer.getName() + "\" where ST_disjoint(" + geomUpper
					+ ", (SELECT ST_Union(" + geomLower + ") As geom FROM " + lowerLayer.getSchema() + ".\"" + lowerLayer.getName() + "\")))"
					+ "union " + "(select " + geomUpper + " from " + upperLayer.getSchema() + ".\"" + upperLayer.getName()
					+ "\" where ST_touches(" + geomUpper + ", (SELECT ST_Union(" + geomLower + ") As geom FROM " + lowerLayer.getSchema()
					+ ".\"" + lowerLayer.getName() + "\")))) as foo";
			
			LOGGER.debug("query " + operationQuery);
			
			String insertIntoQuery = "insert into " + resultLayer.getSchema() + ".\"" + resultLayer.getName()
					+ "\" (geometry_type,result_geom,operation_type) " + operationQuery;
			LOGGER.debug("query " + insertIntoQuery);
			
			DbUtils.executeQuery(insertIntoQuery);
		
	}
	

	/*
	 * es: controllo se la provincia e coperta completamente dai comuni
	 */
	private static void returnIfLowerGeometryHasHoles(SimpleFeatureCollection lowerFeatures,
		SimpleFeatureCollection upperFeatures, Layer lowerLayer, Layer upperLayer, Layer resultLayer) throws Exception{
		//recupero come si chiama il campo geometria
		String geomLower = lowerFeatures.getSchema().getGeometryDescriptor().getLocalName();
		String geomUpper = upperFeatures.getSchema().getGeometryDescriptor().getLocalName();
		//eseguo l'operazione topologica
		String operationQuery = "select ST_dimension(foo.geom), foo.geom, " + Constants.HOLES_VALIDATION
				+ " from (select ST_difference(ST_union(a2." + geomLower + "), ST_union(a1." + geomUpper
				+ ")) as geom from " + upperLayer.getSchema() + ".\"" + upperLayer.getName() + "\" a1, " + lowerLayer.getSchema() + ".\"" + lowerLayer.getName()
				+ "\" a2 where ST_intersects(a1." + geomUpper + ", a2." + geomLower + ")) as foo";
		
		LOGGER.debug("query " + operationQuery);
		
		String insertIntoQuery = "insert into " + resultLayer.getSchema() + ".\"" + resultLayer.getName()
				+ "\" (geometry_type,result_geom,operation_type) " + operationQuery;
		LOGGER.debug("query " + insertIntoQuery);
		
		DbUtils.executeQuery(insertIntoQuery);			
	}
}
