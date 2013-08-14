package com.cffreedom.utils.security;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import com.cffreedom.utils.Convert;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Original Class: com.cffreedom.utils.security.SecurityManager
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
 * 2013-07-16 	markjacobsen.net 	Renamed from EncryptDecryptProxy
 * 2013-07-19 	markjacobsen.net 	Renamed from SecurityManager
 * 2013-08-14 	markjacobsen.net 	More informative error handling
 */
public class SecurityCipher
{
	private static final Logger logger = LoggerFactory.getLogger("com.cffreedom.utils.security.SecurityCipher");
	private static final String ALGORITHM = "DES";
	private static final String CHARSET = "UTF8";
	private String secretKey = null;
	private DESKeySpec keySpec = null;
	private SecretKeyFactory keyFactory = null;
	private SecretKey key = null;
	
	public SecurityCipher(String secretKey)
	{
		try
		{
			if (secretKey.length() < 16)
			{
				logger.debug("Padding secretKey");
				secretKey += "----------------";
				secretKey = secretKey.substring(0, 16);
			}
			this.secretKey = secretKey;
			this.keySpec = new DESKeySpec(this.secretKey.getBytes(CHARSET));
			this.keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
			this.key = this.keyFactory.generateSecret(this.keySpec);
		}
		catch (InvalidKeyException | InvalidKeySpecException | NoSuchAlgorithmException | UnsupportedEncodingException e)
		{
			logger.error(e.getClass().getSimpleName() + " - " + e.getMessage());
		}
	}
	
	public String encrypt(String value)
	{
		try
		{
			if ((value == null) || (value.length() == 0))
			{
				logger.warn("No value passed in. Returning zero length string.");
				return "";
			}
			else
			{
				logger.trace("Encrypting \"{}\"", value, this.key);
				byte[] cleartext = value.getBytes(CHARSET);
				Cipher cipher = Cipher.getInstance(ALGORITHM);
				cipher.init(Cipher.ENCRYPT_MODE, this.key);
				String encryptedValue = Convert.toString(Base64.encodeBase64(cipher.doFinal(cleartext)));
				logger.trace("Encrypted value \"{}\"", encryptedValue);
				return encryptedValue;
			}
		}
		catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | 
				IllegalBlockSizeException | UnsupportedEncodingException e)
		{
			logger.error(e.getClass().getSimpleName() + " - " + e.getMessage());
			return null;
		}
	}
	
	public String decrypt(String value)
	{
		try
		{
			if ((value == null) || (value.length() == 0))
			{
				logger.warn("No value passed in. Returning zero length string.");
				return "";
			}
			else
			{
				logger.trace("Decrypting \"{}\"", value, this.key);
				byte[] encrypedPwdBytes = Base64.decodeBase64(value.getBytes());
				Cipher cipher = Cipher.getInstance(ALGORITHM);
				cipher.init(Cipher.DECRYPT_MODE, this.key);
				byte[] plainTextPwdBytes = (cipher.doFinal(encrypedPwdBytes));
				String decryptedValue = Convert.toString(plainTextPwdBytes);
				logger.trace("Decrypted value \"{}\"", decryptedValue);
				return decryptedValue;
			}
		}
		catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException |
				IllegalBlockSizeException e)
		{
			logger.error(e.getClass().getSimpleName() + " - " + e.getMessage());
			return null;
		}
	}
}
