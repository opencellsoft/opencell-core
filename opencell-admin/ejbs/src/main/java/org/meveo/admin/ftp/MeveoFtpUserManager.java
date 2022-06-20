/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.admin.ftp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.ftpserver.ftplet.Authentication;
import org.apache.ftpserver.ftplet.AuthenticationFailedException;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.usermanager.PasswordEncryptor;
import org.apache.ftpserver.usermanager.impl.AbstractUserManager;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.ConcurrentLoginPermission;
import org.apache.ftpserver.usermanager.impl.TransferRatePermission;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.service.admin.impl.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Wassim Drira
 * @lastModifiedVersion 5.0
 * 
 */
public class MeveoFtpUserManager extends AbstractUserManager {
    private static final String FTPREAD = "ftpread";
    private static final String FTPWRITE = "ftpwrite";
    private static final int HOUR = 60 * 60;
    private static final String ADMINISTRATOR = "administrateur";

    private Logger log = LoggerFactory.getLogger(MeveoFtpUserManager.class);

    private UserService userService;

    public MeveoFtpUserManager(String adminName, PasswordEncryptor passwordEncryptor, UserService userService) {
        super(adminName, passwordEncryptor);
        this.userService = userService;
    }

    @Override
    public User authenticate(Authentication authentication) throws AuthenticationFailedException {
        // if (authentication instanceof UsernamePasswordAuthentication) {
        // UsernamePasswordAuthentication upauth = (UsernamePasswordAuthentication) authentication;
        //
        // String user = upauth.getUsername();
        // String password = upauth.getPassword();
        // log.debug("authenticate ..." + user);
        //
        // if (user == null) {
        // throw new AuthenticationFailedException("Authentication failed");
        // }
        //
        // if (password == null) {
        // throw new IllegalArgumentException("Authentication failed because null password!");
        // }
        //
        // log.error("Ftp user {} failed login into FTP server - NOT SUPPORTED", user);
        // throw new AuthenticationFailedException("Authentication failed");
        //
        // } else {
        throw new IllegalArgumentException("Authentication not supported by this user manager");
        // }
    }

    @Override
    public void delete(String arg0) throws FtpException {
        log.debug("ftp delete a user ");
        throw new FtpException("Don't support");

    }

    @Override
    public boolean doesExist(String username) throws FtpException {
        log.debug("doesExisted .. " + username);
        org.meveo.model.admin.User meveoUser = userService.findByUsername(username, false);
        return meveoUser != null;
    }

    @Override
    public String[] getAllUserNames() throws FtpException {
        log.debug("getAllUserNames...");
        throw new FtpException("Don't support");
    }

    @Override
    public User getUserByName(String username) throws FtpException {
        log.debug("getUserByName... " + username);
        org.meveo.model.admin.User result = userService.findByUsername(username, false);
        if (result == null) {
            log.error("Ftp user {} doesn't exist!", username);
            throw new FtpException(String.format("Ftp user {} doesn't exist!", username));
        }

        BaseUser user = new BaseUser();
        user.setName(username);
        List<Authority> authorities = new ArrayList<Authority>();
        authorities.add(new ConcurrentLoginPermission(0, 0));
        authorities.add(new TransferRatePermission(0, 0));
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
     * @param meveoUser Meveo user
     * @return ftp user
     */
    private User getUserFromMeveoUser(org.meveo.model.admin.User meveoUser) throws FtpException {
        ParamBeanFactory paramBeanFactory = (ParamBeanFactory) EjbUtils.getServiceInterface(ParamBeanFactory.class.getSimpleName());
        String homeDir = String.format("%s", paramBeanFactory.getChrootDir());
        log.debug("ftp user home {}", homeDir);
        File home = new File(homeDir);
        if (!home.exists()) {
            home.mkdirs();
        }
        BaseUser user = new BaseUser();
        user.setName(meveoUser.getUserName());
        user.setHomeDirectory(homeDir);
        List<Authority> authorities = new ArrayList<Authority>();

//        boolean hasRole = false;
//        for (Role role : roles) {
//            meveoPermissions.addAll(role.getPermissions());
//            if (role.getName().equalsIgnoreCase(ADMINISTRATOR)) {
//                hasRole = true;
//            }
//        }
//        if (!hasRole) {
//            log.error("ftp user {} don't have administrator role", meveoUser.getUserName());
//            throw new FtpException("no administrator role!");
//        }
//        log.debug("meveo user has permissions {}", meveoPermissions.size());
        boolean hasRead = false;
        boolean hasWrite = false;
//        for (Permission p : meveoPermissions) {
//            if (FTPREAD.equalsIgnoreCase(p.getPermission())) {
//                hasRead = true;
//            }
//            if (FTPWRITE.equalsIgnoreCase(p.getPermission())) {
//                hasWrite = true;
//            }
//            if (hasRead && hasWrite) {
//                break;
//            }
//        }
        if (!hasRead) {
            log.error("ftp user {} doesn't have read permission!", meveoUser.getUserName());
            throw new FtpException("no read permission!");
        }
        log.debug("ftp user {} has read permission", meveoUser.getUserName());
        if (hasWrite) {
            log.debug("ftp user {} has write permission", meveoUser.getUserName());
            authorities.add(new WritePermission());
        }

        authorities.add(new ConcurrentLoginPermission(0, 0));
        authorities.add(new TransferRatePermission(0, 0));

        user.setAuthorities(authorities);

        user.setMaxIdleTime(HOUR);
        return user;
    }

}
