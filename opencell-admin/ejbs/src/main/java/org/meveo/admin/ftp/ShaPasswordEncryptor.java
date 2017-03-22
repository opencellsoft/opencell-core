package org.meveo.admin.ftp;

import org.apache.ftpserver.usermanager.PasswordEncryptor;
import org.meveo.admin.util.security.Sha1Encrypt;

public class ShaPasswordEncryptor implements PasswordEncryptor {

	@Override
	public String encrypt(String password) {
		return Sha1Encrypt.encodePassword(password);
	}

	@Override
	public boolean matches(String passwordToCheck, String storedPassword) {
		if (storedPassword == null) {
			throw new NullPointerException("storedPassword can not be null");
		}
		if (passwordToCheck == null) {
			throw new NullPointerException("passwordToCheck can not be null");
		}

		return encrypt(passwordToCheck).equalsIgnoreCase(storedPassword);
	}

}
