package it.sinergis.wns;

import it.sinergis.constant.WnsConstant;
import it.sinergis.element.Response;
import it.sinergis.element.SingleUserElement;
import it.sinergis.util.ReadFromConfig;
import it.sinergis.wns.jaxb.CommunicationProtocolType;
import it.sinergis.wns.jaxb.RegisterResponseType;
import it.sinergis.wns.jaxb.RegisterSingleUserType;
import it.sinergis.wns.jaxb.RegisterType;

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

@Path("/registerSingle")
public class RegisterSingleService extends WnsService {
	
	private static final Logger LOGGER = Logger.getLogger(RegisterSingleService.class);
	
	private SingleUserElement singleUserElement;
	
	@POST
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response registerSingleUserService(JAXBElement<SingleUserElement> jaxbSingleUserElement) throws JAXBException,
			ParserConfigurationException, XMLStreamException, FactoryConfigurationError, HttpException, IOException,
			DatatypeConfigurationException {
		
		singleUserElement = jaxbSingleUserElement.getValue();
		
		// init marshaller
		Marshaller jaxbMarshaller = initMarshaller(RegisterType.class);
		
		// compone xml
		JAXBElement<RegisterType> root = registerSingleUserRequest();
		
		StringWriter stringWriter = new StringWriter();
		XMLStreamWriter xmlStreamWriter = (XMLStreamWriter) XMLOutputFactory.newInstance().createXMLStreamWriter(
				stringWriter);
		jaxbMarshaller.marshal(root, xmlStreamWriter);
		
		LOGGER.info("xml inviato: " + stringWriter.toString());
		String responseWns = sendRequest(stringWriter.toString());
		LOGGER.info("xml risposta: " + responseWns);
		
		Response response = new Response();
		
		if (responseWns != null && !responseWns.equals("")) {
			JAXBContext jaxbContext = JAXBContext.newInstance(RegisterResponseType.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			StreamSource streamSource = new StreamSource(new StringReader(responseWns));
			JAXBElement<RegisterResponseType> registerResponse = unmarshaller.unmarshal(streamSource, RegisterResponseType.class);
			response.setHttpCode(HttpStatus.SC_OK+"");
			if (registerResponse!= null && registerResponse.getValue() != null) {
				response.setResponseValue(registerResponse.getValue().getUserID());
			}
			return response;
		}
		
		response.setHttpCode(HttpStatus.SC_SERVICE_UNAVAILABLE+"");
		return response;
	}
	
	/*
	 * metodo che compone xml da inviare al servizio per la registrazione dell'utente
	 */
	private JAXBElement<RegisterType> registerSingleUserRequest() throws JAXBException, ParserConfigurationException,
			DatatypeConfigurationException {
		
		RegisterSingleUserType registerSingleUser = new RegisterSingleUserType();
		registerSingleUser.setName(singleUserElement.getName());
		
		CommunicationProtocolType communicationProtocol = new CommunicationProtocolType();
		communicationProtocol.getEmail().add(singleUserElement.getEmail());
		registerSingleUser.setCommunicationProtocol(communicationProtocol);
		
		RegisterType registerType = new RegisterType();
		registerType.setSingleUser(registerSingleUser);
		registerType.setService(WnsConstant.WNS);
		registerType.setVersion(WnsConstant.WNS_VERSION);
		
		QName qName = new QName(ReadFromConfig.loadByName(WnsConstant.WNS_URL), "Register","");
		JAXBElement<RegisterType> rootSingleUserType = new JAXBElement<RegisterType>(qName, RegisterType.class,
				registerType);
		
		return rootSingleUserType;
	}
	
}
