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

import org.meveo.api.dto.EnableBusinessDto;
import org.meveo.api.dto.FilterDto;
import org.meveo.model.generic.wf.GWFTransition;
import org.meveo.model.generic.wf.GenericWorkflow;
import org.meveo.model.generic.wf.WFStatus;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class GenericWorkflowDto
 *
 * @author Amine Ben Aicha
 * @author Mounir Bahije
 * @lastModifiedVersion 7.0
 */
@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class GenericWorkflowDto extends EnableBusinessDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 8309866046667741458L;

    /** The target entity class. */
    @XmlElement(required = true)
    private String targetEntityClass;

    /** The target custom entity template code. */
    private String targetCetCode;

    /** The filter. */
    @XmlElement()
    private FilterDto filter;

    /** The init status. */
    @XmlElement(required = true)
    private String initStatus;

    /** The list WF status dto. */
    @XmlElementWrapper(name = "statuses")
    @XmlElement(name = "status")
    private List<WFStatusDto> statuses = new ArrayList<>();

    /** The list WF transition dto. */
    @XmlElementWrapper(name = "transitions")
    @XmlElement(name = "transition")
    private List<GWFTransitionDto> transitions;

    /** The enable history. */
    private Boolean enableHistory = true;

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
        this.targetCetCode = genericWorkflow.getTargetCetCode();
        this.enableHistory = genericWorkflow.isEnableHistory();
        this.initStatus = genericWorkflow.getInitStatus();
        this.transitions = new ArrayList<>();

        for (WFStatus wfStatus : genericWorkflow.getStatuses()) {
            WFStatusDto wftdto = new WFStatusDto(wfStatus);
            statuses.add(wftdto);
        }
        for (GWFTransition wfTransition : genericWorkflow.getTransitions()) {
            GWFTransitionDto wftdto = new GWFTransitionDto(wfTransition);
            transitions.add(wftdto);
        }
    }

    public String getTargetEntityClass() {
        return targetEntityClass;
    }

    public void setTargetEntityClass(String targetEntityClass) {
        this.targetEntityClass = targetEntityClass;
    }

    public String getTargetCetCode() {
        return targetCetCode;
    }

    public void setTargetCetCode(String targetCetCode) {
        this.targetCetCode = targetCetCode;
    }

    public FilterDto getFilter() {
        return filter;
    }

    public void setFilter(FilterDto filter) {
        this.filter = filter;
    }

    public String getInitStatus() {
        return initStatus;
    }

    public void setInitStatus(String initStatus) {
        this.initStatus = initStatus;
    }

    public List<WFStatusDto> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<WFStatusDto> statuses) {
        this.statuses = statuses;
    }

    public List<GWFTransitionDto> getTransitions() {
        return transitions;
    }

    public void setTransitions(List<GWFTransitionDto> transitions) {
        this.transitions = transitions;
    }

    public Boolean getEnableHistory() {
        return enableHistory;
    }

    public void setEnableHistory(Boolean enableHistory) {
        this.enableHistory = enableHistory;
    }

    @Override
    public String toString() {
        return "GenericWorkflowDto [targetEntityClass=" + targetEntityClass + ", statuses=" + statuses + ", listGWFTransitionDto=" + transitions + ", enableHistory="
                + enableHistory + ", id=" + id + ", code=" + code + ", description=" + description + ", updatedCode=" + updatedCode + "]";
    }
}