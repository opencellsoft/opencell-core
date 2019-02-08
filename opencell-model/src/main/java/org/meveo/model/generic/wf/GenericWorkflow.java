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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.EnableBusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ModuleItem;

/**
 * Generic Workflow for entity data processing
 */
@Entity
@ModuleItem
@Cacheable
@ExportIdentifier({ "code" })
@Table(name = "wf_generic_workflow", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "wf_generic_workflow_seq") })
@NamedQueries({ @NamedQuery(name = "GenericWorkflow.findByTargetEntityClass", query = "From GenericWorkflow where targetEntityClass=:targetEntityClass") })
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
     * Defaut status for workflow instances
     */
    @Column(name = "init_status", nullable = false)
    private String initStatus;

    /**
     * A list of transitions making up worklfow
     */
    @OneToMany(mappedBy = "genericWorkflow", fetch = FetchType.EAGER, cascade = { CascadeType.REMOVE })
    @OrderBy("priority ASC")
    private List<GWFTransition> transitions = new ArrayList<>();

    /**
     * Should worklfow history be tracked
     */
    @Type(type = "numeric_boolean")
    @Column(name = "enable_history")
    private boolean enableHistory = true;

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

    public List<GWFTransition> getTransitions() {
        return transitions;
    }

    public void setTransitions(List<GWFTransition> transitions) {
        this.transitions = transitions;
    }

    public String getInitStatus() {
        return initStatus;
    }

    public void setInitStatus(String initStatus) {
        this.initStatus = initStatus;
    }
}
