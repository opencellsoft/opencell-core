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

package org.meveo.admin.web.converter;

import java.math.BigDecimal;

import org.meveo.model.BusinessCFEntity;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.InvoiceSubCategory;

/**
 * @author Edward P. Legaspi
 **/
public class InvoiceCatSubCatModel {

	public static final int CATEGORY = 0;
	public static final int SUB_CATEGORY = 1;

	private int type;
	private BusinessCFEntity entity;;
	private String label;
	private String description;
	private BigDecimal amountWithTax;
	private BigDecimal amountWithoutTax;
	private BigDecimal amountTax;

	public InvoiceCatSubCatModel() {

	}

	public InvoiceCatSubCatModel(int type, BusinessCFEntity entity, String label) {
		this.type = type;
		this.entity = entity;
		this.label = (type == CATEGORY) ? label : " >" + label;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public BusinessCFEntity getEntity() {
		return entity;
	}

	public void setEntity(BusinessCFEntity entity) {
		this.entity = entity;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return "InvoiceCatSubCatModel [type=" + type + ", entity=" + entity + ", label=" + label + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (entity == null) {
			return false;
		}
		InvoiceCatSubCatModel temp = (InvoiceCatSubCatModel) obj;
		if (temp != null && temp.getType() == CATEGORY) {
			return ((InvoiceCategory) temp.getEntity()).getCode().equals(entity.getCode());
		} else if (temp != null && temp.getType() == SUB_CATEGORY) {
			return ((InvoiceSubCategory) temp.getEntity()).getCode().equals(entity.getCode());
		}

		return false;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getAmountWithTax() {
		return amountWithTax;
	}

	public void setAmountWithTax(BigDecimal amountWithTax) {
		this.amountWithTax = amountWithTax;
	}

	public BigDecimal getAmountWithoutTax() {
		return amountWithoutTax;
	}

	public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
		this.amountWithoutTax = amountWithoutTax;
	}

	public BigDecimal getAmountTax() {
		return amountTax;
	}

	public void setAmountTax(BigDecimal amountTax) {
		this.amountTax = amountTax;
	}

}
