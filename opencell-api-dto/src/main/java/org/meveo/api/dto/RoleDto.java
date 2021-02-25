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

package org.meveo.api.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.admin.SecuredEntity;
import org.meveo.model.crm.custom.CustomFieldValues;
import org.meveo.model.security.Permission;
import org.meveo.model.security.Role;

/**
 * The Class RoleDto.
 * 
 * @author anasseh
 * @author Edward P. Legaspi
 * @lastModifiedVersion 6.0
 */
@XmlRootElement(name = "Role")
@XmlAccessorType(XmlAccessType.FIELD)
public class RoleDto extends BaseEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The name. */
    @XmlAttribute(required = true)
    private String name;

    /** The description. */
    @XmlAttribute
    private String description;

    /** The permission. */
    @XmlElementWrapper(name = "permissions")
    @XmlElement(name = "permission")
    private List<PermissionDto> permission = new ArrayList<>();

    /** The roles. */
    @XmlElementWrapper(name = "roles")
    @XmlElement(name = "role")
    private List<RoleDto> roles = new ArrayList<>();
    
    /** The secured entities. */
    @XmlElementWrapper(name = "accessibleEntities")
    @XmlElement(name = "accessibleEntity")
    private List<SecuredEntityDto> securedEntities;
    
    private String uuid;

    protected CustomFieldsDto customFields;

    /**
     * Instantiates a new role dto.
     */
    public RoleDto() {

    }

    /**
     * Instantiates a new role dto.
     *
     * @param name the name
     */
    public RoleDto(String name) {
        this.name = name;
    }
    
    /**
     * Instantiates a new role dto.
     *
     * @param role the role
     * @param includeRoles the include roles
     * @param includePermissions the include permissions
     */
    public RoleDto(Role role, boolean includeRoles, boolean includePermissions) {
    	this(role, includeRoles, includePermissions, false);
    }

    /**
     * Instantiates a new role dto.
     *
     * @param role the role
     * @param includeRoles the include roles
     * @param includePermissions the include permissions
     * @param includeSecuredEntities include secured entities
     */
    public RoleDto(Role role, boolean includeRoles, boolean includePermissions, boolean includeSecuredEntities) {
        this.setName(role.getName());
        this.setDescription(role.getDescription());
        this.uuid = role.getUuid();

        Set<Permission> permissions = role.getPermissions();

        if (includePermissions && permissions != null && !permissions.isEmpty()) {
            List<PermissionDto> permissionDtos = new ArrayList<PermissionDto>();
            for (Permission p : permissions) {
                PermissionDto pd = new PermissionDto(p);
                permissionDtos.add(pd);
            }
            this.setPermission(permissionDtos);

            Collections.sort(this.permission, Comparator.comparing(PermissionDto::getName));
        }

        if (includeRoles) {
            for (Role r : role.getRoles()) {
                roles.add(new RoleDto(r, includeRoles, includePermissions));
            }
            Collections.sort(this.roles, Comparator.comparing(RoleDto::getName));
        }
        
		if (includeSecuredEntities && role.getSecuredEntities() != null) {
			this.securedEntities = new ArrayList<>();
			SecuredEntityDto securedEntityDto = null;
			for (SecuredEntity securedEntity : role.getSecuredEntities()) {
				securedEntityDto = new SecuredEntityDto(securedEntity);
				this.securedEntities.add(securedEntityDto);
			}
			Collections.sort(this.securedEntities, Comparator.comparing(SecuredEntityDto::getCode));
		}
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     *
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final int maxLen = 10;
        return String.format("RoleDto [name=%s, description=%s, permission=%s, roles=%s]", name, description,
            permission != null ? permission.subList(0, Math.min(permission.size(), maxLen)) : null, roles != null ? roles.subList(0, Math.min(roles.size(), maxLen)) : null);
    }

    /**
     * Gets the permission.
     *
     * @return the permission
     */
    public List<PermissionDto> getPermission() {
        return permission;
    }

    /**
     * Sets the permission.
     *
     * @param permission the new permission
     */
    public void setPermission(List<PermissionDto> permission) {
        this.permission = permission;
    }

    /**
     * Gets the roles.
     *
     * @return the roles
     */
    public List<RoleDto> getRoles() {
        return roles;
    }

    /**
     * Sets the roles.
     *
     * @param roles the new roles
     */
    public void setRoles(List<RoleDto> roles) {
        this.roles = roles;
    }

    /**
     * Returns a list of secured entities
     */
	public List<SecuredEntityDto> getSecuredEntities() {
		return securedEntities;
	}

	/**
	 * Gets a list of secured entities
	 */
	public void setSecuredEntities(List<SecuredEntityDto> securedEntities) {
		this.securedEntities = securedEntities;
	}

	/**
	 * @return the uuid
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * @param uuid the uuid to set
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * @return the customFields
	 */
	public CustomFieldsDto getCustomFields() {
		return customFields;
	}

	/**
	 * @param customFields the customFields to set
	 */
	public void setCustomFields(CustomFieldsDto customFields) {
		this.customFields = customFields;
	}
}