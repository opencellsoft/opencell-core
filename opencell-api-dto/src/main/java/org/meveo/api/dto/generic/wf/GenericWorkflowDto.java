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
package org.meveo.api.dto.generic.wf;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.EnableBusinessDto;
import org.meveo.model.generic.wf.GWFTransition;
import org.meveo.model.generic.wf.GenericWorkflow;
import org.meveo.model.generic.wf.WFStatus;

/**
 * The Class GenericWorkflowDto
 */
@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class GenericWorkflowDto extends EnableBusinessDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 8309866046667741458L;

    /** The target entity class. */
    @XmlElement(required = true)
    private String targetEntityClass;

    /** The list WF status dto. */
    @XmlElementWrapper(name = "statuses")
    @XmlElement(name = "status")
    private List<WFStatusDto> listWFStatusDto = new ArrayList<>();

    /** The list WF transition dto. */
    @XmlElementWrapper(name = "transitions")
    @XmlElement(name = "transition")
    private List<GWFTransitionDto> listGWFTransitionDto;

    /** The enable history. */
    private Boolean enableHistory = false;

    /**
     * Instantiates a new generic workflow dto.
     */
    public GenericWorkflowDto() {
    }

    /**
     * Instantiates a new generic workflow dto.
     *
     * @param genericWorkflow the generic workflow entity
     */
    public GenericWorkflowDto(GenericWorkflow genericWorkflow) {
        super(genericWorkflow);

        this.targetEntityClass = genericWorkflow.getTargetEntityClass();
        this.enableHistory = genericWorkflow.isEnableHistory();
        this.listGWFTransitionDto = new ArrayList<>();

        for (WFStatus wfStatus : genericWorkflow.getStatuses()) {
            WFStatusDto wftdto = new WFStatusDto(wfStatus);
            listWFStatusDto.add(wftdto);
        }
        for (GWFTransition wfTransition : genericWorkflow.getTransitions()) {
            GWFTransitionDto wftdto = new GWFTransitionDto(wfTransition);
            listGWFTransitionDto.add(wftdto);
        }
    }

    public String getTargetEntityClass() {
        return targetEntityClass;
    }

    public void setTargetEntityClass(String targetEntityClass) {
        this.targetEntityClass = targetEntityClass;
    }

    public List<WFStatusDto> getListWFStatusDto() {
        return listWFStatusDto;
    }

    public void setListWFStatusDto(List<WFStatusDto> listWFStatusDto) {
        this.listWFStatusDto = listWFStatusDto;
    }

    public List<GWFTransitionDto> getListGWFTransitionDto() {
        return listGWFTransitionDto;
    }

    public void setListGWFTransitionDto(List<GWFTransitionDto> listGWFTransitionDto) {
        this.listGWFTransitionDto = listGWFTransitionDto;
    }

    public Boolean getEnableHistory() {
        return enableHistory;
    }

    public void setEnableHistory(Boolean enableHistory) {
        this.enableHistory = enableHistory;
    }

    @Override
    public String toString() {
        return "GenericWorkflowDto [targetEntityClass=" + targetEntityClass + ", statuses=" + listWFStatusDto + ", listGWFTransitionDto=" + listGWFTransitionDto
                + ", enableHistory=" + enableHistory + ", id=" + id + ", code=" + code + ", description=" + description + ", updatedCode=" + updatedCode + "]";
    }
}