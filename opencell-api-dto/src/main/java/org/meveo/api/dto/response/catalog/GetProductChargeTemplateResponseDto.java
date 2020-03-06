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

package org.meveo.api.dto.response.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.ProductChargeTemplateDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class GetProductChargeTemplateResponseDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "GetProductChargeTemplateResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetProductChargeTemplateResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 6452175086333220603L;

    /** The product charge template. */
    private ProductChargeTemplateDto productChargeTemplate;

    /**
     * Gets the product charge template.
     *
     * @return the product charge template
     */
    public ProductChargeTemplateDto getProductChargeTemplate() {
        return productChargeTemplate;
    }

    /**
     * Sets the product charge template.
     *
     * @param productChargeTemplate the new product charge template
     */
    public void setProductChargeTemplate(ProductChargeTemplateDto productChargeTemplate) {
        this.productChargeTemplate = productChargeTemplate;
    }

    @Override
    public String toString() {
        return "GetProductChargeTemplateResponseDto [getProductChargeTemplate=" + productChargeTemplate + ", toString()=" + super.toString() + "]";
    }
}