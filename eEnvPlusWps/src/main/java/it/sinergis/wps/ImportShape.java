package it.sinergis.wps;

import it.sinergis.object.WpsResponse;
import it.sinergis.utils.Constants;
import it.sinergis.utils.EnvPlusUtils;
import it.sinergis.utils.ReadFromConfig;
import it.sinergis.utils.ShapeUtils;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.geoserver.catalog.Catalog;
import org.geoserver.wps.gs.GeoServerProcess;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;
import org.opengis.util.ProgressListener;

@DescribeProcess(title="importShape", description="Import di shapefiles")
public class ImportShape
  implements GeoServerProcess
{
  private static final Logger LOGGER = Logger.getLogger(ImportShape.class);
  private Catalog catalog;
  private WpsResponse wpsResponse = null;
 
   public ImportShape(Catalog catalog) {
     this.catalog = catalog;
   }

  @DescribeResult(name="result", description="output result", type=String.class)
	   public Map<String, Object> execute(@DescribeParameter(name="pathZip", description="url or path where is the zip with shape") String pathZip, ProgressListener progressListener)
	     throws Exception
	   {
	     Map<String, Object> results = new HashMap<String, Object>();
	 
	     if (!checkInput(pathZip)) {
	       results.put("aaaaaa", "aaaaadff");
	     }
	 
	     progressListener.started();
	 
	     URL url = null;
	     File fileZip = null;
	     if ((pathZip.startsWith("http")) || (pathZip.startsWith("https"))) {
	       url = new URL(pathZip);
	       fileZip = downloadFilesFromUrl(pathZip, url);
	     }
	     else {
	       fileZip = new File(pathZip);
	     }
	 
	     long time = System.currentTimeMillis();
	 
	     String pathUnZippedFile = fileZip.getPath().substring(0, fileZip.getPath().indexOf(".zip")) + "_" + time;
	     try {
	       if (fileZip.getName().endsWith(".zip")) {
	         List<String> files = EnvPlusUtils.unZipArchive(fileZip.getPath(), pathUnZippedFile);
	         if ((files == null) || (files.size() == 0)) {
	           this.wpsResponse.setErrorMessage("unzip archive " + fileZip.getPath() + " fails");
	           return null;
	         }
	         for (String file : files)
	           if (file.endsWith(".shp")) {
	             SimpleFeatureCollection sfc = ShapeUtils.readShp(file);
	             EnvPlusUtils.importInPostgisFeatures(sfc, this.catalog, ReadFromConfig.loadByName(Constants.GEOSERVER_WORKSPACE), ReadFromConfig.loadByName(Constants.GEOSERVER_DATASTORE), time);
	           }
	       }
	     }
	     catch (Exception e)
	     {
	       this.wpsResponse.setErrorMessage(WpsResponse.ERRORE_GENERICO + " errore nel processamento del wps FeatureUnion ");
	 
	       LOGGER.error("errore nel processamento del wps FeatureUnion ", e);
	     }
	     finally {
	       if (url != null) {
	         deleteFile(fileZip.getPath());
	       }
	       deleteFile(pathUnZippedFile);
	     }
	 
	     results.put("time", String.valueOf(time));
	     return results;
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
	       this.wpsResponse.setErrorMessage("url " + pathFeatures + " is not valid ");
	       e.printStackTrace();
	     }
	     return fileZip;
	   }
	 
	   private void deleteFile(String path)
	   {
	     LOGGER.debug("cancellazione del file " + path);
	     File file = new File(path);
	 
	     if (file.isDirectory()) {
	       if (file.list().length == 0) {
	         file.delete();
	       }
	       else {
	         String[] files = file.list();
	 
	         for (String temp : files) {
	           File fileDelete = new File(file, temp);
	           deleteFile(fileDelete.getPath());
	         }
	 
	         if (file.list().length == 0) {
	           file.delete();
	         }
	       }
	     }
	     else
	     {
	       file.delete();
	     }
	   }
	 
//	   private SimpleFeatureCollection featureUnion(SimpleFeatureCollection sfc1, SimpleFeatureCollection sfc2)
//	   {
//	     UnionFeatureCollectionSinergis union = new UnionFeatureCollectionSinergis();
//	     try {
//	       return union.execute(sfc1, sfc2);
//	     }
//	     catch (ClassNotFoundException e) {
//	       LOGGER.error("errore nell'effettuare l'unione ", e);
//	       this.wpsResponse.setErrorMessage("errore nell'effettuare l'unione " + e);
//	       e.printStackTrace();
//	       return null;
//	     }
//	     catch (Exception e) {
//	       LOGGER.error("errore nell'effettuare l'unione ", e);
//	       this.wpsResponse.setErrorMessage("errore nell'effettuare l'unione " + e);
//	       e.printStackTrace();
//	     }return null;
//	   }
	 
	   private boolean checkInput(String pathZip)
	   {
	     if ((pathZip == null) || (pathZip.equals(""))) {
	       this.wpsResponse.setErrorMessage(WpsResponse.ERRORE_INPUT + " pathZip " + pathZip + " is not valid");
	       return false;
	     }
	     return true;
	   }
	 }

