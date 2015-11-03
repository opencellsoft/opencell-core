package org.meveo.admin.ftp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.ftpserver.ftplet.Authentication;
import org.apache.ftpserver.ftplet.AuthenticationFailedException;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.usermanager.PasswordEncryptor;
import org.apache.ftpserver.usermanager.UsernamePasswordAuthentication;
import org.apache.ftpserver.usermanager.impl.AbstractUserManager;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.ConcurrentLoginPermission;
import org.apache.ftpserver.usermanager.impl.TransferRatePermission;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.meveo.admin.exception.LoginException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.security.Permission;
import org.meveo.model.security.Role;
import org.meveo.service.admin.impl.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MeveoFtpUserManager extends AbstractUserManager {
	private static final String FTPREAD="ftpread";
	private static final String FTPWRITE="ftpwrite";
	private static final int HOUR=60*60;
	private static final String ADMINISTRATOR="administrateur";

	private Logger log = LoggerFactory.getLogger(MeveoFtpUserManager.class);
	private static final String PREFIX = ParamBean.getInstance().getProperty("ftpserver.path", "/tmp/meveoftp");

	private UserService userService;

	public MeveoFtpUserManager(String adminName, PasswordEncryptor passwordEncryptor, UserService userService) {
		super(adminName, passwordEncryptor);
		this.userService = userService;
	}

	@Override
	public User authenticate(Authentication authentication) throws AuthenticationFailedException {
		if (authentication instanceof UsernamePasswordAuthentication) {
			UsernamePasswordAuthentication upauth = (UsernamePasswordAuthentication) authentication;

			String user = upauth.getUsername();
			String password = upauth.getPassword();
			log.debug("authenticate ..." + user);

			if (user == null) {
				throw new AuthenticationFailedException("Authentication failed");
			}

			if (password == null) {
				throw new IllegalArgumentException("Authentication failed because null password!");
			}

			org.meveo.model.admin.User meveoUser = null;
			try {
				meveoUser = userService.loginChecks(user, password);
				return getUserFromMeveoUser(meveoUser);
			} catch (LoginException |FtpException e) {
				log.error("failed login into FTP server with credential {}", user, e);
				throw new AuthenticationFailedException("Authentication failed");
			}

		} else {
			throw new IllegalArgumentException("Authentication not supported by this user manager");
		}
	}

	@Override
	public void delete(String arg0) throws FtpException {
		log.debug("ftp delete a user ");
		throw new FtpException("Don't support");

	}

	@Override
	public boolean doesExist(String username) throws FtpException {
		log.debug("doesExisted .. " + username);
		org.meveo.model.admin.User meveoUser = userService.findByUsername(username);
		return meveoUser != null && meveoUser.isActive();
	}

	@Override
	public String[] getAllUserNames() throws FtpException {
		log.debug("getAllUserNames...");
		throw new FtpException("Don't support");
	}

	@Override
	public User getUserByName(String username) throws FtpException {
		log.debug("getUserByName... " + username);
		org.meveo.model.admin.User meveoUser = userService.findByUsername(username);
		if (meveoUser == null || meveoUser.isDisabled()) {
			log.debug("not found ftp user {} or user is disabled",username);
			return null;
		}
		String homeDir = String.format("%s%s%s", PREFIX,File.separator, meveoUser.getUserName());
		log.debug("ftp user home {}",homeDir);
		File home = new File(homeDir);
		if (!home.exists()) {
			home.mkdirs();
		}
		BaseUser user = new BaseUser();
		user.setName(meveoUser.getUserName());
		user.setEnabled(meveoUser.isActive());
		user.setHomeDirectory(homeDir);
		List<Authority> authorities = new ArrayList<Authority>();
		authorities.add(new ConcurrentLoginPermission(0,0));
		authorities.add(new TransferRatePermission(0,0));

		user.setAuthorities(authorities);

		user.setMaxIdleTime(HOUR);
		return user;
	}

	@Override
	public void save(User arg0) throws FtpException {
		log.debug("save user... " + arg0);
		throw new FtpException("Don't support");
	}

	/**
	 * load ftp User by meveo User.
	 * 
	 * @param meveoUser
	 *            Meveo user
	 * @return ftp user
	 */
	private User getUserFromMeveoUser(org.meveo.model.admin.User meveoUser) throws FtpException{
		String homeDir = String.format("%s%s%s", PREFIX,File.separator, meveoUser.getUserName());
		log.debug("ftp user home {}",homeDir);
		File home = new File(homeDir);
		if (!home.exists()) {
			home.mkdirs();
		}
		BaseUser user = new BaseUser();
		user.setName(meveoUser.getUserName());
		user.setEnabled(meveoUser.isActive());
		user.setHomeDirectory(homeDir);
		List<Authority> authorities = new ArrayList<Authority>();

		Set<Role> roles=meveoUser.getRoles();
		log.debug("meveo user has roles {}",roles.size());
		List<Permission> meveoPermissions=new ArrayList<Permission>();
		boolean hasRole=false;
		for(Role role:roles){
			meveoPermissions.addAll(role.getPermissions());
			if(role.getName().equalsIgnoreCase(ADMINISTRATOR)){
				hasRole=true;
			}
		}
		if(!hasRole){
			log.error("ftp user {} don't have administrator role",meveoUser.getUserName());
			throw new FtpException("no administrator role!");
		}
		log.debug("meveo user has permissions {}",meveoPermissions.size());
		boolean hasRead=false;
		boolean hasWrite=false;
		for(Permission p:meveoPermissions){
			if(FTPREAD.equalsIgnoreCase(p.getPermission())){
				hasRead=true;
			}
			if(FTPWRITE.equalsIgnoreCase(p.getPermission())){
				hasWrite=true;
			}
			if(hasRead&&hasWrite){
				break;
			}
		}
		if(!hasRead){
			log.error("ftp user {} doesn't have read permission!",meveoUser.getUserName());
			throw new FtpException("no read permission!");
		}
		log.debug("ftp user {} has read permission",meveoUser.getUserName());
		if(hasWrite){
			log.debug("ftp user {} has write permission",meveoUser.getUserName());
			authorities.add(new WritePermission());
		}

		authorities.add(new ConcurrentLoginPermission(0,0));
		authorities.add(new TransferRatePermission(0,0));

		user.setAuthorities(authorities);

		user.setMaxIdleTime(HOUR);
		return user;
	}

}
