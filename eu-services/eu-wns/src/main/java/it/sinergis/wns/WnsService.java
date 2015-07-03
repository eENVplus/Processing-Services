package it.sinergis.wns;

import it.sinergis.constant.WnsConstant;
import it.sinergis.util.ReadFromConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

public class WnsService {
	
	private static final Logger LOGGER = Logger.getLogger(WnsService.class);
	

	protected static Marshaller initMarshaller(Class<?> classe) throws JAXBException {

		JAXBContext jaxbContext = JAXBContext.newInstance(classe);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

		return jaxbMarshaller;

	}

	
	protected static String sendRequest(String xml) throws HttpException,
			IOException {
		HttpClient client = new HttpClient();
		String url = ReadFromConfig.loadByName(WnsConstant.URL_WNS_SERVICE);
		PostMethod method = new PostMethod(url);
		NameValuePair[] data = { new NameValuePair("request", xml) };
		method.setRequestBody(data);
		client.executeMethod(method);
		InputStream is = method.getResponseBodyAsStream();

		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		String output;
		LOGGER.info("output del server ");
		String response = "";
		while ((output = br.readLine()) != null) {
			LOGGER.info(output);
			response += output;
		}

		is.close();

		return response;
	}

}
