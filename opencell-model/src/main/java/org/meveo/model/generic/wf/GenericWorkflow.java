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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.EnableBusinessCFEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ModuleItem;
import org.meveo.model.filter.Filter;

/**
 * Generic Workflow for entity data processing
 *
 * @author Amine Ben Aicha
 * @author Mounir Bahije
 * @lastModifiedVersion 7.0
 */
@Entity
@ModuleItem
@Cacheable
@ExportIdentifier({ "code" })
@CustomFieldEntity(cftCodePrefix = "GenericWorkflow")
@Table(name = "wf_generic_workflow", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "wf_generic_workflow_seq") })
@NamedQueries({ @NamedQuery(name = "GenericWorkflow.findByTargetEntityClass", query = "From GenericWorkflow where targetEntityClass=:targetEntityClass") })
public class GenericWorkflow extends EnableBusinessCFEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Qualified name of Workflowed entities
     */
    @Column(name = "target_entity_class", length = 255, nullable = false)
    private String targetEntityClass;

    /**
     * Custom entity template code
     */
    @Column(name = "target_cet_code", length = 255)
    private String targetCetCode;

    /** The filter. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "filter_id")
    private Filter filter;

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

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
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

    public String getTargetCetCode() {
        return targetCetCode;
    }

    public void setTargetCetCode(String targetCetCode) {
        this.targetCetCode = targetCetCode;
    }
}
