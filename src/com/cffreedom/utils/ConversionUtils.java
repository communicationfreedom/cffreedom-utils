package com.cffreedom.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.codec.binary.Base64;

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
 * 2013-04-24 	markjacobsen.net 	Added toString(InputStream)
 */
public class ConversionUtils
{
	public static String toBase64(String val)
	{
		return new String(Base64.encodeBase64(val.getBytes()));
	}

	public static String toMd5(String val) throws NoSuchAlgorithmException
	{
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(val.getBytes());

		byte byteData[] = md.digest();

		// convert the byte to hex format method 1
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {
			sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16)
					.substring(1));
		}

		System.out.println("Digest(in hex format):: " + sb.toString());

		// convert the byte to hex format method 2
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {
			String hex = Integer.toHexString(0xff & byteData[i]);
			if (hex.length() == 1)
				hexString.append('0');
			hexString.append(hex);
		}

		return hexString.toString();
	}

	public static String toString(Long val)
	{
		return String.valueOf(val.longValue());
	}
	
	public static String toString(int val)
    {
        return (new Integer(val)).toString();
    }
	
	public static String toString(byte val)
	{
		return Byte.toString(val);
	}
	
	public static String toString(byte[] val)
	{
		return new String(val);
	}
	
	public static String toString(char val)
	{
		return Character.toString(val);
	}
	
	public static String toString(char[] val)
	{
		return new String(val);
	}
	
	public static String toString(InputStream val) throws IOException
	{
		StringBuilder sb = new StringBuilder();
		String line = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(val));
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		return sb.toString();
	}
	
	//------------------------------------------------------------------
	// Int methods
	public static int toInt(String a_sVal)
	{
		return (new Integer(a_sVal)).intValue();	
	}
	
	public static int toInt(long a_nVal)
	{
		return (new Long(a_nVal)).intValue();	
	}
	
	public static int toInt(double a_nVal)
	{
		return (new Double(a_nVal)).intValue();	
	}
	
	public static int toInt(boolean a_bVal)
	{
	    if (a_bVal == true)
	    {
	        return 1;
	    }else{
	        return 0;
	    }
	}
	
	public static int[] toIntArray(String[] a_sVal)
	{
		int[] l_nArray = new int[a_sVal.length];
		for (int x = 0; x < a_sVal.length; x++)
		{
			l_nArray[x] = toInt(a_sVal[x]);
		}
		return l_nArray;	
	}

    //------------------------------------------------------------------
    // Calendar methods
    public static Calendar toCalendar(String a_sVal, String a_sMask) throws ParseException
    {
                Calendar cal = Calendar.getInstance();
                cal.setTime(toDate(a_sVal, a_sMask));
                return cal;
    }
   
    public static Calendar toCalendar(java.util.Date a_dVal)
    {
                Calendar cal = Calendar.getInstance();
                cal.setTime(a_dVal);
                return cal;
    }
   
    //------------------------------------------------------------------
    // Date/Time methods           
    public static java.util.Date toDate(Calendar a_oVal)
    {
        try {
            return a_oVal.getTime();
        } catch (Exception e) { return null; }
    }
   
    public static java.util.Date toDate(java.sql.Date a_oVal)
    {
        try {
            return (java.util.Date)a_oVal;
        } catch (Exception e) { return null; }
    }
   
    public static java.util.Date toDate(String a_sVal)
    {
    	return toDate(a_sVal, DateTimeUtils.MASK_DEFAULT_DATE);
    }
   
    public static java.util.Date toDate(String a_sVal, String a_sMask)
    {
        String l_sVal = a_sVal;
       
        try
        {
                    if (a_sMask.compareTo(DateTimeUtils.MASK_DEFAULT_DATE) == 0)
                    {
                                String[] l_oParts = a_sVal.split("/"); // split(a_sVal, "/");
                                if ( (l_oParts.length == 3) && (l_oParts[2].length() != 4) )
                                {
                                    String l_sYear = toString(DateTimeUtils.year(new java.util.Date()));
                                    l_sYear = l_sYear.substring(0, 4 - l_oParts[2].length());
                                    l_sYear = l_sYear + l_oParts[2];
                                    l_sVal = l_oParts[0] + "/" + l_oParts[1] + "/" + l_sYear;
                                }
                    }
                   
                            DateFormat df = new SimpleDateFormat(a_sMask);
                            return df.parse(l_sVal);
        }
        catch (Exception e) { return null; }
    }
    
    public static java.util.Date toDateNoTime(java.util.Date val)
    {
    	return toDate(DateTimeUtils.dateFormat(val));
    }
   
    public static java.util.Date[] toDateArray(String[] a_sVal) throws ParseException
    {
                return toDateArray(a_sVal, DateTimeUtils.MASK_DEFAULT_DATE);
    }
               
    public static java.util.Date[] toDateArray(String[] a_sVal, String a_sMask) throws ParseException
    {
                java.util.Date[] l_dArray = new java.util.Date[a_sVal.length];
                for (int x = 0; x < a_sVal.length; x++)
                {
                            l_dArray[x] = toDate(a_sVal[x], a_sMask);
                }
                return l_dArray;         
    }
   
    @SuppressWarnings("deprecation")
	public static java.sql.Date toSqlDate(String a_sVal)
    {
        try {
            return new java.sql.Date(java.sql.Date.parse(a_sVal));
        } catch (Exception e) { return null; }
    }
   
    public static java.sql.Date toSqlDate(java.util.Date a_dVal)
    {
        try {
            return (java.sql.Date)a_dVal;
        } catch (Exception e) { return null; }
    }
   
    public static java.util.Date toTime(String a_sVal) throws Exception
    {
        try {
                            DateFormat df = new SimpleDateFormat(DateTimeUtils.MASK_TIME_12_HOUR);
                            return df.parse(a_sVal);
        } catch (Exception e) { return null; }
    }
   
    /***
    * This function converts a standard java.util.Date to a
     * DB2 Formated date.
     * @param inDate java.util.Date to convert to DB2 date string
    * @return DB2 date string
    */
    public static String toDB2DateString(java.util.Date inDate){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(inDate);
    }
   
    /***
    * This function converts a standard java.sql.Date to a
     * DB2 Formated date.
     * @param inDate java.sql.Date to convert to DB2 date string
    * @return DB2 date string
    */       
    public static String toDB2DateString(java.sql.Date inDate){
       
        return inDate.toString();
    }          
    /***
    * This function converts a standard java.util.Date to a
     * DB2 Formated date.
     * @param inDate java.util.Date to convert to DB2 date string
    * @return DB2 date string
    * @throws ParseException
    */
    public static java.sql.Date toDB2Date(String inDate) throws ParseException{
        DateFormat df;
        if (inDate.trim().charAt(4)=='-'){
            df = new SimpleDateFormat("yyyy-MM-dd");
        } else {
            df = new SimpleDateFormat("MM/dd/yyyy");
        }
       
        java.util.Date tempdate = df.parse(inDate);
        java.sql.Date db2date = (java.sql.Date)tempdate;

        return db2date;
    }
   
    /**
    * This function converts a standard java.util.Date to a
    * java.sql.Timestamp suitable for a db TIMESTAMP or DATETIME
     * @param a_dVal java.util.Date to convert to a Timestamp object
    * @return java.sql.Timestamp object
    */
    public static java.sql.Timestamp toTimestamp(java.util.Date a_dVal)
    {
        return new java.sql.Timestamp( a_dVal.getTime() );
    }

}
