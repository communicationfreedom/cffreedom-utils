package com.cffreedom.utils;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateTimeUtilsTest
{
	private static final Logger LOG = LoggerFactory.getLogger(DateTimeUtilsTest.class);
	
	@Test
	public void testDayOfYear() throws IOException {
		Date date = Convert.toDate("01/02/2012");
		int doy = DateTimeUtils.dayOfYear(date);
		assertEquals(2, doy);
		
		date = Convert.toDate("02/02/2012");
		doy = DateTimeUtils.dayOfYear(date);
		assertEquals(33, doy);
	}

	@Test
	public void testMonth() {
		assertEquals(DateTimeUtils.month("January"), 1);
		assertEquals(DateTimeUtils.month("feb"), 2);
		assertEquals(DateTimeUtils.month("lkasdflj"), -1);
	}
	
	@Test
	public void testSetValues() {
		Calendar today = Calendar.getInstance();
		Calendar noTime = DateTimeUtils.stripTime(today);
		//LOG.debug("{}-{}", today.get(Calendar.MONTH), noTime.get(Calendar.MONTH));
		assertEquals(today.get(Calendar.MONTH), noTime.get(Calendar.MONTH));
	}
	
	@Test
	public void testEquality() {
		Calendar one = DateTimeUtils.setDateTime(2015, 10, 3, 5, 23, 9, 2);
		Calendar two = DateTimeUtils.setDateTime(2015, 10, 7, 15, 32, 17, 2);
		assertTrue(DateTimeUtils.datesEqual(DateTimeUtils.dateAdd(one, 4, DateTimeUtils.DATE_PART_DAY), two, true));
		assertFalse(DateTimeUtils.datesEqual(DateTimeUtils.dateAdd(one, 4, DateTimeUtils.DATE_PART_DAY), two, false));
	}
}
