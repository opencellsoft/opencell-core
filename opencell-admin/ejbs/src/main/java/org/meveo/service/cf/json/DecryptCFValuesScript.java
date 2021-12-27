package org.meveo.service.cf.json;

import java.security.MessageDigest;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.encryption.EncyptionException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.AccountEntity;
import org.meveo.service.crm.impl.AccountEntitySearchService;
import org.meveo.service.script.Script;

public class DecryptCFValuesScript extends Script  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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

	private final AccountEntitySearchService accountentityService = (AccountEntitySearchService) getServiceInterface(
			AccountEntitySearchService.class.getSimpleName());

	@Override
	public void init(Map<String, Object> methodContext) throws BusinessException {
	}

	@Override
	public void execute(Map<String, Object> methodContext) throws BusinessException {

		for (String s : listCfvaluesString()) {
			listCfvaluesTable(s);
		}
	}

	@Override
	public void terminate(Map<String, Object> methodContext) throws BusinessException {

	}

	

	
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

	



	public List<String> listCfvaluesString() {
		@SuppressWarnings("unchecked")
		List<String> entities = accountentityService.getEntityManager()
				.createNativeQuery("select table_name from information_schema.columns where column_name='cf_values'")
				.getResultList();

		return entities;
	}

	public void listCfvaluesTable(String nmTable) {
		@SuppressWarnings("unchecked")
		List<String> entities = accountentityService.getEntityManager()
				.createNativeQuery("select cf_Values from " + nmTable + " a where cf_Values is not null")
				.getResultList();

		for(String str:entities) {
			if(!str.equals(" ")) {
				@SuppressWarnings("unchecked")
				int upateEntity = accountentityService.getEntityManager()
						.createNativeQuery("update  " + nmTable + " set cf_Values='"+decrypt(str)+"' where cf_values like 'AES%'")
						.executeUpdate();
			}
		}
	}
	public String encrypt(String strToEncrypt) {
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

	
	public String decrypt(String strToDecrypt) {
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


	public String getFileKey() throws Exception {
		return ParamBean.getInstance().getProperty(OPENCELL_SHA_KEY_PROPERTY, null);
	}

}
