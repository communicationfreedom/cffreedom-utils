package com.cffreedom.utils;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author markjacobsen.net (http://mjg2.net/code)
 * Copyright: Communication Freedom, LLC - http://www.communicationfreedom.com
 * 
 * Free to use, modify, redistribute.  Must keep full class header including 
 * copyright and note your modifications.
 * 
 * If this helped you out or saved you time, please consider...
 * 1) Donating: http://www.communicationfreedom.com/go/donate/
 * 2) Shoutout on twitter: @MarkJacobsen or @cffreedom
 * 3) Linking to: http://visit.markjacobsen.net
 * 
 * Changes:
 * 2013-05-20 	markjacobsen.net 	Added getFirstChildNodeNamed()
 * 									Added the ability to get a Document from XML string (not in a file)
 */
public class XmlUtils
{
	private static final Logger logger = LoggerFactory.getLogger("com.cffreedom.utils.XmlUtils");
	
	public static Document getDomDocument(String source) { return getDomDocument(source, true); }
	public static Document getDomDocument(String source, boolean sourceIsFile)
	{
		try
		{
			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
			domFactory.setNamespaceAware(false); // NEVER FORGET THIS
			DocumentBuilder builder = domFactory.newDocumentBuilder();
			if (sourceIsFile == false){
				return builder.parse(new InputSource(new StringReader(source)));
			}else{
				return builder.parse(source);
			}
		}
		catch (IOException e){e.printStackTrace(); return null;}
		catch (ParserConfigurationException e) {e.printStackTrace(); return null;}
		catch (SAXException e){e.printStackTrace(); return null;}
	}
	
	public static XPath getXPathInstance()
	{
		XPathFactory factory = XPathFactory.newInstance();
	    return factory.newXPath();
	}
	
	public static NodeList getXPathNodes(String search, String file) { return getXPathNodes(search, getDomDocument(file)); }
	public static NodeList getXPathNodes(String search, Document domDocument) { return getXPathNodes(search, domDocument, getXPathInstance()); }
	public static NodeList getXPathNodes(String search, Document domDocument, XPath xpathInstance)
	{
		try
		{
			XPathExpression expr = xpathInstance.compile(search);
			return (NodeList)expr.evaluate(domDocument, XPathConstants.NODESET);
		}
		catch (XPathExpressionException e) {e.printStackTrace(); return null;}
	}
	
	public static Node getFirstChildNodeNamed(Node node, String name)
	{
		NodeList nodes = node.getChildNodes();
		for (int x = 0; x < nodes.getLength(); x++)
		{
			if (nodes.item(x).getNodeName().equalsIgnoreCase(name) == true)
			{
				return nodes.item(x);
			}
		}
		return null;
	}
	
	public static String getAttributeValue(Node element, String attribute)
	{
		return element.getAttributes().getNamedItem(attribute).getNodeValue();
	}
	
	public static NodeList getDomNodes(String file, String xmlTag)
	{
		NodeList nodes = null;
		
		try
		{
			File xml = new File(file);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xml);
			doc.getDocumentElement().normalize();
	
			nodes = doc.getElementsByTagName(xmlTag);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			nodes = null;
		}
		return nodes;
	}
	
	public static String getDomValue(String tag, Element element) 
	{
		NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
		Node node = (Node) nodes.item(0);
		return node.getNodeValue();
	}
	
	public static String getDomAttributeValue(String attrib, Element element) 
	{
		return element.getAttribute(attrib);
	}
	
	public static String replaceXPath(Document domDocument, String search, String replace)
	{
		try
		{
			NodeList nodes = getXPathNodes(search, domDocument);
			for (int i=0 ; i < nodes.getLength() ; i++)
			{
				//logger.debug("Replacing");
				Node node = nodes.item(i);
				node.setTextContent(replace);
			}
			return getDomDocumentAsXml(domDocument);
		}
		catch (Exception e) { e.printStackTrace(); return null; }
	}
	
	public static String getDomDocumentAsXml(org.w3c.dom.Document domDocument)
	{
		try
		{
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			StreamResult xmlOutput = new StreamResult(new StringWriter());
			transformer.transform(new DOMSource(domDocument), xmlOutput);
			return xmlOutput.getWriter().toString();
		}
		catch (Exception e) { e.printStackTrace(); return null; }
	}
}
