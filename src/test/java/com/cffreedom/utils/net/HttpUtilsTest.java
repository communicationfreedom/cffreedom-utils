package com.cffreedom.utils.net;

import java.io.IOException;

import junit.framework.Assert;
import org.junit.Test;

import com.cffreedom.beans.Response;

public class HttpUtilsTest
{
	@Test
	public void testHttpGetDepricated() throws IOException
	{
		String response = HttpUtils.httpGet("http://www.google.com");
		Assert.assertNotNull(response);
		Assert.assertFalse(response.equalsIgnoreCase(""));
	}
	
	@Test
	public void testHttpGetResponse() throws IOException
	{
		Response response = HttpUtils.httpGetResponse("http://www.google.com");
		Assert.assertNotNull(response);
		Assert.assertEquals(200, response.getIntCode());
	}
}
