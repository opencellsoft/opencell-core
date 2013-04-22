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
package org.meveo.admin.action.catalog;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.model.billing.InvoiceSubcategoryCountry;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.catalog.ServiceUsageChargeTemplate;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;
import org.meveo.service.catalog.impl.RecurringChargeTemplateService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.catalog.impl.ServiceUsageChargeTemplateService;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.model.DualListModel;

/**
 * Standard backing bean for {@link ServiceTemplate} (extends {@link BaseBean}
 * that provides almost all common methods to handle entities filtering/sorting
 * in datatable, their create, edit, view, delete operations). It works with
 * Manaty custom JSF components.
 * 
 * @author Ignas Lelys
 * @created Dec 7, 2010
 * 
 */
@Named
@ConversationScoped
public class ServiceTemplateBean extends BaseBean<ServiceTemplate> {

	private static final long serialVersionUID = 1L;
	
	 @Produces
	    @Named
	    private ServiceUsageChargeTemplate serviceUsageChargeTemplate = new ServiceUsageChargeTemplate();
	 
	 
	 public void newServiceUsageChargeTemplate() {
	        this.serviceUsageChargeTemplate = new ServiceUsageChargeTemplate();
	    }
	 
	    @Inject
	    private ServiceUsageChargeTemplateService serviceUsageChargeTemplateService;

	/**
	 * Injected
	 * 
	 * @{link ServiceTemplate} service. Extends {@link PersistenceService}.
	 */
	@Inject
	private ServiceTemplateService serviceTemplateService;

	@Inject
	private OneShotChargeTemplateService oneShotChargeTemplateService;

	@Inject
	private RecurringChargeTemplateService recurringChargeTemplateService;

	private DualListModel<RecurringChargeTemplate> recurringCharges;
	private DualListModel<OneShotChargeTemplate> subscriptionCharges;
	private DualListModel<OneShotChargeTemplate> terminationCharges;

	public DualListModel<OneShotChargeTemplate> getTerminationChargesModel() {
		if (terminationCharges == null) {
			List<OneShotChargeTemplate> source = oneShotChargeTemplateService.getTerminationChargeTemplates();
			List<OneShotChargeTemplate> target = new ArrayList<OneShotChargeTemplate>();
			if (getEntity().getTerminationCharges() != null) {
				target.addAll(getEntity().getTerminationCharges());
			}
			source.removeAll(target);
			terminationCharges = new DualListModel<OneShotChargeTemplate>(source, target);
		}
		return terminationCharges;
	}

	public void setTerminationChargesModel(DualListModel<OneShotChargeTemplate> temp) {
		getEntity().setSubscriptionCharges(temp.getTarget());
	}

	public DualListModel<OneShotChargeTemplate> getSubscriptionChargesModel() {
		if (subscriptionCharges == null) {
			List<OneShotChargeTemplate> source = oneShotChargeTemplateService.getSubscriptionChargeTemplates();
			List<OneShotChargeTemplate> target = new ArrayList<OneShotChargeTemplate>();
			if (getEntity().getSubscriptionCharges() != null) {
				target.addAll(getEntity().getSubscriptionCharges());
			}
			source.removeAll(target);
			subscriptionCharges = new DualListModel<OneShotChargeTemplate>(source, target);
		}
		return subscriptionCharges;
	}

	public void setSubscriptionChargesModel(DualListModel<OneShotChargeTemplate> temp) {
		getEntity().setSubscriptionCharges(temp.getTarget());
	}

	public DualListModel<RecurringChargeTemplate> getRecurringChargesModel() {
		if (recurringCharges == null) {
			List<RecurringChargeTemplate> source = recurringChargeTemplateService.list();
			List<RecurringChargeTemplate> target = new ArrayList<RecurringChargeTemplate>();
			if (getEntity().getRecurringCharges() != null) {
				target.addAll(getEntity().getRecurringCharges());
			}
			source.removeAll(target);
			recurringCharges = new DualListModel<RecurringChargeTemplate>(source, target);
		}
		return recurringCharges;
	}

	public void setRecurringChargesModel(DualListModel<RecurringChargeTemplate> temp) {
		getEntity().setRecurringCharges(temp.getTarget());
	}

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public ServiceTemplateBean() {
		super(ServiceTemplate.class);
	}

