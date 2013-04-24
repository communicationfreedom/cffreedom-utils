package com.cffreedom.net;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.cffreedom.utils.ConversionUtils;

public class HttpSessionService
{
	private HttpClient httpClient = null;
	private CookieStore cookieStore = null;
	private HttpContext httpContext = null;

	public HttpSessionService()
	{
		this.httpClient = new DefaultHttpClient();
		this.cookieStore = new BasicCookieStore();
		this.httpContext = new BasicHttpContext();
		httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
	}
	
	public String getRequest(String url) throws IOException
	{
		HttpGet httpGet = new HttpGet(url);
		HttpResponse response = this.httpClient.execute(httpGet, this.httpContext);
		String result = ConversionUtils.toString(response.getEntity().getContent());
		if (response.getEntity() != null)
		{
			EntityUtils.consume(response.getEntity());
		}
		return result;
	}
}
