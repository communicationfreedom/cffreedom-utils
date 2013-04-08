package com.cffreedom.db;


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
class ConnectionReaper extends Thread
{
	private final LoggerUtil logger = new LoggerUtil(LoggerUtil.FAMILY_UTIL, this.getClass().getPackage().getName() + "." + this.getClass().getSimpleName());
		
    private ConnectionPool 	pool;
    private final long 		delay = 300000;
    private boolean 		shutdown = false;

    ConnectionReaper(ConnectionPool pool)
    {
        this.pool=pool;
    }

    public void run()
    {
    	final String METHOD = "run";
    	
        while(this.shutdown == false)
        {
           try
           {
              sleep(this.delay);
           }
           catch( InterruptedException e) { }
           
           logger.logDebug(METHOD, "Calling reapConnections");
           this.pool.reapConnections();
        }
        
        logger.logDebug(METHOD, "Exiting");
    }
    
    protected void shutdown()
    {
    	this.shutdown = true;
    	this.interrupt();
    }
}

public class ConnectionPool
{
	private final LoggerUtil logger = new LoggerUtil(LoggerUtil.FAMILY_UTIL, this.getClass().getPackage().getName() + "." + this.getClass().getSimpleName());
	
	private String 					poolName;
	private Vector<DbConnection> 	connections;
	private String 					driver;
	private String 					url;
	private String 					user;
	private String 					password;
	final private long 				timeout = 60000;
	private ConnectionReaper 		reaper;
	final private int 				poolsize = 10;

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

	public synchronized void reapConnections()
	{
		long stale = System.currentTimeMillis() - timeout;
		
		if ( (this.connections != null) && (this.connections.size() > 0) )
		{
			Enumeration<DbConnection> connlist = this.connections.elements();
	    
			while((connlist != null) && (connlist.hasMoreElements()))
			{
				DbConnection conn = (DbConnection)connlist.nextElement();
	
				if  (
					(conn.inUse()) && 
					(stale >conn.getLastUse()) && 
	        		(!conn.validate())
	        		)
				{
					removeConnection(conn);
				}
			}
		}
	}

	public synchronized void close()
	{        
		final String METHOD = "close";
		
		try
		{
			if ( (this.connections != null) && (this.connections.size() > 0) )
			{
				logger.logDebug(METHOD, "Closing all connections " + this.getPoolName());
				Enumeration<DbConnection> connlist = this.connections.elements();
		
				while((connlist != null) && (connlist.hasMoreElements()))
				{
					DbConnection conn = (DbConnection)connlist.nextElement();
					removeConnection(conn);
				}
			}
			else
			{
				logger.logDebug(METHOD, "No connections to close");
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

	private synchronized void removeConnection(DbConnection conn) {
		this.connections.removeElement(conn);
	}


	public synchronized DbConnection getConnection() throws SQLException, ClassNotFoundException
	{
		final String METHOD = "getConnection";
		DbConnection c;
		
		logger.logDebug(METHOD, "Getting connection " + this.getPoolName());
		for(int i = 0; i < this.connections.size(); i++)
		{
			c = (DbConnection)this.connections.elementAt(i);
			if (c.lease() == true)
			{
				return c;
			}
		}

		logger.logDebug(METHOD, "Creating new connection " + this.getDetailName());
		Class.forName(this.driver);
		Connection conn = DriverManager.getConnection(this.url, this.user, this.password);
		c = new DbConnection(conn, this);
		logger.logDebug(METHOD, "Connection Created");
		c.lease();
		this.connections.addElement(c);
		logger.logDebug(METHOD, "Returning Connection");
		return c;
	} 

   public synchronized void returnConnection(DbConnection conn) {
      conn.expireLease();
   }
   
   public String getPoolName()
   {
	   return this.poolName;
   }
   
   private String getDetailName()
   {
	   return "** " + this.getPoolName() + " ** " + this.driver + " ** " + this.url + " ** " + this.user + " **";
   }
}