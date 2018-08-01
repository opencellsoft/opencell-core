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

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.model.wf.WFAction;

/**
 * The Class WFActionDto.
 * 
 * @author anasseh
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class WFActionDto extends BaseEntityDto {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 8309866046667741458L;

    /** The uuid. */
    @XmlElement(required = false)
    private String uuid;

    /** The action el. */
    @XmlElement(required = true)
    private String actionEl;

    /** The priority. */
    @XmlElement(required = false)
    private Integer priority;

    /** The condition el. */
    @XmlElement(required = false)
    private String conditionEl;

    /**
     * Instantiates a new WF action dto.
     */
    public WFActionDto() {
    }

    /**
     * Instantiates a new WF action dto.
     *
     * @param wfAction the WFAction entity
     */
    public WFActionDto(WFAction wfAction) {
        this.uuid = wfAction.getUuid();
        this.actionEl = wfAction.getActionEl();
        this.priority = wfAction.getPriority();
        this.conditionEl = wfAction.getConditionEl();
    }

    /**
     * From dto.
     *
     * @param wfAction the wf action
     * @return the WF action
     */
    public WFAction fromDto(WFAction wfAction) {
        if (wfAction == null) {
            wfAction = new WFAction();
        }
        wfAction.setUuid(getUuid());
        wfAction.setActionEl(getActionEl());
        wfAction.setPriority(getPriority());
        wfAction.setConditionEl(getConditionEl());
        return wfAction;
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
     * Gets the action el.
     *
     * @return the actionEl
     */
    public String getActionEl() {
        return actionEl;
    }

    /**
     * Sets the action el.
     *
     * @param actionEl the actionEl to set
     */
    public void setActionEl(String actionEl) {
        this.actionEl = actionEl;
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
     * @param priority the priority to set
     */
    public void setPriority(Integer priority) {
        this.priority = priority;
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

    @Override
    public String toString() {
        return "WFActionDto [actionEl=" + actionEl + ", priority=" + priority + ", conditionEl=" + conditionEl + "]";
    }

}
