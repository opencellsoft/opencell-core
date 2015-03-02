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
package org.meveo.admin.action.catalog;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.CustomFieldEnabledBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.catalog.ServiceChargeTemplateRecurring;
import org.meveo.model.catalog.ServiceChargeTemplateSubscription;
import org.meveo.model.catalog.ServiceChargeTemplateTermination;
import org.meveo.model.catalog.ServiceChargeTemplateUsage;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.catalog.WalletTemplate;
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.WalletTemplateService;
import org.meveo.service.catalog.impl.ServiceChargeTemplateRecurringService;
import org.meveo.service.catalog.impl.ServiceChargeTemplateSubscriptionService;
import org.meveo.service.catalog.impl.ServiceChargeTemplateTerminationService;
import org.meveo.service.catalog.impl.ServiceChargeTemplateUsageService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.model.DualListModel;

@Named
@ViewScoped
@CustomFieldEnabledBean(accountLevel = AccountLevelEnum.SERVICE)
public class ServiceTemplateBean extends BaseBean<ServiceTemplate> {

	private static final long serialVersionUID = 1L;

	@Produces
	@Named
	private ServiceChargeTemplateRecurring serviceChargeTemplateRecurring = new ServiceChargeTemplateRecurring();

	public void newServiceChargeTemplateRecurring() {
		this.serviceChargeTemplateRecurring = new ServiceChargeTemplateRecurring();
	}

	@Produces
	@Named
	private ServiceChargeTemplateSubscription serviceChargeTemplateSubscription = new ServiceChargeTemplateSubscription();

	public void newServiceChargeTemplateSubscription() {
		this.serviceChargeTemplateSubscription = new ServiceChargeTemplateSubscription();
	}

	@Produces
	@Named
	private ServiceChargeTemplateTermination serviceChargeTemplateTermination = new ServiceChargeTemplateTermination();

	public void newServiceChargeTemplateTermination() {
		this.serviceChargeTemplateTermination = new ServiceChargeTemplateTermination();
	}

	@Produces
	@Named
	private ServiceChargeTemplateUsage serviceChargeTemplateUsage = new ServiceChargeTemplateUsage();

	public void newServiceChargeTemplateUsage() {
		this.serviceChargeTemplateUsage = new ServiceChargeTemplateUsage();
		serviceChargeTemplateUsage.setProvider(getCurrentProvider());
	}

	@Inject
	private ServiceChargeTemplateSubscriptionService serviceChargeTemplateSubscriptionService;
	@Inject
	private ServiceChargeTemplateTerminationService serviceChargeTemplateTerminationService;
	@Inject
	private ServiceChargeTemplateRecurringService serviceChargeTemplateRecurringService;
	@Inject
	private ServiceChargeTemplateUsageService serviceChargeTemplateUsageService;

	/**
	 * Injected
	 * 
	 * @{link ServiceTemplate} service. Extends {@link PersistenceService}.
	 */
	@Inject
	private ServiceTemplateService serviceTemplateService;

	@Inject
	private WalletTemplateService walletTemplateService;

	private DualListModel<WalletTemplate> usageWallets;
	private DualListModel<WalletTemplate> recurringWallets;
	private DualListModel<WalletTemplate> subscriptionWallets;
	private DualListModel<WalletTemplate> terminationWallets;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public ServiceTemplateBean() {
		super(ServiceTemplate.class);
	}

	public DualListModel<WalletTemplate> getUsageDualListModel() {
		List<WalletTemplate> perksSource = walletTemplateService.list();
		List<WalletTemplate> perksTarget = new ArrayList<WalletTemplate>();
		if (getEntity().getServiceUsageCharges().size() > 0) {
			List<WalletTemplate> walletTemplates = serviceChargeTemplateUsage.getWalletTemplates();
			if (walletTemplates != null) {
				perksTarget.addAll(walletTemplates);
			}
		}
		perksSource.removeAll(perksTarget);
		usageWallets = new DualListModel<WalletTemplate>(perksSource, perksTarget);

		return usageWallets;
	}

	public void setUsageDualListModel(DualListModel<WalletTemplate> perks) {
		serviceChargeTemplateUsage.setWalletTemplates((List<WalletTemplate>) perks.getTarget());
	}

