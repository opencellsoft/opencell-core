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
package org.meveo.service.billing.impl;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.OneShotChargeInstance;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionStatusEnum;
import org.meveo.model.billing.UsageChargeInstance;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.service.base.BusinessService;

@Stateless
public class ChargeInstanceService<P extends ChargeInstance> extends BusinessService<P> {

    @EJB
    private RecurringChargeInstanceService recurringChargeInstanceService;

    @EJB
    private UsageChargeInstanceService usageChargeInstanceService;

    @EJB
    private OneShotChargeInstanceService oneShotChargeInstanceService;

    @SuppressWarnings("unchecked")
    public P findByCodeAndService(String code, Long subscriptionId) {
        P chargeInstance = null;
        try {
            log.debug("start of find {} by code (code={}) ..", "ChargeInstance", code);
            QueryBuilder qb = new QueryBuilder(ChargeInstance.class, "c");
            qb.addCriterion("c.code", "=", code, true);
            qb.addCriterion("c.subscription.id", "=", subscriptionId, true);
            chargeInstance = (P) qb.getQuery(getEntityManager()).getSingleResult();
            log.debug("end of find {} by code (code={}). Result found={}.", new Object[] { "ChargeInstance", code, chargeInstance != null });

        } catch (NoResultException nre) {
            log.warn("findByCodeAndService : no charges have been found");
        } catch (Exception e) {
            log.error("findByCodeAndService error={} ", e.getMessage());
        }
        return chargeInstance;
    }

    @SuppressWarnings("unchecked")
    public P findByCodeAndService(String code, Long subscriptionId, InstanceStatusEnum status) {
        P chargeInstance = null;
        try {
            log.debug("start of find {} by code (code={}) ..", "ChargeInstance", code);
            QueryBuilder qb = new QueryBuilder(ChargeInstance.class, "c");
            qb.addCriterion("c.code", "=", code, true);
            qb.addCriterion("c.subscription.id", "=", subscriptionId, true);
            qb.addCriterionEntity("c.status", status);
            chargeInstance = (P) qb.getQuery(getEntityManager()).getSingleResult();
            log.debug("end of find {} by code (code={}). Result found={}.", new Object[] { "ChargeInstance", code, chargeInstance != null });

        } catch (NoResultException nre) {
            log.warn("findByCodeAndService : no charges have been found");
        } catch (Exception e) {
            log.error("findByCodeAndService error={} ", e.getMessage());
        }
        return chargeInstance;
    }

    public RecurringChargeInstance recurringChargeInstanciation(ServiceInstance serviceInst, RecurringChargeTemplate recurringChargeTemplate, Date subscriptionDate, Seller seller)
            throws BusinessException {

        if (serviceInst == null) {
            throw new BusinessException("service instance does not exist.");
        }

        if (serviceInst.getStatus() == InstanceStatusEnum.CANCELED || serviceInst.getStatus() == InstanceStatusEnum.TERMINATED
                || serviceInst.getStatus() == InstanceStatusEnum.SUSPENDED) {
            throw new BusinessException("service instance is " + serviceInst.getStatus() + ". code=" + serviceInst.getCode());
        }
        String chargeCode = recurringChargeTemplate.getCode();
        RecurringChargeInstance chargeInst = (RecurringChargeInstance) recurringChargeInstanceService.findByCodeAndService(chargeCode, serviceInst.getId());

        if (chargeInst != null) {
            throw new BusinessException("charge instance code already exists. code=" + chargeCode);
        }

        RecurringChargeInstance chargeInstance = new RecurringChargeInstance();
        chargeInstance.setCode(chargeCode);
        chargeInstance.setDescription(recurringChargeTemplate.getDescription());
        chargeInstance.setStatus(InstanceStatusEnum.INACTIVE);
        chargeInstance.setChargeDate(subscriptionDate);
        chargeInstance.setSubscriptionDate(subscriptionDate);
        chargeInstance.setSubscription(serviceInst.getSubscription());
        chargeInstance.setChargeTemplate(recurringChargeTemplate);
        chargeInstance.setRecurringChargeTemplate(recurringChargeTemplate);
        chargeInstance.setServiceInstance(serviceInst);
        chargeInstance.setSeller(seller);
        chargeInstance.setCountry(serviceInst.getSubscription().getUserAccount().getBillingAccount().getTradingCountry());
        chargeInstance.setCurrency(serviceInst.getSubscription().getUserAccount().getBillingAccount().getCustomerAccount().getTradingCurrency());
        chargeInstance.setOrderNumber(serviceInst.getOrderNumber());

        recurringChargeInstanceService.create(chargeInstance);
        return chargeInstance;
    }

