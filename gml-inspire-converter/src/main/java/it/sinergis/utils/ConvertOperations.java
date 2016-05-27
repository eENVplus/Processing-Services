package it.sinergis.utils;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class ConvertOperations {
	
	private static final Logger LOGGER = Logger.getLogger(ConvertOperations.class);
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:MM:ss");
	
	public String insertValueForEp10(String result, String localName, String valueNamespace, String codelistUrl,
			String documentName, String documentLink) {
		try {
			//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:MM:ss");
			String idPolygon = "polygon_";
			String idMultiSurface = "multisurface_";
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(false);
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(result));
			Document doc = db.parse(is);
			
			// Get the landCoverDataset element by tag name directly
			Node landCoverDataset = doc.getElementsByTagName("lcv:LandCoverDataset").item(0);
			// update id attribute
			NamedNodeMap attr = landCoverDataset.getAttributes();
			Node nodeAttr = attr.getNamedItem("gml:id");
			nodeAttr.setTextContent(localName);
			
			Node rootElement = doc.getElementsByTagName("gml:FeatureCollection").item(0);
			// update id attribute
			NamedNodeMap attr0 = rootElement.getAttributes();
			Node nodeAttr0 = attr0.getNamedItem("xmlns:gml");
			nodeAttr0.setTextContent("http://www.opengis.net/gml/3.2");
			
			Element element = null;
			NodeList listElement = null;
			
			listElement = doc.getElementsByTagName("base:localId");
			for (int i = 0; i < listElement.getLength(); i++) {
				element = (Element) listElement.item(i);
				element.setTextContent(localName);
			}
			
			listElement = doc.getElementsByTagName("base:namespace");
			for (int i = 0; i < listElement.getLength(); i++) {
				element = (Element) listElement.item(i);
				element.setTextContent(valueNamespace);
			}
			
			listElement = doc.getElementsByTagName("lcv:LandCoverUnit");
			for (int i = 0; i < listElement.getLength(); i++) {
				element = (Element) listElement.item(i);
				NamedNodeMap attr1 = element.getAttributes();
				Node nodeAttr1 = attr1.getNamedItem("gml:id");
				nodeAttr1.setTextContent(nodeAttr1.getTextContent() + "_1");
			}
			
			element = (Element) doc.getElementsByTagName("lcn:nomenclatureCodeList").item(0);
			element.setTextContent(codelistUrl);
			element = (Element) doc.getElementsByTagName("base2:name").item(0);
			element.setTextContent(documentName);
			element = (Element) doc.getElementsByTagName("base2:link").item(0);
			element.setTextContent(documentLink);
			element = (Element) doc.getElementsByTagName("gco:DateTime").item(0);
			element.setTextContent(sdf.format(new Date()));
			element = (Element) doc.getElementsByTagName("lcv:name").item(0);
			element.setTextContent(sdf.format(new Date()));
			
			listElement = doc.getElementsByTagName("gml:Polygon");
			for (int i = 0; i < listElement.getLength(); i++) {
				element = (Element) listElement.item(i);
				if (!element.hasAttribute("gml:id")) {
					element.setAttribute("gml:id", idPolygon + i);
				}
			}
			
			listElement = doc.getElementsByTagName("gml:MultiSurface");
			for (int i = 0; i < listElement.getLength(); i++) {
				element = (Element) listElement.item(i);
				if (!element.hasAttribute("gml:id")) {
					element.setAttribute("gml:id", idMultiSurface + i);
				}
			}
			
			listElement = doc.getElementsByTagName("gml:LinearRing");
			for (int i = 0; i < listElement.getLength(); i++) {
				element = (Element) listElement.item(i);
				if (element.hasAttribute("srsDimension")) {
					element.removeAttribute("srsDimension");
				}
			}
			
			result = prettyPrint(doc, localName);
			return result;
		}
		catch (Exception e) {
			LOGGER.error(e.getMessage());
			return "error while entering parameters ";
		}
		
	}
	
	public String insertValueForEp09(String result, String localName, String valueNamespace, String codelistUrl,
			String documentName, String documentLink) {
		try {
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(false);
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(result));
			Document doc = db.parse(is);
			
			Node rootElement = doc.getElementsByTagName("gml:FeatureCollection").item(0);
			// update id attribute
			NamedNodeMap attr0 = rootElement.getAttributes();
			Node nodeAttr0 = attr0.getNamedItem("xmlns:gml");
			nodeAttr0.setTextContent("http://www.opengis.net/gml/3.2");
			
			Element element;
			NodeList listElement = doc.getElementsByTagName("nz-core:beginLifeSpanVersion");
			for (int i = 0; i < listElement.getLength(); i++) {
				element = (Element) listElement.item(i);
				element.setTextContent(sdf.format(new Date()));
			}
			
			listElement = doc.getElementsByTagName("gml:Polygon");
			for (int i = 0; i < listElement.getLength(); i++) {
				element = (Element) listElement.item(i);
				element.setAttribute("gml:id", "fid_" + i);
				
			}
			
			listElement = doc.getElementsByTagName("base:localId");
			for (int i = 0; i < listElement.getLength(); i++) {
				element = (Element) listElement.item(i);
				element.setTextContent("landslide_" + i);
			}
			
			listElement = doc.getElementsByTagName("gml:LinearRing");
			for (int i = 0; i < listElement.getLength(); i++) {
				element = (Element) listElement.item(i);
				if (element.hasAttribute("srsDimension")) {
					element.removeAttribute("srsDimension");
				}
			}
			
			result = prettyPrint(doc, localName);
			return result;
		}
		catch (Exception e) {
			LOGGER.error(e.getMessage());
			return "error while entering parameters ";
		}
		
	}
	
	public static String prettyPrint(Document xml, String localName) throws Exception {
		Transformer tf = TransformerFactory.newInstance().newTransformer();
		tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		tf.setOutputProperty(OutputKeys.INDENT, "yes");
		Writer out = new StringWriter();
		tf.transform(new DOMSource(xml), new StreamResult(out));
		
		PrintWriter out_file = new PrintWriter(ProjectPropertiesUtils.loadByName(Constants.PATH_GML) + localName
				+ ".gml");
		out_file.println(out.toString());
		out_file.close();
		
		return out.toString();
	}
	
}
