package org.meveo.service.cf.json;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.ParamBean;
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
        List<String> tablesWithCfValues = getTablesWithCfvalues();
        for (String tableName : tablesWithCfValues) {
            decryptCfvalues(tableName);
        }
    }

    private SecretKeySpec buildSecretKey() 
    {
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
            log.error("Error while building Secret Key: " + e.getLocalizedMessage());
        }
        return null;
    }
    
    private List<String> getTablesWithCfvalues() 
    {
        return accountentityService.getEntityManager()
                .createNativeQuery("select table_name from information_schema.columns where column_name='cf_values'")
                .getResultList();
    }

    private void decryptCfvalues(String tableName) 
    {
        List<Object[]>  entities = accountentityService.getEntityManager()
                .createNativeQuery("select id, cast(cf_values as varchar) from " + tableName + " where cf_values like 'AES%'")
                .getResultList();
      
        for (Object[] result : entities) {
            long cfId = ((BigInteger) result[0]).longValue();
            String cfValue = (String) result[1];
          
            log.info("descrypting line id = "+cfId+", value = "+cfValue+", table = "+tableName);
            
            String decryptedCf = decrypt(cfValue);
            if(decryptedCf != null) {
                accountentityService.getEntityManager()
                            .createNativeQuery("update  " + tableName + " set cf_values='"+decryptedCf+"' where  id="+cfId)
                            .executeUpdate();      
            }
        }
    }
    
    private String decrypt(String strToDecrypt) 
    {
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
            return null;
        }
        return strToDecrypt;
    }


    private String getFileKey() throws Exception 
    {
        return ParamBean.getInstance().getProperty(OPENCELL_SHA_KEY_PROPERTY, null);
    }

}