package com.cffreedom.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Original Class: com.cffreedom.utils.DateTimeUtils
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
 * 2013-08-19	markjacobsen.net 	Added dayOfYear()
 * 2013-09-03 	markjacobsen.net 	Added hourMinAsInt()
 * 2015-06-07   MarkJacobsen.net 	Added setTime()
 */
public class DateTimeUtils extends Format {
	private static final Logger logger = LoggerFactory.getLogger(DateTimeUtils.class);
	
    public static final char DATE_PART_SECOND = 's';
    public static final char DATE_PART_MINUTE = 'n';
    public static final char DATE_PART_HOUR = 'h';
    public static final char DATE_PART_DAY = 'd';
    public static final char DATE_PART_MONTH = 'm';
    public static final char DATE_PART_YEAR = 'Y';
       
    @SuppressWarnings("deprecation")
	public static Date time(int hour24, int minute) {
        Date date = new Date();
        date.setHours(hour24);
        date.setMinutes(minute);
       
        return date;
    }
   
    public static int millisecond(Calendar date) {
    	return date.get(Calendar.MILLISECOND);
    }
   
    public static int second(Calendar date) {
    	return date.get(Calendar.SECOND);
    }
           
    public static int second(Date date) {
    	return second(Convert.toCalendar(date));
    }
           
    public static int minute(Calendar date) {
    	return date.get(Calendar.MINUTE);
    }
           
    public static int minute(Date date) {
    	return minute(Convert.toCalendar(date));
    }
           
    public static int hour24(Calendar date) {
    	return date.get(Calendar.HOUR_OF_DAY);
    }
           
    public static int hour24(Date date) {
    	return hour24(Convert.toCalendar(date));
    }
   
    public static int hour(Calendar date) {
    	return date.get(Calendar.HOUR);
    }
   
    public static int hour(Date date) {
    	return hour(Convert.toCalendar(date));
    }
    
    public static int hourMinAsInt(Date date) {
    	return Convert.toInt(Format.date("Hmm", date), false);
    }
   
    public static int day(Calendar date) {
    	return date.get(Calendar.DAY_OF_MONTH);
    }
   
    public static int day(Date date) {
    	return day(Convert.toCalendar(date));
    }
   
    public static int dayOfWeek(Calendar date) {
    	return date.get(Calendar.DAY_OF_WEEK);
    }
           
    public static int dayOfWeek(Date date) {
    	return dayOfWeek(Convert.toCalendar(date));
    }
    
    public static int dayOfYear(Calendar date) {
    	return date.get(Calendar.DAY_OF_YEAR);
    }
    
    public static int dayOfYear(Date date) {
    	return dayOfYear(Convert.toCalendar(date));
    }
           
    /**
	* Return the number of minutes for a given time
	* @param a_oDate Object containing the time
	* @return Minutes in the time
	*/
    public static int minutes(Calendar date) {
        int min = date.get(Calendar.MINUTE);
        int hours = date.get(Calendar.HOUR_OF_DAY);
       
        return min + (hours * 60);
    }
   
    /**
	* Return the number of minutes for a given time
	* @param a_oDate Object containing the time
	* @return Minutes in the time
	*/
	public static int minutes(Date date) {
        return minutes(Convert.toCalendar(date));
	}
           
	public static Date minutesToTime(int min) throws Exception {
		String time;
        int hours = Convert.toInt( Math.floor(min / 60) );
        int minutes = min - (60 * hours);
           
        if (minutes < 10){
        	time = hours + ":0" + minutes;
        }else{
        	time = hours + ":" + minutes;
        }
               
        return Convert.toDate(time, Format.DATE_TIME_24_HOUR);
    }
   
    public static Date[] minutesToTimeArray(int[] minutes) throws Exception {
        Date[] time = new Date[minutes.length];
        for (int x = 0; x < minutes.length; x++) {
            time[x] = minutesToTime(minutes[x]);
        }
        return time;
    }
   
    public static int month(Calendar date) {
    	return date.get(Calendar.MONTH) + 1;
    }
   
