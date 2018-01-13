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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.meveo.admin.exception.BusinessException;
import org.meveo.cache.RatingCacheContainerProvider;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.CounterValueChangeInfo;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.billing.CounterPeriod;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.UsageChargeInstance;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.catalog.CounterTemplateLevel;
import org.meveo.model.catalog.CounterTypeEnum;
import org.meveo.model.notification.Notification;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.ValueExpressionWrapper;

@Stateless
public class CounterInstanceService extends PersistenceService<CounterInstance> {

    @Inject
    private UserAccountService userAccountService;

    @Inject
    private BillingAccountService billingAccountService;

    @Inject
    private CounterPeriodService counterPeriodService;

    @EJB
    private UsageChargeInstanceService usageChargeInstanceService;

    @Inject
    private RatingCacheContainerProvider ratingCacheContainerProvider;

    public CounterInstance counterInstanciation(UserAccount userAccount, CounterTemplate counterTemplate, boolean isVirtual) throws BusinessException {
        CounterInstance result = null;

        if (userAccount == null) {
            throw new BusinessException("userAccount is null");
        }

        if (counterTemplate == null) {
            throw new BusinessException("counterTemplate is null");
        }

        // we instanciate the counter only if there is no existing instance for
        // the same template
        if (counterTemplate.getCounterLevel() == CounterTemplateLevel.BA) {
            BillingAccount billingAccount = userAccount.getBillingAccount();
            if (!billingAccount.getCounters().containsKey(counterTemplate.getCode())) {
                result = new CounterInstance();
                result.setCounterTemplate(counterTemplate);
                result.setBillingAccount(billingAccount);

                if (!isVirtual) {
                    create(result);
                }

                billingAccount.getCounters().put(counterTemplate.getCode(), result);

                if (!isVirtual) {
                    billingAccountService.update(billingAccount);
                }
            } else {
                result = userAccount.getBillingAccount().getCounters().get(counterTemplate.getCode());
            }
        } else {
            if (!userAccount.getCounters().containsKey(counterTemplate.getCode())) {
                result = new CounterInstance();
                result.setCounterTemplate(counterTemplate);
                result.setUserAccount(userAccount);

                if (!isVirtual) {
                    create(result);
                }
                userAccount.getCounters().put(counterTemplate.getCode(), result);

                if (!isVirtual) {
                    userAccountService.update(userAccount);
                }
            } else {
                result = userAccount.getCounters().get(counterTemplate.getCode());
            }
        }

        return result;
    }

    public CounterInstance counterInstanciation(Notification notification, CounterTemplate counterTemplate) throws BusinessException {
        CounterInstance counterInstance = null;

        if (notification == null) {
            throw new BusinessException("notification is null");
        }

        if (counterTemplate == null) {
            throw new BusinessException("counterTemplate is null");
        }

        // Remove current counter instance if it does not match the counter
        // template to be instantiated
        if (notification.getCounterInstance() != null && !counterTemplate.getId().equals(notification.getCounterInstance().getCounterTemplate().getId())) {
            CounterInstance ci = notification.getCounterInstance();
            notification.setCounterInstance(null);
            remove(ci);
        }

        // Instantiate counter instance if there is not one yet
        if (notification.getCounterInstance() == null) {
            counterInstance = new CounterInstance();
            counterInstance.setCounterTemplate(counterTemplate);
            create(counterInstance);

            notification.setCounterTemplate(counterTemplate);
            notification.setCounterInstance(counterInstance);
        } else {
            counterInstance = notification.getCounterInstance();
        }

        return counterInstance;
    }

    // we must make sure the counter period is persisted in db before storing it in cache
    // @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW) - problem with MariaDB. See #2393 - Issue with counter period creation in MariaDB
    public CounterPeriod createPeriod(CounterInstance counterInstance, Date chargeDate, Date initDate, UsageChargeInstance usageChargeInstance) throws BusinessException {

        CounterTemplate counterTemplate = counterInstance.getCounterTemplate();

        CounterPeriod counterPeriod = instantiateCounterPeriod(counterTemplate, chargeDate, initDate, usageChargeInstance);
        counterPeriod.setCounterInstance(counterInstance);
        counterPeriodService.create(counterPeriod);

        counterInstance.getCounterPeriods().add(counterPeriod);
        counterInstance.updateAudit(currentUser);

        // Add usage type counter period to cache
        if (counterTemplate.getCounterType() == CounterTypeEnum.USAGE) {
            ratingCacheContainerProvider.addCounterPeriodToCache(counterPeriod);
        }

        return counterPeriod;
    }

