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
package org.meveo.model.wf;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;

/**
 * Workflow action history
 * 
 * @author Andrius Karpavicius
 */
@Entity
@Table(name = "wf_history")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "wf_history_seq"), })
public class WorkflowHistory extends AuditableEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Action date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "action_date")
    private Date actionDate;

    /**
     * Worklow
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_id")
    private Workflow workflow;

    /**
     * Afected entity instance code
     */
    @Column(name = "entity_instance_code")
    @NotNull
    private String entityInstanceCode;

    /**
     * Workflow status change - from status
     */
    @Column(name = "from_status")
    @NotNull
    String fromStatus;

    /**
     * Workflow status change - to status
     */
    @Column(name = "to_status")
    @NotNull
    String toStatus;

    /**
     * Transition name
     */
    @Column(name = "transition_name")
    @NotNull
    String transitionName;

    /**
     * Executed actions and their execution report
     */
    @OneToMany(mappedBy = "workflowHistory", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<WorkflowHistoryAction> actionsAndReports = new ArrayList<>();

    public WorkflowHistory() {

    }

    /**
     * @return the actionDate
     */
    public Date getActionDate() {
        return actionDate;
    }

    /**
     * @param actionDate the actionDate to set
     */
    public void setActionDate(Date actionDate) {
        this.actionDate = actionDate;
    }

    /**
     * @return the entityInstanceCode
     */
    public String getEntityInstanceCode() {
        return entityInstanceCode;
    }

    /**
     * @param entityInstanceCode the entityInstanceCode to set
     */
    public void setEntityInstanceCode(String entityInstanceCode) {
        this.entityInstanceCode = entityInstanceCode;
    }

    /**
     * @return the fromStatus
     */
    public String getFromStatus() {
        return fromStatus;
    }

    /**
     * @param fromStatus the fromStatus to set
     */
    public void setFromStatus(String fromStatus) {
        this.fromStatus = fromStatus;
    }

    /**
     * @return the toStatus
     */
    public String getToStatus() {
        return toStatus;
    }

    /**
     * @param toStatus the toStatus to set
     */
    public void setToStatus(String toStatus) {
        this.toStatus = toStatus;
    }

    /**
     * @return the transitionName
     */
    public String getTransitionName() {
        return transitionName;
    }

    /**
     * @param transitionName the transitionName to set
     */
    public void setTransitionName(String transitionName) {
        this.transitionName = transitionName;
    }

    /**
     * @return the actionsAndReports
     */
    public List<WorkflowHistoryAction> getActionsAndReports() {
        return actionsAndReports;
    }

    /**
     * @param actionsAndReports the actionsAndReports to set
     */
    public void setActionsAndReports(List<WorkflowHistoryAction> actionsAndReports) {
        this.actionsAndReports = actionsAndReports;
    }

    /**
     * @return the workflow
     */
    public Workflow getWorkflow() {
        return workflow;
    }

    /**
     * @param workflow the workflow to set
     */
    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }

}
