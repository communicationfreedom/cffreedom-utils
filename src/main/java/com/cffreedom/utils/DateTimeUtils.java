package com.cffreedom.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

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
 * 2013-05-08 	markjacobsen.net 	Added MASK_FILE_DATESTAMP and MASK_FILE_TIMESTAMP
 * 2013-05-20 	markjacobsen.net 	dayOfWeekAsString() now returns full day (not just 3 letter abbreviation)
 * 2013-06-12 	markjacobsen.net 	Moved masks into FormatUtils for consistency and added combineDates()
 * 2013-06-13	markjacobsen.net 	Added gmtToLocal()
 */
public class DateTimeUtils extends FormatUtils
{
    public static final char DATE_PART_SECOND = 's';
    public static final char DATE_PART_MINUTE = 'n';
    public static final char DATE_PART_HOUR = 'h';
    public static final char DATE_PART_DAY = 'd';
    public static final char DATE_PART_MONTH = 'm';
    public static final char DATE_PART_YEAR = 'Y';
   
           
    @SuppressWarnings("deprecation")
	public static Date time(int a_iHour24, int a_iMinute)
    {
        Date l_dVal = new Date();
        l_dVal.setHours(a_iHour24);
        l_dVal.setMinutes(a_iMinute);
       
        return l_dVal;
    }
   
    public static int second(Calendar a_oDate)
    {
    	return a_oDate.get(Calendar.SECOND);
    }
           
    public static int second(Date a_oDate)
    {
    	return second(ConversionUtils.toCalendar(a_oDate));
    }
           
    public static int minute(Calendar a_oDate)
    {
    	return a_oDate.get(Calendar.MINUTE);
    }
           
    public static int minute(Date a_oDate)
    {
    	return minute(ConversionUtils.toCalendar(a_oDate));
    }
           
    public static int hour24(Calendar a_oDate)
    {
    	return a_oDate.get(Calendar.HOUR_OF_DAY);
    }
           
    public static int hour24(Date a_oDate)
    {
    	return hour(ConversionUtils.toCalendar(a_oDate));
    }
   
    public static int hour(Calendar a_oDate)
    {
    	return a_oDate.get(Calendar.HOUR);
    }
   
    public static int hour(Date a_oDate)
    {
    	return hour(ConversionUtils.toCalendar(a_oDate));
    }
   
    public static int day(Calendar a_oDate)
    {
    	return a_oDate.get(Calendar.DAY_OF_MONTH);
    }
   
    public static int day(Date a_oDate)
    {
    	return day(ConversionUtils.toCalendar(a_oDate));
    }
   
    public static int dayOfWeek(Calendar a_oDate)
    {
    	return a_oDate.get(Calendar.DAY_OF_WEEK);
    }
           
    public static int dayOfWeek(Date a_oDate)
    {
    	return dayOfWeek(ConversionUtils.toCalendar(a_oDate));
    }
           
    /**
	* Return the number of minutes for a given time
	* @param a_oDate Object containing the time
	* @return Minutes in the time
	*/
    public static int minutes(Calendar a_oDate)
    {
        int l_nMin = a_oDate.get(Calendar.MINUTE);
        int l_nHours = a_oDate.get(Calendar.HOUR_OF_DAY);
       
        return l_nMin + (l_nHours * 60);
    }
   
    /**
	* Return the number of minutes for a given time
	* @param a_oDate Object containing the time
	* @return Minutes in the time
	*/
	public static int minutes(Date a_oDate)
	{
        return minutes(ConversionUtils.toCalendar(a_oDate));
	}
           
	public static Date minutesToTime(int a_nMin) throws Exception
	{
		String l_sTime;
        int l_nHours = ConversionUtils.toInt( Math.floor(a_nMin / 60) );
        int l_nMin = a_nMin - (60 * l_nHours);
           
        if (l_nMin < 10){
        	l_sTime = l_nHours + ":0" + l_nMin;
        }else{
        	l_sTime = l_nHours + ":" + l_nMin;
        }
               
        return ConversionUtils.toDate(l_sTime, MASK_TIME_24_HOUR);
    }
   
    public static Date[] minutesToTimeArray(int[] a_oMin) throws Exception
    {
        Date[] l_oTime = new Date[a_oMin.length];
        for (int x = 0; x < a_oMin.length; x++)
        {
            l_oTime[x] = minutesToTime(a_oMin[x]);
        }
        return l_oTime;
    }
   
    public static int month(Calendar a_oDate)
    {
    	return a_oDate.get(Calendar.MONTH) + 1;
    }
   
    public static int month(Date a_oDate)
    {
    	return month(ConversionUtils.toCalendar(a_oDate));
    }
   
    public static int month()
    {
        return month(new Date());
    }
   
    public static int year(Calendar a_oDate)
    {
    	return a_oDate.get(Calendar.YEAR);
    }
   
    public static int year(Date a_oDate)
    {
    	return year(ConversionUtils.toCalendar(a_oDate));
    }
   
    public static int year()
    {
        return year(new Date());
    }
   
