package org.meveo.service.cf.json;

import java.io.IOException;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.service.crm.impl.AccountEntitySearchService;
import org.meveo.service.script.Script;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class EncryptCFValuesScript extends Script  {

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
		for (String s : tablesCfvalues()) {
			try {
				updateDecryptCfvalues(s);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			};
		}
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
			log.info("Error while building Secret Key: " + e.getLocalizedMessage());
		}
		return null;
	}
	
	public List<String> tablesCfvalues() {
		@SuppressWarnings("unchecked")
		List<String> entities = accountentityService.getEntityManager()
				.createNativeQuery("select table_name from information_schema.columns where column_name='cf_values'")
				.getResultList();

		return entities;
	}

	public void updateDecryptCfvalues(String tableCfvalue) throws JsonParseException, JsonMappingException, IOException {
		HashMap<Long, String> map=new HashMap<Long, String>();
		@SuppressWarnings("unchecked")
		List<Object[]>  entities = accountentityService.getEntityManager()
				.createNativeQuery("select id,cf_Values from "+ tableCfvalue +"  where cf_Values is not null ")
				.getResultList();
		for (Object[] result : entities) {
	        map.put(( ((BigInteger) result[0]).longValue()), result[1].toString());
	    }
		for(Map.Entry<Long, String> entry :map.entrySet()) {
				int upateEntity = accountentityService.getEntityManager()
						.createNativeQuery("update  Account_entity set cf_Values='"+cryptageString(entry.getValue())+"' where  id="+entry.getValue())
						.executeUpdate();
				
				
		}
	}
	

	
	


	public String getFileKey() throws Exception {
		return ParamBean.getInstance().getProperty(OPENCELL_SHA_KEY_PROPERTY, null);
	}

	public String encrypt(String strToEncrypt) {
		try {

			if (strToEncrypt != null) {
				if (strToEncrypt.startsWith(ENCRYPTION_CHECK_STRING)) {
					return strToEncrypt;
				}
				SecretKeySpec secretKey = buildSecretKey();
				Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
				cipher.init(Cipher.ENCRYPT_MODE, secretKey);
				String encrypted = Base64.getEncoder()
						.encodeToString(cipher.doFinal(strToEncrypt.getBytes(UTF_8_ENCODING)));
				return ENCRYPTION_CHECK_STRING + encrypted;
			}

		} catch (Exception e) {
			log.error("Error while encrypting: " + e.getLocalizedMessage(), e);
			throw new EncyptionException(e);
		}
		return strToEncrypt;
	}
	

	public  String cryptageString(String value) throws com.fasterxml.jackson.core.JsonParseException, JsonMappingException, IOException {
		Map<String, List> mapsList;
		Map<String, List> mapsList2 = new HashMap<String, List>();
		ObjectMapper mapper = new ObjectMapper();
		Gson gsonparser = new GsonBuilder().disableHtmlEscaping().create();
		mapsList = mapper.readValue(value, Map.class);
		for (Entry<String, List> maps : mapsList.entrySet()) {
			List list2 = new ArrayList<Map>();
			List<Map> list = maps.getValue();
			log.info("Niveau 1 Key : " + maps.getKey());
			for (Map<String, String> map : list) {
				for (Entry<String, String> element : map.entrySet()) {

						Map map2 = new HashMap<String, String>();
						map2.put(element.getKey(),encrypt(element.getValue()));
						list2.add(map2);
				}
			}
			mapsList2.put(maps.getKey(), list2);
		}
		return mapsList2.toString();
	}

}
class EncyptionException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 5175511780611978989L;
    
    
    public EncyptionException() {
        super();
    }

    public EncyptionException(Throwable cause) {
        super(cause);
    }

    public EncyptionException(String message) {
        super(message);
    }

}


