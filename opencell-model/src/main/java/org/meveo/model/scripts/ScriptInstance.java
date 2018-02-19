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
package org.meveo.model.scripts;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.ModuleItem;
import org.meveo.model.security.Role;

@Entity
@ModuleItem
@Cacheable
@Table(name = "meveo_script_instance", uniqueConstraints = @UniqueConstraint(columnNames = { "code"}))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {@Parameter(name = "sequence_name", value = "meveo_script_instance_seq"), })
@NamedQueries({ @NamedQuery(name = "CustomScript.countScriptInstanceOnError", query = "select count (*) from ScriptInstance o where o.error=:isError "),
    @NamedQuery(name = "CustomScript.getScriptInstanceOnError", query = "from ScriptInstance o where o.error=:isError "),
    @NamedQuery(name = "CustomScript.getScriptInstanceByTypeActive", query = "from ScriptInstance o where o.sourceTypeEnum=:sourceTypeEnum and o.disabled = false")})
public class ScriptInstance extends CustomScript {

    private static final long serialVersionUID = -7691357496569390167L;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "adm_script_exec_role", joinColumns = @JoinColumn(name = "script_instance_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> executionRoles = new HashSet<Role>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "adm_script_sourc_role", joinColumns = @JoinColumn(name = "script_instance_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
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