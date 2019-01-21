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
package org.meveo.model.generic.wf;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.EnableBusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ModuleItem;
import org.meveo.model.scripts.ScriptInstance;

/**
 * Generic Workflow for entity data processing
 */
@Entity
@ModuleItem
@Cacheable
@ExportIdentifier({ "code" })
@Table(name = "wf_generic_workflow", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "wf_generic_workflow_seq"), })
public class GenericWorkflow extends EnableBusinessEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "target_entity_class", nullable = false)
    private String targetEntityClass;

    /**
     * A list of workflow instances
     */
    @OneToMany(mappedBy = "genericWorkflow", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkflowInstance> wfInstances = new ArrayList<>();

    /**
     * A list of workflow statuses
     */
    @OneToMany(mappedBy = "genericWorkflow", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WFStatus> statuses = new ArrayList<>();

    /**
     * Should worklfow history be tracked
     */
    @Type(type = "numeric_boolean")
    @Column(name = "enable_history")
    private boolean enableHistory;

    /**
     * The transition script
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "transition_script_id")
    private ScriptInstance transitionScript;

    public String getTargetEntityClass() {
        return targetEntityClass;
    }

    public void setTargetEntityClass(String targetEntityClass) {
        this.targetEntityClass = targetEntityClass;
    }

    public List<WorkflowInstance> getWfInstances() {
        return wfInstances;
    }

    public void setWfInstances(List<WorkflowInstance> wfInstances) {
        this.wfInstances = wfInstances;
    }

    public List<WFStatus> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<WFStatus> statuses) {
        this.statuses = statuses;
    }

    public boolean isEnableHistory() {
        return enableHistory;
    }

    public void setEnableHistory(boolean enableHistory) {
        this.enableHistory = enableHistory;
    }

    public ScriptInstance getTransitionScript() {
        return transitionScript;
    }

    public void setTransitionScript(ScriptInstance transitionScript) {
        this.transitionScript = transitionScript;
    }
}
