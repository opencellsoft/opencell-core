/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.scripts;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import org.meveo.model.ExportIdentifier;
import org.meveo.model.security.Role;

@Entity
@ExportIdentifier({ "code", "provider" })
@DiscriminatorValue("ScriptInstance")
public class ScriptInstance extends CustomScript {

    private static final long serialVersionUID = -7691357496569390167L;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "ADM_SCRIPT_EXEC_ROLE", joinColumns = @JoinColumn(name = "SCRIPT_INSTANCE_ID"), inverseJoinColumns = @JoinColumn(name = "ROLE_ID"))
    private Set<Role> executionRoles = new HashSet<Role>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "ADM_SCRIPT_SOURC_ROLE", joinColumns = @JoinColumn(name = "SCRIPT_INSTANCE_ID"), inverseJoinColumns = @JoinColumn(name = "ROLE_ID"))
    private Set<Role> sourcingRoles = new HashSet<Role>();

    public ScriptInstance() {

    }

    /**
     * @return the executionRoles
     */
    public Set<Role> getExecutionRoles() {
        return executionRoles;
    }

    /**
     * @param executionRoles the executionRoles to set
     */
    public void setExecutionRoles(Set<Role> executionRoles) {
        this.executionRoles = executionRoles;
    }

    /**
     * @return the sourcingRoles
     */
    public Set<Role> getSourcingRoles() {
        return sourcingRoles;
    }

    /**
     * @param sourcingRoles the sourcingRoles to set
     */
    public void setSourcingRoles(Set<Role> sourcingRoles) {
        this.sourcingRoles = sourcingRoles;
    }
}