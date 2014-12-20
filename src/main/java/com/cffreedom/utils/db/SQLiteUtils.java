package com.cffreedom.utils.db;

import java.sql.Connection;
import java.sql.ResultSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cffreedom.beans.DbDriver;
import com.cffreedom.beans.DbType;
import com.cffreedom.exceptions.DbException;
import com.cffreedom.exceptions.InfrastructureException;

/**
 * Original Class: com.cffreedom.utils.db.SQLiteUtils
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
 * 2013-11-08 	MarkJacobsen.net 	Created
 */
public class SQLiteUtils 
{
	private static final Logger logger = LoggerFactory.getLogger(SQLiteUtils.class);
	public final static String DRIVER = DbDriver.SQLITE.value;
	public final static DbType DB_TYPE = DbType.SQLITE;
	public final static String URL_IN_MEMORY_DB = DbUtils.getUrl(DbType.SQLITE, null, null);
	
	/**
	 * Get a connection to the DB file passed in (note: File gets created if it does not already exist)
	 * @param file File to hold the DB
	 * @return
	 * @throws DbException
	 * @throws InfrastructureException
	 */
	public static Connection getConnection(String file) throws DbException, InfrastructureException
	{
		String url = DbUtils.getUrl(DbType.SQLITE, null, file);
		return DbUtils.getConnection(DRIVER, url, null, null);
	}
	
	/**
	 * Create a DB for use in memory. Will be destroyed when the app exists
	 * @return
	 * @throws DbException
	 * @throws InfrastructureException
	 */
	public static Connection getConnectionForInMemoryDb() throws DbException, InfrastructureException
	{
		return DbUtils.getConnection(DRIVER, URL_IN_MEMORY_DB, null, null);
	}
	
	public static boolean tableExists(Connection conn, String table)
	{
		ResultSet rs = null;
				
		try
		{
			String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='"+table+"'";
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
}
