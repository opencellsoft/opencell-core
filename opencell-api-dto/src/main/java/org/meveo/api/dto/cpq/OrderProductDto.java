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
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.cpq.ProductVersionAttribute;
import org.meveo.model.cpq.commercial.OrderAttribute;
import org.meveo.model.cpq.commercial.OrderProduct;
import org.meveo.model.cpq.commercial.ProductActionTypeEnum;
import org.meveo.model.cpq.enums.AttributeTypeEnum;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO to create or update a order product
 * 
 * @author Mbarek-Ay
 * @lastModiedVersion 11.0 
 */
@XmlRootElement(name = "OrderProductDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class OrderProductDto extends BaseEntityDto{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7231556751341912018L;

	@Schema(description = "The order product id")
	private Long orderProductId;

	@Schema(description = "The commercial order id")
	private Long commercialOrderId;
    

	@Schema(description = "The order lot code")
    private String orderLotCode;

	@Schema(description = "The order offer id")
    private Long orderOfferId; 

	@Schema(description = "The discount plan code")
    private String discountPlanCode;

	@Schema(description = "The proudct version")
    private Integer productVersion;

	@Schema(description = "The product code")
    private String productCode;

	@Schema(description = "The quantity")
    private BigDecimal quantity;
	
	@Schema(description = "The delivery date")
    private Date deliveryDate;
	
	@Schema(description = "The termination date")
    private Date terminationDate;
	
	@Schema(description = "The termination reason code")
    private String terminationReasonCode;
	
	@Schema(description = "The action type")
    private ProductActionTypeEnum actionType;
	
	@Schema(description = "The Instance Status Enum")
    private InstanceStatusEnum status;

	@Schema(description = "The service instance id")
	private Long serviceInstanceId;

    private List<OrderAttributeDto> orderAttributes=new ArrayList<OrderAttributeDto>();

	public OrderProductDto() {
    	super();
    }
    

	public OrderProductDto(OrderProduct orderProduct) {
		super();
		init(orderProduct);
		
	}
	
	public void init(OrderProduct orderProduct) {
		orderProductId=orderProduct.getId();
		orderLotCode=orderProduct.getOrderServiceCommercial()!=null?orderProduct.getOrderServiceCommercial().getCode():null;
		commercialOrderId=orderProduct.getOrder()!=null?orderProduct.getOrder().getId():null;
		if(orderProduct.getProductVersion() != null){
			productCode= orderProduct.getProductVersion().getProduct().getCode();
		}
		quantity=orderProduct.getQuantity();
		discountPlanCode=orderProduct.getDiscountPlan()!=null?orderProduct.getDiscountPlan().getCode():null;
		deliveryDate=orderProduct.getDeliveryDate();
		actionType=orderProduct.getProductActionType();
		terminationReasonCode=orderProduct.getTerminationReason()!=null?orderProduct.getTerminationReason().getCode():null;
		terminationDate=orderProduct.getTerminationDate();
		status=orderProduct.getStatus();
		serviceInstanceId = orderProduct.getServiceInstance() != null ? orderProduct.getServiceInstance().getId() : null;
	}
	
	public OrderProductDto(OrderProduct orderProduct, boolean loadAttributes) {
		super();
		init(orderProduct);
		if(loadAttributes && orderProduct.getProductVersion() != null) {
			orderAttributes=new ArrayList<OrderAttributeDto>();
			for(OrderAttribute orderAttribute:orderProduct.getOrderAttributes()) {
				OrderAttributeDto orderAttributeDto = new OrderAttributeDto(orderAttribute);
				AttributeTypeEnum attributeType = orderAttribute.getAttribute().getAttributeType();
				Optional<ProductVersionAttribute> productVersionAttribute = orderProduct.getProductVersion().getAttributes().stream()
						.filter(pva -> pva.getAttribute().getId() == orderAttribute.getAttribute().getId())
						.findFirst();
				productVersionAttribute.ifPresent(pAttribute -> resolveDefaultValuesIfNull(orderAttributeDto, attributeType, pAttribute));
				orderAttributes.add(orderAttributeDto);
			}
		} 
		
	}

	private void resolveDefaultValuesIfNull(OrderAttributeDto orderAttributeDto, AttributeTypeEnum attributeType, ProductVersionAttribute pAttribute) {
		switch (attributeType) {
			case BOOLEAN:
				if(orderAttributeDto.getBooleanValue() == null){
					orderAttributeDto.setBooleanValue(Boolean.valueOf(pAttribute.getDefaultValue()));
				}
				break;
			case NUMERIC:
			case INTEGER:
				if(orderAttributeDto.getDoubleValue() == null && pAttribute.getDefaultValue() != null  &&  !pAttribute.getDefaultValue().isEmpty()){
					orderAttributeDto.setDoubleValue(Double.valueOf(pAttribute.getDefaultValue()));
				}
				break;
			case DATE:
				if(orderAttributeDto.getDateValue() == null){
					orderAttributeDto.setDateValue(new Date(pAttribute.getDefaultValue()));
				}
				break;
			default:
				if(StringUtils.isBlank(orderAttributeDto.getStringValue())){
					orderAttributeDto.setStringValue(pAttribute.getDefaultValue());
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


	public String getDiscountPlanCode() {
		return discountPlanCode;
	}


	public void setDiscountPlanCode(String discountPlanCode) {
		this.discountPlanCode = discountPlanCode;
	}


	public Date getDeliveryDate() {
		return deliveryDate;
	}


	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}


	public Date getTerminationDate() {
		return terminationDate;
	}


	public void setTerminationDate(Date terminationDate) {
		this.terminationDate = terminationDate;
	}


	public String getTerminationReasonCode() {
		return terminationReasonCode;
	}


	public void setTerminationReasonCode(String terminationReasonCode) {
		this.terminationReasonCode = terminationReasonCode;
	}


	public ProductActionTypeEnum getActionType() {
		return actionType;
	}


	public void setActionType(ProductActionTypeEnum actionType) {
		this.actionType = actionType;
	}


	public InstanceStatusEnum getInstanceStatus() {
		return status;
	}


	public void setInstanceStatus(InstanceStatusEnum status) {
		this.status = status;
	}

	public Long getServiceInstanceId() {
		return serviceInstanceId;
	}

	public void setServiceInstanceId(Long serviceInstanceId) {
		this.serviceInstanceId = serviceInstanceId;
	}
}