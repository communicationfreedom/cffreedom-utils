package com.cffreedom.utils;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Date;

import org.junit.Test;

public class DateTimeUtilsTest
{
	@Test
	public void testDayOfYear() throws IOException
	{
		Date date = Convert.toDate("01/02/2012");
		int doy = DateTimeUtils.dayOfYear(date);
		assertEquals(2, doy);
		
		date = Convert.toDate("02/02/2012");
		doy = DateTimeUtils.dayOfYear(date);
		assertEquals(33, doy);
	}

	@Test
	public void testMonth()
	{
		assertEquals(DateTimeUtils.month("January"), 1);
		assertEquals(DateTimeUtils.month("feb"), 2);
		assertEquals(DateTimeUtils.month("lkasdflj"), -1);
	}
}
