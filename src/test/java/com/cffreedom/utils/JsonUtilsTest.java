package com.cffreedom.utils;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Test;

import com.cffreedom.beans.Container;

public class JsonUtilsTest
{
	public void testString()
	{
		assertEquals("{\"var\":\"val\"}", JsonUtils.getJson("var", "val"));
	}
	
	@Test
	public void testList()
	{
		List<String> obj = new ArrayList<String>();
		obj.add("peach");
		obj.add("pear");
		obj.add("banana");
		assertEquals("[\"peach\",\"pear\",\"banana\"]", JsonUtils.getJson(obj));
	}
	
	@Test
	public void testMap()
	{
		Map<String, String> obj = new LinkedHashMap<String, String>();
		obj.put("work", "CF");
		obj.put("name", "Mark");
		assertEquals("{\"work\":\"CF\",\"name\":\"Mark\"}", JsonUtils.getJson(obj));
	}

	@Test
	public void testGetList() throws ParseException
	{
		List<String> obj = new ArrayList<String>();
		obj.add("peach");
		obj.add("pear");
		obj.add("banana");
		JSONArray jsonArray = JsonUtils.getJsonArray(JsonUtils.getJson(obj));
		List ret = JsonUtils.getList(jsonArray);
		assertEquals(ret.size(), 3);
		assertTrue(ret.contains("pear"));
	}
	
	@Test
	public void testGetMap() throws ParseException
	{
		Map<String, String> obj = new LinkedHashMap<String, String>();
		obj.put("work", "CF");
		obj.put("name", "Mark");
		JSONObject jsonObj = JsonUtils.getJsonObject(JsonUtils.getJson(obj));
		Map ret = JsonUtils.getMap(jsonObj);
		assertEquals(ret.size(), 2);
		assertEquals(ret.get("name"), "Mark");
	}
}
