package org.meveo.model.security;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.meveo.model.BaseEntity;
import org.meveo.model.ExportIdentifier;

@Entity
@ExportIdentifier({ "name", "provider" })
@Table(name = "ADM_ROLE")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "ADM_ROLE_SEQ")
// @org.hibernate.annotations.Cache(usage =
// org.hibernate.annotations.CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Role extends BaseEntity {

	private static final long serialVersionUID = -2309961042891712685L;

	public static final Comparator<Role> COMP_BY_ROLE_NAME = new Comparator<Role>() {
		public int compare(Role o1, Role o2) {
			int result = o1.getName().compareToIgnoreCase(o2.getName());
			if (result == 0)
				result = o1.getId().compareTo(o2.getId());
			return result;
		}
	};

	@Column(name = "ROLE_NAME", nullable = false)
	private String name;

	@Column(name = "ROLE_DESCRIPTION", nullable = false)
	private String description;

	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "ADM_ROLE_PERMISSION", joinColumns = @JoinColumn(name = "ROLE_ID"), inverseJoinColumns = @JoinColumn(name = "PERMISSION_ID"))
	private List<Permission> permissions = new ArrayList<Permission>();

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

	public List<Permission> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}

	/**
	 * Check if role as a following permision
	 * 
	 * @param resource
	 *            Resource to match
	 * @param permission
	 *            Permission/action to match
	 * @return
	 */
	public boolean hasPermission(String resource, String permission) {
		for (Permission permissionObj : getPermissions()) {
			if (permissionObj.getResource().equals(resource)
					&& permissionObj.getPermission().equals(permission)) {
				return true;
			}
		}
		return false;
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