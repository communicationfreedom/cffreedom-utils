package com.cffreedom.utils.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cffreedom.beans.DbDriver;
import com.cffreedom.beans.DbType;
import com.cffreedom.exceptions.DbException;
import com.cffreedom.exceptions.InfrastructureException;

/**
 * Original Class: com.cffreedom.utils.db.HSQLDBUtils
 * @author markjacobsen.net
 * Copyright: Communication Freedom, LLC - http://www.communicationfreedom.com
 * 
 * Free to use, modify, redistribute.  Must keep full class header including 
 * copyright and note your modifications.
 * 
 * If this helped you out or saved you time, please consider...
 * 1) My wishlist: http://markjacobsen.net/wishlist/
 * 2) Following me on twitter: @MarkJacobsen or @cffreedom
 * 3) Linking to: http://markjacobsen.net
 * 
 * Changes:
 * 2013-12-07 	MarkJacobsen.net 	Created
 */
public class HSQLDBUtils 
{
	private static final Logger logger = LoggerFactory.getLogger(HSQLDBUtils.class);
	public final static String DRIVER = DbDriver.HSQLDB.value;
	public final static DbType DB_TYPE = DbType.HSQLDB;
	
	/**
	 * Get a connection to the DB file passed in (note: File gets created if it does not already exist)
	 * @param file File to hold the DB
	 * @return
	 * @throws DbException
	 * @throws InfrastructureException
	 */
	public static Connection getConnection(String file) throws DbException, InfrastructureException
	{
		String url = DbUtils.getUrl(DB_TYPE, null, file);
		return DbUtils.getConnection(DRIVER, url, "sa", null);
	}
	
	public static boolean tableExists(Connection conn, String table)
	{
		ResultSet rs = null;
				
		try
		{
			String sql = "SELECT TABLE_NAME AS TABLE_NM FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = \'TABLE\' AND TABLE_NAME='"+table+"' ORDER BY TABLE_NAME";
			rs = DbUtils.getResultSet(conn, sql);
			while (rs.next())
			{
				return true;
			}
		}
		catch (Exception e)
		{
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			DbUtils.cleanup(conn, null, rs);
		}
		
		return false;
	}
	
	public static String getCreateTableSql(String name, Map<String, String> columns)
	{
		String ret = "CREATE TABLE "+name+" (";
		int counter = 0;
		for (String key : columns.keySet())
		{
			counter++;
			ret += key + " " + columns.get(key);
			if (counter < columns.size()) { ret += ", "; }
		}
		ret += ")";
		
		return ret;
	}
}
