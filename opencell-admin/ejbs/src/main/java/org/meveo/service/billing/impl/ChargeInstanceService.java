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

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.OneShotChargeInstance;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.UsageChargeInstance;
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
     * workround to find serviceInstance from chargeInstance , to avoid hibernate proxy cast.
     * 
     * @param chargeInstance charge instance
     * @return service instance.
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

    /**
     * Get a list of prepaid and active usage charge instances to populate a cache
     * 
     * @return A list of prepaid and active usage charge instances
     */
    public List<ChargeInstance> getPrepaidChargeInstancesForCache() {
        return getEntityManager().createNamedQuery("ChargeInstance.listPrepaid", ChargeInstance.class).getResultList();
    }
}