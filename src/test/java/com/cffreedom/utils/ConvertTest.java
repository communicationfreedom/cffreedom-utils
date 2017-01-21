package com.cffreedom.utils;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ConvertTest {

	@Test
	public void testToBigInteger() {
		long convert = 2;
		BigInteger converted = Convert.toBigInteger(convert);
		assertEquals(convert, converted.longValue());
		
		int convert2 = 5;
		BigInteger converted2 = Convert.toBigInteger(convert2);
		assertEquals(convert2, converted2.longValue());
		
		assertEquals(Convert.toBigInteger("5,000"), Convert.toBigInteger("5000"));
		assertEquals(Convert.toBigInteger("5,000.45"), Convert.toBigInteger("5000"));
	}
	
	@Test
	public void testToBigDecimal() {
		assertEquals(Convert.toBigDecimal("5,000"), Convert.toBigDecimal("5000"));
		assertEquals(Convert.toBigDecimal("5,000.345"), Convert.toBigDecimal("5000.345"));
	}
	
	@Test
	public void testToCents() {
		BigDecimal val = new BigDecimal(1.60);
		assertEquals(160, Convert.toCents(val));
	}
	
	@Test
	public void testToSqlStringList() {
		assertEquals(Convert.toSqlStringList("hi, bye", ",", true), "'hi','bye'");
		assertEquals(Convert.toSqlStringList("hi, bye", ",", false), "'hi',' bye'");
		assertEquals(Convert.toSqlStringList("hi, bye", "-", true), "'hi, bye'");
	}
	
	@Test
	public void testToBigDecimalFromCents() {
		BigDecimal expected = Convert.toBigDecimal(1.53);
		BigDecimal actual = Convert.toBigDecimalFromCents(153);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testToStringList() {
		List<String> vals = new ArrayList<String>();
		vals.add("A");
		vals.add("B");
		vals.add("C");
		String expected = "A,B,C";
		assertEquals(expected, Convert.toStringList(vals, ","));
		
		assertNull(Convert.toStringList(null, ","));
		
		expected = "";
		assertEquals(expected, Convert.toStringList(new ArrayList<String>(), ","));
	}
	
	@Test
	public void testToHtml() {
		String txt = "Don\'t you think that's fun?\n\nRight.";
		String html = "Don't you think that's fun?<br /><br />Right.";
		//Utils.output(Convert.toHtml(txt));
		assertEquals(html, Convert.toHtml(txt));
	}
	
	@Test
	public void testToInt() {
		String val = "183000";
		assertEquals(183000, Convert.toInt(val, true));
		val = "183,000";
		assertEquals(183000, Convert.toInt(val, true));
		val = "183,000.23";
		assertEquals(183000, Convert.toInt(val, true));
	}
	
	@Test(expected=NumberFormatException.class)
	public void testToIntException1() {
		Convert.toInt("SMITH-STREET", true);
	}
	
	@Test(expected=NumberFormatException.class)
	public void testToIntException2() {
		Convert.toInt("18,000.50", false);
	}
}
