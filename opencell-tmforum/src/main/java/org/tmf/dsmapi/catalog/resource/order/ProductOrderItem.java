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

package org.tmf.dsmapi.catalog.resource.order;

import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.CustomFieldsDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmf.dsmapi.catalog.resource.product.ProductOffering;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@XmlRootElement(name="OrderItem", namespace="http://www.tmforum.org")
@XmlType(name = "OrderItem", namespace = "http://www.tmforum.org")
@JsonInclude(value = Include.NON_NULL)
public class ProductOrderItem implements Serializable {

    private static final long serialVersionUID = 2224931518265750159L;
    private String id;
    private String action;
    private String state;
    private String appointment;
    private List<BillingAccount> billingAccount;
    private ProductOffering productOffering;
    private Product product;
    private CustomFieldsDto customFields;
    
    private static JAXBContext jaxbCxt;
    
    private static final Logger log = LoggerFactory.getLogger(ProductOrderItem.class);
    
	static {
		try {
			
			jaxbCxt =  JAXBContext.newInstance(ProductOrderItem.class);
			
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			log.error("error = {}", e);
		}
	}
	
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAppointment() {
        return appointment;
    }

    public void setAppointment(String appointment) {
        this.appointment = appointment;
    }

    public List<BillingAccount> getBillingAccount() {
        return billingAccount;
    }

    public void setBillingAccount(List<BillingAccount> billingAccount) {
        this.billingAccount = billingAccount;
    }

    public ProductOffering getProductOffering() {
        return productOffering;
    }

    public void setProductOffering(ProductOffering productOffering) {
        this.productOffering = productOffering;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    /**
     * Serialize orderItem DTO into a string
     * 
     * @param productOrderItem Order item to serialize
     * @return String in XML format
     * 
     * @throws BusinessException business exception
     */
    public static String serializeOrderItem(ProductOrderItem productOrderItem) throws BusinessException {
        try {
            //Marshaller m = JAXBContext.newInstance(ProductOrderItem.class).createMarshaller();
            //m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        	Marshaller mar;
        	mar = jaxbCxt.createMarshaller();
			mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			
            StringWriter w = new StringWriter();
            mar.marshal(productOrderItem, w);

            return w.toString();

        } catch (JAXBException e) {
            throw new BusinessException(e);
        }
    }

    /**
     * Deserialize order item from a string.
     * 
     * @param orderItemSource Serialized orderItem dto
     * @return Order item object
     * 
     * @throws BusinessException business exception
     */
    public static ProductOrderItem deserializeOrderItem(String orderItemSource) throws BusinessException {
        // Store orderItem DTO into DB to be retrieved for full information
        try {
            //Unmarshaller m = JAXBContext.newInstance(ProductOrderItem.class).createUnmarshaller();
            // m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        	Unmarshaller umar;
			umar =jaxbCxt.createUnmarshaller();
        	ProductOrderItem productOrderItem = (ProductOrderItem) umar.unmarshal(new StringReader(orderItemSource));

            return productOrderItem;

        } catch (JAXBException e) {
            throw new BusinessException(e);
        }
    }

	/**
	 * @return the customFields
	 */
	public CustomFieldsDto getCustomFields() {
		return customFields;
	}

	/**
	 * @param customFields the customFields to set
	 */
	public void setCustomFields(CustomFieldsDto customFields) {
		this.customFields = customFields;
	}
}