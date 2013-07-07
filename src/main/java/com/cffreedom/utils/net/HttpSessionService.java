package com.cffreedom.utils.net;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cffreedom.beans.Container;
import com.cffreedom.utils.ConversionUtils;
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
 * 
 * Changes:
 * 2013-05-10 	markjacobsen.net 	Using PoolingClientConnectionManager() instead of ThreadSafeClientConnManager()
 * 									Allowed usage of non-standard ports
 */
public class HttpSessionService
{
	private static final Logger logger = LoggerFactory.getLogger("com.cffreedom.utils.net.HttpSessionService");
	private DefaultHttpClient httpClient = null;
	private CookieStore cookieStore = null;
	private HttpContext httpContext = null;
	private String lastRequestUrl = null;
	private String lastRedirectUrl = null;
	private String lastResult = null;
	private HttpResponse lastResponse = null;

	public HttpSessionService() throws KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException
	{
		this(new ArrayList<Container>());
	}
	
	public HttpSessionService(ArrayList<Container> protocols) throws KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException
	{
		X509TrustManager tm = new X509TrustManager() 
		{
			public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {}
			 
			public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {}
			 
			public X509Certificate[] getAcceptedIssuers() { return null; }
		};
		
		TrustStrategy easyStrategy = new TrustStrategy()
		{
			public boolean isTrusted(X509Certificate[] chain, String authType)
		    {
		        return true;
		    }
		};
		
		SSLContext ctx = SSLContext.getInstance("TLS");
		ctx.init(null, new TrustManager[]{tm}, null);
		SSLSocketFactory sf = new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		
		//SSLSocketFactory sf = new SSLSocketFactory(easyStrategy, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("https", 443, sf));
        registry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
        
        for (Container container : protocols)
        {
        	if (container.getCode().equalsIgnoreCase("https") == true)
        	{
        		registry.register(new Scheme(container.getCode(), ConversionUtils.toInt(container.getValue()), sf));
        	}
        	else
        	{
        		registry.register(new Scheme(container.getCode(), ConversionUtils.toInt(container.getValue()), PlainSocketFactory.getSocketFactory()));
        	}
        }

        ClientConnectionManager ccm = new PoolingClientConnectionManager(registry); //new ThreadSafeClientConnManager(registry);
        
		this.httpClient = new DefaultHttpClient(ccm);
		this.httpClient.setRedirectStrategy(new LaxRedirectStrategy());
		this.cookieStore = new BasicCookieStore();
		this.httpContext = new BasicHttpContext();
		this.httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
	}
	
	public String getRequest(String url) throws ClientProtocolException, IOException
	{
		this.lastRequestUrl = url;
		logger.debug("URL: {}", url);
		HttpGet httpGet = new HttpGet(url);
		HttpResponse response = this.httpClient.execute(httpGet, this.httpContext);
		processResponse(response);
		return this.lastResult;
	}
	
	public String postRequest(String url, HashMap<String, String> queryParams) throws IOException
	{
		this.lastRequestUrl = url;
		logger.debug("URL: {}", url);
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
		logger.debug("Processing response");
		
		this.lastRedirectUrl = null;
		this.lastResponse = response;
		
		logger.trace("Getting lastResult");
		if ((response.getEntity() != null) && (response.getEntity().getContent() != null))
		{
			this.lastResult = ConversionUtils.toString(response.getEntity().getContent());
		}
		else
		{
			this.lastResult = response.toString();
		}
		
		logger.trace("Getting lastRedirectUrl");
		if (response.containsHeader("Location") == true)
		{
			this.lastRedirectUrl = response.getLastHeader("Location").getValue();
		}
		
		logger.trace("Consuming response");
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
