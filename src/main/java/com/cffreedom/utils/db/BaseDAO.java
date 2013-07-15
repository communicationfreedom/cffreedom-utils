package com.cffreedom.utils.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cffreedom.utils.ConversionUtils;
import com.cffreedom.utils.db.pool.ConnectionFactory;

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
 * 2013-07-15	markjacobsen.net 	Moved all functionality to DbUtils
 */
public class BaseDAO
{
	private static final Logger logger = LoggerFactory.getLogger("com.cffreedom.utils.db.BaseDAO");
	
	ConnectionFactory factory = null;

	public BaseDAO()
	{
		// do nothing
	}

	public BaseDAO(ConnectionFactory factory)
	{
		this.factory = factory;
	}
}
