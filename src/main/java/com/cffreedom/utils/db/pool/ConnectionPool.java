package com.cffreedom.utils.db.pool;

import java.sql.*;
import java.util.*;

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
 */
public class ConnectionPool
{
	private final LoggerUtil logger = new LoggerUtil(LoggerUtil.FAMILY_UTIL, this.getClass().getPackage().getName() + "." + this.getClass().getSimpleName());
	
	private String 					poolName;
	private Vector<DbConnection> 	connections;
	private String 					driver;
	private String 					url;
	private String 					user;
	private String 					password;
	final private long 				secondsUntilStale = 60;
	private ConnectionReaper 		reaper;
	final private int 				poolsize = 20;

	public ConnectionPool(String poolName, String driver, String url, String user, String password)
	{
		this.poolName = poolName;
		this.driver = driver;
		this.url = url;
		this.user = user;
		this.password = password;
		this.connections = new Vector<DbConnection>(poolsize);
		logger.logDebug("constructor", "Creating pool " + this.getDetailName());
		
		this.reaper = new ConnectionReaper(this);
		this.reaper.start();
	}

	protected synchronized void reapConnections()
	{
		final String METHOD = "reapConnections";
		
		long stale = System.currentTimeMillis() - (secondsUntilStale * 1000);
		
		if ( (this.connections != null) && (this.getPoolSize() > 0) )
		{
			Enumeration<DbConnection> connlist = this.connections.elements();
	    
			while((connlist != null) && (connlist.hasMoreElements()))
			{
				DbConnection conn = (DbConnection)connlist.nextElement();
	
				//logger.logDebug(METHOD, "In Use: " + conn.inUse());
				//logger.logDebug(METHOD, "Last Use: " + conn.getLastUse());
				//logger.logDebug(METHOD, "Stale: " + stale);
				
				if  (
					(conn.inUse() == false) && 
					(stale > conn.getLastUse())
	        		)
				{
					//logger.logDebug(METHOD, "Removing stale connection");
					this.removeConnection(conn, "expired");
				}
				else if (conn.validate() == false)
				{
					this.removeConnection(conn, "invalid");
				}
			}
			
			//logger.logDebug(METHOD, "Current connections: " + this.getPoolSize());
		}
		
		if ((this.connections == null) || (this.getPoolSize() == 0))
		{
			logger.logInfo(METHOD, "No connections to reap. Closing pool: " + this.getPoolName());
			this.close();
		}
	}

	public synchronized void close()
	{        
		final String METHOD = "close";
		
		try
		{
			if ( (this.connections != null) && (this.getPoolSize() > 0) )
			{
				logger.logDebug(METHOD, "Closing all connections in pool: " + this.getPoolName());
				Enumeration<DbConnection> connlist = this.connections.elements();
		
				while((connlist != null) && (connlist.hasMoreElements()))
				{
					DbConnection conn = (DbConnection)connlist.nextElement();
					this.removeConnection(conn, "closing pool");
				}
			}
			else
			{
				logger.logDebug(METHOD, "No connections to close in pool: " + this.getPoolName());
			}
		}
		catch (Exception e)
		{
			logger.logError(METHOD, e.getMessage(), e);
		}
		finally
		{
			try { this.reaper.shutdown(); } catch (Exception e) {}
			try { this.connections = null; } catch (Exception e) {}
		}
	}

	private synchronized void removeConnection(DbConnection conn, String reason)
	{
		final String METHOD = "removeConnection";
		logger.logDebug(METHOD, "Removing Connection from pool: " + this.getPoolName() + ", reason: " + reason);
		try { conn.close(); } catch (Exception e) {}
		this.connections.removeElement(conn);
		logger.logDebug(METHOD, this.getPoolSize() + " Connections currently in pool: " + this.getPoolName());
	}

   public synchronized void returnConnection(DbConnection conn) {
	   conn.expireLease();  // Mark the connection as not in use, but leave in the pool for reuse
   }

	public synchronized DbConnection getConnection() throws SQLException, ClassNotFoundException
	{
		final String METHOD = "getConnection";
		DbConnection dbConnection;
		
		logger.logDebug(METHOD, "Getting connection from pool: " + this.getPoolName());
		
		if (this.connections == null)
		{
			logger.logDebug(METHOD, "Reinitializing pool connections: " + this.getPoolName());
			this.connections = new Vector<DbConnection>(poolsize);
		}
		
		for(int i = 0; i < this.getPoolSize(); i++)
		{
			dbConnection = (DbConnection)this.connections.elementAt(i);
			if (dbConnection.lease() == true)
			{
				if (dbConnection.validate() == true)
				{
					logger.logDebug(METHOD, "Returning cached Connection from pool: " + this.getPoolName());
					return dbConnection;
				}
				else
				{
					this.removeConnection(dbConnection, "invalid connection");
				}
			}
		}

		// A connection was not obtained from the pool so create a new one
		logger.logDebug(METHOD, "Creating new connection in pool: " + this.getPoolName());
		Class.forName(this.driver);
		Connection conn = DriverManager.getConnection(this.url, this.user, this.password);
		dbConnection = new DbConnection(conn, this);
		//logger.logDebug(METHOD, "New Connection created in pool: " + this.getPoolName());
		dbConnection.lease();
		this.connections.addElement(dbConnection);
		//logger.logDebug(METHOD, "Returning new Connection from pool: " + this.getPoolName());
		logger.logDebug(METHOD, this.getPoolSize() + " Connections currently in pool: " + this.getPoolName());
		return dbConnection;
	}
	
	public synchronized int getPoolSize()
	{
		return this.connections.size();
	}
   
   private String getPoolName()
   {
	   return this.poolName;
   }
   
   private String getDetailName()
   {
	   return "** " + this.getPoolName() + " ** " + this.driver + " ** " + this.url + " ** " + this.user + " **";
   }
}