	@Override
	public DataTable search() {
		getFilters();
		if (!filters.containsKey("disabled")) {
			filters.put("disabled", false);
		}
		return super.search();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.meveo.admin.action.BaseBean#saveOrUpdate(boolean)
	 */
	@Override
	public String saveOrUpdate(boolean killConversation) {

		List<RecurringChargeTemplate> recurringCharges = entity.getRecurringCharges();
		for (RecurringChargeTemplate recurringCharge : recurringCharges) {
			if (!recurringCharge.getApplyInAdvance()) {
				break;
			}
		}

		return super.saveOrUpdate(killConversation);
	}
	
	
	  public void saveServiceUsageChargeTemplate() {
	        log.info("saveServiceUsageChargeTemplate getObjectId=#0", getObjectId());

	        try {
	            if (serviceUsageChargeTemplate != null) {
	                for (ServiceUsageChargeTemplate inc : entity.getServiceUsageCharges()) {
	                    if (inc.getChargeTemplate().getCode().equalsIgnoreCase(serviceUsageChargeTemplate.getChargeTemplate().getCode())
	                            && inc.getCounterTemplate().getCode().equalsIgnoreCase(serviceUsageChargeTemplate.getCounterTemplate().getCode())
	                            && !inc.getId().equals(serviceUsageChargeTemplate.getId())) {
	                        throw new Exception();
	                    }
	                }
	                if (serviceUsageChargeTemplate.getId() != null) {
	                	serviceUsageChargeTemplateService.update(serviceUsageChargeTemplate);
	                    messages.info(new BundleKey("messages", "update.successful"));
	                } else {
	                	serviceUsageChargeTemplate.setServiceTemplate(entity);
	                	serviceUsageChargeTemplateService.create(serviceUsageChargeTemplate);
	                    entity.getServiceUsageCharges().add(serviceUsageChargeTemplate);
	                    messages.info(new BundleKey("messages", "save.successful"));
	                }
	            }
	        } catch (Exception e) {
	            log.error("exception when applying one serviceUsageChargeTemplate !", e);
	            messages.error(new BundleKey("messages", "serviceTemplate.uniqueUsageCounterFlied"));
	        }
	        serviceUsageChargeTemplate = new ServiceUsageChargeTemplate();
	    }
	
	
	  public void deleteServiceUsageChargeTemplate(ServiceUsageChargeTemplate serviceUsageChargeTemplate) {
		  serviceUsageChargeTemplateService.remove(serviceUsageChargeTemplate);
	    	entity.getServiceUsageCharges().remove(serviceUsageChargeTemplate);
	    }
	
	 public void editServiceUsageChargeTemplate(ServiceUsageChargeTemplate serviceUsageChargeTemplate) {
	        this.serviceUsageChargeTemplate = serviceUsageChargeTemplate;
	    }

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<ServiceTemplate> getPersistenceService() {
		return serviceTemplateService;
	}

	public DualListModel<RecurringChargeTemplate> getRecurringCharges() {
		return recurringCharges;
	}

	public void setRecurringCharges(DualListModel<RecurringChargeTemplate> recurringCharges) {
		this.recurringCharges = recurringCharges;
	}

	public DualListModel<OneShotChargeTemplate> getSubscriptionCharges() {
		return subscriptionCharges;
	}

	public void setSubscriptionCharges(DualListModel<OneShotChargeTemplate> subscriptionCharges) {
		this.subscriptionCharges = subscriptionCharges;
	}

	public DualListModel<OneShotChargeTemplate> getTerminationCharges() {
		return terminationCharges;
	}

	public void setTerminationCharges(DualListModel<OneShotChargeTemplate> terminationCharges) {
		this.terminationCharges = terminationCharges;
	}

	// /**
	// * @see org.meveo.admin.action.BaseBean#getListFieldsToFetch()
	// */
	// protected List<String> getListFieldsToFetch() {
	// return Arrays.asList("recurringCharges", "subscriptionCharges",
	// "terminationCharges", "durationTermCalendar");
	// }
	//
	// /**
	// * @see org.meveo.admin.action.BaseBean#getFormFieldsToFetch()
	// */
	// protected List<String> getFormFieldsToFetch() {
	// return Arrays.asList("recurringCharges", "subscriptionCharges",
	// "terminationCharges", "durationTermCalendar");
	// }

}
