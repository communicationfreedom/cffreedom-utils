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
	public final void isDateTest() {
		assertFalse(Utils.isDate("kajsdfljsdf"));
		assertFalse(Utils.isDate("13/02/2015"));
		assertTrue(Utils.isDate("01/02/2015"));
		assertTrue(Utils.isDate("12/31/9999"));
		assertTrue(Utils.isDate("01/01/1900"));
		assertTrue(Utils.isDate("2015-01-05"));
		assertTrue(Utils.isDate("2015-01-05 23:45:15"));
		assertFalse(Utils.isDate("2015-01-40 23:45:15"));
		assertFalse(Utils.isDate("2015-01-05 99:45:15"));
	}
	
	@Test
	public final void isPhoneNumberTest() {
		assertTrue(Utils.isPhoneNumber("234-456-4987"));
		assertTrue(Utils.isPhoneNumber("(234) 456-4987"));
		assertTrue(Utils.isPhoneNumber("234.456.4987"));
		assertTrue(Utils.isPhoneNumber("+1 234-456-4987"));
		
		assertFalse(Utils.isPhoneNumber("234-456-498"));
		assertFalse(Utils.isPhoneNumber("234-G56-4987"));
		assertFalse(Utils.isPhoneNumber("456-4987"));
	}
	
	@Test
	public final void isIntListTest() {
		assertTrue(Utils.isIntList("12, 15,9", ",", true));
		assertTrue(Utils.isIntList("1", ",", true));
		
		assertFalse(Utils.isIntList("12, 15,9", ",", false));
	}
}
