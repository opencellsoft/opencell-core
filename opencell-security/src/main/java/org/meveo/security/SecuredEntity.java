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

import org.keycloak.representations.idm.RoleRepresentation;
import org.meveo.commons.utils.ReflectionUtils;

/**
 * Entity accessibility rules
 */
public class SecuredEntity implements Serializable {

    private static final long serialVersionUID = 84222776645282176L;

    /**
     * A resource name prefix to identify resources in Keycloak that specify secured entities
     */
    public static final String RESOURCE_NAME_PREFIX = "SE:";

    /**
     * A resource type to identify resources in Keycloak that specify secured entities
     */
    public static final String RESOURCE_TYPE = "SE";

    /**
     * Resource identifier in Keycloak
     */
    private String kcId;

    /**
     * Accessible entity id
     */
    private String id;

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
     * @param rule Resource name in Keycloak. Format: SE:&lt;entity class&gt;:&lt;entity id&gt;:&lt;entity code&gt;:&lt;action allowed&gt;
     * @param kcId Resource identifier in Keycloak
     */
    public SecuredEntity(String rule, String kcId) {
        this.kcId = kcId;

        String[] ruleInfo = rule.split(":");
        this.entityClass = ruleInfo[1];
        this.id = ruleInfo[2];
        this.code = ruleInfo[3];
        this.permission = ruleInfo.length > 4 ? SecuredEntityPermissionEnum.valueOf(ruleInfo[4]) : SecuredEntityPermissionEnum.READ;
    }

    /**
     * Return a corresponding Resource name in Keycloak. Format: SE:&lt;entity class&gt;:&lt;entity id&gt;:&lt;entity code&gt;:&lt;action allowed&gt;
     * 
     * @return A corresponding Resource name in Keycloak
     */
    public String getRule() {
        return RESOURCE_NAME_PREFIX + entityClass + ":" + id + ":" + code + (permission != null ? ":" + permission : "");
    }

    /**
     * @return Resource identifier in Keycloak
     */
    public String getKcId() {
        return kcId;
    }

    /**
     * @param kcId Resource identifier in Keycloak
     */
    public void setKcId(String kcId) {
        this.kcId = kcId;
    }

    /**
     * @return Accessible entity ID
     */
    public String getId() {
        return id;
    }

    /**
     * @param code Accessible entity ID
     */
    public void setId(String id) {
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
        hash = 29 * hash + Objects.hashCode(this.getRule());
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
        return Objects.equals(this.getRule(), other.getRule());
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