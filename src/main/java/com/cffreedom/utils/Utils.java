package com.cffreedom.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
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
 * 2013-04-08 	markjacobsen.net 	Added JavaDoc comments
 * 2013-04-30 	markjacobsen.net 	Added longestString()
 */
public class Utils
{
	/**
     * @param val The number to evaluate
     * @return True if we can convert the value to an integer, otherwise false
     */
	public static boolean isInt(String val)
	{
		try
		{
			ConversionUtils.toInt(val);
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}
	
	/**
     * @param val The number to evaluate
     * @return True if all characters in the string are digits, otherwise false
     */
    public static boolean isNumeric(String val)
    {
        if (val.length() == 0)
        {
            return false;
        }
       
        for (int x = 0; x < val.length(); x++)
        {
            if (Character.isDigit(val.charAt(x)) == false)
            {
                return false;
            }
      }
        return true;
    }
   
    /**
     * @param val The date to evaluate
     * @return True if we can convert it to a date, otherwise false
     */
    public static boolean isDate(String val)
    {
        try
        {
            ConversionUtils.toDate(val);
            return true;
        }catch (Exception e){
            return false;
        }
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
			if (isPassword == true)
			{
				enteredVal = ConversionUtils.toString(System.console().readPassword(prompt));
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
    		return ConversionUtils.toString(val.charAt(val.length() - 1));
    	}
    	else
    	{
    		return "";
    	}
    }
    
    public static int longestString(ArrayList<String> vals)
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
		return longestString(ConversionUtils.toArrayListOfStrings(vals));
	}
}
