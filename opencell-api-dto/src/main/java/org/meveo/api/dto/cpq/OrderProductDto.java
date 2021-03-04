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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.model.cpq.commercial.OrderAttribute;
import org.meveo.model.cpq.commercial.OrderProduct;

/**
 * DTO to create or update a order product
 * 
 * @author Mbarek-Ay
 * @lastModiedVersion 11.0 
 */
@XmlRootElement(name = "QuoteProductDTO")
@XmlAccessorType(XmlAccessType.FIELD)
public class OrderProductDto extends BaseEntityDto{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7231556751341912018L;

	private Long orderProductId;
	 
	private Long commercialOrderId;
    
     
    private String orderLotCode;
    
    private Long orderOfferId;
 
    private int quoteVersion;
    
    private Integer productVersion;
    
    private String productCode;
 
    private BigDecimal quantity;
    
    private List<OrderAttributeDto> orderAttributes=new ArrayList<OrderAttributeDto>(); 
    
    public OrderProductDto() {
    	super();
    }
    

	public OrderProductDto(OrderProduct orderProduct) {
		super();
		init(orderProduct);
		
	}
	
	public void init(OrderProduct orderProduct) {
		orderLotCode=orderProduct.getOrderServiceCommercial()!=null?orderProduct.getOrderServiceCommercial().getCode():null;
		commercialOrderId=orderProduct.getOrder()!=null?orderProduct.getOrder().getId():null;
		productCode=orderProduct.getProductVersion().getProduct().getCode();
		productVersion=orderProduct.getProductVersion().getCurrentVersion();
		quantity=orderProduct.getQuantity();
	}
	
	public OrderProductDto(OrderProduct orderProduct, boolean loadAttributes) {
		super();
		init(orderProduct);
		if(loadAttributes) {
			orderAttributes=new ArrayList<OrderAttributeDto>();
			for(OrderAttribute orderAttribute:orderProduct.getOrderAttributes()) {
				orderAttributes.add(new OrderAttributeDto(orderAttribute));
			}
		} 
		
	}


	/**
	 * @return the orderProductId
	 */
	public Long getOrderProductId() {
		return orderProductId;
	}


	/**
	 * @param orderProductId the orderProductId to set
	 */
	public void setOrderProductId(Long orderProductId) {
		this.orderProductId = orderProductId;
	}


	/**
	 * @return the commercialOrderId
	 */
	public Long getCommercialOrderId() {
		return commercialOrderId;
	}


	/**
	 * @param commercialOrderId the commercialOrderId to set
	 */
	public void setCommercialOrderId(Long commercialOrderId) {
		this.commercialOrderId = commercialOrderId;
	}


	/**
	 * @return the orderLotCode
	 */
	public String getOrderLotCode() {
		return orderLotCode;
	}


	/**
	 * @param orderLotCode the orderLotCode to set
	 */
	public void setOrderLotCode(String orderLotCode) {
		this.orderLotCode = orderLotCode;
	}


	/**
	 * @return the orderOfferId
	 */
	public Long getOrderOfferId() {
		return orderOfferId;
	}


	/**
	 * @param orderOfferId the orderOfferId to set
	 */
	public void setOrderOfferId(Long orderOfferId) {
		this.orderOfferId = orderOfferId;
	}


	/**
	 * @return the quoteVersion
	 */
	public int getQuoteVersion() {
		return quoteVersion;
	}


	/**
	 * @param quoteVersion the quoteVersion to set
	 */
	public void setQuoteVersion(int quoteVersion) {
		this.quoteVersion = quoteVersion;
	}


	/**
	 * @return the productVersion
	 */
	public Integer getProductVersion() {
		return productVersion;
	}


	/**
	 * @param productVersion the productVersion to set
	 */
	public void setProductVersion(Integer productVersion) {
		this.productVersion = productVersion;
	}


	/**
	 * @return the productCode
	 */
	public String getProductCode() {
		return productCode;
	}


	/**
	 * @param productCode the productCode to set
	 */
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}


	/**
	 * @return the quantity
	 */
	public BigDecimal getQuantity() {
		return quantity;
	}


	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}


	/**
	 * @return the orderAttributes
	 */
	public List<OrderAttributeDto> getOrderAttributes() {
		return orderAttributes;
	}


	/**
	 * @param orderAttributes the orderAttributes to set
	 */
	public void setOrderAttributes(List<OrderAttributeDto> orderAttributes) {
		this.orderAttributes = orderAttributes;
	}

	
}