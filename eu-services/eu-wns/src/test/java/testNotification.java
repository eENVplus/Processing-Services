import it.sinergis.element.DoNotificationElement;
import it.sinergis.wns.DoNotificationService;

import java.io.IOException;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

public class testNotification {

	public static void main(String[] args) throws IOException, JAXBException,
			ParserConfigurationException, XMLStreamException,
			FactoryConfigurationError {

		DoNotificationElement element = new DoNotificationElement();
		element.setUserId("1");
		element.setShortMessage("caiaoo");
		element.setMaxTTLOfMessage(System.currentTimeMillis());

		element.setServiceType("aaa");
		element.setServiceUrl("2342");
		element.setServiceTypeVersion("123");
		element.setStatus("new data");

		DoNotificationService service = new DoNotificationService();
		try {
			service.sendNotification(null);
		} catch (DatatypeConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
