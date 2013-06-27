package com.cffreedom.utils.db.pool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;

import com.cffreedom.beans.DbConn;
import com.cffreedom.utils.LoggerUtil;

/**
 * Provides a key/value set of connection pools (essentially a pool of pools)
 * for a single interface into getting connections to more than 1 DB
 * 
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
 * 2013-06-25	markjacobsen.net 	Added containsPool()
 * 2013-06-27	markjacobsen.net 	Made a singleton
 */
public class ConnectionFactory
{
	private static ConnectionFactory instance = null;
	
	private final LoggerUtil logger = new LoggerUtil(LoggerUtil.FAMILY_UTIL, this.getClass().getPackage().getName() + "." + this.getClass().getSimpleName());

	private Hashtable<String, ConnectionPool> connectionPools = new Hashtable<String, ConnectionPool>();
	private Hashtable<String, DbConn> dbConns = new Hashtable<String, DbConn>();

	protected ConnectionFactory()
	{
		//Exists only to defeat instantiation
	}

	public static ConnectionFactory getInstance()
	{
		if (instance == null)
		{
			LoggerUtil.log(LoggerUtil.LEVEL_DEBUG, "ConnectionFactory: getInstance", "Creating new instance");
			instance = new ConnectionFactory();
		}
		return instance;
	}

	/**
	 * Returns a cached DbConn if it exists, otherwise null
	 * @param poolKey Cached DbConn to return
	 * @return Cached DbConn if it exists, otherwise null
	 */
	private DbConn getCachedDbConn(String poolKey)
	{
		String METHOD = "getCachedDbConn";

		if (this.containsCachedDbConn(poolKey) == true)
		{
			return this.dbConns.get(poolKey);
		}
		else
		{
			logger.logWarn(METHOD, "Cached DbConn not found: " + poolKey);
			return null;
		}
	}
	
	/**
	 * Returns a Non-JNDI connection pool if it exists, otherwise null
	 * @param poolKey Non-JNDI pool to return
	 * @return Non-JNDI pool if it exists, otherwise null
	 */
	private ConnectionPool getConnectionPool(String poolKey)
	{
		String METHOD = "getConnectionPool";

		if (this.containsPool(poolKey) == true)
		{
			return this.connectionPools.get(poolKey);
		}
		else
		{
			logger.logWarn(METHOD, "Connection pool not found: " + poolKey);
			return null;
		}
	}
	
	public boolean containsPool(String poolKey)
	{
		return this.connectionPools.containsKey(poolKey);
	}
	
	public boolean containsCachedDbConn(String poolKey)
	{
		return this.dbConns.containsKey(poolKey);
	}

	/**
	 * Get a Connection (Really a com.cffreedom.utils.db.DbConnection) for the passed
	 * in key.
	 * 
	 * @param key DB to get connection for. If the datasource is a JNDI datasource, 
	 * 			  the "key" should be the JNDI datasource name.
	 * @return A Connection for the key
	 */
	public Connection getConnection(String poolKey)
	{
		String METHOD = "getConnection";

		Connection conn = null;

		if (this.containsPool(poolKey) == false)
		{
			// Assume JNDI since we don't have a record of this pool
			logger.logDebug(METHOD, "Getting jndi connection: " + poolKey);
			try
			{
				conn = this.getJndiConn(poolKey);
				logger.logDebug(METHOD, "Got jndi connection: " + poolKey);
			}
			catch (SQLException sqe)
			{
				logger.logWarn(METHOD, "JNDI Connection not found for key: " + poolKey);
			}
		}
		else
		{
			// Non-JNDI
			logger.logDebug(METHOD, "Getting non-jndi connection: " + poolKey);
			try
			{
				ConnectionPool pool = null;
				
				if (this.getConnectionPool(poolKey) != null)
				{
					pool = this.getConnectionPool(poolKey);
				}
				else if (this.containsCachedDbConn(poolKey) == true)
				{
					logger.logInfo(METHOD, "Restoring pool from cached DbConn");
					this.removePool(poolKey);
					this.addPool(poolKey, this.getCachedDbConn(poolKey));
					pool = this.getConnectionPool(poolKey);
				}
				else
				{
					logger.logWarn(METHOD, "ConnectionPool not found for key: " + poolKey);
				}
				
				if (pool != null)
				{
					conn = pool.getConnection();
					logger.logDebug(METHOD, "Got non-jndi connection: " + poolKey);
				}
				else
				{
					logger.logInfo(METHOD, "Removing invalid pool: " + poolKey);
					this.removePool(poolKey);
					this.removeCachedDbConn(poolKey);
				}
			}
			catch (SQLException sqe)
			{
				logger.logError(METHOD, "SQLException getting non-jndi connection: " + poolKey, sqe);
				conn = null;
			}
			catch (ClassNotFoundException cnfe)
			{
				logger.logError(METHOD, "ClassNotFoundException getting non-jndi connection: " + poolKey, cnfe);
				conn = null;
			}
		}
		
		return conn;
	}

