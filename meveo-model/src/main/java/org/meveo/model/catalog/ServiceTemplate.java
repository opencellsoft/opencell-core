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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.meveo.model.BusinessEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.crm.CustomFieldInstance;

@Entity
@Table(name = "CAT_SERVICE_TEMPLATE", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE", "PROVIDER_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CAT_SERVICE_TEMPLATE_SEQ")
public class ServiceTemplate extends BusinessEntity implements ICustomFieldEntity {

	private static final long serialVersionUID = 1L;

	@OneToMany(mappedBy = "serviceTemplate", fetch = FetchType.LAZY)
	private List<ServiceChargeTemplateRecurring> serviceRecurringCharges = new ArrayList<ServiceChargeTemplateRecurring>();

	@OneToMany(mappedBy = "serviceTemplate", fetch = FetchType.LAZY)
	private List<ServiceChargeTemplateSubscription> serviceSubscriptionCharges = new ArrayList<ServiceChargeTemplateSubscription>();

	@OneToMany(mappedBy = "serviceTemplate", fetch = FetchType.LAZY)
	private List<ServiceChargeTemplateTermination> serviceTerminationCharges = new ArrayList<ServiceChargeTemplateTermination>();

	@OneToMany(mappedBy = "serviceTemplate", fetch = FetchType.LAZY)
	private List<ServiceChargeTemplateUsage> serviceUsageCharges = new ArrayList<ServiceChargeTemplateUsage>();

	@OneToMany(mappedBy = "serviceTemplate", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@MapKeyColumn(name = "code")
	private Map<String, CustomFieldInstance> customFields = new HashMap<String, CustomFieldInstance>();

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
	
	public String getInheritedCustomStringValue(String code){
		String stringValue=null;
		if (getCustomFields().containsKey(code)&& getCustomFields().get(code).getStringValue()!=null) {
			stringValue=getCustomFields().get(code).getStringValue();
		}
		return stringValue;
		}
	
	public Long getInheritedCustomLongValue(String code){
		Long result=null; 
		if (getCustomFields().containsKey(code)&& getCustomFields().get(code).getLongValue()!=null) {
			result=getCustomFields().get(code).getLongValue();
		}
		return result;
		}
	
	public Date getInheritedCustomDateValue(String code){
		Date result=null; 
		if (getCustomFields().containsKey(code)&& getCustomFields().get(code).getDateValue()!=null) {
			result=getCustomFields().get(code).getDateValue();
		}
		return result;
		}

	public Double getInheritedCustomDoubleValue(String code){
		Double result=null; 
		if (getCustomFields().containsKey(code)&& getCustomFields().get(code).getDoubleValue()!=null) {
			result=getCustomFields().get(code).getDoubleValue();
		}
		return result;
		}
	
	public String getICsv(String code){
		return getInheritedCustomStringValue(code);
	}
	
	public Long getIClv(String code){
		return getInheritedCustomLongValue(code);
	}
	
	public Date getICdav(String code){
		return getInheritedCustomDateValue(code);
	}
	
	public Double getICdov(String code){
		return getInheritedCustomDoubleValue(code);
	}

}
