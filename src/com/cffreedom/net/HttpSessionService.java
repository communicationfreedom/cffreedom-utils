package com.cffreedom.net;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.cffreedom.utils.ConversionUtils;
import com.cffreedom.utils.LoggerUtil;
import com.cffreedom.utils.Utils;

/**
 * Useful for simulating working your way through a web site that needs a session.
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
public class HttpSessionService
{
	private LoggerUtil logger = new LoggerUtil(LoggerUtil.FAMILY_UTIL, this.getClass().getSimpleName());
	private DefaultHttpClient httpClient = null;
	private CookieStore cookieStore = null;
	private HttpContext httpContext = null;
	private String lastRequestUrl = null;
	private String lastRedirectUrl = null;
	private HttpResponse lastResponse = null;
	private String lastResult = null;

	public HttpSessionService() throws KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException
	{
		this(false);
	}
	
	public HttpSessionService(boolean overrideSSLTrustStrategy) throws KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException
	{
		if (overrideSSLTrustStrategy == true)
		{
			TrustStrategy easyStrategy = new TrustStrategy()
			{
				@Override
			    public boolean isTrusted(X509Certificate[] chain, String authType)
			    {
			        return true;
			    }
			};
			SSLSocketFactory sf = new SSLSocketFactory(easyStrategy, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			SchemeRegistry registry = new SchemeRegistry();
	        registry.register(new Scheme("https", 8443, sf));
	
	        ClientConnectionManager ccm = new ThreadSafeClientConnManager(registry);
	        
			this.httpClient = new DefaultHttpClient(ccm);
		}
		else
		{
			this.httpClient = new DefaultHttpClient();
		}
		this.httpClient.setRedirectStrategy(new LaxRedirectStrategy());
		this.cookieStore = new BasicCookieStore();
		this.httpContext = new BasicHttpContext();
		this.httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
	}
	
	public String getRequest(String url) throws ClientProtocolException, IOException
	{
		final String METHOD = "getRequest";
		this.lastRequestUrl = url;
		//logger.logDebug(METHOD, "URL: " + url);
		HttpGet httpGet = new HttpGet(url);
		//logger.logDebug(METHOD, "Calling execute");
		HttpResponse response = this.httpClient.execute(httpGet, this.httpContext);
		processResponse(response);
		return this.lastResult;
	}
	
	public String postRequest(String url, HashMap<String, String> queryParams) throws IOException
	{
		this.lastRequestUrl = url;
		HttpPost httpPost = new HttpPost(url);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		for (String key : queryParams.keySet())
		{
			nvps.add(new BasicNameValuePair(key, queryParams.get(key)));
		}
		httpPost.setEntity(new UrlEncodedFormEntity(nvps));
		HttpResponse response = this.httpClient.execute(httpPost, this.httpContext);
		processResponse(response);
		return this.lastResult;
	}
	
	private void processResponse(HttpResponse response) throws IllegalStateException, IOException
	{
		final String METHOD = "processResponse";
		
		//logger.logDebug(METHOD, "Processing response");
		
		this.lastRedirectUrl = null;
		this.lastResponse = response;
		
		//logger.logDebug(METHOD, "Getting lastResult");
		if ((response.getEntity() != null) && (response.getEntity().getContent() != null))
		{
			this.lastResult = ConversionUtils.toString(response.getEntity().getContent());
		}
		else
		{
			this.lastResult = response.toString();
		}
		
		//logger.logDebug(METHOD, "Getting lastRedirectUrl");
		if (response.containsHeader("Location") == true)
		{
			this.lastRedirectUrl = response.getLastHeader("Location").getValue();
		}
		
		//logger.logDebug(METHOD, "Consuming response");
		if (response.getEntity() != null) {
			EntityUtils.consume(response.getEntity());
	    }
	}
	
	public void printLastResponseInfo()
	{
		Utils.output("Last Request URL: " + this.lastRequestUrl);
		Utils.output("Last Redirect URL: " + this.lastRedirectUrl);
		Utils.output("Last Results: " + this.lastResult);
	}
	
	public String getLastRedirectUrl() { return this.lastRedirectUrl; }
}
