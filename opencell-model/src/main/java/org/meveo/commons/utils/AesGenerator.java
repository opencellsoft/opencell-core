package org.meveo.commons.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;

public class AesGenerator {

	public static void writeToFile(String path, byte[] key) throws IOException {
		File f = new File(path);
		f.getParentFile().mkdirs();

		try (FileOutputStream fos = new FileOutputStream(f)) {
			fos.write(key);
			fos.flush();
		}
	}

	public static void main(String[] args) throws NoSuchAlgorithmException, IOException {

		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(256);
		writeToFile("AESP/aesKey", keyGenerator.generateKey().getEncoded());
		

	}

}
