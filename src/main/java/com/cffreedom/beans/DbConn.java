package com.cffreedom.beans;

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
 * Changes
 * 2013-05-23 	markjacobsen.net 	Added setUser() and setPassword()
 */
public class DbConn
{
	String type = null;
	String host = null;
	String db = null;
	int port = 0;
	String user = null;
	String pass = null;
	
	public DbConn(String type, String host, String db, int port)
	{
		this(type, host, db, port, null, null);
	}
	
	public DbConn(String type, String host, String db, String user, String pass)
	{
		this(type, host, db, 0, user, pass);
	}
	
	public DbConn(String type, String host, String db, int port, String user, String pass)
	{
		this.type = type;
		this.host = host;
		this.db = db;
		this.port = port;
		this.user = user;
		this.pass = pass;
	}
	
	public String getType() { return this.type; }
	public String getHost() { return this.host; }
	public String getDb() { return this.db; }
	public int getPort() { return this.port; }
	public String getUser() { return this.user; }
	public String getPassword() { return this.pass; } 
	
	public void setUser(String s) { this.user = s; }
	public void setPassword(String s) { this.pass = s; }
}
