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

/**
 * 
 */
package org.meveo.commons.encryption;

import java.security.MessageDigest;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author melyoussoufi
 * @lastModifiedVersion 6.2.1
 *
 */
public interface IEncryptable {

	static final Logger log = LoggerFactory.getLogger(IEncryptable.class);
	static final String UTF_8_ENCODING = "UTF-8";
	static final String ENCRYPTION_ALGORITHM = "AES/ECB/PKCS5PADDING";
	static final String INTERNAL_SECRET_KEY = "staySafe";
	static final String SHA_256_HASHING = "SHA-256";
	static final String AES_ALOGRITHM = "AES";
	static final String OPENCELL_SHA_KEY_PROPERTY = "opencell.sha.key";
	static final String ENCRYPT_CUSTOM_FIELDS_PROPERTY = "encrypt.customFields";
	static final String ENCRYPT_BANK_DATA_PROPERTY = "encrypt.bankData";
	static final String ENCRYPT_PERSONNAL_DATA_PROPERTY = "encrypt.personnalData";
	public static final String ENCRYPTION_CHECK_STRING = "AES";
	static final String ON_ERROR_RETURN = "####";
	static final String FALSE_STR = "false";
	static final String TRUE_STR = "true";

	/**
	 * Encrypts a string using aes Algorithm
	 * 
	 * @param strToEncrypt
	 * @return Encrypted String
	 */
	default String encrypt(String strToEncrypt) {
		try {
			
			if (strToEncrypt != null) {
				if(strToEncrypt.startsWith(ENCRYPTION_CHECK_STRING)) {
					return strToEncrypt;
				}
				SecretKeySpec secretKey = buildSecretKey();
				Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
				cipher.init(Cipher.ENCRYPT_MODE, secretKey);
				String encrypted  = Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(UTF_8_ENCODING)));
				return ENCRYPTION_CHECK_STRING + encrypted;
			}
			
		} catch (Exception e) {
			log.error("Error while encrypting: " + e.getLocalizedMessage(), e);
			throw new EncyptionException(e);
		}
		return strToEncrypt;
	}

	/**
	 * Decrypts a string using aes Algorithm
	 * 
	 * @param strToDecrypt
	 * @return decrypted String, in case of a problem while decryption we return ###
	 *         instead
	 */
	default String decrypt(String strToDecrypt) {
		try {
			if (strToDecrypt != null) {
				if(strToDecrypt.startsWith(ENCRYPTION_CHECK_STRING)) {
					strToDecrypt = strToDecrypt.replace(ENCRYPTION_CHECK_STRING, "");
					SecretKeySpec secretKey = buildSecretKey();
					Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
					cipher.init(Cipher.DECRYPT_MODE, secretKey);
					String res = new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
					return res;
				}
				return strToDecrypt;
				
			}
		} catch (Exception e) {
			log.error("Error while decrypting: " + e.getLocalizedMessage(), e);
			return ON_ERROR_RETURN;
		}
		return strToDecrypt;
	}

	/**
	 * Builds the secret key, using sha-256 and aes algorithm 2 keys, first one in
	 * opencell-admin.properties and the second one is the value of the constatnt
	 * INTERNAL_SECRET_KEY in source
	 * 
	 * @return SecretKeySpec
	 */
	default SecretKeySpec buildSecretKey() {
		MessageDigest sha = null;
		byte[] hash = null;
		try {
			String fileKeyStr = getFileKey();
			if (!StringUtils.isBlank(fileKeyStr)) {
				byte[] fileKey = fileKeyStr.getBytes(UTF_8_ENCODING);
				byte[] internalKey = INTERNAL_SECRET_KEY.getBytes(UTF_8_ENCODING);
				sha = MessageDigest.getInstance(SHA_256_HASHING);
				sha.update(fileKey);
				sha.update(internalKey);
				hash = sha.digest();
				return new SecretKeySpec(hash, AES_ALOGRITHM);
			} else {
				throw new Exception("External secret key cannot be null while encrypting / decrypting");
			}
		} catch (Exception e) {
			log.error("Error while building Secret Key: " + e.getLocalizedMessage(), e);
		}
		return null;
	}

	/**
	 * 
	 * @return String encryption/decryption key from opencell-admin.properties
	 * @throws Exception
	 */
	default String getFileKey() throws Exception {
		return ParamBean.getInstance().getProperty(OPENCELL_SHA_KEY_PROPERTY, null);
	}

}
