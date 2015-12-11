package org.meveo.security;

import java.io.Serializable;

import org.meveo.model.crm.Provider;
import org.picketlink.idm.api.User;

public class MeveoUser implements User, Serializable {

	private static final long serialVersionUID = 4333140556503076034L;

	private org.meveo.model.admin.User user;
	
	private Provider provider;

	public MeveoUser(org.meveo.model.admin.User user) {
		this.user = user;
	}

	// TODO: @Override
	public String getKey() {
		return getId();
	}

	// TODO: @Override
	public String getId() {
		return user.getUserName();
	}

	public org.meveo.model.admin.User getUser() {
		return this.user;
	}

    /**
     * Refresh logged in user with a new information
     * 
     * @param user Refreshed user information
     */
    public void refreshUser(org.meveo.model.admin.User user) {
        if (this.user.equals(user)) {
            this.user = user;
        }
    }

	public void setCurrentProvider(Provider provider) {
        this.provider = provider;
	}
	
	public Provider getCurrentProvider() {
	    return provider;	    
	}
}