    public static int month(Date date) {
    	return month(Convert.toCalendar(date));
    }
    
    public static int month(String date) {
    	if (date.length() >= 3)
    	{
    		date = date.trim().substring(0, 3).toUpperCase();
	    	if (date.equals("JAN") == true) { return 1; }
	    	else if (date.equals("FEB") == true) { return 2; }
	    	else if (date.equals("MAR") == true) { return 3; }
	    	else if (date.equals("APR") == true) { return 4; }
	    	else if (date.equals("MAY") == true) { return 5; }
	    	else if (date.equals("JUN") == true) { return 6; }
	    	else if (date.equals("JUL") == true) { return 7; }
	    	else if (date.equals("AUG") == true) { return 8; }
	    	else if (date.equals("SEP") == true) { return 9; }
	    	else if (date.equals("OCT") == true) { return 10; }
	    	else if (date.equals("NOV") == true) { return 11; } 
	    	else if (date.equals("DEC") == true) { return 12; } 
	    	else { return -1;	}
    	} else if (Utils.isInt(date, false) == true) {
    		int val = Convert.toInt(date, false);
    		if ((val >= 1) && (val <= 12)) {
    			return val;
    		} else {
    			return -1;
    		}
    	} else {
    		return -1;
    	}
    }
   
    public static int month() {
        return month(new Date());
    }
   
    public static int year(Calendar date) {
    	return date.get(Calendar.YEAR);
    }
   
    public static int year(Date date) {
    	return year(Convert.toCalendar(date));
    }
   
    public static int year() {
        return year(new Date());
    }
   
    /**
	* Add on to a date
	* @param a_oDate Date to add to
	* @param a_nInterval Number of units of datepart to add to date (positive, to get dates in the future; negative, to get dates in the past)
	* @param a_cDatePart s = seconds, n = minutes, h = hours, d = days, m = months, y = years
	* @return New date
	*/
    public static Calendar dateAdd(Calendar date, int interval, char datePart) {
    	Calendar cal = (Calendar)date.clone();
   
        switch (datePart) {
        	case (DATE_PART_SECOND):
        		cal.add(Calendar.SECOND, interval);
                break;
                                                             
            case (DATE_PART_MINUTE):
            	cal.add(Calendar.MINUTE, interval);
                break;
                                                             
            case (DATE_PART_HOUR):
            	cal.add(Calendar.HOUR, interval);
                break;
                                                             
            case (DATE_PART_DAY):
            	cal.add(Calendar.DATE, interval);
                break;
                           
            case (DATE_PART_MONTH):
            	cal.add(Calendar.MONTH, interval);
                break;
                           
            case (DATE_PART_YEAR):
            	cal.add(Calendar.YEAR, interval);
                break;
        }
   
        return cal;
    }
   
   
    public static Date dateAdd(Date date, int interval, char datePart) {
        return Convert.toDate(dateAdd(Convert.toCalendar(date), interval, datePart));
    }
   
   
    public static int dateDiff(Date dayOne, Date dayTwo, char datePart) {
    	int ret = 0;
               
        long milliseconds = dayTwo.getTime() - dayOne.getTime();
        if (milliseconds < 0) {
        	milliseconds = -milliseconds;
        }
        long seconds = milliseconds / 1000L;
        long minutes = seconds / 60L;
        long hours = minutes / 60L;
        long days = hours / 24L;
                                       
        switch (datePart) {
        	case (DATE_PART_MONTH):
        		int monthsOne = (year(dayOne) * 12) + month(dayOne);
        		int monthsTwo = (year(dayTwo) * 12) + month(dayTwo);
        		ret = monthsTwo - monthsOne;
                break;                         
                           
            case (DATE_PART_DAY):
            	ret = Convert.toInt(days);
                break;
                                       
            case (DATE_PART_HOUR):
            	ret = Convert.toInt(hours);
                break;
                                       
            case (DATE_PART_MINUTE):
            	ret = Convert.toInt(minutes);
                break;
                                       
            case (DATE_PART_SECOND):
            	ret = Convert.toInt(seconds);
                break;
        }
               
        return ret;
    }
   