	/**
	 * This should only ever be called for Non-JNDI pools.
	 * @param poolKey
	 * @param driver
	 * @param url
	 * @param username
	 * @param password
	 * @return
	 */
	public boolean addPool(String poolKey, String driver, String url, String username, String password)
	{
		DbConn dbconn = new DbConn(driver, url, username, password);
		return this.addPool(poolKey, dbconn);
	}
	
	/**
	 * This should only ever be called for Non-JNDI pools.
	 * @param poolKey
	 * @param jndi
	 * @param dbconn
	 * @return
	 */
	public boolean addPool(String poolKey, DbConn dbconn)
	{
		final String METHOD = "addPool";

		if (this.containsPool(poolKey) == false)
		{
			logger.logDebug(METHOD, "Creating pool: " + poolKey);
			this.connectionPools.put(poolKey, new ConnectionPool(poolKey, dbconn.getDriver(), dbconn.getUrl(), dbconn.getUser(), dbconn.getPassword()));
			
			if (this.containsCachedDbConn(poolKey) == false)
			{
				logger.logDebug(METHOD, "Caching DbConn: " + poolKey);
				this.dbConns.put(poolKey, dbconn);
			}
			
			return true;
		}
		else
		{
			logger.logWarn(METHOD, "Pool already exists with name: " + poolKey);
			return false;
		}
	}
	
	private void removePool(String poolKey)
	{
		if (this.containsPool(poolKey))	{ this.connectionPools.remove(poolKey); }
	}
	
	private void removeCachedDbConn(String poolKey)
	{
		if (this.containsPool(poolKey))	{ this.dbConns.remove(poolKey); }
	}
	
	public void closePool(String poolKey)
	{
		final String METHOD = "closePool";

		logger.logDebug(METHOD, "Closing ConnectionFactory pool: " + poolKey);
		
		if (this.containsPool(poolKey) == true)
		{
			try
			{
				ConnectionPool pool = this.getConnectionPool(poolKey);
				pool.close();
			}
			catch (Exception e)
			{
				logger.logError(METHOD, e.getMessage(), e);
			}
			
			this.removePool(poolKey);
		}
		
		this.removeCachedDbConn(poolKey);
	}

	/**
	 * Close the factory - i.e. all ConnectionPools
	 */
	public void close()
	{
		final String METHOD = "close";

		logger.logDebug(METHOD, "Closing ConnectionFactory");

		try
		{
			if (this.connectionPools != null)
			{
				for (String poolKey : this.connectionPools.keySet())
				{
					this.closePool(poolKey);
				}
			}
		}
		catch (Exception e)
		{
			logger.logError(METHOD, e.getMessage(), e);
		}
		finally
		{
			try{ this.connectionPools = null; } catch (Exception e){}
		}

	}

	private Connection getJndiConn(String dsn) throws SQLException
	{
		final String METHOD = "getJndiConn";

		logger.logDebug(METHOD, "Getting connection: " + dsn);
		return null; // DataSource.getConnection(dsn);
	}
}
