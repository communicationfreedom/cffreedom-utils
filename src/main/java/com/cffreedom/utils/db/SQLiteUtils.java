package com.cffreedom.utils.db;

import java.sql.Connection;

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
	/**
	 * Get a connection to the DB file passed in (note: File gets created if it does not already exist)
	 * @param file File to hold the DB
	 * @return
	 * @throws DbException
	 * @throws InfrastructureException
	 */
	public static Connection getConnection(String file) throws DbException, InfrastructureException
	{
		String url = DbUtils.getUrl(DbUtils.TYPE_SQLITE, null, file);
		return DbUtils.getConnection(DbUtils.DRIVER_SQLITE, url, null, null);
	}
	
	/**
	 * Create a DB for use in memory. Will be destroyed when the app exists
	 * @return
	 * @throws DbException
	 * @throws InfrastructureException
	 */
	public static Connection getConnectionForInMemoryDb() throws DbException, InfrastructureException
	{
		String url = "jdbc:sqlite::memory:";
		return DbUtils.getConnection(DbUtils.DRIVER_SQLITE, url, null, null);
	}
}
