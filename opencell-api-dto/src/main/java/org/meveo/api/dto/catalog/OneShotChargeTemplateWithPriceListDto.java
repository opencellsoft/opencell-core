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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class OneShotChargeTemplateWithPriceListDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "OneShotChargeTemplateList")
@XmlAccessorType(XmlAccessType.FIELD)
public class OneShotChargeTemplateWithPriceListDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -8879818156156191005L;

    /** The one shot charge template dtos. */
    private List<OneShotChargeTemplateWithPriceDto> oneShotChargeTemplateDtos = new ArrayList<OneShotChargeTemplateWithPriceDto>();

    /**
     * Gets the one shot charge template dtos.
     *
     * @return the one shot charge template dtos
     */
    public List<OneShotChargeTemplateWithPriceDto> getOneShotChargeTemplateDtos() {
        return oneShotChargeTemplateDtos;
    }

    /**
     * Sets the one shot charge template dtos.
     *
     * @param oneShotChargeTemplateDtos the new one shot charge template dtos
     */
    public void setOneShotChargeTemplateDtos(List<OneShotChargeTemplateWithPriceDto> oneShotChargeTemplateDtos) {
        this.oneShotChargeTemplateDtos = oneShotChargeTemplateDtos;
    }
}