    public static int dateDiff(Calendar dayOne, Calendar dayTwo, char datePart)
    {
        return dateDiff(Convert.toDate(dayOne), Convert.toDate(dayTwo), datePart);
    }
   
    public static boolean datesEqual(Date date1, Date date2) {
    	return datesEqual(Convert.toCalendar(date1), Convert.toCalendar(date2));
    }
    
    public static boolean datesEqual(Calendar date1, Calendar date2) {
    	return datesEqual(date1, date2, false);
    }
    
    public static boolean datesEqual(Calendar date1, Calendar date2, boolean ignoreTime) {
    	boolean equal = false;
    	
    	if ((date1 != null) && (date2 != null)) {
    		if (ignoreTime == true) {
        		date1 = stripTime(date1);
        		date2 = stripTime(date2);
        	}
    		if (date1.compareTo(date2) == 0) {
    			equal = true;
    		}
    	}
    	
    	return equal;
    }
    
    @Deprecated
    public static boolean dateEqual(Calendar date1, Calendar date2) {
    	if ((date1 != null) && (date2 != null) && (date1.compareTo(date2) == 0)) {
    		return true;
    	} else {
    		return false;
    	}
    }
    
    /**
     * Determine if a date is between (inclusive) 2 dates
     * @param dateToCheck
     * @param lowerDate
     * @param upperDate
     * @return
     */
    public static boolean dateBetween(Calendar dateToCheck, Calendar lowerDate, Calendar upperDate) {
    	if (
    		(dateToCheck.after(lowerDate) && dateToCheck.before(upperDate)) ||
    		(dateEqual(dateToCheck, lowerDate)) ||
    		(dateEqual(dateToCheck, upperDate))) 
    	{
    		return true;
    	} else {
    		return false;
    	}
    }
   
    public static String dayOfWeekAsString(Date date) {
    	return Format.date("EEEE", date);
    }
   
    public static String monthAsString(Date date) {
    	return Format.date("MMMM", date);
    }
   
    public static String monthAsString(int month) throws Exception {
        Date date = Convert.toDate(month + "/1/2000");
        return Format.date("MMMM", date);
    }
    
    public static String timeFormat(Date date) {
    	return Format.date(Format.DATE_TIME_12_HOUR, date);
    }
    
    public static String dateFormat(Date date) {
        return Format.date(Format.DATE_DEFAULT, date);
    }
    
    public static String dateTimeFormat(Date date) {
        return Format.date(Format.DATE_HUMAN, date);
    }
    
    public static int dateAsEpoc(Date date) {
    	return Convert.toInt(date.getTime() / 1000);
    }
   
    public static int weekInYear(Date date) {
        return weekInYear(date, Calendar.SUNDAY);
    }
   
    public static int weekInYear(Date date, int firstDayOfWeek) {
        Calendar cal = Convert.toCalendar(date);
        cal.setFirstDayOfWeek(firstDayOfWeek);
        return cal.get(Calendar.WEEK_OF_YEAR);
    }
    
    public static Date combineDates(Date dateDate, Date timeDate) {
    	Calendar dateCal = Convert.toCalendar(dateDate);
    	dateCal.setTime(timeDate);
    	return Convert.toDate(dateCal);
    }
    
    public static Calendar combineDates(Calendar dateDate, Calendar timeDate) {
    	DateTimeUtils.setTime(dateDate, DateTimeUtils.hour24(timeDate), DateTimeUtils.minute(timeDate), DateTimeUtils.second(timeDate), DateTimeUtils.millisecond(timeDate));
    	return dateDate;
    }
    
    public static Calendar stripTime(Calendar date) {
    	return setDate(date.get(Calendar.YEAR), date.get(Calendar.MONTH)+1, date.get(Calendar.DAY_OF_MONTH));
    }
    
