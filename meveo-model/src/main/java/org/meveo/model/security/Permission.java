package org.meveo.model.security;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.meveo.commons.utils.StringUtils;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.IEntity;

/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
@Entity
@ExportIdentifier("name")
@Table(name = "ADM_PERMISSION")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "ADM_PERMISSION_SEQ")
@NamedQueries({ @NamedQuery(name = "Permission.getPermission", query = "select p from Permission p where p.resource=:resource and p.permission=:permission") })
public class Permission implements IEntity, Serializable {
    private static final long serialVersionUID = 2884657784984355718L;

    @Id
    @GeneratedValue(generator = "ID_GENERATOR")
    @Column(name = "ID")
    @Access(AccessType.PROPERTY)
    private Long id;

    @Column(name = "RESSOURCE", nullable = false, length = 255)
    @Size(max = 255)
    @NotNull
    private String resource;

    @Column(name = "PERMISSION", nullable = false, length = 255)
    @Size(max = 255)
    @NotNull
    private String permission;

    @Column(name = "name", nullable = false, length = 255)
    @Size(max = 255)
    @NotNull
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

    @Override
    public boolean isTransient() {
        return id == null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        } else if (!(obj instanceof Permission)) { // Fails with proxed objects: getClass() != obj.getClass()){
            return false;
        }

        Permission other = (Permission) obj;

        if (getId() != null && other.getId() != null && getId() == other.getId()) {
            // return true;
        }

        return StringUtils.compare(this.getPermission(), other.getPermission()) == 0 && StringUtils.compare(this.getResource(), other.getResource()) == 0;
    }
}