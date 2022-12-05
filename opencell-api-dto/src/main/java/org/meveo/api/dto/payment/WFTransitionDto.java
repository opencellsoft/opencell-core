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
package org.meveo.api.dto.payment;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.model.wf.WFAction;
import org.meveo.model.wf.WFDecisionRule;
import org.meveo.model.wf.WFTransition;

/**
 * The Class WFTransitionDto.
 * 
 * @author anasseh
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class WFTransitionDto extends BaseEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 8309866046667741458L;

    /** The uuid. */
    @XmlElement(required = false)
    private String uuid;

    /** The from status. */
    @XmlElement(required = true)
    private String fromStatus;

    /** The to status. */
    @XmlElement(required = true)
    private String toStatus;

    /** The condition el. */
    @XmlElement(required = false)
    private String conditionEl;

    /** The priority. */
    @XmlElement(required = false)
    private Integer priority;

    /** The description. */
    @XmlElement(required = true)
    private String description;

    /** The list WF action dto. */
    @XmlElementWrapper(name = "actions")
    @XmlElement(name = "action")
    private List<WFActionDto> listWFActionDto = new ArrayList<>();

    /** The list WF decision rule dto. */
    @XmlElementWrapper(name = "decisionRules")
    @XmlElement(name = "decisionRule")
    private List<WFDecisionRuleDto> listWFDecisionRuleDto = new ArrayList<>();

    /**
     * Instantiates a new WF transition dto.
     */
    public WFTransitionDto() {
    }

    /**
     * Instantiates a new WF transition dto.
     *
     * @param wfTransition the WFTransition entity
     */
    public WFTransitionDto(WFTransition wfTransition) {
        this.uuid = wfTransition.getUuid();
        this.fromStatus = wfTransition.getFromStatus();
        this.toStatus = wfTransition.getToStatus();
        this.conditionEl = wfTransition.getConditionEl();
        this.priority = wfTransition.getPriority();
        this.description = wfTransition.getDescription();
        for (WFAction wfAction : wfTransition.getWfActions()) {
            WFActionDto wfadto = new WFActionDto(wfAction);
            listWFActionDto.add(wfadto);
        }

        for (WFDecisionRule wfDecisionRule : wfTransition.getWfDecisionRules()) {
            WFDecisionRuleDto wfDecisionRuleDto = new WFDecisionRuleDto(wfDecisionRule);
            listWFDecisionRuleDto.add(wfDecisionRuleDto);
        }
    }

    /**
     * From dto.
     *
     * @param wfTransition the wf transition
     * @return the WF transition
     */
    public WFTransition fromDto(WFTransition wfTransition) {
        if (wfTransition == null)
            wfTransition = new WFTransition();
        wfTransition.setUuid(getUuid());
        wfTransition.setFromStatus(getFromStatus());
        wfTransition.setToStatus(getToStatus());
        wfTransition.setConditionEl(getConditionEl());
        wfTransition.setPriority(getPriority());
        wfTransition.setDescription(getDescription());
        return wfTransition;
    }

    /**
     * Gets the uuid.
     *
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Sets the uuid.
     *
     * @param uuid the uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
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
     * Gets the condition el.
     *
     * @return the conditionEl
     */
    public String getConditionEl() {
        return conditionEl;
    }

    /**
     * Sets the condition el.
     *
     * @param conditionEl the conditionEl to set
     */
    public void setConditionEl(String conditionEl) {
        this.conditionEl = conditionEl;
    }

    /**
     * Gets the priority.
     *
     * @return the priority
     */
    public Integer getPriority() {
        return priority;
    }

    /**
     * Sets the priority.
     *
     * @param priority the new priority
     */
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     *
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the list WF action dto.
     *
     * @return the listWFActionDto
     */
    public List<WFActionDto> getListWFActionDto() {
        return listWFActionDto;
    }

    /**
     * Gets the list WF decision rule dto.
     *
     * @return the list WF decision rule dto
     */
    public List<WFDecisionRuleDto> getListWFDecisionRuleDto() {
        return listWFDecisionRuleDto;
    }

    /**
     * Sets the list WF decision rule dto.
     *
     * @param listWFDecisionRuleDto the new list WF decision rule dto
     */
    public void setListWFDecisionRuleDto(List<WFDecisionRuleDto> listWFDecisionRuleDto) {
        this.listWFDecisionRuleDto = listWFDecisionRuleDto;
    }

    /**
     * Sets the list WF action dto.
     *
     * @param listWFActionDto the listWFActionDto to set
     */
    public void setListWFActionDto(List<WFActionDto> listWFActionDto) {
        this.listWFActionDto = listWFActionDto;
    }

    @Override
    public String toString() {
        return "WFTransitionDto [fromStatus=" + fromStatus + ", toStatus=" + toStatus + ", conditionEl=" + conditionEl + ", listWFActionDto="
                + (listWFActionDto == null ? null : listWFActionDto) + "]";
    }

}
