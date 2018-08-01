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
package org.meveo.admin.action.catalog;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.Produces;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.ServiceChargeTemplateRecurring;
import org.meveo.model.catalog.ServiceChargeTemplateSubscription;
import org.meveo.model.catalog.ServiceChargeTemplateTermination;
import org.meveo.model.catalog.ServiceChargeTemplateUsage;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.catalog.WalletTemplate;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.WalletTemplateService;
import org.meveo.service.catalog.impl.RecurringChargeTemplateService;
import org.meveo.service.catalog.impl.ServiceChargeTemplateRecurringService;
import org.meveo.service.catalog.impl.ServiceChargeTemplateSubscriptionService;
import org.meveo.service.catalog.impl.ServiceChargeTemplateTerminationService;
import org.meveo.service.catalog.impl.ServiceChargeTemplateUsageService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.primefaces.model.DualListModel;

@Named
@ViewScoped
public class ServiceTemplateBean extends CustomFieldBean<ServiceTemplate> {

    private static final long serialVersionUID = 1L;

    @Inject
    private ServiceInstanceService serviceInstanceService;

    @Inject
    protected CustomFieldInstanceService customFieldInstanceService;

    @Inject
    private ServiceTemplateService serviceTemplateService;

    @Inject
    private WalletTemplateService walletTemplateService;

    @Inject
    private ServiceChargeTemplateSubscriptionService serviceChargeTemplateSubscriptionService;
    @Inject
    private ServiceChargeTemplateTerminationService serviceChargeTemplateTerminationService;
    @Inject
    private ServiceChargeTemplateRecurringService serviceChargeTemplateRecurringService;
    @Inject
    private ServiceChargeTemplateUsageService serviceChargeTemplateUsageService;
    
    @Inject
    private  RecurringChargeTemplateService recurringChargeTemplateService;

    private DualListModel<WalletTemplate> usageWallets;
    private DualListModel<WalletTemplate> recurringWallets;
    private DualListModel<WalletTemplate> subscriptionWallets;
    private DualListModel<WalletTemplate> terminationWallets;

    private ServiceChargeTemplateRecurring serviceChargeTemplateRecurring = new ServiceChargeTemplateRecurring();

    public ServiceChargeTemplateRecurring getServiceChargeTemplateRecurring() {
        return serviceChargeTemplateRecurring;
    }

    public void setServiceChargeTemplateRecurring(ServiceChargeTemplateRecurring serviceChargeTemplateRecurring) {
        this.serviceChargeTemplateRecurring = serviceChargeTemplateRecurring;
    }

    public void newServiceChargeTemplateRecurring() {
        this.serviceChargeTemplateRecurring = new ServiceChargeTemplateRecurring();
        this.recurringWallets = null;
    }

    private ServiceChargeTemplateSubscription serviceChargeTemplateSubscription = new ServiceChargeTemplateSubscription();

    public ServiceChargeTemplateSubscription getServiceChargeTemplateSubscription() {
        return serviceChargeTemplateSubscription;
    }

    public void setServiceChargeTemplateSubscription(ServiceChargeTemplateSubscription serviceChargeTemplateSubscription) {
        this.serviceChargeTemplateSubscription = serviceChargeTemplateSubscription;
    }

    public void newServiceChargeTemplateSubscription() {
        this.serviceChargeTemplateSubscription = new ServiceChargeTemplateSubscription();
        this.subscriptionWallets = null;
    }

    private ServiceChargeTemplateTermination serviceChargeTemplateTermination = new ServiceChargeTemplateTermination();

    public ServiceChargeTemplateTermination getServiceChargeTemplateTermination() {
        return serviceChargeTemplateTermination;
    }

    public void setServiceChargeTemplateTermination(ServiceChargeTemplateTermination serviceChargeTemplateTermination) {
        this.serviceChargeTemplateTermination = serviceChargeTemplateTermination;
    }

    public void newServiceChargeTemplateTermination() {
        this.serviceChargeTemplateTermination = new ServiceChargeTemplateTermination();
        this.terminationWallets = null;
    }

    @Produces
    @Named
    private ServiceChargeTemplateUsage serviceChargeTemplateUsage = new ServiceChargeTemplateUsage();

    public void newServiceChargeTemplateUsage() {
        this.serviceChargeTemplateUsage = new ServiceChargeTemplateUsage();
        this.usageWallets = null;
    }

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public ServiceTemplateBean() {
        super(ServiceTemplate.class);
    }

    public DualListModel<WalletTemplate> getUsageDualListModel() {
        if (usageWallets == null) {
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
        }
        return usageWallets;
    }

