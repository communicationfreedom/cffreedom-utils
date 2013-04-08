package com.cffreedom.beans;

import java.io.Serializable;

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
public class PasswordEntry implements Serializable
{
	private static final long serialVersionUID = 1L;
	private String username;
	private String password;
	private String note;
	
	public PasswordEntry(String user, String pass, String note)
	{
		this.username = user;
		this.password = pass;
		this.note = note;
	}
	
	public String getUsername() { return this.username; }
	public String getPassword() { return this.password; }
	public String getNote() { return this.note; }
}
