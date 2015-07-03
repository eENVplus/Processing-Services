package it.sinergis.wns;

import it.sinergis.constant.WnsConstant;
import it.sinergis.element.Response;
import it.sinergis.util.ReadFromConfig;
import it.sinergis.wns.jaxb.UnregisterResponseType;
import it.sinergis.wns.jaxb.UnregisterType;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.log4j.Logger;

@Path("/unRegister")
public class UnRegister extends WnsService {
	
	private static final Logger LOGGER = Logger.getLogger(UnRegister.class);
	
	@POST
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response registerSingleUserService(JAXBElement<String> jaxbIdUtente) throws JAXBException,
			ParserConfigurationException, XMLStreamException, FactoryConfigurationError, HttpException, IOException,
			DatatypeConfigurationException {
		
		String idUtente = jaxbIdUtente.getValue();
		
		// init marshaller
		Marshaller jaxbMarshaller = initMarshaller(UnregisterType.class);
		
		// compone xml
		JAXBElement<UnregisterType> root = unRegisterRequest(idUtente);
		
		StringWriter stringWriter = new StringWriter();
		XMLStreamWriter xmlStreamWriter = (XMLStreamWriter) XMLOutputFactory.newInstance().createXMLStreamWriter(
				stringWriter);
		jaxbMarshaller.marshal(root, xmlStreamWriter);
		
		LOGGER.info("xml inviato: " + stringWriter.toString());
		String responseWns = sendRequest(stringWriter.toString());
		LOGGER.info("xml risposta: " + responseWns);
		
		Response response = new Response();
		
		if (responseWns != null && !responseWns.equals("")) {
			JAXBContext jaxbContext = JAXBContext.newInstance(UnregisterResponseType.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			StreamSource streamSource = new StreamSource(new StringReader(responseWns));
			JAXBElement<UnregisterResponseType> unRegisterResponse = unmarshaller.unmarshal(streamSource, UnregisterResponseType.class);
			if (unRegisterResponse != null && unRegisterResponse.getValue() != null && unRegisterResponse.getValue().getStatus() != null && unRegisterResponse.getValue().getStatus().equals(WnsConstant.SUCCESS))
			response.setHttpCode(HttpStatus.SC_OK+"");
			return response;
		}
		
		response.setHttpCode(HttpStatus.SC_SERVICE_UNAVAILABLE+"");
		return response;
	}
	
	/*
	 * metodo che compone xml da inviare al servizio per la registrazione dell'utente
	 */
	private JAXBElement<UnregisterType> unRegisterRequest(String idUtente) throws JAXBException, ParserConfigurationException,
			DatatypeConfigurationException {
		
		UnregisterType registerType = new UnregisterType();
		registerType.setID(idUtente);
		registerType.setService(WnsConstant.WNS);
		registerType.setVersion(WnsConstant.WNS_VERSION);
		
		QName qName = new QName(ReadFromConfig.loadByName(WnsConstant.WNS_URL), "Unregister","");
		JAXBElement<UnregisterType> rootSingleUserType = new JAXBElement<UnregisterType>(qName, UnregisterType.class,
				registerType);
		
		return rootSingleUserType;
	}
	
}
