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
 * The Class BundleProductTemplateDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "BundleProductTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class BundleProductTemplateDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4914322874611290121L;

    /** The product template. */
    private ProductTemplateDto productTemplate;

    /** The quantity. */
    private int quantity;

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
     * Gets the quantity.
     *
     * @return the quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity.
     *
     * @param quantity the new quantity
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}