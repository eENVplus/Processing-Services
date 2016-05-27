package it.sinergis.wps;

import it.sinergis.utils.Constants;
import it.sinergis.utils.ConvertOperations;
import it.sinergis.utils.ProjectPropertiesUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;

import javax.xml.transform.TransformerException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.geoserver.catalog.Catalog;
import org.geoserver.wps.jts.SpringBeanProcessFactory;
import org.geotools.GML;
import org.geotools.GML.Version;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;
import org.opengis.util.ProgressListener;

@DescribeProcess(title = "gmlInspireConverter", description = "Service to convert gml into a gml inspire compliant")
public class GmlInspireConverter extends SpringBeanProcessFactory {
	
	private Catalog catalog;
	private Properties properties = null;
	private Properties messages = null;
	
	private static final Logger LOGGER = Logger.getLogger(GmlInspireConverter.class);
	private static final String NAMESPACE_SINERGIS = "http://inspire.ec.europa.eu/schemas/lcv/3.0";
	private static final String NAMESPACE_OPENGIS = "http://inspire.ec.europa.eu/schemas/base";
	
	public GmlInspireConverter(Catalog catalog) {
		super(Constants.EENV_TITLE, Constants.EENV_NAMESPACE, GmlInspireConverter.class);
		this.catalog = catalog;
	}
	
	@DescribeResult(name = "result", description = "output result")
	public String execute(
			@DescribeParameter(name = "simpleFeatureCollection", description = "features to convert") SimpleFeatureCollection simpleFeatureCollection,
			@DescribeParameter(name = "inspireTheme", description = "inspire theme") String inspireTheme,
			@DescribeParameter(name = "namespace value", description = "namespace value", min = 0) String valueNamespace,
			@DescribeParameter(name = "url codelist", description = "url codelist", min = 0) String codelistUrl,
			@DescribeParameter(name = "document name", description = "document name", min = 0) String documentName,
			@DescribeParameter(name = "document link", description = "document link", min = 0) String documentLink,
			ProgressListener progressListener) {
		
		try {
			String localName = simpleFeatureCollection.getSchema().getName().getLocalPart();
			InputStream xsltFile = null;
			ConvertOperations converter = new ConvertOperations();
			
			String gmlResult = featureCollectionToGML3(simpleFeatureCollection);
			if (inspireTheme.equalsIgnoreCase("LC")) {
				if (!localName.startsWith("wps_ep10_intersect"))
					return ProjectPropertiesUtils.loadMessageByName("error_input_layer");
				xsltFile = GmlInspireConverter.class.getClassLoader().getResourceAsStream(
						ProjectPropertiesUtils.loadByName(Constants.XSLT_FILE_EP10));
			}
			else if (inspireTheme.equalsIgnoreCase("HZ")) {
				if (!localName.startsWith("landslide_"))
					return ProjectPropertiesUtils.loadMessageByName("error_input_layer");
				xsltFile = GmlInspireConverter.class.getClassLoader().getResourceAsStream(
						ProjectPropertiesUtils.loadByName(Constants.XSLT_FILE_EP09));
			}
			else {
				return ProjectPropertiesUtils.loadMessageByName("error_inspire_theme");
			}
			
			StringWriter sw = new StringWriter();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(IOUtils.toInputStream(gmlResult, "UTF-8")));
			String output;
			while ((output = br.readLine()) != null) {
				sw.write(output);
			}
			
			StringWriter stringWriter = new StringWriter();
			StringReader stringReader = new StringReader(sw.toString());
			
			javax.xml.transform.Source xmlSource = new javax.xml.transform.stream.StreamSource(stringReader);
			javax.xml.transform.Source xsltSource = new javax.xml.transform.stream.StreamSource(xsltFile);
			javax.xml.transform.Result result = new javax.xml.transform.stream.StreamResult(stringWriter);
			// create an instance of TransformerFactory
			javax.xml.transform.TransformerFactory transFact = javax.xml.transform.TransformerFactory.newInstance();
			javax.xml.transform.Transformer trans = transFact.newTransformer(xsltSource);
			trans.transform(xmlSource, result);
			//print gml inspire compliant
			
			String retval = "";
			if (inspireTheme.equalsIgnoreCase("LC"))
				retval = converter.insertValueForEp10(stringWriter.toString(), localName, valueNamespace, codelistUrl,
						documentName, documentLink);
			else if (inspireTheme.equalsIgnoreCase("HZ"))
				retval = converter.insertValueForEp09(stringWriter.toString(), localName, valueNamespace, codelistUrl,
						documentName, documentLink);
			
			return retval;
		}
		catch (IOException ioe) {
			LOGGER.error(ioe.getMessage());
			return ProjectPropertiesUtils.loadMessageByName("error_gml_input");
		}
		catch (TransformerException te) {
			LOGGER.error(te.getMessage());
			return ProjectPropertiesUtils.loadMessageByName("error_xslt_transformation");
		}
	}
	
	private String featureCollectionToGML3(SimpleFeatureCollection simpleFeatureCollection) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		File file = new File(ProjectPropertiesUtils.loadByName(Constants.PATH_GML) + "wps_base.gml");
		FileOutputStream file_out = new FileOutputStream(file);
		GML encode = new GML(Version.WFS1_1);
		encode.setNamespace("sinergis", simpleFeatureCollection.getSchema().getName().getNamespaceURI());
		encode.encode(file_out, simpleFeatureCollection);
		encode.encode(baos, simpleFeatureCollection);
		
		return baos.toString();
	}
}
