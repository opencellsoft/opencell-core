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
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.meveo.api.dto.BaseDto;
import org.meveo.model.wf.WFTransition;
import org.meveo.model.wf.Workflow;
import org.meveo.model.wf.WorkflowStatusEnum;


@XmlType(name = "Workflow")
@XmlRootElement(name = "Workflow")
@XmlAccessorType(XmlAccessType.FIELD)
public class WorkflowDto extends BaseDto {

	private static final long serialVersionUID = 8309866046667741458L;
	
	@XmlElement(required=true)
	private String code;
	
	private String description;
	
	@XmlElement(required = true)	
	private String wfType;
	
	@XmlElement(required = true)
	private WorkflowStatusEnum status;
	
	private Boolean exportLot =false;
	private Boolean enableHistory =false;
	
	@XmlElementWrapper
    @XmlElement(name="wfTransitionDto")
	private List<WFTransitionDto> listWFTransitionDto = new ArrayList<WFTransitionDto>();
	
	public WorkflowDto(){
	}
	public WorkflowDto(Workflow workflow) {
		this.code=workflow.getCode();
		this.description=workflow.getDescription();		
		this.status=workflow.getStatus(); 
	    this.wfType = workflow.getWfType();
	    this.exportLot = workflow.isExportLot();
	    this.enableHistory = workflow.isEnableHistory();
	    for(WFTransition wfTransition : workflow.getTransitions()){
	    	WFTransitionDto wftdto = new WFTransitionDto(wfTransition);
	    	wftdto.setWorkflowCode(getCode());
	    	listWFTransitionDto.add(wftdto);
	    }
	}
	
	public Workflow fromDto(Workflow workflow){
		if(workflow == null)
			workflow = new Workflow();
		workflow.setCode(getCode());
		workflow.setDescription(getDescription());
		workflow.setStatus(getStatus());
		workflow.setEnableHistory(getEnableHistory());
		workflow.setExportLot(getExportLot());
		workflow.setWfType(getWfType());		
	    return workflow;
		
	}
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the wfType
	 */
	public String getWfType() {
		return wfType;
	}
	/**
	 * @param wfType the wfType to set
	 */
	public void setWfType(String wfType) {
		this.wfType = wfType;
	}
	/**
	 * @return the status
	 */
	public WorkflowStatusEnum getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(WorkflowStatusEnum status) {
		this.status = status;
	}
	/**
	 * @return the exportLot
	 */
	public Boolean getExportLot() {
		return exportLot;
	}
	/**
	 * @param exportLot the exportLot to set
	 */
	public void setExportLot(Boolean exportLot) {
		this.exportLot = exportLot;
	}
	/**
	 * @return the enableHistory
	 */
	public Boolean getEnableHistory() {
		return enableHistory;
	}
	/**
	 * @param enableHistory the enableHistory to set
	 */
	public void setEnableHistory(Boolean enableHistory) {
		this.enableHistory = enableHistory;
	}
	/**
	 * @return the listWFTransitionDto
	 */
	public List<WFTransitionDto> getListWFTransitionDto() {
		return listWFTransitionDto;
	}
	/**
	 * @param listWFTransitionDto the listWFTransitionDto to set
	 */
	public void setListWFTransitionDto(List<WFTransitionDto> listWFTransitionDto) {
		this.listWFTransitionDto = listWFTransitionDto;
	}

	@Override
	public String toString() {
		return "WorkflowDto [code=" + code + ", description=" + description + ", wfType=" + wfType + ", status=" + status + ", exportLot=" + exportLot + ", enableHistory=" + enableHistory + ", listWFTransitionDto=" + listWFTransitionDto + "]";
	}
	
}

