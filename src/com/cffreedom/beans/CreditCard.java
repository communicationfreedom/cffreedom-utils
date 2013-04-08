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
public class CreditCard
{
	private String fullName;
	private String cardNum;
	private String secNum;
	private int expMonth;
	private int expYear;
	
	public CreditCard(String fullName, String cardNum, String secNum, int expMonth, int expYear)
	{
		super();
		this.fullName = fullName;
		this.cardNum = cardNum;
		this.secNum = secNum;
		this.expMonth = expMonth;
		this.expYear = expYear;
	}
	
	public String getFullName()
	{
		return fullName;
	}
	public String getCardNum()
	{
		return cardNum;
	}
	public String getSecNum()
	{
		return secNum;
	}
	public int getExpMonth()
	{
		return expMonth;
	}
	public int getExpYear()
	{
		return expYear;
	}
}
