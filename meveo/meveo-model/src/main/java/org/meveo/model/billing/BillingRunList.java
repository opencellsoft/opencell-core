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
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.meveo.model.AuditableEntity;

@Entity
@Table(name = "BILLING_BILLING_RUN_LIST")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_BILLING_RUN_LIST_SEQ")
public class BillingRunList extends AuditableEntity {

	private static final long serialVersionUID = 1L;

	@Column(name = "INVOICE")
	private Boolean invoice;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BILLING_RUN_ID")
	private BillingRun billingRun;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BILLING_ACCOUNT_ID")
	private BillingAccount billingAccount;

	@Column(name = "RATED_AMOUNT_WITHOUT_TAX", precision = 23, scale = 12)
	private BigDecimal RatedAmountWithoutTax = BigDecimal.ZERO;

	@Column(name = "RATED_AMOUNT_TAX", precision = 23, scale = 12)
	private BigDecimal RatedAmountTax = BigDecimal.ZERO;

	@Column(name = "RATED_AMOUNT_WITH_TAX", precision = 23, scale = 12)
	private BigDecimal RatedAmountWithTax = BigDecimal.ZERO;

	@Column(name = "RATED_AMOUNT2_WITHOUT_TAX", precision = 23, scale = 12)
	private BigDecimal RatedAmount2WithoutTax = BigDecimal.ZERO;

	public Boolean getInvoice() {
		return invoice;
	}

	public void setInvoice(Boolean invoice) {
		this.invoice = invoice;
	}

	public BillingRun getBillingRun() {
		return billingRun;
	}

	public void setBillingRun(BillingRun billingRun) {
		this.billingRun = billingRun;
		if (billingRun != null) {
			billingRun.getBillingRunLists().add(this);
		}
	}

	public BigDecimal getRatedAmountWithoutTax() {
		return RatedAmountWithoutTax;
	}

	public void setRatedAmountWithoutTax(BigDecimal ratedAmountWithoutTax) {
		RatedAmountWithoutTax = ratedAmountWithoutTax;
	}

	public BigDecimal getRatedAmountTax() {
		return RatedAmountTax;
	}

	public void setRatedAmountTax(BigDecimal ratedAmountTax) {
		RatedAmountTax = ratedAmountTax;
	}

	public BigDecimal getRatedAmountWithTax() {
		return RatedAmountWithTax;
	}

	public void setRatedAmountWithTax(BigDecimal ratedAmountWithTax) {
		RatedAmountWithTax = ratedAmountWithTax;
	}

	public BillingAccount getBillingAccount() {
		return billingAccount;
	}

	public void setBillingAccount(BillingAccount billingAccount) {
		this.billingAccount = billingAccount;
	}

	public BigDecimal getRatedAmount2WithoutTax() {
		return RatedAmount2WithoutTax;
	}

	public void setRatedAmount2WithoutTax(BigDecimal ratedAmount2WithoutTax) {
		RatedAmount2WithoutTax = ratedAmount2WithoutTax;
	}

}
