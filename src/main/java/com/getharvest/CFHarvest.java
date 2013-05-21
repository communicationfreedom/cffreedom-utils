package com.getharvest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.cffreedom.beans.Container;
import com.cffreedom.utils.Utils;
import com.cffreedom.utils.XmlUtils;
import com.cffreedom.utils.net.HttpUtils;

/**
 * Class to make working with the Harvest API easier
 * 
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
public class CFHarvest
{
	private String authVal;
	private String subdomain;
	
	public CFHarvest(String subdomain, String user, String pass)
	{
		String key = user + ":" + pass;
		String encodedLogin = new String(Base64.encodeBase64String(key.getBytes()).trim());
		this.authVal = "Basic " + encodedLogin;
		this.subdomain = subdomain;
	}
	
	private String getAuthVal()
	{
		return this.authVal;
	}
	
	private String getSubdomain()
	{
		return this.subdomain;
	}
	
	public void getAccount()
	{
		try
		{
			String url = "/account/who_am_i";
			String response = makeRequest(url);
			Utils.output(response);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public boolean projectExists(String name)
	{
		for (Container container : this.getProjects())
		{
			if (container.getValue().equalsIgnoreCase(name) == true)
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean taskExists(String name)
	{
		for (Container container : this.getTasks())
		{
			if (container.getValue().equalsIgnoreCase(name) == true)
			{
				return true;
			}
		}
		return false;
	}
	
	public ArrayList<Container> getProjects()
	{
		ArrayList<Container> ret = new ArrayList<Container>();
		
		try
		{
			String url = "/projects";
			String response = makeRequest(url);
			Utils.output(response);
			Document doc = XmlUtils.getDomDocument(response, false);
			NodeList nodes = XmlUtils.getXPathNodes("/projects/project", doc);

			for (int x = 0; x < nodes.getLength(); x++)
			{
				Node node = nodes.item(x);
				Container container = new Container(XmlUtils.getFirstChildNodeNamed(node, "id").getTextContent(), 
													XmlUtils.getFirstChildNodeNamed(node, "name").getTextContent());
				ret.add(container);
				Utils.output(container.toString());
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return ret;
	}
	
	public ArrayList<Container> getTasks()
	{
		ArrayList<Container> ret = new ArrayList<Container>();
		
		try
		{
			String url = "/tasks";
			String response = makeRequest(url);
			Utils.output(response);
			Document doc = XmlUtils.getDomDocument(response, false);
			NodeList nodes = XmlUtils.getXPathNodes("/tasks/task", doc);

			for (int x = 0; x < nodes.getLength(); x++)
			{
				Node node = nodes.item(x);
				Container container = new Container(XmlUtils.getFirstChildNodeNamed(node, "id").getTextContent(), 
													XmlUtils.getFirstChildNodeNamed(node, "name").getTextContent());
				ret.add(container);
				Utils.output(container.toString());
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return ret;
	}
	
	private String makeRequest(String url) throws IOException
	{
		String fullurl = "https://" + this.getSubdomain() + ".harvestapp.com" + url;
		HashMap<String, String> reqProps = new HashMap<String, String>();
		reqProps.put("Authorization", this.getAuthVal());
		reqProps.put("Accept", "application/xml");
		reqProps.put("Content-Type", "application/xml");
		return HttpUtils.httpGetWithReqProp(fullurl, reqProps);
	}
}