    /**
     * Instantiate only a counter period. Note: Will not be persisted
     * 
     * @param counterTemplate Counter template
     * @param chargeDate Charge date
     * @param initDate Initial date, used for period start/end date calculation
     * @param usageChargeInstance Usage charge instance to associate counter with
     * @return CounterPeriod instance
     * @throws BusinessException business exception
     */
    public CounterPeriod instantiateCounterPeriod(CounterTemplate counterTemplate, Date chargeDate, Date initDate, UsageChargeInstance usageChargeInstance)
            throws BusinessException {

        CounterPeriod counterPeriod = new CounterPeriod();
        Calendar cal = counterTemplate.getCalendar();
        cal.setInitDate(initDate);
        Date startDate = cal.previousCalendarDate(chargeDate);
        if (startDate == null) {
            log.info("cannot create counter for the date {} (not in calendar)", chargeDate);
            return null;
        }
        Date endDate = cal.nextCalendarDate(startDate);
        BigDecimal initialValue = counterTemplate.getCeiling();
        log.info("create counter period from {} to {}", startDate, endDate);
        if (!StringUtils.isBlank(counterTemplate.getCeilingExpressionEl()) && usageChargeInstance != null) {
            initialValue = evaluateCeilingElExpression(counterTemplate.getCeilingExpressionEl(), usageChargeInstance, usageChargeInstance.getServiceInstance(),
                usageChargeInstance.getSubscription());
        }
        counterPeriod.setPeriodStartDate(startDate);
        counterPeriod.setPeriodEndDate(endDate);
        counterPeriod.setValue(initialValue);
        counterPeriod.setCode(counterTemplate.getCode());
        counterPeriod.setDescription(counterTemplate.getDescription());
        counterPeriod.setLevel(initialValue);
        counterPeriod.setCounterType(counterTemplate.getCounterType());
        counterPeriod.setNotificationLevels(counterTemplate.getNotificationLevels(), initialValue);

        counterPeriod.isCorrespondsToPeriod(chargeDate);

        return counterPeriod;
    }

    /**
     * Find or create a counter period for a given date.
     * 
     * @param counterInstance Counter instance
     * @param date Date to match
     * @param initDate initial date.
     * @return Found or created counter period
     * @throws BusinessException business exception
     */
    public CounterPeriod getCounterPeriod(CounterInstance counterInstance, Date date, Date initDate) throws BusinessException {
        Query query = getEntityManager().createNamedQuery("CounterPeriod.findByPeriodDate");
        query.setParameter("counterInstance", counterInstance);
        query.setParameter("date", date, TemporalType.TIMESTAMP);

        try {
            return (CounterPeriod) query.getSingleResult();
        } catch (NoResultException e) {
            return createPeriod(counterInstance, date, initDate, null);
        }
    }

    // /**
    // * Update counter period value. If for some reason counter period is not found, it will be created.
    // *
    // * @param counterPeriodId Counter period identifier
    // * @param value Value to set to
    // * @param counterInstanceId Counter instance identifier (used to create counter period if one was not found)
    // * @param valueDate Date to calculate period (used to create counter period if one was not found)
    // * @param initDate initialization date to calculate period by calendar(used to create counter period if one was not found)
    // * @param usageChargeInstanceId Usage charge instance identifier for initial value calculation (used to create counter period if one was not found)
    // * @throws BusinessException business exception
    // * @throws BusinessException business exception If counter period was not found and required values for counter period creation were not passed
    // */
    // public void updateOrCreatePeriodValue(Long counterPeriodId, BigDecimal value, Long counterInstanceId, Date valueDate, Date initDate, Long usageChargeInstanceId) throws
    // BusinessException {
    // CounterPeriod counterPeriod = counterPeriodService.findById(counterPeriodId);
    //
    // if (counterPeriod == null) {
    //
    // if (counterInstanceId != null) { // Fix for #2393 - Issue with counter period creation in MariaDB
    // CounterInstance counterInstance = findById(counterInstanceId);
    // UsageChargeInstance usageChargeInstance = usageChargeInstanceService.findById(usageChargeInstanceId);
    // counterPeriod = createPeriod(counterInstance, valueDate, initDate, usageChargeInstance);
    // } else {
    // throw new BusinessException("CounterPeriod with id=" + counterPeriodId + " does not exists.");
    // }
    // }
    //
    // counterPeriod.setValue(value);
    // counterPeriod.updateAudit(currentUser);
    // }

