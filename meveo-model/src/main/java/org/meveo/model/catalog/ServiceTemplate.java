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
package org.meveo.model.catalog;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.meveo.model.BusinessEntity;
import org.meveo.model.billing.ServiceInstance;

@Entity
@Table(name = "CAT_SERVICE_TEMPLATE")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CAT_SERVICE_TEMPLATE_SEQ")
public class ServiceTemplate extends BusinessEntity {

	private static final long serialVersionUID = 1L;

	@OneToMany(mappedBy = "serviceTemplate",fetch = FetchType.LAZY)
	private List<ServiceChargeTemplateRecurring> serviceRecurringCharges = new ArrayList<ServiceChargeTemplateRecurring>();

	@OneToMany(mappedBy = "serviceTemplate",fetch = FetchType.LAZY)
	private List<ServiceChargeTemplateSubscription> serviceSubscriptionCharges = new ArrayList<ServiceChargeTemplateSubscription>();

	@OneToMany(mappedBy = "serviceTemplate",fetch = FetchType.LAZY)
	private List<ServiceChargeTemplateTermination> serviceTerminationCharges = new ArrayList<ServiceChargeTemplateTermination>();

	@OneToMany(mappedBy = "serviceTemplate", fetch = FetchType.LAZY)
	private List<ServiceChargeTemplateUsage> serviceUsageCharges = new ArrayList<ServiceChargeTemplateUsage>();
	
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "CAT_SERV_RECCHARGE_TEMPLATES", joinColumns = @JoinColumn(name = "SERVICE_TEMPLATE_ID"), inverseJoinColumns = @JoinColumn(name = "CHARGE_TEMPLATE_ID"))
	private List<RecurringChargeTemplate> recurringCharges = new ArrayList<RecurringChargeTemplate>();

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "CAT_SERV_ONECHARGE_S_TEMPLATES", joinColumns = @JoinColumn(name = "SERVICE_TEMPLATE_ID"), inverseJoinColumns = @JoinColumn(name = "CHARGE_TEMPLATE_ID"))
	private List<OneShotChargeTemplate> subscriptionCharges = new ArrayList<OneShotChargeTemplate>();

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "CAT_SERV_ONECHARGE_T_TEMPLATES", joinColumns = @JoinColumn(name = "SERVICE_TEMPLATE_ID"), inverseJoinColumns = @JoinColumn(name = "CHARGE_TEMPLATE_ID"))
	private List<OneShotChargeTemplate> terminationCharges = new ArrayList<OneShotChargeTemplate>();
 

	@OneToMany(mappedBy = "serviceTemplate", fetch = FetchType.LAZY)
	private List<ServiceInstance> serviceInstances = new ArrayList<ServiceInstance>();

	

	public List<RecurringChargeTemplate> getRecurringCharges() {
		return recurringCharges;
	}

	public void setRecurringCharges(
			List<RecurringChargeTemplate> recurringCharges) {
		this.recurringCharges = recurringCharges;
	}

	public List<OneShotChargeTemplate> getSubscriptionCharges() {
		return subscriptionCharges;
	}

	public void setSubscriptionCharges(
			List<OneShotChargeTemplate> subscriptionCharges) {
		this.subscriptionCharges = subscriptionCharges;
	}

	public List<OneShotChargeTemplate> getTerminationCharges() {
		return terminationCharges;
	}

	public void setTerminationCharges(
			List<OneShotChargeTemplate> terminationCharges) {
		this.terminationCharges = terminationCharges;
	}

	public List<ServiceInstance> getServiceInstances() {
		return serviceInstances;
	}

	public void setServiceInstances(List<ServiceInstance> serviceInstances) {
		this.serviceInstances = serviceInstances;
	}

 
	
	

	public List<ServiceChargeTemplateUsage> getServiceUsageCharges() {
		return serviceUsageCharges;
	}

	public List<ServiceChargeTemplateRecurring> getServiceRecurringCharges() {
		return serviceRecurringCharges;
	}

	public void setServiceRecurringCharges(
			List<ServiceChargeTemplateRecurring> serviceRecurringCharges) {
		this.serviceRecurringCharges = serviceRecurringCharges;
	}

	public List<ServiceChargeTemplateSubscription> getServiceSubscriptionCharges() {
		return serviceSubscriptionCharges;
	}

	public void setServiceSubscriptionCharges(
			List<ServiceChargeTemplateSubscription> serviceSubscriptionCharges) {
		this.serviceSubscriptionCharges = serviceSubscriptionCharges;
	}

	public List<ServiceChargeTemplateTermination> getServiceTerminationCharges() {
		return serviceTerminationCharges;
	}

	public void setServiceTerminationCharges(
			List<ServiceChargeTemplateTermination> serviceTerminationCharges) {
		this.serviceTerminationCharges = serviceTerminationCharges;
	}

	public void setServiceUsageCharges(
			List<ServiceChargeTemplateUsage> serviceUsageCharges) {
		this.serviceUsageCharges = serviceUsageCharges;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		BusinessEntity other = (BusinessEntity) obj;
		if (code == null) {
			if (other.getCode() != null)
				return false;
		} else if (!code.equals(other.getCode()))
			return false;
		return true;
	}

}