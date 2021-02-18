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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.model.generic.wf.GWFTransition;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The Class GWFTransitionDto
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class GWFTransitionDto extends BaseEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 8309866046667741458L;

    /** The uuid. */
    @XmlElement
    private String uuid;

    /** The from status. */
    @XmlElement(required = true)
    private String fromStatus;

    /** The to status. */
    @XmlElement(required = true)
    private String toStatus;

    /** The condition el. */
    @XmlElement(required = true)
    private String conditionEl;

    /** The priority. */
    @XmlElement
    private Integer priority;

    /** The description. */
    @XmlElement(required = true)
    private String description;

    /** The ScriptInstanceDto. */
    /** The description. */
    @XmlElement
    private String actionScriptCode;

    private List<GWFActionDto> actions = new ArrayList<>();

    /**
     * Instantiates a new WF transition dto.
     */
    public GWFTransitionDto() {
    }

    /**
     * Instantiates a new GWF transition dto.
     *
     * @param gwfTransition the GWFTransition entity
     */
    public GWFTransitionDto(GWFTransition gwfTransition) {
        this.uuid = gwfTransition.getUuid();
        this.fromStatus = gwfTransition.getFromStatus();
        this.toStatus = gwfTransition.getToStatus();
        this.conditionEl = gwfTransition.getConditionEl();
        this.priority = gwfTransition.getPriority();
        this.description = gwfTransition.getDescription();

        if (gwfTransition.getActionScript() != null) {
            this.actionScriptCode = gwfTransition.getActionScript().getCode();
        }
        this.getActions().addAll(gwfTransition.getActions().stream().map(GWFActionDto::new).collect(Collectors.toList()));
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

    public String getActionScriptCode() {
        return actionScriptCode;
    }

    public void setActionScriptCode(String actionScriptCode) {
        this.actionScriptCode = actionScriptCode;
    }

    @Override
    public String toString() {
        return "GWFTransitionDto [uuid=" + uuid + ", fromStatus=" + fromStatus + ", toStatus=" + toStatus + ", conditionEl=" + conditionEl + ", priority=" + priority
                + ", description=" + description + ", actionScriptCode=" + actionScriptCode + "]";
    }

    public List<GWFActionDto> getActions() {
        return actions;
    }

    public void setActions(List<GWFActionDto> actions) {
        this.actions = actions;
    }
}
