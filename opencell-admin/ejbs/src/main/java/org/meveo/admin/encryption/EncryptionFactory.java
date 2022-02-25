package org.meveo.admin.encryption;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jasypt.digest.StandardStringDigester;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;

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

    private static String encryptionPassword;

    private static String nbIterations;

    private static boolean enableEncryption;

    private static String digestAlgo;

    private static final String BY_DEFAULT_ENCRYPTION_ALGO = "PBEWITHSHA256AND128BITAES-CBC-BC";

    private static final String BY_DEFAULT_PW = "aPassword";

    private static final String BY_DEFAULT_ITERATION = "5000";

    private static final String BY_DEFAULT_ENABLE_ENCRYPTION = "false";

    private static final String BY_DEFAULT_DIGEST_ALGO = "SHA-256";

    private static final StandardPBEStringEncryptor encryptor;

    private static final StandardStringDigester digester;

    static {
        ParamBean tmpParamBean = ParamBeanFactory.getAppScopeInstance();

        encryptionAlgo = tmpParamBean.getProperty("encryptionAlgo", BY_DEFAULT_ENCRYPTION_ALGO);
        encryptionPassword = tmpParamBean.getProperty("encryptionPassword", BY_DEFAULT_PW);
        nbIterations = tmpParamBean.getProperty("numberIterations", BY_DEFAULT_ITERATION);
        enableEncryption = Boolean.parseBoolean(tmpParamBean.getProperty("enableEncryption", BY_DEFAULT_ENABLE_ENCRYPTION));
        digestAlgo = tmpParamBean.getProperty("digestAlgo", BY_DEFAULT_DIGEST_ALGO);

        // initialize an encryptor
        encryptor = new StandardPBEStringEncryptor();
        encryptor.setProvider(new BouncyCastleProvider());
        encryptor.setAlgorithm(encryptionAlgo);
        encryptor.setPassword(encryptionPassword);

        // initialize a digester
        digester = new StandardStringDigester();
        digester.setAlgorithm(digestAlgo); // optionally set the digest algorithm
        digester.setIterations(Integer.parseInt(nbIterations));
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

    public static String encrypt(String clearText){
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
//        CryptageFactory.testCryptageAlgo();

        String clearText = "this text";
        String algorithm = "PBEWITHSHA256AND128BITAES-CBC-BC";
        String password = "myPass";
        String encryptedText = EncryptionFactory.encrypt(clearText);
        String decryptedText = EncryptionFactory.decrypt(encryptedText);
        System.out.println("decryptedText DAY NE : " + decryptedText);
    }

}
