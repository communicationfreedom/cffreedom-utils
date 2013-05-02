package com.cffreedom.utils;

import java.io.File;
import java.io.IOException;
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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
 */
public class XmlUtils
{
	public static Document getDomDocument(String file)
	{
		try
		{
			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
			domFactory.setNamespaceAware(false); // NEVER FORGET THIS
			DocumentBuilder builder = domFactory.newDocumentBuilder();
		    return builder.parse(file);
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
				//LoggerUtil.log(LoggerUtil.LEVEL_DEBUG, "replaceXPath", "Replacing");
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
