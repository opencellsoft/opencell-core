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

import org.meveo.api.dto.EnableBusinessDto;
import org.meveo.model.wf.WFTransition;
import org.meveo.model.wf.Workflow;

@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class WorkflowDto extends EnableBusinessDto {

    private static final long serialVersionUID = 8309866046667741458L;

    @XmlElement(required = true)
    private String wfType;

    private Boolean enableHistory = false;

    @XmlElementWrapper(name = "transitions")
    @XmlElement(name = "transition")
    private List<WFTransitionDto> listWFTransitionDto;

    public WorkflowDto() {
    }

    public WorkflowDto(Workflow workflow) {
        super(workflow);

        this.wfType = workflow.getWfType();
        this.enableHistory = workflow.isEnableHistory();
        this.listWFTransitionDto = new ArrayList<>(); 
        for (WFTransition wfTransition : workflow.getTransitions()) {
            WFTransitionDto wftdto = new WFTransitionDto(wfTransition);
            listWFTransitionDto.add(wftdto);
        }
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
        return "WorkflowDto [code=" + code + ", description=" + description + ", wfType=" + wfType + " enableHistory=" + enableHistory + ", listWFTransitionDto="
                + listWFTransitionDto + "]";
    }

}