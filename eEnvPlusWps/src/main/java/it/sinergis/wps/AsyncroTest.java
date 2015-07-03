package it.sinergis.wps;

import it.sinergis.utils.Constants;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

public class AsyncroTest
{
	
	private final static Logger LOGGER = Logger.getLogger(AsyncroTest.class);
	
	public static void main1(String[] args){
		try {
			HttpClient client = new HttpClient();
			String address ="http://localhost:8080/geoserver/wps?service=WPS&version=1.0&request=execute&identifier=gs:TopologicalValidationShp&storeExecuteResponse=true&status=true&DataInputs=pathLowerLevelShpZipped=D:/Sviluppo/Progetti/eenvplus/dati/TopologicalValidation/shapefiles/province_errore.zip;pathUpperLevelShpZipped=D:/Sviluppo/Progetti/eenvplus/dati/TopologicalValidation/shapefiles/comuni_errore.zip;emailAddresses=tullia.furini#sinergis.it";
			GetMethod method = new GetMethod(address);
			client.executeMethod(method);
			InputStream is = method.getResponseBodyAsStream();
			String output = "D:\\Sviluppo\\Progetti\\eenvplus\\download\\shp_env\\output.xml";
			OutputStream out = new FileOutputStream(output);
			// Transfer bytes from in to out
			byte[] buf = new byte[1024];
			int len;
			while ((len = is.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			is.close();
			out.close();
		} catch (Exception e) {
			LOGGER.error(e);
			e.printStackTrace();
		}
	}
	
	public static void main2(String[] args){
		try {
			HttpClient client = new HttpClient();
			String address ="http://localhost:8080/geoserver/wps";
			PostMethod method = new PostMethod(address);
			
			NameValuePair[] data = {
					new NameValuePair("service", "WPS"),
					new NameValuePair("version", "1.0"),
					new NameValuePair("request", "execute"),
					new NameValuePair("identifier", "gs:TopologicalValidationShp"),
					new NameValuePair("storeExecuteResponse", "true"),
					new NameValuePair("status", "true"),
					new NameValuePair("DataInputs", "pathLowerLevelShpZipped=C:/Sviluppo/Progetti/eenvplus/download/shp_env/tpval/Province Trentino_ok.zip;pathUpperLevelShpZipped=C:/Sviluppo/Progetti/eenvplus/download/shp_env/tpval/Comuni Trentino_ok.zip;emailAddresses=alessandro.cuel#sinergis.it")
					//new NameValuePair("DataInputs", "pathLowerLevelShpZipped=D:/Sviluppo/Progetti/eenvplus/dati/TopologicalValidation/shapefiles/PIEMONTE_ZONE_prj.zip;pathUpperLevelShpZipped=D:/Sviluppo/Progetti/eenvplus/dati/TopologicalValidation/shapefiles/PIEMONTE_COMUNI_ZONE_prj.zip")
					
		    };
			method.setRequestBody(data);
			client.executeMethod(method);
			InputStream is = method.getResponseBodyAsStream();
			
			StringWriter writer = new StringWriter();
			IOUtils.copy(is, writer,Constants.ENCODING_UTF8);
			String response = writer.toString();
			System.out.println(response);
		} catch (Exception e) {
			LOGGER.error(e);
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args){
		try {
			HttpClient client = new HttpClient();
			String address ="http://localhost:8080/geoserver/wps";
			PostMethod method = new PostMethod(address);
			
			NameValuePair[] data = {
					new NameValuePair("service", "WPS"),
					new NameValuePair("version", "1.0"),
					new NameValuePair("request", "execute"),
					new NameValuePair("identifier", "gs:UrbanRuralGrowthIdentification"),
					new NameValuePair("storeExecuteResponse", "true"),
					new NameValuePair("status", "true"),
					new NameValuePair("DataInputs", "year1=1990;year2=2007;shpyear1=C:/Sviluppo/Progetti/eenvplus/Dati/164-Urban and Rural Growth/sviluppo/COS1990.zip;shpyear2=C:/Sviluppo/Progetti/eenvplus/Dati/164-Urban and Rural Growth/sviluppo/COS2007.zip;clcfield1=CLC;clcfield2=CLC;aggregationLevel=1;emailAddresses=alessandro.cuel#sinergis.it")
					//new NameValuePair("DataInputs", "pathLowerLevelShpZipped=D:/Sviluppo/Progetti/eenvplus/dati/TopologicalValidation/shapefiles/PIEMONTE_ZONE_prj.zip;pathUpperLevelShpZipped=D:/Sviluppo/Progetti/eenvplus/dati/TopologicalValidation/shapefiles/PIEMONTE_COMUNI_ZONE_prj.zip")
				
		    };
			method.setRequestBody(data);
			client.executeMethod(method);
			InputStream is = method.getResponseBodyAsStream();
			
			StringWriter writer = new StringWriter();
			IOUtils.copy(is, writer,Constants.ENCODING_UTF8);
			String response = writer.toString();
			System.out.println(response);
			
			response.indexOf("statusLocation=");
			System.out.println("http://localhost:8080/geoserver/ows?service=WPS&version=1.0.0&request=GetExecutionStatus&"+response.substring(response.indexOf("executionId="), response.indexOf("\" version=\"1.0.0\"")));
		} catch (Exception e) {
			LOGGER.error(e);
			e.printStackTrace();
		}
	}
}