    /**
	* Add on to a date
	* @param a_oDate Date to add to
	* @param a_nInterval Number of units of datepart to add to date (positive, to get dates in the future; negative, to get dates in the past)
	* @param a_cDatePart s = seconds, n = minutes, h = hours, d = days, m = months, y = years
	* @return New date
	*/
    public static Calendar dateAdd(Calendar a_oDate, int a_nInterval, char a_cDatePart)
    {
    	Calendar l_oCal = a_oDate;
   
        switch (a_cDatePart)
        {
        	case (DATE_PART_SECOND):
        		l_oCal.add(Calendar.SECOND, a_nInterval);
                break;
                                                             
            case (DATE_PART_MINUTE):
            	l_oCal.add(Calendar.MINUTE, a_nInterval);
                break;
                                                             
            case (DATE_PART_HOUR):
                l_oCal.add(Calendar.HOUR, a_nInterval);
                break;
                                                             
            case (DATE_PART_DAY):
                l_oCal.add(Calendar.DATE, a_nInterval);
                break;
                           
            case (DATE_PART_MONTH):
                l_oCal.add(Calendar.MONTH, a_nInterval);
                break;
                           
            case (DATE_PART_YEAR):
                l_oCal.add(Calendar.YEAR, a_nInterval);
                break;
        }
   
        return l_oCal;
    }
   
   
    public static Date dateAdd(Date a_dDate, int a_nInterval, char a_cDatePart)
    {
        return ConversionUtils.toDate(dateAdd(ConversionUtils.toCalendar(a_dDate), a_nInterval, a_cDatePart));
    }
   
   
    public static int dateDiff(Date a_dOne, Date a_dTwo, char a_cDatePart)
    {
    	int l_nReturn = 0;
               
        long l_nMilliseconds = a_dTwo.getTime() - a_dOne.getTime();
        if (l_nMilliseconds < 0)
        {
        	l_nMilliseconds = -l_nMilliseconds;
        }
        long l_nSeconds = l_nMilliseconds / 1000L;
        long l_nMinutes = l_nSeconds / 60L;
        long l_nHours = l_nMinutes / 60L;
        long l_nDays = l_nHours / 24L;
                                       
        switch (a_cDatePart)
        {
        	case (DATE_PART_MONTH):
        		int l_iMonthsOne = (year(a_dOne) * 12) + month(a_dOne);
        		int l_iMonthsTwo = (year(a_dTwo) * 12) + month(a_dTwo);
        		l_nReturn = l_iMonthsTwo - l_iMonthsOne;
                break;                         
                           
            case (DATE_PART_DAY):
                l_nReturn = ConversionUtils.toInt(l_nDays);
                break;
                                       
            case (DATE_PART_HOUR):
                l_nReturn = ConversionUtils.toInt(l_nHours);
                break;
                                       
            case (DATE_PART_MINUTE):
                l_nReturn = ConversionUtils.toInt(l_nMinutes);
                break;
                                       
            case (DATE_PART_SECOND):
                l_nReturn = ConversionUtils.toInt(l_nSeconds);
                break;
        }
               
        return l_nReturn;
    }
   
    public static int dateDiff(Calendar a_dOne, Calendar a_dTwo, char a_cDatePart)
    {
        return dateDiff(ConversionUtils.toDate(a_dOne), ConversionUtils.toDate(a_dTwo), a_cDatePart);
    }
   
    public static boolean datesEqual(Date date1, Date date2)
    {
    	if (date1.compareTo(date2) == 0){
    		return true;
        }else{
        	return false;
        }
    }
   
    public static String dayOfWeekAsString(Date date)
    {
    	return formatDate("EEEE", date);
    }
   
    public static String monthAsString(Date date)
    {
    	return formatDate("MMMM", date);
    }
   
    public static String monthAsString(int month) throws Exception
    {
        Date date = ConversionUtils.toDate(month + "/1/2000");
        return formatDate("MMMM", date);
    }
    
    public static String timeFormat(Date date)
    {
    	return formatDate(MASK_TIME_12_HOUR, date);
    }
    
    public static String dateFormat(Date date)
    {
        return formatDate(MASK_DEFAULT_DATE, date);
    }
    
    public static String dateTimeFormat(Date date)
    {
        return formatDate(MASK_FULL_DATE_TIME, date);
    }
    
    public static int dateAsEpoc(Date date)
    {
    	return ConversionUtils.toInt(date.getTime() / 1000);
    }
   
    public static int weekInYear(Date a_dDate)
    {
        return weekInYear(a_dDate, Calendar.SUNDAY);
    }
   
    public static int weekInYear(Date a_dDate, int a_iFirstDayOfWeek)
    {
        Calendar l_oCal = ConversionUtils.toCalendar(a_dDate);
        l_oCal.setFirstDayOfWeek(a_iFirstDayOfWeek);
        return l_oCal.get(Calendar.WEEK_OF_YEAR);
    }
    
    public static Date combineDates(Date dateDate, Date timeDate)
    {
    	Calendar dateCal = ConversionUtils.toCalendar(dateDate);
    	dateCal.setTime(timeDate);
    	return ConversionUtils.toDate(dateCal);
    	//Calendar timeCal = ConversionUtils.toCalendar(timeDate);
    	//Calendar combined = new Calendar();
    }
    
    /**
     * Convert a date in GMT to the local time
     * 
     * Hat tip to: http://stackoverflow.com/questions/10599109/how-to-convert-a-local-date-to-gmt
     * 
     * @param date Date with GMT value
     * @return Date in the local time
     */
    public static Date gmtToLocal(Date date)
    {
    	try 
    	{
    		TimeZone localTimeZone = Calendar.getInstance().getTimeZone();
    		Date ret = new Date(date.getTime() - localTimeZone.getRawOffset());
    		
    		// If we are now in DST, back off by the delta.  
    		// Note that we are checking the GMT date, this is the KEY.
    		if (localTimeZone.inDaylightTime(ret) == true)
    		{
    			Date dstDate = new Date(ret.getTime() - localTimeZone.getDSTSavings());
    			
    			// Check to make sure we have not crossed back into standard time.
                // This happens when we are on the cusp of DST (7pm the day before the change for PDT)
    			if (localTimeZone.inDaylightTime(dstDate) == true)
    			{
    				ret = dstDate;
    			}
    		}
    		
    		return ret;
        }
    	catch (Exception e) {e.printStackTrace(); return null; }
    }
}
