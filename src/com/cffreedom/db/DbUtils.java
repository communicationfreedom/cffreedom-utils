package com.cffreedom.db;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.cffreedom.beans.DbConn;
import com.cffreedom.utils.FileUtils;
import com.cffreedom.utils.LoggerUtil;
import com.cffreedom.utils.Utils;

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
 * 2013-04-11 	markjacobsen.net 	Added the ability to specify outputTo to runSql() - null/STDOUT or fileName
 * 2013-04-12	markjacobsen.net 	Added getObjectSerializedInBlob()
 * 2013-04-12 	markjacobsen.net 	Added additional testConnection() that accepts a DbConn bean
 */
public class DbUtils
{
	private final LoggerUtil logger = new LoggerUtil(LoggerUtil.FAMILY_UTIL, this.getClass().getPackage().getName() + "." + this.getClass().getSimpleName());
	
	public static enum FORMAT {CSV,TAB,XML,RAW,NO_OUTPUT};
	
	public static String testConnection(DbConn dbconn, String user, String pass)
	{
		return testConnection(dbconn.getType(), dbconn.getHost(), dbconn.getDb(), dbconn.getPort(), user, pass);
	}
	
	public static String testConnection(String type, String host, String db, int port, String user, String pass)
	{
		String driver = BaseDAO.getDriver(type);
		String url = BaseDAO.getUrl(type, host, db, port);
		try
		{
			Connection conn = BaseDAO.getConn(driver, url, user, pass);
			conn.close();
			return "SUCCESS: " + url;
		}
		catch (SQLException e)
		{
			int errorCode = e.getErrorCode();
			String sqlState = e.getSQLState();
			String readable = "";
			if  (
				(type.equalsIgnoreCase(BaseDAO.TYPE_DB2) == true) && 
				(errorCode == -1060) && 
				(sqlState.equalsIgnoreCase("08004") == true)
				)
			{
				readable = user + " does not have CONNECT permission: ";
			}
			return "ERROR: SQLException: " + readable + url + ": " + e.getMessage() + " (" + errorCode + "/" + sqlState + ")";
		}
	}
	
	/**
	 * Run a script.  Prompts are in the form [prompt:text|replaceVar|optDefaultVal] and should be
	 * one per line listed at the top.
	 * @param conn
	 * @param file
	 */
	public static int runSqlScript(Connection conn, String file) { return runSqlScript(conn, file, FORMAT.XML); }
	public static int runSqlScript(Connection conn, String file, FORMAT format) { return runSqlScript(conn, file, format, ";"); }
	public static int runSqlScript(Connection conn, String file, FORMAT format, String delimiter) { return runSqlScript(conn, file, format, delimiter, true); }
	public static int runSqlScript(Connection conn, String file, FORMAT format, String delimiter, boolean outputResults)
	{
		final String METHOD = "runSqlScript";
		final String PROMPT_KEY = "[prompt:";
		HashMap<String, String> replaceVals = new HashMap<String, String>();
		int errors = 0;
		int worked = 0;
		
		LoggerUtil.log(METHOD, "Running: " + file);
		
		ArrayList<String> content = FileUtils.getFileLines(file, " ");
		String temp = "";
		for (String line : content)
		{				
			if (line.trim().length() > 0)
			{
				if (line.indexOf(PROMPT_KEY) == 0)
				{
					// There is a prompt
					String tempPrompt = line.replaceFirst(Pattern.quote(PROMPT_KEY), "");
					tempPrompt = tempPrompt.substring(0, tempPrompt.length() - 1);
					String[] prompt = tempPrompt.split("\\|");
					String text = prompt[0];
					String replaceHolder = prompt[1];
					String defaultVal = null;
					if (prompt.length == 2)
					{
						// There is NO default value for the prompt
						replaceHolder = replaceHolder.substring(0, replaceHolder.length() - 1);
					}
					else if (prompt.length >= 3)
					{
						// There is a default value for the prompt
						String tmpDef = prompt[2].trim();
						defaultVal = tmpDef.substring(0, tmpDef.length() - 1);
					}
					String promptVal = Utils.prompt(text, defaultVal);
					replaceVals.put(replaceHolder, promptVal);
					line = "";
				}
				
				if (line.indexOf("--") >= 0)
				{
					int endIndex = line.indexOf("--");
					if (endIndex > 0)
					{
						line = line.substring(0, endIndex - 1);
					}
					else
					{
						line = "";
					}
				}
			
				temp += line;
				if (Utils.lastChar(line.trim()).equalsIgnoreCase(delimiter) == true)
				{
					// run it
					String sql = temp.substring(0, temp.trim().length() - 1);
					
					for(Map.Entry<String,String> entry : replaceVals.entrySet())
					{
						//LoggerUtil.log(METHOD, "Replacing \"" + entry.getKey() + "\" with \"" + entry.getValue() + "\"");
						sql = sql.replace(entry.getKey(), entry.getValue());
					}
					
					if (runSql(conn, sql, format) == false)
					{
						errors++;
					}
					else
					{
						worked++;
					}
					Utils.output("\n\n");
					
					// reset
					temp = "";
				}
			}
		}
		
		if (outputResults == true)
		{
			Utils.output(worked + " statements completed successfully during script execution");
			Utils.output(errors + " statements generated errors during script execution");
		}
		
		return errors;
	}
	
