package com.cffreedom.utils.security;

import com.cffreedom.utils.FileUtils;

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
 * 2013-04-11 	markjacobsen.net 	Added savePasswordToFile() and getPasswordFromFile()
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
	
	/**
	 * Use the crappy encryptDecrypt() function and save the value to a file
	 * @param file File to save the pw to
	 * @param password Password to encrypt
	 * @return true on success
	 */
	public static boolean savePasswordToFile(String file, String password)
	{
		return FileUtils.writeStringToFile(file, encryptDecrypt(password), false);
	}
	
	/**
	 * @param file File containing the password
	 * @return The unencrypted password
	 */
	public static String getPasswordFromFile(String file)
	{
		return encryptDecrypt(FileUtils.getFileContents(file));
	}
}
