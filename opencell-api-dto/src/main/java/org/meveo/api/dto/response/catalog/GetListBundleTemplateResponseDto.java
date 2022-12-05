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

import org.meveo.api.dto.catalog.BundleTemplateDto;
import org.meveo.api.dto.response.SearchResponse;

/**
 * The Class GetListBundleTemplateResponseDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "GetListBundleTemplateResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetListBundleTemplateResponseDto extends SearchResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5535571034571826093L;

    /** The bundle templates. */
    @XmlElementWrapper(name = "bundleTemplates")
    @XmlElement(name = "bundleTemplate")
    private List<BundleTemplateDto> bundleTemplates;

    /**
     * Instantiates a new gets the list bundle template response dto.
     */
    public GetListBundleTemplateResponseDto() {
    }

    /**
     * Gets the bundle templates.
     *
     * @return the bundle templates
     */
    public List<BundleTemplateDto> getBundleTemplates() {
        return bundleTemplates;
    }

    /**
     * Sets the bundle templates.
     *
     * @param bundleTemplates the new bundle templates
     */
    public void setBundleTemplates(List<BundleTemplateDto> bundleTemplates) {
        this.bundleTemplates = bundleTemplates;
    }

    /**
     * Adds the bundle template.
     *
     * @param bundleTemplate the bundle template
     */
    public void addBundleTemplate(BundleTemplateDto bundleTemplate) {
        if (bundleTemplates == null) {
            bundleTemplates = new ArrayList<>();
        }
        bundleTemplates.add(bundleTemplate);
    }
}