	public static boolean runSql(Connection conn, String sql) { return runSql(conn, sql, FORMAT.XML); }
	public static boolean runSql(Connection conn, String sql, FORMAT format) { return runSql(conn, sql, format, null); }
	public static boolean runSql(Connection conn, String sql, FORMAT format, String outputTo)
	{
		final String METHOD = "runSql";
		boolean success = false;
		
		try
		{
			Statement stmt = conn.createStatement();
			LoggerUtil.log(METHOD, "Running: " + sql);
			boolean hasResults = stmt.execute(sql);
			LoggerUtil.log(METHOD, "Ran sql");
			
			if ((format.compareTo(DbUtils.FORMAT.NO_OUTPUT) != 0) && (hasResults == true))
			{
				ResultSet rs = stmt.getResultSet();
				outputResultSet(rs, outputTo, format);
				try { rs.close(); } catch (Exception e) {}
			}
			
			stmt.close();
			success = true;
		}
		catch (SQLException e)
		{
			success = false;
		}
		catch (IOException e)
		{
			success = false;
		}
		catch (ClassNotFoundException e)
		{
			success = false;
		}
		return success;
	}
	
	public static void outputResultSet(ResultSet rs, String file, FORMAT format) throws SQLException, IOException, ClassNotFoundException
	{		
		if (rs != null)
		{
			StringBuffer sb = new StringBuffer();
			ResultSetMetaData md = rs.getMetaData();
			int cols = md.getColumnCount();
			
			String separator = null;
			if (format.compareTo(DbUtils.FORMAT.CSV) == 0) { separator = ","; }
			if (format.compareTo(DbUtils.FORMAT.TAB) == 0) { separator = "\t"; }
			
			// START: Output header
			if  (
				(format.compareTo(DbUtils.FORMAT.XML) != 0) &&
				(format.compareTo(DbUtils.FORMAT.RAW) != 0)
				)
			{
				// Output column headers
				for (int i = 1; i <= cols; i++) // ResultSet columns are 1 (not 0) based
				{
					sb.append(md.getColumnLabel(i) + separator);
				}
				sb.append("\n");
			}
			else if (format.compareTo(DbUtils.FORMAT.XML) == 0)
			{
				sb.append("<results>\n");
			}
			// END: Output header
			
			// START: Output data
			while (rs.next() == true)
			{
				if (format.compareTo(DbUtils.FORMAT.XML) == 0) { sb.append("<result row=\"" + rs.getRow() + "\">\n"); }
				for (int i = 1; i <= cols; i++) // ResultSet columns are 1 (not 0) based
				{
					if (format.compareTo(DbUtils.FORMAT.XML) == 0)
					{
						sb.append("    <" + md.getColumnLabel(i) + ">" + rs.getString(i) + "</" + md.getColumnLabel(i) + ">\n");
					}
					else
					{
						sb.append(rs.getString(i) + separator);
					}
				}
				if (format.compareTo(DbUtils.FORMAT.XML) == 0) { sb.append("</result>"); }
				sb.append("\n");
			}
			// END: Output data
			
			// Output footer
			if (format.compareTo(DbUtils.FORMAT.XML) == 0) { sb.append("</results>\n"); }
			
			// Final output
			if ((file == null) || (file.equalsIgnoreCase("STDOUT") == true) || (file.length() == 0))
			{
				Utils.output(sb.toString());
			}
			else
			{
				FileUtils.writeStringToFile(file, sb.toString(), false);
			}
		}
	}
	
	/** 
	 * @param conn DB Connection
	 * @param sql SQL to get back 1 row with 1 field that is a BLOB
	 * @return The contents of the BLOB as an Object
	 */
	public static Object getObjectSerializedInBlob(Connection conn, String sql)
	{
		Object returnVal = null;
		try
		{
			Statement stmt = conn.createStatement();
			boolean hasResults = stmt.execute(sql);
			if (hasResults == true)
			{
				ResultSet rs = stmt.getResultSet();
				while (rs.next() == true)
				{
					byte[] buf = rs.getBytes(1);
				    ObjectInputStream objectIn = null;
				    if (buf != null)
				    {
				    	objectIn = new ObjectInputStream(new ByteArrayInputStream(buf));
				    }
				    returnVal = objectIn.readObject();
				}
			}
		}
		catch (Exception e)
		{
			returnVal = null;
		}
		return returnVal;
	}
	
	public static boolean validFormat(String valueToCheck)
    {
    	try
    	{
    		FORMAT.valueOf(valueToCheck);
    		return true;
    	}
    	catch (IllegalArgumentException e)
    	{
    		return false;
    	}
    }
}