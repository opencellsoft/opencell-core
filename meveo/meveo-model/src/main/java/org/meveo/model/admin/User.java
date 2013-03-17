/*
* (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
*
* Licensed under the GNU Public Licence, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.gnu.org/licenses/gpl-2.0.txt
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.meveo.model.admin;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.meveo.model.AuditableEntity;
import org.meveo.model.crm.Provider;
import org.meveo.model.security.Role;
import org.meveo.model.shared.Name;

/**
 * Entity that represents system user.
 * 
 * @author Gediminas Ubartas
 * @created May 31, 2010
 * 
 */
@Entity
@Table(name = "ADM_USER")
//@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "ADM_USER_SEQ")
public class User extends AuditableEntity {

    private static final long serialVersionUID = 1L;

    @Embedded
    private Name name = new Name();

    @Column(name = "USERNAME", length = 50, unique = true)
    private String userName;

    @Column(name = "PASSWORD", length = 50)
    private String password;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "ADM_USER_ROLE", joinColumns = @JoinColumn(name = "USER_ID"), inverseJoinColumns = @JoinColumn(name = "ROLE_ID"))
    private List<Role> roles = new ArrayList<Role>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "ADM_USER_PROVIDER", joinColumns = @JoinColumn(name = "USER_ID"), inverseJoinColumns = @JoinColumn(name = "PROVIDER_ID"))
    private List<Provider> providers = new ArrayList<Provider>();

    @Temporal(TemporalType.DATE)
    @Column(name = "LAST_PASSWORD_MODIFICATION")
    private Date lastPasswordModification;

    @Transient
    private String newPassword;

    @Transient
    private String newPasswordConfirmation;

    public User() {
    }

    public List<Provider> getProviders() {
        return providers;
    }

    public void setProviders(List<Provider> providers) {
        this.providers = providers;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> val) {
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
            long diffMilliseconds = System.currentTimeMillis() - lastPasswordModification.getTime();
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
        if (getProviders().size() == 1) {
            return true;
        } else {
            return false;
        }
    }
}
