package org.meveo.commons.encryption;

import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.meveo.commons.keystore.KeystoreManager;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.util.Base64;

/**
 * EncryptionFactory
 *
 * @author Thang Nguyen
 * @author Wassim Drira
 * @lastModifiedVersion 12.0.0
 *
 */
public class EncryptionFactory {

    private EncryptionFactory() {
    }

    private static final Logger log = LoggerFactory.getLogger(EncryptionFactory.class);

    private static final IvParameterSpec ivParameterSpec;

    private static final String BY_DEFAULT_DIGEST_ALGO = "SHA-256";

    private static final String BUILD_KEY_ALGORITHM = "AES";

    private static Cipher cipher = null;

    private static MessageDigest messageDigest = null;

    public static final String PREFIX = "pref";

    private static final String SEPARATOR = "|";

    public static final String ENCRYPTION_ALGO_PROP = "encrypt.algorithm";

    public static final String ENCRYPTION_KEY_PROP = "encrypt.secretKey";

    private static final int LENGTH_HASH_MD5_EN_HEXA = 32;



    public static final String ENCRYPTION_CHECK_STRING = "AES";

    private static final String OLD_AES_ENCRYPTION_ALGORITHM = "AES/ECB/PKCS5PADDING";

    private static final String OPENCELL_SHA_KEY_PROPERTY = "opencell.sha.key";

    private static final String INTERNAL_SECRET_KEY = "staySafe";

    private static final String SHA_256_HASHING = "SHA-256";

    private static final String ON_ERROR_RETURN = "####";

    static {
        Security.addProvider(new BouncyCastleProvider());

        // set initialized vector
        ivParameterSpec = generateIv();

        String digestAlgo = ParamBean.getInstance().getProperty("digest.algorithm", BY_DEFAULT_DIGEST_ALGO);
        // initialize a digest
        try {
            messageDigest = MessageDigest.getInstance(digestAlgo);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        // initialize a cipher for migration
        try {
            SecretKeySpec secretKey = buildSecretKey();
            cipher = Cipher.getInstance(OLD_AES_ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    public static void listOfSecurityProviders() {
        //Security listing
        for (Provider provider : Security.getProviders()) {
            log.info("Security provider : {}", provider.getName());
            for (Provider.Service service : provider.getServices()) {
                log.info("Algorithm : {}", service.getAlgorithm());
            }
        }
    }

    public static void listAlgoBountyCastle() {
        Provider provider = new BouncyCastleProvider();
        for (Provider.Service service : provider.getServices()) {
            log.info("Algorithm: {}", service.getAlgorithm());
        }
    }

    public static byte[] concatenateByteArrays(byte[] firstArr, byte[] secArr) {
        byte[] result = new byte[firstArr.length + secArr.length];
        System.arraycopy(firstArr, 0, result, 0, firstArr.length);
        System.arraycopy(secArr, 0, result, firstArr.length, secArr.length);

        return result;
    }

    public static String getHashOfAlgoKey(String algoKey){
        MessageDigest md5;
        byte[] keyBytes = new byte[0];

        try {
            md5 = MessageDigest.getInstance("MD5");
            md5.update(algoKey.getBytes(StandardCharsets.UTF_8));
            keyBytes = md5.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return Hex.encodeHexString(keyBytes);
    }

    public static String encrypt(String clearText){
        try {
            String completePrefix = buildCipherAndGetPrefix();
            return completePrefix + SEPARATOR
                    + Base64.getEncoder().encodeToString(cipher.doFinal(clearText.getBytes()));
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchPaddingException e) {
            log.error("Error while encrypting: {}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * Encrypt value without adding Opencell custom prefix
     * Use to keep interoperability with old AesEncrypt with use "AES" prefix
     * /!\ CAREFULNESS : PLEASE DONT USE IT TO OTHER CONTEXT, THE CIPHER DATA CANNOT BE DECRYPTED /!\
     *
     * @param clearText clear content
     * @return encrypted value without prefix
     * @deprecated Only for "FNAC FR" Migration (from 9.8.6 to 13.1.1)
     */
    @Deprecated
    public static String encryptWithoutPrefix(String clearText) {
        try {
            buildCipherAndGetPrefix();
            return Base64.getEncoder().encodeToString(cipher.doFinal(clearText.getBytes()));
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchPaddingException e) {
            log.error("Error while encrypting without prefix: {}", e.getMessage(), e);
        }
        return null;
    }

    private static String buildCipherAndGetPrefix() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        String algorithm = KeystoreManager.retrieveCredential(ENCRYPTION_ALGO_PROP);
        String secretKey = KeystoreManager.retrieveCredential(ENCRYPTION_KEY_PROP);
        String algoKey = algorithm + SEPARATOR + secretKey;
        String hashAlgoKey = getHashOfAlgoKey(algoKey);

        String completePrefix = PREFIX + hashAlgoKey;

        cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, defineKey(secretKey), ivParameterSpec);

        // add algorithm and secret key if they do not exist in the keystore
        // the format is as follows : encrypt.algorithm.hash1 is as alias, and algorithm or key is as password
        if (!KeystoreManager.existCredential(ENCRYPTION_ALGO_PROP + "." + completePrefix)) {
            KeystoreManager.addCredential(ENCRYPTION_ALGO_PROP + "." + completePrefix, algorithm);
            KeystoreManager.addCredential(ENCRYPTION_KEY_PROP + "." + completePrefix, secretKey);
        }
        return completePrefix;
    }

    public static String decrypt(String encryptedText){
        try {
            if (encryptedText != null) {
                // this if is for migration from data encrypted by AES in client database
                if (encryptedText.startsWith(ENCRYPTION_CHECK_STRING)) {
                    encryptedText = encryptedText.replaceFirst(ENCRYPTION_CHECK_STRING, "");
                    SecretKeySpec secretKey = buildSecretKey();
                    cipher = Cipher.getInstance(OLD_AES_ENCRYPTION_ALGORITHM);
                    cipher.init(Cipher.DECRYPT_MODE, secretKey);

                    return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedText)));
                }
                else if (encryptedText.startsWith(PREFIX)) {
                    String completePrefix = encryptedText.substring(0, PREFIX.length() + LENGTH_HASH_MD5_EN_HEXA); // get complete prefix
                    encryptedText = encryptedText.substring(PREFIX.length() + LENGTH_HASH_MD5_EN_HEXA + 1);

                    String algo = null;
                    String key = null;
                    if (KeystoreManager.existCredential(ENCRYPTION_ALGO_PROP + "." + completePrefix)) {
                        algo = KeystoreManager.retrieveCredential(ENCRYPTION_ALGO_PROP + "." + completePrefix);
                        key = KeystoreManager.retrieveCredential(ENCRYPTION_KEY_PROP + "." + completePrefix);
                    }

                    assert algo != null;
                    cipher = Cipher.getInstance(algo);
                    assert key != null;
                    cipher.init(Cipher.DECRYPT_MODE, defineKey(key), ivParameterSpec);

                    return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedText)));
                }
            }
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException | NoSuchPaddingException | NoSuchAlgorithmException e) {
            log.error("Error while decrypting: " + e.getLocalizedMessage(), e);
            return ON_ERROR_RETURN;
        }

        return encryptedText;
    }

