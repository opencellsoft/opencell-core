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

import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplateTypeEnum;

/**
 * The Class OneShotChargeTemplateDto.
 *
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 */
@XmlRootElement(name = "OneShotChargeTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class OneShotChargeTemplateDto extends ChargeTemplateDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4465303539660526917L;

    /** The one shot charge template type. */
    @XmlElement(required = true)
    private OneShotChargeTemplateTypeEnum oneShotChargeTemplateType;

    /** The immediate invoicing. */
    private Boolean immediateInvoicing;

    /**
     * Instantiates a new one shot charge template dto.
     */
    public OneShotChargeTemplateDto() {

    }

    /**
     * Instantiates a new one shot charge template dto.
     *
     * @param oneShotChargeTemplate the OneShotChargeTemplate entity
     * @param customFieldInstances the custom field instances
     */
    public OneShotChargeTemplateDto(OneShotChargeTemplate oneShotChargeTemplate, CustomFieldsDto customFieldInstances) {
        super(oneShotChargeTemplate, customFieldInstances);
        oneShotChargeTemplateType = oneShotChargeTemplate.getOneShotChargeTemplateType();
        immediateInvoicing = oneShotChargeTemplate.getImmediateInvoicing();
    }

    /**
     * Gets the immediate invoicing.
     *
     * @return the immediate invoicing
     */
    public Boolean getImmediateInvoicing() {
        return immediateInvoicing;
    }

    /**
     * Sets the immediate invoicing.
     *
     * @param immediateInvoicing the new immediate invoicing
     */
    public void setImmediateInvoicing(Boolean immediateInvoicing) {
        this.immediateInvoicing = immediateInvoicing;
    }

    /**
     * Gets the one shot charge template type.
     *
     * @return the one shot charge template type
     */
    public OneShotChargeTemplateTypeEnum getOneShotChargeTemplateType() {
        return oneShotChargeTemplateType;
    }

    /**
     * Sets the one shot charge template type.
     *
     * @param oneShotChargeTemplateType the new one shot charge template type
     */
    public void setOneShotChargeTemplateType(OneShotChargeTemplateTypeEnum oneShotChargeTemplateType) {
        this.oneShotChargeTemplateType = oneShotChargeTemplateType;
    }

    @Override
    public String toString() {
        return "OneShotChargeTemplateDto [" + super.toString() + ", oneShotChargeTemplateType=" + oneShotChargeTemplateType + ", immediateInvoicing=" + immediateInvoicing + "]";
    }
}