package com.cffreedom.utils.db.pool;

import java.sql.*;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private static final Logger logger = LoggerFactory.getLogger("com.cffreedom.utils.db.pool.ConnectionPool");
	
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
		logger.debug("Creating pool {}", this.getDetailName());
		
		this.reaper = new ConnectionReaper(this);
		this.reaper.start();
	}

	protected synchronized void reapConnections()
	{
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
			logger.info("No connections to reap. Closing pool: {}", this.getPoolName());
			this.close();
		}
	}

	public synchronized void close()
	{        
		try
		{
			if ( (this.connections != null) && (this.getPoolSize() > 0) )
			{
				logger.debug("Closing all connections in pool: {}", this.getPoolName());
				Enumeration<DbConnection> connlist = this.connections.elements();
		
				while((connlist != null) && (connlist.hasMoreElements()))
				{
					DbConnection conn = (DbConnection)connlist.nextElement();
					this.removeConnection(conn, "closing pool");
				}
			}
			else
			{
				logger.debug("No connections to close in pool: {}", this.getPoolName());
			}
		}
		catch (Exception e)
		{
			logger.error(e.getMessage());
		}
		finally
		{
			try { this.reaper.shutdown(); } catch (Exception e) {}
			try { this.connections = null; } catch (Exception e) {}
		}
	}

	private synchronized void removeConnection(DbConnection conn, String reason)
	{
		logger.debug("Removing Connection from pool: {}, reason: {}", this.getPoolName(), reason);
		try { conn.close(); } catch (Exception e) {}
		this.connections.removeElement(conn);
		logger.debug("{} Connections currently in pool: {}", this.getPoolSize(), this.getPoolName());
	}

   public synchronized void returnConnection(DbConnection conn) {
	   conn.expireLease();  // Mark the connection as not in use, but leave in the pool for reuse
   }

	public synchronized DbConnection getConnection() throws SQLException, ClassNotFoundException
	{
		DbConnection dbConnection;
		
		logger.debug("Getting connection from pool: {}", this.getPoolName());
		
		if (this.connections == null)
		{
			logger.debug("Reinitializing pool connections: {}", this.getPoolName());
			this.connections = new Vector<DbConnection>(poolsize);
		}
		
		for(int i = 0; i < this.getPoolSize(); i++)
		{
			dbConnection = (DbConnection)this.connections.elementAt(i);
			if (dbConnection.lease() == true)
			{
				if (dbConnection.validate() == true)
				{
					logger.debug("Returning cached Connection from pool: {}", this.getPoolName());
					return dbConnection;
				}
				else
				{
					this.removeConnection(dbConnection, "invalid connection");
				}
			}
		}

		// A connection was not obtained from the pool so create a new one
		logger.debug("Creating new connection in pool: {}", this.getPoolName());
		Class.forName(this.driver);
		Connection conn = DriverManager.getConnection(this.url, this.user, this.password);
		dbConnection = new DbConnection(conn, this);
		//logger.logDebug(METHOD, "New Connection created in pool: " + this.getPoolName());
		dbConnection.lease();
		this.connections.addElement(dbConnection);
		//logger.logDebug(METHOD, "Returning new Connection from pool: " + this.getPoolName());
		logger.debug("{} Connections currently in pool: {}", this.getPoolSize(), this.getPoolName());
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