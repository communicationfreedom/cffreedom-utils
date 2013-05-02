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
public class Product
{
	int id;
	String planCode;
	String name;
	
	public Product(int id, String planCode, String name)
	{
		super();
		this.id = id;
		this.planCode = planCode;
		this.name = name;
	}
	
	public int getId()
	{
		return id;
	}
	public String getPlanCode()
	{
		return planCode;
	}
	public String getName()
	{
		return name;
	}
	
}
