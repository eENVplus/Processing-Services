package it.sinergis.wps;

import it.sinergis.utils.Constants;
import it.sinergis.utils.DbUtils;
import it.sinergis.utils.EnvPlusUtils;
import it.sinergis.utils.Layer;
import it.sinergis.utils.ReadFromConfig;
import it.sinergis.utils.ShapeUtils;
import it.sinergis.utils.TopologicalValidationUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.catalog.StoreInfo;
import org.geoserver.wps.WPSException;
import org.geoserver.wps.gs.GeoServerProcess;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureWriter;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.ProgressListener;

@DescribeProcess(title = "topologicalValidationShp", description = "Topological validation. Input data : compressed shapefiles")
public class TopologicalValidationShp implements GeoServerProcess {
	

	private static final Logger LOGGER = Logger.getLogger(TopologicalValidationShp.class);
	
	private Catalog catalog;
	
	private String resultTableName = null;
	private long systemCurrentMillis;
	private String lowerLayerName = null;
	private String upperLayerName = null;
	
	private String workspace = null;
	private String store = null;
	private String schema = null;
	private String tempDir = null;
	
	public TopologicalValidationShp(Catalog catalog) {
		this.catalog = catalog;
		this.workspace = ReadFromConfig.loadByName(Constants.GEOSERVER_WORKSPACE);
		this.store = ReadFromConfig.loadByName(Constants.GEOSERVER_DATASTORE);
		this.tempDir = ReadFromConfig.loadByName(Constants.SHP_PATH_TEMP_DIR)+Constants.NR_TOPOLOGICAL_VALIDATION;
	}
	
