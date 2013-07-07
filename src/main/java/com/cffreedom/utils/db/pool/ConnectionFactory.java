package com.cffreedom.utils.db.pool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cffreedom.beans.DbConn;

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
 * 2013-06-27	markjacobsen.net 	Made a singleton (Hat Tip: http://stackoverflow.com/questions/11165852/java-singleton-and-synchronization)
 */
public class ConnectionFactory
{
	private static final Logger logger = LoggerFactory.getLogger("com.cffreedom.utils.db.pool.ConnectionFactory");

	private Hashtable<String, ConnectionPool> connectionPools = new Hashtable<String, ConnectionPool>();
	private Hashtable<String, DbConn> dbConns = new Hashtable<String, DbConn>();
	
	private static class Loader {
		private static final ConnectionFactory INSTANCE = new ConnectionFactory();
	}

	private ConnectionFactory()
	{
		//Exists only to defeat external instantiation
	}

	public static ConnectionFactory getInstance()
	{
		return Loader.INSTANCE;
	}

	/**
	 * Returns a cached DbConn if it exists, otherwise null
	 * @param poolKey Cached DbConn to return
	 * @return Cached DbConn if it exists, otherwise null
	 */
	private DbConn getCachedDbConn(String poolKey)
	{
		if (this.containsCachedDbConn(poolKey) == true)
		{
			return this.dbConns.get(poolKey);
		}
		else
		{
			logger.warn("Cached DbConn not found: {}", poolKey);
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
		if (this.containsPool(poolKey) == true)
		{
			return this.connectionPools.get(poolKey);
		}
		else
		{
			logger.warn("Connection pool not found: {}", poolKey);
			return null;
		}
	}
	
	public synchronized boolean containsPool(String poolKey)
	{
		return this.connectionPools.containsKey(poolKey);
	}
	
	private boolean containsCachedDbConn(String poolKey)
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
	public synchronized Connection getConnection(String poolKey)
	{
		Connection conn = null;

		if (this.containsPool(poolKey) == false)
		{
			// Assume JNDI since we don't have a record of this pool
			logger.debug("Getting jndi connection: {}", poolKey);
			try
			{
				conn = this.getJndiConn(poolKey);
				logger.debug("Got jndi connection: {}", poolKey);
			}
			catch (SQLException sqe)
			{
				logger.warn("JNDI Connection not found for key: {}", poolKey);
			}
		}
		else
		{
			// Non-JNDI
			logger.debug("Getting non-jndi connection: {}", poolKey);
			try
			{
				ConnectionPool pool = null;
				
				if (this.getConnectionPool(poolKey) != null)
				{
					pool = this.getConnectionPool(poolKey);
				}
				else if (this.containsCachedDbConn(poolKey) == true)
				{
					logger.info("Restoring pool from cached DbConn");
					this.removePool(poolKey);
					this.addPool(poolKey, this.getCachedDbConn(poolKey));
					pool = this.getConnectionPool(poolKey);
				}
				else
				{
					logger.warn("ConnectionPool not found for key: {}", poolKey);
				}
				
				if (pool != null)
				{
					conn = pool.getConnection();
					logger.debug("Got non-jndi connection: {}", poolKey);
				}
				else
				{
					logger.info("Removing invalid pool: {}", poolKey);
					this.removePool(poolKey);
					this.removeCachedDbConn(poolKey);
				}
			}
			catch (SQLException sqe)
			{
				logger.error("SQLException getting non-jndi connection: {}", poolKey);
				conn = null;
			}
			catch (ClassNotFoundException cnfe)
			{
				logger.error("ClassNotFoundException getting non-jndi connection: {}", poolKey);
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
	public synchronized boolean addPool(String poolKey, String driver, String url, String username, String password)
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
	public synchronized boolean addPool(String poolKey, DbConn dbconn)
	{
		if (this.containsPool(poolKey) == false)
		{
			logger.debug("Creating pool: {}", poolKey);
			this.connectionPools.put(poolKey, new ConnectionPool(poolKey, dbconn.getDriver(), dbconn.getUrl(), dbconn.getUser(), dbconn.getPassword()));
			
			if (this.containsCachedDbConn(poolKey) == false)
			{
				logger.debug("Caching DbConn: {}", poolKey);
				this.dbConns.put(poolKey, dbconn);
			}
			
			return true;
		}
		else
		{
			logger.warn("Pool already exists with name: {}", poolKey);
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
	
	private void closePool(String poolKey)
	{
		logger.debug("Closing ConnectionFactory pool: {}", poolKey);
		
		if (this.containsPool(poolKey) == true)
		{
			try
			{
				ConnectionPool pool = this.getConnectionPool(poolKey);
				pool.close();
			}
			catch (Exception e)
			{
				logger.error(e.getMessage());
			}
			
			this.removePool(poolKey);
		}
		
		this.removeCachedDbConn(poolKey);
	}

	/**
	 * Close the factory - i.e. all ConnectionPools
	 */
	public synchronized void close()
	{
		logger.debug("Closing ConnectionFactory");

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
			logger.error(e.getMessage());
		}
		finally
		{
			try{ this.connectionPools = null; } catch (Exception e){}
		}

	}

	private Connection getJndiConn(String dsn) throws SQLException
	{
		logger.debug("Getting connection: {}", dsn);
		return null; // DataSource.getConnection(dsn);
	}
}
