package it.sinergis.wps;

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.encoder.GSLayerEncoder;
import it.geosolutions.geoserver.rest.encoder.GSPostGISDatastoreEncoder;
import it.geosolutions.geoserver.rest.encoder.feature.GSFeatureTypeEncoder;
import it.sinergis.ep10.utils.Constants;
import it.sinergis.ep10.utils.Layer;
import it.sinergis.utils.DbConnection;
import it.sinergis.utils.EnvPlusUtils;
import it.sinergis.utils.FeatureCollectionUtils;
import it.sinergis.utils.ProjectProperties;
import it.sinergis.utils.WnsNotification;
import it.sinergis.utils.WpsConstants;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Map;

import org.apache.log4j.Logger;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.catalog.StoreInfo;
import org.geoserver.wps.WPSException;
import org.geoserver.wps.jts.SpringBeanProcessFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.process.ProcessException;
import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;
import org.geotools.process.vector.ClipProcess;
import org.opengis.util.ProgressListener;

import com.vividsolutions.jts.geom.Geometry;

@DescribeProcess(title = "urbanRuralGrowthIdentification", description = "Service that compares soil areas in different years and identifies zones interested by urban and rural growth")
public class UrbanRuralGrowthIdentification extends SpringBeanProcessFactory {
	
	private static final Logger LOGGER = Logger.getLogger(UrbanRuralGrowthIdentification.class);
	
	private static String CONFIG_FILE = "config_ep10.properties";
	private static String MESSAGES_FILE = "messages_ep10.properties";
	private Catalog catalog;
	
	private long systemCurrentMillis;
	
	private String workspace = null;
	private String store = null;
	private String schema = null;
	private String aggregationSelected;
	
	private DbConnection dbConnection;
	private Connection connection;
	
	private ProjectProperties properties = null;
	
	public UrbanRuralGrowthIdentification(Catalog catalog) {
		super(WpsConstants.EENV_TITLE, WpsConstants.EENV_NAMESPACE, UrbanRuralGrowthIdentification.class);
		this.catalog = catalog;
		properties = new ProjectProperties(CONFIG_FILE, MESSAGES_FILE);
		this.workspace = properties.loadByNameFromProject(Constants.GEOSERVER_WORKSPACE);
		this.store = properties.loadByNameFromProject(Constants.GEOSERVER_DATASTORE);
	}
	
