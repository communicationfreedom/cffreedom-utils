package com.cffreedom.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

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
 */
public class Utils
{
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
	
    public static boolean isNumeric(String a_sVal)
    {
        if (a_sVal.length() == 0)
        {
            return false;
        }
       
        for (int x = 0; x < a_sVal.length(); x++)
        {
            if (Character.isDigit(a_sVal.charAt(x)) == false)
            {
                return false;
            }
      }
        return true;
    }
   
    public static boolean isDate(String a_sVal)
    {
        try
        {
            ConversionUtils.toDate(a_sVal);
            return true;
        }catch (Exception e){
            return false;
        }
    }
    
    public static void output(String val) { output(val, true); }
    public static void output(String val, boolean newline)
    {
    	if (newline == true) {
    		System.out.println(val);
    	}else{
    		System.out.print(val);
    	}
    }
    
    public static String promptBare() { return prompt(null, null, false); } 
    
    public static String promptPassword() { return promptPassword("Password"); }
    public static String promptPassword(String prompt) { return prompt(prompt, null, true); }
    
    public static String prompt(String prompt) { return prompt(prompt, null); }
    public static String prompt(String prompt, String defaultVal) { return prompt(prompt, defaultVal, false); }
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
}
