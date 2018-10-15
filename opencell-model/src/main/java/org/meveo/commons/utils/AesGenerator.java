package org.meveo.commons.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AesGenerator {

	private static final Logger log = LoggerFactory.getLogger(AesGenerator.class);

	public static void writeToFile(String path, byte[] key) throws IOException {
		File f = new File(path);
		f.getParentFile().mkdirs();

		FileOutputStream fos = new FileOutputStream(f);
		fos.write(key);
		fos.flush();
		fos.close();
	}

	public static void main(String[] args) throws NoSuchAlgorithmException, IOException {

		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(256);
		// writeToFile("AESPair/aesKey", keyGenerator.generateKey().getEncoded());
		String key = Base64.encodeBase64String(keyGenerator.generateKey().getEncoded());

	}

}
