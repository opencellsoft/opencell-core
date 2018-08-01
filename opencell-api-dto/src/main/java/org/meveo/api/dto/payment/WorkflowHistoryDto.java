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
package org.meveo.api.dto.payment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.AuditableEntityDto;
import org.meveo.model.wf.WorkflowHistory;
import org.meveo.model.wf.WorkflowHistoryAction;

/**
 * The Class WorkflowHistoryDto.
 * 
 * @author anasseh
 */
@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class WorkflowHistoryDto extends AuditableEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 8309866046667741458L;

    /** The action date. */
    private Date actionDate;

    /** The workflow code. */
    private String workflowCode;

    /** The entity instance code. */
    private String entityInstanceCode;

    /** The from status. */
    private String fromStatus;

    /** The to status. */
    private String toStatus = null;

    /** The list workflow history action dto. */
    @XmlElementWrapper(name = "actions")
    @XmlElement(name = "action")
    private List<WorkflowHistoryActionDto> listWorkflowHistoryActionDto = new ArrayList<WorkflowHistoryActionDto>();

    /**
     * Instantiates a new workflow history dto.
     */
    public WorkflowHistoryDto() {
    }

    /**
     * Instantiates a new workflow history dto.
     *
     * @param workflowHistory the workflow history entity
     */
    public WorkflowHistoryDto(WorkflowHistory workflowHistory) {
        super(workflowHistory);
        this.actionDate = workflowHistory.getActionDate();
        this.workflowCode = workflowHistory.getWorkflow() == null ? null : workflowHistory.getWorkflow().getCode();
        this.entityInstanceCode = workflowHistory.getEntityInstanceCode();
        this.fromStatus = workflowHistory.getFromStatus();
        this.toStatus = workflowHistory.getToStatus();
        for (WorkflowHistoryAction action : workflowHistory.getActionsAndReports()) {
            WorkflowHistoryActionDto wftdto = new WorkflowHistoryActionDto(action);
            listWorkflowHistoryActionDto.add(wftdto);
        }
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
     * Gets the workflow code.
     *
     * @return the workflowCode
     */
    public String getWorkflowCode() {
        return workflowCode;
    }

    /**
     * Sets the workflow code.
     *
     * @param workflowCode the workflowCode to set
     */
    public void setWorkflowCode(String workflowCode) {
        this.workflowCode = workflowCode;
    }

    /**
     * Gets the entity instance code.
     *
     * @return the entityInstanceCode
     */
    public String getEntityInstanceCode() {
        return entityInstanceCode;
    }

    /**
     * Sets the entity instance code.
     *
     * @param entityInstanceCode the entityInstanceCode to set
     */
    public void setEntityInstanceCode(String entityInstanceCode) {
        this.entityInstanceCode = entityInstanceCode;
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

    /**
     * Gets the list workflow history action dto.
     *
     * @return the listWorkflowHistoryActionDto
     */
    public List<WorkflowHistoryActionDto> getListWorkflowHistoryActionDto() {
        return listWorkflowHistoryActionDto;
    }

    /**
     * Sets the list workflow history action dto.
     *
     * @param listWorkflowHistoryActionDto the listWorkflowHistoryActionDto to set
     */
    public void setListWorkflowHistoryActionDto(List<WorkflowHistoryActionDto> listWorkflowHistoryActionDto) {
        this.listWorkflowHistoryActionDto = listWorkflowHistoryActionDto;
    }


    @Override
    public String toString() {
        return "WorkflowHistoryDto [actionDate=" + actionDate + ", workflowCode=" + workflowCode + ", entityInstanceCode=" + entityInstanceCode + ", fromStatus=" + fromStatus
                + ", toStatus=" + toStatus + ", listWorkflowHistoryActionDto=" + listWorkflowHistoryActionDto + "]";
    }

}