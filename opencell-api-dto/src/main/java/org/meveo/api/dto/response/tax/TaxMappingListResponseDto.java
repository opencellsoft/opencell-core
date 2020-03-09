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

package org.meveo.api.dto.response.tax;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.GenericSearchResponse;
import org.meveo.api.dto.response.SearchResponse;
import org.meveo.api.dto.tax.TaxMappingDto;

/**
 * API response containing a list of Tax mapping Dto
 *
 * @author Andrius Karpavicius
 */
@XmlRootElement(name = "TaxMappingListResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class TaxMappingListResponseDto extends SearchResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * A list of Tax mapping dto
     */
    @XmlElementWrapper(name = "dtos")
    @XmlElement(name = "dto")
    private List<TaxMappingDto> dtos;

    /**
     * Constructor
     */
    public TaxMappingListResponseDto() {
        super();
    }

    /**
     * Constructor
     */
    public TaxMappingListResponseDto(GenericSearchResponse<TaxMappingDto> searchResponse) {
        super(searchResponse.getPaging());
        this.dtos = searchResponse.getSearchResults();
    }

    /**
     * @return A list of Tax mapping dto
     */
    public List<TaxMappingDto> getDtos() {
        if (dtos == null) {
            dtos = new ArrayList<>();
        }
        return dtos;
    }

    /**
     * @param dtos A list of TaxMapping Dto
     */
    public void setDtos(List<TaxMappingDto> dtos) {
        this.dtos = dtos;
    }
}