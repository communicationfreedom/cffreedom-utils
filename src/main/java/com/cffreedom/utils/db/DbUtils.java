package com.cffreedom.utils.db;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cffreedom.beans.DbConn;
import com.cffreedom.beans.DbDriver;
import com.cffreedom.beans.DbType;
import com.cffreedom.exceptions.DbException;
import com.cffreedom.exceptions.FileSystemException;
import com.cffreedom.exceptions.InfrastructureException;
import com.cffreedom.utils.Utils;
import com.cffreedom.utils.file.FileUtils;

/**
 * Original Class: com.cffreedom.utils.db.DbUtils
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
 * 2013-04-12 	markjacobsen.net 	Running test SQL in testConnection() if we can - not just making a connection
 * 2013-04-16 	markjacobsen.net 	testConnection() methods now return a boolean
 * 2013-04-22 	markjacobsen.net 	Added toInClausItems()
 * 2013-04-27 	markjacobsen.net 	Added getResultSet()
 * 2013-05-18 	markjacobsen.net 	Added listTables()
 * 2013-05-23	markjacobsen.net 	Updated outputResultSet() to handle RAW format better
 * 2013-07-05	markjacobsen.net 	Added getJndiDataSourceNames()
 * 2013-07-06 	markjacobsen.net 	Using slf4j
 * 2013-07-20	markjacobsen.net 	Fixed getConnectionJNDI()
 * 2013-09-20 	markjacobsen.net 	testConnection() is not void and throws exceptions, getResultSet() throws exceptions
 */
public class DbUtils
{
	private static final Logger logger = LoggerFactory.getLogger("com.cffreedom.utils.db.DbUtils");
	
	public static enum FORMAT {CSV,TAB,XML,RAW,NO_OUTPUT};

	public final static String SQL_TEST_SQLSERVER = "SELECT getDate()";
	public final static String SQL_TEST_DB2 = "SELECT CURRENT_TIMESTAMP FROM SYSIBM.SYSDUMMY1";
	
	public final static String SQL_LIST_TABLES_DB2 = "SELECT TRIM(CREATOR)||\'.\'||TRIM(NAME) AS TABLE_NM FROM SYSIBM.SYSTABLES WHERE TYPE = \'T\' AND CREATOR NOT IN (\'SYSIBM\', \'SYSPROC\') ORDER BY CREATOR, NAME";
	public final static String SQL_LIST_TABLES_SQLSERVER = "SELECT TABLE_SCHEMA+\'.\'+TABLE_NAME AS TABLE_NM FROM information_schema.tables WHERE TABLE_TYPE = \'BASE TABLE\' ORDER BY TABLE_SCHEMA, TABLE_NAME";
	public final static String SQL_LIST_TABLES_MYSQL = "SELECT CONCAT(TABLE_SCHEMA, \'.\', TABLE_NAME) AS TABLE_NM FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = \'BASE TABLE\' ORDER BY TABLE_SCHEMA, TABLE_NAME";
	
	public static List<String> getJndiDataSourceNames()
	{
		List<String> dataSources = new ArrayList<String>();
		try
		{
			Context ctx = new InitialContext();
			NamingEnumeration<?> bindings = ctx.listBindings("java:comp/env/jdbc");

			while (bindings.hasMore())
			{
				Binding binding = (Binding) bindings.next();
				dataSources.add(binding.getName());
			}

		}
		catch (Exception e)
		{
			dataSources = null;
		}
		
		return dataSources;
	}
	
	public static DbType getDbType(String val)
	{
		return DbType.valueOf(val);
	}
	
