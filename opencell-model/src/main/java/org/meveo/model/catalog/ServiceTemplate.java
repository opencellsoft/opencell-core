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
package org.meveo.model.catalog;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ModuleItem;
import org.meveo.model.ObservableEntity;
import org.meveo.model.annotation.ImageType;

@Entity
@ModuleItem
@ObservableEntity
@CustomFieldEntity(cftCodePrefix = "SERVICE")
@ExportIdentifier({ "code"})
@Table(name = "cat_service_template", uniqueConstraints = @UniqueConstraint(columnNames = { "code"}))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {@Parameter(name = "sequence_name", value = "cat_service_template_seq"), })
@NamedQueries({			
@NamedQuery(name = "serviceTemplate.getNbServiceWithNotOffer", 
	           query = "select count(*) from ServiceTemplate s where s.id not in (select serv.serviceTemplate.id from OfferTemplate o join o.offerServiceTemplates serv)"),
@NamedQuery(name = "serviceTemplate.getServicesWithNotOffer", 
	           query = "from ServiceTemplate s where s.id not in (select serv.serviceTemplate.id from OfferTemplate o join o.offerServiceTemplates serv)"),
@NamedQuery(name = "serviceTemplate.getServicesWithRecurringsByChargeTemplate",
	           query = "from ServiceTemplate s left join s.serviceRecurringCharges c where c.chargeTemplate=:chargeTemplate")
//@NamedQuery(name = "serviceTemplate.getServicesWithSubscriptionsByChargeTemplate", 
//				query = "from ServiceTemplate s left join s.serviceSubscriptionCharges c where c.chargeTemplate=:chargeTemplate"),
//@NamedQuery(name = "serviceTemplate.getServicesWithTerminationsByChargeTemplate", 
//				query = "from ServiceTemplate s left join s.serviceTerminationCharges c where c.chargeTemplate=:chargeTemplate"),
//@NamedQuery(name = "serviceTemplate.getServicesWithUsagesByChargeTemplate", 
//				query = "from ServiceTemplate s left join s.serviceUsageCharges c where c.chargeTemplate=:chargeTemplate")
})
public class ServiceTemplate extends BusinessCFEntity implements IImageUpload {

	private static final long serialVersionUID = 1L;

	@OneToMany(mappedBy = "serviceTemplate", fetch = FetchType.LAZY,cascade=CascadeType.ALL)
	private List<ServiceChargeTemplateRecurring> serviceRecurringCharges = new ArrayList<ServiceChargeTemplateRecurring>();

	@OneToMany(mappedBy = "serviceTemplate", fetch = FetchType.LAZY,cascade=CascadeType.ALL)
	private List<ServiceChargeTemplateSubscription> serviceSubscriptionCharges = new ArrayList<ServiceChargeTemplateSubscription>();

	@OneToMany(mappedBy = "serviceTemplate", fetch = FetchType.LAZY,cascade=CascadeType.ALL)
	private List<ServiceChargeTemplateTermination> serviceTerminationCharges = new ArrayList<ServiceChargeTemplateTermination>();

	@OneToMany(mappedBy = "serviceTemplate", fetch = FetchType.LAZY,cascade=CascadeType.ALL)
	private List<ServiceChargeTemplateUsage> serviceUsageCharges = new ArrayList<ServiceChargeTemplateUsage>();

	@ManyToOne
	@JoinColumn(name = "invoicing_calendar_id")
	private Calendar invoicingCalendar;
	
	@ManyToOne
	@JoinColumn(name = "business_service_model_id")
	private BusinessServiceModel businessServiceModel;

	@Size(max = 2000)
	@Column(name = "long_description", columnDefinition = "TEXT")
	private String longDescription;
	
	@ImageType
	@Column(name = "image_path", length = 100)
	@Size(max = 100)
    private String imagePath;
	
	@Transient
	private boolean selected;
	
	@Transient
	private boolean duplicate;

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

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof ServiceTemplate)) {
            return false;
        }
        
        ServiceTemplate other = (ServiceTemplate) obj;
		if (code == null) {
			if (other.getCode() != null)
				return false;
		} else if (!code.equals(other.getCode()))
			return false;
		return true;
	}

	public Calendar getInvoicingCalendar() {
		return invoicingCalendar;
	}

	public void setInvoicingCalendar(Calendar invoicingCalendar) {
		this.invoicingCalendar = invoicingCalendar;
	}

	public BusinessServiceModel getBusinessServiceModel() {
		return businessServiceModel;
	}

	public void setBusinessServiceModel(BusinessServiceModel businessServiceModel) {
		this.businessServiceModel = businessServiceModel;
	}

	public String getLongDescription() {
		return longDescription;
	}
    
	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public boolean isDuplicate() {
		return duplicate;
	}

	public void setDuplicate(boolean duplicate) {
		this.duplicate = duplicate;
	}


}