package org.meveo.service.cf.json;

import java.security.MessageDigest;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.encryption.IEncryptable;
import org.meveo.model.AccountEntity;
import org.meveo.service.crm.impl.AccountEntitySearchService;
import org.meveo.service.script.Script;

public class MigrateCFfValue extends Script implements IEncryptable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final AccountEntitySearchService accountentityService = (AccountEntitySearchService) getServiceInterface(
			AccountEntitySearchService.class.getSimpleName());

	@Override
	public void init(Map<String, Object> methodContext) throws BusinessException {
	}

	@Override
	public void execute(Map<String, Object> methodContext) throws BusinessException {

		System.out.println("list des entites" + ListCfvaluesString());
		for (String s : ListCfvaluesString()) {
			ListCfvaluesTable(s);
		}
	}

	@Override
	public void terminate(Map<String, Object> methodContext) throws BusinessException {

	}

	public String decrypt(String strToDecrypt) {
		try {
			if (strToDecrypt != null) {
				if (strToDecrypt.startsWith(ENCRYPTION_CHECK_STRING)) {
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
			System.out.println("Error while decrypting: " + e.getLocalizedMessage());
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
	public SecretKeySpec buildSecretKey() {
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
			System.out.println("Error while building Secret Key: " + e.getLocalizedMessage());
		}
		return null;
	}

	public List<AccountEntity> ListCfvalues() {
		@SuppressWarnings("unchecked")
		List<AccountEntity> entities = accountentityService.getEntityManager()
				.createQuery("select a from AccountEntity a where a.cfValues is not null", AccountEntity.class)
				.getResultList();

		return entities;
	}



	public List<String> ListCfvaluesString() {
		@SuppressWarnings("unchecked")
		List<String> entities = accountentityService.getEntityManager()
				.createNativeQuery("select table_name from information_schema.columns where column_name='cf_values'")
				.getResultList();

		return entities;
	}

	public void ListCfvaluesTable(String nmTable) {
		@SuppressWarnings("unchecked")
		List<String> entities = accountentityService.getEntityManager()
				.createNativeQuery("select cf_Values from " + nmTable + " a where cf_Values is not null")
				.getResultList();

		for(String str:entities) {
			if(!str.equals(" ")) {
			System.out.println("Code decrypter :" + decrypt(str));
			}
		}
	}
}
