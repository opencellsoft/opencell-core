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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.model.cpq.commercial.OrderAttribute;

/**
 * DTO to create or update a order attribute
 * 
 * @author Mbarek-Ay
 * @lastModiedVersion 11.0 
 */
@XmlRootElement(name = "OrderAttributeDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class OrderAttributeDto extends BaseEntityDto{

    /**
	 * 
	 */
	private static final long serialVersionUID = 8115890992793236496L;
	
	private Long orderCommercialId;
	private String orderAttributeCode;
    private Long orderAttributeId;
    private String orderLotCode;
    private Long orderProductId;
    private Long orderOfferId;
    
    private String accessPoint;
   

    private List<OrderAttributeDto> linkedOrderAttribute = new ArrayList<>();
    
    private String stringValue;

	private Double doubleValue;

	private Date dateValue;
 
	
	public OrderAttributeDto() {
		super();
	}

	public OrderAttributeDto(OrderAttribute orderAttribute) {
		super();
		orderAttributeId=orderAttribute.getId();
		orderAttributeCode=orderAttribute.getAttribute()!=null?orderAttribute.getAttribute().getCode():null;
		orderCommercialId=orderAttribute.getCommercialOrder()!=null?orderAttribute.getCommercialOrder().getId():null;
	    orderLotCode=orderAttribute.getOrderLot()!=null?orderAttribute.getOrderLot().getCode():null;
	    orderProductId=orderAttribute.getOrderProduct()!=null?orderAttribute.getOrderProduct().getId():null;
	    orderOfferId=orderAttribute.getOrderOffer()!=null?orderAttribute.getOrderOffer().getId():null;
	    accessPoint=orderAttribute.getAccessPoint();
		stringValue =orderAttribute.getStringValue();
		dateValue =orderAttribute.getDateValue();
		doubleValue =orderAttribute.getDoubleValue();
	}

	/**
	 * @return the orderCommercialId
	 */
	public Long getOrderCommercialId() {
		return orderCommercialId;
	}

	/**
	 * @param orderCommercialId the orderCommercialId to set
	 */
	public void setOrderCommercialId(Long orderCommercialId) {
		this.orderCommercialId = orderCommercialId;
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
	 * @return the accessPoint
	 */
	public String getAccessPoint() {
		return accessPoint;
	}

	/**
	 * @param accessPoint the accessPoint to set
	 */
	public void setAccessPoint(String accessPoint) {
		this.accessPoint = accessPoint;
	}

 

	/**
	 * @return the linkedOrderAttribute
	 */
	public List<OrderAttributeDto> getLinkedOrderAttribute() {
		return linkedOrderAttribute;
	}

	/**
	 * @param linkedOrderAttribute the linkedOrderAttribute to set
	 */
	public void setLinkedOrderAttribute(List<OrderAttributeDto> linkedOrderAttribute) {
		this.linkedOrderAttribute = linkedOrderAttribute;
	}

	/**
	 * @return the stringValue
	 */
	public String getStringValue() {
		return stringValue;
	}

	/**
	 * @param stringValue the stringValue to set
	 */
	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	/**
	 * @return the doubleValue
	 */
	public Double getDoubleValue() {
		return doubleValue;
	}

	/**
	 * @param doubleValue the doubleValue to set
	 */
	public void setDoubleValue(Double doubleValue) {
		this.doubleValue = doubleValue;
	}

	/**
	 * @return the dateValue
	 */
	public Date getDateValue() {
		return dateValue;
	}

	/**
	 * @param dateValue the dateValue to set
	 */
	public void setDateValue(Date dateValue) {
		this.dateValue = dateValue;
	}

	/**
	 * @return the orderAttributeCode
	 */
	public String getOrderAttributeCode() {
		return orderAttributeCode;
	}

	/**
	 * @param orderAttributeCode the orderAttributeCode to set
	 */
	public void setOrderAttributeCode(String orderAttributeCode) {
		this.orderAttributeCode = orderAttributeCode;
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
	 * @return the orderAttributeId
	 */
	public Long getOrderAttributeId() {
		return orderAttributeId;
	}

	/**
	 * @param orderAttributeId the orderAttributeId to set
	 */
	public void setOrderAttributeId(Long orderAttributeId) {
		this.orderAttributeId = orderAttributeId;
	}

	
	
  

	
	 
}