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

package org.meveo.api.dto.response.billing;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.cpq.commercial.InvoicingPlanItem;

@XmlRootElement(name = "InvoicingPlanItem")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoicingPlanItemDto extends BusinessEntityDto {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	private String billingPlanCode;

	private Integer advancement;

	private BigDecimal rateToBill;

	/**
	 * Instantiates a new invoicingPlanItem dto.
	 */
	public InvoicingPlanItemDto() {
	}

	/**
	 * @param billingPlan
	 * @param advancement
	 * @param rateToBill
	 */
	public InvoicingPlanItemDto(String billingPlanCode, Integer advancement, BigDecimal rateToBill) {
		this.billingPlanCode = billingPlanCode;
		this.advancement = advancement;
		this.rateToBill = rateToBill;
	}

	@Override
	public String toString() {
		return "InvoicingPlanItemDto [code=" + getCode() + ", description=" + getDescription() + "]";
	}

	/**
	 * @return the billingPlan
	 */
	public String getBillingPlanCode() {
		return billingPlanCode;
	}

	/**
	 * @param billingPlan the billingPlan to set
	 */
	public void setBillingPlanCode(String billingPlanCode) {
		this.billingPlanCode = billingPlanCode;
	}

	/**
	 * @return the advancement
	 */
	public Integer getAdvancement() {
		return advancement;
	}

	/**
	 * @param advancement the advancement to set
	 */
	public void setAdvancement(Integer advancement) {
		this.advancement = advancement;
	}

	/**
	 * @return the rateToBill
	 */
	public BigDecimal getRateToBill() {
		return rateToBill;
	}

	/**
	 * @param rateToBill the rateToBill to set
	 */
	public void setRateToBill(BigDecimal rateToBill) {
		this.rateToBill = rateToBill;
	}

	/**
	 * Instantiates a new invoicingPlanItem dto.
	 *
	 * @param invoicingPlanItem    the invoicingPlan entity
	 * @param customFieldInstances Custom field values. Not applicable here.
	 */
	public InvoicingPlanItemDto(InvoicingPlanItem invoicingPlanItem, CustomFieldsDto customFieldInstances) {
		super(invoicingPlanItem);
	}
}