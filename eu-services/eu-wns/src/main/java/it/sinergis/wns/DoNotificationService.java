package it.sinergis.wns;

import it.sinergis.constant.WnsConstant;
import it.sinergis.element.DoNotificationElement;
import it.sinergis.element.Response;
import it.sinergis.util.ReadFromConfig;
import it.sinergis.wns.jaxb.DoNotificationType;
import it.sinergis.wns.jaxb.NotificationMessageType;
import it.sinergis.wns.jaxb.DoNotificationType.Message;
import it.sinergis.wns.jaxb.WNSMessageType.Payload;
import it.sinergis.wns.jaxb.WNSMessageType.ServiceDescription;

import java.io.IOException;
import java.io.StringWriter;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import net.opengis.sps.v_1_0_0.SPSMessage;
import net.opengis.sps.v_1_0_0.SPSMessage.StatusInformation;

import org.apache.commons.httpclient.HttpException;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

@Path("/doNotification")
public class DoNotificationService extends WnsService {

	private static final Logger LOGGER = Logger
			.getLogger(DoNotificationService.class);

	private DoNotificationElement doNotificationElement;
	
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public DoNotificationElement getDoNotificationElement() {
		DoNotificationElement oggetto = new DoNotificationElement();
		oggetto.setMaxTTLOfMessage(12);
		oggetto.setServiceType("123");
		oggetto.setServiceUrl("aaa");
		oggetto.setStatus("1234");
		oggetto.setUserId("1");
		return oggetto;
	}

	@POST
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response sendNotification(JAXBElement<DoNotificationElement> jaxbDoNotificationElement) throws JAXBException,
			ParserConfigurationException, XMLStreamException,
			FactoryConfigurationError, HttpException, IOException,
			DatatypeConfigurationException {
		
		doNotificationElement = jaxbDoNotificationElement.getValue();

		// init marshaller
		Marshaller jaxbMarshaller = initMarshaller(DoNotificationType.class);

		// compone xml
		JAXBElement<DoNotificationType> root = doNotificationService();

		StringWriter stringWriter = new StringWriter();
		XMLStreamWriter xmlStreamWriter = (XMLStreamWriter) XMLOutputFactory
				.newInstance().createXMLStreamWriter(stringWriter);
		jaxbMarshaller.marshal(root, xmlStreamWriter);

		LOGGER.info("xml inviato: " + stringWriter.toString());
		sendRequest(stringWriter.toString());
		
		Response response = new Response();
		response.setHttpCode("200");
		return response;
	}

	/*
	 * metodo che compone xml da inviare al servizio di notifica
	 */
	private JAXBElement<DoNotificationType> doNotificationService()
			throws JAXBException, ParserConfigurationException,
			DatatypeConfigurationException {

		DoNotificationType doNotification = new DoNotificationType();
		doNotification.setUserID(doNotificationElement.getUserId());
		doNotification.setMaxTTLOfMessage(getDuration());

		QName qName = new QName(ReadFromConfig.loadByName(WnsConstant.WNS_URL),
				"DoNotification");
		JAXBElement<DoNotificationType> rootDoNotification = new JAXBElement<DoNotificationType>(
				qName, DoNotificationType.class, doNotification);

		Message message = new Message();

		// <NotificationMessage>
		NotificationMessageType notificationMessageType = new NotificationMessageType();
		qName = new QName("", "NotificationMessage");
		JAXBElement<NotificationMessageType> rootNotificationMessageType = new JAXBElement<NotificationMessageType>(
				qName, NotificationMessageType.class, notificationMessageType);
		message.setAny(rootNotificationMessageType);

		// <ServiceDescription>
		ServiceDescription description = getServiceDescription();
		notificationMessageType.setServiceDescription(description);

		// <Payload>
		Payload py = getPayload();
		notificationMessageType.setPayload(py);

		doNotification.setService(WnsConstant.WNS);
		doNotification.setShortMessage(doNotificationElement.getShortMessage());
		doNotification.setVersion(WnsConstant.WNS_VERSION);
		doNotification.setMessage(message);

		return rootDoNotification;
	}

	/*
	 * TAG PAYLOAD
	 */
	private Payload getPayload() throws ParserConfigurationException, JAXBException {
		//SPSMessage
		SPSMessage spsMessage = new SPSMessage();
		spsMessage.setSPSCorrID(doNotificationElement.getSPSCorrID());
		
		StatusInformation statusInformation = new StatusInformation();
		statusInformation.setStatus(doNotificationElement.getStatus());
		spsMessage.setStatusInformation(statusInformation);
		
		QName qName = new QName(WnsConstant.SPS_URL, "SPSMessage");
		JAXBElement<SPSMessage> rootSPSMessage = new JAXBElement<SPSMessage>(
				qName, SPSMessage.class, spsMessage);

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document doc = dbf.newDocumentBuilder().newDocument();
		JAXBContext context = JAXBContext.newInstance(SPSMessage.class);
		context.createMarshaller().marshal(rootSPSMessage, doc);

		// Payload
		Payload py = new Payload();
		py.setAny(doc.getDocumentElement());
		return py;
	}

	/*
	 * TAG MaxTTLOfMessage
	 */
	private Duration getDuration() {
		Duration duration = null;
		try {
			DatatypeFactory df = DatatypeFactory.newInstance();
			duration = df.newDuration(doNotificationElement
					.getMaxTTLOfMessage());
		} catch (DatatypeConfigurationException e) {
			LOGGER.error("errore nel recupero della durata ", e);
			e.printStackTrace();
		}

		return duration;
	}

	/*
	 * TAG ServiceDescription
	 */
	private ServiceDescription getServiceDescription() {
		ServiceDescription description = new ServiceDescription();
		description.setServiceType(doNotificationElement.getServiceType());
		description.setServiceTypeVersion(doNotificationElement
				.getServiceTypeVersion());
		description.setServiceURL(doNotificationElement.getServiceUrl());
		return description;
	}
}
