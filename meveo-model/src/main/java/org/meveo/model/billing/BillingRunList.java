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

	@Column(name = "RATED_AMOUNT_WITHOUT_TAX", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal RatedAmountWithoutTax = BigDecimal.ZERO;

	@Column(name = "RATED_AMOUNT_TAX", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal RatedAmountTax = BigDecimal.ZERO;

	@Column(name = "RATED_AMOUNT_WITH_TAX", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal RatedAmountWithTax = BigDecimal.ZERO;

	@Column(name = "RATED_AMOUNT2_WITHOUT_TAX", precision = NB_PRECISION, scale = NB_DECIMALS)
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
