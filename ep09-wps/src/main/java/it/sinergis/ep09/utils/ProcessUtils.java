package it.sinergis.ep09.utils;

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.media.jai.PlanarImage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.geoserver.wps.WPSException;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.GridFormatFinder;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.factory.Hints;
import org.geotools.gce.geotiff.GeoTiffWriter;
import org.geotools.resources.image.ImageUtilities;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class ProcessUtils {
	
	private static Logger LOGGER = Logger.getLogger(ProcessUtils.class);
	
	public static String exec(String command, boolean getOutput) throws Exception {
		
		Runtime run = Runtime.getRuntime();
		Process pp = run.exec(command);
		
		StringBuffer response = new StringBuffer();
		BufferedReader streamReader = null;
		if (getOutput) {
			//se il processo restituisce in output delle informazioni e non solo degli errori (vedi zonal_stat.py)
			streamReader = new BufferedReader(new InputStreamReader(pp.getInputStream()));
		}
		else {
			streamReader = new BufferedReader(new InputStreamReader(pp.getErrorStream()));
		}
		
		
		for (String line; (line = streamReader.readLine()) != null;) {
			LOGGER.debug("log di gdal " + line);
			if (!getOutput && (line.toLowerCase().contains("warning") || !line.toLowerCase().contains("error"))) {
				continue;
			}
			response.append(line);
		}
		
		if (!getOutput) {
			if (!response.toString().equals("")) {
				LOGGER.error("errore di gdal " + response.toString());
				throw new Exception(response.toString());
			}
		}
		
		return response.toString();
	}
	
	public static String execArrayString(String[] command, boolean getOutput) throws Exception {
		
		Runtime run = Runtime.getRuntime();
		Process pp = run.exec(command);
		
		StringBuffer response = new StringBuffer();
		BufferedReader streamReader = null;
		if (getOutput) {
			//se il processo restituisce in output delle informazioni e non solo degli errori (vedi zonal_stat.py)
			streamReader = new BufferedReader(new InputStreamReader(pp.getInputStream()));
		}
		else {
			streamReader = new BufferedReader(new InputStreamReader(pp.getErrorStream()));
		}
		
		
		for (String line; (line = streamReader.readLine()) != null;) {
			LOGGER.debug("log di gdal " + line);
			if (!getOutput && (line.toLowerCase().contains("warning") || !line.toLowerCase().contains("error"))) {
				continue;
			}
			response.append(line);
		}
		
		if (!getOutput) {
			if (!response.toString().equals("")) {
				LOGGER.error("errore di gdal " + response.toString());
				throw new Exception(response.toString());
			}
		}
		
		return response.toString();
	}
	
	public static DocumentBuilder initDocumentBuilder() throws ParserConfigurationException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		return docBuilder;
	}
	
	/*
	 * metodo che restituisce il primo sistema di riferimento presente
	 */
	public static String getEpsgFromCoordinateReferenceSystem(CoordinateReferenceSystem coord) {
		Set<ReferenceIdentifier> identifiers = coord.getIdentifiers();
		if (identifiers != null) {
			Iterator<ReferenceIdentifier> iterator = identifiers.iterator();
			if (iterator.hasNext()) {
				ReferenceIdentifier identifier = iterator.next();
				if (identifier.getCodeSpace() != null && identifier.getCode() != null) {
					return identifier.getCodeSpace() + ":" + identifier.getCode();
				}
			}
		}
		return null;
	}
	
	/*
	 * scrive il raster su un file in modo da poter lanciare il comando gdal
	 */
	public static boolean writeRasterSuFile(String pathFile, GridCoverage2D raster) {
		File fileDem = new File(pathFile);
		
		GeoTiffWriter writer = null;
		try {
			if (fileDem != null) {
				LOGGER.debug("inizio a scrivere il raster " + fileDem.getName() + " in " + pathFile);
				writer = new GeoTiffWriter(fileDem);
				writer.write(raster, null);
				LOGGER.debug("fine scrittura raster " + fileDem.getName() + " in " + pathFile);
			}
		}
		catch (Exception e) {
			LOGGER.error("errore nella scrittura del raster " + raster.getName(), e);
			throw new WPSException("errore during writing raster " + raster.getName(), e);
		}
		finally {
			try {
				writer.dispose();
				raster.dispose(false);
				
				//per togliere il lock sul file
				PlanarImage planarImage = (PlanarImage) raster.getRenderedImage();
				ImageUtilities.disposePlanarImageChain(planarImage);
			}
			catch (Throwable e) {
				LOGGER.error("errore nella chiusura del writer ", e);
				throw new WPSException("errore during closing raster " + raster.getName(), e);
			}
		}
		return true;
	}
	
	/*
	 * scrive il raster su un file in modo da poter lanciare il comando gdal
	 */
	public static boolean writeShpSuFile(String pathFile, SimpleFeatureCollection sfs) {
		File fileShp = new File(pathFile);
		
		ShapefileDataStore newDataStore = null;
		try {
			
			ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
			
			Map<String, Serializable> params = new HashMap<String, Serializable>();
			params.put("url", fileShp.toURI().toURL());
			params.put("create spatial index", Boolean.TRUE);
			
			newDataStore = (ShapefileDataStore) dataStoreFactory.createNewDataStore(params);
			newDataStore.createSchema(sfs.getSchema());
			
			 /*
	         * Write the features to the shapefile
	         */
	        Transaction transaction = new DefaultTransaction("create");

	        String typeName = newDataStore.getTypeNames()[0];
	        SimpleFeatureSource featureSource = newDataStore.getFeatureSource(typeName);

	        if (featureSource instanceof SimpleFeatureStore) {
	            SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;
	            featureStore.setTransaction(transaction);
	            try {
	                featureStore.addFeatures(sfs);
	                transaction.commit();
	            } catch (Exception problem) {
	                problem.printStackTrace();
	                transaction.rollback();
	            } finally {
	                transaction.close();
	            }
	        } else {
	        	throw new WPSException(typeName + " does not support read/write access");
	        }
			
			newDataStore.forceSchemaCRS(sfs.getSchema().getCoordinateReferenceSystem());
		}
		catch (Exception e) {
			LOGGER.error("errore nella scrittura della simpleFeatureCollection " + sfs.getSchema(), e);
			throw new WPSException("errore during writing simpleFeatureCollection " + sfs.getSchema(), e);
		}
		finally {
			try {
				if (newDataStore != null) {
					newDataStore.dispose();
				}
			}
			catch (Throwable e) {
				LOGGER.error("errore nella chiusura del writer ", e);
				throw new WPSException("errore during closing sfs " + sfs.getSchema(), e);
			}
		}
		return true;
	}
	
	/*
	 * cancella i file temporanei
	 */
	public static boolean deleteTmpFile(String filePath) {
		File tmpFile = new File(filePath);
		if (tmpFile != null && tmpFile.exists()
				&& ProjectProperties.loadByName(Constants.DELETE_TMP_RASTER).equals("true")) {
			LOGGER.debug("cancello il file temporaneo " + tmpFile.getName());
			return tmpFile.delete();
		}
		return false;
	}
	
	public static GeoServerRESTPublisher getRestPublisher() {
		GeoServerRESTPublisher publisher = new GeoServerRESTPublisher(
				ProjectProperties.loadByName(Constants.GEOSERVER_REST_URL),
				ProjectProperties.loadByName(Constants.GEOSERVER_REST_USER),
				ProjectProperties.loadByName(Constants.GEOSERVER_REST_PW));
		return publisher;
	}
	
	public static GeoServerRESTReader getRestReader() throws MalformedURLException {
		GeoServerRESTReader reader = new GeoServerRESTReader(
				ProjectProperties.loadByName(Constants.GEOSERVER_REST_URL),
				ProjectProperties.loadByName(Constants.GEOSERVER_REST_USER),
				ProjectProperties.loadByName(Constants.GEOSERVER_REST_PW));
		return reader;
	}
	
	/*
	 * restituisce il raster in formato GridCoverage2D da un file su fs
	 */
	public static GridCoverage2D getGridCoverageFromFile(File file, CoordinateReferenceSystem coords) {
		GridCoverage2D gridCoverage = null;
		try {
			gridCoverage = GridFormatFinder.findFormat(file)
					.getReader(file, new Hints(Hints.DEFAULT_COORDINATE_REFERENCE_SYSTEM, coords)).read(null);
		}
		catch (Exception e) {
			LOGGER.error("errore nella lettura del raster ", e);
			e.printStackTrace();
			throw new WPSException("errore during read raster " + gridCoverage.getName(), e);
		}
		return gridCoverage;
	}
	
	public static void checkFieldLayer(SimpleFeatureCollection layer, String field) {
		if (layer != null && layer.size()>0) {
			boolean trovato = false;
			String listField = "";
			//controllo che il layer abbia il campo su cui rasterizzare
			List<AttributeDescriptor> attributeDescriptor = layer.getSchema().getAttributeDescriptors();
			for (AttributeDescriptor attribute : attributeDescriptor) {
				listField += attribute.getLocalName() + ",";
				if (attribute.getLocalName() != null && attribute.getLocalName().equalsIgnoreCase(field)) {
					trovato = true;
					break;
				}
			}
			
			if (!trovato) {
				LOGGER.error("valueFieldForRasterize " + field + "  is wrong, listField are " + listField);
				throw new WPSException("valueFieldForRasterize " + field + "  is wrong, listField are " + listField);
			}
		} 
	}
	
}
