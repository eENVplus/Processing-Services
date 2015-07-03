package it.sinergis.ep09.utils;

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.encoder.GSResourceEncoder.ProjectionPolicy;
import it.sinergis.ep09.flooding.wps.ComputeFinalFloodProbabilityMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.CoverageInfo;
import org.geoserver.wps.WPSException;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.gml.GMLFilterFeature;
import org.geotools.gml.GMLReceiver;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public class GeoserverUtils {
	
	private static final Logger LOGGER = Logger.getLogger(GeoserverUtils.class);
	
	/*
	 * pubblica il layer su geoserver
	 */
	public static String publisherLayerOnGeoserver(String raster, ReferencedEnvelope envelope, double width,
			double heigth, String landslideStyle, Catalog catalog, String layerName, String ws)
			throws FileNotFoundException {
		
		String getMapRequest = null;
		String outputRaster = ProjectProperties.loadByName(Constants.TMP_PATH) + layerName + "_"
				+ System.currentTimeMillis() + Constants.TIF_EXTENSION;
		try {
			String srs = ProcessUtils.getEpsgFromCoordinateReferenceSystem(envelope.getCoordinateReferenceSystem());
			double[] bbox = new double[4];
			bbox[0] = envelope.getMinX();
			bbox[1] = envelope.getMinY();
			bbox[2] = envelope.getMaxX();
			bbox[3] = envelope.getMaxY();
			
			//normalizzo il tif prima di fare l'upload su geoserver
			
			raster = GdalOperation.translateToNormalize(raster, outputRaster, srs);
			File outputRasterFile = new File(raster);
			
			GeoServerRESTPublisher publisher = ProcessUtils.getRestPublisher();
			
			//se non c'è il ws temp lo creo
			boolean wsCreated = true;
			boolean publishResult = false;
			if (catalog.getWorkspaceByName(ws) == null) {
				wsCreated = publisher.createWorkspace(ws);
			}
			
			String fileName = null;
			if (outputRasterFile.getName() != null) {
				fileName = outputRasterFile.getName().substring(0, outputRasterFile.getName().indexOf("."));
			}
			
			if (landslideStyle == null) {
				landslideStyle = "raster";
			}
			
			if (wsCreated) {
				publishResult = publisher.publishGeoTIFF(ws, fileName, fileName, outputRasterFile, srs,
						ProjectionPolicy.FORCE_DECLARED, landslideStyle, null);
				CoverageInfo coverage = catalog.getCoverageByName(fileName);
				if (coverage != null) {
					coverage.getSupportedFormats().add(Constants.GEOTIFF);
					catalog.save(coverage);
				}
				
				if (publishResult) {
					getMapRequest = ProjectProperties.loadByName(Constants.GEOSERVER_REST_URL)
							+ "/wms?SERVICE=WMS&VERSION=" + ProjectProperties.loadByName(Constants.WMS_VERSION)
							+ "&request=GetMap&LAYERS=" + fileName + "&bbox=" + envelope.getMinX() + ","
							+ envelope.getMinY() + "," + envelope.getMaxX() + "," + envelope.getMaxY() + "&srs=" + srs
							+ "&WIDTH=" + ((int) width) + "&HEIGHT=" + ((int) heigth) + "&FORMAT=image/jpeg";
					
					getMapRequest += "&styles=" + landslideStyle;
				}
			}
			
		}
		catch (Exception e) {
			throw new WPSException("error publishing layer on geoserver");
		}
		finally {
			if (raster != null) {
				ProcessUtils.deleteTmpFile(raster);
			}
			if (outputRaster != null) {
				ProcessUtils.deleteTmpFile(outputRaster);
			}
		}
		
		return getMapRequest;
	}
	
	/*
	 * metodo che recupera il rastser o la sfs dalla cartella data di geoserver oppure se non lo trova lo riscrive su fs
	 */
	public static String getObjPath(Catalog catalog, Object obj) throws MalformedURLException {
		String filePath = null;
		
		String objName = null;
		GridCoverage2D raster = null;
		SimpleFeatureCollection sfs = null;
		String objUrl = null;
		
		if (obj instanceof GridCoverage2D) {
			raster = (GridCoverage2D) obj;
			if (raster.getName() != null) {
				objName = raster.getName().toString();
				if (catalog.getCoverageByName(objName) != null) {
					objUrl = catalog.getCoverageByName(objName).getStore().getURL();
				}
			}
		} else if (obj instanceof SimpleFeatureCollection) {
			sfs = (SimpleFeatureCollection) obj;
			if (sfs.getSchema() != null) {
				objName = sfs.getSchema().getTypeName();
				if (catalog.getFeatureTypeByName(objName) != null && catalog.getFeatureTypeByName(objName).getStore() != null && catalog.getFeatureTypeByName(objName).getStore().getConnectionParameters()!=null) {
					objUrl = (String) catalog.getFeatureTypeByName(objName).getStore().getConnectionParameters().get("url");
				}
			}
		}
		
		//vedo se riesco a recuperarlo dal catalog di geoserver senza doverlo riscrivere sul filesystem
		if (objName != null && objUrl != null) {
			
			
			URL url = new URL(objUrl);
			
			String geoserverPath = url.getPath();
			
			LOGGER.debug("inizio recupero layer dalla cartella di geoserver " + geoserverPath);
			
			//recupero dove geoserver ha i raster caricati
			File dirGeoserver = null;
			try {
				dirGeoserver = catalog.getResourceLoader().find(geoserverPath);
				LOGGER.debug("path geoserver " + dirGeoserver);
				if (dirGeoserver.exists()) {
					filePath = dirGeoserver.getPath();
					LOGGER.debug("fine recupero da " + filePath);
				}
			}
			catch (IOException e) {
				LOGGER.error("errore nel recupero di " + objName + " dalla directory di geoserver "
						+ geoserverPath, e);
			}
		}
		
		//se non sono riuscita a recuperare il path oppure il file non esiste riscrivo il raster su fs
		if (filePath == null) {
			if (raster != null) {
				filePath = ProjectProperties.loadByName(Constants.TMP_PATH) + raster.getName() + "_"
						+ System.currentTimeMillis() + Constants.TIF_EXTENSION;
				LOGGER.debug("inizio scrittura raster " + raster.getName() + " in " + filePath);
				ProcessUtils.writeRasterSuFile(filePath, raster);
			} else if (sfs != null) {
				filePath = ProjectProperties.loadByName(Constants.TMP_PATH) + sfs.getSchema().getTypeName() + "_"
						+ System.currentTimeMillis() + Constants.SHP_EXTENSION;
				LOGGER.debug("inizio scrittura sfs " + sfs.getSchema().getTypeName() + " in " + filePath);
				ProcessUtils.writeShpSuFile(filePath, sfs);
			}
		}
		return filePath;
	}
	
	public static void parseGML(SimpleFeatureCollection feature) {
		GMLReceiver featureFilter = new GMLReceiver((DefaultFeatureCollection) feature);		
		try {
			Object obj = featureFilter.getProperty("GE_EVENTENVIRONMENT");
		}
		catch (SAXNotRecognizedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (SAXNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
