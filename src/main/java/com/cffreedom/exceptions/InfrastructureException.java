package com.cffreedom.exceptions;

/**
 * Original Class: com.cffreedom.exceptions.InfrastructureException
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
 * 2013-07-10	markjacobsen.net 	Created
 * 2013-07-15	markjacobsen.net 	Removed constructors
 */
public class InfrastructureException extends Exception
{
	private static final long serialVersionUID = 1L;

	public InfrastructureException(Throwable exception)
	{
		super(exception);
	}
	
	public InfrastructureException(String message, Throwable exception)
	{
		super(message, exception);
	}
	
	public InfrastructureException(String message)
	{
		super(message);
	}
}