	@DescribeResult(name = "result", description = "output result")
	public String execute(
			@DescribeParameter(name = "pathLowerLevelShpZipped", description = "path of zip file containing lower level features (shapefile compressed)")
			String pathLowerLevelShpZipped,
			@DescribeParameter(name = "pathUpperLevelShpZipped", description = "path of zip file containing upper level features (shapefile compressed)")
			String pathUpperLevelShpZipped,
			@DescribeParameter(name = "validationType", description = "validation type: 1 for upper-lower validation, 2 for holes validation, 3 for lower validation", min = 0) 
			String validationType,
			@DescribeParameter(name = "emailAddresses", description = "email address/es for receiving notication at the end of wps process", min = 0) 
			String emailAddresses,
			ProgressListener progressListener) throws Exception {
		
		Layer lowerLayer = null;
		Layer upperLayer = null;
		Layer resultLayer = null;
		String pathLowerZip = null;
		String pathUpperZip = null;
		String idmulti = "";
		try{
			
		
			idmulti = EnvPlusUtils.registerWNS(emailAddresses); 
			
			String check = checkInput(pathLowerLevelShpZipped, pathUpperLevelShpZipped, validationType);
			if (!check.equals("")) {
				throw new WPSException(check);
			}
			
			progressListener.started();
			systemCurrentMillis = System.currentTimeMillis();
			SimpleFeatureCollection lowerFeatures  =null;
			SimpleFeatureCollection upperFeatures =null;
			// lettura dello shapefile lower level
			pathLowerZip = ShapeUtils.getZipFilePath(tempDir,pathLowerLevelShpZipped,systemCurrentMillis);
			lowerFeatures = ShapeUtils.readFeatureCollectionFromZip(pathLowerZip,pathLowerLevelShpZipped,systemCurrentMillis,lowerFeatures);
			TopologicalValidationUtils.checkInputFeatureCollection(lowerFeatures, validationType);
			
			// lettura dello shapefile upper level
			pathUpperZip = ShapeUtils.getZipFilePath(tempDir,pathUpperLevelShpZipped,systemCurrentMillis);
			upperFeatures = ShapeUtils.readFeatureCollectionFromZip(pathUpperZip,pathUpperLevelShpZipped,systemCurrentMillis,upperFeatures);
			TopologicalValidationUtils.checkInputFeatureCollection(upperFeatures, validationType);
			
			// controllo che i dati dei 2 shapefiles
			// abbiano lo stesso sistema di riferimento
			if(lowerFeatures.getSchema()!=null && upperFeatures.getSchema()!=null  &&
					!lowerFeatures.getSchema().getCoordinateReferenceSystem().getName().getCode().equalsIgnoreCase(
							upperFeatures.getSchema().getCoordinateReferenceSystem().getName().getCode())){
				throw new WPSException("shapafiles containing data with coordinate reference system not homogenous");
			}
			// copia in memoria per poter cancellare 
			// gli shapefiles unzippati
			DefaultFeatureCollection lowmemory = new DefaultFeatureCollection();
			FeatureIterator<SimpleFeature> iter =lowerFeatures.features();
			while ( iter.hasNext()) {
				SimpleFeature lowFeat = iter.next();
				lowmemory.add(lowFeat);
			}
			iter.close();
			DefaultFeatureCollection upmemory = new DefaultFeatureCollection();
			FeatureIterator<SimpleFeature> iter1 =upperFeatures.features();
			while ( iter1.hasNext()) {
				SimpleFeature upFeat = iter1.next();
				upmemory.add(upFeat);
			}
			iter1.close();
			// esecuzione controlli topologici
			DataStoreInfo storeInfo = catalog.getDataStoreByName(workspace, store);
			Map<String, Serializable> params = storeInfo.getConnectionParameters();
			
			schema = (String) params.get("schema");
			resultTableName = "result" + systemCurrentMillis;
					
			resultLayer = new Layer(schema, resultTableName);
			
			if (!DbUtils.createResultTable(storeInfo,resultLayer)) {
				throw new Exception("errore while connecting to db: ");
			}
					
			//import in postgis le lower e le upper (se non ho richiesto solo la validazione di primo livello)
			lowerLayerName = EnvPlusUtils.importInPostgisFeatures(lowmemory, catalog, workspace, store,
					systemCurrentMillis);
			if (validationType == null || validationType.indexOf(Constants.HOLES_VALIDATION) != -1
					|| validationType.indexOf(Constants.UPPER_LOWER_VALIDATION) != -1) {
				upperLayerName = EnvPlusUtils.importInPostgisFeatures(upmemory, catalog, workspace, store,
						systemCurrentMillis);
			}
			
			lowerLayer = new Layer(schema, lowerLayerName);
			upperLayer = new Layer(schema, upperLayerName);
			
			if (validationType == null) {
				TopologicalValidationUtils.callTopologicalFunction(lowmemory, upmemory, null,lowerLayer, upperLayer, resultLayer);
			}
			else {
				String[] validationTypeArray = validationType.split(",");
				for (String type : validationTypeArray) {
					if (type != null && !type.equals("")) {
						TopologicalValidationUtils.callTopologicalFunction(lowmemory, upmemory, type, lowerLayer, upperLayer, resultLayer);
					}
				}
			}
			
			//recupero i dati restituiti
			String result = getResult(storeInfo, progressListener, resultTableName);
			
			EnvPlusUtils.doNotificationWNS(idmulti);
			
			return result;
			
		
		}catch (Exception e) {
			LOGGER.error("error while executing TopologicalValidationShp", e);
			throw e;
		}
		finally {
			if(lowerLayer!=null){
				DbUtils.deleteTable(lowerLayer);
			}
			if (upperLayer!=null && upperLayerName != null) {
				DbUtils.deleteTable(upperLayer);
			}
			if (resultLayer!=null) {
				DbUtils.deleteTable(resultLayer);
			}
			if(pathLowerZip!=null){
				FileUtils.deleteDirectory(new File(pathLowerZip));
			}
			if(pathUpperZip!=null){
				FileUtils.deleteDirectory(new File(pathUpperZip));
			}
			if(!idmulti.equals("")){
				EnvPlusUtils.unregisterMulti(idmulti);
			}
			
		}
	}
	
	
	
