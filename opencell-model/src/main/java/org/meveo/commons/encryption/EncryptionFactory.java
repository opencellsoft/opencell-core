package org.meveo.commons.encryption;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jasypt.digest.StandardByteDigester;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.meveo.commons.utils.ParamBean;

import java.nio.charset.StandardCharsets;
import java.security.Provider;
import java.security.Security;

/**
 * EncryptionFactory
 *
 * @author Thang Nguyen
 * @author Wassim Drira
 * @lastModifiedVersion 12.0.0
 *
 */
public class EncryptionFactory {

    private static final String encryptionAlgo;

    private static final String symEncSecretKey;

    private static final String BY_DEFAULT_ENCRYPTION_ALGO = "PBEWITHSHA256AND128BITAES-CBC-BC";

    private static final String BY_DEFAULT_ITERATION = "5000";

    private static final String BY_DEFAULT_ENABLE_ENCRYPTION = "false";

    private static final String BY_DEFAULT_DIGEST_ALGO = "SHA-256";

    private static final StandardPBEStringEncryptor encryptor;

    private static final StandardByteDigester byteDigester;

//    private static final String CURRENT_ENCRYPTION_ALGO_AND_KEY = "encryption.currentEncAlgoAndKey";

    static {
        encryptionAlgo = ParamBean.getInstance().getProperty("encrypt.algorithm", BY_DEFAULT_ENCRYPTION_ALGO);
        symEncSecretKey = ParamBean.getInstance().getProperty("encrypt.secretKey", null);
        String nbIterations = ParamBean.getInstance().getProperty("digest.numberIterations", BY_DEFAULT_ITERATION);
        boolean enableEncryption = Boolean.parseBoolean(ParamBean.getInstance().getProperty("encryption.enable", BY_DEFAULT_ENABLE_ENCRYPTION));
        String digestAlgo = ParamBean.getInstance().getProperty("digest.algorithm", BY_DEFAULT_DIGEST_ALGO);

        // initialize a byteDigester
        byteDigester = new StandardByteDigester();
        byteDigester.setAlgorithm(digestAlgo); // optionally set the digest algorithm
        byteDigester.setIterations(Integer.parseInt(nbIterations));

        // initialize an encryptor
        encryptor = new StandardPBEStringEncryptor();
        encryptor.setProvider(new BouncyCastleProvider());
        encryptor.setAlgorithm(encryptionAlgo);
        encryptor.setPassword(symEncSecretKey);

//        // if currentEncryptionAlgoAndKey is empty, we set new value
//        if (ParamBean.getInstance().getProperty(CURRENT_ENCRYPTION_ALGO_AND_KEY, "").isBlank())
//            ParamBean.getInstance().setProperty(CURRENT_ENCRYPTION_ALGO_AND_KEY, encryptionAlgo + " | " + symEncSecretKey);
//
//        if (! ParamBean.getInstance().getProperty(CURRENT_ENCRYPTION_ALGO_AND_KEY, "").equals(encryptionAlgo + " | " + symEncSecretKey)) {
//            ParamBean.getInstance().setProperty(CURRENT_ENCRYPTION_ALGO_AND_KEY, encryptionAlgo + " | " + symEncSecretKey); // update with new encryption algorithm and secret key
//            updateEncryptedValues(encryptionAlgo, symEncSecretKey);
//        }
    }

    public static void listOfSecurityProviders() {
        //Security listing
        for (Provider provider : Security.getProviders()) {
            System.out.println("Security provider : " + provider.getName());
            for (Provider.Service service : provider.getServices()) {
                System.out.println("Algorithm : " + service.getAlgorithm());
            }
        }
    }

    public static void listAlgoBountyCastle() {
        Provider provider = new BouncyCastleProvider();
        for (Provider.Service service : provider.getServices()) {
            System.out.println("Algorithm: " + service.getAlgorithm());
        }
    }

    public static String buildSecretPassword() {
        if (!StringUtils.isBlank(symEncSecretKey)) {
            byte[] fileKey = symEncSecretKey.getBytes(StandardCharsets.UTF_8);

            return new String(byteDigester.digest(fileKey));
        }

        return null;
    }

    public static byte[] concatenateByteArrays(byte[] firstArr, byte[] secArr) {
        byte[] result = new byte[firstArr.length + secArr.length];
        System.arraycopy(firstArr, 0, result, 0, firstArr.length);
        System.arraycopy(secArr, 0, result, firstArr.length, secArr.length);

        return result;
    }

    public static String encrypt(String clearText){
        return encryptor.encrypt(clearText);
    }

    public static String decrypt(String encryptedText){
        return encryptor.decrypt(encryptedText);
    }

    public static byte[] digest(byte[] bytesToDigest){
        synchronized (byteDigester) {
            return byteDigester.digest(bytesToDigest);
        }
    }

//    private static void updateEncryptedValues(String encryptionAlgo, String key){
//        // get tables with CF values
//        List<String> tablesWithCfValues = getTablesWithCfValues();
//for (String tableName : tablesWithCfValues) {
//    System.out.println("tableName with CF : " + tableName);
//}
//System.out.println("--------------------updateEncryptedValues--------------------------");
//        for (String tableName : tablesWithCfValues) {
//System.out.println("tableName decrypt with CF : " + tableName);
//            decryptCfvalues(tableName);
//        }
//    }
//
//    private static List<String> getTablesWithCfValues() {
//        assert accountEntityService != null;
//
//        return accountEntityService.getEntityManager()
//                .createNativeQuery("select table_name from information_schema.columns where column_name='cf_values'")
//                .getResultList();
//    }
//
//    private static void decryptCfvalues(String tableName) {
//        assert accountEntityService != null;
//        List<Object[]> entities = accountEntityService.getEntityManager()
//                .createNativeQuery("select id, cast(cf_values as varchar) from " + tableName + " where cf_values like 'AES%'")
//                .getResultList();
//
//        for (Object[] result : entities) {
//            long cfId = ((BigInteger) result[0]).longValue();
//            String cfValue = (String) result[1];
//
//System.out.println("descrypting line id = " + cfId + ", value = " + cfValue + ", table = " + tableName);
//
//            String decryptedCf = decrypt(cfValue);
//            if(decryptedCf != null) {
//                accountEntityService.getEntityManager()
//                        .createNativeQuery("update  " + tableName + " set cf_values='"+decryptedCf+"' where  id="+cfId)
//                        .executeUpdate();
//            }
//        }
//    }
}
