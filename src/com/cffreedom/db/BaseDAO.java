package com.cffreedom.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import com.cffreedom.beans.DbConn;
import com.cffreedom.utils.ConversionUtils;
import com.cffreedom.utils.LoggerUtil;

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
 * 2013-04-12 	markjacobsen.net 	Added SQL_TEST_SQLSERVER and additional getConn() method
 * 2013-04-12 	markjacobsen.net 	Added methods to check if a "type" is of the requested type (i.e. isMySql())
 */
public class BaseDAO
{
	private final LoggerUtil logger = new LoggerUtil(LoggerUtil.FAMILY_UTIL, this.getClass().getPackage().getName() + "." + this.getClass().getSimpleName());

	ConnectionFactory factory = null;

	public final static Date DATE_01_01_1900 = ConversionUtils.toDate("01/01/1900");
	public final static Date DATE_12_31_9999 = ConversionUtils.toDate("12/31/9999");

	public final static String DRIVER_MYSQL = "com.mysql.jdbc.Driver";
	public final static String DRIVER_DB2_JCC = "com.ibm.db2.jcc.DB2Driver";
	public final static String DRIVER_DB2_APP = "COM.ibm.db2.jdbc.app.DB2Driver";
	public final static String DRIVER_DB2_NET = "COM.ibm.db2.jdbc.net.DB2Driver";
	public final static String DRIVER_SQL_SERVER_2005 = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	public final static String DRIVER_ODBC = "sun.jdbc.odbc.JdbcOdbcDriver";
	public final static String DRIVER_SQLITE = "org.sqlite.JDBC";

	public final static String TYPE_MYSQL = "MYSQL";
	public final static String TYPE_DB2_JCC = "DB2_JCC";
	public final static String TYPE_DB2_APP = "DB2_APP";
	public final static String TYPE_DB2 = TYPE_DB2_JCC;
	public final static String TYPE_SQL_SERVER = "SQL_SERVER";
	public final static String TYPE_ODBC = "ODBC";
	public final static String TYPE_SQLITE = "SQLITE";

	public final static String SQL_TEST_SQLSERVER = "SELECT getDate()";
	public final static String SQL_TEST_DB2 = "SELECT CURRENT_TIMESTAMP FROM SYSIBM.SYSDUMMY1";

	public BaseDAO()
	{
		// do nothing
	}

	public BaseDAO(ConnectionFactory factory)
	{
		this.factory = factory;
	}
	
	public static String getDriver(String type)
	{
		if (isMySql(type) == true)
		{
			return BaseDAO.DRIVER_MYSQL;
		}
		else if (isDb2JCC(type) == true)
		{
			return BaseDAO.DRIVER_DB2_JCC;
		}
		else if (isDb2App(type) == true)
		{
			return BaseDAO.DRIVER_DB2_APP;
		}
		else if (isSqlServer(type) == true)
		{
			return BaseDAO.DRIVER_SQL_SERVER_2005;
		}
		else if (isOdbc(type) == true)
		{
			return BaseDAO.DRIVER_ODBC;
		}
		else if (isSqlLite(type) == true)
		{
			return BaseDAO.DRIVER_SQLITE;
		}
		else
		{
			return "";
		}
	}

	public static String getUrl(String type, String host, String db)
	{
		return getUrl(type, host, db, 0);
	}

	public static String getUrl(String type, String host, String db, int port)
	{
		if (isMySql(type) == true)
		{
			if (port <= 0)
			{
				port = getDefaultPort(type);
			}
			return "jdbc:mysql://" + host + ":" + port + "/" + db;
		}
		else if (isDb2JCC(type) == true)
		{
			return "jdbc:db2://" + host + ":" + port + "/" + db;
		}
		else if (isDb2App(type) == true)
		{
			return "jdbc:db2:" + db;
		}
		else if (isSqlServer(type) == true)
		{
			if (port <= 0)
			{
				port = getDefaultPort(type);
			}
			return "jdbc:microsoft:sqlserver://" + host + ":" + port + ";databaseName=" + db;
		}
		else if (isOdbc(type) == true)
		{
			return "jdbc:odbc:" + db;
		}
		else if (isSqlLite(type) == true)
		{
			return "jdbc:sqlite:" + db;
		}
		else
		{
			return "";
		}
	}
	
