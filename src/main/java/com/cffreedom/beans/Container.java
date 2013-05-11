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
 * Changes:
 * 2013-05-10 	markjacobsen.net	Renamed name to value
 */
public class Container
{
	private String code;
	private String value;
	
	public Container(String code, String value)
	{
		this.code = code;
		this.value = value;
	}
	
	public String toString() { return this.getCode() + "::" + this.getValue(); }
	public String getCode() { return this.code; }
	public String getValue() { return this.value; }
}
