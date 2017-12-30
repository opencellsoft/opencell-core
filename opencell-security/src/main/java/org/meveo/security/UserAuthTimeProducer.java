package org.meveo.security;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;

/**
 * @author Edward P. Legaspi
 * 
 **/
@SessionScoped
public class UserAuthTimeProducer implements Serializable {

    private static final long serialVersionUID = 5510518807024231791L;

    private int authTime = 0;

    public int getAuthTime() {
        return authTime;
    }

    public void setAuthTime(int authTime) {
        if (this.authTime != authTime) {
            this.authTime = authTime;
        }
    }

}
