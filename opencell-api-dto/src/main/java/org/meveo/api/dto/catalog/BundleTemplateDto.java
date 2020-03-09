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

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.catalog.BundleTemplate;

/**
 * The Class BundleTemplateDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "BundleTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class BundleTemplateDto extends ProductTemplateDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -6581346092486998984L;

    /** The bundle product templates. */
    @XmlElementWrapper(required = true, name = "bundleProducts")
    @XmlElement(required = true, name = "bundleProduct")
    private List<BundleProductTemplateDto> bundleProductTemplates;

    /**
     * Instantiates a new bundle template dto.
     */
    public BundleTemplateDto() {
    }

    /**
     * Instantiates a new bundle template dto.
     *
     * @param bundleTemplate the bundle template
     * @param customFieldsDto the custom fields dto
     * @param asLink the as link
     */
    public BundleTemplateDto(BundleTemplate bundleTemplate, CustomFieldsDto customFieldsDto, boolean asLink) {
        super(bundleTemplate, customFieldsDto, asLink, true);
    }

    /**
     * Gets the bundle product templates.
     *
     * @return the bundle product templates
     */
    public List<BundleProductTemplateDto> getBundleProductTemplates() {
        return bundleProductTemplates;
    }

    /**
     * Sets the bundle product templates.
     *
     * @param bundleProductTemplates the new bundle product templates
     */
    public void setBundleProductTemplates(List<BundleProductTemplateDto> bundleProductTemplates) {
        this.bundleProductTemplates = bundleProductTemplates;
    }
}