	@DescribeResult(name = "result", description = "output result")
	public String execute(
			@DescribeParameter(name = "firstYearFeatures", description = "features relative to the first year") SimpleFeatureCollection firstYearFeatures,
			@DescribeParameter(name = "secondYearFeatures", description = "features relative to the second year") SimpleFeatureCollection secondYearFeatures,
			@DescribeParameter(name = "restrictedArea", description = "area to analyse", min = 0) Geometry restrictedArea,
			@DescribeParameter(name = "useClip", description = "compares only the selected area", min = 0) String useClip,
			@DescribeParameter(name = "clcfield1", description = "field name containing classification code (first feature collection)") String clcfield1,
			@DescribeParameter(name = "clcfield2", description = "field name containing classification code (second feature collection)") String clcfield2,
			@DescribeParameter(name = "areafield1", description = "field name containing area (first feature collection)") String areafield1,
			@DescribeParameter(name = "areafield2", description = "field name containing area (second feature collection)") String areafield2,
			@DescribeParameter(name = "aggregationLevel", description = "number of level of aggregation", min = 0) String aggregationLevel,
			@DescribeParameter(name = "changeDetectionStyle", description = "style for change detection layer", min = 0) String changeDetectionStyle,
			@DescribeParameter(name = "emailAddresses", description = "email address/es for receiving notication at the end of wps process", min = 0) String emailAddresses,
			ProgressListener progressListener) throws Exception {
		
		dbConnection = new DbConnection();
		
		Layer firstLayer = null;
		Layer secondLayer = null;
		WnsNotification notification = null;
		String idUtente = null;
		String importFirstLayerName = null;
		String importSecondLayerName = null;
		
		try {
			
			if (emailAddresses != null && !emailAddresses.equals("")) {
				notification = new WnsNotification();
				
				//registrazione utente sul wns
				idUtente = notification.registerSingleUser(emailAddresses, emailAddresses,
						properties.loadByNameFromProject(Constants.WNS_URL));
			}
			
			progressListener.started();
			systemCurrentMillis = System.currentTimeMillis();
			
			//controllo parametri in input
			checkInput(aggregationLevel, firstYearFeatures, secondYearFeatures, clcfield1, clcfield2, areafield1,
					areafield2);
			
			if (useClip != null) {
				if (restrictedArea != null && useClip.equals("true")) {
					ClipProcess clipProcess = new ClipProcess();
					firstYearFeatures = clipProcess.execute(firstYearFeatures, restrictedArea);
					secondYearFeatures = clipProcess.execute(secondYearFeatures, restrictedArea);
				}
				else if (restrictedArea == null)
					return "if useClip is true please select a restrictedArea";
				else if (!useClip.equals("") && !useClip.equals("false"))
					return "useClip can be only true or false";
			}
			
			// pubblicazione su geoserver del risultato come layer
			String RESTURL = properties.loadByNameFromProject(Constants.RESTURL);
			String RESTUSER = properties.loadByNameFromProject(Constants.RESTUSER);
			String RESTPW = properties.loadByNameFromProject(Constants.RESTPW);
			GeoServerRESTPublisher publisher = new GeoServerRESTPublisher(RESTURL, RESTUSER, RESTPW);
			
			boolean wsCreated = createWorkSpaceGeoserver(publisher);
			
			if (wsCreated) {
				DataStoreInfo storeInfo = catalog.getDataStoreByName(workspace, store);
				connection = dbConnection.initConnection(storeInfo);
				Map<String, Serializable> params = storeInfo.getConnectionParameters();
				
				schema = (String) params.get("schema");
				
				String epsgLayer = FeatureCollectionUtils.getFirstEpsgCode(firstYearFeatures,
						properties.loadByNameFromProject(Constants.DEFAULT_FORCED_EPSG));
				
				//import in postgis 
				importFirstLayerName = EnvPlusUtils.importInPostgisFeatures(firstYearFeatures, catalog, workspace,
						store, systemCurrentMillis + "_1", epsgLayer);
				
				importSecondLayerName = EnvPlusUtils.importInPostgisFeatures(secondYearFeatures, catalog, workspace,
						store, systemCurrentMillis + "_2", epsgLayer);
				
				firstLayer = new Layer(schema, importFirstLayerName, firstYearFeatures.getSchema()
						.getGeometryDescriptor().getLocalName(), epsgLayer);
				secondLayer = new Layer(schema, importSecondLayerName, secondYearFeatures.getSchema()
						.getGeometryDescriptor().getLocalName(), epsgLayer);
				
				//controllo che il livello di aggregazione non sia maggiore della lunghezza del campo CLC
				Integer maxFieldLength1 = dbConnection.getMaxLengthFieldOnDb(clcfield1, importFirstLayerName, schema,
						connection);
				Integer maxFieldLength2 = dbConnection.getMaxLengthFieldOnDb(clcfield2, importSecondLayerName, schema,
						connection);
				if (maxFieldLength1 == null || maxFieldLength1 < Integer.valueOf(aggregationSelected)
						|| maxFieldLength2 == null || maxFieldLength2 < Integer.valueOf(aggregationSelected))
					throw new WPSException("Error on aggregation level on field " + clcfield1);
				
				//controllo overlaps e contains delle features in modo da estrarre solo quelle nella restrictedArea
				if (restrictedArea != null && ((useClip == null || !useClip.equals("true")))) {
					
					String restrictedLayerNameFirstLayer = getFeaturesInRestrictedArea(importFirstLayerName,
							restrictedArea, firstLayer);
					firstLayer.setName(restrictedLayerNameFirstLayer);
					
					String restrictedLayerNameSecondLayer = getFeaturesInRestrictedArea(importSecondLayerName,
							restrictedArea, secondLayer);
					secondLayer.setName(restrictedLayerNameSecondLayer);
				}
				
				String resultIntersectTableName = Constants.TMP_POSTGIS_LAYER_NAME + systemCurrentMillis;
				
				//intesect in postgis per layer change detection
				intersectForChangeDetection(clcfield1, clcfield2, areafield1, areafield2, firstLayer, secondLayer,
						new Layer(schema, resultIntersectTableName), aggregationSelected);
				
				//controllo che la tabella di intersezione non sia vuota
				checkIntersectionTable(schema, resultIntersectTableName);
				
				boolean successPublishLayer = publishLayer(publisher, storeInfo, resultIntersectTableName, epsgLayer,
						changeDetectionStyle);
				if (!successPublishLayer) {
					throw new WPSException("Error while publish layer on geoserver");
				}
				
				String xml = getXmlResult(clcfield1, clcfield2, areafield1, areafield2, resultIntersectTableName,
						firstYearFeatures.getSchema().getTypeName(), secondYearFeatures.getSchema().getTypeName(),
						firstYearFeatures.getBounds(), schema);
				
				if (notification != null && idUtente != null) {
					notification.sendNotification(idUtente, xml, properties.loadByNameFromMessages("short_msg"),
							properties.loadByNameFromProject(Constants.WNS_URL));
				}
				
				if (progressListener != null) {
					progressListener.progress(100);
					progressListener.complete();
				}
				
				return xml;
			}
			throw new WPSException("Error while created workspace on geoserver");
		}
		catch (ProcessException pe) {
			LOGGER.error("error while executing clip inside urbanRuralGrowthIdentification", pe);
			throw pe;
		}
		catch (Exception e) {
			LOGGER.error("error while executing urbanRuralGrowthIdentification", e);
			throw e;
		}
		finally {
			if (firstLayer != null) {
				dbConnection.deleteTable(firstLayer.getSchema(), firstLayer.getName(), connection);
			}
			if (secondLayer != null) {
				dbConnection.deleteTable(secondLayer.getSchema(), secondLayer.getName(), connection);
			}
			
			if (notification != null && idUtente != null) {
				notification.unRegister(idUtente, properties.loadByNameFromProject(Constants.WNS_URL));
			}
			if (restrictedArea != null && ((useClip == null || !useClip.equals("true")))) {
				//devo cancellare anche le tabelle di import
				if (firstLayer != null) {
					dbConnection.deleteTable(firstLayer.getSchema(), importFirstLayerName, connection);
				}
				
				if (secondLayer != null && ((useClip == null || !useClip.equals("true")))) {
					dbConnection.deleteTable(secondLayer.getSchema(), importSecondLayerName, connection);
				}
			}
			dbConnection.closeConnection(connection);
		}
	}
	
