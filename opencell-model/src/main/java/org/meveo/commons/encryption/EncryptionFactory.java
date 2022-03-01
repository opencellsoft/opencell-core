package org.meveo.commons.encryption;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jasypt.digest.StandardByteDigester;
import org.jasypt.digest.StandardStringDigester;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.meveo.commons.utils.ParamBean;

import java.nio.charset.StandardCharsets;
import java.security.Provider;
import java.security.Security;

/**
 * StorageFactory
 *
 * @author Thang Nguyen
 * @author Wassim Drira
 * @lastModifiedVersion 12.0.0
 *
 */
public class EncryptionFactory {


    private static String encryptionAlgo;

    private static String nbIterations;

    private static boolean enableEncryption;

    private static String digestAlgo;

    private static String symEncSecretKey;

    private static final String BY_DEFAULT_ENCRYPTION_ALGO = "PBEWITHSHA256AND128BITAES-CBC-BC";

    private static final String BY_DEFAULT_ITERATION = "5000";

    private static final String BY_DEFAULT_ENABLE_ENCRYPTION = "false";

    private static final String BY_DEFAULT_DIGEST_ALGO = "SHA-256";

    private static final StandardPBEStringEncryptor encryptor;

    private static final StandardByteDigester byteDigester;

    static {
        ParamBean tmpParamBean = ParamBean.getInstanceByProvider("");

        encryptionAlgo = tmpParamBean.getProperty("encryption.algorithm", BY_DEFAULT_ENCRYPTION_ALGO);
        nbIterations = tmpParamBean.getProperty("digest.numberIterations", BY_DEFAULT_ITERATION);
        enableEncryption = Boolean.parseBoolean(tmpParamBean.getProperty("encryption.enable", BY_DEFAULT_ENABLE_ENCRYPTION));
        digestAlgo = tmpParamBean.getProperty("digest.algorithm", BY_DEFAULT_DIGEST_ALGO);
        symEncSecretKey = tmpParamBean.getProperty("symmetricEncryption.secretKey", null);

        // initialize a byteDigester
        byteDigester = new StandardByteDigester();
        byteDigester.setAlgorithm(digestAlgo); // optionally set the digest algorithm
        byteDigester.setIterations(Integer.parseInt(nbIterations));

        // initialize an encryptor
        encryptor = new StandardPBEStringEncryptor();
        encryptor.setProvider(new BouncyCastleProvider());
        encryptor.setAlgorithm(encryptionAlgo);
        String password = buildSecretPassword();
        assert password != null;
        encryptor.setPassword(password);
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

    private static String buildSecretPassword() {
System.out.println("buildSecretPassword 1 DAY NE");
        if (!StringUtils.isBlank(symEncSecretKey)) {
            byte[] fileKey = symEncSecretKey.getBytes(StandardCharsets.UTF_8);
System.out.println("fileKey.length DAY NE : " + fileKey.length);
System.out.println("password encrypted DAY NE : " + new String(byteDigester.digest(fileKey)));
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
System.out.println("clearText day ne : " + clearText);
System.out.println("encryptor != null day ne : " + (encryptor != null));
        return encryptor.encrypt(clearText);
    }

    public static String decrypt(String encryptedText){
        return encryptor.decrypt(encryptedText);
    }

    public static void testCryptageAlgo() {
        System.out.println("Hello World!");

        StandardStringDigester digester = new StandardStringDigester();
        digester.setAlgorithm("SHA-256"); // optionally set the algorithm
        digester.setIterations(50000); // increase security by performing 50000 hashing iterations
        String digest = digester.digest("myMessage");
        System.out.println("digest:" + digest);


//        // second way : using provider name
//        Security.addProvider(new BouncyCastleProvider());
//
//        StandardPBEStringEncryptor mySecondEncryptor = new StandardPBEStringEncryptor();
//        mySecondEncryptor.setProviderName("BC");
//        mySecondEncryptor.setAlgorithm("PBEWITHSHA256AND128BITAES-CBC-BC");
//        mySecondEncryptor.setPassword("myPass");
//
//        encryptedText = mySecondEncryptor.encrypt(clearText);
//        System.out.println("second way enc:" + encryptedText);

    }

    public static void main(String[] args) {
        EncryptionFactory.testCryptageAlgo();

//        String clearText = "this text";
//        String algorithm = "PBEWITHSHA256AND128BITAES-CBC-BC";
//        String password = "myPass";
//        String encryptedText = EncryptionFactory.encrypt(clearText);
//        String decryptedText = EncryptionFactory.decrypt(encryptedText);
//        System.out.println("decryptedText DAY NE : " + decryptedText);
    }

}