	/*
	 * metodo che controlla i dati di input
	 */
	private String checkInput(String urlLowerLevelShpZipped, String urlUpperLevelShpZipped, String validationType) {
		
		if(urlLowerLevelShpZipped==null || !urlLowerLevelShpZipped.toLowerCase().endsWith(".zip")){
			return "input parameter not valid - pathLowerLevelShpZipped=" +urlLowerLevelShpZipped ;
		}
		if(urlUpperLevelShpZipped==null || !urlUpperLevelShpZipped.toLowerCase().endsWith(".zip")){
			return "input parameter not valid - pathUpperLevelShpZipped=" +urlUpperLevelShpZipped ;
		}
		if(validationType!=null && !validationType.equals("")){
			String[] validationTypeArray = validationType.split(",");
			java.util.List<String> validTypes = java.util.Arrays.asList(new String[]{Constants.HOLES_VALIDATION,Constants.LOWER_VALIDATION,Constants.UPPER_LOWER_VALIDATION});
			for (String type : validationTypeArray) {
				if (!validTypes.contains(type)) {
					return "input parameter not valid - validationtype: " + type + " not valid ";
				}
			}
		}
		return "";
	}
	
	
	/*
	 * recupero il risultato dalla feature-collection
	 */
	public String getResult(StoreInfo storeInfo, ProgressListener progressListener,
			String resultTableName) throws Exception {
		
		DataStore dataStore = DataStoreFinder.getDataStore(storeInfo.getConnectionParameters());
		SimpleFeatureStore resultFeatureSourceDataStore = (SimpleFeatureStore) dataStore
				.getFeatureSource(resultTableName);
		
		SimpleFeatureCollection resultFeatures = resultFeatureSourceDataStore.getFeatures();
		
		//per la scrittura dello shapefile
	
		LOGGER.debug("inizio scrittura shapefile ");
		String resultpath = writeShapefileResult(ReadFromConfig.loadByName(Constants.SHP_PATH_TOPOLOGICAL_VALIDATION), resultTableName, resultFeatures, resultFeatureSourceDataStore.getName().getLocalPart() );
	
		dataStore.dispose();
		
		resultpath = ShapeUtils.zipShpResult(resultpath);
		
		if (progressListener != null) {
			progressListener.progress(100);
			progressListener.complete();
		}
		
		if (resultpath.length() > 0) {
			return resultpath;
		}
		else {
			throw new Exception("filepath of result not valid");
		}
		
	}
	
	private String writeShapefileResult(String shapePath, String shapeName,  SimpleFeatureCollection resultFeatures,
			String typeName) throws Exception {
		
		String pathShp = shapePath + shapeName + ".shp";

		CoordinateReferenceSystem crs = resultFeatures.getSchema().getCoordinateReferenceSystem();
		if(crs==null){
			crs = CRS.decode("EPSG:" + ReadFromConfig.loadByName(Constants.DEFAULT_FORCED_EPSG));
		}
		// resultFeatureSourceDataStore.getName().getLocalPart()
		SimpleFeatureType featureType = DataUtilities.createType(typeName,
		"result_geom:Polygon,op_type:String");
		SimpleFeatureType type = DataUtilities.createSubType( featureType, null, crs );

		ShapefileDataStore newDataStore = ShapeUtils.createShapeFile(pathShp, type, resultFeatures
		.getSchema().getCoordinateReferenceSystem());

		DefaultTransaction transaction = new DefaultTransaction();
		FeatureWriter<SimpleFeatureType, SimpleFeature> fw = newDataStore.getFeatureWriterAppend(transaction);
		try {
			SimpleFeatureIterator iter = resultFeatures.features();
			while (iter.hasNext()) {
				SimpleFeature sf = iter.next();
				String geometryType = (String) sf.getAttribute("geometry_type");
				if (geometryType != null && geometryType.equals(Constants.GEOMETRY_TYPE_POLYGON)) {
					SimpleFeature f = fw.next();
					f.setAttributes(new Object[] { sf.getDefaultGeometry(), sf.getAttribute("operation_type") });
					fw.write();
				}
				
			}
			iter.close();
			transaction.commit();
		}
		catch (IOException eek) {
			eek.printStackTrace();
			try {
				transaction.rollback();
			}
			catch (IOException doubleEeek) {
				
			}
			LOGGER.error("errore nella scrittura sullo shapefile ", eek);
		}
		finally {
			fw.close();
			transaction.close();
			newDataStore.dispose();
		}
		
		return pathShp;
	}
	
}