    public static IvParameterSpec generateIv() {

        byte[] iv = new byte[] { (byte) 0x14, (byte) 0x0b,
                (byte) 0x41, (byte) 0xb2, (byte) 0x2a, (byte) 0x29, (byte) 0xbe,
                (byte) 0xb4, (byte) 0x06, (byte) 0x1b, (byte) 0xda, (byte) 0x66,
                (byte) 0xb6, (byte) 0x74, (byte) 0x7e, (byte) 0x14 };

        return new IvParameterSpec(iv);
    }

    public static SecretKey defineKey(String keyStr) {
        MessageDigest md5;
        byte[] keyBytes = new byte[0];

        try {
            md5 = MessageDigest.getInstance("MD5");
            md5.update(keyStr.getBytes(StandardCharsets.UTF_8));
            keyBytes = md5.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return new SecretKeySpec(keyBytes, BUILD_KEY_ALGORITHM);
    }

    public static byte[] digest(byte[] bytesToDigest){
        synchronized (messageDigest) {
            return messageDigest.digest(bytesToDigest);
        }
    }

    private static SecretKeySpec buildSecretKey() {
        MessageDigest sha = null;
        byte[] hash = null;
        try {
            String fileKeyStr = getFileKey();
            if (!StringUtils.isBlank(fileKeyStr)) {
                byte[] fileKey = fileKeyStr.getBytes(StandardCharsets.UTF_8);
                byte[] internalKey = INTERNAL_SECRET_KEY.getBytes(StandardCharsets.UTF_8);
                sha = MessageDigest.getInstance(SHA_256_HASHING);
                sha.update(fileKey);
                sha.update(internalKey);
                hash = sha.digest();
                return new SecretKeySpec(hash, BUILD_KEY_ALGORITHM);
            } else {
                throw new EncryptionException("External secret key cannot be null while encrypting / decrypting");
            }
        } catch (Exception e) {
            log.error("Error while building Secret Key: " + e.getLocalizedMessage(), e);
        }
        return null;
    }

    private static String getFileKey() {
        return ParamBean.getInstance().getProperty(OPENCELL_SHA_KEY_PROPERTY, "a file key string");
    }

}