package com.cffreedom.utils.security;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

public class SecurityCipherTest
{
	private final String KEY_16_PLUS = "somerandomvaluefortesting";
	private final String KEY_SMALL = "secret";
	private final String TEST_STRING = "this is my value";

	@Test
	public void testSecurityCipher() throws IOException
	{
		SecurityCipher edp = new SecurityCipher(KEY_16_PLUS);
		String encrypted = edp.encrypt(TEST_STRING);
		Assert.assertEquals(TEST_STRING, edp.decrypt(encrypted));
	}
	
	@Test
	public void testZeroLengthVal() throws IOException
	{
		SecurityCipher edp = new SecurityCipher(KEY_16_PLUS);
		String encrypted = edp.encrypt("");
		Assert.assertEquals("", edp.decrypt(encrypted));
	}

	@Test
	public void testSecurityCipherSmallKey() throws IOException
	{
		SecurityCipher edp = new SecurityCipher(KEY_SMALL);
		String encrypted = edp.encrypt(TEST_STRING);
		Assert.assertEquals(TEST_STRING, edp.decrypt(encrypted));
	}
}
