package com.cffreedom.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cffreedom.exceptions.InfrastructureException;
import com.cffreedom.utils.file.FileUtils;

/**
 * Original Class: com.cffreedom.utils.Utils
 * @author markjacobsen.net
 * Copyright: Communication Freedom, LLC - http://www.communicationfreedom.com
 * 
 * Free to use, modify, redistribute.  Must keep full class header including 
 * copyright and note your modifications.
 * 
 * If this helped you out or saved you time, please consider...
 * 1) Donating: http://www.communicationfreedom.com/go/donate/
 * 2) Shoutout on twitter: @MarkJacobsen or @cffreedom
 * 3) Linking to: http://markjacobsen.net
 * 
 * Changes:
 * 2013-04-08 	markjacobsen.net 	Added JavaDoc comments
 * 2013-04-30 	markjacobsen.net 	Added longestString()
 * 2013-05-21 	markjacobsen.net 	Added appendToStringArray() and appendToIntArray()
 * 2013-07-19	markjacobsen.net 	Added hasLength()
 * 2013-09-15 	markjacobsen.net 	Added getRandomString()
 * 2014-10-22 	MarkJacobsen.net 	Added getProperties()
 * 2015-03-29 	MarkJacobsen.net 	Added replaceLast()
 * 2017-06-30	MarkJacobsen.net 	Added isNull(x, x) functions
 */
public class Utils
{
	private static final Logger logger = LoggerFactory.getLogger("com.cffreedom.utils.Utils");
	private static final String ALPHA_NUM = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	private static Random random = new Random();
	
	@Deprecated
	public static boolean isInt(String val) {
		return isInt(val, true);
	}
	
	/**
	 * Check if a string is an integer
	 * @param val Value to check
	 * @param liberalParse true to consider thousands separators ok, false to ensure strict parsing (i.e. 1056 but not 1,056)
	 * @return
	 */
	public static boolean isInt(String val, boolean liberalParse) {
		boolean is = false;
    	if (hasLength(val) == true) {
			try {
				Convert.toInt(val, liberalParse);
				is = true;
			}
			catch (Exception e){}
    	}
    	return is;
	}
	
	/**
	 * Determine if a string is a list of integers
	 * @param val
	 * @param delimiter
	 * @param trimElements
	 * @return
	 */
	public static boolean isIntList(String val, String delimiter, boolean trimElements) {
		boolean is = false;
		if (Utils.hasLength(val)) {
			try {
				String[] strArray = val.split(delimiter);
				
				if (strArray.length > 0) {
					boolean allInts = true;
					for (String tmp : strArray) {
						if (trimElements) {
							tmp = tmp.trim();
						}
						if (!isInt(tmp)) {
							allInts = false;
							break;
						}
					}
					is = allInts;
				}
			} catch (Exception e) {}
		}
		return is;
	}
	
