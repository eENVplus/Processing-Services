package it.sinergis.wps;

import it.sinergis.object.WpsResponse;
import it.sinergis.utils.Constants;
import it.sinergis.utils.EnvPlusUtils;
import it.sinergis.utils.ReadFromConfig;
import it.sinergis.utils.ShapeUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.geoserver.catalog.Catalog;
import org.geoserver.wps.gs.GeoServerProcess;
import org.geotools.data.DataUtilities;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;
import org.opengis.util.ProgressListener;

@DescribeProcess(title = "featureUnion", description = "Unione di features con validazione topologica")
public class FeatureUnion implements GeoServerProcess {
	
	private static final Logger LOGGER = Logger.getLogger(FeatureUnion.class);
	
	private Catalog catalog;
	
	private WpsResponse wpsResponse = null;
	
	public FeatureUnion(Catalog catalog) {
		this.catalog = catalog;
	}
	
	@DescribeResult(name = "result", description = "output result")
	public String execute(
			@DescribeParameter(name = "path zip features", description = "url or path where is the features to be merged")
			String pathFeatures,
			@DescribeParameter(name = "featuresLowerLevel", description = "polygon features type of lower level", min = 0)
			SimpleFeatureCollection lowerFeatures,
			@DescribeParameter(name = "partitionCheck", description = "flag for validation", min = 0)
			Boolean partitionCheck, ProgressListener progressListener) throws Exception {
		
		wpsResponse = new WpsResponse();
		
		//metodo che controlla l'input
		if (!checkInput(pathFeatures)) {
			return wpsResponse.toXml();
		}
		
		progressListener.started();
		
		URL url = null;
		File fileZip = null;
		if (pathFeatures.startsWith("http") || pathFeatures.startsWith("https")) {
			url = new URL(pathFeatures);
			fileZip = downloadFilesFromUrl(pathFeatures, url);
		}
		else {
			fileZip = new File(pathFeatures);
		}
		
		String pathUnZippedFile = fileZip.getPath().substring(0, fileZip.getPath().indexOf(".zip")) + "_"
				+ System.currentTimeMillis();
		SimpleFeatureCollection sfc1 = null;
		try {
			
			TopologicalValidation topologicalValidationWps = new TopologicalValidation(catalog);
			if (fileZip.getName().endsWith(".zip")) {
				List<String> files = EnvPlusUtils.unZipArchive(fileZip.getPath(), pathUnZippedFile);
				if (files == null || files.size() == 0) {
					wpsResponse.setErrorMessage("unzip archive " + fileZip.getPath() + " fails");
					return wpsResponse.toXml();
				}
				for (String file : files) {
					if (file.endsWith(".shp")) {
						SimpleFeatureCollection sfc2 = ShapeUtils.readShp(file);
						if (sfc1 == null) {
							sfc1 = sfc2;
						}
						else {
							SimpleFeatureCollection sfcTopologicalValidation = null;
							if (partitionCheck != null && partitionCheck) {
								sfcTopologicalValidation = topologicalValidationWps.execute(lowerFeatures, sfc2, null,
										progressListener);
							}
							if (sfcTopologicalValidation == null || sfcTopologicalValidation.size() == 0) {
								if (DataUtilities.compare(sfc1.getSchema(), sfc2.getSchema()) != -1) {
									sfc1 = featureUnion(sfc1, sfc2);
									if (sfc1 == null) {
										return wpsResponse.toXml();
									}
								}
								else {
									wpsResponse.setErrorMessage("the shape " + sfc1.getSchema().getTypeName()
											+ " schema doesn't match the shape " + sfc2.getSchema().getTypeName()
											+ " schema");
									return wpsResponse.toXml();
								}
							}
						}
					}
				}
			}
		}
		catch (Exception e) {
			wpsResponse
					.setErrorMessage(WpsResponse.ERRORE_GENERICO + " errore nel processamento del wps FeatureUnion ");
			LOGGER.error("errore nel processamento del wps FeatureUnion ", e);
		}
		finally {
			if (url != null) {
				deleteFile(fileZip.getPath());
			}
			deleteFile(pathUnZippedFile);
		}
		
		String pathUnionShape = ShapeUtils.writeShapefile(
				ReadFromConfig.loadByName(Constants.SHP_PATH_TOPOLOGICAL_VALIDATION), sfc1,
				"union_" + System.currentTimeMillis());
		wpsResponse.setPathUnionShapeFile(pathUnionShape);
		
		return wpsResponse.toXml();
	}
	
	private File downloadFilesFromUrl(String pathFeatures, URL url) {
		File fileZip = null;
		try {
			String fileName = pathFeatures.substring(pathFeatures.lastIndexOf("/"));
			fileZip = new File(ReadFromConfig.loadByName("unzip_path") + fileName);
			LOGGER.debug("copiatura del file alla url " + url.getPath() + " in " + fileZip.getPath());
			
			FileUtils.copyURLToFile(url, fileZip);
		}
		catch (IOException e) {
			LOGGER.error("errore nella copiatura del file dalla url " + url.getPath(), e);
			wpsResponse.setErrorMessage("url " + pathFeatures + " is not valid ");
			e.printStackTrace();
		}
		return fileZip;
	}
	
	/*
	 * cancellazione dei file
	 */
	private void deleteFile(String path) {
		LOGGER.debug("cancellazione del file " + path);
		File file = new File(path);
		
		if (file.isDirectory()) {
			if (file.list().length == 0) {
				file.delete();
			}
			else {
				String files[] = file.list();
				
				for (String temp : files) {
					File fileDelete = new File(file, temp);
					deleteFile(fileDelete.getPath());
				}
				
				if (file.list().length == 0) {
					file.delete();
				}
			}
			
		}
		else {
			file.delete();
		}
	}
	
	/*
	 * metodo che effettua l'unione
	 */
	private SimpleFeatureCollection featureUnion(SimpleFeatureCollection sfc1, SimpleFeatureCollection sfc2) {
		UnionFeatureCollectionSinergis union = new UnionFeatureCollectionSinergis();
		try {
			return union.execute(sfc1, sfc2);
		}
		catch (ClassNotFoundException e) {
			LOGGER.error("errore nell'effettuare l'unione ", e);
			wpsResponse.setErrorMessage("errore nell'effettuare l'unione " + e);
			e.printStackTrace();
			return null;
		}
		catch (Exception e) {
			LOGGER.error("errore nell'effettuare l'unione ", e);
			wpsResponse.setErrorMessage("errore nell'effettuare l'unione " + e);
			e.printStackTrace();
			return null;
		}
	}
	
	/*
	 * metodo che controlla i dati di input
	 */
	private boolean checkInput(String pathZip) {
		if (pathZip == null || pathZip.equals("")) {
			wpsResponse.setErrorMessage(WpsResponse.ERRORE_INPUT + " pathZip " + pathZip + " is not valid");
			return false;
		}
		return true;
	}
	
}
