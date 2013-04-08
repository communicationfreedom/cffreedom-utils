package com.cffreedom.security;

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
public class SecurityUtils
{
	/**
	 * This is NOT a very secure method to encrypt/decrypt, but can
	 * be used in a pinch
	 * @param input The string to encrypt of decrypt
	 * @return The encrypted or decrypted string
	 */
	public static String encryptDecrypt(String input)
	{
		byte[] workArray = input.getBytes();
		StringBuffer work = new StringBuffer(input.length());
		
		for (byte i = 0; i < workArray.length; i++)
		{
			workArray[i] ^= (i + 1);
			work.append((char)workArray[i]);
		}
		
		return work.toString();
	}
}
