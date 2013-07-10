package com.cffreedom.exceptions;

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
 * 2013-07-10	markjacobsen.net 	Created
 */
public class ValidationException extends Exception
{
	private static final long serialVersionUID = 1L;

	public ValidationException(Throwable exception)
	{
		super(exception);
	}
	
	public ValidationException(String message, Throwable exception)
	{
		super(message, exception);
	}
	
	public ValidationException(String message)
	{
		super(message);
	}
	
	public ValidationException(String source, String message)
	{
		super(source + ": " + message);
	}
	
	public ValidationException(String source, String message, Throwable exception)
	{
		super(source + ": " + message, exception);
	}
}
