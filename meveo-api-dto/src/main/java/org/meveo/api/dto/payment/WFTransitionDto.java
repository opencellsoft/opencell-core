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
import javax.xml.bind.annotation.XmlType;

import org.meveo.api.dto.BaseDto;
import org.meveo.model.wf.WFAction;
import org.meveo.model.wf.WFTransition;


@XmlType(name = "WFTransition")
@XmlAccessorType(XmlAccessType.FIELD)
public class WFTransitionDto extends BaseDto {
	
	private static final long serialVersionUID = 8309866046667741458L; 
	
	@XmlElement(required = true)
	private String fromStatus;
	
	@XmlElement(required = true)
	private String toStatus;
	
	@XmlElement(required = true)
	private String conditionEl ;
	
	@XmlElement(required = true)
	private String workflowCode;
	
	@XmlElementWrapper
    @XmlElement(name="wfActionDto")
	private List<WFActionDto>  listWFActionDto = new ArrayList<WFActionDto>();
	
	
	public WFTransitionDto(){
	}
		
	public WFTransitionDto(WFTransition wfTransition) { 		
		this.fromStatus = wfTransition.getFromStatus();
		this.toStatus = wfTransition.getToStatus();
		this.conditionEl = wfTransition.getConditionEl();
		this.workflowCode = wfTransition.getWorkflow().getCode();
		for(WFAction wfAction : wfTransition.getWfActions() ){
			WFActionDto wfadto = new WFActionDto(wfAction);
			wfadto.setWfTransitionDto(this);
			listWFActionDto.add(wfadto);
		}
	}
	
	public WFTransition fromDto(WFTransition wfTransition){
		if(wfTransition == null)
			wfTransition = new WFTransition();
		wfTransition.setFromStatus(getFromStatus());
		wfTransition.setToStatus(getToStatus());
		wfTransition.setConditionEl(getConditionEl());		
		return wfTransition;
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
	 * @return the conditionEl
	 */
	public String getConditionEl() {
		return conditionEl;
	}

	/**
	 * @param conditionEl the conditionEl to set
	 */
	public void setConditionEl(String conditionEl) {
		this.conditionEl = conditionEl;
	}

	


	/**
	 * @return the workflowDto
	 */
	public String getWorkflowCode() {
		return workflowCode;
	}

	/**
	 * @param workflowDto the workflowDto to set
	 */
	public void setWorkflowCode(String workflowCode) {
		this.workflowCode = workflowCode;
	}

	/**
	 * @return the listWFActionDto
	 */
	public List<WFActionDto> getListWFActionDto() {
		return listWFActionDto;
	}

	/**
	 * @param listWFActionDto the listWFActionDto to set
	 */
	public void setListWFActionDto(List<WFActionDto> listWFActionDto) {
		this.listWFActionDto = listWFActionDto;
	}


	@Override
	public String toString() {
		return "WFTransitionDto [fromStatus=" + fromStatus + ", toStatus=" + toStatus + ", conditionEl=" + conditionEl + ", listWFActionDto=" + (listWFActionDto == null ? null : listWFActionDto) + "]";
	}

	
	
}

