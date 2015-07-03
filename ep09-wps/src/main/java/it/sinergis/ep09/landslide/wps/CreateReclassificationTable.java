package it.sinergis.ep09.landslide.wps;

import it.sinergis.ep09.utils.Constants;
import it.sinergis.ep09.utils.ProcessUtils;
import it.sinergis.ep09.utils.ProjectProperties;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.wps.WPSException;
import org.geoserver.wps.gs.GeoServerProcess;
import org.geoserver.wps.jts.SpringBeanProcessFactory;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.AttributeDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


@DescribeProcess(title = "createReclassificationTable", description = "Create reclassification table")
public class CreateReclassificationTable extends SpringBeanProcessFactory {
	
	private static final Logger LOGGER = Logger.getLogger(CreateReclassificationTable.class);
	
	private Catalog catalog;
	
	private String workspace;
	
	private String store;
	
	public CreateReclassificationTable(Catalog catalog) {
		super(Constants.EENV_TITLE, Constants.EENV_NAMESPACE, CreateReclassificationTable.class);

		this.catalog = catalog;
		this.workspace = ProjectProperties.loadByName(Constants.GEOSERVER_WORKSPACE);
		this.store = ProjectProperties.loadByName(Constants.GEOSERVER_DATASTORE);
	}
	
	@DescribeResult(name = "result", description = "output result")
	public String execute(
			@DescribeParameter(name = "reclassificationTableName", description = "Reclassification table name")
			String reclassificationTableName,
			@DescribeParameter(name = "workspace", description = "Target workspace (default is the system default) where there is the reclassification table", min = 0)
			String workspace,
			@DescribeParameter(name = "store", description = "Target store (default is the system default) where there is the reclassification table", min = 0)
			String store) throws Exception {
		
		checkInputParameter(workspace, store, reclassificationTableName);
		
		DataStoreInfo storeInfo = catalog.getDataStoreByName(workspace, store);
		if (storeInfo == null) {
			throw new WPSException("wrong workspace " + workspace + " and store " + store + " parameter");
		}
		
		SimpleFeatureStore featureSourceDataStore = null;
		DataStore dataStore = null;
		
		try {
			
			dataStore = DataStoreFinder.getDataStore(storeInfo.getConnectionParameters());
			
			// recupero le feature dal datastore
			featureSourceDataStore = (SimpleFeatureStore) dataStore.getFeatureSource(reclassificationTableName);
			
			if (featureSourceDataStore == null) {
				throw new WPSException("reclassification table " + reclassificationTableName + " does not exist");
			}
			
			SimpleFeatureCollection sfc = featureSourceDataStore.getFeatures();
			
			DocumentBuilder docBuilder = ProcessUtils.initDocumentBuilder();
			Document doc = docBuilder.newDocument();
			
			// leggo la tabella e creo xml per la risposta
			if (sfc == null) {
				throw new WPSException("reclassification table " + reclassificationTableName + " is empty");
			}
			
			createXmlTable(sfc, doc, reclassificationTableName);
			
			// scrive xml se si vuole scrivere anche su file indicare il path come secondo parametro
			String response = writeXml(doc, null);
			
			return response;
			
		}
		catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("errore nella creazione della tabella ", e);
			throw new WPSException("error creating reclassification table");
		}
	}
	
	private void checkInputParameter(String workspace, String store, String reclassificationTableName) {
		// se è diverso da null uso il ws definito in input
		if (workspace != null && !workspace.equals("")) {
			this.workspace = workspace;
		}
		LOGGER.debug("workspace " + this.workspace);
		
		if (store != null && !store.equals("")) {
			this.store = store;
		}
		LOGGER.debug("workspace " + this.store);
		
		if (reclassificationTableName == null || reclassificationTableName.equals("")) {
			throw new WPSException("insert reclassification table name");
		}
		LOGGER.debug("reclassificationTableName " + reclassificationTableName);
	}
	
	
	/*
	 * metodo che legge le varie colonne della tabella e crea xml
	 */
	private void createXmlTable(SimpleFeatureCollection sfc, Document doc, String reclassificationTableName) {
		// intestazione xml
		Element rootElement = doc.createElement(reclassificationTableName);
		doc.appendChild(rootElement);
		
		// scorro le features e popolo il xml
		SimpleFeatureIterator iterator = sfc.features();
		while (iterator.hasNext()) {
			SimpleFeature simpleFeature = iterator.next();
			
			List<AttributeDescriptor> attributeDescriptors = simpleFeature.getFeatureType().getAttributeDescriptors();
			
			Element member = doc.createElement("row");
			for (AttributeDescriptor attributeDescriptor : attributeDescriptors) {
				String nomeColonna = attributeDescriptor.getLocalName();
				if (nomeColonna != null && !nomeColonna.equals("")) {
					
					rootElement.appendChild(member);
					Element attributeElement = doc.createElement(nomeColonna.replace(" ", "_"));
					attributeElement.appendChild(doc.createTextNode((String) simpleFeature
							.getAttribute(attributeDescriptor.getName())));
					member.appendChild(attributeElement);
				}
			}
		}
		
	}
	
	/*
	 * metodo che scrive xml della tabella
	 */
	private String writeXml(Document doc, String xmlFilePath) {
		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			
			ByteArrayOutputStream s = new ByteArrayOutputStream();
			
			if (xmlFilePath != null) {
				if (!xmlFilePath.endsWith(".xml")) {
					xmlFilePath = xmlFilePath + ".xml";
				}
				// scrivo il file anche sul filesystem
				File file = new File(xmlFilePath);
				
				if (file.exists()) {
					file.createNewFile();
				}
				
				StreamResult result = new StreamResult(file);
				transformer.transform(new DOMSource(doc), result);
			}
			
			transformer.transform(new DOMSource(doc), new StreamResult(s));
			return new String(s.toByteArray());
		}
		catch (Exception e) {
			LOGGER.error("errore nella scrittura del xml in " + xmlFilePath, e);
			throw new WPSException("error writing response");
		}
	}
}
