package com.cffreedom.utils.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Original Class: com.cffreedom.utils.db.BaseDAO
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
 * 2013-07-15	markjacobsen.net 	Using new and improved ConnectionManager
 */
public class BaseDAO
{
	private static final Logger logger = LoggerFactory.getLogger("com.cffreedom.utils.db.BaseDAO");
	
	ConnectionManager cm = null;

	public BaseDAO()
	{
		logger.debug("Initialized with no ConnectionManager");
	}

	public BaseDAO(ConnectionManager cm)
	{
		logger.debug("Initializing with passed in ConnectionManager");
		this.cm = cm;
	}
}
