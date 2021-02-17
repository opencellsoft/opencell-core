/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.commons.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class AesEncrypt {

	private Cipher cipher;

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
        return ParamBean.getInstance().getProperty("opencell.aes.key", null);
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
