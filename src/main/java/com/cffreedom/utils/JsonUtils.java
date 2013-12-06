package com.cffreedom.utils;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Original Class: com.cffreedom.utils.JsonUtils
 * @author markjacobsen.net (http://mjg2.net/code)
 * Copyright: Communication Freedom, LLC - http://www.communicationfreedom.com
 * 
 * Free to use, modify, redistribute.  Must keep full class header including 
 * copyright and note your modifications.
 * 
 * If this helped you out or saved you time, please consider...
 * 1) Donating: http://www.communicationfreedom.com/go/donate/
 * 2) Shoutout on twitter: @MarkJacobsen or @cffreedom
 * 3) Linking to: http://visit.markjacobsen.net
 * 
 * Changes:
 * 2013-12-01 	MarkJacobsen.net 	Added getJsonObjectBooleanVal()
 */
public class JsonUtils
{
	public static JSONObject getJsonObject(String jsonText)	throws ParseException
	{
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(jsonText);
		JSONObject jsonObj = (JSONObject)obj;
		return jsonObj;
	}

	public static JSONArray getJsonArray(String jsonText) throws ParseException
	{
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(jsonText);
		JSONArray jsonArray = (JSONArray)obj;
		return jsonArray;
	}

	public static JSONObject getJsonObject(JSONObject jsonObj, String key)
	{
		return (JSONObject)jsonObj.get(key);
	}

	public static JSONArray getJsonArray(JSONObject jsonObj, String key)
	{
		return (JSONArray)jsonObj.get(key);
	}

	public static String getJsonObjectStringVal(JSONObject jsonObj, String key)
	{
		return (String)jsonObj.get(key);
	}

	public static Long getJsonObjectLongVal(JSONObject jsonObj, String key)
	{
		return (Long)jsonObj.get(key);
	}
	
	public static boolean getJsonObjectBooleanVal(JSONObject jsonObj, String key)
	{
		return (boolean)jsonObj.get(key);
	}
	
	public static String getJsonString(String key, String value)
	{		
		Map obj = new HashMap();
		obj.put(key, value);
		return getJsonString(obj);
	}
	
	public static String getJsonString(Map obj)
	{
		return JSONValue.toJSONString(obj);
	}
	
	public static String getJsonString(List obj)
	{
		return JSONValue.toJSONString(obj);
	}
}
