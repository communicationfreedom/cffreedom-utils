package com.cffreedom.utils;

import java.io.IOException;
import java.util.Date;

import org.junit.Test;

import junit.framework.Assert;

public class DateTimeUtilsTest
{
	@Test
	public void testDayOfYear() throws IOException
	{
		Date date = Convert.toDate("01/02/2012");
		int doy = DateTimeUtils.dayOfYear(date);
		Assert.assertEquals(2, doy);
		
		date = Convert.toDate("02/02/2012");
		doy = DateTimeUtils.dayOfYear(date);
		Assert.assertEquals(33, doy);
	}

}
