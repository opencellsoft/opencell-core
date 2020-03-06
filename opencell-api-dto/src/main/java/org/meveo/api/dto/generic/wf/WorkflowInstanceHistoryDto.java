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
package org.meveo.api.dto.generic.wf;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.model.generic.wf.WorkflowInstanceHistory;

/**
 * The Class WorkflowHistoryDto.
 * 
 * @author anasseh
 */
@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class WorkflowInstanceHistoryDto extends BaseEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 8309866046667741458L;

    /** The action date. */
    private Date actionDate;

    /** The workflow instance code. */
    private String workflowInstanceCode;

    /** The from status. */
    private String fromStatus;

    /** The to status. */
    private String toStatus;

    /** The tansition name. */
    private String transitionName;

    /**
     * Instantiates a new workflow instance history dto.
     */
    public WorkflowInstanceHistoryDto() {
    }

    /**
     * Instantiates a new workflow history dto.
     *
     * @param workflowInstanceHistory the workflow history entity
     */
    public WorkflowInstanceHistoryDto(WorkflowInstanceHistory workflowInstanceHistory) {
        super();
        this.actionDate = workflowInstanceHistory.getActionDate();
        this.workflowInstanceCode = workflowInstanceHistory.getWorkflowInstance() == null ? null : workflowInstanceHistory.getWorkflowInstance().getEntityInstanceCode();
        this.fromStatus = workflowInstanceHistory.getFromStatus();
        this.toStatus = workflowInstanceHistory.getToStatus();
        this.transitionName = workflowInstanceHistory.getTransitionName();
    }

    /**
     * Gets the action date.
     *
     * @return the actionDate
     */
    public Date getActionDate() {
        return actionDate;
    }

    /**
     * Sets the action date.
     *
     * @param actionDate the actionDate to set
     */
    public void setActionDate(Date actionDate) {
        this.actionDate = actionDate;
    }

    /**
     * Gets the from status.
     *
     * @return the fromStatus
     */
    public String getFromStatus() {
        return fromStatus;
    }

    /**
     * Sets the from status.
     *
     * @param fromStatus the fromStatus to set
     */
    public void setFromStatus(String fromStatus) {
        this.fromStatus = fromStatus;
    }

    /**
     * Gets the to status.
     *
     * @return the toStatus
     */
    public String getToStatus() {
        return toStatus;
    }

    /**
     * Sets the to status.
     *
     * @param toStatus the toStatus to set
     */
    public void setToStatus(String toStatus) {
        this.toStatus = toStatus;
    }

    public String getWorkflowInstanceCode() {
        return workflowInstanceCode;
    }

    public void setWorkflowInstanceCode(String workflowInstanceCode) {
        this.workflowInstanceCode = workflowInstanceCode;
    }

    public String getTransitionName() {
        return transitionName;
    }

    public void setTransitionName(String transitionName) {
        this.transitionName = transitionName;
    }

    @Override
    public String toString() {
        return "WorkflowInstanceHistoryDto [actionDate=" + actionDate + ", workflowInstanceCode=" + workflowInstanceCode + ", fromStatus=" + fromStatus + ", toStatus=" + toStatus
                + ", transitionName=" + transitionName + "]";
    }
}