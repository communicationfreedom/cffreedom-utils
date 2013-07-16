package com.cffreedom.utils.security;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

public class EncryptDecryptProxyTest
{
	private final String KEY = "somerandomvaluefortesting";
	private final String TEST_STRING = "this is my value";

	@Test
	public void testEncryptDecrypt() throws IOException
	{
		EncryptDecryptProxy edp = new EncryptDecryptProxy(KEY);
		String encrypted = edp.encrypt(TEST_STRING);
		Assert.assertEquals(TEST_STRING, edp.decrypt(encrypted));
	}
}
