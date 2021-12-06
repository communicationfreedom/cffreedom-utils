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
	public void testTimePieces() {
		Calendar test = DateTimeUtils.setDateTime(2015, 10, 3, 15, 22, 16, 0);
		assertEquals(DateTimeUtils.hour(test), 3);
		assertEquals(DateTimeUtils.hour24(test), 15);
		assertEquals(DateTimeUtils.minute(test), 22);
		assertEquals(DateTimeUtils.second(test), 16);
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
	
	@Test
	public void testStandardizeDate() {
		Calendar one = DateTimeUtils.setDateTime(2015, 10, 3, 5, 23, 9);
		Calendar two = DateTimeUtils.standardizeDate("10/03/2015 05:23:09");
		assertTrue(DateTimeUtils.datesEqual(one, two, false));
		
		two = DateTimeUtils.standardizeDate("2015-10-03 05:23:09");
		assertTrue(DateTimeUtils.datesEqual(one, two, false));
		
		two = DateTimeUtils.standardizeDate("2015/10/03");
		assertTrue(DateTimeUtils.datesEqual(one, two, true));
		
		two = DateTimeUtils.standardizeDate("2015-10-03");
		assertTrue(DateTimeUtils.datesEqual(one, two, true));
		
		two = DateTimeUtils.standardizeDate("10/03/2015");
		assertTrue(DateTimeUtils.datesEqual(one, two, true));
		
		two = DateTimeUtils.standardizeDate("2015/10/03");
		assertTrue(DateTimeUtils.datesEqual(one, two, true));
	}
	
	@Test
	public void testDateDiff() {
		assertEquals(4, DateTimeUtils.dateDiff(DateTimeUtils.setDate(2014, 9, 1), DateTimeUtils.setDate(2018, 10, 1), DateTimeUtils.DATE_PART_YEAR));
		assertEquals(0, DateTimeUtils.dateDiff(DateTimeUtils.setDate(2014, 9, 1), DateTimeUtils.setDate(2015, 8, 30), DateTimeUtils.DATE_PART_YEAR));
		assertEquals(1, DateTimeUtils.dateDiff(DateTimeUtils.setDate(2014, 9, 1), DateTimeUtils.setDate(2015, 9, 2), DateTimeUtils.DATE_PART_YEAR));
	}
	
	@Test
	public void testGmtUtc() {
		Calendar gmt = DateTimeUtils.standardizeDate("2021-12-05T18:00Z");
		assertEquals(6, gmt.get(Calendar.HOUR));
		Calendar local = DateTimeUtils.gmtToLocal(gmt);
		assertEquals(1, local.get(Calendar.HOUR));
	}
}