	/*
	 * metodo che crea una tabella nel db che contiene le features che intersecano con la geometria in input
	 */
	private String getFeaturesInRestrictedArea(String inputLayerName, Geometry restrictedArea, Layer featureLayer)
			throws Exception {
		String resultLayer = featureLayer.getName() + "_bbox" + systemCurrentMillis;
		
		String query = "create table " + featureLayer.getSchema() + ".\"" + resultLayer + "\" as (select * from "
				+ featureLayer.getSchema() + ".\"" + inputLayerName + "\" where ST_Overlaps(" + featureLayer.getGeom()
				+ ", st_geomfromtext('" + restrictedArea.toText() + "', '" + featureLayer.getEpsg() + "'))";
		query += " or ST_CONTAINS(st_geomfromtext('" + restrictedArea.toText() + "', '" + featureLayer.getEpsg()
				+ "'), " + featureLayer.getGeom() + "))";
		
		LOGGER.debug("query per estrarre le features nell'area selezionata " + query);
		
		dbConnection.executeQuery(query, connection);
		return resultLayer;
	}
	
	/*
	 * controlli sull'input
	 */
	private void checkInput(String aggregationLevel, SimpleFeatureCollection firstYearFeatures,
			SimpleFeatureCollection secondYearFeatures, String clcfield1, String clcfield2, String areafield1,
			String areafield2) throws Exception {
		if (aggregationLevel == null) {
			aggregationSelected = properties.loadByNameFromProject(Constants.DEFAULT_AGGREGATION_LEVEL);
		}
		else {
			aggregationSelected = aggregationLevel;
		}
		
		// controllo che le features in input abbiano lo stesso sistema di riferimento
		if (firstYearFeatures.getSchema() != null
				&& secondYearFeatures.getSchema() != null
				&& !firstYearFeatures
						.getSchema()
						.getCoordinateReferenceSystem()
						.getName()
						.getCode()
						.equalsIgnoreCase(
								secondYearFeatures.getSchema().getCoordinateReferenceSystem().getName().getCode())) {
			throw new WPSException("features containing data with coordinate reference system not homogenous");
		}
		
		//Verifico che il campo indicato come CLC sia presente negli shape di input
		boolean firstClcCorrect = FeatureCollectionUtils.checkAttribute(firstYearFeatures.getSchema(), clcfield1);
		boolean secondClcCorrect = FeatureCollectionUtils.checkAttribute(secondYearFeatures.getSchema(), clcfield2);
		
		if (!firstClcCorrect || !secondClcCorrect)
			throw new WPSException(properties.loadByNameFromMessages("clc_error"));
		
		boolean firstAreaCorrect = FeatureCollectionUtils.checkAttribute(secondYearFeatures.getSchema(), areafield1);
		boolean secondAreaCorrect = FeatureCollectionUtils.checkAttribute(secondYearFeatures.getSchema(), areafield2);
		
		if (!firstAreaCorrect || !secondAreaCorrect)
			throw new WPSException(properties.loadByNameFromMessages("area_error"));
	}
	
