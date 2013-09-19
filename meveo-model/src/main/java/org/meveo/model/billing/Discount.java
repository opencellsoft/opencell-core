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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.meveo.model.AuditableEntity;

/**
 * Discount entity.
 */
@Entity
@Table(name = "BILLING_DISCOUNT")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_DISCOUNT_SEQ")
public class Discount extends AuditableEntity {
	private static final long serialVersionUID = 1L;

	@Column(name = "DISCOUNT_CODE", length = 20)
	private String discountCode;

	@Column(name = "PR_DESCRIPTION", length = 100)
	private String prDescription;

	@Column(name = "POURCENT")
	private BigDecimal pourcent;

	public String getDiscountCode() {
		return discountCode;
	}

	public void setDiscountCode(String discountCode) {
		this.discountCode = discountCode;
	}

	public String getPrDescription() {
		return prDescription;
	}

	public void setPrDescription(String prDescription) {
		this.prDescription = prDescription;
	}

	public BigDecimal getPourcent() {
		return pourcent;
	}

	public void setPourcent(BigDecimal pourcent) {
		this.pourcent = pourcent;
	}

}
