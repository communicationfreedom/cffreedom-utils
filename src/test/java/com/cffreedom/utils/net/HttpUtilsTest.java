package com.cffreedom.utils.net;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.cffreedom.beans.Container;
import com.cffreedom.beans.Response;
import com.cffreedom.exceptions.NetworkException;

public class HttpUtilsTest
{
	@Test
	public void testHttpGet() throws NetworkException
	{
		Response response = HttpUtils.httpGet("http://www.google.com");
		assertNotNull(response);
		assertEquals(200, response.getIntCode());
	}
	
	@Test
	public void testGetProtocol()
	{
		assertEquals("http", HttpUtils.getProtocol("http://www.somesite.com/test.cfm?this=that"));
	}
	
	@Test
	public void testGetDomain()
	{
		assertEquals("www.somesite.com", HttpUtils.getDomain("http://www.somesite.com/test.cfm?this=that"));
		assertEquals("somesite.com", HttpUtils.getDomain("http://somesite.com/"));
		assertEquals("somesite.com:8080", HttpUtils.getDomain("http://somesite.com:8080/more/junk.txt"));
	}
	
	@Test
	public void testGetScript()
	{
		String url = "http://www.somesite.com/test.cfm?this=that&that=theOtherThing";
		String script = HttpUtils.getScript(url);
		assertEquals("/test.cfm", script);
		
		url = "http://www.somesite.com/?this=that&that=theOtherThing";
		script = HttpUtils.getScript(url);
		assertEquals("/", script);
		
		url = "http://www.somesite.com/page.txt?";
		script = HttpUtils.getScript(url);
		assertEquals("/page.txt", script);
	}
	
	@Test
	public void testGetQueryString()
	{
		String url = "http://www.somesite.com/test.cfm?this=that";
		String qs = HttpUtils.getQueryString(url);
		assertEquals("this=that", qs);
		
		url = "http://www.somesite.com/test.cfm?this=that&that=theOtherThing";
		qs = HttpUtils.getQueryString(url);
		assertEquals("this=that&that=theOtherThing", qs);
		
		url = "http://www.somesite.com/?this=that&that=theOtherThing";
		qs = HttpUtils.getQueryString(url);
		assertEquals("this=that&that=theOtherThing", qs);
				
		url = "http://www.somesite.com/page.txt?";
		qs = HttpUtils.getQueryString(url);
		assertEquals("", qs);
	}
	
	@Test
	public void testGetQueryStringValues()
	{
		String url = "http://www.somesite.com/test.cfm?this=that";
		List<Container> vals = HttpUtils.getQueryStringValues(url);
		assertEquals(1, vals.size());
		assertEquals("this", vals.get(0).getCode());
		assertEquals("that", vals.get(0).getValue());
		
		url = "http://www.somesite.com/test.cfm?this= that ";
		vals = HttpUtils.getQueryStringValues(url);
		assertEquals(1, vals.size());
		assertEquals("this", vals.get(0).getCode());
		assertEquals("that", vals.get(0).getValue());
		
		url = "http://www.somesite.com/test.cfm?this=that=joe";
		vals = HttpUtils.getQueryStringValues(url);
		assertEquals(1, vals.size());
		assertEquals("this", vals.get(0).getCode());
		assertEquals("that=joe", vals.get(0).getValue());
	}
}
