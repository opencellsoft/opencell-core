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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * The Class OfferProductTemplateDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "OfferProductTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class OfferProductTemplateDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1231940046600480645L;

    /** The product template. */
    private ProductTemplateDto productTemplate;

    /** The mandatory. */
    private Boolean mandatory;

    /**
     * Instantiates a new offer product template dto.
     */
    public OfferProductTemplateDto() {
    }

    /**
     * Gets the product template.
     *
     * @return the product template
     */
    public ProductTemplateDto getProductTemplate() {
        return productTemplate;
    }

    /**
     * Sets the product template.
     *
     * @param productTemplate the new product template
     */
    public void setProductTemplate(ProductTemplateDto productTemplate) {
        this.productTemplate = productTemplate;
    }

    /**
     * Gets the mandatory.
     *
     * @return the mandatory
     */
    public Boolean getMandatory() {
        return mandatory;
    }

    /**
     * Sets the mandatory.
     *
     * @param mandatory the new mandatory
     */
    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }

}