package com.cffreedom.utils;

import static org.junit.Assert.assertEquals;
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
	
	@Test
	public void formatPhone()
	{
		String expected = "517-803-2254";
		assertEquals(expected, FormatUtils.formatPhoneNumber(FormatUtils.PHONE_DASH, "517-803-2254"));
		assertEquals(expected, FormatUtils.formatPhoneNumber(FormatUtils.PHONE_DASH, "5178032254"));
		assertEquals(expected, FormatUtils.formatPhoneNumber(FormatUtils.PHONE_DASH, "(517) 803-2254"));
		assertEquals(expected, FormatUtils.formatPhoneNumber(FormatUtils.PHONE_DASH, "+15178032254"));
		assertEquals(expected, FormatUtils.formatPhoneNumber(FormatUtils.PHONE_DASH, "1-517-803-2254"));
		
		expected = "517.803.2254";
		assertEquals(expected, FormatUtils.formatPhoneNumber(FormatUtils.PHONE_DOT, "517-803-2254"));
		assertEquals(expected, FormatUtils.formatPhoneNumber(FormatUtils.PHONE_DOT, "5178032254"));
		assertEquals(expected, FormatUtils.formatPhoneNumber(FormatUtils.PHONE_DOT, "(517) 803-2254"));
		assertEquals(expected, FormatUtils.formatPhoneNumber(FormatUtils.PHONE_DOT, "+15178032254"));
		assertEquals(expected, FormatUtils.formatPhoneNumber(FormatUtils.PHONE_DOT, "1-517-803-2254"));
	}
}
