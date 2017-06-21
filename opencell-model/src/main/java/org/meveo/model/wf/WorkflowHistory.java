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
package org.meveo.model.wf;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.EnableEntity;

@Entity
@Table(name = "wf_history")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {@Parameter(name = "sequence_name", value = "wf_history_seq"), })
public class WorkflowHistory extends EnableEntity {

	private static final long serialVersionUID = 1L;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "action_date")
	private Date actionDate;
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_id")   
	private Workflow workflow;
    
	@Column(name = "entity_instance_code")
	@NotNull 
    private String entityInstanceCode;

	@Column(name = "from_status")
	@NotNull    
	String fromStatus = null;
	
	@Column(name = "to_status")
	@NotNull    
	String toStatus = null;	

	@Column(name = "transition_name")
	@NotNull    
	String transitionName = null;	
	
    @OneToMany(mappedBy = "workflowHistory", fetch = FetchType.LAZY, cascade = CascadeType.ALL)    
	private List<WorkflowHistoryAction> actionsAndReports = new ArrayList<WorkflowHistoryAction>();
	
	
	public WorkflowHistory(){
		
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
