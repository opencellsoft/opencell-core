package org.meveo.commons.encryption;

import com.google.common.base.Joiner;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
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
import java.security.SecureRandom;
import java.security.Security;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * EncryptionFactory
 *
 * @author Thang Nguyen
 * @author Wassim Drira
 * @lastModifiedVersion 12.0.0
 *
 */
public class EncryptionFactory {

    private static final Logger log = LoggerFactory.getLogger(IEncryptable.class);

    private static final IvParameterSpec ivParameterSpec;

    private static final String BY_DEFAULT_DIGEST_ALGO = "SHA-256";

    private static final String BUILD_KEY_ALGORITHM = "AES";

    private static Cipher cipher = null;

    private static MessageDigest messageDigest = null;

    public static Map<String, String> mapPrefAndAlgoKey;

    public static final String PREFIX = "pref";

    private static final String SEPARATOR = "|";

    public static final String MAP_PREFIX_ALGO_AND_KEY = "encrypt.mapPrefixAndAlgoKey";

    private static final int LENGTH_HASH_MD5 = 24;



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

        // get and convert from string to mapPrefAndAlgoKey
        mapPrefAndAlgoKey = new HashMap<>();
        String strMapPrefAndAlgoKey = ParamBean.getInstance().getProperty(MAP_PREFIX_ALGO_AND_KEY, null);
        if (strMapPrefAndAlgoKey != null) {
            String[] pairs = strMapPrefAndAlgoKey.split(",");
            for (String pair : pairs) {
                String pref = pair.substring(0, PREFIX.length() + LENGTH_HASH_MD5);
                String algoKey = pair.substring(PREFIX.length() + LENGTH_HASH_MD5 + 1);
                mapPrefAndAlgoKey.put(pref, algoKey);
            }
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

    public static byte[] concatenateByteArrays(byte[] firstArr, byte[] secArr) {
        byte[] result = new byte[firstArr.length + secArr.length];
        System.arraycopy(firstArr, 0, result, 0, firstArr.length);
        System.arraycopy(secArr, 0, result, firstArr.length, secArr.length);

        return result;
    }

    public static String getCompletePrefix(String algorithm, String secretKey){
        String algoKey = algorithm + SEPARATOR + secretKey;
        String completePrefix = PREFIX + getHashOfAlgoKey(algoKey);

        if (! mapPrefAndAlgoKey.containsKey(completePrefix)) {
            mapPrefAndAlgoKey.put(completePrefix, algoKey);
            ParamBean.getInstance().setProperty(MAP_PREFIX_ALGO_AND_KEY,
                    String.join(",", Joiner.on(",").withKeyValueSeparator("=").join(mapPrefAndAlgoKey)));

            ParamBean.getInstance().saveProperties();
        }

        return completePrefix;
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

        return Base64.getEncoder().encodeToString(keyBytes);
    }

    public static String encrypt(String clearText){
        try {
            String algorithm = ParamBean.getInstance().getProperty("encrypt.algorithm", null);
            String secretKey = ParamBean.getInstance().getProperty("encrypt.secretKey", null);
            String completePrefix = getCompletePrefix(algorithm, secretKey);

            cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, defineKey(secretKey), ivParameterSpec);

            return completePrefix + SEPARATOR
                    + Base64.getEncoder().encodeToString(cipher.doFinal(clearText.getBytes()));
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decrypt(String encryptedText){
        try {
            if (encryptedText != null) {
                // this if is for migration from data encrypted by AES in client database
                if (encryptedText.startsWith(ENCRYPTION_CHECK_STRING)) {
                    encryptedText = encryptedText.replace(ENCRYPTION_CHECK_STRING, "");
                    SecretKeySpec secretKey = buildSecretKey();
                    cipher = Cipher.getInstance(OLD_AES_ENCRYPTION_ALGORITHM);
                    cipher.init(Cipher.DECRYPT_MODE, secretKey);

                    return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedText)));
                }
                else if (encryptedText.startsWith(PREFIX)) {
                    String completePrefix = encryptedText.substring(0, PREFIX.length() + LENGTH_HASH_MD5); // get complete prefix
                    encryptedText = encryptedText.substring(PREFIX.length() + LENGTH_HASH_MD5 + 1);

                    String[] algoAndKey = new String[2];
                    if (mapPrefAndAlgoKey.containsKey(completePrefix)) {
                        algoAndKey = mapPrefAndAlgoKey.get(completePrefix).split("\\|");
                    }
                    cipher = Cipher.getInstance(algoAndKey[0]);
                    cipher.init(Cipher.DECRYPT_MODE, defineKey(algoAndKey[1]), ivParameterSpec);

                    return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedText)));
                }
            }
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException | NoSuchPaddingException | NoSuchAlgorithmException e) {
            log.error("Error while encrypting: " + e.getLocalizedMessage(), e);
            return ON_ERROR_RETURN;
        }

        return encryptedText;
    }

    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
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
                byte[] fileKey = fileKeyStr.getBytes("UTF-8");
                byte[] internalKey = INTERNAL_SECRET_KEY.getBytes("UTF-8");
                sha = MessageDigest.getInstance(SHA_256_HASHING);
                sha.update(fileKey);
                sha.update(internalKey);
                hash = sha.digest();
                return new SecretKeySpec(hash, BUILD_KEY_ALGORITHM);
            } else {
                throw new Exception("External secret key cannot be null while encrypting / decrypting");
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
