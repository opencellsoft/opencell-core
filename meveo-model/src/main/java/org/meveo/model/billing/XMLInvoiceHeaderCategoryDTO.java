/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
 *
 * Licensed under the GNU Public Licence, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