	public static void listTables(DbConn dbconn)
	{
		Connection conn = null;
		try
		{
			conn = DbUtils.getConnection(dbconn.getDriver(), dbconn.getUrl(), dbconn.getUser(), dbconn.getPassword());
			String testSql = DbUtils.getListTablesSql(dbconn.getType());
			if (testSql != null)
			{
				runSql(conn, testSql, FORMAT.RAW);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try {conn.close();} catch (Exception e) {}
		}
	}
	
	public static void testConnection(DbConn dbconn, String user, String pass) throws DbException, InfrastructureException
	{
		testConnection(dbconn.getType(), dbconn.getHost(), dbconn.getDb(), dbconn.getPort(), user, pass);
	}
	
	public static void testConnection(DbType dbType, String host, String db, int port, String user, String pass) throws DbException, InfrastructureException
	{
		Connection conn = null;
		
		try
		{
			String driver = DbUtils.getDefaultDriver(dbType);
			String url = DbUtils.getUrl(dbType, host, db, port);
			
			conn = DbUtils.getConnection(driver, url, user, pass);
			String testSql = DbUtils.getTestSql(dbType);
			if (testSql != null)
			{
				if (runSql(conn, DbUtils.getTestSql(dbType)) == false)
				{
					throw new DbException("Error running test SQL");
				}
				
				logger.debug("SUCCESS: " + url);
			}
		}
		catch (DbException | InfrastructureException e)
		{
			throw e;
		}
		finally
			{
			try {conn.close();} catch (Exception e){}
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
		final String PROMPT_KEY = "[prompt:";
		HashMap<String, String> replaceVals = new HashMap<String, String>();
		int errors = 0;
		int worked = 0;
		
		logger.debug("Running: " + file);
		
		List<String> content = FileUtils.getFileLines(file, " ");
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
						logger.debug("Replacing \"{}\" with \"{}\"", entry.getKey(), entry.getValue());
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
		boolean success = false;
		
		try
		{
			Statement stmt = conn.createStatement();
			logger.debug("Running: " + sql);
			boolean hasResults = stmt.execute(sql);
			logger.debug("Ran sql");
			
			if ((format.compareTo(DbUtils.FORMAT.NO_OUTPUT) != 0) && (hasResults == true))
			{
				ResultSet rs = stmt.getResultSet();
				outputResultSet(rs, outputTo, format);
				cleanup(null, null, rs);
			}
			
			stmt.close();
			success = true;
		}
		catch (SQLException | IOException | ClassNotFoundException | FileSystemException e)
		{
			success = false;
		}
		return success;
	}
	
	/**
	 * Execute SQL and return the result set. 
	 * Note that nothing (like the Connection) is closed, so you will need to do that.
	 * @param conn DB Connection
	 * @param sql SQL to execute
	 * @return ResultSet for the SQL
	 * @throws DbException 
	 */
	public static ResultSet getResultSet(Connection conn, String sql) throws DbException
	{
		ResultSet rs = null;
		
		try
		{
			Statement stmt = conn.createStatement();
			logger.debug("Running: " + sql);
			rs = stmt.executeQuery(sql);
		}
		catch (SQLException e)
		{
			logger.error(e.getMessage(), e);
			throw new DbException(e);
		}
		return rs;
	}
	
	/**
	 * Output the contents of a ResultSet
	 * @param rs ResultSet to output
	 * @param file Full path to write output to. Null if output to STDOUT
	 * @param format How to show the data (delimited, xml, etc)
	 * @throws SQLException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws FileSystemException 
	 */
	public static void outputResultSet(ResultSet rs, String file, FORMAT format) throws SQLException, IOException, ClassNotFoundException, FileSystemException
	{		
		if (rs != null)
		{
			StringBuffer sb = new StringBuffer();
			ResultSetMetaData md = rs.getMetaData();
			int cols = md.getColumnCount();
			
			String separator = null;
			if (format.compareTo(DbUtils.FORMAT.CSV) == 0) { separator = ","; }
			if (format.compareTo(DbUtils.FORMAT.TAB) == 0) { separator = "\t"; }
			if (format.compareTo(DbUtils.FORMAT.RAW) == 0) { separator = ""; }
			
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
	 * @throws DbException 
	 */
	public static Object getObjectSerializedInBlob(Connection conn, String sql) throws DbException
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
		catch (SQLException e)
		{
			throw new DbException("SQLException - " + e.getMessage(), e);
		}
		catch (IOException e)
		{
			throw new DbException("IOException - " + e.getMessage());
		}
		catch (ClassNotFoundException e)
		{
			throw new DbException("ClassNotFoundException - " + e.getMessage());
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
	
	/**
	 * Convert a ArrayList of Strings to a SQL IN clause values ex: 'val 1', 'val 2', 'something else'
	 * @param vals ArrayList of strings
	 * @param stringList true if you want values surrounded by single quotes
	 * @return String containing SQL IN clause values
	 */
	public static String toInClausItems(ArrayList<String> vals, boolean stringList)
	{
		StringBuffer retVal = new StringBuffer();
		String valPrefix = "";
		String valSuffix = "";
		int counter = 0;
		
		if (stringList == true)
		{
			valPrefix = "\'";
			valSuffix = "\'";
		}
		
		for (String val : vals)
		{
			counter++;
			retVal.append(valPrefix);
			retVal.append(val);
			retVal.append(valSuffix);
			if (counter < vals.size())
			{
				retVal.append(", ");
			}
		}
		
		return retVal.toString();
	}
	
	public static String getDefaultDriver(DbType dbType)
	{
		if (dbType == DbType.MYSQL)
		{
			return DbDriver.MYSQL.value;
		}
		else if (dbType == DbType.DB2)
		{
			return DbDriver.DB2_JCC.value;
		}
		else if (dbType == DbType.SQL_SERVER)
		{
			return DbDriver.SQL_SERVER.value;
		}
		else if (dbType == DbType.ODBC)
		{
			return DbDriver.ODBC.value;
		}
		else if (dbType == DbType.SQLITE)
		{
			return DbDriver.SQLITE.value;
		}
		else
		{
			return "";
		}
	}

	public static String getUrl(DbType dbType, String host, String db)
	{
		return getUrl(dbType, host, db, 0);
	}

	public static String getUrl(DbType dbType, String host, String db, int port)
	{
		if (dbType == DbType.MYSQL)
		{
			if (port <= 0)
			{
				port = getDefaultPort(dbType);
			}
			return "jdbc:mysql://" + host + ":" + port + "/" + db;
		}
		else if (dbType == DbType.DB2)
		{
			if (host == null) {
				return "jdbc:db2:" + db;
			} else {
				return "jdbc:db2://" + host + ":" + port + "/" + db;
			}
		}
		else if (dbType == DbType.SQL_SERVER)
		{
			if (port <= 0)
			{
				port = getDefaultPort(dbType);
			}
			return "jdbc:microsoft:sqlserver://" + host + ":" + port + ";databaseName=" + db;
		}
		else if (dbType == DbType.ODBC)
		{
			return "jdbc:odbc:" + db;
		}
		else if (dbType == DbType.SQLITE)
		{
			if (db == null) {
				return "jdbc:sqlite::memory:";
			} else {
				return "jdbc:sqlite:" + db;
			}
		}
		else
		{
			return "";
		}
	}
	
	public static int getDefaultPort(DbType dbType)
	{
		if (dbType == null)
		{
			return 0;
		}
		else if (dbType == DbType.MYSQL)
		{
			return 3306;
		}
		else if (dbType == DbType.SQL_SERVER)
		{
			return 1443;
		}
		else if (dbType == DbType.DB2)
		{
			return 50000;
		}
		else
		{
			return 0;
		}
	}
	
	public static Connection getConnection(String driver, String url, String user, String pass) throws DbException, InfrastructureException
	{	
		try
		{
			logger.debug("Creating new connection\n   Driver: {}\n   Url: {}\n   user: {}", driver, url, user);
			Class.forName(driver);
			return DriverManager.getConnection(url, user, pass);
		}
		catch (SQLException e)
		{
			throw new DbException(e);
		}
		catch (ClassNotFoundException e)
		{
			throw new InfrastructureException("ERROR: " + url + ": ClassNotFoundException (check that the driver is on the CLASSPATH): " + e.getMessage(), e);
		}
	}
	
	public static Connection getConnectionJNDI(String dsn) throws InfrastructureException, DbException { return getConnectionJNDI(dsn, "java:comp/env"); }
	public static Connection getConnectionJNDI(String dsn, String initContext) throws InfrastructureException, DbException
	{
		logger.trace("Getting connection {} via initial context {}", dsn, initContext);
		
		try
		{
			Context initialContext = new InitialContext();
			Context envCtx = (Context)initialContext.lookup(initContext);
			DataSource datasource = (DataSource)envCtx.lookup(dsn);
			if (datasource != null) {
		        return datasource.getConnection();
			}
			else
			{
				throw new InfrastructureException("Unable to get JNDI connection: " + dsn);
			}
		}
		catch (NamingException e)
		{
			throw new InfrastructureException("NamingException looking up JNDI connection: " + dsn);
		}
		catch (SQLException e)
		{
			throw new DbException("SQLException getting JNDI connection: " + dsn);
		}
	}

	public static ResultSet execQuery(Connection conn, String sql) throws SQLException
	{
		Statement stmt = conn.createStatement();
		return stmt.executeQuery(sql);
	}

	public static int getRecordCount(ResultSet rs) throws SQLException
	{
		int count = 0;

		while (rs.next())
		{
			count++;
		}

		return count;
	}
	
	public static String getListTablesSql(DbType dbType)
	{
		if (dbType == DbType.DB2)
		{
			 return DbUtils.SQL_LIST_TABLES_DB2;
		}
		else if (dbType == DbType.SQL_SERVER)
		{
			return DbUtils.SQL_LIST_TABLES_SQLSERVER;
		}
		else if (dbType == DbType.MYSQL)
		{
			return DbUtils.SQL_LIST_TABLES_MYSQL;
		}
		else
		{
			return null;
		}
	}	
	
	public static String getTestSql(DbType dbType)
	{
		if (dbType == DbType.DB2)
		{
			 return DbUtils.SQL_TEST_DB2;
		}
		else if (dbType == DbType.SQL_SERVER)
		{
			return DbUtils.SQL_TEST_SQLSERVER;
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * Cleanup the items passed in to free up resources
	 * @param conn Connection to close
	 * @param stmt Statement to close
	 * @param rs ResultSet to close
	 */
	public static void cleanup(Connection conn, Statement stmt, ResultSet rs)
	{
		if (rs != null) { try { rs.close(); } catch (Exception e) {} finally { rs = null; } }
		if (stmt != null) { try { stmt.close(); } catch (Exception e) {} finally { stmt = null; } }
		if (conn != null) { try { conn.close(); } catch (Exception e) {} finally { conn = null; } }
	}
}
