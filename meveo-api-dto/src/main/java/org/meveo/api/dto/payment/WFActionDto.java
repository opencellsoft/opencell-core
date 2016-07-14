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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.meveo.api.dto.BaseDto;
import org.meveo.model.wf.WFAction;


@XmlType(name = "WFAction")
@XmlAccessorType(XmlAccessType.FIELD)
public class WFActionDto extends BaseDto {
	private static final long serialVersionUID = 8309866046667741458L;  
	
	@XmlElement(required = true)
	private String actionEl;
	
	@XmlElement(required = true)
	private Integer priority;
	
	@XmlElement(required = true)
	private String conditionEl;
	
	@XmlTransient
	private WFTransitionDto wfTransitionDto;
	
	public WFActionDto(){
	}
	
	public WFActionDto(WFAction wfAction) {		
		this.actionEl = wfAction.getActionEl();
		this.priority = wfAction.getPriority();
		this.conditionEl = wfAction.getConditionEl();
		this.wfTransitionDto = new WFTransitionDto();
		this.wfTransitionDto.setConditionEl(wfAction.getWfTransition().getConditionEl());
		this.wfTransitionDto.setFromStatus(wfAction.getWfTransition().getFromStatus());
		this.wfTransitionDto.setToStatus(wfAction.getWfTransition().getToStatus());
		this.wfTransitionDto.setWorkflowCode(wfAction.getWfTransition().getWorkflow().getCode());
	}
	
	public WFAction fromDto(WFAction wfAction) {
		if(wfAction == null)
			wfAction = new WFAction();
		wfAction.setActionEl(getActionEl());
		wfAction.setPriority(getPriority());
		wfAction.setConditionEl(getConditionEl());				
		return wfAction;
	}

	/**
	 * @return the actionEl
	 */
	public String getActionEl() {
		return actionEl;
	}

	/**
	 * @param actionEl the actionEl to set
	 */
	public void setActionEl(String actionEl) {
		this.actionEl = actionEl;
	}

	/**
	 * @return the priority
	 */
	public Integer getPriority() {
		return priority;
	}

	/**
	 * @param priority the priority to set
	 */
	public void setPriority(Integer priority) {
		this.priority = priority;
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
	
	public WFTransitionDto getWfTransitionDto() {
		return wfTransitionDto;
	}
	
	public void setWfTransitionDto(WFTransitionDto wfTransitionDto) {
		this.wfTransitionDto = wfTransitionDto;
	}
	
	@Override
	public String toString() {
		return "WFActionDto [actionEl=" + actionEl + ", priority=" + priority + ", conditionEl=" + conditionEl + "]";
	}


}

