package org.meveo.model.security;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;
import org.meveo.model.ExportIdentifier;

@Entity
@Cacheable
@ExportIdentifier({ "name" })
@Table(name = "adm_role")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "adm_role_seq"), })
@NamedQueries({ @NamedQuery(name = "Role.getAllRoles", query = "select r from org.meveo.model.security.Role r LEFT JOIN r.permissions p", hints = {
        @QueryHint(name = "org.hibernate.cacheable", value = "true") }) })
public class Role extends BaseEntity {

    private static final long serialVersionUID = -2309961042891712685L;

    @Column(name = "role_name", nullable = false, length = 255)
    @Size(max = 255)
    @NotNull
    private String name;

    @Column(name = "role_description", nullable = false, length = 255)
    @Size(max = 255)
    @NotNull
    private String description;

    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(name = "adm_role_permission", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<Permission> permissions = new HashSet<Permission>();

    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(name = "adm_role_role", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "child_role_id"))
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
     * Check if role as a following permission.
     * 
     * @param permission Permission/action to match
     * @return true if having permission.
     */
    public boolean hasPermission(String permission) {
        for (Permission permissionObj : getPermissions()) {
            if (permissionObj.getPermission().equals(permission)) {
                return true;
            }
        }
        for (Role role : roles) {
            if (role.hasPermission(permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return description or name.
     */
    public String getDescriptionOrName() {
        if (!StringUtils.isBlank(description)) {
            return description;
        } else {
            return name;
        }
    }

    @Override
    public int hashCode() {
        return 961 + ("Role" + id).hashCode();
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
        } else if (!getId().equals(other.getId())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format("Role [name=%s]", name);
    }

    /**
     * Get all permission - the direct ones and the ones inherited via child roles.
     * 
     * @return A set of permissions
     */
    public Set<Permission> getAllPermissions() {
        Set<Permission> allPermissions = new HashSet<>();
        allPermissions.addAll(getPermissions());

        for (Role childRole : getRoles()) {
            allPermissions.addAll(childRole.getAllPermissions());
        }

        return allPermissions;
    }
}