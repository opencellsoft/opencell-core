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

package org.meveo.api.dto;

import org.meveo.api.dto.catalog.UsageChargeTemplateDto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class UsageChargeTemplatesDto.
 *
 * @author akadid abdelmounaim
 */
@XmlRootElement(name = "UsageChargeTemplates")
@XmlAccessorType(XmlAccessType.FIELD)
public class UsageChargeTemplatesDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -4830516684799736318L;

    /** The invoice sub category. */
    private List<UsageChargeTemplateDto> usageChargeTemplates = new ArrayList<>();

    /**
     * Gets the invoice sub category.
     *
     * @return the invoice sub category
     */
    public List<UsageChargeTemplateDto> getUsageChargeTemplates() {
        if (usageChargeTemplates == null)
            usageChargeTemplates = new ArrayList<UsageChargeTemplateDto>();
        return usageChargeTemplates;
    }

    /**
     * Sets the invoice sub category.
     *
     * @param usageChargeTemplates the new invoice sub category
     */
    public void setUsageChargeTemplates(List<UsageChargeTemplateDto> usageChargeTemplates) {
        this.usageChargeTemplates = usageChargeTemplates;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "UsageChargeTemplates [usageChargeTemplates=" + usageChargeTemplates + "]";
    }

}
