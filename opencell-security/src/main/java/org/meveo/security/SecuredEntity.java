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

package org.meveo.security;

import java.io.Serializable;
import java.util.Objects;

import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.admin.SecuredEntity.SecuredEntityPermissionEnum;

/**
 * Entity accessibility rules
 */
public class SecuredEntity implements Serializable {

    private static final long serialVersionUID = 84222776645282176L;

    /**
     * Accessible entity id
     */
    private Long id;

    /**
     * Accessible entity code
     */
    private String code;

    /**
     * Accessible entity type/class
     */
    private String entityClass;

    /**
     * Allowed action to perform on the entity
     */
    private SecuredEntityPermissionEnum permission = SecuredEntityPermissionEnum.READ;

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

        this.id = entityId;
        this.code = entityCode;
        this.entityClass = entityClass;
        this.permission = permission == null ? SecuredEntityPermissionEnum.READ : permission;
    }

    @Override
    public String toString() {
        return entityClass + ":" + id + ":" + code + (permission != null ? ":" + permission : "");
    }

    /**
     * @return Accessible entity ID
     */
    public Long getId() {
        return id;
    }

    /**
     * @param code Accessible entity ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return Accessible entity code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code Accessible entity code
     */
    public void setCode(String code) {
        this.code = code;
    }

    public String getEntityClass() {
        return entityClass;
    }

    /**
     * @param entityClass Accessible entity type/class
     */
    public void setEntityClass(String entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * @return Accessible entity type/class in a human-friendly way
     */
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
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.toString());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || (!(obj instanceof SecuredEntity))) {
            return false;
        }
        final SecuredEntity other = (SecuredEntity) obj;
        return Objects.equals(this.toString(), other.toString());
    }
}