    public void setUsageDualListModel(DualListModel<WalletTemplate> perks) {
        this.usageWallets = perks;
    }

    public DualListModel<WalletTemplate> getSubscriptionDualListModel() {
        if (subscriptionWallets == null) {
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
        }
        return subscriptionWallets;
    }

    public void setSubscriptionDualListModel(DualListModel<WalletTemplate> perks) {
        this.subscriptionWallets = perks;
    }

    public DualListModel<WalletTemplate> getTerminationDualListModel() {
        if (terminationWallets == null) {
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
        }
        return terminationWallets;
    }

    public void setTerminationDualListModel(DualListModel<WalletTemplate> perks) {
        this.terminationWallets = perks;
    }

    public DualListModel<WalletTemplate> getRecurringDualListModel() {
        if (recurringWallets == null) {
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
        }
        return recurringWallets;
    }

    public void setRecurringDualListModel(DualListModel<WalletTemplate> perks) {
        this.recurringWallets = perks;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.meveo.admin.action.BaseBean#saveOrUpdate(boolean)
     */
    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {
        List<ServiceChargeTemplateRecurring> recurringCharges = entity.getServiceRecurringCharges();
        for (ServiceChargeTemplateRecurring recurringCharge : recurringCharges) {
            boolean isApplyInAdvance = recurringCharge.getChargeTemplate().getApplyInAdvance() == null ? false : recurringCharge.getChargeTemplate().getApplyInAdvance();
            if(!StringUtils.isBlank(recurringCharge.getChargeTemplate().getApplyInAdvanceEl())) {
                isApplyInAdvance = recurringChargeTemplateService.matchExpression(recurringCharge.getChargeTemplate().getApplyInAdvanceEl(), null,entity, recurringCharge.getChargeTemplate());
            }
            if (!isApplyInAdvance) {
                break;
            }
        }
        boolean newEntity = (entity.getId() == null);

        String outcome = super.saveOrUpdate(killConversation);

        if (outcome != null) {
            return newEntity ? getEditViewName() : outcome;
        }
        return null;
    }

    public void saveServiceChargeTemplateSubscription() {
        log.info("saveServiceChargeTemplateSubscription getObjectId=#0", getObjectId());

        try {
            if (serviceChargeTemplateSubscription == null) {
                return;
            }
            for (ServiceChargeTemplateSubscription inc : entity.getServiceSubscriptionCharges()) {
                if (inc.getChargeTemplate().getCode().equalsIgnoreCase(serviceChargeTemplateSubscription.getChargeTemplate().getCode())
                        && !inc.getId().equals(serviceChargeTemplateSubscription.getId())) {
                    throw new Exception();
                }
            }

            if (serviceChargeTemplateSubscription.getWalletTemplates() == null) {
                serviceChargeTemplateSubscription.setWalletTemplates(new ArrayList<WalletTemplate>());
            } else {
                serviceChargeTemplateSubscription.getWalletTemplates().clear();
            }
            serviceChargeTemplateSubscription.getWalletTemplates().addAll(walletTemplateService.refreshOrRetrieve(subscriptionWallets.getTarget()));

            if (serviceChargeTemplateSubscription.getId() != null) {
                serviceChargeTemplateSubscriptionService.update(serviceChargeTemplateSubscription);
                entity = getPersistenceService().refreshOrRetrieve(entity); // TODO this line might cause an issue when after update of charge template service template can not be
                                                                            // saved
                messages.info(new BundleKey("messages", "update.successful"));
            } else {
                serviceChargeTemplateSubscription.setServiceTemplate(entity);
                serviceChargeTemplateSubscriptionService.create(serviceChargeTemplateSubscription);
                entity.getServiceSubscriptionCharges().add(serviceChargeTemplateSubscription);
                messages.info(new BundleKey("messages", "save.successful"));
            }
        } catch (Exception e) {
            log.error("exception when applying one serviceUsageChargeTemplate !", e);
            messages.error(new BundleKey("messages", "serviceTemplate.uniqueUsageCounterFlied"));
        }

        newServiceChargeTemplateSubscription();
    }

    public void deleteServiceSubscriptionChargeTemplate(Long id) throws BusinessException {
        ServiceChargeTemplateSubscription subscription = serviceChargeTemplateSubscriptionService.findById(id);
        entity.getServiceSubscriptionCharges().remove(subscription);
        entity = getPersistenceService().update(entity);
        serviceChargeTemplateSubscriptionService.remove(subscription);
        messages.info(new BundleKey("messages", "delete.successful"));
    }

    public void editServiceSubscriptionChargeTemplate(ServiceChargeTemplateSubscription serviceSubscriptionChargeTemplate) {
        this.serviceChargeTemplateSubscription = serviceSubscriptionChargeTemplate;
        this.subscriptionWallets = null;
    }

    public void saveServiceChargeTemplateTermination() {
        log.info("saveServiceChargeTemplateTermination getObjectId=#0", getObjectId());

        try {
            if (serviceChargeTemplateTermination == null) {
                return;
            }
            for (ServiceChargeTemplateTermination inc : entity.getServiceTerminationCharges()) {
                if (inc.getChargeTemplate().getCode().equalsIgnoreCase(serviceChargeTemplateTermination.getChargeTemplate().getCode())
                        && !inc.getId().equals(serviceChargeTemplateTermination.getId())) {
                    throw new Exception();
                }
            }

            if (serviceChargeTemplateTermination.getWalletTemplates() == null) {
                serviceChargeTemplateTermination.setWalletTemplates(new ArrayList<WalletTemplate>());
            } else {
                serviceChargeTemplateTermination.getWalletTemplates().clear();
            }
            serviceChargeTemplateTermination.getWalletTemplates().addAll(walletTemplateService.refreshOrRetrieve(terminationWallets.getTarget()));

            if (serviceChargeTemplateTermination.getId() != null) {
                serviceChargeTemplateTerminationService.update(serviceChargeTemplateTermination);
                entity = getPersistenceService().refreshOrRetrieve(entity); // TODO this line might cause an issue when after update of charge template service template can not be
                                                                            // saved
                messages.info(new BundleKey("messages", "update.successful"));
            } else {
                serviceChargeTemplateTermination.setServiceTemplate(entity);
                serviceChargeTemplateTerminationService.create(serviceChargeTemplateTermination);
                entity.getServiceTerminationCharges().add(serviceChargeTemplateTermination);
                messages.info(new BundleKey("messages", "save.successful"));
            }
        } catch (Exception e) {
            log.error("exception when applying one serviceUsageChargeTemplate !", e);
            messages.error(new BundleKey("messages", "serviceTemplate.uniqueUsageCounterFlied"));
        }
        newServiceChargeTemplateTermination();
    }

    public void deleteServiceTerminationChargeTemplate(Long id) throws BusinessException {
        ServiceChargeTemplateTermination termination = serviceChargeTemplateTerminationService.findById(id);
        entity.getServiceTerminationCharges().remove(termination);
        entity = getPersistenceService().update(entity);
        serviceChargeTemplateTerminationService.remove(termination);
        messages.info(new BundleKey("messages", "delete.successful"));
    }

    public void editServiceTerminationChargeTemplate(ServiceChargeTemplateTermination serviceTerminationChargeTemplate) {
        this.serviceChargeTemplateTermination = serviceTerminationChargeTemplate;
        this.terminationWallets = null;
    }

    public void saveServiceChargeTemplateRecurring() {
        log.info("saveServiceChargeTemplateRecurring getObjectId=#0", getObjectId());

        try {
            if (serviceChargeTemplateRecurring == null) {
                return;
            }
            for (ServiceChargeTemplateRecurring inc : entity.getServiceRecurringCharges()) {
                if (inc.getChargeTemplate().getCode().equalsIgnoreCase(serviceChargeTemplateRecurring.getChargeTemplate().getCode())
                        && !inc.getId().equals(serviceChargeTemplateRecurring.getId())) {
                    throw new Exception();
                }
            }

            if (serviceChargeTemplateRecurring.getWalletTemplates() == null) {
                serviceChargeTemplateRecurring.setWalletTemplates(new ArrayList<WalletTemplate>());
            } else {
                serviceChargeTemplateRecurring.getWalletTemplates().clear();
            }
            serviceChargeTemplateRecurring.getWalletTemplates().addAll(walletTemplateService.refreshOrRetrieve(recurringWallets.getTarget()));

            if (serviceChargeTemplateRecurring.getId() != null) {
                serviceChargeTemplateRecurringService.update(serviceChargeTemplateRecurring);
                entity = getPersistenceService().refreshOrRetrieve(entity); // TODO this line might cause an issue when after update of charge template service template can not be
                                                                            // saved
                messages.info(new BundleKey("messages", "update.successful"));
            } else {
                serviceChargeTemplateRecurring.setServiceTemplate(entity);
                serviceChargeTemplateRecurringService.create(serviceChargeTemplateRecurring);
                entity.getServiceRecurringCharges().add(serviceChargeTemplateRecurring);
                messages.info(new BundleKey("messages", "save.successful"));
            }
        } catch (Exception e) {
            log.error("exception when applying one serviceUsageChargeTemplate !", e);
            messages.error(new BundleKey("messages", "serviceTemplate.uniqueUsageCounterFlied"));
        }
        newServiceChargeTemplateRecurring();
    }

    public void deleteServiceRecurringChargeTemplate(Long id) throws BusinessException {
        ServiceChargeTemplateRecurring recurring = serviceChargeTemplateRecurringService.findById(id);
        entity.getServiceRecurringCharges().remove(recurring);
        entity = getPersistenceService().update(entity);
        serviceChargeTemplateRecurringService.remove(recurring);
        messages.info(new BundleKey("messages", "delete.successful"));
    }

    public void editServiceRecurringChargeTemplate(ServiceChargeTemplateRecurring serviceRecurringChargeTemplate) {
        this.serviceChargeTemplateRecurring = serviceRecurringChargeTemplate;
        this.recurringWallets = null;
    }

    public void saveServiceChargeTemplateUsage() {
        log.info("saveServiceChargeTemplateUsage getObjectId=" + getObjectId());

        try {
            if (serviceChargeTemplateUsage == null) {
                return;
            }
            for (ServiceChargeTemplateUsage inc : entity.getServiceUsageCharges()) {
                if (inc.getChargeTemplate().getCode().equalsIgnoreCase(serviceChargeTemplateUsage.getChargeTemplate().getCode())
                        && !inc.getId().equals(serviceChargeTemplateUsage.getId()) && ((inc.getCounterTemplate() == null && serviceChargeTemplateUsage.getCounterTemplate() == null)
                                || inc.getCounterTemplate().getCode().equalsIgnoreCase(serviceChargeTemplateUsage.getCounterTemplate().getCode()))) {
                    log.error("exception when applying one serviceUsageChargeTemplate !");
                    messages.error(new BundleKey("messages", "serviceTemplate.uniqueUsageCounterFlied"));
                    return;
                }
            }

            if (serviceChargeTemplateUsage.getWalletTemplates() == null) {
                serviceChargeTemplateUsage.setWalletTemplates(new ArrayList<WalletTemplate>());
            } else {
                serviceChargeTemplateUsage.getWalletTemplates().clear();
            }
            serviceChargeTemplateUsage.getWalletTemplates().addAll(walletTemplateService.refreshOrRetrieve(usageWallets.getTarget()));

            if (serviceChargeTemplateUsage.getId() != null) {
                serviceChargeTemplateUsageService.update(serviceChargeTemplateUsage);
                entity = getPersistenceService().refreshOrRetrieve(entity); // TODO this line might cause an issue when after update of charge template service template can not be
                                                                            // saved
                messages.info(new BundleKey("messages", "update.successful"));
            } else {
                serviceChargeTemplateUsage.setServiceTemplate(entity);
                serviceChargeTemplateUsageService.create(serviceChargeTemplateUsage);
                entity.getServiceUsageCharges().add(serviceChargeTemplateUsage);
                messages.info(new BundleKey("messages", "save.successful"));
            }
        } catch (Exception e) {
            log.error("exception when applying one serviceUsageChargeTemplate !", e);
            messages.error(new BundleKey("messages", "serviceTemplate.uniqueUsageCounterFlied"));
        }
        newServiceChargeTemplateUsage();
    }

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     * 
     * @param id usage charge identifier
     * @throws BusinessException General business exception
     */

    public void deleteServiceUsageChargeTemplate(Long id) throws BusinessException {
        ServiceChargeTemplateUsage usage = serviceChargeTemplateUsageService.findById(id);
        entity.getServiceUsageCharges().remove(usage);
        entity = getPersistenceService().update(entity);
        serviceChargeTemplateUsageService.remove(usage);
        messages.info(new BundleKey("messages", "delete.successful"));
    }

    public void editServiceUsageChargeTemplate(ServiceChargeTemplateUsage serviceUsageChargeTemplate) {
        this.serviceChargeTemplateUsage = serviceUsageChargeTemplate;
        this.usageWallets = null;
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

    @ActionMethod
    public void duplicate() {

        if (entity != null && entity.getId() != null) {
            try {
                serviceTemplateService.duplicate(entity);
                messages.info(new BundleKey("messages", "duplicate.successfull"));
            } catch (BusinessException e) {
                log.error("Error encountered duplicating service template entity: {}", entity.getCode(), e);
                messages.error(new BundleKey("messages", "error.duplicate.unexpected"));
            }
        }
    }

    public boolean isUsedInSubscription() {
        if (getEntity() == null || getEntity().isTransient()) {
            return false;
        }

        return serviceInstanceService.hasInstances(entity, null);
    }
}