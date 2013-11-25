package com.cffreedom.utils.security;

import static org.junit.Assert.*;

import java.security.NoSuchAlgorithmException;

import org.junit.Test;

public class SecurityUtilsTest
{
	@Test
	public void testSHA512() throws NoSuchAlgorithmException
	{
		// Really just testing that we get the same value twice in a row
		String password = "someRandom$va1U";
		String salt = "salt-1";
		String pass1 = SecurityUtils.encryptSHA512(password, salt);
		String pass2 = SecurityUtils.encryptSHA512(password, salt);
		
		assertEquals(pass1, pass2);
	}

	@Test
	public void testMD5() throws NoSuchAlgorithmException
	{
		// Really just testing that we get the same value twice in a row
		String password = "someRandom$va1U";
		String pass1 = SecurityUtils.encryptMD5(password);
		String pass2 = SecurityUtils.encryptMD5(password);
		
		assertEquals(pass1, pass2);
	}
}
