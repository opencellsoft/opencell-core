package org.meveo.service.cf.json;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.service.crm.impl.AccountEntitySearchService;
import org.meveo.service.script.Script;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	    List<String> tablesWithCfvalues = getTablesWithCfvalues();
		for (String tableName : tablesWithCfvalues) {
			encryptCfvalues(tableName);
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
	
	public List<String> getTablesWithCfvalues() 
	{
	    return accountentityService.getEntityManager()
				.createNativeQuery("select table_name from information_schema.columns where column_name='cf_values'")
				.getResultList();
	}

	public void encryptCfvalues(String tableName)
	{
		List<Object[]>  entities = accountentityService.getEntityManager()
				.createNativeQuery("select id, cast(cf_values as varchar) from " + tableName + "  where cf_values is not null ")
				.getResultList();
		
		for (Object[] result : entities) {
		    long cfId = ((BigInteger) result[0]).longValue();
            String cfValue = (String) result[1];
            
            log.info("encrypting line id = "+cfId+", value = "+cfValue+", table = "+tableName);
            
			accountentityService.getEntityManager()
						.createNativeQuery("update " + tableName + " set cf_Values='" + encrypt(cfValue) + "' where  id="+cfId)
						.executeUpdate();
				
		}
	}
	
	public String getFileKey() throws Exception {
		return ParamBean.getInstance().getProperty(OPENCELL_SHA_KEY_PROPERTY, null);
	}	

	public  String encrypt(String value) throws com.fasterxml.jackson.core.JsonParseException, JsonMappingException, IOException {
	    
		Map<String, List> mapsList2 = new HashMap<String, List>();
		
		ObjectMapper mapper = new ObjectMapper();
		Map<String, List> mapsList = mapper.readValue(value, Map.class);
		
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


