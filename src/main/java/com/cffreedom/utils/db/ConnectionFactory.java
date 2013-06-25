package com.cffreedom.utils.db;

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
 */
public class ConnectionFactory
{
	private final LoggerUtil logger = new LoggerUtil(LoggerUtil.FAMILY_UTIL, this.getClass().getPackage().getName() + "." + this.getClass().getSimpleName());

	public static final int FACTORY_TYPE_UNKNOWN = 0;
	public static final int FACTORY_TYPE_JNDI = 1;
	public static final int FACTORY_TYPE_BATCH = 2;

	private int factoryType = FACTORY_TYPE_UNKNOWN;
	private Hashtable<String, ConnectionPool> connectionPools = new Hashtable<String, ConnectionPool>();
	private Hashtable<String, DbConn> dbConns = new Hashtable<String, DbConn>();

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

	/**
	 * Get a Connection (Really a com.cffreedom.utils.db.DbConnection) for the passed
	 * in key.
	 * 
	 * @param key
	 *            DB to get connection for. If an I3K app, the "key" should be
	 *            the jndi datasource name.
	 * @return A Connection for the Key (com.cffreedom.utils.db.DbConnection)
	 */
	public Connection getConnection(String poolKey)
	{
		String METHOD = "getConnection";

		Connection conn = null;

		if (this.getFactoryType() == FACTORY_TYPE_JNDI)
		{
			logger.logDebug(METHOD, "Getting jndi connection: " + poolKey);
			try
			{
				conn = new DbConnection(getI3kConn(poolKey));
				logger.logDebug(METHOD, "Got jndi connection: " + poolKey);
			}
			catch (SQLException sqe)
			{
				logger.logWarn(METHOD, "JNDI Connection not found for key: " + poolKey);
			}
		}

		if ((conn == null) && (this.getFactoryType() != FACTORY_TYPE_JNDI))
		{
			logger.logDebug(METHOD, "Getting non-jndi connection: " + poolKey);
			try
			{
				if (this.containsPool(poolKey) == true)
				{
					ConnectionPool pool = this.getConnectionPool(poolKey);
					
					if ((pool == null) && (this.dbConns.containsKey(poolKey) == true))
					{
						logger.logInfo(METHOD, "Restoring pool from cached DbConn");
						this.connectionPools.remove(poolKey);
						this.addPool(poolKey, this.dbConns.get(poolKey));
					}
					
					conn = pool.getConnection();
					logger.logDebug(METHOD, "Got non-jndi connection: " + poolKey);
				}
				else
				{
					logger.logWarn(METHOD, "ConnectionPool not found for key: " + poolKey);
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

		if ((conn == null) && (this.containsPool(poolKey) == true))
		{
			logger.logInfo(METHOD, "Removing invalid pool: " + poolKey);
			this.connectionPools.remove(poolKey);
		}
		
		return conn;
	}

	public boolean addPool(String poolKey, String driver, String url, String username, String password)
	{
		final String METHOD = "addPool";

		if (this.getConnectionPool(poolKey) == null)
		{
			logger.logDebug(METHOD, "Creating pool: " + poolKey);
			this.connectionPools.put(poolKey, new ConnectionPool(poolKey, driver, url, username, password));
			
			if (this.dbConns.containsKey(poolKey) == false)
			{
				this.dbConns.put(poolKey, new DbConn(driver, url, username, password));
			}
			
			return true;
		}
		else
		{
			logger.logWarn(METHOD, "Pool already exists with name: " + poolKey);
			return false;
		}
	}

	public boolean addPool(String poolKey, DbConn dbconn, String username, String password)
	{
		String driver = BaseDAO.getDriver(dbconn.getType());
		String url = BaseDAO.getUrl(dbconn.getType(), dbconn.getHost(), dbconn.getDb(), dbconn.getPort());
		
		return this.addPool(poolKey, driver, url, username, password);
	}
	
	public boolean addPool(String poolKey, DbConn dbconn)
	{
		String driver = BaseDAO.getDriver(dbconn.getType());
		String url = BaseDAO.getUrl(dbconn.getType(), dbconn.getHost(), dbconn.getDb(), dbconn.getPort());
		
		return this.addPool(poolKey, driver, url, dbconn.getUser(), dbconn.getPassword());
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
			
			this.connectionPools.remove(poolKey);
		}
		
		if (this.dbConns.containsKey(poolKey) == true)
		{
			this.dbConns.remove(poolKey);
		}
	}

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

	private Connection getI3kConn(String dsn) throws SQLException
	{
		final String METHOD = "getI3kConn";

		logger.logDebug(METHOD, "Getting connection: " + dsn);
		return null; // DataSource.getConnection(dsn);
	}
}
