package com.cffreedom.utils;

import java.math.BigInteger;

import junit.framework.Assert;

import org.junit.Test;

public class ConvertTest {

	@Test
	public void testToBigInteger()
	{
		long convert = 2;
		BigInteger converted = Convert.toBigInteger(convert);
		Assert.assertEquals(convert, converted.longValue());
		
		int convert2 = 5;
		BigInteger converted2 = Convert.toBigInteger(convert2);
		Assert.assertEquals(convert2, converted2.longValue());
	}
}
