package com.cffreedom.utils;

import java.util.Calendar;
import java.util.Date;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

/**
 * Original Class: com.cffreedom.utils.FormatUtils
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
 * 2013-04-27 	markjacobsen.net 	Added pad()
 * 2013-04-30 	markjacobsne.net 	Fixed repeatString()
 * 2013-06-12 	markjacobsen.net 	Consolidated date masks here
 * 2013-10-05 	markjacobsen.net 	Fixed formatBigDecimal()
 * 2014-09-16 	MarkJacobsen.net 	Changed format of MASK_FILE_TIMESTAMP
 * 2014-09-24 	MarkJacobsen.net 	stripNonNumeric() will return null if the input is null
 * 2014-10-13 	MarkJacobsen.net 	Added maxLenString()
 * 2014-12-20 	MarkJacobsen.net 	Deprecated class in favor of new Format class
 */
@Deprecated
public class FormatUtils
{
	public final static String PHONE_10 = "PHONE_10";
	public final static String PHONE_DASH = "PHONE_DASH";
	public final static String PHONE_DOT = "PHONE_DOT";
	public final static String PHONE_INT = "PHONE_INT";
	
	public static final String MASK_DEFAULT_DATE = "MM/dd/yyyy";
    public static final String MASK_FULL_DATE_TIME = "MM/dd/yyyy hh:mm a";
    public static final String MASK_FULL_TIMESTAMP = "yyyy-MM-dd HH:mm:ss";
    public static final String MASK_XML_TIMESTAMP = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String MASK_YYYYMM = "yyyyMM";
    public static final String MASK_YYYYMMDD = "yyyyMMdd";
    public static final String MASK_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public static final String MASK_TIME_12_HOUR = "h:mm a";
    public static final String MASK_TIME_24_HOUR = "H:mm";
    public static final String MASK_FILE_DATESTAMP = "yyyy-MM-dd";
    public static final String MASK_FILE_TIMESTAMP = "yyyy-MM-dd_HH-mm-ss";
    public static final String MASK_DB2_TIMESTAMP = MASK_FULL_TIMESTAMP;
    public static final String MASK_MMDDYY = "MMddyy";
	
    @Deprecated
	public static String formatDate(String format, Date date)
	{
		return Format.date(format, date);
	}
	
    @Deprecated
	public static String formatDate(String format, Calendar date)
	{
		return Format.date(format, date);
	}
	
    @Deprecated
	public static String formatPhoneNumber(String format, String phoneNumber)
	{
		return Format.phoneNumber(format, phoneNumber);
	}

    @Deprecated
	public static String formatBigDecimal(BigDecimal n, int decimalPlaces)
	{
		return Format.number(n, decimalPlaces);
	}

    @Deprecated
	public static String formatBigDecimal(BigDecimal n, int decimalPlaces, boolean includeThousandsSeparator)
	{
		return Format.number(n, decimalPlaces, includeThousandsSeparator);
	}

    @Deprecated
	public static String repeatString(String repeatThis, int repeatTimes)
	{
		return Format.repeatString(repeatThis, repeatTimes);
	}

    @Deprecated
	public static String upperCaseFirstChar(String value)
	{
		return Format.upperCaseFirstChar(value);
	}

    @Deprecated
	public static String stripNonNumeric(String source)
	{
		return Format.stripNonNumeric(source);
	}

    @Deprecated
	public static String stripCrLf(String source)
	{
		return Format.stripCrLf(source);
	}
	
    @Deprecated
	public static String stripExtraSpaces(String source)
	{
		return Format.stripExtraSpaces(source);
	}
	
    @Deprecated
	public static String maxLenString(String val, int maxLen)
	{
		return Format.maxLenString(val, maxLen);
	}

    @Deprecated
	public static String replace(String source, String find, String replace)
	{
		return Format.replace(source, find, replace);
	}

    @Deprecated
	public static String replace(String source, String find, String replace, boolean caseSensative)
	{
		return Format.replace(source, find, replace, caseSensative);
	}

    @Deprecated
	public static String replaceSpan(String source, String findStart, String findEnd, String replace)
	{
		return Format.replaceSpan(source, findStart, findEnd, replace);
	}

    @Deprecated
	public static String replaceSpan(String source, String findStart, String findEnd, String replace, boolean caseSensative)
	{
		return Format.replaceSpan(source, findStart, findEnd, replace, caseSensative);
	}
	
    @Deprecated
	public static String pad(String val, int totalChars) { return Format.pad(val, totalChars, " "); }
	
    @Deprecated
    public static String pad(String val, int totalChars, String padChar) { return Format.pad(val, totalChars, padChar, true); }
    
    @Deprecated
	public static String pad(String val, int totalChars, String padChar, boolean padRight)
	{
		return Format.pad(val, totalChars, padChar, padRight);
	}
}
