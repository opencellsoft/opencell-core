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

    protected String locale;

    protected int authTime;

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

    public String getLocale() {
        return locale;
    }

    @Override
    public String toString() {
        return "MeveoUser [" + " auth=" + authenticated + ", forced=" + forcedAuthentication + ", sub=" + subject + ", userName=" + userName + ", fullName=" + fullName
                + ", provider=" + providerCode + ", roles " + roles + "]";
    }

    public Object toStringShort() {
        return "MeveoUser [forced=" + forcedAuthentication + ", sub=" + subject + ", userName=" + userName + ", provider=" + providerCode + "]";
    }

    public int getAuthTime() {
        return authTime;
    }

    public void setAuthTime(int authTime) {
        this.authTime = authTime;
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
}