package org.meveo.admin.ftp;

import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.usermanager.UserManagerFactory;
import org.meveo.service.admin.impl.UserService;

public class MeveoFtpUserManagerFactory implements UserManagerFactory {

	private UserService userService;

	public MeveoFtpUserManagerFactory(UserService userService) {
		this.userService = userService;
	}

	@Override
	public UserManager createUserManager() {
		return new MeveoFtpUserManager("meveoftp.admin", new ShaPasswordEncryptor(), userService);
	}

}
