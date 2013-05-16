package com.cffreedom.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Hashtable;

import com.cffreedom.utils.file.FileUtils;

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
 * 2013-05-06 	markjacobsen.net 	Using Utils.output instead of System.out.println
 * 2013-05-16 	markjacobsen.net	Adding DateTime stamp to messages
 */
public class LoggerUtil
{
	public static final String FAMILY_ACCOUNT = "ACCOUNT";
	public static final String FAMILY_COMMERCE = "COMMERCE";
	public static final String FAMILY_SWITCHBOARD = "SWITCHBOARD";
	public static final String FAMILY_TASK = "TASK";
	public static final String FAMILY_TEST = "TEST";
	public static final String FAMILY_UTIL = "UTIL";
	public static final String FAMILY_UNKNOWN = "UNKNOWN";
	
	public static final String LEVEL_DEBUG = "DEBUG";
	public static final String LEVEL_INFO = "INFO";
	public static final String LEVEL_WARN = "WARN";
	public static final String LEVEL_ERROR = "ERROR";
	
	private static Hashtable<String, LoggerUtil> loggers = new Hashtable<String, LoggerUtil>();
	private static Hashtable<String, FileWriter> logFiles = new Hashtable<String, FileWriter>();
	
	private String name = LoggerUtil.FAMILY_UNKNOWN;
	private String family = LoggerUtil.FAMILY_UNKNOWN;
	
	public LoggerUtil(String family, String name)
	{
		//String host = System.getenv("COMPUTERNAME");
		
		if (logFiles.contains(family) == false)
		{
			try
			{
				String logFile = getLoggingDir() + SystemUtils.getPathSeparator() + family + ".log";
				FileWriter fw = new FileWriter(logFile, true);
				logFiles.put(family, fw);
			}
			catch (IOException e)
			{
				Utils.output("ERROR: IOException in constructor: " + e.getMessage());
			}
		}
		
		if (loggers.containsKey(name) == false)
		{
			Utils.output("Creating new LoggerUtil instance: " + name);
			this.family = family;
			this.name = name;
			loggers.put(name, this);
	    }
	}
	
	public String getFamily() { return this.family; }
	public String getName() { return this.name; }
	private FileWriter getFileWriter() { return logFiles.get(this.getFamily()); }
	
	public static void log(String method, String msg) { log(null, method, msg); }
	public static void log(String level, String method, String msg)
	{
		String fullmsg = FormatUtils.formatDate(FormatUtils.DATE_DB2_TIMESTAMP, new Date()) + ": ";
		if (level != null)			{ fullmsg += level + ": "; }
		if (method != null)			{ fullmsg += method + ": "; }
		if (msg != null)			{ fullmsg += msg; }
		
		Utils.output(fullmsg);
	}
	
	public void logDebug(String method, String msg)
	{
		logIt(LEVEL_DEBUG, method, msg, null);
	}
	
	public void logInfo(String method, String msg)
	{
		logIt(LEVEL_INFO, method, msg, null);
	}
	
	public void logWarn(String method, String msg)
	{
		logIt(LEVEL_WARN, method, msg, null);
	}
	
	public void logError(String method, String msg, Throwable err)
	{
		logIt(LEVEL_ERROR, method, msg, err);
		err.printStackTrace();
	}
	
	private void logIt(String level, String method, String msg, Throwable err)
	{
		String fullmsg = FormatUtils.formatDate(FormatUtils.DATE_DB2_TIMESTAMP, new Date()) + ": ";
		if (level != null)			{ fullmsg += level + ": "; }
		if (this.getName() != null)	{ fullmsg += this.getName() + ": "; }
		if (method != null)			{ fullmsg += method + ": "; }
		if (msg != null)			{ fullmsg += msg; }
		
		Utils.output(fullmsg);
		try
		{
			FileWriter fw = this.getFileWriter();
			
			if (fw != null)
			{
				fw.write(fullmsg + "\n");
				
				if (err != null)
				{
					StackTraceElement[] lines = err.getStackTrace();
					for (int x = 0; x < lines.length; x++)
					{
						fw.write("    " + lines[x].toString() + "\n");
					}
				}
				fw.flush();
			}
		}
		catch (IOException io)
		{
			Utils.output("ERROR: IOException");
		}
	}
	
	public static String getLoggingDir()
	{
		if (SystemUtils.isWindows() == true)
		{
			String dir = "c:\\logs";
			FileUtils.createFolder(dir);
			return dir;
		}
		else
		{
			return "/var/log/cf";
		}
	}
}
