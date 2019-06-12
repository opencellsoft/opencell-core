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
import java.util.UUID;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IReferenceEntity;
import org.meveo.model.ObservableEntity;
import org.meveo.model.ReferenceIdentifierCode;
import org.meveo.model.ReferenceIdentifierDescription;
import org.meveo.model.crm.custom.CustomFieldValues;
import org.meveo.model.hierarchy.UserHierarchyLevel;
import org.meveo.model.intcrm.AddressBook;
import org.meveo.model.persistence.CustomFieldValuesConverter;
import org.meveo.model.security.Role;
import org.meveo.model.shared.Name;

/**
 * Entity that represents system user.
 */
@Entity
@ObservableEntity
@Cacheable
@CustomFieldEntity(cftCodePrefix = "USER")
@ExportIdentifier({ "userName" })
@ReferenceIdentifierCode("userName")
@ReferenceIdentifierDescription("email")
@Table(name = "adm_user")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "adm_user_seq"), })
@NamedQueries({ @NamedQuery(name = "User.listUsersInMM", query = "SELECT u FROM User u LEFT JOIN u.roles as role WHERE role.name IN (:roleNames)"),
        @NamedQuery(name = "User.getByUsername", query = "SELECT u FROM User u WHERE lower(u.userName)=:username", hints = {
                @QueryHint(name = "org.hibernate.cacheable", value = "TRUE") }) })
public class User extends AuditableEntity implements ICustomFieldEntity, IReferenceEntity {

    private static final long serialVersionUID = 1L;

    @Embedded
    private Name name;

    @Column(name = "username", length = 50, unique = true)
    @Size(max = 50)
    private String userName;

    @OneToOne(fetch=FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "crm_address_book_id")
    private AddressBook addressbook;
    
    @Column(name = "email", length = 100)
    @Size(max = 100)
    private String email;

    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "adm_user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<Role>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hierarchy_level_id")
    private UserHierarchyLevel userLevel;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "adm_secured_entity", joinColumns = { @JoinColumn(name = "user_id") })
    @AttributeOverrides({ @AttributeOverride(name = "code", column = @Column(name = "code", nullable = false, length = 255)),
            @AttributeOverride(name = "entityClass", column = @Column(name = "entity_class", nullable = false, length = 255)) })
    private List<SecuredEntity> securedEntities = new ArrayList<>();

    @Column(name = "uuid", nullable = false, updatable = false, length = 60)
    @Size(max = 60)
    @NotNull
    private String uuid;

    // @Type(type = "json")
    @Convert(converter = CustomFieldValuesConverter.class)
    @Column(name = "cf_values", columnDefinition = "text")
    private CustomFieldValues cfValues;

    @Transient
    private Map<Class<?>, Set<SecuredEntity>> securedEntitiesMap;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_login_date")
    private Date lastLoginDate;

    public User() {
    }
    
    public AddressBook getAddressbook() {
		return addressbook;
	}

	public void setAddressbook(AddressBook addressbook) {
		this.addressbook = addressbook;
	}

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

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
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
        return 961 + (("User" + (userName == null ? "" : userName)).hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof User)) {
            return false;
        }

        User other = (User) obj;

        if (getId() != null && other.getId() != null && getId().equals(other.getId())) {
            return true;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
        if (name != null && name.getFullName().length() > 0) {
            return name.getFullName();
        }

        return userName;
    }
    
    /**
     * setting uuid if null
     */
    @PrePersist
    public void setUUIDIfNull() {
    	if (uuid == null) {
    		uuid = UUID.randomUUID().toString();
    	}
    }
    
    @Override
    public String getUuid() {
    	setUUIDIfNull(); // setting uuid if null to be sure that the existing code expecting uuid not null will not be impacted
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public CustomFieldValues getCfValues() {
        return cfValues;
    }

    @Override
    public void setCfValues(CustomFieldValues cfValues) {
        this.cfValues = cfValues;
    }

    @Override
    public String clearUuid() {
        String oldUuid = uuid;
        uuid = UUID.randomUUID().toString();
        return oldUuid;
    }

    @Override
    public ICustomFieldEntity[] getParentCFEntities() {
        return null;
    }

    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    @Override
	public String getReferenceCode() {
		return getUserName();
	}
    
    @Override
	public void setReferenceCode(Object value) {
		setUserName(value.toString());
	}

	@Override
	public String getReferenceDescription() {
		return getNameOrUsername();
	}

}