    /**
     * Sets a date, stripping out all time values
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static Calendar setDate(int year, int month, int day) {
    	return setDateTime(year, month, day, 0, 0, 0, 0);
    }
    
    public static Calendar setTime(Calendar date, int hour24, int minute, int second, int millisecond) {
    	date.set(Calendar.HOUR_OF_DAY, hour24);
    	date.set(Calendar.MINUTE, minute);
    	date.set(Calendar.SECOND, second);
    	date.set(Calendar.MILLISECOND, millisecond);
    	return date;
    }
    
    public static Calendar setDateTime(int year, int month, int day, int hour24, int minute, int second) {
    	return setDateTime(year, month, day, hour24, minute, second, 0);
    }
    
    public static Calendar setDateTime(int year, int month, int day, int hour24, int minute, int second, int millisecond) {
    	Calendar date = Calendar.getInstance();
    	date.set(Calendar.MONTH, month-1);
    	date.set(Calendar.DAY_OF_MONTH, day);
    	date.set(Calendar.YEAR, year);
    	date.set(Calendar.HOUR_OF_DAY, hour24);
    	date.set(Calendar.MINUTE, minute);
    	date.set(Calendar.SECOND, second);
    	date.set(Calendar.MILLISECOND, millisecond);
    	return date;
    }
    
    /**
     * Convert a date in GMT to the local time
     * 
     * Hat tip to: http://stackoverflow.com/questions/10599109/how-to-convert-a-local-date-to-gmt
     * 
     * @param date Date with GMT value
     * @return Date in the local time
     */
    public static Date gmtToLocal(Date gmtDate) {
    	try {
    		TimeZone localTimeZone = Calendar.getInstance().getTimeZone();
    		Date ret = new Date(gmtDate.getTime() - localTimeZone.getRawOffset());
    		
    		// If we are now in DST, back off by the delta.  
    		// Note that we are checking the GMT date, this is the KEY.
    		if (localTimeZone.inDaylightTime(gmtDate) == true) {
    			Date dstDate = new Date(ret.getTime() - localTimeZone.getDSTSavings());
    			
    			// Check to make sure we have not crossed back into standard time.
                // This happens when we are on the cusp of DST (7pm the day before the change for PDT)
    			if (localTimeZone.inDaylightTime(dstDate) == true) {
    				ret = dstDate;
    			}
    		}
    		
    		return ret;
        }
    	catch (Exception e) {e.printStackTrace(); return null; }
    }
    
    public static Calendar standardizeDate(String val) {
    	// Order to try and standardize dates
        final String[] STD_DATE_FORMATS = new String[] {
        		Format.DATE_DEFAULT,
        		"yyyy/MM/dd HH:mm:ss",
            	Format.DATE_TIMESTAMP,
            	"MM-dd-yyyy HH:mm:ss",
            	"yyyy/MM/dd-HH:mm:ss",
            	"yyyy-MM-dd-HH:mm:ss",
            	"MM-dd-yyyy-HH:mm:ss",
            	"MM/dd/yyyy-HH:mm:ss",
                "yyyy/MM/dd",
            	Format.DATE_FILE,
            	"MM-dd-yyyy",
            	Format.DATE_TIMESTAMP_DEFAULT,
            	"MMddyyyy"
            };
        
        Calendar result = null;
  	
    	// search through potential date formats to find a match
    	for(String potentialFormatMatch : STD_DATE_FORMATS) {
    		DateFormat source = new SimpleDateFormat(potentialFormatMatch);
    		source.setLenient(false);
    		try {
    			ParsePosition pos = new ParsePosition(0);
    			// no exception is being thrown by this parse method; instead null is returned
    			result = Convert.toCalendar(source.parse(val, pos));
    			
    			if (result == null) {
    				 throw new ParseException("Unparseable date: \"" + val + "\" for \"" + potentialFormatMatch + "\"" , pos.getErrorIndex());
    			} else if (pos.getIndex() != val.length()) {
    				result = null;
    			} 
    			
    			if (result != null) {
    				// Date parsed successfully so we're good
    				break;
    			}
    		} catch (ParseException ex) {
    			// probably just not a matching date format, continue to next possible match
    			continue;
    		}
    	}
    	
    	if (result == null) {
    		logger.warn("Unable to parse into date: "+val);
    	}
    	
    	return result;
    }
}