	public DualListModel<WalletTemplate> getSubscriptionDualListModel() {
		List<WalletTemplate> perksSource = walletTemplateService.list();
		List<WalletTemplate> perksTarget = new ArrayList<WalletTemplate>();
		if (getEntity().getServiceSubscriptionCharges().size() > 0) {
			List<WalletTemplate> walletTemplates = serviceChargeTemplateSubscription.getWalletTemplates();
			if (walletTemplates != null) {
				perksTarget.addAll(walletTemplates);
			}
		}
		perksSource.removeAll(perksTarget);
		subscriptionWallets = new DualListModel<WalletTemplate>(perksSource, perksTarget);
		return subscriptionWallets;
	}

	public void setSubscriptionDualListModel(DualListModel<WalletTemplate> perks) {
		serviceChargeTemplateSubscription.setWalletTemplates((List<WalletTemplate>) perks.getTarget());
	}

	public DualListModel<WalletTemplate> getTerminationDualListModel() {
		List<WalletTemplate> perksSource = walletTemplateService.list();
		List<WalletTemplate> perksTarget = new ArrayList<WalletTemplate>();
		if (getEntity().getServiceTerminationCharges().size() > 0) {
			List<WalletTemplate> walletTemplates = serviceChargeTemplateTermination.getWalletTemplates();
			if (walletTemplates != null) {
				perksTarget.addAll(walletTemplates);
			}
		}
		perksSource.removeAll(perksTarget);
		terminationWallets = new DualListModel<WalletTemplate>(perksSource, perksTarget);
		return terminationWallets;
	}

	public void setTerminationDualListModel(DualListModel<WalletTemplate> perks) {
		serviceChargeTemplateTermination.setWalletTemplates((List<WalletTemplate>) perks.getTarget());
	}

	public DualListModel<WalletTemplate> getRecurringDualListModel() {
		List<WalletTemplate> perksSource = walletTemplateService.list();
		List<WalletTemplate> perksTarget = new ArrayList<WalletTemplate>();
		if (getEntity().getServiceRecurringCharges().size() > 0) {
			List<WalletTemplate> walletTemplates = serviceChargeTemplateRecurring.getWalletTemplates();
			if (walletTemplates != null) {
				perksTarget.addAll(walletTemplates);
			}
		}
		perksSource.removeAll(perksTarget);
		recurringWallets = new DualListModel<WalletTemplate>(perksSource, perksTarget);
		return recurringWallets;
	}

