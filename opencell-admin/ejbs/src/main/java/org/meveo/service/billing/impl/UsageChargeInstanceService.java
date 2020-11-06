/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.service.billing.impl;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.cache.WalletCacheContainerProvider;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.BillingWalletTypeEnum;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.UsageChargeInstance;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.catalog.ServiceChargeTemplateUsage;
import org.meveo.model.catalog.WalletTemplate;
import org.meveo.service.base.BusinessService;

/**
 * @author khalid HORRI
 * @lastModifiedVersion 6.1
 */
@Stateless
public class UsageChargeInstanceService extends BusinessService<UsageChargeInstance> {

    @Inject
    private WalletService walletService;

    @Inject
    private CounterInstanceService counterInstanceService;

    @Inject
    private WalletCacheContainerProvider walletCacheContainerProvider;

    public UsageChargeInstance usageChargeInstanciation(ServiceInstance serviceInstance, ServiceChargeTemplateUsage serviceUsageChargeTemplate, boolean isVirtual) throws BusinessException {

        log.debug("Instanciate usage charge for code {} and subscription {}", serviceUsageChargeTemplate.getChargeTemplate().getCode(), serviceInstance.getSubscription().getCode());

        UsageChargeInstance usageChargeInstance = new UsageChargeInstance(null, null, serviceUsageChargeTemplate.getChargeTemplate(), serviceInstance, InstanceStatusEnum.INACTIVE);

        List<WalletTemplate> walletTemplates = serviceUsageChargeTemplate.getWalletTemplates();
        log.debug("usage charge wallet templates {}, by default we set it to postpaid", walletTemplates);
        usageChargeInstance.setPrepaid(false);
        if (walletTemplates != null && walletTemplates.size() > 0) {
            for (WalletTemplate walletTemplate : walletTemplates) {
                if (walletTemplate.getWalletType() == BillingWalletTypeEnum.PREPAID) {
                    usageChargeInstance.setPrepaid(true);
                }
                WalletInstance walletInstance = walletService.getWalletInstance(serviceInstance.getSubscription().getUserAccount(), walletTemplate, isVirtual);
                log.debug("Added the walletInstance {} to the charge instance {}", walletInstance.getId(), usageChargeInstance.getId());
                usageChargeInstance.getWalletInstances().add(walletInstance);
            }
        } else {
            usageChargeInstance.getWalletInstances().add(serviceInstance.getSubscription().getUserAccount().getWallet());
        }

        if (!isVirtual) {
            create(usageChargeInstance);
        }

        if ((serviceUsageChargeTemplate.getAccumulatorCounterTemplates() != null && !serviceUsageChargeTemplate.getAccumulatorCounterTemplates().isEmpty()) || serviceUsageChargeTemplate.getCounterTemplate() != null) {
            for (CounterTemplate counterTemplate : serviceUsageChargeTemplate.getAccumulatorCounterTemplates()) {
                CounterInstance counterInstance = counterInstanceService.counterInstanciation(serviceInstance, counterTemplate, isVirtual);
                log.debug("Accumulator counter instance {} will be added to charge instance {}", counterInstance, usageChargeInstance);
                usageChargeInstance.addCounterInstance(counterInstance);
            }
            if (serviceUsageChargeTemplate.getCounterTemplate() != null) {
                CounterInstance counterInstance = counterInstanceService.counterInstanciation(serviceInstance, serviceUsageChargeTemplate.getCounterTemplate(), isVirtual);
                log.debug("Counter instance {} will be added to charge instance {}", counterInstance, usageChargeInstance);
                usageChargeInstance.setCounter(counterInstance);
            }
            if (!isVirtual) {
                update(usageChargeInstance);
            }
        }

        return usageChargeInstance;
    }

    public UsageChargeInstance activateUsageChargeInstance(UsageChargeInstance usageChargeInstance) throws BusinessException {
        usageChargeInstance.setChargeDate(usageChargeInstance.getServiceInstance().getSubscriptionDate());
        usageChargeInstance.setStatus(InstanceStatusEnum.ACTIVE);
        usageChargeInstance = update(usageChargeInstance);

        if (usageChargeInstance.getPrepaid()) {
            walletCacheContainerProvider.addChargeInstance(usageChargeInstance);
        }
        return usageChargeInstance;
    }

    public UsageChargeInstance terminateUsageChargeInstance(UsageChargeInstance usageChargeInstance, Date terminationDate) throws BusinessException {
        usageChargeInstance.setTerminationDate(terminationDate);
        usageChargeInstance.setStatus(InstanceStatusEnum.TERMINATED);
        usageChargeInstance = update(usageChargeInstance);
        return usageChargeInstance;
    }

    public UsageChargeInstance suspendUsageChargeInstance(UsageChargeInstance usageChargeInstance, Date suspensionDate) throws BusinessException {
        usageChargeInstance.setTerminationDate(suspensionDate);
        usageChargeInstance.setStatus(InstanceStatusEnum.SUSPENDED);
        usageChargeInstance = update(usageChargeInstance);
        return usageChargeInstance;
    }

    public UsageChargeInstance reactivateUsageChargeInstance(UsageChargeInstance usageChargeInstance, Date reactivationDate) throws BusinessException {
        usageChargeInstance.setChargeDate(reactivationDate);
        usageChargeInstance.setTerminationDate(null);
        usageChargeInstance.setStatus(InstanceStatusEnum.ACTIVE);
        usageChargeInstance = update(usageChargeInstance);
        return usageChargeInstance;
    }

    @SuppressWarnings("unchecked")
    public List<UsageChargeInstance> findUsageChargeInstanceBySubscriptionId(Long subscriptionId) {
        QueryBuilder qb = new QueryBuilder(UsageChargeInstance.class, "c");
        qb.addCriterion("c.subscription.id", "=", subscriptionId, true);
        return qb.getQuery(getEntityManager()).getResultList();
    }

    /**
     * Get a list of active usage charge instances for a given subscription
     *
     * @param subscriptionId Subscription identifier
     * @return An ordered list by priority (ascended) of usage charge instances
     */
    public List<UsageChargeInstance> getActiveUsageChargeInstancesBySubscriptionId(Long subscriptionId) {
        if (subscriptionId == null) {
            return getEntityManager().createNamedQuery("UsageChargeInstance.getActiveUsageCharges", UsageChargeInstance.class).getResultList();
        }
        return getEntityManager().createNamedQuery("UsageChargeInstance.getActiveUsageChargesBySubscriptionId", UsageChargeInstance.class).setParameter("subscriptionId", subscriptionId).getResultList();
    }

    /**
     * Get a list of usage charge instances valid for a given subscription and a consumption date
     *
     * @param subscriptionId Subscription identifier
     * @param consumptionDate
     * @return An ordered list by priority (ascended) of usage charge instances
     */
    public List<UsageChargeInstance> getUsageChargeInstancesValidForDateBySubscriptionId(Long subscriptionId, Object consumptionDate) {
        return getEntityManager().createNamedQuery("UsageChargeInstance.getUsageChargesValidesForDateBySubscription", UsageChargeInstance.class).setParameter("subscriptionId", subscriptionId)
            .setParameter("terminationDate", consumptionDate).getResultList();
    }
}