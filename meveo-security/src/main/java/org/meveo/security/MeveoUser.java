package org.meveo.security;

import java.io.Serializable;

public abstract class MeveoUser implements Serializable {

    private static final long serialVersionUID = 5535661206200553250L;

    protected String subject;

    protected String userName;

    protected String fullName;

    protected String providerCode;
    
    protected Provider providerEntity;

    protected boolean authenticated;

    protected boolean forcedAuthentication;

    public MeveoUser() {
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
    
    public Provider getProvider() {
        return providerEntity;
    }

    public String getFullName() {
        return fullName;
    }

    public abstract boolean hasRole(String role);

    @Override
    public String toString() {
        return "MeveoUser [" + hasRole("user") + " " + hasRole("adminas") + " auth=" + authenticated + ", forced=" + forcedAuthentication + ", sub=" + subject + ", userName="
                + userName + ", fullName=" + fullName + ", provider=" + providerCode + "]";
    }

}