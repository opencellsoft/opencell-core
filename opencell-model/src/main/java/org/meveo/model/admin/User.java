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
package org.meveo.model.admin;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
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
import org.hibernate.annotations.Type;
import org.meveo.model.AuditableEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IReferenceEntity;
import org.meveo.model.ISearchable;
import org.meveo.model.ObservableEntity;
import org.meveo.model.ReferenceIdentifierCode;
import org.meveo.model.ReferenceIdentifierDescription;
import org.meveo.model.crm.custom.CustomFieldValues;
import org.meveo.model.hierarchy.UserHierarchyLevel;
import org.meveo.model.intcrm.AddressBook;
import org.meveo.model.security.Role;
import org.meveo.model.shared.Name;

/**
 * Application user
 * 
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Entity
@ObservableEntity
@Cacheable
@CustomFieldEntity(cftCodePrefix = "User")
@ExportIdentifier({ "userName" })
@ReferenceIdentifierCode("userName")
@ReferenceIdentifierDescription("email")
@Table(name = "adm_user")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "adm_user_seq"), })
@NamedQueries({ @NamedQuery(name = "User.listUsersInMM", query = "SELECT u FROM User u LEFT JOIN u.roles as role WHERE role.name IN (:roleNames)"),
        @NamedQuery(name = "User.getByUsername", query = "SELECT u FROM User u WHERE lower(u.userName)=:username", hints = { @QueryHint(name = "org.hibernate.cacheable", value = "TRUE") }),
        @NamedQuery(name = "User.updateLastLoginById", query = "update User set lastLoginDate=:lastLoginDate where id=:id"),
        @NamedQuery(name = "User.updateLastLoginByUsername", query = "update User set lastLoginDate=:lastLoginDate where lower(userName)=:username") })

public class User extends AuditableEntity implements ICustomFieldEntity, IReferenceEntity, ISearchable {

    private static final long serialVersionUID = 1L;

    /**
     * User name
     */
    @Embedded
    private Name name;

    /**
     * Login name
     */
    @Column(name = "username", length = 50, unique = true)
    @Size(max = 50)
    private String userName;

    /**
     * Address book - list of contacts
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "crm_address_book_id")
    private AddressBook addressbook;

    /**
     * Email
     */
    @Column(name = "email", length = 100)
    @Size(max = 100)
    private String email;

    /**
     * Roles held by the user
     */
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "adm_user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<Role>();

    /**
     * User group
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hierarchy_level_id")
    private UserHierarchyLevel userLevel;

    /**
     * Accessible entities
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "adm_secured_entity", joinColumns = { @JoinColumn(name = "user_id") })
    @AttributeOverrides(value = { @AttributeOverride(name = "code", column = @Column(name = "code", nullable = false, length = 255)),
            @AttributeOverride(name = "entityClass", column = @Column(name = "entity_class", nullable = false, length = 255)) })
    private List<SecuredEntity> securedEntities = new ArrayList<>();

    /**
     * Unique identifier - UUID
     */
    @Column(name = "uuid", nullable = false, updatable = false, length = 60)
    @Size(max = 60)
    @NotNull
    private String uuid;

    /**
     * Custom field values in JSON format
     */
    @Type(type = "cfjson")
    @Column(name = "cf_values", columnDefinition = "text")
    private CustomFieldValues cfValues;

    /**
     * Accumulated custom field values in JSON format
     */
    @Type(type = "cfjson")
    @Column(name = "cf_values_accum", columnDefinition = "text")
    private CustomFieldValues cfAccumulatedValues;

    /**
     * Last login timestamp
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_login_date")
    private Date lastLoginDate;

    /**
     * Code
     */
    @Transient
    private String code;

    /**
     * Description
     */
    @Transient
    private String description;

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
    }

    /**
     * Returns all the secured entities associated with this user's roles.
     * 
     * @return list of secured entities
     */
    public List<SecuredEntity> getRoleSecuredEntities() {
        List<SecuredEntity> result = new ArrayList<>();

        if (getRoles() != null && !getRoles().isEmpty()) {
            for (Role r : getRoles()) {
                if (r.getSecuredEntities() != null && !r.getSecuredEntities().isEmpty()) {
                    result.addAll(r.getSecuredEntities());
                }
            }
        }

        return result;
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

    /**
     * @param uuid Unique identifier
     */
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
    public CustomFieldValues getCfAccumulatedValues() {
        return cfAccumulatedValues;
    }

    @Override
    public void setCfAccumulatedValues(CustomFieldValues cfAccumulatedValues) {
        this.cfAccumulatedValues = cfAccumulatedValues;
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

    @Override
    public String getCode() {
        return getUserName();
    }

    @Override
    public void setCode(String code) {

    }

    @Override
    public String getDescription() {
        return "User " + getCode();
    }

    @Override
    public void setDescription(String description) {

    }
}