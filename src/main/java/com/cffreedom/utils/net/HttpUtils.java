package com.cffreedom.utils.net;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cffreedom.beans.Container;
import com.cffreedom.beans.EmailMessage;
import com.cffreedom.beans.Response;
import com.cffreedom.exceptions.GeneralException;
import com.cffreedom.exceptions.NetworkException;
import com.cffreedom.exceptions.ValidationException;
import com.cffreedom.utils.Convert;
import com.cffreedom.utils.SystemUtils;

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
 * 2013-04-24 	markjacobsen.net 	Moved from com.cffreedom.utils to com.cffreedom.net package
 * 2013-04-24 	markjacobsen.net 	Added optional setupProxy param to httpGet() and created httpPost()
 * 2013-05-07 	markjacobsen.net 	Added getMailtoLink()
 * 2013-05-09 	markjacobsen.net 	Fixed encoding of params containing an ampersand in getMailtoLink()
 * 2013-05-20 	markjacobsen.net 	Added httpGetWithReqProp(String urlStr, HashMap<String, String> reqProps) for sending req w/ multiple request headers
 * 2013-05-21 	markjacobsen.net 	Added reqProps param to httpPost()
 * 2013-06-25 	markjacobsen.net 	Added httpGetResponse() for returning a Response object
 * 2013-07-04 	markjacobsen.net 	httpGetResponse() is now httpGet()
 * 2013-07-06 	markjacobsen.net 	Using slf4j
 * 2013-10-07 	MarkJacobsen.net	Added getParamAsInt()
 * 2013-12-06 	MarkJacobsen.net 	Added getProtocol(), getDomain(), getScript(), getQueryString(), and getQueryStringValues()
 * 2014-09-10 	MarkJacobsen.net 	Added writeServletTextResponse()
 * 2014-09-16 	MarkJacobsen.net	Added downloadFile()
 * 2014-09-16 	MarkJacobsen.net 	Added getUrl()
 */
public class HttpUtils
{
	private static final Logger logger = LoggerFactory.getLogger("com.cffreedom.utils.net.HttpUtils");
	
	/**
	 * Given a HttpServletRequest, get the full URL
	 * @param request
	 * @param includeQueryString
	 * @return
	 */
	public static String getUrl(HttpServletRequest request, boolean includeQueryString)
	{
		StringBuffer url = request.getRequestURL();
		if (request.isSecure() == true) {
			url = new StringBuffer(url.toString().replace("http:", "https:"));
		}
		if ((includeQueryString == true) && (request.getQueryString() != null)) {
			url.append("?");
			url.append(request.getQueryString());
		}
		return url.toString();
	}
	
	public static String buildUrl(String urlStr, Map<String, String> queryParams)
	{
		try
		{
			if ((queryParams != null) && (queryParams.size() > 0))
			{
				String queryStr = encodeParams(queryParams);
				urlStr += "?" + queryStr;
			}
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			urlStr = null;
		}
		return urlStr;
	}
	
	/**
	 * Download a file from a URL to the local machine
	 * @param url
	 * @param localFile
	 * @throws NetworkException
	 */
	public static void downloadFile(String url, String localFile) throws NetworkException
	{
		URL urlObject = null;
		HttpURLConnection connection = null;
		InputStream stream = null;
		OutputStream out = null;
		
		try
		{
			urlObject = new URL(url);
			connection = (HttpURLConnection)urlObject.openConnection();
			stream = connection.getInputStream();
	        out = new FileOutputStream(localFile);
	        byte buf[]= new byte[4096];
	        int read = -1;
	        logger.trace("Start downloading {} to {}", url, localFile);
	        while ((read = stream.read(buf)) != -1)
	        {
	        	if (read > 0)
	        	{
	        		out.write(buf, 0, read);
	        	}
	        }
	        logger.trace("Finished downloading {} to {}", url, localFile);
		}
		catch (IOException e)
		{
			throw new NetworkException("Error during downloadFile: " + e.getMessage(), e);
		}
		finally
		{
			try{ out.close(); }	catch (Exception e) {}
			try{ stream.close(); }	catch (Exception e) {}
			try{ connection.disconnect(); }	catch (Exception e) {}
		}
	}
	
