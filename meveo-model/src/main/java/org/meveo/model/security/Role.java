package org.meveo.model.security;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.meveo.model.BaseEntity;
import org.meveo.model.ExportIdentifier;

@Entity
@ExportIdentifier({ "name"})
@Table(name = "ADM_ROLE")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "ADM_ROLE_SEQ")
public class Role extends BaseEntity {

    private static final long serialVersionUID = -2309961042891712685L;

    @Column(name = "ROLE_NAME", nullable = false, length = 255)
    @Size(max = 255)
    @NotNull
    private String name;

    @Column(name = "ROLE_DESCRIPTION", nullable = false, length = 255)
    @Size(max = 255)
    @NotNull
    private String description;

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(name = "ADM_ROLE_PERMISSION", joinColumns = @JoinColumn(name = "ROLE_ID"), inverseJoinColumns = @JoinColumn(name = "PERMISSION_ID"))
    private Set<Permission> permissions = new HashSet<Permission>();

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(name = "ADM_ROLE_ROLE", joinColumns = @JoinColumn(name = "ROLE_ID"), inverseJoinColumns = @JoinColumn(name = "CHILD_ROLE_ID"))
    private Set<Role> roles = new HashSet<Role>();

    public Role() {
    }

    public String getName() {
        return name;
    }

    public void setName(String val) {
        this.name = val;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    /**
     * Check if role as a following permision
     * 
     * @param resource Resource to match
     * @param permission Permission/action to match
     * @return
     */
    public boolean hasPermission(String resource, String permission) {
        for (Permission permissionObj : getPermissions()) {
            if (permissionObj.getResource().equals(resource) && permissionObj.getPermission().equals(permission)) {
                return true;
            }
        }
        for (Role role : roles) {
            if (role.hasPermission(resource, permission)){
                return true;
            }
        }
        return false;
    }
    
    public String getDescriptionOrName() {
        if (!StringUtils.isBlank(description)) {
            return description;
        } else {
            return name;
        }
    }

    @Override
    public int hashCode() {
        if (getId() == null)
            return super.hashCode();
        return getId().hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof Role)) {
            return false;
        }
        
        final Role other = (Role) obj;
        if (getId() == null) {
            return false;
        } else if (!getId().equals(other.getId()))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("Role [name=%s]", name);
    }

}