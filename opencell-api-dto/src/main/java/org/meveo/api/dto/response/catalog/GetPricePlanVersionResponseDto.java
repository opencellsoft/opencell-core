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

import org.meveo.api.dto.catalog.PricePlanMatrixVersionDto;
import org.meveo.api.dto.response.BaseResponse;
import org.meveo.model.catalog.PricePlanMatrixVersion;

/**
 * The Class GetPricePlanResponseDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "GetPricePlanVersionResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetPricePlanVersionResponseDto extends BaseResponse {


    /** The price plan. */
    private PricePlanMatrixVersionDto pricePlanVersion;

    public GetPricePlanVersionResponseDto(){}

    public GetPricePlanVersionResponseDto(PricePlanMatrixVersion pricePlanMatrixVersion) {
        this.pricePlanVersion =  new PricePlanMatrixVersionDto(pricePlanMatrixVersion);
    }

    public GetPricePlanVersionResponseDto(PricePlanMatrixVersionDto pricePlanMatrixVersion) {
        this.pricePlanVersion = pricePlanMatrixVersion;
    }

    public PricePlanMatrixVersionDto getPricePlanVersion() {
        return pricePlanVersion;
    }

    public void setPricePlanVersion(PricePlanMatrixVersionDto pricePlanVersion) {
        this.pricePlanVersion = pricePlanVersion;
    }

    @Override
    public String toString() {
        return "GetPricePlanVersionResponse [pricePlanVersion=" + pricePlanVersion + ", toString()=" + super.toString() + "]";
    }
}