    /**
     * Deduce a given value from a counter.
     * 
     * @param counterInstance Counter instance
     * @param date Date of event
     * @param initDate initial date.
     * @param value Value to deduce
     * @return deduce counter value.
     * @throws CounterValueInsufficientException counter value insufficient exception.
     * @throws BusinessException business exception
     */
    public BigDecimal deduceCounterValue(CounterInstance counterInstance, Date date, Date initDate, BigDecimal value) throws CounterValueInsufficientException, BusinessException {

        CounterPeriod counterPeriod = getCounterPeriod(counterInstance, date, initDate);

        if (counterPeriod == null || counterPeriod.getValue().compareTo(value) < 0) {
            throw new CounterValueInsufficientException();

        } else {
            counterPeriod.setValue(counterPeriod.getValue().subtract(value));
            counterPeriod.updateAudit(currentUser);
            return counterPeriod.getValue();
        }
    }

    /**
     * Decrease counter period by a given value. If given amount exceeds current value, only partial amount will be deduced. In case of usage type counter, cache is also updated.
     * 
     * @param counterInstanceId Counter instance identifier
     * @param periodId Counter period identifier
     * @param deduceBy Amount to decrease by
     * @param isVirtual Is this a virtual operation - no counter period entity exists nor should be persisted
     * @return Previous, the actual deduced value and new counter value. or NULL if value is not tracked (initial counter value is not set)
     * @throws BusinessException business exception
     */
    public CounterValueChangeInfo deduceCounterValue(Long counterInstanceId, Long periodId, BigDecimal deduceBy, boolean isVirtual) throws BusinessException {

        CounterValueChangeInfo counterValueInfo = null;

        // Virtual operation is only for Usage type counters and involves cache only
        if (isVirtual) {
            counterValueInfo = ratingCacheContainerProvider.deduceCounterValue(counterInstanceId, periodId, deduceBy);
            return counterValueInfo;

        } else {
            CounterPeriod counterPeriod = counterPeriodService.findById(periodId);
            if (counterPeriod == null) {
                return null;
            }

            // For Usage type counter also need to update caches values
            if (counterPeriod.getCounterType() == CounterTypeEnum.USAGE) {

                counterValueInfo = ratingCacheContainerProvider.deduceCounterValue(counterInstanceId, periodId, deduceBy);
                // Value is not tracked
                if (counterValueInfo == null) {
                    counterPeriodService.detach(counterPeriod);
                    return null;

                } else {
                    counterPeriod.setValue(counterValueInfo.getNewValue());
                }

            } else {

                BigDecimal deducedQuantity = null;
                BigDecimal previousValue = counterPeriod.getValue();

                // No initial value, so no need to track present value (will always be able to deduce by any amount) and thus no need to update
                if (counterPeriod.getLevel() == null) {
                    counterPeriodService.detach(counterPeriod);
                    return null;

                    // Previous value is Zero, but deduction value is negative (really an addition)
                } else if (previousValue.compareTo(BigDecimal.ZERO) == 0 && deduceBy.compareTo(BigDecimal.ZERO) > 0) {
                    return new CounterValueChangeInfo(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);

                } else if (previousValue.compareTo(deduceBy) < 0) {
                    deducedQuantity = counterPeriod.getValue();
                    counterPeriod.setValue(BigDecimal.ZERO);

                } else {
                    deducedQuantity = deduceBy;
                    counterPeriod.setValue(counterPeriod.getValue().subtract(deduceBy));
                }

                counterValueInfo = new CounterValueChangeInfo(previousValue, deducedQuantity, counterPeriod.getValue());
            }
            counterPeriod = counterPeriodService.update(counterPeriod);

            log.debug("Counter period {} was changed {}", counterPeriod.getId(), counterValueInfo);

            return counterValueInfo;
        }
    }