	/*
	 * metodo che crea una tabella che contiene le intersezioni tra i due layer
	 */
	private void intersectForChangeDetection(String clcfield1, String clcfield2, String areafield1, String areafield2,
			Layer firstLayer, Layer secondLayer, Layer resultLayer, String aggregationLevel) throws Exception {
		
		String geom1 = firstLayer.getGeom();
		String geom2 = secondLayer.getGeom();
		
		String operationQuery = "select GeometryType(c.intersect), c.intersect, c.area_a, c.area_b, c.attribute_aggregation_a, c.attribute_a, c.attribute_aggregation_b,  c.attribute_b from ("
				+ " select ST_CollectionExtract(st_intersection(a.\""
				+ geom1
				+ "\", b.\""
				+ geom2
				+ "\"),3) as intersect, round(a.\""
				+ areafield1
				+ "\"::numeric,0) as area_a, a.\""
				+ clcfield1
				+ "\" as attribute_a, substr(a.\""
				+ clcfield1
				+ "\"::text,1,"
				+ aggregationLevel
				+ ") as attribute_aggregation_a, round(b.\""
				+ areafield2
				+ "\"::numeric,0) as area_b, b.\""
				+ clcfield2
				+ "\" as attribute_b, substr(b.\""
				+ clcfield2
				+ "\"::text,1,"
				+ aggregationLevel
				+ ") as attribute_aggregation_b from "
				+ firstLayer.getSchema()
				+ ".\""
				+ firstLayer.getName()
				+ "\" a, "
				+ secondLayer.getSchema()
				+ ".\""
				+ secondLayer.getName()
				+ "\" b) as c where not ST_IsEmpty(c.intersect) and (GeometryType(c.intersect) = 'POLYGON' OR GeometryType(c.intersect) = 'MULTIPOLYGON')";
		
		LOGGER.debug("operationQuery " + operationQuery);
		
		String insertIntoQuery = "create table " + resultLayer.getSchema() + ".\"" + resultLayer.getName() + "\" as "
				+ operationQuery;
		
		//create table envplus.test as select GeometryType(c.intersect), c.intersect, c.area_a, c.area_b, c.attribute_aggregation_a, c.attribute_a, c.attribute_aggregation_b,  c.attribute_b from (select ST_CollectionExtract(st_intersection(a.the_geom, b.the_geom),3) as intersect, round(a."AREA"::numeric,0) as area_a, a."CODE_90" as attribute_a, substr(a."CODE_90",1,3) as attribute_aggregation_a, round(b."AREA"::numeric,0) as area_b, b."CODE_00" as attribute_b, substr(b."CODE_00",1,3) as attribute_aggregation_b from envplus."clc901410947024838_bbox1410947024838" a, envplus."clc20001410947024838_bbox1410947024838" b) as c 
		
		LOGGER.debug("query " + insertIntoQuery);
		
		dbConnection.executeQuery(insertIntoQuery, connection);
		
	}
	
