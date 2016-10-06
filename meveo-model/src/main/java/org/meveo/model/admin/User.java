/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.admin;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.meveo.model.AuditableEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ObservableEntity;
import org.meveo.model.crm.Provider;
import org.meveo.model.hierarchy.UserHierarchyLevel;
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
    @Size(max = 50)
    private String userName;

    @Column(name = "PASSWORD", length = 50)
    @Size(max = 50)
    private String password;

    @Column(name = "EMAIL", length = 100)
    @Size(max = 100)
    private String email;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "ADM_USER_ROLE", joinColumns = @JoinColumn(name = "USER_ID"), inverseJoinColumns = @JoinColumn(name = "ROLE_ID"))
    private Set<Role> roles = new HashSet<Role>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HIERARCHY_LEVEL_ID")
    private UserHierarchyLevel userLevel;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "ADM_SECURED_ENTITY", joinColumns = { @JoinColumn(name = "USER_ID") })
    @AttributeOverrides({ @AttributeOverride(name = "code", column = @Column(name = "CODE", nullable = false, length = 60)),
            @AttributeOverride(name = "entityClass", column = @Column(name = "ENTITY_CLASS", nullable = false, length = 255)) })
    private List<SecuredEntity> securedEntities = new ArrayList<>();

    // @ManyToMany(fetch = FetchType.LAZY)
    // @JoinTable(name = "ADM_USER_PROVIDER", joinColumns = @JoinColumn(name =
    // "USER_ID"), inverseJoinColumns = @JoinColumn(name = "PROVIDER_ID"))
    // private Set<Provider> providers = new HashSet<Provider>();

    @Temporal(TemporalType.DATE)
    @Column(name = "LAST_PASSWORD_MODIFICATION")
    private Date lastPasswordModification;

    @Transient
    private String newPassword;

    @Transient
    private String newPasswordConfirmation;

    @Transient
    private Map<Class<?>, Set<SecuredEntity>> securedEntitiesMap;

    public User() {
    }

    // public Set<Provider> getProviders() {
    // return providers;
    // }
    //
    // public void setProviders(Set<Provider> providers) {
    // this.providers = providers;
    // }

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
        int result = prime * 1;// super.hashCode();
        result = result + ((getUserName() == null) ? 0 : getUserName().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        } else if (!(obj instanceof User)) { // Fails with proxed objects: getClass() != obj.getClass()){
            return false;
        }

        User other = (User) obj;

        if (getId() != null && other.getId() != null && getId().equals(other.getId())) {
            // return true;
        }

        if (userName == null) {
            if (other.getUserName() != null) {
                return false;
            }
        } else if (!userName.equals(other.getUserName())) {
            return false;
        }
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
        // if (getProviders().size() == 1) {
        // return true;
        // } else {
        // return false;
        // }
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

        // for (Provider providerItem : providers) {
        Provider p = getProvider();

        if (p != null && p.getId().longValue() == providerToMatch.getId().longValue()) {
            return true;
        }
        // }
        return false;
    }

    /**
     * Check if [current] provider match the provider user is attached to
     */
    @Override
    public boolean doesProviderMatch(Long providerToMatch) {
        // for (Provider providerItem : providers) {
        Provider p = getProvider();
        if (p != null && p.getId().equals(providerToMatch)) {
            return true;
        }
        // }
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

    public List<SecuredEntity> getSecuredEntities() {
        return securedEntities;
    }

    public void setSecuredEntities(List<SecuredEntity> securedEntities) {
        this.securedEntities = securedEntities;
        initializeSecuredEntitiesMap();
    }

    public Map<Class<?>, Set<SecuredEntity>> getSecuredEntitiesMap() {
        if (securedEntitiesMap == null || securedEntitiesMap.isEmpty()) {
            initializeSecuredEntitiesMap();
        }
        return securedEntitiesMap;
    }

    private void initializeSecuredEntitiesMap() {
        securedEntitiesMap = new HashMap<>();
        Set<SecuredEntity> securedEntitySet = null;
        try {
            for (SecuredEntity securedEntity : securedEntities) {
                Class<?> securedBusinessEntityClass = Class.forName(securedEntity.getEntityClass());
                if (securedEntitiesMap.get(securedBusinessEntityClass) == null) {
                    securedEntitySet = new HashSet<>();
                    securedEntitiesMap.put(securedBusinessEntityClass, securedEntitySet);
                }
                securedEntitiesMap.get(securedBusinessEntityClass).add(securedEntity);
            }
        } catch (ClassNotFoundException e) {
            // do nothing
        }
    }

    public UserHierarchyLevel getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(UserHierarchyLevel userLevel) {
        this.userLevel = userLevel;
    }

    public String getNameOrUsername() {
        if (name != null && name.toString().length() > 0) {
            return name.toString();
        }

        return userName;
    }

}