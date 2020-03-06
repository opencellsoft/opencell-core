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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.model.wf.WFDecisionRule;

/**
 * The Class WFDecisionRuleDto.
 * 
 * @author anasseh
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class WFDecisionRuleDto extends BaseEntityDto {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 8309866046667741458L;

    /** The name. */
    @XmlElement(required = true)
    private String name;

    /** The value. */
    @XmlElement(required = true)
    private String value;

    /**
     * Instantiates a new WF decision rule dto.
     */
    public WFDecisionRuleDto() {
    }

    /**
     * Instantiates a new WF decision rule dto.
     *
     * @param wfDecisionRule the wf decision rule
     */
    public WFDecisionRuleDto(WFDecisionRule wfDecisionRule) {
        this.name = wfDecisionRule.getName();
        this.value = wfDecisionRule.getValue();
    }

    /**
     * From dto.
     *
     * @param wfDecisionRule the wf decision rule
     * @return the WF decision rule
     */
    public WFDecisionRule fromDto(WFDecisionRule wfDecisionRule) {
        if (wfDecisionRule == null)
            wfDecisionRule = new WFDecisionRule();
        wfDecisionRule.setName(getName());
        wfDecisionRule.setValue(getValue());
        return wfDecisionRule;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value.
     *
     * @param value the new value
     */
    public void setValue(String value) {
        this.value = value;
    }

}
