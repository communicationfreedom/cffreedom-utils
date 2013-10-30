package com.cffreedom.utils.net;

import junit.framework.Assert;
import org.junit.Test;

import com.cffreedom.beans.Response;
import com.cffreedom.exceptions.NetworkException;

public class HttpUtilsTest
{
	@Test
	public void testHttpGet() throws NetworkException
	{
		Response response = HttpUtils.httpGet("http://www.google.com");
		Assert.assertNotNull(response);
		Assert.assertEquals(200, response.getIntCode());
	}
}
