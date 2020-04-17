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

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.ICounterEntity;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.billing.CounterPeriod;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.AccumulatorCounterTypeEnum;
import org.meveo.model.crm.Customer;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.base.PersistenceService;

/**
 * The CounterPeriod service class
 * 
 * @author Khalid HORRI
 * @lastModifiedVersion 9.0
 */

@Stateless
public class CounterPeriodService extends PersistenceService<CounterPeriod> {

    /**
     * Find an existing counter period matching a given date
     *
     * @param counterInstance Counter instance
     * @param date Date to match
     * @return Counter period
     * @throws BusinessException Business exception
     */
    public CounterPeriod getCounterPeriod(CounterInstance counterInstance, Date date) throws BusinessException {
        Query query = getEntityManager().createNamedQuery("CounterPeriod.findByPeriodDate");
        query.setParameter("counterInstance", counterInstance);
        query.setParameter("date", date, TemporalType.TIMESTAMP);
        try {
            return (CounterPeriod) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Return the counter value of a ICounterEntity UserAccount, BillingAccount, Subscription or ServiceInstance
     *
     * @param entity      the ICounterEntity
     * @param counterCode the counter code
     * @return the counter value.
     */
    public Object getCounterValue(ICounterEntity entity, String counterCode) {
        return getSingleCounterValue(entity, counterCode, null);
    }

    /**
     * Return the counter value of a ICounterEntity UserAccount, BillingAccount, Subscription or ServiceInstance where the startDate<=date<endDate
     *
     * @param entity      the ICounterEntity
     * @param counterCode the counter code
     * @param date        the date to be compared to start and end date of a CounterPeriod
     * @return the counter value.
     */
    public Object getCounterValueByDate(ICounterEntity entity, String counterCode, Date date) {
        return getSingleCounterValue(entity, counterCode, date);
    }

    /**
     * Gets the counter period value or a map of values if the counter is a multi values accumulator.
     *
     * @param entity      The account entity, can be a service instance, a subscription or an account level entity.
     * @param counterCode the counter code
     * @param date        a date that can be inculuded iin the counter period
     * @return the counter period value or Map of values for multi values accumulator.
     */
    private Object getSingleCounterValue(ICounterEntity entity, String counterCode, Date date) {
        Query query;
        query = getEntityManager().createNamedQuery("CounterPeriod.findByCounterEntityAndPeriodDate");
        if (date != null) {
            query.setParameter("date", date, TemporalType.TIMESTAMP);
        } else {
            query.setParameter("date", new Date(), TemporalType.TIMESTAMP);
        }
        query.setParameter("serviceInstance", null);
        query.setParameter("subscription", null);
        query.setParameter("billingAccount", null);
        query.setParameter("userAccount", null);
        query.setParameter("customerAccount", null);
        query.setParameter("customer", null);
        query.setParameter("counterCode", counterCode);

        if (entity instanceof ServiceInstance) {
            query.setParameter("serviceInstance", entity);
        }
        if (entity instanceof Subscription) {
            query.setParameter("subscription", entity);
        }
        if (entity instanceof BillingAccount) {
            query.setParameter("billingAccount", entity);
        }
        if (entity instanceof UserAccount) {
            query.setParameter("userAccount", entity);
        }
        if (entity instanceof CustomerAccount) {
            query.setParameter("customerAccount", entity);
        }
        if (entity instanceof Customer) {
            query.setParameter("customer", entity);
        }
        try {
            CounterPeriod cp = (CounterPeriod) query.getSingleResult();
            if (AccumulatorCounterTypeEnum.MULTI_VALUE.equals(cp.getAccumulatorType()) && cp.getAccumulatedValues() != null && !cp.getAccumulatedValues().isEmpty()) {
                return cp.getAccumulatedValues();
            }
            return cp.getValue();
        } catch (NoResultException e) {
            return null;
        }
    }
}