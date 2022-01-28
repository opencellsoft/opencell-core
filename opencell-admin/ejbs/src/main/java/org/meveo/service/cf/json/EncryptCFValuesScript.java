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
    public void execute(Map<String, Object> methodContext)  {
        List<String> tablesWithCfvalues = getTablesWithCfvalues();
        for (String tableName : tablesWithCfvalues) {
            try {
                encryptCfvalues(tableName);
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    public void encryptCfvalues(String tableName) throws JsonParseException, JsonMappingException, IOException
    {
        List<Object[]>  entities = accountentityService.getEntityManager()
                .createNativeQuery("select id, cast(cf_values as varchar) from " + tableName + "  where cf_values is not null ")
                .getResultList();
        
        for (Object[] result : entities) {
            long cfId = ((BigInteger) result[0]).longValue();
            String cfValue = (String) result[1];
            
            log.info("encrypting line id = "+cfId+", value = "+cfValue+", table = "+tableName);
            
            accountentityService.getEntityManager()
                        .createNativeQuery("update " + tableName + " set cf_Values='" + encryptCfvaluesStr(cfValue) + "' where  id="+cfId)
                        .executeUpdate();
                
        }
    }
    
    public String getFileKey() throws Exception {
        return ParamBean.getInstance().getProperty(OPENCELL_SHA_KEY_PROPERTY, null);
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

    public  String encryptCfvaluesStr(String value) throws com.fasterxml.jackson.core.JsonParseException, JsonMappingException, IOException {
        Map<String, List> mapsList;
        ObjectMapper mapper = new ObjectMapper();
        Gson gsonparser = new GsonBuilder().disableHtmlEscaping().create();
        mapsList = mapper.readValue(value, Map.class);

        for (Entry<String, List> maps : mapsList.entrySet()) {
            List listCfvalues = new ArrayList();
            List<Map> list = maps.getValue();
            for (Map<String, Object> map : list) {
                for (Entry<String, Object> cfValues : map.entrySet()) {
                    if (cfValues.getValue() instanceof String) {
                        if (!cfValues.getValue().equals("")) {
                            map.put(cfValues.getKey(), encrypt(cfValues.getValue().toString()));
                            listCfvalues.add(map);
                        }
                    } else if (cfValues.getValue() instanceof List) {
                        Map<String, List<String>> mapListCfValues = new HashMap<String, List<String>>();
                        List<String> listValueStrings = new ArrayList<String>();
                        for (Object typeListCfvalues : (List) cfValues.getValue()) {
                            if (typeListCfvalues instanceof String) {
                                listValueStrings.add(encrypt(typeListCfvalues.toString()));
                                mapListCfValues.put(cfValues.getKey(), listValueStrings);                           
                            }
                        }
                        listCfvalues.add(mapListCfValues);
                    } else if (cfValues.getValue() instanceof Map) {
                        for (Entry<String, List> typeMapsCfvalues : ((Map<String, List>) cfValues.getValue())
                                .entrySet()) {
                            Object o = typeMapsCfvalues.getKey();
                            Map<String, String> mapMatrix = new HashMap<String, String>();
                            if (o instanceof String) {
                                for (Entry<String, String> typeMapsCfvaluesmaps : ((Map<String, String>) cfValues.getValue()).entrySet()) {
                                    if(typeMapsCfvaluesmaps.getValue() instanceof String) {
                                    mapMatrix.put(typeMapsCfvaluesmaps.getKey(), encrypt(typeMapsCfvaluesmaps.getValue().toString()));
                                    listCfvalues.add(mapMatrix);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            mapsList.put(maps.getKey(), listCfvalues);
        }
        return mapsList.toString();
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