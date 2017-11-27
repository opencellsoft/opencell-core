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

@XmlRootElement(name = "Role")
@XmlAccessorType(XmlAccessType.FIELD)
public class RoleDto extends BaseDto {

    private static final long serialVersionUID = 1L;

    @XmlAttribute(required = true)
    private String name;

    @XmlAttribute
    private String description;

    @XmlElementWrapper(name = "permissions")
    @XmlElement(name = "permission")
    private List<PermissionDto> permission = new ArrayList<PermissionDto>();

    @XmlElementWrapper(name = "roles")
    @XmlElement(name = "role")
    private List<RoleDto> roles = new ArrayList<RoleDto>();

    public RoleDto() {

    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        final int maxLen = 10;
        return String.format("RoleDto [name=%s, description=%s, permission=%s, roles=%s]", name, description,
            permission != null ? permission.subList(0, Math.min(permission.size(), maxLen)) : null, roles != null ? roles.subList(0, Math.min(roles.size(), maxLen)) : null);
    }

    public List<PermissionDto> getPermission() {
        return permission;
    }

    public void setPermission(List<PermissionDto> permission) {
        this.permission = permission;
    }

    public List<RoleDto> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleDto> roles) {
        this.roles = roles;
    }
}