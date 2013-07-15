package com.cffreedom.exceptions;

/**
 * Original Class: com.cffreedom.exceptions.FileSystemException
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
public class FileSystemException extends Exception
{
	private static final long serialVersionUID = 1L;

	public FileSystemException(Throwable exception)
	{
		super(exception);
	}
	
	public FileSystemException(String message, Throwable exception)
	{
		super(message, exception);
	}
	
	public FileSystemException(String message)
	{
		super(message);
	}
}
