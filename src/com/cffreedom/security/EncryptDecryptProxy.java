package com.cffreedom.security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import com.cffreedom.exceptions.GeneralException;
import com.cffreedom.utils.ConversionUtils;
import com.cffreedom.utils.LoggerUtil;

//import sun.misc.BASE64Decoder;
//import sun.misc.BASE64Encoder;
import org.apache.commons.codec.binary.Base64;

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
public class EncryptDecryptProxy
{
	private final LoggerUtil logger = new LoggerUtil(LoggerUtil.FAMILY_UTIL, this.getClass().getPackage().getName() + "." + this.getClass().getSimpleName());
	private String secretKey = null;
	//private BASE64Encoder base64encoder = new BASE64Encoder();
	//private BASE64Decoder base64decoder = new BASE64Decoder();
	private DESKeySpec keySpec = null;
	private SecretKeyFactory keyFactory = null;
	private SecretKey key = null;
	
	public EncryptDecryptProxy(String secretKey)
	{
		final String METHOD = "<init>";
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
			logger.logError(METHOD, e.getMessage(), e);
		}
	}
	
	public String encrypt(String value)
	{
		final String METHOD = "encrypt";
		try
		{
			byte[] cleartext = value.getBytes("UTF8");
			Cipher cipher = Cipher.getInstance("DES"); // cipher is not thread safe
			cipher.init(Cipher.ENCRYPT_MODE, this.key);
			//return this.base64encoder.encode(cipher.doFinal(cleartext));
			return Base64.encodeBase64String(cipher.doFinal(cleartext));
		}
		catch (Exception e)
		{
			logger.logError(METHOD, e.getMessage(), e);
			return null;
		}
	}
	
	public String decrypt(String value)
	{
		final String METHOD = "decrypt";
		try
		{
			//byte[] encrypedPwdBytes = this.base64decoder.decodeBuffer(value);
			byte[] encrypedPwdBytes = Base64.decodeBase64(value);
			Cipher cipher = Cipher.getInstance("DES");// cipher is not thread safe
			cipher.init(Cipher.DECRYPT_MODE, this.key);
			byte[] plainTextPwdBytes = (cipher.doFinal(encrypedPwdBytes));
			return ConversionUtils.toString(plainTextPwdBytes);
		}
		catch (Exception e)
		{
			logger.logError(METHOD, e.getMessage(), e);
			return null;
		}
	}
}