	public void setRecurringDualListModel(DualListModel<WalletTemplate> perks) {
		serviceChargeTemplateRecurring.setWalletTemplates((List<WalletTemplate>) perks.getTarget());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.meveo.admin.action.BaseBean#saveOrUpdate(boolean)
	 */
	@Override
	public String saveOrUpdate(boolean killConversation) throws BusinessException {
		List<ServiceChargeTemplateRecurring> recurringCharges = entity.getServiceRecurringCharges();
		for (ServiceChargeTemplateRecurring recurringCharge : recurringCharges) {
			if (!recurringCharge.getChargeTemplate().getApplyInAdvance()) {
				break;
			}
		}
		boolean newEntity = (entity.getId() == null);
		String back = super.saveOrUpdate(killConversation);
		return newEntity ? null : back;
	}

	public void saveServiceChargeTemplateSubscription() {
		log.info("saveServiceChargeTemplateSubscription getObjectId=#0", getObjectId());

		try {
			if (serviceChargeTemplateSubscription != null) {
				for (ServiceChargeTemplateSubscription inc : entity.getServiceSubscriptionCharges()) {
					if (inc.getChargeTemplate().getCode()
							.equalsIgnoreCase(serviceChargeTemplateSubscription.getChargeTemplate().getCode())
							&& !inc.getId().equals(serviceChargeTemplateSubscription.getId())) {
						throw new Exception();
					}
				}
				if (serviceChargeTemplateSubscription.getId() != null) {
					serviceChargeTemplateSubscriptionService.update(serviceChargeTemplateSubscription);
					messages.info(new BundleKey("messages", "update.successful"));
				} else {
					serviceChargeTemplateSubscription.setServiceTemplate(entity);
					serviceChargeTemplateSubscriptionService.create(serviceChargeTemplateSubscription);
					entity.getServiceSubscriptionCharges().add(serviceChargeTemplateSubscription);
					messages.info(new BundleKey("messages", "save.successful"));
				}
			}
		} catch (Exception e) {
			log.error("exception when applying one serviceUsageChargeTemplate !", e);
			messages.error(new BundleKey("messages", "serviceTemplate.uniqueUsageCounterFlied"));
		}
		serviceChargeTemplateSubscription = new ServiceChargeTemplateSubscription();
	}

	public void deleteServiceSubscriptionChargeTemplate(
			ServiceChargeTemplateSubscription serviceSubscriptionChargeTemplate) {
		serviceChargeTemplateSubscriptionService.remove(serviceSubscriptionChargeTemplate);
		entity.getServiceSubscriptionCharges().remove(serviceSubscriptionChargeTemplate);
		messages.info(new BundleKey("messages", "delete.successful"));
	}

	public void editServiceSubscriptionChargeTemplate(
			ServiceChargeTemplateSubscription serviceSubscriptionChargeTemplate) {
		this.serviceChargeTemplateSubscription = serviceSubscriptionChargeTemplate;
	}

	public void saveServiceChargeTemplateTermination() {
		log.info("saveServiceChargeTemplateTermination getObjectId=#0", getObjectId());

		try {
			if (serviceChargeTemplateTermination != null) {
				for (ServiceChargeTemplateTermination inc : entity.getServiceTerminationCharges()) {
					if (inc.getChargeTemplate().getCode()
							.equalsIgnoreCase(serviceChargeTemplateTermination.getChargeTemplate().getCode())
							&& !inc.getId().equals(serviceChargeTemplateTermination.getId())) {
						throw new Exception();
					}
				}
				if (serviceChargeTemplateTermination.getId() != null) {
					serviceChargeTemplateTerminationService.update(serviceChargeTemplateTermination);
					messages.info(new BundleKey("messages", "update.successful"));
				} else {
					serviceChargeTemplateTermination.setServiceTemplate(entity);
					serviceChargeTemplateTerminationService.create(serviceChargeTemplateTermination);
					entity.getServiceTerminationCharges().add(serviceChargeTemplateTermination);
					messages.info(new BundleKey("messages", "save.successful"));
				}
			}
		} catch (Exception e) {
			log.error("exception when applying one serviceUsageChargeTemplate !", e);
			messages.error(new BundleKey("messages", "serviceTemplate.uniqueUsageCounterFlied"));
		}
		serviceChargeTemplateTermination = new ServiceChargeTemplateTermination();
	}

	public void deleteServiceTerminationChargeTemplate(ServiceChargeTemplateTermination serviceTerminationChargeTemplate) {
		serviceChargeTemplateTerminationService.remove(serviceTerminationChargeTemplate);
		entity.getServiceTerminationCharges().remove(serviceTerminationChargeTemplate);
		messages.info(new BundleKey("messages", "delete.successful"));
	}

	public void editServiceTerminationChargeTemplate(ServiceChargeTemplateTermination serviceTerminationChargeTemplate) {
		this.serviceChargeTemplateTermination = serviceTerminationChargeTemplate;
	}

	public void saveServiceChargeTemplateRecurring() {
		log.info("saveServiceChargeTemplateRecurring getObjectId=#0", getObjectId());

		try {
			if (serviceChargeTemplateRecurring != null) {
				for (ServiceChargeTemplateRecurring inc : entity.getServiceRecurringCharges()) {
					if (inc.getChargeTemplate().getCode()
							.equalsIgnoreCase(serviceChargeTemplateRecurring.getChargeTemplate().getCode())
							&& !inc.getId().equals(serviceChargeTemplateRecurring.getId())) {
						throw new Exception();
					}
				}
				if (serviceChargeTemplateRecurring.getId() != null) {
					serviceChargeTemplateRecurringService.update(serviceChargeTemplateRecurring);
					messages.info(new BundleKey("messages", "update.successful"));
				} else {
					serviceChargeTemplateRecurring.setServiceTemplate(entity);
					serviceChargeTemplateRecurringService.create(serviceChargeTemplateRecurring);
					entity.getServiceRecurringCharges().add(serviceChargeTemplateRecurring);
					messages.info(new BundleKey("messages", "save.successful"));
				}
			}
		} catch (Exception e) {
			log.error("exception when applying one serviceUsageChargeTemplate !", e);
			messages.error(new BundleKey("messages", "serviceTemplate.uniqueUsageCounterFlied"));
		}
		serviceChargeTemplateRecurring = new ServiceChargeTemplateRecurring();
	}

	public void deleteServiceRecurringChargeTemplate(ServiceChargeTemplateRecurring serviceRecurringChargeTemplate) {
		serviceChargeTemplateRecurringService.remove(serviceRecurringChargeTemplate);
		entity.getServiceRecurringCharges().remove(serviceRecurringChargeTemplate);
		messages.info(new BundleKey("messages", "delete.successful"));
	}

	public void editServiceRecurringChargeTemplate(ServiceChargeTemplateRecurring serviceRecurringChargeTemplate) {
		this.serviceChargeTemplateRecurring = serviceRecurringChargeTemplate;
	}

	public void saveServiceChargeTemplateUsage() {
		log.info("saveServiceChargeTemplateUsage getObjectId=" + getObjectId());

		try {
			if (serviceChargeTemplateUsage != null) {
				for (ServiceChargeTemplateUsage inc : entity.getServiceUsageCharges()) {
					if (inc.getChargeTemplate().getCode()
							.equalsIgnoreCase(serviceChargeTemplateUsage.getChargeTemplate().getCode())
							&& !inc.getId().equals(serviceChargeTemplateUsage.getId())
							&& ((inc.getCounterTemplate() == null && serviceChargeTemplateUsage.getCounterTemplate() == null) || inc
									.getCounterTemplate().getCode()
									.equalsIgnoreCase(serviceChargeTemplateUsage.getCounterTemplate().getCode()))) {
						log.error("exception when applying one serviceUsageChargeTemplate !");
						messages.error(new BundleKey("messages", "serviceTemplate.uniqueUsageCounterFlied"));
						return;
					}
				}
				if (serviceChargeTemplateUsage.getId() != null) {
					serviceChargeTemplateUsageService.update(serviceChargeTemplateUsage);
					messages.info(new BundleKey("messages", "update.successful"));
				} else {
					serviceChargeTemplateUsage.setServiceTemplate(entity);
					serviceChargeTemplateUsageService.create(serviceChargeTemplateUsage);
					entity.getServiceUsageCharges().add(serviceChargeTemplateUsage);
					messages.info(new BundleKey("messages", "save.successful"));
				}
			}
		} catch (Exception e) {
			log.error("exception when applying one serviceUsageChargeTemplate !", e);
			messages.error(new BundleKey("messages", "serviceTemplate.uniqueUsageCounterFlied"));
		}
		serviceChargeTemplateUsage = new ServiceChargeTemplateUsage();
	}

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */

	public void deleteServiceUsageChargeTemplate(ServiceChargeTemplateUsage serviceUsageChargeTemplate) {
		serviceChargeTemplateUsageService.remove(serviceUsageChargeTemplate);
		entity.getServiceUsageCharges().remove(serviceUsageChargeTemplate);
		messages.info(new BundleKey("messages", "delete.successful"));
	}

	public void editServiceUsageChargeTemplate(ServiceChargeTemplateUsage serviceUsageChargeTemplate) {
		this.serviceChargeTemplateUsage = serviceUsageChargeTemplate;
	}

	public ServiceChargeTemplateUsage getServiceChargeTemplateUsage() {
		return serviceChargeTemplateUsage;
	}

	public void setServiceChargeTemplateUsage(ServiceChargeTemplateUsage serviceChargeTemplateUsage) {
		this.serviceChargeTemplateUsage = serviceChargeTemplateUsage;
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<ServiceTemplate> getPersistenceService() {
		return serviceTemplateService;
	}

	@Override
	protected String getDefaultSort() {
		return "code";
	}

}
