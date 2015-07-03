package it.sinergis.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.ProjectionPolicy;
import org.geoserver.wps.WPSException;
import org.geoserver.wps.gs.ImportProcess;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class EnvPlusUtils
{
  private static final Logger LOGGER = Logger.getLogger(EnvPlusUtils.class);

  public static String importInPostgisFeatures(SimpleFeatureCollection features, Catalog catalog, String workspace, String store, long systemCurrentMillis) throws Exception
  {
    ImportProcess importProcess = new ImportProcess(catalog);
    CoordinateReferenceSystem coordinateReferenceSystem = features.getBounds().getCoordinateReferenceSystem();
    if (coordinateReferenceSystem == null) {
      try {
        coordinateReferenceSystem = CRS.decode("EPSG:" + ReadFromConfig.loadByName(Constants.DEFAULT_FORCED_EPSG));
      }
      catch (NoSuchAuthorityCodeException e) {
        LOGGER.error("errore nel set del sistema di riferimento EPSG:" + ReadFromConfig.loadByName(Constants.DEFAULT_FORCED_EPSG), e);
        e.printStackTrace();
      }
      catch (FactoryException e) {
        LOGGER.error("errore nel set del sistema di riferimento EPSG:" + ReadFromConfig.loadByName(Constants.DEFAULT_FORCED_EPSG), e);
        e.printStackTrace();
      }
    }
    String layerName = importProcess.execute(features, null, workspace, store, ((SimpleFeatureType)features.getSchema()).getTypeName() + systemCurrentMillis, coordinateReferenceSystem, ProjectionPolicy.FORCE_DECLARED, null);

    LayerInfo layerInfo = catalog.getLayerByName(layerName);
    catalog.remove(layerInfo);

    if (layerName.indexOf(":") != -1) {
      layerName = layerName.substring(layerName.indexOf(":") + 1);
    }
    return layerName;
  }

  public static List<String> unZipArchive(String archiveName, String pathZipFile)
  {
    byte[] buffer = new byte[1024];
    List<String> extractFiles = new ArrayList<String>();
    try
    {
      File folder = new File(pathZipFile);
      if (!folder.exists()) {
        folder.mkdir();
      }

      ZipInputStream zis = new ZipInputStream(new FileInputStream(archiveName));

      ZipEntry ze = zis.getNextEntry();

      while (ze != null)
      {
        String fileName = ze.getName();
        File newFile = new File(pathZipFile + File.separator + fileName);

        extractFiles.add(newFile.getAbsolutePath());

        new File(newFile.getParent()).mkdirs();

        FileOutputStream fos = new FileOutputStream(newFile);
        int len;
        while ((len = zis.read(buffer)) > 0) {
          fos.write(buffer, 0, len);
        }

        fos.close();
        ze = zis.getNextEntry();
      }

      zis.closeEntry();
      zis.close();

      LOGGER.debug("fine unzip file " + archiveName);
      return extractFiles;
    }
    catch (IOException ex) {
      LOGGER.error("errore nell'unzip dello zip file ", ex);
      ex.printStackTrace();
    }return null;
  }
  
  public static Boolean unzip(String zipFile,String outputFolder) throws Exception{
		Boolean ok = Boolean.FALSE;
		FileInputStream fis = null;
		try{
			LOGGER.info("Begin unzip "+ zipFile + " into "+outputFolder);
			fis = new FileInputStream(zipFile);
			ZipInputStream zis = new ZipInputStream(fis);
			ZipEntry ze = zis.getNextEntry();
			while(ze!=null){
				if(ze.isDirectory()){
					throw new WPSException("Il file  " +zipFile+ " contiene una directory zippata. Lo zip deve contenere solo files, non directories." );
				}
				String entryName = ze.getName();
				LOGGER.debug("Extracting " + entryName + " -> " + outputFolder + File.separator +  entryName + "...");
				File f = new File(outputFolder + File.separator +  entryName);
				//create all folder needed to store in correct relative path.
				if(!f.getParentFile().exists())
					f.getParentFile().mkdirs();
				FileOutputStream fos = new FileOutputStream(f);
				int len;
				byte buffer[] = new byte[1024];
	            while ((len = zis.read(buffer)) > 0) {
	            	fos.write(buffer, 0, len);
	            }
	            fos.close();   
	            LOGGER.debug(entryName+ " extracted ");
	            ze = zis.getNextEntry();
			}
			zis.closeEntry();
	    	zis.close();

	    	ok = Boolean.TRUE;
	    	
	    	LOGGER.info( zipFile + " unzipped successfully");
			
		}catch (Exception e) {
			LOGGER.error("errore nella decompressione dello zip contenente gli shapefiles ", e);
			throw e;
		}finally{
			if(fis!=null){
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return ok;
	}
  
   
   public static void addFilesToExistingZip(File zipFile,
	         List<File> files) throws IOException {

	    byte[] buf = new byte[1024];

	    if(!zipFile.exists()){
	    	zipFile.createNewFile();
	    }
		String zipPath = zipFile.getAbsolutePath();
		String path = zipPath.substring(0,zipPath.lastIndexOf(File.separator));
		String namezip = zipPath.substring(zipPath.lastIndexOf(File.separator)+1);
		File tempFile = new File(path+ File.separator+"temp_"+namezip);
		FileUtils.copyFile(zipFile, tempFile);
	    ZipInputStream zin = new ZipInputStream(new FileInputStream(tempFile));
	    ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));

	    ZipEntry entry = zin.getNextEntry();
	    while (entry != null) {
	        String name = entry.getName();
	        boolean notInFiles = true;
	        for (File f : files) {
	            if (f.getName().equals(name)) {
	                notInFiles = false;
	                break;
	            }
	        }
	        if (notInFiles) {
	            // Add ZIP entry to output stream.
	            out.putNextEntry(new ZipEntry(name));
	            // Transfer bytes from the ZIP file to the output file
	            int len;
	            while ((len = zin.read(buf)) > 0) {
	                out.write(buf, 0, len);
	            }
	        }
	        entry = zin.getNextEntry();
	    }
	    // Close the streams        
	    zin.close();
	    // Compress the files
	    for (int i = 0; i < files.size(); i++) {
	        InputStream in = new FileInputStream(files.get(i));
	        // Add ZIP entry to output stream.
	        out.putNextEntry(new ZipEntry(files.get(i).getName()));
	        // Transfer bytes from the file to the ZIP file
	        int len;
	        while ((len = in.read(buf)) > 0) {
	            out.write(buf, 0, len);
	        }
	        // Complete the entry
	        out.closeEntry();
	        in.close();
	    }
	    // Complete the ZIP file
	    out.close();
	    tempFile.delete();
	}
	
   	public static String registerWNS(String emailAddresses) throws HttpException, IOException{
   		StringBuilder ids = new StringBuilder(); 
   		if(emailAddresses!=null && !emailAddresses.trim().equals("")){
   			if(emailAddresses.indexOf("#")!=-1){
   				emailAddresses = StringUtils.replace(emailAddresses, "#","@");
   			}
			if(emailAddresses.indexOf(",")!=-1){
				String[] addresses = emailAddresses.split(",");
				for (String address : addresses) {
					String id = registerSingle(address);
					if(id.length()>0){
						ids.append(id).append(",");
					}
				}
			}else{
				String id = registerSingle(emailAddresses);
				if(id.length()>0){
					ids.append(id).append(",");
				}
			}
			ids = ids.delete(ids.length()-1, ids.length());
		}
   		String usersid = ids.toString();
   		String idmulti = usersid;
//   		if(usersid.indexOf(",")!=-1){
//   			idmulti = registerMulti(usersid);
//   		}
   		return idmulti;
   	}
   
   	public static Boolean doNotificationWNS(String usersid) throws HttpException, IOException{
   		if(usersid!=null && !usersid.equals("")){
   			String[] userid = usersid.split(",");
   			boolean ok = true;
   			for (String id : userid) {
   				String xmldonotifiation = ReadFromConfig.loadByName(Constants.DO_NOTIFICATION);
   		   		xmldonotifiation = StringUtils.replace(xmldonotifiation, Constants.PH_USERID,id);
   		   		String response = sendRequest(xmldonotifiation);
   		   		ok = ok && (response.indexOf(Constants.SUCCESS_52NWNS)!=-1);
			}
	   		return ok;
   		}
   		return Boolean.FALSE;
   	}
   	
   	private static String registerSingle(String email) throws HttpException, IOException{
   		String id = "";
   		String xmlregister = ReadFromConfig.loadByName(Constants.REGISTER_SINGLE);
   		xmlregister = StringUtils.replace(xmlregister, Constants.PH_EMAIL, email);
   		String response = sendRequest(xmlregister);
	   	// <UserID>7</UserID>
	   	if(response.indexOf(Constants.USERID_START_TAG_52NWNS)!=-1){
	   		int start = response.indexOf(Constants.USERID_START_TAG_52NWNS)+8;
	   		int end = response.indexOf(Constants.USERID_END_TAG_52NWNS);
	   		id = response.substring(start,end);
	   	}
	   	return id;
   	}
   	
   	private static String registerMulti(String usersid) throws HttpException, IOException{
   		String id = "";
   		String xmluserid = ReadFromConfig.loadByName(Constants.USERSID);
   		String[] useridvalue = usersid.split(",");
   		StringBuilder xmlusersid = new StringBuilder(); 
   		for (String userid : useridvalue) {
   			xmlusersid.append( StringUtils.replace(xmluserid, Constants.PH_USERID,userid));
		}
   		String xmlregister = ReadFromConfig.loadByName(Constants.REGISTER_MULTI);
	   	xmlregister = StringUtils.replace(xmlregister, Constants.PH_USERSID, xmlusersid.toString());
	   	String response = sendRequest(xmlregister);
	   	// <UserID>7</UserID>
	   	if(response.indexOf(Constants.USERID_START_TAG_52NWNS)!=-1){
	   		int start = response.indexOf(Constants.USERID_START_TAG_52NWNS)+8;
	   		int end = response.indexOf(Constants.USERID_END_TAG_52NWNS);
	   		id = response.substring(start,end);
	   	}
	   	return id;
   	}
   	
   	public static Boolean unregisterMulti(String usersid) throws HttpException, IOException{
   		String[] userid = usersid.split(",");
   		boolean ok = true;
   		for (String id : userid) {
   			ok  = ok && unregister(id);
		}
		return ok;
   	}
   	
   	private static Boolean unregister(String userid) throws HttpException, IOException{
		String xmlunregister = ReadFromConfig.loadByName(Constants.UNREGISTER);
		xmlunregister = StringUtils.replace(xmlunregister, Constants.PH_USERID,userid);
		String response = sendRequest(xmlunregister);
		return (response.indexOf(Constants.SUCCESS_52NWNS)!=-1);
   	}
	
	private static String sendRequest(String xml) throws HttpException, IOException{
		HttpClient client = new HttpClient();
		String url = ReadFromConfig.loadByName(Constants.URL_52NWNS);
		PostMethod method = new PostMethod(url);
		NameValuePair[] data = {
				new NameValuePair("request", xml)
	    };
		method.setRequestBody(data);
		client.executeMethod(method);
		InputStream is = method.getResponseBodyAsStream();
		
		StringWriter writer = new StringWriter();
		IOUtils.copy(is, writer,Constants.ENCODING_UTF8);
		String response = writer.toString();
		
		if(LOGGER.isDebugEnabled()){
			String output = ReadFromConfig.loadByName(Constants.TEMP_DIR_52NWNS)+UUID.randomUUID().toString()+".xml";
			OutputStream out = new FileOutputStream(output);
			// Transfer bytes from in to out
			byte[] buf = new byte[1024];
			int len;
			while ((len = is.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			out.close();
		}
		is.close();
				
		return response;
	}
	
	public static void main(String[] args) {
		try{
			HttpClient client = new HttpClient();
			String url = "http://localhost:8080/52nWNS/wns";
			PostMethod method = new PostMethod(url);

			String xml = "<?xml version='1.0' encoding='UTF-8'?><DoNotification xmlns='http://www.opengis.net/wns' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:schemaLocation='http://www.opengis.net/wns ../wns.xsd' service='WNS' version='1.0.0'><UserID>3</UserID><MaxTTLOfMessage>P7D</MaxTTLOfMessage><ShortMessage>Task 1161690976970_684004688690043 is commencing.</ShortMessage><Message><NotificationMessage><ServiceDescription><ServiceType>SPS</ServiceType><ServiceTypeVersion>0.0.30</ServiceTypeVersion><ServiceURL>http://mars.uni-muenster.de:8080/52nSPSv1/SPS</ServiceURL></ServiceDescription><Payload><sps:SPSMessage SPSCorrID='1161690976970_684004688690043' xmlns:sps='http://www.opengis.net/sps'><sps:StatusInformation><sps:status>New data available</sps:status></sps:StatusInformation></sps:SPSMessage></Payload></NotificationMessage></Message></DoNotification>";
			NameValuePair[] data = {
	        		new NameValuePair("request", xml)
	        };
	        method.setRequestBody(data);
	        client.executeMethod(method);
	        // execute method and handle any error responses.
	   
	        InputStream is = method.getResponseBodyAsStream();
	        
			String output = ReadFromConfig.loadByName(Constants.TEMP_DIR_52NWNS)+UUID.randomUUID().toString()+".xml";
			OutputStream out = new FileOutputStream(output);
			// Transfer bytes from in to out
			byte[] buf = new byte[1024];
			int len;
			while ((len = is.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			is.close();
			out.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}