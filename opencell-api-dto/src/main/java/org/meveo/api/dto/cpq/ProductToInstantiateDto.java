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

package org.meveo.api.dto.cpq;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.api.dto.CustomFieldsDto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The Class ProductToInstantiate.
 *
 * @author Mbarek-Ay
 */
@XmlRootElement(name = "productToInstantiate")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductToInstantiateDto extends BaseEntityDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -3815026205495621916L;

    /** The code. */
    @XmlAttribute(required = true)
    private String productCode;
   

    /** The quantity. */
    @XmlElement(required = true)
    private BigDecimal quantity = BigDecimal.ONE;
    
    private Date deliveryDate;

    private List<OrderAttributeDto> attributeInstances = new ArrayList<OrderAttributeDto>();
    

    @Schema(description = "The custom fields")
    protected CustomFieldsDto customFields;

  
    public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	/**
     * Gets the quantity.
     *
     * @return the quantity
     */
    public BigDecimal getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity.
     *
     * @param quantity the new quantity
     */
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public List<OrderAttributeDto> getAttributeInstances() {
        return attributeInstances;
    }

    public void setAttributeInstances(List<OrderAttributeDto> attributeInstances) {
        this.attributeInstances = attributeInstances;
    }

    @Override
	public String toString() {
		return "ProductToInstantiateDto [code=" + productCode + ", quantity=" + quantity + "]";
	}

	public Date getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

    public CustomFieldsDto getCustomFields() {
        return customFields;
    }

    public void setCustomFields(CustomFieldsDto customFields) {
        this.customFields = customFields;
    }
       
}