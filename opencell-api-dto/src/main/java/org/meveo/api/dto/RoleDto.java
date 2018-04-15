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

import org.meveo.model.security.Permission;
import org.meveo.model.security.Role;

/**
 * The Class RoleDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "Role")
@XmlAccessorType(XmlAccessType.FIELD)
public class RoleDto extends BaseDto {

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
    private List<PermissionDto> permission = new ArrayList<PermissionDto>();

    /** The roles. */
    @XmlElementWrapper(name = "roles")
    @XmlElement(name = "role")
    private List<RoleDto> roles = new ArrayList<RoleDto>();

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
        this.setName(role.getName());
        this.setDescription(role.getDescription());

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
}