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

package org.meveo.api.dto.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.model.catalog.TriggeredEDRTemplate;

/**
 * The Class TriggeredEdrTemplateDto.
 *
 * @author Edward P. Legaspi
 * @lastModifiedVersion 6.0
 */
@XmlRootElement(name = "TriggeredEdrTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class TriggeredEdrTemplateDto extends BusinessEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5790679004639676207L;

    /** The subscription el. */
    private String subscriptionEl;

    /** The meveo instance code. */
    private String meveoInstanceCode;

    /** The condition el. */
    private String conditionEl;

    /** The quantity el. */
    @XmlElement(required = true)
    private String quantityEl;

    /** The param 1 el. */
    private String param1El;

    /** The param 2 el. */
    private String param2El;

    /** The param 3 el. */
    private String param3El;

    /** The param 4 el. */
    private String param4El;

    /**
     * Expression to compute the OpencellInstance code so the instance on which the EDR is triggered can be inferred from the Offer or whatever.
     * It overrides the value on meveoInstance.
     */
    private String opencellInstanceEL;
    
    /**
     * Script to run
     */
    private String triggeredEdrScript;

    /**
     * Instantiates a new triggered edr template dto.
     */
    public TriggeredEdrTemplateDto() {

    }

    /**
     * Instantiates a new triggered edr template dto.
     *
     * @param triggeredEDRTemplate the TriggeredEDRTemplate entity
     */
    public TriggeredEdrTemplateDto(TriggeredEDRTemplate triggeredEDRTemplate) {
        super(triggeredEDRTemplate);

        subscriptionEl = triggeredEDRTemplate.getSubscriptionEl();
        meveoInstanceCode = triggeredEDRTemplate.getMeveoInstance() == null ? null : triggeredEDRTemplate.getMeveoInstance().getCode();
        conditionEl = triggeredEDRTemplate.getConditionEl();
        quantityEl = triggeredEDRTemplate.getQuantityEl();
        param1El = triggeredEDRTemplate.getParam1El();
        param2El = triggeredEDRTemplate.getParam2El();
        param3El = triggeredEDRTemplate.getParam3El();
        param4El = triggeredEDRTemplate.getParam4El();
        opencellInstanceEL = triggeredEDRTemplate.getOpencellInstanceEL();
        if (triggeredEDRTemplate.getTriggeredEdrScript() != null) {
            triggeredEdrScript = triggeredEDRTemplate.getTriggeredEdrScript().getCode();
        }
    }

    /**
     * @return Expression to evaluate subscription code
     */
    public String getSubscriptionEl() {
        return subscriptionEl;
    }

    /**
     * @param subscriptionEl Expression to evaluate subscription code
     */
    public void setSubscriptionEl(String subscriptionEl) {
        this.subscriptionEl = subscriptionEl;
    }

    /**
     * @return Meveo instance code to register a new EDR on. If not empty, EDR will be send via API
     */
    public String getMeveoInstanceCode() {
        return meveoInstanceCode;
    }

    /**
     * @param meveoInstanceCode Meveo instance to register a new EDR on. If not empty, EDR will be send via API
     */
    public void setMeveoInstanceCode(String meveoInstanceCode) {
        this.meveoInstanceCode = meveoInstanceCode;
    }

    /**
     * @return Expression to determine if EDR applies
     */
    public String getConditionEl() {
        return conditionEl;
    }

    /**
     * @param conditionEl Expression to determine if EDR applies
     */
    public void setConditionEl(String conditionEl) {
        this.conditionEl = conditionEl;
    }

    /**
     * @return Expression to determine the quantity
     */
    public String getQuantityEl() {
        return quantityEl;
    }

    /**
     * @param quantityEl Expression to determine the quantity
     */
    public void setQuantityEl(String quantityEl) {
        this.quantityEl = quantityEl;
    }

    /**
     * @return Expression to determine parameter 1 value
     */
    public String getParam1El() {
        return param1El;
    }

    /**
     * @param param1El Expression to determine parameter 1 value
     */
    public void setParam1El(String param1El) {
        this.param1El = param1El;
    }
    
    /**
     * @return Expression to determine parameter 2 value
     */
    public String getParam2El() {
        return param2El;
    }

    /**
     * @param param2El Expression to determine parameter 2 value
     */
    public void setParam2El(String param2El) {
        this.param2El = param2El;
    }

    /**
     * @return Expression to determine parameter 3 value
     */
    public String getParam3El() {
        return param3El;
    }

    /**
     * @param param3El Expression to determine parameter 3 value
     */
    public void setParam3El(String param3El) {
        this.param3El = param3El;
    }

    /**
     * @return Expression to determine parameter 4 value
     */
    public String getParam4El() {
        return param4El;
    }

    /**
     * @param param4El Expression to determine parameter 4 value
     */
    public void setParam4El(String param4El) {
        this.param4El = param4El;
    }

    @Override
    public String toString() {
        return "TriggeredEdrTemplateDto [subscriptionEl=" + subscriptionEl + ", meveoInstanceCode=" + meveoInstanceCode
                + ", conditionEl=" + conditionEl + ", quantityEl=" + quantityEl + ", param1El="
                + param1El + ", param2El=" + param2El + ", param3El=" + param3El + ", param4El=" + param4El + ", id=" + id + ", code=" + code + ", description=" + description
                + ", updatedCode=" + updatedCode + "]";
    }

    public String getOpencellInstanceEL() {
        return opencellInstanceEL;
    }

    public void setOpencellInstanceEL(String opencellInstanceEL) {
        this.opencellInstanceEL = opencellInstanceEL;
    }

    public String getTriggeredEdrScript() {
        return triggeredEdrScript;
    }

    public void setTriggeredEdrScript(String triggeredEdrScript) {
        this.triggeredEdrScript = triggeredEdrScript;
    }
}