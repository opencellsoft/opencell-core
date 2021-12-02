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

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.event.CounterPeriodEvent;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.BusinessEntity;
import org.meveo.model.CounterValueChangeInfo;
import org.meveo.model.ICounterEntity;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.billing.CounterPeriod;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.catalog.AccumulatorCounterTypeEnum;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.catalog.CounterTypeEnum;
import org.meveo.model.crm.Customer;
import org.meveo.model.notification.Notification;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.audit.AuditOrigin;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.catalog.impl.CalendarService;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.payments.impl.CustomerAccountService;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * @author Said Ramli
 * @author Abdellatif BARI
 * @author Khalid HORRI
 * @lastModifiedVersion 6.1
 */
@Singleton
@Lock(LockType.WRITE)
public class CounterInstanceService extends PersistenceService<CounterInstance> {

    private static final String CHARGE = "charge";
    private static final String SERVICE = "service";
    private static final String SERVICE_INSTANCE = "serviceInstance";
    private static final String WALLET_OPERATION = "op";

    @Inject
    @MeveoJpa
    private EntityManagerWrapper emWrapper;

    @Inject
    private CustomerAccountService customerAccountService;

    @Inject
    private CustomerService customerService;

    @Inject
    private UserAccountService userAccountService;

    @Inject
    private CalendarService calendarService;

    @Inject
    private BillingAccountService billingAccountService;

    @Inject
    private SubscriptionService subscriptionService;

    @Inject
    private ServiceInstanceService serviceInstanceService;

    @Inject
    private CounterPeriodService counterPeriodService;

    @Inject
    private Event<CounterPeriodEvent> counterPeriodEvent;

    @Inject
    private AuditOrigin auditOrigin;

    @Inject
    private VirtualCounterInstances virtualCounterInstances;

    @Inject
    private CounterUpdateTracking counterUpdatesTracking;

    public CounterInstance counterInstanciation(ServiceInstance serviceInstance, CounterTemplate counterTemplate, boolean isVirtual) throws BusinessException {

        if (serviceInstance == null) {
            throw new BusinessException("entity is null");
        }

        if (counterTemplate == null) {
            throw new BusinessException("counterTemplate is null");
        }

        CounterInstance counterInstance = null;

        switch (counterTemplate.getCounterLevel()) {
            case CUST:

                counterInstance = instantiateCounter(customerService, serviceInstance.getSubscription().getUserAccount().getBillingAccount().getCustomerAccount().getCustomer(), counterTemplate, isVirtual);
                break;

            case CA:
                counterInstance = instantiateCounter(customerAccountService, serviceInstance.getSubscription().getUserAccount().getBillingAccount().getCustomerAccount(), counterTemplate, isVirtual);
                break;

            case BA:
                counterInstance = instantiateCounter(billingAccountService, serviceInstance.getSubscription().getUserAccount().getBillingAccount(), counterTemplate, isVirtual);
                break;

            case UA:
                counterInstance = instantiateCounter(userAccountService, serviceInstance.getSubscription().getUserAccount(), counterTemplate, isVirtual);
                break;

            case SU:
                counterInstance = instantiateCounter(subscriptionService, serviceInstance.getSubscription(), counterTemplate, isVirtual);
                break;

            case SI:
                counterInstance = instantiateCounter(serviceInstanceService, serviceInstance, counterTemplate, isVirtual);
                break;
        }

        return counterInstance;
    }

