package com.cffreedom.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class CacherTest 
{
	@Test
	public void typeTest()
	{
		Cacher cache = new Cacher(15);
		List<String> list = new ArrayList<String>();
		list.add("hi");
		list.add("bye");
		cache.put("myList", list);
		assertTrue(cache.get("myList") instanceof List);
		
		Cacher cache2 = new Cacher(15);
		Map<String, Calendar> map = new HashMap<String, Calendar>();
		cache2.put("myMap", map);
		assertTrue(cache2.get("myMap") instanceof Map);
	}
	
	@Test
	public void sizeTest()
	{
		Cacher cache = new Cacher(15);
		List<String> list = new ArrayList<String>();
		list.add("hi");
		list.add("bye");
		cache.put("myList", list);
		
		List<String> returnedList = cache.get("myList");
		assertEquals(2, returnedList.size());
	}
	
	@Test
	public void valueTest()
	{
		Cacher cache = new Cacher(15);
		List<String> list = new ArrayList<String>();
		list.add("hi");
		cache.put("myList", list);
		
		List<String> returnedList = cache.get("myList");
		assertEquals("hi", returnedList.get(0));
	}
	
	@Test
	public void containsTest()
	{
		Cacher cache = new Cacher(15);
		cache.put("val1", "hi");
		cache.put("val2", "bye");
		
		assertTrue(cache.containsKey("val1"));
		assertFalse(cache.containsKey("junk1"));
		
		assertNotNull(cache.get("val2"));
		assertNull(cache.get("junk2"));
	}
	
	/*
	@Test
	public void toListTest() {
		Cacher cache = new Cacher(15);
		cache.put("val1", Convert.toBigDecimal(17));
		cache.put("val2", Convert.toBigDecimal(7.6));
		
		assertEquals(cache.toList().size(), 2);
		
		for (BigDecimal val : cache.toList<BigDecimal>()) {
			
		}
	}
	*/
}
