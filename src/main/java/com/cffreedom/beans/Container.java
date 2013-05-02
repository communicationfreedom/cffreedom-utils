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
 */
public class Container
{
	private String code;
	private String name;
	
	public Container(String code, String name)
	{
		this.code = code;
		this.name = name;
	}
	
	public String toString() { return this.getCode() + "::" + this.getName(); }
	public String getCode() { return this.code; }
	public String getName() { return this.name; }
}
