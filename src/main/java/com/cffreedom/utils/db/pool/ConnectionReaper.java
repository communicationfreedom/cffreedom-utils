package com.cffreedom.utils.db.pool;

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
public class ConnectionReaper extends Thread
{
	private final LoggerUtil logger = new LoggerUtil(LoggerUtil.FAMILY_UTIL, this.getClass().getPackage().getName() + "." + this.getClass().getSimpleName());
	
    private ConnectionPool 	pool;
    private final long 		delaySeconds = 2*60;
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
              sleep(this.delaySeconds * 1000);
           }
           catch( InterruptedException e) { }
           
           logger.logDebug(METHOD, "Calling reapConnections");
           this.pool.reapConnections();
        }
        
        logger.logDebug(METHOD, "Exiting pool");
    }
    
    protected void shutdown()
    {
    	this.shutdown = true;
    	this.interrupt();
    }
}
