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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.meveo.model.AuditableEntity;

/**
 * RejectedBillingAccount.
 */
@Entity
@Table(name = "BILLING_REJECTED_BILLING_ACCOUNTS")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_REJECTED_BA_SEQ")
public class RejectedBillingAccount extends AuditableEntity {

	private static final long serialVersionUID = 1L;

	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BILLING_ACCOUNT")
	private BillingAccount billingAccount;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BILLING_RUN")
	private BillingRun billingRun;
	
	@Column(name = "REJECT_CAUSE", length=3000)
	private String rejectCause;
	
	

	public RejectedBillingAccount(BillingAccount billingAccount,
			BillingRun billingRun, String rejectCause) {
		super();
		this.billingAccount = billingAccount;
		this.billingRun = billingRun;
		this.rejectCause = rejectCause;
	}

	public RejectedBillingAccount() { 
	}

	public BillingAccount getBillingAccount() {
		return billingAccount;
	}

	public void setBillingAccount(BillingAccount billingAccount) {
		this.billingAccount = billingAccount;
	}

	public BillingRun getBillingRun() {
		return billingRun;
	}

	public void setBillingRun(BillingRun billingRun) {
		this.billingRun = billingRun;
	}

	public String getRejectCause() {
		return rejectCause;
	}

	public void setRejectCause(String rejectCause) {
		this.rejectCause = rejectCause;
	}
	
	

	
}
