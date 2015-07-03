package it.sinergis.utils;

import it.sinergis.element.DoNotificationElement;
import it.sinergis.element.Response;
import it.sinergis.element.SingleUserElement;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

public class WnsNotification {
	
	public final static String WNS_URL = "wns_url";
	public static final String WNS = "WNS";
	public static final String WNS_VERSION = "1.0.0";
	
	/**
	 * metodo che invoca il servizio rest di eu-wns per l'invio della notifica
	 * 
	 * @param userId
	 * @param status
	 * @param shortMessage
	 * @param wnsUrl
	 * @throws Exception
	 */
	public void sendNotification(String userId, String status, String shortMessage, String wnsUrl) throws Exception {
		
		StringWriter sw = null;
		try {
			sw = new StringWriter();
			
			JAXBContext context = JAXBContext.newInstance(DoNotificationElement.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			
			DoNotificationElement element = new DoNotificationElement();
			element.setUserId(userId);
			element.setServiceType(WNS);
			element.setServiceTypeVersion(WNS_VERSION);
			element.setStatus(status);
			element.setShortMessage(shortMessage);
			m.marshal(element, sw);
			
			String responseDoNotification = sendRequest(sw, "/doNotification", wnsUrl);
			JAXBElement<Response> response = unMarshalResponse(responseDoNotification);
		}
		catch (Exception e) {
			throw new Exception("error during sending messages " + e);
		}
		finally {
			if (sw != null) {
				try {
					sw.close();
				}
				catch (IOException e) {
					e.printStackTrace();
					throw new Exception("error during sending messages, error code: " + e);
				}
			}
		}
	}
	
	/**
	 * metodo che invoca il servizio rest di eu-wns per la registrazione dell'utente
	 * 
	 * @param email
	 * @param name
	 * @param wnsUrl
	 * @return
	 * @throws Exception
	 */
	public String registerSingleUser(String email, String name, String wnsUrl) throws Exception {
		
		StringWriter sw = null;
		try {
			sw = new StringWriter();
			
			JAXBContext context = JAXBContext.newInstance(SingleUserElement.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			
			SingleUserElement element = new SingleUserElement();
			element.setEmail(email);
			element.setName(name);
			m.marshal(element, sw);
			
			String responseRegister = sendRequest(sw, "/registerSingle", wnsUrl);
			JAXBElement<Response> response = unMarshalResponse(responseRegister);
			if (response != null && response.getValue() != null) {
				//restituisco l'id dell'utente
				return response.getValue().getResponseValue().toString();
			}
			return null;
		}
		catch (Exception e) {
			throw new Exception("error during sending messages " + e);
		}
		finally {
			if (sw != null) {
				try {
					sw.close();
				}
				catch (IOException e) {
					e.printStackTrace();
					throw new Exception("error during sending messages, error code: " + e);
				}
			}
		}
	}
	
	/**
	 * metodo che invoca il servizio rest di eu-wns per la de-registrazione dell'utente
	 * @param idUtente
	 * @param wnsUrl
	 * @return
	 * @throws Exception
	 */
	public String unRegister(String idUtente, String wnsUrl) throws Exception {
		
		StringWriter sw = null;
		try {
			sw = new StringWriter();
			sw.write("<ID>" + idUtente + "</ID>");
			
			String responseRegister = sendRequest(sw, "/unRegister", wnsUrl);
			JAXBElement<Response> response = unMarshalResponse(responseRegister);
			
			if (response != null && response.getValue().getHttpCode() != null
					&& !response.getValue().getHttpCode().equals(HttpStatus.SC_OK + "")) {
				throw new Exception("error unregister user");
			}
			return null;
		}
		catch (Exception e) {
			throw new Exception("error during sending messages " + e);
		}
		finally {
			if (sw != null) {
				try {
					sw.close();
				}
				catch (IOException e) {
					e.printStackTrace();
					throw new Exception("error during sending messages, error code: " + e);
				}
			}
		}
	}
	
	/**
	 * metodo che converte la risposta in un oggetto jaxb
	 * @param responseXML
	 * @return
	 * @throws JAXBException
	 */
	private JAXBElement<Response> unMarshalResponse(String responseXML) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(Response.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		StreamSource streamSource = new StreamSource(new StringReader(responseXML));
		JAXBElement<Response> response = unmarshaller.unmarshal(streamSource, Response.class);
		return response;
	}
	
	/**
	 * metodo che manda la richiesta in post
	 * @param sw
	 * @param operazione
	 * @param wnsUrl
	 * @return
	 * @throws Exception
	 */
	public String sendRequest(StringWriter sw, String operazione, String wnsUrl) throws Exception {
		
		try {
			PostMethod post = new PostMethod(wnsUrl + operazione);
			post.setRequestEntity(new StringRequestEntity(sw.toString(), "application/xml", "UTF8"));
			HttpClient httpclient = new HttpClient();
			int result = httpclient.executeMethod(post);
			
			if (result != HttpStatus.SC_OK) {
				throw new Exception("error during sending messages, error code: " + result);
			}
			else {
				return post.getResponseBodyAsString();
			}
		}
		catch (Exception e) {
			throw new Exception("error during sending messages " + e);
		}
		finally {
			if (sw != null) {
				try {
					sw.close();
				}
				catch (IOException e) {
					e.printStackTrace();
					throw new Exception("error during sending messages, error code: " + e);
				}
			}
		}
	}
}