    @SuppressWarnings("unchecked")
    public List<CounterInstance> findByCounterTemplate(CounterTemplate counterTemplate) {
        QueryBuilder qb = new QueryBuilder(CounterInstance.class, "c");
        qb.addCriterionEntity("counterTemplate", counterTemplate);
        return qb.find(getEntityManager());
    }

    public BigDecimal evaluateCeilingElExpression(String expression, ChargeInstance charge, ServiceInstance serviceInstance, Subscription subscription) throws BusinessException {
        int rounding = appProvider.getRounding() == null ? 3 : appProvider.getRounding();
        BigDecimal result = null;
        if (StringUtils.isBlank(expression)) {
            return result;
        }
        Map<Object, Object> userMap = new HashMap<Object, Object>();
        if (expression.indexOf("charge") >= 0) {
            userMap.put("charge", charge);
        }
        if (expression.indexOf("service") >= 0) {
            userMap.put("service", serviceInstance);
        }
        if (expression.indexOf("sub") >= 0) {
            userMap.put("sub", subscription);
        }

        Object res = ValueExpressionWrapper.evaluateExpression(expression, userMap, BigDecimal.class);
        try {
            result = (BigDecimal) res;
            result = result.setScale(rounding, RoundingMode.HALF_UP);
        } catch (Exception e) {
            throw new BusinessException("Expression " + expression + " do not evaluate to BigDecimal but " + res);
        }
        return result;
    }

    /**
     * Count counter periods which end date is older then a given date.
     * 
     * @param date Date to check
     * @return A number of counter periods which end date is older then a given date
     */
    public long countCounterPeriodsToDelete(Date date) {
        long result = 0;
        QueryBuilder qb = new QueryBuilder(CounterPeriod.class, "cp");
        qb.addCriterion("cp.periodEndDate", "<", date, false);
        result = qb.count(getEntityManager());

        return result;
    }

    /**
     * Remove counter periods which end date is older then a given date.
     * 
     * @param date Date to check
     * @return A number of counter periods that were removed
     */
    @SuppressWarnings("unchecked")
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public long deleteCounterPeriods(Date date) {
        log.trace("Removing counter periods which end date is older then a {} date", date);
        long itemsDeleted = 0;
        QueryBuilder qb = new QueryBuilder(CounterPeriod.class, "cp");
        qb.addCriterion("cp.periodEndDate", "<", date, false);
        EntityManager em = getEntityManager();
        List<CounterPeriod> periods = qb.find(em);
        for (CounterPeriod counterPeriod : periods) {
            em.remove(counterPeriod);
            itemsDeleted++;
        }

        log.info("Removed {} counter periods which end date is older then a {} date", itemsDeleted, date);

        return itemsDeleted;
    }

    /**
     * Increment counter period by a given value.
     * 
     * @param periodId Counter period identifier
     * @param incrementBy Increment by
     * @return The new value, or NULL if value is not tracked (initial value is not set)
     * @throws BusinessException business exception
     * 
     */
    public BigDecimal incrementCounterValue(Long periodId, BigDecimal incrementBy) throws BusinessException {

        CounterPeriod counterPeriod = counterPeriodService.findById(periodId);
        if (counterPeriod == null) {
            return null;
        }
        if (counterPeriod.getCounterType() == CounterTypeEnum.USAGE) {

            CounterValueChangeInfo counterValueChangeInfo = ratingCacheContainerProvider.deduceCounterValue(counterPeriod.getCounterInstance().getId(), periodId,
                incrementBy.negate());
            // Value is not tracked
            if (counterValueChangeInfo == null) {
                return null;
            } else {
                counterPeriod.setValue(counterValueChangeInfo.getNewValue());
            }

        } else {
            counterPeriod.setValue(counterPeriod.getValue().add(incrementBy));
        }
        counterPeriodService.update(counterPeriod);

        log.debug("Counter period {} was incremented by {} to {}", counterPeriod.getId(), incrementBy, counterPeriod.getValue());

        return counterPeriod.getValue();
    }

    /**
     * Get a list of usage type counter instances to populate a cache
     * 
     * @return A list of usage type counter instances
     */
    public List<CounterInstance> getCounterInstancesForCache() {
        return getEntityManager().createNamedQuery("CounterInstance.getUsageCountersForCache", CounterInstance.class).getResultList();
    }
}