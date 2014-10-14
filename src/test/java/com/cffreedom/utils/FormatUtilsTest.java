package com.cffreedom.utils;

import junit.framework.Assert;

import org.junit.Test;

public class FormatUtilsTest
{
	@Test
	public void stripNonNumericTest()
	{
		Assert.assertEquals("3000", FormatUtils.stripNonNumeric("$3,000"));
		Assert.assertEquals("3000000", FormatUtils.stripNonNumeric("3,000,000"));
	}
	
	@Test
	public void upperCaseFirstCharTest()
	{
		Assert.assertEquals("Mark", FormatUtils.upperCaseFirstChar("mark"));
	}
	
	@Test
	public void stripExtraSpacesTest()
	{
		Assert.assertEquals("Mark Jacobsen", FormatUtils.stripExtraSpaces("Mark  Jacobsen"));
	}
	
	@Test
	public void maxLenStringTest()
	{
		String test = "hi there";
		Assert.assertEquals(test, FormatUtils.maxLenString(test, 5000));
		Assert.assertEquals("hi", FormatUtils.maxLenString(test, 2));
		Assert.assertNull(FormatUtils.maxLenString(null, 20));
	}
}
