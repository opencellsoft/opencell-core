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

package org.meveo.model.security;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.IReferenceEntity;
import org.meveo.model.ReferenceIdentifierCode;
import org.meveo.model.ReferenceIdentifierDescription;
import org.meveo.model.admin.SecuredEntity;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Cacheable;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Application security role
 * 
 * @author Andrius Karpavicius
 * @author Edward P. Legaspi
 * @lastModifiedVersion 6.0
 */
@Entity
@Cacheable
@CustomFieldEntity(cftCodePrefix = "Role")
@ExportIdentifier({ "name" })
@ReferenceIdentifierCode("name")
@ReferenceIdentifierDescription("description")
@Table(name = "adm_role")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "adm_role_seq"), })
@NamedQueries({ @NamedQuery(name = "Role.getByName", query = "SELECT r FROM Role r WHERE lower(r.name)=:name", hints = { @QueryHint(name = "org.hibernate.cacheable", value = "TRUE") }), })
public class Role extends AuditableCFEntity implements IReferenceEntity {

    private static final long serialVersionUID = -2309961042891712685L;

    /**
     * Role name
     */
    @Column(name = "role_name", nullable = false, length = 255)
    @Size(max = 255)
    @NotNull
    private String name;

    /**
     * Role description
     */
    @Column(name = "role_description", nullable = false, length = 255)
    @Size(max = 255)
    private String description;

    /**
     * Is this a client role
     */
    @Transient
    private boolean clientRole;

    /**
     * Parent role
     */
    @Transient
    private Role parentRole;

    /**
     * Roles held by the role
     */
    @Transient
    private Set<Role> roles = new HashSet<>();
    
    
    /**
     * Create In KC
     */
    @Transient
    private Boolean replicateInKc=Boolean.TRUE;
	
    public Role() {
        // TODO Auto-generated constructor stub
    }

    /**
     * Constructor
     * 
     * @param name Role name
     * @param description Role description
     */
    public Role(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Constructor
     * 
     * @param name Role name
     * @param description Role description
     * @param clientRole Is this a client role
     */
    public Role(String name, String description, boolean clientRole, Role parentRole) {
        this(name, description);
        this.clientRole = clientRole;
        this.parentRole = parentRole;
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

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
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

    @Override
    public String getReferenceCode() {
        return getName();
    }

    @Override
    public void setReferenceCode(Object value) {
        setName(value.toString());
    }

    @Override
    public String getReferenceDescription() {
        return getDescription();
    }

    /**
     * @return Is this a client role
     */
    public boolean isClientRole() {
        return clientRole;
    }

    /**
     * @param clientRole Is this a client role
     */
    public void setClientRole(boolean clientRole) {
        this.clientRole = clientRole;
    }

    /**
     * @return Parent role
     */
    public Role getParentRole() {
        return parentRole;
    }

    /**
     * @param parentRole Parent role
     */
    public void setParentRole(Role parentRole) {
        this.parentRole = parentRole;
    }

	public Boolean getReplicateInKc() {
		return replicateInKc;
	}

	public void setReplicateInKc(Boolean replicateInKc) {
		this.replicateInKc = replicateInKc;
	}
}