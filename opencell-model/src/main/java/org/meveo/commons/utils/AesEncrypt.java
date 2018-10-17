package org.meveo.commons.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class AesEncrypt {

	private Cipher cipher;
	private static String aesKey;

	/**
	 * 
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 */
	public AesEncrypt() throws NoSuchAlgorithmException, NoSuchPaddingException {
		this.cipher = Cipher.getInstance("AES");
	}

	/**
	 * 
	 * @param key
	 * @return Secret key
	 * @throws Exception
	 */
	public SecretKey getKey(String key) throws Exception {
		byte[] keyBytes = Base64.decodeBase64(key);
		SecretKeySpec spec = new SecretKeySpec(keyBytes, "AES");
		return spec;
	}

	/**
	 * 
	 * @return String encryption/decryption key from opencell-admin.properties
	 * @throws Exception
	 */
	public String getFileKey() throws Exception {
		if(aesKey == null) {
		String _propertyFile = System.getProperty("jboss.server.config.dir") + File.separator
				+ "opencell-admin.properties";
		if (_propertyFile.startsWith("file:")) {
			_propertyFile = _propertyFile.substring(5);
		}
		Properties pr = new Properties();
		pr.load(new FileInputStream(_propertyFile));
		aesKey = getProperty("opencell.aes.key", pr);
		}

		return aesKey;
	}

	/**
	 * Get property value.
	 * 
	 * @param key Property key
	 * @return Value of property
	 */
	public String getProperty(String key, Properties properties) {
		String result = null;
		if (properties.containsKey(key)) {
			result = properties.getProperty(key);
		}
		return result;
	}

	/**
	 * 
	 * @param msg encrypt
	 * @param key secretKey
	 * @return encrypted msg
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws UnsupportedEncodingException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws InvalidKeyException
	 */
	public String encryptText(String msg, SecretKey key) throws NoSuchAlgorithmException, NoSuchPaddingException,
			UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
		this.cipher.init(Cipher.ENCRYPT_MODE, key);
		return Base64.encodeBase64String(cipher.doFinal(msg.getBytes("UTF-8")));
	}

	/**
	 * 
	 * @param msg to decrypt
	 * @param key secretKey
	 * @return
	 * @throws InvalidKeyException
	 * @throws UnsupportedEncodingException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public String decryptText(String msg, SecretKey key)
			throws InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
		this.cipher.init(Cipher.DECRYPT_MODE, key);
		return new String(cipher.doFinal(Base64.decodeBase64(msg)), "UTF-8");
	}

	/**
	 * 
	 * @param iban
	 * @param ae
	 * @return encryptedIban if encryption key exist in config file else return iban
	 * @throws Exception
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws UnsupportedEncodingException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws InvalidKeyException
	 */
	public String getEncyptedIban(String iban, AesEncrypt ae)
			throws Exception, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException,
			IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
		String encryptedIban;
		String key = ae.getFileKey();
		if(key == null || key.isEmpty()) {
			return iban;
		}
		SecretKey secretKey = ae.getKey(key);
		encryptedIban = ae.encryptText(iban, secretKey);
		return "AES" + encryptedIban;
	}

	/**
	 * 
	 * @param iban
	 * @param ae
	 * @return decryptedIban if encryption key exist in config file else return iban
	 * @throws Exception
	 * @throws InvalidKeyException
	 * @throws UnsupportedEncodingException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public String getDecryptedIban(String iban, AesEncrypt ae) throws Exception, InvalidKeyException,
			UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
		String decryptedIban;
		String key = ae.getFileKey();
		if(key == null || key.isEmpty()) {
			return iban;
		}
		SecretKey secretKey = ae.getKey(key);
		decryptedIban = ae.decryptText(iban, secretKey);
		return decryptedIban;
	}

}
