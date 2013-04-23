package com.cffreedom.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;

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
 * 2013-04-23 	markjacobsen.net 	Removed old ColdFusion connection logic
 */
public class ConnectionFactory
{
	private final LoggerUtil logger = new LoggerUtil(LoggerUtil.FAMILY_UTIL, this.getClass().getPackage().getName() + "." + this.getClass().getSimpleName());

	public static final int FACTORY_TYPE_UNKNOWN = 0;
	public static final int FACTORY_TYPE_JNDI = 1;
	public static final int FACTORY_TYPE_BATCH = 2;

	private int factoryType = FACTORY_TYPE_UNKNOWN;
	private Hashtable<String, ConnectionPool> connectionPools = new Hashtable<String, ConnectionPool>();

	public ConnectionFactory()
	{
		this(FACTORY_TYPE_UNKNOWN);
	}

	public ConnectionFactory(int factoryType)
	{
		logger.logDebug("<init>", "init type: " + factoryType);
		this.setFactoryType(factoryType);
	}

	private void setFactoryType(int factoryType)
	{
		this.factoryType = factoryType;
	}

	private int getFactoryType()
	{
		return this.factoryType;
	}

	private ConnectionPool getConnectionPool(String key)
	{
		String METHOD = "getConnectionPool";

		if (this.connectionPools.containsKey(key) == true)
		{
			return this.connectionPools.get(key);
		}
		else
		{
			logger.logWarn(METHOD, "Connection pool not found: " + key);
			return null;
		}
	}

	/**
	 * Get a Connection (Really a com.cffreedom.db.DbConnection) for the passed
	 * in key.
	 * 
	 * @param key
	 *            DB to get connection for. If an I3K app, the "key" should be
	 *            the jndi datasource name.
	 * @return A Connection for the Key (com.cffreedom.db.DbConnection)
	 */
	public Connection getConnection(String key)
	{
		String METHOD = "getConnection";

		Connection conn = null;

		if (this.getFactoryType() == FACTORY_TYPE_JNDI)
		{
			logger.logDebug(METHOD, "Getting jndi connection: " + key);
			try
			{
				conn = new DbConnection(getI3kConn(key));
				logger.logDebug(METHOD, "Got jndi connection: " + key);
			}
			catch (SQLException sqe)
			{
				logger.logWarn(METHOD, "JNDI Connection not found for key: " + key);
			}
		}

		if ((conn == null) && (this.getFactoryType() != FACTORY_TYPE_JNDI))
		{
			logger.logDebug(METHOD, "Getting non-jndi connection: " + key);
			try
			{
				if (this.connectionPools.containsKey(key) == true)
				{
					conn = this.getConnectionPool(key).getConnection();
					logger.logDebug(METHOD, "Got non-jndi connection: " + key);
				}
				else
				{
					logger.logWarn(METHOD, "ConnectionPool not found for key: " + key);
				}
			}
			catch (SQLException sqe)
			{
				logger.logError(METHOD, "SQLException getting non-jndi connection: " + key, sqe);
				conn = null;
			}
			catch (ClassNotFoundException cnfe)
			{
				logger.logError(METHOD, "ClassNotFoundException getting non-jndi connection: " + key, cnfe);
				conn = null;
			}
		}

		return conn;
	}

	public boolean setDbConnectionInfo(String key, String driver, String url, String username, String password)
	{
		final String METHOD = "setDbConnectionInfo";

		if (this.getConnectionPool(key) == null)
		{
			logger.logDebug(METHOD, "Creating pool: " + key);
			this.connectionPools.put(key, new ConnectionPool(key, driver, url, username, password));
			return true;
		}
		else
		{
			logger.logWarn(METHOD, "Connection already exists with name: " + key);
			return false;
		}
	}

	public void close()
	{
		final String METHOD = "close";

		logger.logDebug(METHOD, "Closing ConnectionFactory");

		try
		{
			if ((this.connectionPools != null) && (this.connectionPools.size() > 0))
			{
				Enumeration<String> keys = this.connectionPools.keys();

				while (keys.hasMoreElements() == true)
				{
					String key = keys.nextElement();
					logger.logDebug(METHOD, "Closing connections in pool: " + key);
					ConnectionPool pool = this.getConnectionPool(key);
					pool.close();
					this.connectionPools.remove(key);
				}
			}
		}
		catch (Exception e)
		{
			logger.logError(METHOD, e.getMessage(), e);
		}
		finally
		{
			try
			{
				this.connectionPools = null;
			}
			catch (Exception e)
			{
			}
		}

	}

	private Connection getI3kConn(String dsn) throws SQLException
	{
		final String METHOD = "getI3kConn";

		logger.logDebug(METHOD, "Getting connection: " + dsn);
		return null; // DataSource.getConnection(dsn);
	}
}
