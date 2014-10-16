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

import javax.persistence.FetchType;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;

import org.meveo.model.BusinessEntity;

@MappedSuperclass
public class ServiceTemplate extends BusinessEntity {

	private static final long serialVersionUID = 1L;

	@OneToMany(mappedBy = "serviceTemplate",fetch = FetchType.LAZY)
	private List<ServiceChargeTemplate<RecurringChargeTemplate>> recurringCharges = new ArrayList<ServiceChargeTemplate<RecurringChargeTemplate>>();

	@OneToMany(mappedBy = "serviceTemplate",fetch = FetchType.LAZY)
	private List<ServiceChargeTemplate<OneShotChargeTemplate>> subscriptionCharges = new ArrayList<ServiceChargeTemplate<OneShotChargeTemplate>>();

	@OneToMany(mappedBy = "serviceTemplate",fetch = FetchType.LAZY)
	private List<ServiceChargeTemplate<OneShotChargeTemplate>> terminationCharges = new ArrayList<ServiceChargeTemplate<OneShotChargeTemplate>>();

	@OneToMany(mappedBy = "serviceTemplate", fetch = FetchType.LAZY)
	private List<ServiceChargeTemplateUsage> serviceUsageCharges = new ArrayList<ServiceChargeTemplateUsage>();

	public List<ServiceChargeTemplate<RecurringChargeTemplate>> getRecurringCharges() {
		return recurringCharges;
	}

	public void setRecurringCharges(
			List<ServiceChargeTemplate<RecurringChargeTemplate>> recurringCharges) {
		this.recurringCharges = recurringCharges;
	}

	public List<ServiceChargeTemplate<OneShotChargeTemplate>> getSubscriptionCharges() {
		return subscriptionCharges;
	}

	public void setSubscriptionCharges(
			List<ServiceChargeTemplate<OneShotChargeTemplate>> subscriptionCharges) {
		this.subscriptionCharges = subscriptionCharges;
	}

	public List<ServiceChargeTemplate<OneShotChargeTemplate>> getTerminationCharges() {
		return terminationCharges;
	}

	public void setTerminationCharges(
			List<ServiceChargeTemplate<OneShotChargeTemplate>> terminationCharges) {
		this.terminationCharges = terminationCharges;
	}


	public List<ServiceChargeTemplateUsage> getServiceUsageCharges() {
		return serviceUsageCharges;
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
