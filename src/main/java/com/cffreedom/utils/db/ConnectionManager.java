package com.cffreedom.utils.db;

import java.sql.Connection;

import com.cffreedom.beans.DbConn;
import com.cffreedom.exceptions.DbException;
import com.cffreedom.utils.ConversionUtils;
import com.cffreedom.utils.KeyValueFileMgr;
import com.cffreedom.utils.LoggerUtil;
import com.cffreedom.utils.SystemUtils;
import com.cffreedom.utils.Utils;
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
 * 2013-05-06 	markjacobsen.net 	Created
 */
public class ConnectionManager
{
	public static final String DEFAULT_FILE = SystemUtils.getMyCFConfigDir() + SystemUtils.getPathSeparator() + "dbconn.dat";
	private final LoggerUtil logger = new LoggerUtil(LoggerUtil.FAMILY_UTIL, this.getClass().getPackage().getName() + "." + this.getClass().getSimpleName());
	private KeyValueFileMgr kvfm = null;
	private String file = null;
	
	public ConnectionManager() throws DbException
	{
		this(ConnectionManager.DEFAULT_FILE);
	}
	
	public ConnectionManager(String file) throws DbException
	{
		final String METHOD = "init";
		
		if (FileUtils.fileExists(file) == true)
		{
			logger.logDebug(METHOD, "Loading file: " + file);
			this.file = file;
			this.kvfm = new KeyValueFileMgr(this.file);  // init so we can use it from other apps
		}
		else
		{
			throw new DbException(METHOD, "File does not exist: " + file);
		}
	}
	
	public boolean keyExists(String key)
	{
		return this.kvfm.keyExists(key);
	}
	
	public DbConn getDbConn(String key)
	{
		String[] entryVals = this.kvfm.getEntryAsString(key).split("\\|");
		int port = 0;
		if (Utils.isInt(entryVals[3]) == true)
		{
			port = ConversionUtils.toInt(entryVals[3]);
		}
		DbConn dbconn = new DbConn(entryVals[0],
									entryVals[2],
									entryVals[1],
									port);
		return dbconn;
	}
	
	public Connection getConnection(String key, String user, String pass)
	{
		return BaseDAO.getConn(getDbConn(key), user, pass);
	}
	
	private String buildValString(DbConn dbconn)
	{
		return dbconn.getType() + "|" + dbconn.getDb() + "|" + dbconn.getHost() + "|" + dbconn.getPort();
	}
	
	public boolean addConnection(String key, DbConn dbconn)
	{
		return this.kvfm.addEntry(key, this.buildValString(dbconn));
	}
	
	public boolean updateConnection(String key, DbConn dbconn)
	{
		return this.kvfm.updateEntry(key, this.buildValString(dbconn));
	}
	
	public boolean deleteConnection(String key)
	{
		return this.kvfm.removeEntry(key);
	}
}
