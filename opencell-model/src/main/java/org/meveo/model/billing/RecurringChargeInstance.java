/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.billing;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.catalog.RecurringChargeTemplate;

@Entity
@Table(name = "billing_recurring_charge_inst")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {@Parameter(name = "sequence_name", value = "billing_recurring_chrg_inst_seq"), })
public class RecurringChargeInstance extends ChargeInstance {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "recurring_chrg_tmpl_id")
	private RecurringChargeTemplate recurringChargeTemplate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "service_instance_id")
	protected ServiceInstance serviceInstance;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "subscription_date")
	protected Date subscriptionDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "next_charge_date")
	protected Date nextChargeDate;

	public RecurringChargeInstance(String code, String description, Date subscriptionDate,
			BigDecimal amountWithoutTax, BigDecimal amount2, Subscription subscription,
			RecurringChargeTemplate recurringChargeTemplate, ServiceInstance serviceInstance) {
		this.code = code;
		this.description = description;
		this.subscriptionDate = subscriptionDate;
		this.chargeDate = subscriptionDate;
		this.amountWithoutTax = amountWithoutTax;
		this.amountWithTax = amount2;
		this.chargeTemplate = recurringChargeTemplate;
		this.serviceInstance = serviceInstance;
		this.setSubscription(subscription);
	}

	public RecurringChargeInstance() {

	}

	public RecurringChargeTemplate getRecurringChargeTemplate() {
		return recurringChargeTemplate;
	}

	public void setRecurringChargeTemplate(RecurringChargeTemplate recurringChargeTemplate) {
		this.recurringChargeTemplate = recurringChargeTemplate;
		this.code = recurringChargeTemplate.getCode();
		this.description = recurringChargeTemplate.getDescription();
	}

	public ServiceInstance getServiceInstance() {
		return serviceInstance;
	}

	public void setServiceInstance(ServiceInstance serviceInstance) {
		this.serviceInstance = serviceInstance;
		//if (serviceInstance != null && !serviceInstance.getRecurringChargeInstances().contains(this)) {
		//	serviceInstance.getRecurringChargeInstances().add(this);
		//}
	}

	public Date getSubscriptionDate() {
		return subscriptionDate;
	}

	public void setSubscriptionDate(Date subscriptionDate) {
		this.subscriptionDate = subscriptionDate;
	}

	public Date getNextChargeDate() {
		return nextChargeDate;
	}

	public void setNextChargeDate(Date nextChargeDate) {
		this.nextChargeDate = nextChargeDate;
	}



}