	/*
	 * metodo che pubblica il layer su geoserver
	 */
	private boolean publishLayer(GeoServerRESTPublisher publisher, StoreInfo storeInfo, String resultTableName,
			String srs, String style) throws Exception {
		
		if (style == null) {
			style = properties.loadByNameFromProject(Constants.DEFAULT_STYLE);
		}
		
		String workspace = properties.loadByNameFromProject(Constants.GEOSERVER_WORKSPACE);
		String store = properties.loadByNameFromProject(Constants.GEOSERVER_DATASTORE);
		
		GSFeatureTypeEncoder featureTypeEncoder = new GSFeatureTypeEncoder();
		featureTypeEncoder.setSRS("EPSG:" + srs);
		featureTypeEncoder.setName(resultTableName);
		
		GSLayerEncoder layerEncoder = new GSLayerEncoder();
		layerEncoder.setEnabled(true);
		layerEncoder.setQueryable(true);
		layerEncoder.setDefaultStyle(style);
		
		return publisher.publishDBLayer(workspace, store, featureTypeEncoder, layerEncoder);
		
	}
	
	/*
	 * metodo che compone xml del risultato
	 */
	private String getXmlResult(String clcfield1, String clcfield2, String areafield1, String areafield2,
			String resultTableName, String firstDatasetName, String secondDatasetName, ReferencedEnvelope envelope,
			String schema) throws Exception {
		
		ResultSet rs = null;
		try {
			StringBuilder xml = new StringBuilder();
			
			xml.append("<UrbanRuralGrowthIdentification><result>");
			
			xml.append("<datasetA>" + firstDatasetName + "</datasetA>");
			xml.append("<attributeA>" + clcfield1 + "</attributeA>");
			xml.append("<areaA>" + areafield1 + "</areaA>");
			
			xml.append("<datasetB>" + secondDatasetName + "</datasetB>");
			xml.append("<attributeB>" + clcfield2 + "</attributeB>");
			xml.append("<areaB>" + areafield2 + "</areaB>");
			
			String query = "select t1.attr_a, coalesce(sum_a,0) as sum_a, coalesce(sum_b,0) as sum_b, coalesce(sum_a,0)-coalesce(sum_b,0) as diff from ";
			query += "(select attribute_aggregation_a as attr_a, sum(area_a) as sum_a from " + schema + "."
					+ resultTableName
					+ " group by attribute_aggregation_a order by attribute_aggregation_a) as t1 LEFT JOIN";
			query += "(select attribute_aggregation_b as attr_b, sum(area_b) as sum_b from "
					+ schema
					+ "."
					+ resultTableName
					+ " group by attribute_aggregation_b order by attribute_aggregation_b) as t2 on t1.attr_a = t2.attr_b";
			rs = dbConnection.executeQueryWithResultSet(query, connection);
			
			LOGGER.debug("groupBy " + query);
			
			while (rs.next()) {
				String attr_A = rs.getString("attr_a");
				String area_A = rs.getString("sum_a");
				String area_B = rs.getString("sum_b");
				String diff = rs.getString("diff");
				
				xml.append("\n" + "<growth><attributeA>");
				xml.append(attr_A).append("</attributeA>");
				xml.append("<area_A>").append(area_A).append("</area_A>");
				xml.append("<area_B>").append(area_B).append("</area_B><diff>").append(diff).append("</diff></growth>");
			}
			xml.append("\n" + "<urlwms><![CDATA[").append(getWmsRequest(resultTableName, envelope))
					.append("]]></urlwms>");
			xml.append("\n" + "<urlwfs><![CDATA[").append(getWfsRequest(resultTableName)).append("]]></urlwfs>");
			xml.append("\n" + "<urlwfs-shape><![CDATA[").append(getShapeWfsRequest(resultTableName))
					.append("]]></urlwfs-shape>");
			xml.append("\n" + "</result></UrbanRuralGrowthIdentification>");
			return xml.toString();
		}
		finally {
			if (rs != null) {
				rs.close();
			}
		}
	}
	
	/*
	 * metodo che controlla che la tabella di intersezione non sia vuota
	 */
	private void checkIntersectionTable(String schema, String resultIntersectTableName) throws Exception {
		String query = "select count(*) from " + schema + ".\"" + resultIntersectTableName + "\"";
		LOGGER.debug("count query " + query);
		
		ResultSet result = dbConnection.executeQueryWithResultSet(query, connection);
		if (result.next()) {
			int count = result.getInt(1);
			if (count == 0) {
				throw new WPSException(properties.loadByNameFromMessages("no_data"));
			}
		}
	}
	
