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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.meveo.model.BusinessCFEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.ObservableEntity;
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.model.crm.CustomFieldInstance;

@Entity
@ObservableEntity
@CustomFieldEntity(accountLevel = AccountLevelEnum.SERVICE)
@ExportIdentifier({ "code", "provider" })
@Table(name = "CAT_SERVICE_TEMPLATE", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE", "PROVIDER_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CAT_SERVICE_TEMPLATE_SEQ")
@NamedQueries({			
@NamedQuery(name = "serviceTemplate.getNbServiceWithNotOffer", 
	           query = "select count(*) from ServiceTemplate s where s.id not in (select serv from OfferTemplate o join o.serviceTemplates serv) and s.provider=:provider"),
@NamedQuery(name = "serviceTemplate.getServicesWithNotOffer", 
	           query = "from ServiceTemplate s where s.id not in (select serv from OfferTemplate o join o.serviceTemplates serv) and s.provider=:provider"),
@NamedQuery(name = "serviceTemplate.getServicesWithRecurringsByChargeTemplate",
	           query = "from ServiceTemplate s left join s.serviceRecurringCharges c where c.chargeTemplate=:chargeTemplate")
//@NamedQuery(name = "serviceTemplate.getServicesWithSubscriptionsByChargeTemplate", 
//				query = "from ServiceTemplate s left join s.serviceSubscriptionCharges c where c.chargeTemplate=:chargeTemplate"),
//@NamedQuery(name = "serviceTemplate.getServicesWithTerminationsByChargeTemplate", 
//				query = "from ServiceTemplate s left join s.serviceTerminationCharges c where c.chargeTemplate=:chargeTemplate"),
//@NamedQuery(name = "serviceTemplate.getServicesWithUsagesByChargeTemplate", 
//				query = "from ServiceTemplate s left join s.serviceUsageCharges c where c.chargeTemplate=:chargeTemplate")
})
public class ServiceTemplate extends BusinessCFEntity {

	private static final long serialVersionUID = 1L;

	@OneToMany(mappedBy = "serviceTemplate", fetch = FetchType.LAZY,cascade=CascadeType.PERSIST)
	private List<ServiceChargeTemplateRecurring> serviceRecurringCharges = new ArrayList<ServiceChargeTemplateRecurring>();

	@OneToMany(mappedBy = "serviceTemplate", fetch = FetchType.LAZY,cascade=CascadeType.PERSIST)
	private List<ServiceChargeTemplateSubscription> serviceSubscriptionCharges = new ArrayList<ServiceChargeTemplateSubscription>();

	@OneToMany(mappedBy = "serviceTemplate", fetch = FetchType.LAZY,cascade=CascadeType.PERSIST)
	private List<ServiceChargeTemplateTermination> serviceTerminationCharges = new ArrayList<ServiceChargeTemplateTermination>();

	@OneToMany(mappedBy = "serviceTemplate", fetch = FetchType.LAZY,cascade=CascadeType.PERSIST)
	private List<ServiceChargeTemplateUsage> serviceUsageCharges = new ArrayList<ServiceChargeTemplateUsage>();

	@OneToMany(mappedBy = "serviceTemplate", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@MapKeyColumn(name = "code")
	private Map<String, CustomFieldInstance> customFields = new HashMap<String, CustomFieldInstance>();

	@ManyToOne
	@JoinColumn(name = "INVOICING_CALENDAR_ID")
	private Calendar invoicingCalendar;
	
	public ServiceChargeTemplateRecurring getServiceRecurringChargeByChargeCode(String chargeCode) {
		ServiceChargeTemplateRecurring result = null;
		for (ServiceChargeTemplateRecurring sctr : serviceRecurringCharges) {
			if (sctr.getChargeTemplate().getCode().equals(chargeCode)) {
				result = sctr;
				break;
			}
		}
		return result;
	}

	public List<ServiceChargeTemplateRecurring> getServiceRecurringCharges() {
		return serviceRecurringCharges;
	}

	public void setServiceRecurringCharges(List<ServiceChargeTemplateRecurring> serviceRecurringCharges) {
		this.serviceRecurringCharges = serviceRecurringCharges;
	}

	public ServiceChargeTemplateSubscription getServiceChargeTemplateSubscriptionByChargeCode(String chargeCode) {
		ServiceChargeTemplateSubscription result = null;
		for (ServiceChargeTemplateSubscription sctr : serviceSubscriptionCharges) {
			if (sctr.getChargeTemplate().getCode().equals(chargeCode)) {
				result = sctr;
				break;
			}
		}
		return result;
	}

	public List<ServiceChargeTemplateSubscription> getServiceSubscriptionCharges() {
		return serviceSubscriptionCharges;
	}

	public void setServiceSubscriptionCharges(List<ServiceChargeTemplateSubscription> serviceSubscriptionCharges) {
		this.serviceSubscriptionCharges = serviceSubscriptionCharges;
	}

	public ServiceChargeTemplateTermination getServiceChargeTemplateTerminationByChargeCode(String chargeCode) {
		ServiceChargeTemplateTermination result = null;
		for (ServiceChargeTemplateTermination sctr : serviceTerminationCharges) {
			if (sctr.getChargeTemplate().getCode().equals(chargeCode)) {
				result = sctr;
				break;
			}
		}
		return result;
	}

	public List<ServiceChargeTemplateTermination> getServiceTerminationCharges() {
		return serviceTerminationCharges;
	}

	public void setServiceTerminationCharges(List<ServiceChargeTemplateTermination> serviceTerminationCharges) {
		this.serviceTerminationCharges = serviceTerminationCharges;
	}

	public ServiceChargeTemplateUsage getServiceChargeTemplateUsageByChargeCode(String chargeCode) {
		ServiceChargeTemplateUsage result = null;
		for (ServiceChargeTemplateUsage sctr : serviceUsageCharges) {
			if (sctr.getChargeTemplate().getCode().equals(chargeCode)) {
				result = sctr;
				break;
			}
		}
		return result;
	}

	public List<ServiceChargeTemplateUsage> getServiceUsageCharges() {
		return serviceUsageCharges;
	}

	public void setServiceUsageCharges(List<ServiceChargeTemplateUsage> serviceUsageCharges) {
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

	public Map<String, CustomFieldInstance> getCustomFields() {
		return customFields;
	}

	public void setCustomFields(Map<String, CustomFieldInstance> customFields) {
		this.customFields = customFields;
	}

	public Calendar getInvoicingCalendar() {
		return invoicingCalendar;
	}

	public void setInvoicingCalendar(Calendar invoicingCalendar) {
		this.invoicingCalendar = invoicingCalendar;
	}

    @Override
    public ICustomFieldEntity getParentCFEntity() {
        return null;
	}
}