    /**
     * Instantiate a counter
     *
     * @param service the business service to manage the entity that counter is instantiated for
     * @param entity the business entity Entity to instantiate the counter for
     * @param clazz the class of the business entity
     * @param counterTemplate the counter template
     * @param isVirtual is virtual
     * @return a counter instance
     * @throws BusinessException
     */
    private CounterInstance instantiateCounter(BusinessService service, ICounterEntity entity, CounterTemplate counterTemplate, boolean isVirtual) throws BusinessException {
        CounterInstance counterInstance = new CounterInstance();
        if (!entity.getCounters().containsKey(counterTemplate.getCode())) {
            counterInstance.setCounterTemplate(counterTemplate);

            if (entity instanceof Customer) {
                counterInstance.setCustomer((Customer) entity);
            } else if (entity instanceof CustomerAccount) {
                counterInstance.setCustomerAccount((CustomerAccount) entity);
            } else if (entity instanceof BillingAccount) {
                counterInstance.setBillingAccount((BillingAccount) entity);
            } else if (entity instanceof UserAccount) {
                counterInstance.setUserAccount((UserAccount) entity);
            } else if (entity instanceof Subscription) {
                counterInstance.setSubscription((Subscription) entity);
            } else if (entity instanceof ServiceInstance) {
                counterInstance.setServiceInstance((ServiceInstance) entity);
            }

            if (!isVirtual) {
                create(counterInstance);
            }

            entity.getCounters().put(counterTemplate.getCode(), counterInstance);

            if (!isVirtual) {
                service.update((BusinessEntity) entity);
            }
        } else {
            counterInstance = entity.getCounters().get(counterTemplate.getCode());
        }
        return counterInstance;
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

    /**
     * Instantiate AND persist counter period for a given date
     *
     * @param counterInstance Counter instance
     * @param chargeDate Charge date - to match the period validity dates
     * @param initDate Initial date, used for period start/end date calculation
     * @param chargeInstance Charge instance to associate counter with
     * @return CounterPeriod instance or NULL if counter period can not be created because of calendar limitations
     * @throws BusinessException Business exception
     */
    private CounterPeriod createPeriod(CounterInstance counterInstance, Date chargeDate, Date initDate, ChargeInstance chargeInstance) throws BusinessException {

        CounterPeriod counterPeriod = null;
        CounterTemplate counterTemplate = counterInstance.getCounterTemplate();

        counterPeriod = instantiateCounterPeriod(counterTemplate, chargeDate, initDate, chargeInstance);

        if (counterPeriod != null) {
            counterPeriod.setCounterInstance(counterInstance);
            counterPeriodService.create(counterPeriod);

            // AK is this really needed?
            // counterInstance.getCounterPeriods().add(counterPeriod);
            // counterInstance.updateAudit(currentUser);
        }

        return counterPeriod;
    }

    /**
     * Instantiate AND persist <b>for duration of the request</b> a counter period for a given date
     *
     * @param counterInstance Counter instance
     * @param chargeDate Charge date - to match the period validity dates
     * @param initDate Initial date, used for period start/end date calculation
     * @param chargeInstance Charge instance to associate counter with
     * @return CounterPeriod instance or NULL if counter period can not be created because of calendar limitations
     * @throws BusinessException Business exception
     */
    private CounterPeriod createPeriodVirtual(CounterInstance counterInstance, Date chargeDate, Date initDate, ChargeInstance chargeInstance) throws BusinessException {

        CounterPeriod counterPeriod = null;
        // It is a pure virtual counter instance as when simulating rating from quote
        if (counterInstance.getId() == null) {
            CounterTemplate counterTemplate = counterInstance.getCounterTemplate();

            counterPeriod = instantiateCounterPeriod(counterTemplate, chargeDate, initDate, chargeInstance);

            if (counterPeriod != null) {
                counterPeriod.setCounterInstance(counterInstance);
            }

            // It is a real counter instance, just need to create a copy of the counter period for a virtual rating purpose
        } else {

            CounterPeriod realCounterPeriod = getOrCreateCounterPeriod(chargeInstance.getCounter(), chargeDate, initDate, chargeInstance);

            if (realCounterPeriod != null) {
                try {
                    counterPeriod = realCounterPeriod.clone();
                } catch (CloneNotSupportedException e) {
                    // There is no reason to get here
                }
            }
        }

        if (counterPeriod != null) {
            virtualCounterInstances.addCounterPeriod(counterPeriod);
        }
        return counterPeriod;
    }

    /**
     * Instantiate only a counter period. Note: Will not be persisted
     *
     * @param counterTemplate Counter template
     * @param chargeDate Charge date
     * @param initDate Initial date, used for period start/end date calculation
     * @param chargeInstance charge instance to associate counter with
     * @return a counter period or NULL if counter period can not be created because of calendar limitations
     * @throws BusinessException the business exception
     */
    private CounterPeriod instantiateCounterPeriod(CounterTemplate counterTemplate, Date chargeDate, Date initDate, ChargeInstance chargeInstance) throws BusinessException {

        CounterPeriod counterPeriod = new CounterPeriod();
        Calendar cal = counterTemplate.getCalendar();
        if (!StringUtils.isBlank(counterTemplate.getCalendarCodeEl())) {
            cal = getCalendarFromEl(counterTemplate.getCalendarCodeEl(), chargeInstance);
        }
        cal = CalendarService.initializeCalendar(cal, initDate, chargeInstance, chargeInstance.getServiceInstance());

        Date startDate = cal.previousCalendarDate(chargeDate);
        if (startDate == null) {
            log.warn("Can't create counter {} for the date {} (not in calendar)", counterTemplate.getCode(), chargeDate);
            return null;
        }
        Date endDate = cal.nextCalendarDate(startDate);
        counterPeriod.setPeriodStartDate(startDate);
        counterPeriod.setPeriodEndDate(endDate);

        BigDecimal initialValue = counterTemplate.getCeiling();

        if (!StringUtils.isBlank(counterTemplate.getCeilingExpressionEl()) && chargeInstance != null) {
            initialValue = evaluateCeilingElExpression(counterTemplate.getCeilingExpressionEl(), chargeInstance);
        }

        counterPeriod.setValue(initialValue);
        counterPeriod.setCode(counterTemplate.getCode());
        counterPeriod.setDescription(counterTemplate.getDescription());
        counterPeriod.setLevel(initialValue);
        counterPeriod.setCounterType(counterTemplate.getCounterType());
        counterPeriod.setAccumulator(counterTemplate.getAccumulator());
        counterPeriod.setAccumulatorType(counterTemplate.getAccumulatorType());
        counterPeriod.setNotificationLevels(counterTemplate.getNotificationLevels(), initialValue);

        return counterPeriod;
    }

    /**
     * trigger counter period event
     *
     * @param counterPeriod the counter period
     * @param counterPeriodEventLevels the counter period event levels
     */
    private void triggerCounterPeriodEvent(CounterPeriod counterPeriod, List<Entry<String, BigDecimal>> counterPeriodEventLevels) {
        for (Entry<String, BigDecimal> counterValue : counterPeriodEventLevels) {
            try {
                CounterPeriodEvent event = new CounterPeriodEvent(counterPeriod, counterValue.getValue(), counterValue.getKey());
                event.setCounterPeriod(counterPeriod);
                counterPeriodEvent.fire(event);
            } catch (Exception e) {
                log.error("Failed to executing trigger counterPeriodEvent", e);
            }
        }
    }

    /**
     * Get a counter period for a given date
     *
     * @param counterInstance Counter instance
     * @param date Date
     * @return A counter period matched or NULL if no match found
     */
    private CounterPeriod getCounterPeriodByDate(CounterInstance counterInstance, Date date) {
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
     * Get a virtual counter period for a given date
     *
     * @param counterInstanceId Counter instance identifier. Optional.
     * @param counterCode Counter code
     * @param date Date
     * @return A counter period matched or NULL if no match found
     */
    private CounterPeriod getCounterPeriodVirtualByDate(Long counterInstanceId, String counterCode, Date date) {
        return virtualCounterInstances.getCounterPeriod(counterInstanceId, counterCode, date);
    }

    /**
     * Find or create a counter period for a given date.
     *
     * @param counterInstance Counter instance
     * @param date Date to match
     * @param initDate initial date.
     * @param chargeInstance Charge instance to associate counter with
     * @return Found or created counter period or NULL if counter period can not be created because of calendar limitations
     * @throws BusinessException business exception
     */
    private CounterPeriod getOrCreateCounterPeriod(CounterInstance counterInstance, Date date, Date initDate, ChargeInstance chargeInstance) throws BusinessException {
        CounterPeriod counterPeriod = getCounterPeriodByDate(counterInstance, date);

        if (counterPeriod != null) {
            return counterPeriod;
        }
        return createPeriod(counterInstance, date, initDate, chargeInstance);
    }

    /**
     * Find or create a counter period for a given date.
     *
     * @param counterInstance Counter instance
     * @param date Date to match
     * @param initDate initial date.
     * @param chargeInstance Charge instance to associate counter with
     * @param serviceInstance the Service instance of charge instance
     * @return Found or created counter period
     * @throws BusinessException business exception
     */
    private CounterPeriod getOrCreateCounterPeriodVirtual(CounterInstance counterInstance, Date date, Date initDate, ChargeInstance chargeInstance) throws BusinessException {
        CounterPeriod counterPeriod = getCounterPeriodVirtualByDate(counterInstance.getId(), counterInstance.getCode(), date);

        if (counterPeriod != null) {
            return counterPeriod;
        }
        return createPeriodVirtual(counterInstance, date, initDate, chargeInstance);
    }

    /**
     * Deduce a given value from a counter. Will instantiate a counter period if one was not created yet matching the given date. Used from Notification counters.
     *
     * @param counterInstance Counter instance
     * @param date Date of event
     * @param initDate initial date.
     * @param value Value to deduce
     * @return deduce counter value.
     * @throws CounterValueInsufficientException counter value insufficient exception.
     * @throws BusinessException business exception
     */
    @Lock(LockType.WRITE)
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public BigDecimal deduceCounterValue(CounterInstance counterInstance, Date date, Date initDate, BigDecimal value) throws CounterValueInsufficientException, BusinessException {

        counterInstance = retrieveIfNotManaged(counterInstance);
        CounterPeriod counterPeriod = getOrCreateCounterPeriod(counterInstance, date, initDate, null);
        if (counterPeriod == null || counterPeriod.getValue().compareTo(value) < 0) {
            throw new CounterValueInsufficientException();

        } else {
            counterPeriod.setValue(counterPeriod.getValue().subtract(value));
            counterPeriod.updateAudit(currentUser);
            return counterPeriod.getValue();
        }
    }

    /**
     * Deduce a given value from a counter. Will instantiate a counter period if one was not created yet matching the given date
     *
     * @param counterInstance Counter instance
     * @param date Date of event for counter period calculation
     * @param initDate initial date for counter period calculation
     * @param chargeInstance Charge instance counter is associated to. Used to calculate various El values.
     * @param valueToDeduce Value to deduce
     * @param isVirtual Is this a virtual operation - no counter period entity exists nor should be persisted
     * @return CounterValueChangeInfo Counter value change summary - the previous, deduced and new counter value
     * @throws BusinessException business exception
     */
    @Lock(LockType.WRITE)
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public CounterValueChangeInfo deduceCounterValue(CounterInstance counterInstance, Date date, Date initDate, ChargeInstance chargeInstance, BigDecimal valueToDeduce, boolean isVirtual) throws BusinessException {

        CounterPeriod counterPeriod = null;
        // In case of virtual operation only instantiate a counter period, don't create it
        if (isVirtual) {
            counterPeriod = getOrCreateCounterPeriodVirtual(counterInstance, date, initDate, chargeInstance);

        } else {
            counterPeriod = getOrCreateCounterPeriod(counterInstance, date, initDate, chargeInstance);
        }

        if (counterPeriod == null) {
            return null;
        }

        CounterValueChangeInfo counterValueChangeInfo = deduceCounterValue(counterPeriod, valueToDeduce, isVirtual);

        if (counterValueChangeInfo.isChange()) {// && (auditOrigin.getAuditOrigin() == ChangeOriginEnum.API || auditOrigin.getAuditOrigin() == ChangeOriginEnum.INBOUND_REQUEST)) {
            counterUpdatesTracking.addCounterPeriodChange(counterPeriod, counterValueChangeInfo);
        }

        // Fire notifications if counter value matches trigger value and counter value is tracked
        if (!isVirtual && counterPeriod.getNotificationLevels() != null) {
            List<Entry<String, BigDecimal>> counterPeriodEventLevels = counterPeriod.getMatchedNotificationLevels(counterValueChangeInfo.getPreviousValue(), counterValueChangeInfo.getNewValue());

            if (counterPeriodEventLevels != null && !counterPeriodEventLevels.isEmpty()) {
                triggerCounterPeriodEvent(counterPeriod, counterPeriodEventLevels);
            }
        }

        return counterValueChangeInfo;
    }

    /**
     * Decrease counter period by a given value. If given amount exceeds current value, only partial amount will be deduced. NOTE: counterPeriod passed to the method will become stale if it happens to be updated in this
     * method
     *
     * @param counterPeriod Counter period to update
     * @param deduceBy Amount to decrease by
     * @param isVirtual Is this a virtual operation - no counter period entity exists nor should be persisted
     * @return CounterValueChangeInfo Counter value change summary - the previous, deduced and new counter value
     * @throws BusinessException business exception
     */
    private CounterValueChangeInfo deduceCounterValue(CounterPeriod counterPeriod, BigDecimal deduceBy, boolean isVirtual) throws BusinessException {

        BigDecimal deducedQuantity = null;
        BigDecimal previousValue = counterPeriod.getValue();

        // No initial value, so no need to track present value (will always be able to deduce by any amount) and thus no need to update
        if (counterPeriod.getLevel() == null) {
            return new CounterValueChangeInfo(counterPeriod.getId(), counterPeriod.getAccumulator(), null, deduceBy, null);

            // Previous value is Zero, there is not much further to reduce
        } else if (previousValue.compareTo(BigDecimal.ZERO) == 0 && deduceBy.compareTo(BigDecimal.ZERO) > 0) {
            return new CounterValueChangeInfo(counterPeriod.getId(), counterPeriod.getAccumulator(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);

        } else {
            if (previousValue.compareTo(deduceBy) < 0) {
                deducedQuantity = counterPeriod.getValue();
                counterPeriod.setValue(BigDecimal.ZERO);

            } else {
                deducedQuantity = deduceBy;
                counterPeriod.setValue(counterPeriod.getValue().subtract(deduceBy));
            }

            CounterValueChangeInfo counterValueInfo = new CounterValueChangeInfo(counterPeriod.getId(), counterPeriod.getAccumulator(), previousValue, deducedQuantity, counterPeriod.getValue());

            log.debug("Counter period {} was changed {}", isVirtual ? counterPeriod.getCode() : counterPeriod.getId(), counterValueInfo);

            return counterValueInfo;
        }
    }

    public List<CounterInstance> findByCounterTemplate(CounterTemplate counterTemplate) {
        QueryBuilder qb = new QueryBuilder(CounterInstance.class, "c");
        qb.addCriterionEntity("counterTemplate", counterTemplate);
        return qb.find(getEntityManager());
    }

    /**
     * Gets the calendar from EL
     *
     * @param calendarCodeEl the calendar code EL
     * @param chargeInstance Charge instance
     * @return
     * @throws BusinessException
     */
    private Calendar getCalendarFromEl(String calendarCodeEl, ChargeInstance chargeInstance) throws BusinessException {
        String calendarCode = evaluateCalendarElExpression(calendarCodeEl, chargeInstance);
        Calendar calendar = calendarService.findByCode(calendarCode);
        if (calendar == null) {
            throw new BusinessException("Cant found calendar by code:" + calendarCode);
        }
        return calendar;
    }

    private String evaluateCalendarElExpression(String expression, ChargeInstance chargeInstance) throws BusinessException {

        String result = null;
        if (StringUtils.isBlank(expression)) {
            return result;
        }

        Map<Object, Object> userMap = new HashMap<Object, Object>();
        if (expression.indexOf(CHARGE) >= 0 || expression.indexOf("ci") >= 0) {
            userMap.put(CHARGE, chargeInstance);
            userMap.put("ci", chargeInstance);
        }
        if (chargeInstance != null && expression.indexOf(SERVICE) >= 0 || expression.indexOf(SERVICE_INSTANCE) >= 0) {
            userMap.put(SERVICE, chargeInstance.getServiceInstance());
            userMap.put(SERVICE_INSTANCE, chargeInstance.getServiceInstance());
        }
        if (chargeInstance != null && expression.indexOf("sub") >= 0) {
            userMap.put("sub", chargeInstance.getSubscription());
        }

        Object res = ValueExpressionWrapper.evaluateExpression(expression, userMap, String.class);
        try {
            result = (String) res;
        } catch (Exception e) {
            throw new BusinessException("Expression " + expression + " do not evaluate to String but " + res);
        }
        return result;
    }

    private BigDecimal evaluateCeilingElExpression(String expression, ChargeInstance chargeInstance) throws BusinessException {

        if (StringUtils.isBlank(expression)) {
            return null;
        }
        Map<Object, Object> userMap = new HashMap<>();
        if (expression.contains(CHARGE) || expression.contains("ci")) {
            userMap.put(CHARGE, chargeInstance);
            userMap.put("ci", chargeInstance);
        }
        if (chargeInstance != null && expression.contains(SERVICE) || expression.contains(SERVICE_INSTANCE)) {
            userMap.put(SERVICE, chargeInstance.getServiceInstance());
            userMap.put(SERVICE_INSTANCE, chargeInstance.getServiceInstance());
        }
        if (chargeInstance != null && expression.contains("sub")) {
            userMap.put("sub", chargeInstance.getSubscription());
        }

        BigDecimal result = ValueExpressionWrapper.evaluateExpression(expression, userMap, BigDecimal.class);
        result = result.setScale(chargeInstance.getChargeTemplate().getUnitNbDecimal(), chargeInstance.getChargeTemplate().getRoundingMode().getRoundingMode());

        return result;
    }

    /**
     * Count counter periods which end date is older than a given date.
     *
     * @param date Date to check
     * @return A number of counter periods which end date is older than a given date
     */
    @Lock(LockType.READ)
    public long countCounterPeriodsToDelete(Date date) {
        long result = getEntityManager().createNamedQuery("CounterPeriod.countPeriodsToPurgeByDate", Long.class).setParameter("date", date).getSingleResult();
        return result;
    }

    /**
     * Remove counter periods which end date is older than a given date.
     *
     * @param date Date to check
     * @return A number of counter periods that were removed
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public long deleteCounterPeriods(Date date) {
        log.debug("Removing counter periods which end date is older than a {} date", date);

        long itemsDeleted = getEntityManager().createNamedQuery("CounterPeriod.purgePeriodsByDate").setParameter("date", date).executeUpdate();

        log.info("Removed {} counter periods which end date is older than a {} date", itemsDeleted, date);

        return itemsDeleted;
    }

    /**
     * Increment a non-accumulation typecounter period by a given value.
     *
     * @param periodId Counter period identifier
     * @param incrementBy Increment by
     * @return The new value, or NULL if value is not tracked (initial value is not set)
     * @throws BusinessException business exception
     */
    @Lock(LockType.WRITE)
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public BigDecimal incrementCounterValue(Long periodId, BigDecimal incrementBy) throws BusinessException {

        CounterPeriod counterPeriod = counterPeriodService.findById(periodId);
        if (counterPeriod == null) {
            return null;
        }

//        if (counterPeriod.getCounterType().equals(CounterTypeEnum.USAGE)) {
        CounterValueChangeInfo counterValueChangeInfo = deduceCounterValue(counterPeriod, incrementBy.negate(), false);
        // Value is not tracked
        if (counterValueChangeInfo.getPreviousValue() == null) {
            return null;
        } else {
            return counterValueChangeInfo.getNewValue();
        }

//        } else if (counterPeriod.getCounterType().equals(CounterTypeEnum.USAGE_AMOUNT)) {
//            counterPeriod.setValue(counterPeriod.getValue().subtract(incrementBy));
//            log.debug("Counter period {} was decremented by {} to {}", counterPeriod.getId(), incrementBy, counterPeriod.getValue());
//            return counterPeriod.getValue();
//
//        } else {
//            counterPeriod.setValue(counterPeriod.getValue().add(incrementBy));
//            log.debug("Counter period {} was incremented by {} to {}", counterPeriod.getId(), incrementBy, counterPeriod.getValue());
//            return counterPeriod.getValue();
//        }
    }

    /**
     * Increment accumulator counter by a given value. Will instantiate a counter period if one was not created yet matching the given date
     *
     * @param counterInstance Counter instance
     * @param date Date of event for counter period calculation
     * @param initDate initial date for counter period calculation
     * @param chargeInstance Charge instance counter is associated to
     * @param valueToDeduce Value to deduce. In case counter is an accumulator counter, counter value will be incremented instead od reduced
     * @param isVirtual Is this a virtual operation - no counter period entity exists nor should be persisted
     * @return CounterValueChangeInfo Counter value change summary - the previous, deduced and new counter value
     * @throws BusinessException business exception
     */

    @Lock(LockType.WRITE)
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<CounterValueChangeInfo> incrementAccumulatorCounterValue(ChargeInstance chargeInstance, List<WalletOperation> walletOperations, boolean isVirtual) throws BusinessException {

        List<CounterValueChangeInfo> counterValueChangeInfos = new ArrayList<CounterValueChangeInfo>();

        for (CounterInstance counterInstance : chargeInstance.getAccumulatorCounterInstances()) {

            CounterPeriod counterPeriod = null;

            for (WalletOperation wo : walletOperations) {

                // In case of virtual operation only instantiate a counter period, don't create it
                if (isVirtual) {
                    counterPeriod = getOrCreateCounterPeriodVirtual(counterInstance, wo.getOperationDate(), chargeInstance.getServiceInstance().getSubscriptionDate(), chargeInstance);

                } else {
                    counterPeriod = getOrCreateCounterPeriod(counterInstance, wo.getOperationDate(), chargeInstance.getServiceInstance().getSubscriptionDate(), chargeInstance);
                }

                if (counterPeriod == null) {
                    continue;
                }
                CounterValueChangeInfo counterValueChangeInfo = accumulateCounterValue(counterPeriod, wo, isVirtual);
                counterValueChangeInfos.add(counterValueChangeInfo);

                if (counterValueChangeInfo.isChange()) {// && (auditOrigin.getAuditOrigin() == ChangeOriginEnum.API || auditOrigin.getAuditOrigin() == ChangeOriginEnum.INBOUND_REQUEST)) {
                    counterUpdatesTracking.addCounterPeriodChange(counterPeriod, counterValueChangeInfo);
                }

                // Fire notifications if counter value matches trigger value and counter value is tracked
                if (!isVirtual && counterPeriod.getNotificationLevels() != null) {
                    List<Entry<String, BigDecimal>> counterPeriodEventLevels = counterPeriod.getMatchedNotificationLevels(counterValueChangeInfo.getPreviousValue(), counterValueChangeInfo.getNewValue());

                    if (counterPeriodEventLevels != null && !counterPeriodEventLevels.isEmpty()) {
                        triggerCounterPeriodEvent(counterPeriod, counterPeriodEventLevels);
                    }
                }
            }
        }
        return counterValueChangeInfos;
    }

    private CounterValueChangeInfo accumulateCounterValue(CounterPeriod counterPeriod, WalletOperation walletOperation, boolean isVirtual) {

        BigDecimal previousValue = counterPeriod.getValue();
        CounterInstance counterInstance = counterPeriod.getCounterInstance();
        CounterTemplate counterTemplate = counterInstance.getCounterTemplate();
        boolean isMultiValuesAccumulator = counterPeriod.getAccumulatorType() != null && counterPeriod.getAccumulatorType().equals(AccumulatorCounterTypeEnum.MULTI_VALUE);
        boolean isMultiValuesApplied = isMultiValuesAccumulator && evaluateFilterElExpression(counterTemplate.getFilterEl(), walletOperation);

        BigDecimal value = BigDecimal.ZERO;

        if (isMultiValuesApplied) {
            value = applyMultiAccumulatedValue(counterPeriod, walletOperation);

        } else {
            if (CounterTypeEnum.USAGE_AMOUNT.equals(counterPeriod.getCounterType())) {
                value = appProvider.isEntreprise() ? walletOperation.getAmountWithoutTax() : walletOperation.getAmountWithTax();
                log.trace("Increment counter period value {} by amount {}", counterPeriod.getId() == null ? counterPeriod.getCode() : counterPeriod.getId(), value);
            } else if (CounterTypeEnum.USAGE.equals(counterPeriod.getCounterType())) {
                value = walletOperation.getQuantity();
                log.trace("Increment counter period value {} by quantity {}", counterPeriod.getId() == null ? counterPeriod.getCode() : counterPeriod.getId(), value);
            }
            counterPeriod.setValue(counterPeriod.getValue().add(value));
        }

        // AK Do not understand why this code is here
//        if (walletOperation instanceof WalletReservation) {
//            previousValue = ((WalletReservation)walletOperation).getReservation().getCounterPeriodValues().get(counterPeriod.getId());
//            if (previousValue == null) {
//                previousValue = BigDecimal.ZERO;
//            }
//            reservation.getCounterPeriodValues().put(counterPeriod.getId(), previousValue.add(value));
//            counterPeriod.setValue(reservation.getCounterPeriodValues().get(counterPeriod.getId()));
//        }

        CounterValueChangeInfo counterValueChangeInfo = new CounterValueChangeInfo(counterPeriod.getId(), counterPeriod.getAccumulator(), previousValue, value, counterPeriod.getValue());

        return counterValueChangeInfo;
    }

    /**
     * Accumulate counter multi values, Each value is stored in map with a key evaluated for an EL expression. If value can not be resolved, a value of ZERO will be considered
     *
     * @param counterPeriod the counter period
     * @param walletOperation the wallet operation
     * @return A value applied
     */
    private BigDecimal applyMultiAccumulatedValue(CounterPeriod counterPeriod, WalletOperation walletOperation) {
        CounterTemplate counterTemplate = counterPeriod.getCounterInstance().getCounterTemplate();
        BigDecimal value = evaluateValueElExpression(counterTemplate.getValueEl(), walletOperation);
        String key = evaluateKeyElExpression(counterTemplate.getKeyEl(), walletOperation);
        if (value == null || key == null) {
            return BigDecimal.ZERO;
        }
        if (counterPeriod.getAccumulatedValues() == null) {
            Map<String, BigDecimal> accumulatedValues = new HashMap<>();
            accumulatedValues.put(key, value);
            counterPeriod.setAccumulatedValues(accumulatedValues);
        } else {
            BigDecimal accumulatedValue = counterPeriod.getAccumulatedValues().get(key);
            if (accumulatedValue == null) {
                counterPeriod.getAccumulatedValues().put(key, value);
            } else {
                counterPeriod.getAccumulatedValues().put(key, accumulatedValue.add(value));
            }
        }

        log.trace("Increment counter period {} by quantity {}/{}", counterPeriod.getId() == null ? counterPeriod.getCode() : counterPeriod.getId(), key, value);

        return value;
    }

    private String evaluateKeyElExpression(String keyEl, WalletOperation walletOperation) {
        if (keyEl == null) {
            throw new BusinessException("The key EL for the counter should not be null");
        }
        Map<Object, Object> context = new HashMap<>();
        context.put(WALLET_OPERATION, walletOperation);
        return ValueExpressionWrapper.evaluateExpression(keyEl, context, String.class);
    }

    private BigDecimal evaluateValueElExpression(String valueEl, WalletOperation walletOperation) {
        if (valueEl == null) {
            throw new BusinessException("The value EL for the counter should not be null");
        }
        Map<Object, Object> context = new HashMap<>();
        context.put(WALLET_OPERATION, walletOperation);
        return ValueExpressionWrapper.evaluateExpression(valueEl, context, BigDecimal.class);
    }

    private boolean evaluateFilterElExpression(String filterEl, WalletOperation walletOperation) {
        if (filterEl == null) {
            return true;
        }
        Map<Object, Object> context = new HashMap<>();
        context.put(WALLET_OPERATION, walletOperation);
        return ValueExpressionWrapper.evaluateToBooleanIgnoreErrors(filterEl, context);
    }

    /**
     * Get a list of updated counter periods
     *
     * @return A list of updated counter periods or NULL if no updates were made
     */
    public List<CounterPeriod> getCounterUpdates() {

        Map<String, List<CounterPeriod>> counterUpdates = counterUpdatesTracking.getCounterUpdates();

        if (counterUpdates == null) {
            return null;
        }

        return counterUpdates.values().stream().flatMap(counterPeriods -> counterPeriods.stream()).collect(Collectors.toList());
    }

    /**
     * Restore virtualCounterInstances and counterUpdatesTracking values. Used when launching a new thread, request scope beans are not preserved. This restores the bean values.
     *
     * @param virtualCounters Virtual counters for the duration of the request
     * @param counterUpdates Counter updates tracking for the duration of the request
     */
    public void reestablishCounterTracking(Map<String, List<CounterPeriod>> virtualCounters, Map<String, List<CounterPeriod>> counterUpdates) {

        virtualCounterInstances.setVirtualCounters(virtualCounters);
        counterUpdatesTracking.setCounterUpdates(counterUpdates);
    }
}