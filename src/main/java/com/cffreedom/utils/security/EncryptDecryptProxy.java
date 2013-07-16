package com.cffreedom.utils.security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import com.cffreedom.utils.Convert;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Original Class: com.cffreedom.utils.security.EncryptDecryptProxy
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
 * Changes
 * 2013-07-15	markjacobsen.net 	Slight mods for greater backword compatibility with org.apache.commons.codec.binary.Base64
 */
public class EncryptDecryptProxy
{
	private static final Logger logger = LoggerFactory.getLogger("com.cffreedom.utils.security.EncryptDecryptProxy");
	private String secretKey = null;
	private DESKeySpec keySpec = null;
	private SecretKeyFactory keyFactory = null;
	private SecretKey key = null;
	
	public EncryptDecryptProxy(String secretKey)
	{
		try
		{
			if (secretKey.length() < 16)
			{
				secretKey += "----------------";
				secretKey = secretKey.substring(0, 16);
			}
			this.secretKey = secretKey;
			this.keySpec = new DESKeySpec(this.secretKey.getBytes("UTF8"));
			this.keyFactory = SecretKeyFactory.getInstance("DES");
			this.key = this.keyFactory.generateSecret(this.keySpec);
		}
		catch (Exception e)
		{
			logger.error(e.getMessage());
		}
	}
	
	public String encrypt(String value)
	{
		try
		{
			byte[] cleartext = value.getBytes("UTF8");
			Cipher cipher = Cipher.getInstance("DES"); // cipher is not thread safe
			cipher.init(Cipher.ENCRYPT_MODE, this.key);
			//return this.base64encoder.encode(cipher.doFinal(cleartext));
			//return Base64.encodeBase64String(cipher.doFinal(cleartext));
			return Convert.toString(Base64.encodeBase64(cipher.doFinal(cleartext)));
		}
		catch (Exception e)
		{
			logger.error(e.getMessage());
			return null;
		}
	}
	
	public String decrypt(String value)
	{
		try
		{
			//byte[] encrypedPwdBytes = this.base64decoder.decodeBuffer(value);
			//byte[] encrypedPwdBytes = Base64.decodeBase64(value);
			byte[] encrypedPwdBytes = Base64.decodeBase64(value.getBytes());
			Cipher cipher = Cipher.getInstance("DES");// cipher is not thread safe
			cipher.init(Cipher.DECRYPT_MODE, this.key);
			byte[] plainTextPwdBytes = (cipher.doFinal(encrypedPwdBytes));
			return Convert.toString(plainTextPwdBytes);
		}
		catch (Exception e)
		{
			logger.error(e.getMessage());
			return null;
		}
	}
}
