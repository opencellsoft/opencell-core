/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.admin;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.meveo.model.AuditableEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ObservableEntity;
import org.meveo.model.crm.Provider;
import org.meveo.model.security.Role;
import org.meveo.model.shared.Name;

/**
 * Entity that represents system user.
 */
@Entity
@ObservableEntity
@ExportIdentifier({ "userName", "provider" })
@Table(name = "ADM_USER")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "ADM_USER_SEQ")
public class User extends AuditableEntity {

	private static final long serialVersionUID = 1L;

	@Embedded
	private Name name = new Name();

	@Column(name = "USERNAME", length = 50, unique = true)
	private String userName;

	@Column(name = "PASSWORD", length = 50)
	private String password;

	@Column(name = "EMAIL", length = 100)
	private String email;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "ADM_USER_ROLE", joinColumns = @JoinColumn(name = "USER_ID"), inverseJoinColumns = @JoinColumn(name = "ROLE_ID"))
	private Set<Role> roles = new HashSet<Role>();

//	@ManyToMany(fetch = FetchType.LAZY)
//	@JoinTable(name = "ADM_USER_PROVIDER", joinColumns = @JoinColumn(name = "USER_ID"), inverseJoinColumns = @JoinColumn(name = "PROVIDER_ID"))
//	private Set<Provider> providers = new HashSet<Provider>();

	@Temporal(TemporalType.DATE)
	@Column(name = "LAST_PASSWORD_MODIFICATION")
	private Date lastPasswordModification;

	@Transient
	private String newPassword;

	@Transient
	private String newPasswordConfirmation;

	public User() {
	}

//	public Set<Provider> getProviders() {
//		return providers;
//	}
//
//	public void setProviders(Set<Provider> providers) {
//		this.providers = providers;
//	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> val) {
		this.roles = val;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getLastPasswordModification() {
		return lastPasswordModification;
	}

	public void setLastPasswordModification(Date lastPasswordModification) {
		this.lastPasswordModification = lastPasswordModification;
	}

	public boolean isPasswordExpired(int expiracyInDays) {
		boolean result = true;

		if (lastPasswordModification != null) {
			long diffMilliseconds = System.currentTimeMillis()
					- lastPasswordModification.getTime();
			result = (expiracyInDays - diffMilliseconds / (24 * 3600 * 1000L)) < 0;
		}

		return result;
	}

	public Name getName() {
		return name;
	}

	public void setName(Name name) {
		this.name = name;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getNewPasswordConfirmation() {
		return newPasswordConfirmation;
	}

	public void setNewPasswordConfirmation(String newPasswordConfirmation) {
		this.newPasswordConfirmation = newPasswordConfirmation;
	}

	public String getRolesLabel() {
		StringBuffer sb = new StringBuffer();
		if (roles != null)
			for (Role r : roles) {
				if (sb.length() != 0)
					sb.append(", ");
				sb.append(r.getDescription());
			}
		return sb.toString();
	}

	public boolean hasRole(String role) {
		boolean result = false;
		if (role != null && roles != null) {
			for (Role r : roles) {
				result = role.equalsIgnoreCase(r.getName());
				if (result) {
					break;
				}
			}
		}
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public String toString() {
		return userName;
	}

	/**
	 * Determines if user is bound to a single provider only
	 * 
	 * @return True if user is bound to a single provider
	 */
	public boolean isOnlyOneProvider() {
//		if (getProviders().size() == 1) {
//			return true;
//		} else {
//			return false;
//		}
		return true;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Check if [current] provider match the provider user is attached to 
	 */
    @Override
    public boolean doesProviderMatch(Provider providerToMatch) {

//        for (Provider providerItem : providers) {
    	Provider p=getProvider();

            if (p!=null&&p.getId().longValue() == providerToMatch.getId().longValue()) {
                return true;
            }
//        }
        return false;
    }
    
    /**
     * Check if [current] provider match the provider user is attached to 
     */
    @Override
    public boolean doesProviderMatch(Long providerToMatch) {
//        for (Provider providerItem : providers) {
    	Provider p=getProvider();
            if (p!=null&&p.getId().equals(providerToMatch)) {
                return true;
            }
//        }
        return false;
    }
    
    public boolean hasPermission(String resource, String permission) {
    	boolean isAllowed = false;
    	
    	if (getRoles() != null && getRoles().size() > 0) {
			for (Role role : getRoles()) {
				if (role.hasPermission(resource, permission)) {
					isAllowed = true;
					break;
				}
			}
		}
    	
    	return isAllowed;
    }
}