	/*
	 * metodo che compone la richiesta wms
	 */
	private String getWmsRequest(String resultTableName, ReferencedEnvelope envelope)
			throws UnsupportedOperationException, Exception {
		String style = catalog.getLayerByName(resultTableName).getDefaultStyle().getName();
		String srs = catalog.getLayerByName(resultTableName).getResource().getSRS();
		
		String urlwms = properties.loadByNameFromProject(Constants.RESTURL) + "/" + workspace
				+ "/wms?service=WMS&version=1.1.0&request=GetMap&layers="
				+ catalog.getLayerByName(resultTableName).getResource().prefixedName() + "&bbox="
				+ catalog.getLayerByName(resultTableName).getResource().boundingBox().getMinX() + ","
				+ catalog.getLayerByName(resultTableName).getResource().boundingBox().getMinY() + ","
				+ catalog.getLayerByName(resultTableName).getResource().boundingBox().getMaxX() + ","
				+ catalog.getLayerByName(resultTableName).getResource().boundingBox().getMaxY() + "&styles=" + style
				+ "&width=" + properties.loadByNameFromProject(Constants.URBAN_RURAL_GROWTH_WIDTH_WMS_LAYER)
				+ "&height=" + properties.loadByNameFromProject(Constants.URBAN_RURAL_GROWTH_HEIGHT_WMS_LAYER)
				+ "&srs=" + srs + "&format="
				+ properties.loadByNameFromProject(Constants.URBAN_RURAL_GROWTH_FORMAT_WMS_LAYER);
		
		return urlwms;
		
	}
	
	/*
	 * metodo che compone la richiesta wfs
	 */
	private String getWfsRequest(String resultTableName) {
		String urlwfs = properties.loadByNameFromProject(Constants.RESTURL) + "/" + workspace
				+ "/wfs?request=GetFeature&typename="
				+ catalog.getLayerByName(resultTableName).getResource().prefixedName() + "&count="
				+ properties.loadByNameFromProject(Constants.URBAN_RURAL_GROWTH_WFS_MAX_FEATURES);
		return urlwfs;
		
	}
	
	/*
	 * metodo che compone la richiesta wfs per shape es:
	 * http://localhost:8080/geoserver/sinergis/ows?service=WFS&version
	 * =1.0.0&request=GetFeature&typeName=sinergis:wps_ep10_intersect1409670563646&maxFeatures=50&outputFormat=SHAPE-ZIP
	 */
	private String getShapeWfsRequest(String resultTableName) {
		String urlwfs = properties.loadByNameFromProject(Constants.RESTURL) + "/" + workspace
				+ "/wfs?service=WFS&request=GetFeature&typename="
				+ catalog.getLayerByName(resultTableName).getResource().prefixedName() + "&outputFormat=SHAPE-ZIP";
		return urlwfs;
	}
	
	@SuppressWarnings("deprecation")
	private boolean createWorkSpaceGeoserver(GeoServerRESTPublisher publisher) {
		boolean result = true;
		if (catalog.getWorkspaceByName(workspace) == null) {
			result = publisher.createWorkspace(workspace);
			
			GSPostGISDatastoreEncoder pg = new GSPostGISDatastoreEncoder();
			pg.setName(properties.loadByNameFromProject(Constants.POSTGIS_NAME));
			pg.setHost(properties.loadByNameFromProject(Constants.POSTGIS_HOST));
			pg.setPort(Integer.valueOf(properties.loadByNameFromProject(Constants.POSTGIS_PORT)));
			pg.setSchema(properties.loadByNameFromProject(Constants.POSTGIS_SCHEMA));
			pg.setUser(properties.loadByNameFromProject(Constants.POSTGIS_USER));
			pg.setPassword(properties.loadByNameFromProject(Constants.POSTGIS_PASS));
			pg.setDatabase(properties.loadByNameFromProject(Constants.POSTGIS_DB));
			publisher.createPostGISDatastore(workspace, pg);
			
		}
		return result;
	}
}
