package com.cffreedom.utils;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class JsonUtilsTest
{
	public void testString()
	{
		assertEquals("{\"var\":\"val\"}", JsonUtils.getJsonString("var", "val"));
	}
	
	@Test
	public void testList()
	{
		List<String> obj = new ArrayList<String>();
		obj.add("peach");
		obj.add("pear");
		obj.add("banana");
		assertEquals("[\"peach\",\"pear\",\"banana\"]", JsonUtils.getJsonString(obj));
	}
	
	@Test
	public void testMap()
	{
		Map<String, String> obj = new LinkedHashMap<String, String>();
		obj.put("work", "CF");
		obj.put("name", "Mark");
		assertEquals("{\"work\":\"CF\",\"name\":\"Mark\"}", JsonUtils.getJsonString(obj));
	}

}