	public static Response httpGet(String urlStr) throws NetworkException { return httpGet(urlStr, null); }
	public static Response httpGet(String urlStr, Map<String, String> queryParams) throws NetworkException { return httpGet(urlStr, queryParams, true); }
	public static Response httpGet(String urlStr, Map<String, String> queryParams, boolean setupProxy) throws NetworkException { return httpGet(urlStr, queryParams, true, 0); }
	public static Response httpGet(String urlStr, Map<String, String> queryParams, boolean setupProxy, int timeoutMS) throws NetworkException
	{
		try
		{
			Response response = new Response(true, 0, "", "", "");
			urlStr = buildUrl(urlStr, queryParams);
			
			if (setupProxy == true) { setupProxy(); }
			
			logger.trace("Getting: {}", urlStr);
			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("GET");
			conn.setInstanceFollowRedirects(true);
			if (timeoutMS > 0) {
				conn.setConnectTimeout(timeoutMS);
				conn.setReadTimeout(timeoutMS);
			}
			conn.connect();
			int resp = conn.getResponseCode();
			
			if ((resp == HttpURLConnection.HTTP_MOVED_TEMP) || (resp == HttpURLConnection.HTTP_MOVED_PERM) || (resp == HttpURLConnection.HTTP_MULT_CHOICE))
			{
				String redirectUrl = conn.getHeaderField("Location");
				return httpGet(redirectUrl, queryParams, setupProxy);
			}
			
			response.setIntCode(conn.getResponseCode());
	
			if (conn.getResponseCode() != 200) 
			{
				response.setErrorLevel(Response.ErrorLevel.ERROR);
				response.setBooleanCode(false);
				response.setMessage(conn.getResponseMessage());
				response.setStringCode("FAIL");
			}
			else
			{
				response.setErrorLevel(Response.ErrorLevel.INFO);
				response.setBooleanCode(true);
				response.setMessage("");
				response.setStringCode("SUCCESS");
				
				// Buffer the result into a string
				BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = rd.readLine()) != null) {
					sb.append(line);
				}
				rd.close();
				response.setDetail(sb.toString());
			}
			
			conn.disconnect();
			
