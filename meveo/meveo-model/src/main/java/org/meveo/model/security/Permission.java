<<<<<<< HEAD
package org.meveo.model.security;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.meveo.model.BaseEntity;
/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
 *
 * Licensed under the GNU Public Licence, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@Entity
@Table(name = "ADM_PERMISSION")
public class Permission {

    @Id
    private Long id;

    @Column(name = "RESOURCE", nullable = false)
    private String resource;

    @Column(name = "PERMISSION", nullable = false)
    private String permission;

    @Column(name = "name", nullable = false)
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Permission [name=" + name + ", resource=" + resource + ", permission=" + permission + "]";
    }
}
=======
/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
 *
 * Licensed under the GNU Public Licence, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.meveo.model.security;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.meveo.model.BaseEntity;

/**
 * @author Ignas Lelys
 * @created Dec 3, 2010
 * 
 */
@Entity
@Table(name = "ADM_ROLE_PERMISSION")
//@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "ADM_ROLE_PERMISSION_SEQ")
@Cacheable
public class Permission extends BaseEntity {

	private static final long serialVersionUID = 1L;

	// TODO: Update to seam3 security.
	// @PermissionUser
	// @PermissionRole
	@Column(name = "ROLE", nullable = false)
	private String role;

	// TODO: Update to seam3 security.
	// @PermissionTarget
	@Column(name = "TARGET", nullable = false)
	private String target;

	// TODO: Update to seam3 security.
	// @PermissionAction
	@Column(name = "ACTION", nullable = false, length = 1500)
	private String action;

	// TODO: Update to seam3 security.
	// @PermissionDiscriminator
	@Column(name = "DISCRIMINATOR", nullable = false)
	private String discriminator;

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getDiscriminator() {
		return discriminator;
	}

	public void setDiscriminator(String discriminator) {
		this.discriminator = discriminator;
	}
}
>>>>>>> branch 'edward-javaee6' of https://git.assembla.com/meveo.git
