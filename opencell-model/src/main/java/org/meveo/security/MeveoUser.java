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

package org.meveo.security;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a current application user
 * 
 * @author Andrius Karpavicius
 *
 */
public abstract class MeveoUser implements Serializable {

    private static final long serialVersionUID = 5535661206200553250L;

    /*
     * User identifier - could or could not match the userName value
     */
    protected String subject;

    /**
     * User login name
     */
    protected String userName;

    /**
     * Full name of a user
     */
    protected String fullName;

    /**
     * Provider code
     */
    protected String providerCode;

    /**
     * Is user authenticated
     */
    protected boolean authenticated;

    /**
     * Was authentication forced (applies to jobs only)
     */
    protected boolean forcedAuthentication;

    /**
     * Roles/permissions held by a user. Contains both role, composite role child role and permission names
     */
    protected Set<String> roles = new HashSet<>();

    /**
     * user email address
     */
    protected String email;

    /**
     * User locale
     */
    protected String locale;

    /**
     * Timestamp when user has authenticated or token was issued
     */
    protected int authenticatedAt;

    /**
     * Authentication/session token hash/id
     */
    protected String authenticationTokenId;

    public MeveoUser() {
    }

    /**
     * Clones a user by preserving username and provider properties
     * 
     * @param user User to clone
     */
    public MeveoUser(MeveoUser user) {
        this.userName = user.getUserName();
        this.providerCode = user.getProviderCode();
    }

    public MeveoUser(String userName, String providerCode) {
        this.userName = userName;
        this.providerCode = providerCode;
    }

    public String getSubject() {
        return subject;
    }

    public String getUserName() {
        return userName;
    }

    public String getProviderCode() {
        return providerCode;
    }

    public String getFullName() {
        return fullName;
    }

    /**
     * Provide a fullname or username if name was not set
     * 
     * @return User's full name or username
     */
    public String getFullNameOrUserName() {
        if (fullName == null || fullName.length() == 0) {
            return userName;
        } else {
            return fullName;
        }
    }

    /**
     * Was user authenticated
     * 
     * @return True if user was authenticated
     */
    public boolean isAuthenticated() {
        return authenticated;
    }

    /**
     * Does user have a given role
     * 
     * @param role Role name to check
     * @return True if user has a role
     */
    public boolean hasRole(String role) {

        // if (!authenticated) {
        // return false;
        // }

        if (roles != null) {
            return roles.contains(role);
        }
        return false;
    }

    /**
     * Get all user's associated roles
     *
     * @return user's roles
     */
    public Set<String> getRoles() {
        return roles;
    }

    public String getLocale() {
        return locale;
    }

    public String toStringLong() {
        return "MeveoUser [" + " auth=" + authenticated + ", forced=" + forcedAuthentication + ", sub=" + subject + ", userName=" + userName + ", fullName=" + fullName + ", provider=" + providerCode + ", roles " + roles
                + "]";
    }

    @Override
    public String toString() {
        return "MeveoUser [forced=" + forcedAuthentication + ", sub=" + subject + ", userName=" + userName + ", provider=" + providerCode + "]";
    }

    /**
     * @return Timestamp when user has authenticated or token was issued
     */
    public int getAuthenticatedAt() {
        return authenticatedAt;
    }

    /**
     * @param authenticatedAt Timestamp when user has authenticated or token was issued
     */
    public void setAuthenticatedAt(int authenticatedAt) {
        this.authenticatedAt = authenticatedAt;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Return unproxied instance of MeveoUser - preserving username and provider code only
     * 
     * @return MeveoUser instance
     */
    public MeveoUser unProxy() {
        return new MeveoUser(this) {
            private static final long serialVersionUID = 1864122036421892838L;
        };
    }

    /**
     * Return an instance of MeveoUser - with username and provider code only
     * 
     * @param userName userName
     * @param providerCode providerCode
     * @return MeveoUser instance
     */
    public static MeveoUser instantiate(String userName, String providerCode) {
        return new MeveoUser(userName, providerCode) {
            private static final long serialVersionUID = 1864122036421892838L;
        };
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return Authentication/session token hash/id
     */
    public String getAuthenticationTokenId() {
        return authenticationTokenId;
    }

    /**
     * @param authenticationTokenId Authentication/session token hash/id
     */
    public void setAuthenticationTokenId(String authenticationTokenId) {
        this.authenticationTokenId = authenticationTokenId;
    }
}