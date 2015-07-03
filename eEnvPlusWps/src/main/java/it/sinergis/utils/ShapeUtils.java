package it.sinergis.utils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.geoserver.wps.WPSException;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureWriter;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;

public class ShapeUtils {
	
	private static final Logger LOGGER = Logger.getLogger(ShapeUtils.class);
	/*
	 * metodo che scrive feature per feature lo shape N:B: lo schema e' cablato, formato da geometria e tipo di
	 * operazione
	 */
	public static boolean writeFeaturesInShapefile(String shapePath, SimpleFeatureCollection resultFeatures,
			SimpleFeatureStore resultFeatureSourceDataStore) throws Exception {
		
		SimpleFeatureType featureType = DataUtilities.createType(resultFeatureSourceDataStore.getName().getLocalPart(),
				"result_geom:Polygon,op_type:String");
		ShapefileDataStore newDataStore = createShapeFile(shapePath
				+ resultFeatureSourceDataStore.getName().getLocalPart() + ".shp", featureType, resultFeatures
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
					f.setAttributes(new Object[] { sf.getDefaultGeometry(), sf.getAttribute("operation_type:String") });
					fw.write();
				}
				
			}
			transaction.commit();
			return true;
		}
		catch (IOException eek) {
			eek.printStackTrace();
			try {
				transaction.rollback();
			}
			catch (IOException doubleEeek) {
				
			}
			LOGGER.error("errore nella scrittura sullo shapefile ", eek);
			return false;
		}
		finally {
			fw.close();
			transaction.close();
		}
	}
	
	/*
	 * creazione dello shapefile
	 */
	public static ShapefileDataStore createShapeFile(String shpFilePath, SimpleFeatureType featureType,
			CoordinateReferenceSystem coordinateReferenceSystem) throws Exception {
		
		if(coordinateReferenceSystem==null){
			coordinateReferenceSystem = CRS.decode("EPSG:" + ReadFromConfig.loadByName(Constants.DEFAULT_FORCED_EPSG));
		}
		File shpFile = new File(shpFilePath);
		
		ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
		Map<String, Serializable> params = new HashMap<String, Serializable>();
		params.put("url", shpFile.toURI().toURL());
		params.put("create spatial index", Boolean.TRUE);
		
		ShapefileDataStore newDataStore = (ShapefileDataStore) dataStoreFactory.createNewDataStore(params);
		newDataStore.forceSchemaCRS(coordinateReferenceSystem);
		newDataStore.createSchema(featureType);
		return newDataStore;
	}
	
	
	public static String zipShpResult(String path) throws Exception{
		String shapePath = path.substring(0,path.lastIndexOf(File.separator));
		File dir = new File(shapePath);
		String shapeName = path.substring(path.lastIndexOf(File.separator)+1);

         
         String name = shapeName.substring(0,shapeName.indexOf(".shp"));
         String files_accepted = name+".shx,"+name+".shp,"+name+".prj,"+name+".fix,"+name+".dbf";
         File zip = new File(shapePath+File.separator+name+".zip");
         String[] files  = dir.list();
         List<File> list = new ArrayList<File>();
		 for (String fileN : files) {
			 if(files_accepted.indexOf(fileN)!=-1){
				 File file = new File(shapePath+File.separator+fileN);
				 list.add(file);
			 }
		 }
		 
		 EnvPlusUtils.addFilesToExistingZip(zip,list);
		 for (File f : list) {
			 FileUtils.deleteQuietly(f);
		 }
		 
		 return zip.getAbsolutePath();
	}
	
	/*
	 * scrive lo shape da una features collection in input
	 */
	public static String writeShapefile(String shapePath, SimpleFeatureCollection resultFeatures, String shapeName)
			throws Exception {
		
		String pathShp = shapePath + shapeName + ".shp";
		ShapefileDataStore newDataStore = createShapeFile(pathShp, resultFeatures.getSchema(), resultFeatures
				.getSchema().getCoordinateReferenceSystem());
		SimpleFeatureSource featureSource = newDataStore.getFeatureSource(shapeName);
		
		DefaultTransaction transaction = new DefaultTransaction();
		if (featureSource instanceof SimpleFeatureStore) {
			SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;
			featureStore.addFeatures(resultFeatures);
			transaction.commit();
		}
		transaction.close();
		return pathShp;
	}
	
	/*
	 * metodo che legge lo shape
	 */
	public static SimpleFeatureCollection readShp(String file) throws IOException {
		DataStore dataStore = null;
		try{
			Map<String, Serializable> params = new HashMap<String, Serializable>();
			File shp = new File(file);
			params.put("url", shp.toURI().toURL());
			
			dataStore = DataStoreFinder.getDataStore(params);
			String typeName = dataStore.getTypeNames()[0];
		
			SimpleFeatureSource source = dataStore.getFeatureSource(typeName);
			return source.getFeatures();
		}finally{
			if(dataStore!=null)
				dataStore.dispose();
		}
	}
	
	
	public static String getZipFilePath(String tempdir, String url, long time){
		String separator1 = "/";
		String separator2 = "\\";
		String filename = "";
		url = url.toLowerCase();
		if(url.endsWith(".zip")){
			if(url.contains(separator1)){
				filename = url.substring(url.lastIndexOf(separator1)+1);
			}
			if(url.contains(separator2)){
				filename = url.substring(url.lastIndexOf(separator2)+1);
			}
		}
		String prefix = String.valueOf(time);
		filename = filename.replace(".zip", "_zip");
		if(tempdir.endsWith(separator2) || tempdir.endsWith(separator1))
			return tempdir + prefix+"_"+filename;
		return tempdir + File.separator + prefix+"_"+filename;
	}
	
	public static SimpleFeatureCollection readFeatureCollectionFromZip(String pathZip, String urlShpZipped,long systemCurrentMillis,SimpleFeatureCollection features) throws Exception{
		Boolean unzip = EnvPlusUtils.unzip(urlShpZipped, pathZip);
		Boolean containsShp = Boolean.FALSE;
		if(unzip){
			File dir = new File(pathZip);
	    	// controllo il contenuto dello zip
	    	// deve contenere uno shapefile valido
	    	for (File file : dir.listFiles()) {
				if(file.getName().toLowerCase().endsWith(".shp")){
					containsShp = Boolean.TRUE;
					try {
						features = ShapeUtils.readShp(file.getAbsolutePath());
						break;
					} catch (MalformedURLException e) {
						throw new WPSException("zipped file has a malformed url - " + file.getAbsolutePath()+" - " +e.getMessage());
					} catch (IOException e) {
						throw new WPSException("shapefile " +file.getAbsolutePath() +" not valid - "+ e.getMessage());
					}
				}
			}
		}else{
			throw new WPSException("impossible unzip the file  " +urlShpZipped );
		}
		
		if(!containsShp){
			throw new WPSException("the zip " +urlShpZipped +" does not contain files with extension .shp");
		}else{
			if(features==null){
				throw new WPSException("the zip " +urlShpZipped +" does not contain features");
			}else{
				if(features.getSchema()!=null && features.getSchema().getCoordinateReferenceSystem()==null){
					throw new WPSException("the zip " +urlShpZipped +" contains data with no coordinate reference system ");
				}
			}
		}
		
		return features;
	}
	
	//Restituisco la lista delle feature che sovrappongono la geometria del layer
	public static SimpleFeatureCollection checkOverlapsFeatureCollection(SimpleFeatureCollection list, SimpleFeature layer){
		
		DefaultFeatureCollection result = new DefaultFeatureCollection();
		
		Geometry gmlArea = (Geometry) layer.getDefaultGeometry();
				
		SimpleFeatureIterator iter = list.features();

			
		while(iter.hasNext()){
			SimpleFeature next = iter.next();	
			Geometry geom = (Geometry) next.getDefaultGeometry();
		
			if(gmlArea.overlaps(geom) || gmlArea.contains(geom)){
				result.add(next);
			}
		}
		
		iter.close();
		
		SimpleFeatureCollection collectionResult = result;
		
		return collectionResult;
		
	}
	
	public static boolean checkShapeAttribute(SimpleFeatureType schema, String attributeName){
		for(int i=0; i<schema.getAttributeCount(); i++){
				if(schema.getAttributeDescriptors().get(i).getLocalName().equalsIgnoreCase(attributeName)){
					return true;
				}
			}
		return false;
	}
	
	
}
