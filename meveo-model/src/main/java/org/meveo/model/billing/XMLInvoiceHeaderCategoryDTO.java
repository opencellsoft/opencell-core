/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.billing;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class XMLInvoiceHeaderCategoryDTO {

	private String description;
	private String code;
	private BigDecimal amountWithoutTax = BigDecimal.ZERO;
	private BigDecimal amountWithTax = BigDecimal.ZERO;

	private Map<Long, RatedTransaction> ratedtransactions = new HashMap<Long, RatedTransaction>();

	public Map<Long, RatedTransaction> getRatedtransactions() {
		return ratedtransactions;
	}

	public void setRatedtransactions(Map<Long, RatedTransaction> ratedtransactions) {
		this.ratedtransactions = ratedtransactions;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public BigDecimal getAmountWithoutTax() {
		return amountWithoutTax;
	}

	public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
		this.amountWithoutTax = amountWithoutTax;
	}

	public BigDecimal getAmountWithTax() {
		return amountWithTax;
	}

	public void setAmountWithTax(BigDecimal amountWithTax) {
		this.amountWithTax = amountWithTax;
	}

	public void addAmountWithTax(BigDecimal amountToAdd) {
		if(amountToAdd!=null){
			if (amountWithTax == null) {
				amountWithTax = new BigDecimal("0");
			}
			amountWithTax = amountWithTax.add(amountToAdd);
		}
	}

	public void addAmountWithoutTax(BigDecimal amountToAdd) {
		if (amountWithoutTax == null) {
			amountWithoutTax = new BigDecimal("0");
		}
		amountWithoutTax = amountWithoutTax.add(amountToAdd);
	}

}
