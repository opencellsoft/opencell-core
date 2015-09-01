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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.meveo.model.BusinessEntity;
import org.meveo.model.catalog.CounterTemplate;

@Entity
@Table(name = "BILLING_COUNTER")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_COUNTER_INSTANCE_SEQ")
public class CounterInstance extends BusinessEntity {
	private static final long serialVersionUID = -4924601467998738157L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COUNTER_TEMPLATE_ID")
	private CounterTemplate counterTemplate;

	@ManyToOne
	@JoinColumn(name = "USER_ACCOUNT_ID")
	private UserAccount userAccount;

	@ManyToOne
	@JoinColumn(name = "BILLING_ACCOUNT_ID")
	private BillingAccount billingAccount;

	@OneToMany(mappedBy = "counterInstance", fetch = FetchType.LAZY)
	private List<CounterPeriod> counterPeriods = new ArrayList<CounterPeriod>();

	public CounterTemplate getCounterTemplate() {
		return counterTemplate;
	}

	public void setCounterTemplate(CounterTemplate counterTemplate) {
		this.counterTemplate = counterTemplate;
		if (counterTemplate != null) {
			this.code = counterTemplate.getCode();
			this.description = counterTemplate.getDescription();
		} else {
			this.code = null;
			this.description = null;
		}
	}

	public UserAccount getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(UserAccount userAccount) {
		this.userAccount = userAccount;
	}

	public List<CounterPeriod> getCounterPeriods() {
		return counterPeriods;
	}

	public void setCounterPeriods(List<CounterPeriod> counterPeriods) {
		this.counterPeriods = counterPeriods;
	}

	public BillingAccount getBillingAccount() {
		return billingAccount;
	}

	public void setBillingAccount(BillingAccount billingAccount) {
		this.billingAccount = billingAccount;
	}

	

}