			return response;
		}
		catch (IOException e)
		{
			throw new NetworkException("Error during httpGet: " + e.getMessage(), e);
		}		
	}

	public static String httpGetWithReqProp(String urlStr, Map<String, String> reqProps) throws NetworkException
	{
		try
		{
			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			for (String key : reqProps.keySet())
			{
				conn.setRequestProperty(key, reqProps.get(key));
			}
	
			if (conn.getResponseCode() != 200) {
				throw new NetworkException(conn.getResponseMessage());
			}
	
			// Buffer the result into a string
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			rd.close();
	
			conn.disconnect();
			return sb.toString();
		}
		catch (IOException e)
		{
			throw new NetworkException("Error during httpGetWithReqProp: " + e.getMessage(), e);
		}
	}
	
	public static String httpPost(String urlStr, Map<String, String> queryParams) throws GeneralException, NetworkException { return httpPost(urlStr, queryParams, null); }
	public static String httpPost(String urlStr, Map<String, String> queryParams, Map<String, String> reqProps) throws GeneralException, NetworkException { return httpPost(urlStr, queryParams, reqProps, true); }
	public static String httpPost(String urlStr, Map<String, String> queryParams, Map<String, String> reqProps, boolean setupProxy) throws GeneralException, NetworkException
	{
		try
		{
			int responseCode = 0;
			StringBuilder response = new StringBuilder();
			String urlParameters = encodeParams(queryParams);
			
			if (setupProxy == true) { setupProxy(); }
			
			logger.trace("Posting to: {}", urlStr);
			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			
			if (reqProps != null)
			{
				for (String key : reqProps.keySet())
				{
					conn.setRequestProperty(key, reqProps.get(key));
				}
			}
			
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setInstanceFollowRedirects(false); 
			conn.setRequestMethod("POST"); 
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
			conn.setRequestProperty("charset", "utf-8");
			conn.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
			conn.setUseCaches(false);
	
			DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();
	
			responseCode = conn.getResponseCode();
			
			if ((responseCode == 200) || (responseCode == 301) || (responseCode == 302))
			{
				// Buffer the result into a string
				BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String line;
				while ((line = rd.readLine()) != null) {
					response.append(line);
				}
				rd.close();
			}
			
			conn.disconnect();
			
			if (responseCode != 200) {
				throw new GeneralException("Bad Response Code: " + responseCode);
			}
			return response.toString();
		}
		catch (IOException e)
		{
			throw new NetworkException("Error during httpPost: " + e.getMessage(), e);
		}
	}
	
	public static String encodeParams(Map<String, String> params) throws UnsupportedEncodingException
	{
		String ret = "";
		if (params != null) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				ret += entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), "UTF-8") + "&";
			}
			if ((ret.length() > 0) && (ret.charAt(ret.length() - 1) == '&')) {
				// Strip the last ampersand
				ret = ret.substring(0, ret.length() - 1);
			}
		}
		return ret;
	}
	
	public static void setupProxy()
	{		
		if (System.getProperties().get("http.proxyHost") != null)
		{
			logger.debug("Proxy already setup via system properties");
		}
		else
		{
			String proxy = null;
			String envVar = null;
			if ((envVar == null) && (SystemUtils.getEnvVal("HTTP_PROXY") != null)) { envVar = "HTTP_PROXY"; }
			if ((envVar == null) && (SystemUtils.getEnvVal("HTTPS_PROXY") != null)) { envVar = "HTTPS_PROXY"; }
			if ((envVar == null) && (SystemUtils.getEnvVal("http_proxy") != null)) { envVar = "http_proxy"; }
			if ((envVar == null) && (SystemUtils.getEnvVal("https_proxy") != null)) { envVar = "https_proxy"; }
			
			if (envVar != null)
			{
				logger.debug("Setting up proxy from env var = {}", envVar);
				proxy = SystemUtils.getEnvVal(envVar);
				String[] parts = proxy.split("@");
				if (parts.length == 1)
				{
					String[] serverParts = parts[0].split(":");
					
					System.getProperties().put("http.proxyHost", serverParts[0]);
					System.getProperties().put("https.proxyHost", serverParts[0]);
					
					if (serverParts.length > 1)
					{
						System.getProperties().put("http.proxyPort", serverParts[1]);
						System.getProperties().put("https.proxyPort", serverParts[1]);
					}
				}
				else if (parts.length == 2)
				{
					String[] userParts = parts[0].replace("http://", "").split(":");
					String[] serverParts = parts[1].split(":");
					
					System.getProperties().put("http.proxyUser", userParts[0]);
					System.getProperties().put("https.proxyUser", userParts[0]);
					
					if (userParts.length > 1)
					{
						System.getProperties().put("http.proxyPassword", userParts[1]);
						System.getProperties().put("https.proxyPassword", userParts[1]);
					}
					
					System.getProperties().put("http.proxyHost", serverParts[0]);
					System.getProperties().put("https.proxyHost", serverParts[0]);
					
					if (serverParts.length > 1)
					{
						System.getProperties().put("http.proxyPort", serverParts[1]);
						System.getProperties().put("https.proxyPort", serverParts[1]);
					}
				}
				
				logger.debug("http.proxyHost = {}", System.getProperties().get("http.proxyHost"));
				logger.debug("http.proxyPort = {}", System.getProperties().get("http.proxyPort"));
				logger.debug("http.proxyUser = {}", System.getProperties().get("http.proxyUser"));
				logger.trace("http.proxyPassword = {}", System.getProperties().get("http.proxyPassword"));
				
				logger.debug("https.proxyHost = {}", System.getProperties().get("https.proxyHost"));
				logger.debug("https.proxyPort = {}", System.getProperties().get("https.proxyPort"));
				logger.debug("https.proxyUser = {}", System.getProperties().get("https.proxyUser"));
				logger.trace("https.proxyPassword = {}", System.getProperties().get("https.proxyPassword"));
			}
		}
	}
	
	public static String getMailtoLink(String to, String linkText) { return getMailtoLink(to, linkText, null); }
	public static String getMailtoLink(String to, String linkText, String subject) { return getMailtoLink(to, linkText, subject, ""); }
	public static String getMailtoLink(String to, String linkText, String subject, String body) { return getMailtoLink(to, linkText, subject, body, null); }
	public static String getMailtoLink(String to, String linkText, String subject, String body, String cc) { return getMailtoLink(to, linkText, subject, body, cc, null); }
	public static String getMailtoLink(String to, String linkText, String subject, String body, String cc, String bcc)
	{
		try
		{
			String qs = "";
			if ((cc != null) && (cc.trim().length() > 0)){ qs += "cc="+cc+"&"; }
			if ((bcc != null) && (bcc.trim().length() > 0)){ qs += "bcc="+bcc+"&"; }
			if ((subject != null) && (subject.trim().length() > 0)){ qs += "subject="+subject.replace("&", "%26")+"&"; }
			if ((body != null) && (body.trim().length() > 0)){ qs += "body="+body.replace("&", "%26")+"&"; }
			
			return "<a href=\"mailto:"+to+"&"+qs+"\">"+linkText+"</a>";
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public static String getMailtoLink(String to, String linkText, String subject, String[] body)
	{
		String newBody = "";
		for (int x = 0; x < body.length; x++)
		{
			newBody += body[x] + "%0A";
		}
		return getMailtoLink(to, linkText, subject, newBody);
	}
	
	public static String getMailtoLink(EmailMessage email, String linkText)
	{
		return getMailtoLink(email.getTo(), linkText, email.getSubject(), email.getBody(), email.getCc(), email.getBcc());
	}
	
	public static int getParamAsInt(HttpServletRequest request, String param) throws ValidationException
	{
		String paramVal = null;
		try
		{
			paramVal = request.getParameter(param);
			if (paramVal != null)
			{
				return Convert.toInt(paramVal);
			}
			else
			{
				throw new ValidationException("No parameter named: " + param);
			}
		}
		catch (Exception e)
		{
			throw new ValidationException("Unable to get int value for " + param + " = " + paramVal, e);
		}
	}
	
	/**
	 * Given a URL, return the protocol (ex: http)
	 * @param url
	 * @return
	 */
	public static String getProtocol(String url)
	{
		return url.split(":")[0].toLowerCase();
	}
	
	/**
	 * Given a URL, return the domain name
	 * @param url
	 * @return
	 */
	public static String getDomain(String url)
	{
		String protocol = getProtocol(url);
		url = url.substring(protocol.length() + 3); // + 3 to get rid of ://
		return url.split("\\/")[0];
	}
	
	/**
	 * Given a URL, return only the script (ex: /some/page.txt)
	 * @param url
	 * @return
	 */
	public static String getScript(String url)
	{
		String protocol = getProtocol(url);
		String domain = getDomain(url);
		String qs = getQueryString(url);
		
		url = url.replace(protocol + "://" + domain, "");
		
		if (qs.length() > 0)
		{
			url = url.replace("?" + qs, "");
		}
		else if (url.substring(url.length() - 1).equalsIgnoreCase("?") == true)
		{
			url = url.substring(0, url.length() - 1);
		}
		
		return url;
	}
	
	/**
	 * Given a URL, return the query string (i.e. everything after the ?)
	 * @param url URL to extract query string from
	 * @return Query String, or zero length string if none
	 */
	public static String getQueryString(String url)
	{
		int start = url.indexOf("?");
		if (start >= 0)
		{
			return url.substring(start+1);
		}
		else
		{
			return "";
		}
	}
	
	/**
	 * Given a URL, strip out the query string (assuming the value passed is a query string
	 * if one is not pulled out), and return a List of the key value pairs in Container objects
	 * @param url
	 * @return
	 */
	public static List<Container> getQueryStringValues(String url)
	{
		List<Container> vals = new ArrayList<Container>();
		String queryStr = getQueryString(url);
		if (queryStr.length() == 0) { queryStr = url; }
		
		String[] pairs = queryStr.split("&");
		for (int x = 0; x < pairs.length; x++)
		{
			if (pairs[x].length() > 0)
			{
				Container val = null;
				String[] pair = pairs[x].split("=");
				String key = pair[0].trim();
				if (pair.length == 1)
				{
					val = new Container(key, null);
				}
				else if (pair.length == 2)
				{
					val = new Container(key, pair[1].trim());
				}
				else if (pair.length > 2)
				{
					String tmp = "";
					for (int y = 1; y < pair.length; y++)
					{
						tmp += pair[y] + "=";
					}
					val = new Container(key, tmp.substring(0, tmp.length() - 1).trim());
				}
				vals.add(val);
			}
		}
		
		return vals;
	}
	
	public static String getGoogleMapsLink(String address, String city, String state, String zip) {
		String qs = address+" "+city+", "+state+" "+zip;
		address = address.replace(" ", "+");
		return "http://maps.google.com/?q="+qs;
	}
	
	public static void writeServletTextResponse(HttpServletResponse response, String body) throws IOException
	{
		response.setContentType("text/html;charset=UTF-8");
	    response.setCharacterEncoding("UTF-8");
		response.getWriter().write(body);
	}
}
