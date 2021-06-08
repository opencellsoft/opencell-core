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

package org.meveo.api.dto.billing;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.api.dto.CustomFieldDto;
import org.meveo.model.DatePeriod;

/**
 * The Class attributeInstanceDto.
 * 
 * @author Tarik FA.
 * @lastModifiedVersion 11
 */
@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class AttributeInstanceDto extends BaseEntityDto {


    /**
	 * 
	 */
	private static final long serialVersionUID = 3522824146767134576L;
	
	private String serviceInstanceCode;
	private String subscriptionCode;
	private DatePeriod subsScriptionValidity;
	private String attributeCode;
	private Long parentAttributeValueId;
	private Set<Long> assignedAttributeValueIds = new HashSet<Long>();
	private String stringValue;
	private Date dateValue;
	private Double doubleValue;
    private CustomFieldDto customFieldDto;
    
	/**
	 * @return the serviceInstanceCode
	 */
	public String getServiceInstanceCode() {
		return serviceInstanceCode;
	}
	/**
	 * @param serviceInstanceCode the serviceInstanceCode to set
	 */
	public void setServiceInstanceCode(String serviceInstanceCode) {
		this.serviceInstanceCode = serviceInstanceCode;
	}
	/**
	 * @return the subscriptionCode
	 */
	public String getSubscriptionCode() {
		return subscriptionCode;
	}
	/**
	 * @param subscriptionCode the subscriptionCode to set
	 */
	public void setSubscriptionCode(String subscriptionCode) {
		this.subscriptionCode = subscriptionCode;
	}
	/**
	 * @return the subsScriptionValidity
	 */
	public DatePeriod getSubsScriptionValidity() {
		return subsScriptionValidity;
	}
	/**
	 * @param subsScriptionValidity the subsScriptionValidity to set
	 */
	public void setSubsScriptionValidity(DatePeriod subsScriptionValidity) {
		this.subsScriptionValidity = subsScriptionValidity;
	}
	/**
	 * @return the attributeCode
	 */
	public String getAttributeCode() {
		return attributeCode;
	}
	/**
	 * @param attributeCode the attributeCode to set
	 */
	public void setAttributeCode(String attributeCode) {
		this.attributeCode = attributeCode;
	}
	/**
	 * @return the parentAttributeValueId
	 */
	public Long getParentAttributeValueId() {
		return parentAttributeValueId;
	}
	/**
	 * @param parentAttributeValueId the parentAttributeValueId to set
	 */
	public void setParentAttributeValueId(Long parentAttributeValueId) {
		this.parentAttributeValueId = parentAttributeValueId;
	}
	/**
	 * @return the assignedAttributeValueIds
	 */
	public Set<Long> getAssignedAttributeValueIds() {
		return assignedAttributeValueIds;
	}
	/**
	 * @param assignedAttributeValueIds the assignedAttributeValueIds to set
	 */
	public void setAssignedAttributeValueIds(Set<Long> assignedAttributeValueIds) {
		this.assignedAttributeValueIds = assignedAttributeValueIds;
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
	 * @return the customFieldDto
	 */
	public CustomFieldDto getCustomFieldDto() {
		return customFieldDto;
	}
	/**
	 * @param customFieldDto the customFieldDto to set
	 */
	public void setCustomFieldDto(CustomFieldDto customFieldDto) {
		this.customFieldDto = customFieldDto;
	}
}