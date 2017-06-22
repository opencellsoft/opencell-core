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

import org.meveo.api.dto.BaseDto;
import org.meveo.model.wf.WorkflowHistory;
import org.meveo.model.wf.WorkflowHistoryAction;

@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class WorkflowHistoryDto extends BaseDto {

	private static final long serialVersionUID = 8309866046667741458L;
	
	
	private Date actionDate;
	
	private String workflowCode;
	
	private String entityInstanceCode;
	
	private String fromStatus;
	
	private String toStatus = null;
	
	
	@XmlElementWrapper(name="actions")
    @XmlElement(name="action")
	private List<WorkflowHistoryActionDto> listWorkflowHistoryActionDto = new ArrayList<WorkflowHistoryActionDto>();
	
	public WorkflowHistoryDto(){
	}
	public WorkflowHistoryDto(WorkflowHistory workflowHistory) {
		this.actionDate=workflowHistory.getActionDate();
		this.workflowCode= workflowHistory.getWorkflow() == null ? null : workflowHistory.getWorkflow().getCode();
	    this.entityInstanceCode = workflowHistory.getEntityInstanceCode();	   
	    this.fromStatus = workflowHistory.getFromStatus();
	    this.toStatus = workflowHistory.getToStatus();
	    for(WorkflowHistoryAction action : workflowHistory.getActionsAndReports()){
	    	WorkflowHistoryActionDto wftdto = new WorkflowHistoryActionDto(action);
	    	listWorkflowHistoryActionDto.add(wftdto);
	    }
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
	 * @return the workflowCode
	 */
	public String getWorkflowCode() {
		return workflowCode;
	}
	/**
	 * @param workflowCode the workflowCode to set
	 */
	public void setWorkflowCode(String workflowCode) {
		this.workflowCode = workflowCode;
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
	 * @return the listWorkflowHistoryActionDto
	 */
	public List<WorkflowHistoryActionDto> getListWorkflowHistoryActionDto() {
		return listWorkflowHistoryActionDto;
	}
	/**
	 * @param listWorkflowHistoryActionDto the listWorkflowHistoryActionDto to set
	 */
	public void setListWorkflowHistoryActionDto(List<WorkflowHistoryActionDto> listWorkflowHistoryActionDto) {
		this.listWorkflowHistoryActionDto = listWorkflowHistoryActionDto;
	}
 
	@Override
	public String toString() {
		return "WorkflowHistoryDto [actionDate=" + actionDate + ", workflowCode=" + workflowCode + ", entityInstanceCode=" + entityInstanceCode + ", fromStatus=" + fromStatus + ", toStatus=" + toStatus + ", listWorkflowHistoryActionDto=" + listWorkflowHistoryActionDto + "]";
	}

	
	
}