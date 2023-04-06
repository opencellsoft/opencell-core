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

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
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
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.service.base.BusinessService;

@Stateless
public class ChargeInstanceService<P extends ChargeInstance> extends BusinessService<P> {

    @Inject
    private RecurringChargeInstanceService recurringChargeInstanceService;

    @Inject
    private UsageChargeInstanceService usageChargeInstanceService;

    @Inject
    private OneShotChargeInstanceService oneShotChargeInstanceService;

    /**
     * Get Charge instance of a given code that belongs to a given subscription
     * 
     * @param code Charge code
     * @param subscription Subscription
     * @param status Charge status - optional
     * @return A Charge instance entity
     * @throws BusinessException
     */
    @SuppressWarnings("unchecked")
    public P findByCodeAndSubscription(String code, Subscription subscription, InstanceStatusEnum status) throws BusinessException {
        QueryBuilder qb = new QueryBuilder(ChargeInstance.class, "c");
        qb.addCriterion("code", "=", code, true);
        qb.addCriterionEntity("subscription", subscription);
        if (status != null) {
            qb.addCriterionEntity("c.status", status);
        }

        try {
            return (P) qb.getQuery(getEntityManager()).getSingleResult();

        } catch (NonUniqueResultException e) {
            throw new BusinessException("More than one charge with code " + code + " found in subscription " + subscription.getId());

        } catch (NoResultException e) {
            log.warn("findByCodeAndService : no charges have been found");
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

        if (chargeInstance.getChargeMainType() == ChargeTemplate.ChargeMainTypeEnum.RECURRING) {
            RecurringChargeInstance recurringChargeInstance = recurringChargeInstanceService.findById(chargeInstance.getId());
            if (recurringChargeInstance != null) {
                return recurringChargeInstance.getServiceInstance();
            }

        } else if (chargeInstance.getChargeMainType() == ChargeTemplate.ChargeMainTypeEnum.USAGE) {
            UsageChargeInstance usageChargeInstance = usageChargeInstanceService.findById(chargeInstance.getId());
            if (usageChargeInstance != null) {
                return usageChargeInstance.getServiceInstance();
            }

        } else if (chargeInstance.getChargeMainType() == ChargeTemplate.ChargeMainTypeEnum.ONESHOT) {
            OneShotChargeInstance oneShotChargeInstance = oneShotChargeInstanceService.findById(chargeInstance.getId());
            if (oneShotChargeInstance != null) {
                return oneShotChargeInstance.getServiceInstance();
            }
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