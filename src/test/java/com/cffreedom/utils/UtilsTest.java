package com.cffreedom.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class UtilsTest 
{
	@Test
	public void replaceLastTest()
	{
		String str = "This is my test string";
		String actual = Utils.replaceLast(str, "test", "favorite");
		String expected = "This is my favorite string";
		assertEquals(expected, actual);
		
		actual = Utils.replaceLast(str, "string", "widget");
		expected = "This is my test widget";
		assertEquals(expected, actual);
		
		actual = Utils.replaceLast(str, "asdlfaslkf", "widget");
		expected = "This is my test string";
		assertEquals(expected, actual);
	}
	
	@Test
	public void isDateTest()
	{
		assertTrue(Utils.isDate("01/13/2015"));
		assertFalse(Utils.isDate("13/13/2015"));
	}
}
