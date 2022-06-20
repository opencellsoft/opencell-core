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

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.IEnable;
import org.meveo.model.IEntity;

/**
 * Entity accessibility rules
 */
@Entity
@Table(name = "adm_secured_entity")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "adm_secured_entity_seq"), })
@NamedQueries({
        @NamedQuery(name = "SecuredEntity.listByRoleName", query = "SELECT s from org.meveo.model.admin.SecuredEntity s where s.roleName=:roleName", hints = {
                @QueryHint(name = "org.hibernate.cacheable", value = "TRUE") }),
        @NamedQuery(name = "SecuredEntity.listByUserName", query = "SELECT s from org.meveo.model.admin.SecuredEntity s where lower(s.userName)=:userName", hints = {
                @QueryHint(name = "org.hibernate.cacheable", value = "TRUE") }),
        @NamedQuery(name = "SecuredEntity.validateByRoleName", query = "SELECT count(*) from org.meveo.model.admin.SecuredEntity s where s.roleName=:roleName and entity_code=:entityCode and entity_class=:entityClass"),
        @NamedQuery(name = "SecuredEntity.validateByUserName", query = "SELECT count(*) from org.meveo.model.admin.SecuredEntity s where lower(s.userName)=:userName and entity_code=:entityCode and entity_class=:entityClass"),
        @NamedQuery(name = "SecuredEntity.listForCurrentUser", query = "SELECT new org.meveo.security.SecuredEntity(s.entityId, s.entityCode, s.entityClass, s.permission) from org.meveo.model.admin.SecuredEntity s where s.disabled=false and (lower(s.userName)=:userName or s.roleName in :roleNames)", hints = {
                @QueryHint(name = "org.hibernate.cacheable", value = "TRUE") }) })
public class SecuredEntity implements Serializable, IEntity, IEnable {

    private static final long serialVersionUID = 84222776645282176L;

    /**
     * Record/entity identifier
     */
    @Id
    @GeneratedValue(generator = "ID_GENERATOR", strategy = GenerationType.AUTO)
    @Column(name = "id")
    @Access(AccessType.PROPERTY) // Access is set to property so a call to getId() wont trigger hibernate proxy loading
    private Long id;

    /**
     * Secured entity is for a role
     */
    @Column(name = "role_name", length = 255)
    @Size(max = 255)
    private String roleName;

    /**
     * Secured entity is for a user
     */
    @Column(name = "user_name", length = 255)
    @Size(max = 255)
    private String userName;

    /**
     * Accessible entity id
     */
    @Column(name = "entity_id")
    private Long entityId;

    /**
     * Accessible entity code
     */
    @Column(name = "entity_code", nullable = false, length = 255)
    @Size(max = 255, min = 1)
    @NotNull
    private String entityCode;

    /**
     * Accessible entity type/class
     */
    @Column(name = "entity_class", length = 255)
    @Size(max = 255)
    @NotNull
    private String entityClass;

    /**
     * Allowed action to perform on the entity
     */
    @Column(name = "permission", length = 6)
    @Enumerated(EnumType.STRING)
    private SecuredEntityPermissionEnum permission = SecuredEntityPermissionEnum.READ;

    @Type(type = "numeric_boolean")
    @Column(name = "disabled", nullable = false)
    @NotNull
    private boolean disabled;

    public SecuredEntity() {
    }

    /**
     * Constructor
     * 
     * @param entityId Accessible entity id
     * @param entityCode Accessible entity code
     * @param entityClass Accessible entity type/class
     * @param permission Allowed action to perform on the entity
     */
    public SecuredEntity(Long entityId, String entityCode, String entityClass, SecuredEntityPermissionEnum permission) {

        this.entityId = entityId;
        this.entityCode = entityCode;
        this.entityClass = entityClass;
        this.permission = permission == null ? SecuredEntityPermissionEnum.READ : permission;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return Accessible entity ID
     */
    public Long getEntityId() {
        return entityId;
    }

    /**
     * @param entityId Accessible entity ID
     */
    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    /**
     * @return Accessible entity code
     */
    public String getEntityCode() {
        return entityCode;
    }

    /**
     * @param code Accessible entity code
     */
    public void setEntityCode(String entityCode) {
        this.entityCode = entityCode;
    }

    public String getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(String entityClass) {
        this.entityClass = entityClass;
    }

    public String readableEntityClass() {
        if (entityClass != null) {
            return ReflectionUtils.getHumanClassName(entityClass);
        }
        return "";
    }

    /**
     * 
     * @return Allowed action to perform on the entity
     */
    public SecuredEntityPermissionEnum getPermission() {
        return permission;
    }

    /**
     * @param permission Allowed action to perform on the entity
     */
    public void setPermission(SecuredEntityPermissionEnum permission) {
        this.permission = permission;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || (!(obj instanceof SecuredEntity))) {
            return false;
        }
        final SecuredEntity other = (SecuredEntity) obj;
        return Objects.equals(this.toStringWoutId(), other.toStringWoutId());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.toStringWoutId());
        return hash;
    }

    @Override
    public String toString() {
        return entityClass + ":" + entityId + ":" + entityCode + (permission != null ? ":" + permission : "") + disabled;
    }

    public String toStringWoutId() {
        return entityClass + ":" + entityCode + (permission != null ? ":" + permission : "") + disabled;
    }

    /**
     * @return Is secured entity configuration disabled
     */
    public boolean isDisabled() {
        return disabled;
    }

    /**
     * @param disabled Is secured entity configuration disabled
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * @return Is secured entity configuration active
     */
    public boolean isActive() {
        return !disabled;
    }

    /**
     * @param active Is secured entity configuration active
     */
    public void setActive(boolean active) {
        setDisabled(!active);
    }

    /**
     * @return Role name to link secured entity to
     */
    public String getRoleName() {
        return roleName;
    }

    /**
     * @param roleName Role name to link secured entity to
     */
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    /**
     * @return User name to link secured entity to
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName User name to link secured entity to
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public boolean isTransient() {
        return id == null;
    }

    /**
     * Permission to perform an action on a Secured entity
     */
    public enum SecuredEntityPermissionEnum {
        /**
         * Permission to list/find
         */
        READ,

        /**
         * Permission to update
         */
        UPDATE,

        /**
         * Permission to delete
         */
        DELETE;

        public String getLabel() {
            return this.getClass().getSimpleName() + "." + this.name();
        }
    }
}