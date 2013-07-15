package com.cffreedom.utils.db;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cffreedom.beans.DbConn;
import com.cffreedom.exceptions.InfrastructureException;
import com.cffreedom.utils.ConversionUtils;
import com.cffreedom.utils.SystemUtils;
import com.cffreedom.utils.Utils;
import com.cffreedom.utils.db.pool.ConnectionFactory;
import com.cffreedom.utils.file.FileUtils;
import com.cffreedom.utils.security.SecurityUtils;

/**
 * Automated layer for accessing DB Connections that should guarantee that
 * the user is not prompted for any information.
 * 
 * Original Class: com.cffreedom.utils.db.ConnectionManager
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
 * 2013-06-25 	markjacobsen.net 	Added connection pooling to getConnection()
 * 2013-07-15	markjacobsne.net 	Replaced the use of KeyValueFileMgr with a properties file
 */
public class ConnectionManager
{
	public static final String DEFAULT_FILE = SystemUtils.getMyCFConfigDir() + SystemUtils.getPathSeparator() + "dbconn.properties";
	private static final Logger logger = LoggerFactory.getLogger("com.cffreedom.utils.db.ConnectionManager");
	private HashMap<String, DbConn> conns = new HashMap<String, DbConn>();
	private String file = null;
	private ConnectionFactory connFactory = null;
	
	public ConnectionManager() throws InfrastructureException, IOException
	{
		this(ConnectionManager.DEFAULT_FILE);
	}
	
	public ConnectionManager(String file) throws InfrastructureException, IOException
	{
		this(file, false);
	}
	
	public ConnectionManager(boolean cacheConnections) throws InfrastructureException, IOException
	{
		this(ConnectionManager.DEFAULT_FILE, cacheConnections);
	}
	
	public ConnectionManager(String file, boolean cacheConnections) throws InfrastructureException, IOException
	{		
		this.loadConnectionFile(file);

		if (cacheConnections == true)
		{
			logger.info("Using ConnectionFactory/Connection Pooling");
			this.connFactory = ConnectionFactory.getInstance();
		}
	}
	
	private void save()
	{
		ArrayList<String> lines = new ArrayList<String>();
		logger.debug("Saving to file {}", this.getConnectionFile());
		
		lines.add("#--------------------------------------------------------------------------------------");
		lines.add("# While it is not recommended, you can put usernames and passwords into this file.");
		lines.add("# Passwords do need to be encrypted using the SecurityUtils class.");
		lines.add("# It is suggested that you use the DbConnManager app in cffreedom-cl-apps to maintain");
		lines.add("# this file.");
		lines.add("#--------------------------------------------------------------------------------------");
		lines.add("");
		lines.add("keys=" + ConversionUtils.toDelimitedString(this.conns.keySet(), ","));
		lines.add("");
		
		for (String entry : this.conns.keySet())
		{
			logger.trace(entry);
			DbConn conn = this.getDbConn(entry);
			lines.add(entry + ".db=" + conn.getDb());
			lines.add(entry + ".type=" + conn.getType());
			lines.add(entry + ".host=" + conn.getHost());
			lines.add(entry + ".port=" + conn.getPort());
			lines.add(entry + ".user=" + conn.getUser());
			lines.add(entry + ".password=" + SecurityUtils.encryptDecrypt(conn.getPassword()));
			lines.add("");
		}
		
		FileUtils.writeLinesToFile(this.getConnectionFile(), lines);
	}
	
	public void loadConnectionFile(String file) throws InfrastructureException, IOException
	{
		if (FileUtils.fileExists(file) == true)
		{
			logger.debug("Loading file: {}", file);
			this.file = file;
			
			Properties props = new Properties();
			FileInputStream in = new FileInputStream(this.file);
			props.load(in);
			in.close();
			
			String[] keys = props.getProperty("keys").split(",");
			
			for (String key : keys)
			{
				logger.debug(key);
				String type = props.getProperty(key + ".type");
				String host = props.getProperty(key + ".host");
				String db = props.getProperty(key + ".db");
				String port = props.getProperty(key + ".port");
				String user = props.getProperty(key + ".user");
				String password = props.getProperty(key + ".password");
				
				if (port == null) { port = "0"; }
				
				DbConn dbconn = new DbConn(DbUtils.getDriver(type),
										DbUtils.getUrl(type, host, db), 
										type,
										host,
										db,
										ConversionUtils.toInt(port));
				
				if (user != null) { dbconn.setUser(user); }
				if (password != null) { dbconn.setPassword(SecurityUtils.encryptDecrypt(password)); }

				this.conns.put(key, dbconn);
			}
		}
		else
		{
			throw new InfrastructureException("File does not exist: " + file);
		}
	}
	
	public void close()
	{
		try{ this.connFactory.close(); } catch (Exception e){}
	}
	
	public String getConnectionFile() { return this.file; }
	
	public boolean keyExists(String key)
	{
		return this.conns.containsKey(key);
	}
	
	public DbConn getDbConn(String key)
	{
		return this.conns.get(key);
	}
	
	public boolean cacheConnections() { if (this.connFactory != null){ return true; }else{ return false; } }
		
	public Connection getConnection(String key, String user, String pass)
	{
		if (this.cacheConnections() == true)
		{
			if (this.connFactory.containsPool(key) == false)
			{
				DbConn dbconn = this.getDbConn(key);
				dbconn.setUser(user);
				dbconn.setPassword(pass);
				this.connFactory.addPool(key, dbconn);
			}
			
			return this.connFactory.getConnection(key);
		}
		else
		{
			DbConn dbconn = this.getDbConn(key);
			return DbUtils.getConn(dbconn.getDriver(), dbconn.getUrl(), user, pass);
		}
	}
	
	public boolean addConnection(String key, DbConn dbconn)
	{
		if (this.conns.containsKey(key) == false)
		{
			this.conns.put(key, dbconn);
			this.save();
			return true;
		}
		else
		{
			logger.error("A connection named {} already exists", key);
			return false;
		}
	}
	
	public boolean updateConnection(String key, DbConn dbconn)
	{
		deleteConnection(key);
		return addConnection(key, dbconn);
	}
	
	public boolean deleteConnection(String key)
	{
		if (this.conns.containsKey(key) == true)
		{
			this.conns.remove(key);
			this.save();
			return true;
		}
		else
		{
			logger.error("A connection named {} does not exist");
			return false;
		}
	}
	
	public void printKeys()
	{
		Utils.output("Keys");
		Utils.output("======================");
		if ((this.conns != null) && (this.conns.size() > 0))
		{
			for(String key : this.conns.keySet())
			{
				Utils.output(key);
			}
		}
	}
	
	public void printConnInfo(String key)
	{
		DbConn dbconn = getDbConn(key);
		Utils.output("");
		Utils.output("Key = " + key);
		Utils.output("Type = " + dbconn.getType());
		Utils.output("DB = " + dbconn.getDb());
		Utils.output("Host = " + dbconn.getHost());
		Utils.output("Port = " + dbconn.getPort());
	}
	
	public boolean testConnection(String key, String user, String pass)
	{
		DbConn dbconn = getDbConn(key);
		boolean success = DbUtils.testConnection(dbconn, user, pass);
		if (success == true)
		{
			Utils.output("Test SQL succeeded for " + key);
		}
		else
		{
			Utils.output("ERROR: Running test SQL for " + key);
		}
		return success;
	}
}
