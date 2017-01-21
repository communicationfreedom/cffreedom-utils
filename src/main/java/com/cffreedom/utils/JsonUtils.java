package com.cffreedom.utils;

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
	/**
	 * Given JSON text, return a JSONObject
	 * @param jsonText
	 * @return
	 * @throws ParseException
	 */
	public static JSONObject getJsonObject(String jsonText)	throws ParseException
	{
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(jsonText);
		JSONObject jsonObj = (JSONObject)obj;
		return jsonObj;
	}

	/**
	 * Given JSON text, return a JSONArray
	 * @param jsonText
	 * @return
	 * @throws ParseException
	 */
	public static JSONArray getJsonArray(String jsonText) throws ParseException
	{
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(jsonText);
		JSONArray jsonArray = (JSONArray)obj;
		return jsonArray;
	}

	/**
	 * Given a JSONObject, return a JSONObject in the object for the key specified
	 * @param jsonObj
	 * @param key
	 * @return
	 */
	public static JSONObject getJsonObject(JSONObject jsonObj, String key)
	{
		return (JSONObject)jsonObj.get(key);
	}

	/**
	 * Given a JSONObject, return a JSONArray in the object for the key specified
	 * @param jsonObj
	 * @param key
	 * @return
	 */
	public static JSONArray getJsonArray(JSONObject jsonObj, String key)
	{
		return (JSONArray)jsonObj.get(key);
	}

	/**
	 * Given a JSONObject, return a String in the object for the key specified
	 * @param jsonObj
	 * @param key
	 * @return
	 */
	public static String getString(JSONObject jsonObj, String key)
	{
		return (String)jsonObj.get(key);
	}

	/**
	 * Given a JSONObject, return a Long in the object for the key specified
	 * @param jsonObj
	 * @param key
	 * @return
	 */
	public static Long getLong(JSONObject jsonObj, String key)
	{
		return (Long)jsonObj.get(key);
	}
	
	/**
	 * Given a JSONObject, return a boolean in the object for the key specified
	 * @param jsonObj
	 * @param key
	 * @return
	 */
	public static boolean getBoolean(JSONObject jsonObj, String key)
	{
		return (boolean)jsonObj.get(key);
	}
	
	/**
	 * Given a JSONObject, return a Map in the object for the key specified
	 * @param jsonObj
	 * @param key
	 * @return
	 */
	public static Map getMap(JSONObject jsonObj, String key) {
		return (Map)jsonObj.get(key);
	}
	
	/**
	 * Given a JSONObject, return it as a Map
	 * @param jsonObj
	 * @return
	 */
	public static Map getMap(JSONObject jsonObj) {
		return (Map)jsonObj;
	}
	
	/**
	 * Given a JSONObject, return a List in the object for the key specified
	 * @param jsonObj
	 * @param key
	 * @return
	 */
	public static List getList(JSONObject jsonObj, String key) {
		return (List)jsonObj.get(key);
	}
	
	/**
	 * Given a JSONArray, return it as a List
	 * @param jsonObj
	 * @return
	 */
	public static List getList(JSONArray jsonObj) {
		return (List)jsonObj;
	}
	
	/**
	 * Return a JSON representation of a key value pair
	 * @param key
	 * @param value
	 * @return
	 */
	public static String getJson(String key, String value) {		
		Map obj = new HashMap();
		obj.put(key, value);
		return getJson(obj);
	}
	
	/**
	 * Return a JSON representation of a Map
	 * @param obj
	 * @return
	 */
	public static String getJson(Map obj) {
		return JSONValue.toJSONString(obj);
	}
	
	/**
	 * Return a JSON representation of a List
	 * @param obj
	 * @return
	 */
	public static String getJson(List obj) {
		return JSONValue.toJSONString(obj);
	}
}