    public void recurringChargeDeactivation(long recurringChargeInstanId, Date terminationDate) throws BusinessException {

        RecurringChargeInstance recurringChargeInstance = recurringChargeInstanceService.findById(recurringChargeInstanId, true);

        log.debug("recurringChargeDeactivation : recurringChargeInstanceId={},ChargeApplications size={}", recurringChargeInstance.getId(),
            recurringChargeInstance.getWalletOperations().size());

        recurringChargeInstance.setStatus(InstanceStatusEnum.TERMINATED);

        // chargeApplicationService.cancelChargeApplications(recurringChargeInstanId,
        // null);

        recurringChargeInstanceService.update(recurringChargeInstance);

    }

    public void recurringChargeReactivation(ServiceInstance serviceInst, Subscription subscription, Date subscriptionDate) throws BusinessException {
        if (subscription.getStatus() == SubscriptionStatusEnum.RESILIATED || subscription.getStatus() == SubscriptionStatusEnum.CANCELED) {
            throw new BusinessException("subscription is " + subscription.getStatus());
        }

        if (serviceInst.getStatus() == InstanceStatusEnum.TERMINATED || serviceInst.getStatus() == InstanceStatusEnum.CANCELED
                || serviceInst.getStatus() == InstanceStatusEnum.SUSPENDED) {
            throw new BusinessException(
                "service instance is " + subscription.getStatus() + ". service Code=" + serviceInst.getCode() + ",subscription Code" + subscription.getCode());
        }

        for (RecurringChargeInstance recurringChargeInstance : serviceInst.getRecurringChargeInstances()) {
            recurringChargeInstance.setStatus(InstanceStatusEnum.ACTIVE);
            // recurringChargeInstance.setSubscriptionDate(subscriptionDate);
            recurringChargeInstance.setTerminationDate(null);
            recurringChargeInstance.setChargeDate(subscriptionDate);
            recurringChargeInstanceService.update(recurringChargeInstance);
        }
    }

    @SuppressWarnings("unchecked")
    public P findByCodeAndSubscription(String code, Subscription subscription) throws BusinessException {
        QueryBuilder qb = new QueryBuilder(ChargeInstance.class, "c");
        qb.addCriterion("code", "=", code, true);
        qb.addCriterionEntity("subscription", subscription);

        try {
            return (P) qb.getQuery(getEntityManager()).getSingleResult();

        } catch (NonUniqueResultException e) {
            throw new BusinessException("More than one charge with code " + code + " found in subscription " + subscription.getId());

        } catch (NoResultException e) {
            log.warn("failed to find By code and subscription", e);
            return null;
        }
    }

    /**
     * workround to find serviceInstance from chargeInstance , to avoid hibernate proxy cast
     * 
     * @param chargeInstance
     * @return
     */

    public ServiceInstance getServiceInstanceFromChargeInstance(ChargeInstance chargeInstance) {
        RecurringChargeInstance recurringChargeInstance = recurringChargeInstanceService.findById(chargeInstance.getId());
        if (recurringChargeInstance != null) {
            return recurringChargeInstance.getServiceInstance();
        }
        UsageChargeInstance usageChargeInstance = usageChargeInstanceService.findById(chargeInstance.getId());
        if (usageChargeInstance != null) {
            return usageChargeInstance.getServiceInstance();
        }
        OneShotChargeInstance oneShotChargeInstance = oneShotChargeInstanceService.findById(chargeInstance.getId());
        if (oneShotChargeInstance != null) {
            ServiceInstance serviceInstance = null;
            serviceInstance = oneShotChargeInstance.getSubscriptionServiceInstance();
            if (serviceInstance == null) {
                serviceInstance = oneShotChargeInstance.getTerminationServiceInstance();
            }
            return serviceInstance;
        }
        return null;
    }

}