	public static int getDefaultPort(String dbType)
	{
		if (dbType == null)
		{
			return 0;
		}
		else if (isMySql(dbType) == true)
		{
			return 3306;
		}
		else if (isSqlServer(dbType) == true)
		{
			return 1443;
		}
		else if (isDb2(dbType) == true)
		{
			return 50000;
		}
		else
		{
			return 0;
		}
	}

	public static Connection getConn(DbConn dbconn, String user, String pass)
	{
		String driver = BaseDAO.getDriver(dbconn.getType());
		String url = BaseDAO.getUrl(dbconn.getType(), dbconn.getHost(), dbconn.getDb(), dbconn.getPort());
		return getConn(driver, url, user, pass);
	}
	
	public static Connection getConn(String driver, String url, String user, String pass)
	{
		final String METHOD = "getConn";
		
		try
		{
			//LoggerUtil.log(METHOD, "DEBUG: com.cffreedom.db.BaseDAO: " + METHOD + ": Creating new connection\n   Driver: " + driver + "\n   Url:" + url);
			Class.forName(driver);
			return DriverManager.getConnection(url, user, pass);
		}
		catch (SQLException e)
		{
			e.printStackTrace(); 
			return null;
		}
		catch (ClassNotFoundException e)
		{
			LoggerUtil.log(METHOD, "ERROR: " + url + ": ClassNotFoundException (check that the driver is on the CLASSPATH): " + e.getMessage());
			e.printStackTrace(); 
			return null;
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
	
	public static String getTestSql(String dbType)
	{
		if (isDb2(dbType) == true)
		{
			 return BaseDAO.SQL_TEST_DB2;
		}
		else if (isSqlServer(dbType) == true)
		{
			return BaseDAO.SQL_TEST_SQLSERVER;
		}
		else
		{
			return null;
		}
	}	
	
	public static boolean isOdbc(String dbType)
	{
		if (dbType.equalsIgnoreCase(BaseDAO.TYPE_ODBC) == true){
			return true;
		}else{
			return false;
		}
	}	
	
	public static boolean isMySql(String dbType)
	{
		if (dbType.equalsIgnoreCase(BaseDAO.TYPE_MYSQL) == true){
			return true;
		}else{
			return false;
		}
	}	
	
	public static boolean isSqlServer(String dbType)
	{
		if (dbType.equalsIgnoreCase(BaseDAO.TYPE_SQL_SERVER) == true){
			return true;
		}else{
			return false;
		}
	}	
	
	public static boolean isSqlLite(String dbType)
	{
		if (dbType.equalsIgnoreCase(BaseDAO.TYPE_SQLITE) == true){
			return true;
		}else{
			return false;
		}
	}	
	
	public static boolean isDb2(String dbType)
	{
		if 
		(
			(isDb2Misc(dbType) == true) ||
			(isDb2App(dbType) == true) ||
			(isDb2JCC(dbType) == true)
		)
		{
			return true;
		}
		else
		{
			return false;
		}
	}	
	
	public static boolean isDb2Misc(String dbType)
	{
		if (dbType.equalsIgnoreCase(BaseDAO.TYPE_DB2) == true){
			return true;
		}else{
			return false;
		}
	}	
	
	public static boolean isDb2JCC(String dbType)
	{
		if (dbType.equalsIgnoreCase(BaseDAO.TYPE_DB2_JCC) == true){
			return true;
		}else{
			return false;
		}
	}
	
	public static boolean isDb2App(String dbType)
	{
		if (dbType.equalsIgnoreCase(BaseDAO.TYPE_DB2_APP) == true){
			return true;
		}else{
			return false;
		}
	}
}