	/**
     * @param val The number to evaluate
     * @return True if all characters in the string are digits, otherwise false
     */
    public static boolean isNumeric(String val) {
        if (hasLength(val) == false) {
            return false;
        }
       
        for (int x = 0; x < val.length(); x++) {
            if (Character.isDigit(val.charAt(x)) == false) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isBigDecimal(String val) {
    	boolean is = false;
    	try {
    		Convert.toBigDecimal(val);
    		is = true;
    	} catch (Exception e) {
    		is = false;
    	}
    	return is;
    }
    
    public static boolean isBigInteger(String val) {
    	boolean is = false;
    	try {
    		Convert.toBigInteger(val);
    		is = true;
    	} catch (Exception e) {
    		is = false;
    	}
    	return is;
    }
    
    /**
     * @param val The value to evaluate
     * @return true if a phone number, false otherwise
     */
    public static boolean isPhoneNumber(String val) {
    	boolean result = false;
    	if (Utils.hasLength(val) == true) {
	    	String tmp = Format.phoneNumber(Format.PHONE_INT, val);
	    	String numbersOnly = Format.stripNonNumeric(tmp);
	    	if ((tmp.startsWith("+") == true) && (tmp.length() >= 12) && (numbersOnly.length() >= 11)) {
	    		result = true;
	    	}
    	}
    	return result;
    }
    
    /**
     * @param val The value to evaluate
     * @return true if an email address, false otherwise
     */
    public static boolean isEmail(String val) {
    	boolean result = false;
    	if 	(
    		(val != null) &&
    		(val.length() > 7) && 
    		val.contains("@") && 
    		val.contains(".") &&
			(val.indexOf("@") < val.lastIndexOf("."))
			) {
			result = true;
		}
    	return result;
    }
   
    /**
     * @param val The date to evaluate
     * @return True if we can convert it to a date, otherwise false
     */
    public static boolean isDate(String val) {
    	boolean isDate = false;
    	if (hasLength(val) == true) {
	        try {
	        	if (Convert.toCalendar(val) != null) {
	            	isDate = true;
	            }
	        } catch (Exception e) {}
    	}
        return isDate;
    }
    
    public static Object isNull(Object value, Object defVal) {
    	if (value == null) {
    		value = defVal;
    	}
    	return value;
    }
    
    public static String isNull(String value, String defVal) {
    	if (value == null) {
    		value = defVal;
    	}
    	return value;
    }
    
    public static BigDecimal isNull(BigDecimal value, BigDecimal defVal) {
    	if (value == null) {
    		value = defVal;
    	}
    	return value;
    }
    
    public static BigInteger isNull(BigInteger value, BigInteger defVal) {
    	if (value == null) {
    		value = defVal;
    	}
    	return value;
    }
    
    public static Boolean isNull(Boolean value, Boolean defVal) {
    	if (value == null) {
    		value = defVal;
    	}
    	return value;
    }
    
    public static Integer isNull(Integer value, Integer defVal) {
    	if (value == null) {
    		value = defVal;
    	}
    	return value;
    }
    
    public static Double isNull(Double value, Double defVal) {
    	if (value == null) {
    		value = defVal;
    	}
    	return value;
    }
    
    public static Long isNull(Long value, Long defVal) {
    	if (value == null) {
    		value = defVal;
    	}
    	return value;
    }
    
    public static Calendar isNull(Calendar value, Calendar defVal) {
    	if (value == null) {
    		value = defVal;
    	}
    	return value;
    }
    
    public static Date isNull(Date value, Date defVal) {
    	if (value == null) {
    		value = defVal;
    	}
    	return value;
    }
    
    /**
     * Determine if a string has length and is not null
     * @param val The value to evaluate
     * @return True if it is not null and has length, false otherwise
     */
    public static boolean hasLength(String val) {
    	boolean hasLen = false;
    	try {
    		if ((val != null) && (val.length() > 0)){
    			hasLen = true;
    		}
    	}catch (NullPointerException e){
    		// Do nothing
    	}
    	return hasLen;
    }
    
    /**
     * Determine if a collection contains elements and is not null
     * @param val The collection to evaluate
     * @return True if it is not null and has contains 1 or more elements, false otherwise
     */
    public static boolean hasLength(Collection<?> val) {
    	boolean hasLen = false;
    	try {
    		if ((val != null) && (val.isEmpty() == false)){
    			hasLen = true;
    		}
    	}catch (NullPointerException e){
    		// Do nothing
    	}
    	return hasLen;
    }
    
    /**
     * Determine if a map contains elements and is not null
     * @param val The map to evaluate
     * @return True if it is not null and has contains 1 or more elements, false otherwise
     */
    public static boolean hasLength(Map<?, ?> val) {
    	boolean hasLen = false;
    	try {
    		if ((val != null) && (val.isEmpty() == false)){
    			hasLen = true;
    		}
    	}catch (NullPointerException e){
    		// Do nothing
    	}
    	return hasLen;
    }
    
    /**
     * This is really just a wrapper to System.out.println and System.out.print - just shorter Utils.output("something")
     * @param val What to output
     * @param newline Include a newline at the end of the output
     */
    public static void output(String val, boolean newline)
    {
    	if (newline == true) {
    		System.out.println(val);
    	}else{
    		System.out.print(val);
    	}
    }
    public static void output(String val) { output(val, true); }
    
    /**
     * Convenience method for prompting for input without any prompt shown on screen.
     * Useful for continuation of input until a special character is read.
     * @return The value the user types in
     */
    public static String promptBare() { return prompt(null, null, false); } 
    
    /**
     * Convenience method for prompting for a password
     * @param prompt What to prompt the user with (ex: "Password:")
     * @return The value the user types in
     */
    public static String promptPassword(String prompt) { return prompt(prompt, null, true); }
    public static String promptPassword() { return promptPassword("Password"); }
    
    /**
     * @param prompt What to prompt the user with (ex: "Username:")
     * @param defaultVal The value to use if the user just presses enter (ex: "Username [jdoe]:")
     * @param isPassword If true, don't show the characters the user types
     * @return The value the user types in
     */
    public static String prompt(String prompt, String defaultVal, boolean isPassword)
	{
    	String enteredVal = null;
    	
    	if (prompt == null)
    	{
    		prompt = "";
    	}
    	else
    	{
	    	if (defaultVal != null) { prompt += " [" + defaultVal + "]"; }
			prompt += ": ";
    	}
    	
		// When running inside of Eclipse you won't be able to get access to the Console object
		if (System.console() != null)
		{
			logger.trace("Got a console object");
			if (isPassword == true)
			{
				enteredVal = Convert.toString(System.console().readPassword(prompt));
			}
			else
			{
				enteredVal = System.console().readLine(prompt);
			}
		}
		else
		{
			System.out.print(prompt);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

            try
            {
            	enteredVal = bufferedReader.readLine();
            }
            catch (Exception e) { enteredVal = null; }
		}
		
		if ((enteredVal == null) || (enteredVal.trim().length() == 0))
		{
			enteredVal = defaultVal;
		}
		
		return enteredVal;
	}
    public static String prompt(String prompt) { return prompt(prompt, null); }
    public static String prompt(String prompt, String defaultVal) { return prompt(prompt, defaultVal, false); }
    
    /**
     * @param val String to get the last character from.
     * @return The last character (as a String) in the passed in string.
     */
    public static String lastChar(String val)
    {
    	if (val.length() > 0)
    	{
    		return Convert.toString(val.charAt(val.length() - 1));
    	}
    	else
    	{
    		return "";
    	}
    }
    
    public static int longestString(List<String> vals)
    {
    	int longest = 0;
    	for (String val : vals)
    	{
    		if (val.length() > longest) { longest = val.length(); }
    	}
    	return longest;
    }

	public static int longestString(String[] vals)
	{
		return longestString(Convert.toListOfStrings(vals));
	}
	
	public static String[] appendToStringArray(String[] array, String val)
	{
		String[] newArray = null;
		
		if (array == null)
		{
			newArray = new String[1];
			newArray[0] = val;
		}
		else
		{
			newArray = Arrays.copyOf(array, array.length + 1);
			newArray[newArray.length - 1] = val;
		}
		
		return newArray;
	}
	
	public static int[] appendToIntArray(int[] array, int val)
	{
		int[] newArray = null;
		
		if (array == null)
		{
			newArray = new int[1];
			newArray[0] = val;
		}
		else
		{
			newArray = Arrays.copyOf(array, array.length + 1);
			newArray[newArray.length - 1] = val;
		}
		
		return newArray;
	}
	
	public static String getRandomString(int len)
	{
		String ret = "";
		for (int x = 0; x < len; x++)
		{
			ret += ALPHA_NUM.charAt(random.nextInt(ALPHA_NUM.length()));
		}
		return ret;
	}
	
	/**
	 * Replace the last occurrence of a string
	 * @param string String containing the value
	 * @param from Value to find
	 * @param to Value to change find to
	 * @return
	 */
	public static String replaceLast(String string, String from, String to) 
	{
	     int lastIndex = string.lastIndexOf(from);
	     if (lastIndex < 0) { return string; }
	     String tail = string.substring(lastIndex).replaceFirst(from, to);
	     return string.substring(0, lastIndex) + tail;
	}
	
	/**
	 * Allows you to get properties from a file on the file system or in the classpath. 
	 * Will use file system first. 
	 * @param file Full path to a properties file or the name of a properties file to pull off the classpath
	 * @return The properties from the file
	 * @throws InfrastructureException 
	 */
	public static Properties getProperties(String file) throws InfrastructureException
	{
		InputStream inputStream = null;
		Properties props = new Properties();
		
		try
		{
			if (FileUtils.fileExists(file) == true)
			{
				logger.info("Loading from passed in file: {}", file);
				inputStream = new FileInputStream(file);
			}
			else
			{
				logger.info("Attempting to find file on classpath: {}", file);
				inputStream = Utils.class.getClassLoader().getResourceAsStream(file);
			}
			
			if (inputStream == null)
			{
				throw new InfrastructureException("Properties file not found: "+file);
			}
			else
			{
				logger.debug("Loading property file");
				
				props.load(inputStream);
				inputStream.close();
			}
		}
		catch (IOException e)
		{
			throw new InfrastructureException("Error getting properties from: "+file, e);
		}
		
		return props;
	}
	
	/**
	 * If val is null, return defaultVal. If not null, just return val
	 * @param val The value to check for null
	 * @param defaultVal The value to replace val with if it is null
	 * @return
	 */
	@Deprecated
	public static String replaceNull(String val, String defaultVal) {
		if (val == null) {
			val = defaultVal;
		}
		return val;
	}
	
	/**
	 * If val is null, return defaultVal. If not null, just return val
	 * @param val The value to check for null
	 * @param defaultVal The value to replace val with if it is null
	 * @return
	 */
	@Deprecated
	public static BigDecimal replaceNull(BigDecimal val, BigDecimal defaultVal) {
		if (val == null) {
			val = defaultVal;
		}
		return val;
	}
	
	/**
	 * If val is null, return defaultVal. If not null, just return val
	 * @param val The value to check for null
	 * @param defaultVal The value to replace val with if it is null
	 * @return
	 */
	@Deprecated
	public static Integer replaceNull(Integer val, Integer defaultVal) {
		if (val == null) {
			val = defaultVal;
		}
		return val;
	}
}
