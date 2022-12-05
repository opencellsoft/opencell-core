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

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.OfferTemplateDto;
import org.meveo.api.dto.response.SearchResponse;

/**
 * The Class GetListOfferTemplateResponseDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "GetListOfferTemplateResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetListOfferTemplateResponseDto extends SearchResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5535571034571826093L;

    /** The offer templates. */
    @XmlElementWrapper(name = "offerTemplates")
    @XmlElement(name = "offerTemplate")
    private List<OfferTemplateDto> offerTemplates;

    /**
     * Instantiates a new gets the list offer template response dto.
     */
    public GetListOfferTemplateResponseDto() {

    }

    /**
     * Gets the offer templates.
     *
     * @return the offer templates
     */
    public List<OfferTemplateDto> getOfferTemplates() {
        return offerTemplates;
    }

    /**
     * Sets the offer templates.
     *
     * @param offerTemplates the new offer templates
     */
    public void setOfferTemplates(List<OfferTemplateDto> offerTemplates) {
        this.offerTemplates = offerTemplates;
    }

    /**
     * Adds the offer template.
     *
     * @param offerTemplate the offer template
     */
    public void addOfferTemplate(OfferTemplateDto offerTemplate) {
        if (offerTemplates == null) {
            offerTemplates = new ArrayList<>();
        }
        offerTemplates.add(offerTemplate);
    }
}