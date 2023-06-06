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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.EJB;
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

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ElementNotFoundException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.api.exception.InvalidELException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.ReflectionUtils;
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
import org.meveo.model.billing.Reservation;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.catalog.AccumulatorCounterTypeEnum;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.catalog.CounterTemplateLevel;
import org.meveo.model.catalog.CounterTemplateLevelAnnotation;
import org.meveo.model.catalog.CounterTypeEnum;
import org.meveo.model.crm.Customer;
import org.meveo.model.notification.Notification;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.catalog.impl.CalendarService;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.payments.impl.CustomerAccountService;

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
    private CounterInstanceService counterInstanceService;

    @EJB
    private UsageChargeInstanceService usageChargeInstanceService;

    @Inject
    private Event<CounterPeriodEvent> counterPeriodEvent;
    @Inject
    private VirtualCounterInstances virtualCounterInstances;
    @Inject
    private CounterUpdateTracking counterUpdatesTracking;
    
   
    public CounterInstance counterInstanciation(ServiceInstance serviceInstance, CounterTemplate counterTemplate, boolean isVirtual) throws BusinessException {
        CounterInstance result = null;

        if (serviceInstance == null) {
            throw new BusinessException("entity is null");
        }

        if (counterTemplate == null) {
            throw new BusinessException("counterTemplate is null");
        }

        List<Method> methods = ReflectionUtils.findAnnotatedMethods(getClass(), CounterTemplateLevelAnnotation.class);
        for (Method m : methods) {
            CounterTemplateLevelAnnotation annotation = m.getAnnotation(CounterTemplateLevelAnnotation.class);
            if (annotation.value().equals(counterTemplate.getCounterLevel())) {
                try {
                    result = (CounterInstance) m.invoke(this, serviceInstance, counterTemplate, isVirtual);
                } catch (IllegalAccessException e) {
                    throw new BusinessException(e);
                } catch (InvocationTargetException e) {
                    throw new BusinessException(e);
                }
            }
        }

        return result;
    }

    /**
     * @param serviceInstance a service instance
     * @param counterTemplate a counter template
     * @param isVirtual is virtual
     * @return a counter instance
     * @throws BusinessException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
   
    @CounterTemplateLevelAnnotation(CounterTemplateLevel.CUST)
    public CounterInstance instantiateCustomerCounter(ServiceInstance serviceInstance, CounterTemplate counterTemplate, boolean isVirtual)
            throws BusinessException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Subscription subscription = serviceInstance.getSubscription();
        if (subscription != null) {
            UserAccount userAccount = subscription.getUserAccount();
            if (userAccount != null) {
                BillingAccount billingAccount = userAccount.getBillingAccount();
                if (billingAccount != null) {
                    CustomerAccount customerAccount = billingAccount.getCustomerAccount();
                    if (customerAccount != null) {
                        return instantiateCounter(customerService, customerAccount.getCustomer(), Customer.class, counterTemplate, isVirtual);
                    }
                }
            }
        }

        return null;
    }

    /**
     * @param serviceInstance a service instance
     * @param counterTemplate a counter template
     * @param isVirtual is virtual
     * @return a counter instance
     * @throws BusinessException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
   
    @CounterTemplateLevelAnnotation(CounterTemplateLevel.CA)
    public CounterInstance instantiateCACounter(ServiceInstance serviceInstance, CounterTemplate counterTemplate, boolean isVirtual)
            throws BusinessException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        Subscription subscription = serviceInstance.getSubscription();
        if (subscription != null) {
            UserAccount userAccount = subscription.getUserAccount();
            if (userAccount != null) {
                BillingAccount billingAccount = userAccount.getBillingAccount();
                if (billingAccount != null) {
                    return instantiateCounter(customerAccountService, billingAccount.getCustomerAccount(), CustomerAccount.class, counterTemplate, isVirtual);
                }
            }
        }

        return null;
    }

    /**
     * @param serviceInstance a service instance
     * @param counterTemplate a counter template
     * @param isVirtual is virtual
     * @return a counter instance
     * @throws BusinessException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
   
    @CounterTemplateLevelAnnotation(CounterTemplateLevel.UA)
    public CounterInstance instantiateUACounter(ServiceInstance serviceInstance, CounterTemplate counterTemplate, boolean isVirtual)
            throws BusinessException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        Subscription subscription = serviceInstance.getSubscription();
        if (subscription == null) {
            return null;
        }
        UserAccount userAccount = serviceInstance.getSubscription().getUserAccount();
        return instantiateCounter(userAccountService, userAccount, UserAccount.class, counterTemplate, isVirtual);
    }

    /**
     * @param serviceInstance a service instance
     * @param counterTemplate a counter template
     * @param isVirtual is virtual
     * @return a counter instance
     * @throws BusinessException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
   
    @CounterTemplateLevelAnnotation(CounterTemplateLevel.BA)
    public CounterInstance instantiateBACounter(ServiceInstance serviceInstance, CounterTemplate counterTemplate, boolean isVirtual)
            throws BusinessException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Subscription subscription = serviceInstance.getSubscription();
        if (subscription != null) {
            UserAccount userAccount = subscription.getUserAccount();
            if (userAccount != null) {
                return instantiateCounter(billingAccountService, userAccount.getBillingAccount(), BillingAccount.class, counterTemplate, isVirtual);
            }
        }

        return null;
    }

    /**
     * @param serviceInstance a service instance
     * @param counterTemplate a counter template
     * @param isVirtual is vertual
     * @return a counter instance
     * @throws BusinessException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
   
    @CounterTemplateLevelAnnotation(CounterTemplateLevel.SU)
    public CounterInstance instantiateSubscriptionCounter(ServiceInstance serviceInstance, CounterTemplate counterTemplate, boolean isVirtual)
            throws BusinessException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return instantiateCounter(subscriptionService, serviceInstance.getSubscription(), Subscription.class, counterTemplate, isVirtual);
    }

    /**
     * @param serviceInstance a service instance
     * @param counterTemplate a counter template
     * @param isVirtual is vertual
     * @return a counter instance
     * @throws BusinessException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
   
    @CounterTemplateLevelAnnotation(CounterTemplateLevel.SI)
    public CounterInstance instantiateServiceCounter(ServiceInstance serviceInstance, CounterTemplate counterTemplate, boolean isVirtual)
            throws BusinessException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return instantiateCounter(serviceInstanceService, serviceInstance, ServiceInstance.class, counterTemplate, isVirtual);
    }

    /**
     * @param service the business service
     * @param entity the business entity
     * @param clazz the class of the business entity
     * @param counterTemplate the counter template
     * @param isVirtual is virtual
     * @return a counter instance
     * @throws BusinessException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private CounterInstance instantiateCounter(BusinessService service, ICounterEntity entity, Class clazz, CounterTemplate counterTemplate, boolean isVirtual)
            throws BusinessException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        CounterInstance result = new CounterInstance();
        if (!entity.getCounters().containsKey(counterTemplate.getCode())) {
            result.setCounterTemplate(counterTemplate);
            String methodName = ReflectionUtils.SET_PREFIX + clazz.getSimpleName();
            result.getClass().getMethod(methodName, clazz).invoke(result, entity);
            if (!isVirtual) {
                create(result);
            }

            entity.getCounters().put(counterTemplate.getCode(), result);

            if (!isVirtual) {
                service.update((BusinessEntity) entity);
            }
        } else {
            result = entity.getCounters().get(counterTemplate.getCode());
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

    /**
     * Instantiate AND persist counter period for a given date
     *
     * @param counterInstance Counter instance
     * @param chargeDate Charge date - to match the period validity dates
     * @param initDate Initial date, used for period start/end date calculation
     * @param chargeInstance Charge instance to associate counter with
     * @param serviceInstance the Service instance of charge instance
     * @return CounterPeriod instance
     * @throws BusinessException Business exception
     */
   
    // we must make sure the counter period is persisted in db before storing it in cache
    // @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW) - problem with MariaDB. See #2393 - Issue with counter period creation in MariaDB
    public CounterPeriod createPeriod(CounterInstance counterInstance, Date chargeDate, Date initDate, ChargeInstance chargeInstance, ServiceInstance serviceInstance) throws BusinessException {

        CounterPeriod counterPeriod = null;

        if (counterInstance != null) {
            CounterTemplate counterTemplate = counterInstance.getCounterTemplate();

            counterPeriod = instantiateCounterPeriod(counterTemplate, chargeDate, initDate, chargeInstance, serviceInstance);

            if (counterPeriod != null) {
                counterPeriod.setCounterInstance(counterInstance);
                counterPeriodService.create(counterPeriod);

                counterInstance.getCounterPeriods().add(counterPeriod);
                counterInstance.updateAudit(currentUser);
            }
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
     * @param serviceInstance the service instance of charge instance
     * @return a counter period.
     * @throws BusinessException the business exception
     */
    @Lock(LockType.WRITE)
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public CounterPeriod instantiateCounterPeriod(CounterTemplate counterTemplate, Date chargeDate, Date initDate, ChargeInstance chargeInstance, ServiceInstance serviceInstance) throws BusinessException {

        CounterPeriod counterPeriod = new CounterPeriod();
        Calendar cal = counterTemplate.getCalendar();
        if (!StringUtils.isBlank(counterTemplate.getCalendarCodeEl())) {
            cal = getCalendarFromEl(counterTemplate.getCalendarCodeEl(), chargeInstance, serviceInstance, chargeInstance.getSubscription());
        }
        cal = CalendarService.initializeCalendar(cal, initDate, chargeInstance, serviceInstance);
        
        Date startDate = cal.previousCalendarDate(chargeDate);
        if (startDate == null) {
            log.warn("cannot create counter for the date {} (not in calendar)", chargeDate);
            return null;
        }
        Date endDate = cal.nextCalendarDate(startDate);
        counterPeriod.setPeriodStartDate(startDate);
        counterPeriod.setPeriodEndDate(endDate);
        log.debug("create counter period from {} to {}", startDate, endDate);

        BigDecimal initialValue = counterTemplate.getCeiling();

        if (!StringUtils.isBlank(counterTemplate.getCeilingExpressionEl()) && chargeInstance != null) {
            initialValue = evaluateCeilingElExpression(counterTemplate.getCeilingExpressionEl(), chargeInstance, serviceInstance, chargeInstance.getSubscription());
        }

        counterPeriod.setValue(initialValue);
        counterPeriod.setCode(counterTemplate.getCode());
        counterPeriod.setDescription(counterTemplate.getDescription());
        counterPeriod.setLevel(initialValue);
        counterPeriod.setCounterType(counterTemplate.getCounterType());
        counterPeriod.setNotificationLevels(counterTemplate.getNotificationLevels(), initialValue);
        counterPeriod.setAccumulator(counterTemplate.getAccumulator());
        counterPeriod.isCorrespondsToPeriod(chargeDate);
        counterPeriod.setAccumulatorType(counterTemplate.getAccumulatorType());

        return counterPeriod;
    }

    /**
     * trigger counter period event
     *
     * @param counterValueChangeInfo the counter value
     * @param counterPeriod the counter period
     */
    public void triggerCounterPeriodEvent(CounterValueChangeInfo counterValueChangeInfo, CounterPeriod counterPeriod) {
        // Fire notifications if counter value matches trigger value and counter value is tracked
        if (counterValueChangeInfo != null && counterPeriod.getNotificationLevels() != null) {
            // Need to refresh counterPeriod as it is stale object if it was updated in counterInstanceService.deduceCounterValue()
            counterPeriod = emWrapper.getEntityManager().find(CounterPeriod.class, counterPeriod.getId());
            List<Entry<String, BigDecimal>> counterPeriodEventLevels = counterPeriod.getMatchedNotificationLevels(counterValueChangeInfo.getPreviousValue(), counterValueChangeInfo.getNewValue());

            if (counterPeriodEventLevels != null && !counterPeriodEventLevels.isEmpty()) {
                triggerCounterPeriodEvent(counterPeriod, counterPeriodEventLevels);
            }
        }
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

    private CounterPeriod getCounterPeriodByDate(CounterInstance counterInstance, Date date) throws NoResultException {
        Query query = getEntityManager().createNamedQuery("CounterPeriod.findByPeriodDate");
        query.setParameter("counterInstance", counterInstance);
        query.setParameter("date", date, TemporalType.TIMESTAMP);

        return (CounterPeriod) query.getSingleResult();
    }

    /**
     * Find a counter period for a given date.
     *
     * @param counterInstance Counter instance
     * @param date Date to match
     * @return Found counter period
     * @throws BusinessException business exception
     */
    @Lock(LockType.WRITE)
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public CounterPeriod getCounterPeriod(CounterInstance counterInstance, Date date) throws BusinessException {
        try {
            CounterPeriod counterPeriod = null;
            if (counterInstance != null) {
                counterPeriod = getCounterPeriodByDate(counterInstance, date);
            }
            return counterPeriod;
        } catch (NoResultException e) {
            return null;
        }
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
    @Lock(LockType.WRITE)
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public CounterPeriod getOrCreateCounterPeriod(CounterInstance counterInstance, Date date, Date initDate, ChargeInstance chargeInstance, ServiceInstance serviceInstance) throws BusinessException {
        try {
            return getCounterPeriodByDate(counterInstance, date);
        } catch (NoResultException e) {
            return createPeriod(counterInstance, date, initDate, chargeInstance, serviceInstance);
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
     * Deduce a given value from a counter. Will instantiate a counter period if one was not created yet matching the given date
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
        CounterPeriod counterPeriod = getOrCreateCounterPeriod(counterInstance, date, initDate, null, null);
        if (counterPeriod == null || counterPeriod.getValue().compareTo(value) < 0) {
            throw new CounterValueInsufficientException();

        } else {
            counterPeriod.setValue(counterPeriod.getValue().subtract(value));
            counterPeriod.updateAudit(currentUser);
            return counterPeriod.getValue();
        }
    }

    /**
     * Decrease counter period by a given value. If given amount exceeds current value, only partial amount will be deduced. NOTE: counterPeriod passed to the method will become
     * stale if it happens to be updated in this method
     *
     * @param counterPeriod Counter period
     * @param deduceBy Amount to decrease by
     * @param isVirtual Is this a virtual operation - no counter period entity exists nor should be persisted
     * @return CounterValueChangeInfo, the actual deduced value and new counter value. or NULL if value is not tracked (initial counter value is not set)
     * @throws BusinessException business exception
     */
    @Lock(LockType.WRITE)
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public CounterValueChangeInfo deduceCounterValue(CounterPeriod counterPeriod, BigDecimal deduceBy, boolean isVirtual) throws BusinessException {

        CounterValueChangeInfo counterValueInfo = null;

        BigDecimal deducedQuantity = null;
        BigDecimal previousValue = counterPeriod.getValue();
        // if is an accumulator counter, return the full quantity
        if (counterPeriod.getAccumulator() != null && counterPeriod.getAccumulator()) {
            return new CounterValueChangeInfo(previousValue, deduceBy, counterPeriod.getValue());
        }
        // No initial value, so no need to track present value (will always be able to deduce by any amount) and thus no need to update
        if (counterPeriod.getLevel() == null) {
            if (!isVirtual) {
                counterPeriodService.detach(counterPeriod);
            }
            return null;

            // Previous value is Zero and deduction is not negative (really its an addition)
        } else if (previousValue.compareTo(BigDecimal.ZERO) == 0 && deduceBy.compareTo(BigDecimal.ZERO) > 0) {
            return new CounterValueChangeInfo(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);

        } else {
            if (previousValue.compareTo(deduceBy) < 0) {
                deducedQuantity = counterPeriod.getValue();
                counterPeriod.setValue(BigDecimal.ZERO);

            } else {
                deducedQuantity = deduceBy;
                counterPeriod.setValue(counterPeriod.getValue().subtract(deduceBy));
            }

            counterValueInfo = new CounterValueChangeInfo(previousValue, deducedQuantity, counterPeriod.getValue());

            if (!isVirtual) {
                log.debug("Counter period {} was changed {}", counterPeriod.getId(), counterValueInfo);
                counterPeriodService.update(counterPeriod);
            }
        }

        return counterValueInfo;
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
     * @param chargeInstance
     * @param serviceInstance
     * @param subscription
     * @return
     * @throws BusinessException
     */
    public Calendar getCalendarFromEl(String calendarCodeEl, ChargeInstance chargeInstance, ServiceInstance serviceInstance, Subscription subscription) throws BusinessException {
        String calendarCode = evaluateCalendarElExpression(calendarCodeEl, chargeInstance, serviceInstance, subscription);
        Calendar calendar = calendarService.findByCode(calendarCode);
        if (calendar == null) {
            throw new BusinessException("Cant found calendar by code:" + calendarCode);
        }
        return calendar;
    }

    public String evaluateCalendarElExpression(String expression, ChargeInstance chargeInstance, ServiceInstance serviceInstance, Subscription subscription)
            throws BusinessException {

        String result = null;
        if (StringUtils.isBlank(expression)) {
            return result;
        }

        Map<Object, Object> userMap = new HashMap<Object, Object>();
        if (expression.indexOf(CHARGE) >= 0 || expression.indexOf("ci") >= 0) {
            userMap.put(CHARGE, chargeInstance);
            userMap.put("ci", chargeInstance);
        }
        if (expression.indexOf(SERVICE) >= 0 || expression.indexOf(SERVICE_INSTANCE) >= 0) {
            userMap.put(SERVICE, serviceInstance);
            userMap.put(SERVICE_INSTANCE, serviceInstance);
        }
        if (expression.indexOf("sub") >= 0) {
            userMap.put("sub", subscription);
        }

        Object res = ValueExpressionWrapper.evaluateExpression(expression, userMap, String.class);
        try {
            result = (String) res;
        } catch (Exception e) {
            throw new BusinessException("Expression " + expression + " do not evaluate to String but " + res);
        }
        return result;
    }

    public BigDecimal evaluateCeilingElExpression(String expression, ChargeInstance chargeInstance, ServiceInstance serviceInstance, Subscription subscription) throws BusinessException {

        if (StringUtils.isBlank(expression)) {
            return null;
        }
        Map<Object, Object> userMap = new HashMap<>();
        if (expression.contains(CHARGE) || expression.contains("ci")) {
            userMap.put(CHARGE, chargeInstance);
            userMap.put("ci", chargeInstance);
        }
        if (expression.contains(SERVICE) || expression.contains(SERVICE_INSTANCE)) {
            userMap.put(SERVICE, serviceInstance);
            userMap.put(SERVICE_INSTANCE, serviceInstance);
        }
        if (expression.contains("sub")) {
            userMap.put("sub", subscription);
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
     * Increment counter period by a given value.
     *
     * @param periodId Counter period identifier
     * @param incrementBy Increment by
     * @param reservation the reservation
     * @return The new value, or NULL if value is not tracked (initial value is not set)
     * @throws BusinessException business exception
     */
    @Lock(LockType.WRITE)
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public BigDecimal incrementCounterValue(Long periodId, BigDecimal incrementBy, Reservation reservation) throws BusinessException {

        CounterPeriod counterPeriod = counterPeriodService.findById(periodId);
        if (counterPeriod == null) {
            return null;
        }

        if (counterPeriod.getCounterType().equals(CounterTypeEnum.USAGE)) {
            CounterValueChangeInfo counterValueChangeInfo = deduceCounterValue(counterPeriod, incrementBy.negate(), false);
            // Value is not tracked
            if (counterValueChangeInfo == null) {
                return null;
            } else {
                return counterValueChangeInfo.getNewValue();
            }

        } else if (counterPeriod.getCounterType().equals(CounterTypeEnum.USAGE_AMOUNT)) {
            BigDecimal amount;
            if (appProvider.isEntreprise()) {
                amount = reservation.getAmountWithoutTax();
            } else {
                amount = reservation.getAmountWithTax();
            }
            counterPeriod.setValue(counterPeriod.getValue().subtract(amount));
            counterPeriod = counterPeriodService.update(counterPeriod);
            log.debug("Counter period {} was decremented by {} to {}", counterPeriod.getId(), amount, counterPeriod.getValue());
            reservation.getCounterPeriodValues().put(counterPeriod.getId(), counterPeriod.getValue());
            return counterPeriod.getValue();
        } else {
            counterPeriod.setValue(counterPeriod.getValue().add(incrementBy));
            counterPeriod = counterPeriodService.update(counterPeriod);
            log.debug("Counter period {} was incremented by {} to {}", counterPeriod.getId(), incrementBy, counterPeriod.getValue());
            return counterPeriod.getValue();
        }
    }

    @Lock(LockType.WRITE)
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<CounterValueChangeInfo> incrementAccumulatorCounterValue(ChargeInstance chargeInstance, List<WalletOperation> walletOperations, boolean isVirtual) {

        List<CounterValueChangeInfo> counterValueChangeInfos = new ArrayList<CounterValueChangeInfo>();

        for (CounterInstance counterInstance : chargeInstance.getCounterInstances()) {

            CounterPeriod counterPeriod = null;

            for (WalletOperation wo : walletOperations) {

                // In case of virtual operation only instantiate a counter period, don't create it
                if (isVirtual) {
                    counterPeriod = getOrCreateCounterPeriodVirtual(counterInstance, wo.getOperationDate(), chargeInstance.getServiceInstance().getSubscriptionDate(), chargeInstance);

                } else {
                    counterPeriod = getOrCreateCounterPeriod(counterInstance, wo.getOperationDate(), chargeInstance.getServiceInstance().getSubscriptionDate(), chargeInstance, null);
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
    /**
     * Find or create a counter period for a given date.
     *
     * @param counterInstance Counter instance
     * @param date Date to match
     * @param initDate initial date.
     * @param chargeInstance Charge instance to associate counter with
     * @param serviceInstance the Service instance of charge instance
     * @return Found or created counter period
     * @throws CounterInstantiationException Failure to create counter period
     */
    private CounterPeriod getOrCreateCounterPeriodVirtual(CounterInstance counterInstance, Date date, Date initDate, ChargeInstance chargeInstance) {
        CounterPeriod counterPeriod = getCounterPeriodVirtualByDate(counterInstance.getId(), counterInstance.getCode(), date);

        if (counterPeriod != null) {
            return counterPeriod;
        }
        return createPeriodVirtual(counterInstance, date, initDate, chargeInstance);
    }
    
    /**
     * Instantiate only a counter period. Note: Will not be persisted
     *
     * @param counterTemplate Counter template
     * @param chargeDate Charge date
     * @param initDate Initial date, used for period start/end date calculation
     * @param chargeInstance charge instance to associate counter with
     * @return a counter period or NULL if counter period can not be created because of calendar limitations
     * @throws CounterInstantiationException Failure to create counter period
     */
    @Lock(LockType.WRITE)
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private CounterPeriod instantiateCounterPeriod(CounterTemplate counterTemplate, Date chargeDate, Date initDate, ChargeInstance chargeInstance) {

        CounterPeriod counterPeriod = new CounterPeriod();
        Calendar cal = counterTemplate.getCalendar();
        if (!StringUtils.isBlank(counterTemplate.getCalendarCodeEl())) {
            cal = getCalendarFromEl(counterTemplate.getCalendarCodeEl(), chargeInstance);
        }
        try {
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
            counterPeriod.setLevel(initialValue);

            counterPeriod.setCode(counterTemplate.getCode());
            counterPeriod.setDescription(counterTemplate.getDescription());
            counterPeriod.setCounterType(counterTemplate.getCounterType());
            counterPeriod.setAccumulator(counterTemplate.getAccumulator());
            counterPeriod.setAccumulatorType(counterTemplate.getAccumulatorType());
            counterPeriod.setNotificationLevels(counterTemplate.getNotificationLevels(), initialValue);

            return counterPeriod;

        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }
    }
    
    private BigDecimal evaluateCeilingElExpression(String expression, ChargeInstance chargeInstance) throws InvalidELException {

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
     * Gets the calendar from EL
     *
     * @param calendarCodeEl the calendar code EL
     * @param chargeInstance Charge instance
     * @return Calendar
     * @throws InvalidELException Invalid EL expression
     * @throws ElementNotFoundException Calendar was not found
     */
    private Calendar getCalendarFromEl(String calendarCodeEl, ChargeInstance chargeInstance) throws InvalidELException, ElementNotFoundException {
        String calendarCode = evaluateCalendarElExpression(calendarCodeEl, chargeInstance);
        Calendar calendar = calendarService.findByCode(calendarCode);
        if (calendar == null) {
            throw new ElementNotFoundException(calendarCode, "Calendar");
        }
        return calendar;
    }
    
    private String evaluateCalendarElExpression(String expression, ChargeInstance chargeInstance) throws InvalidELException {

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
            throw new InvalidELException("Expression " + expression + " do not evaluate to String but " + res, userMap, e);
        }
        return result;
    }
    /**
     * Instantiate AND persist <b>for duration of the request</b> a counter period for a given date
     *
     * @param counterInstance Counter instance
     * @param chargeDate Charge date - to match the period validity dates
     * @param initDate Initial date, used for period start/end date calculation
     * @param chargeInstance Charge instance to associate counter with
     * @return CounterPeriod instance or NULL if counter period can not be created because of calendar limitations
     * @throws CounterInstantiationException Failure to create a counter period
     */
    private CounterPeriod createPeriodVirtual(CounterInstance counterInstance, Date chargeDate, Date initDate, ChargeInstance chargeInstance) {

        CounterPeriod counterPeriod = null;
        // It is a pure virtual counter instance as when simulating rating from quote
        if (counterInstance.getId() == null) {
            CounterTemplate counterTemplate = counterInstance.getCounterTemplate();

            counterPeriod = instantiateCounterPeriod(counterTemplate, chargeDate, initDate, chargeInstance);

            if (counterPeriod != null) {
                counterPeriod.setCounterInstance(counterInstance);
            }

            // It is a real counter instance, just need to create a copy of the counter period for a virtual rating purpose. This is done so counter current values are the same.
        } else {

            CounterPeriod realCounterPeriod = getOrCreateCounterPeriod(counterInstance, chargeDate, initDate, chargeInstance, null);

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
     * Set the accumulator counter period value.
     *
     * @param counterPeriod the counter period
     * @param walletOperation the wallet operation
     * @param reservation the reservation
     * @param isVirtual whether the operation is virtual or not
     */
    @Lock(LockType.WRITE)
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void accumulatorCounterPeriodValue(CounterPeriod counterPeriod, WalletOperation walletOperation, Reservation reservation, boolean isVirtual) {
        BigDecimal value = BigDecimal.ZERO;
        BigDecimal previousValue = counterPeriod.getValue();
        CounterInstance counterInstance = counterInstanceService.retrieveIfNotManaged(counterPeriod.getCounterInstance());
        CounterTemplate counterTemplate = counterInstance.getCounterTemplate();
        boolean isMultiValuesAccumulator = counterPeriod.getAccumulatorType() != null && counterPeriod.getAccumulatorType().equals(AccumulatorCounterTypeEnum.MULTI_VALUE);
        boolean isMultiValuesApplied = isMultiValuesAccumulator && evaluateFilterElExpression(counterTemplate.getFilterEl(),walletOperation);
        if (counterPeriod.getAccumulator() != null && counterPeriod.getAccumulator()) {
            if (CounterTypeEnum.USAGE_AMOUNT.equals(counterPeriod.getCounterType())) {
                value = appProvider.isEntreprise() ? walletOperation.getAmountWithoutTax() : walletOperation.getAmountWithTax();
                log.debug("Increment counter period value {} by amount {}", counterPeriod, value);
            } else if (CounterTypeEnum.USAGE.equals(counterPeriod.getCounterType())) {
                value = walletOperation.getQuantity();
                log.debug("Increment counter period value {} by quantity {}", counterPeriod, value);
            }
            if (isMultiValuesApplied) {
                value = applyMultiAccumulatedValues(counterPeriod, walletOperation);
            } else{
                counterPeriod.setValue(counterPeriod.getValue().add(value));
            }

            if (reservation != null) {
                previousValue = reservation.getCounterPeriodValues().get(counterPeriod.getId());
                if (previousValue == null) {
                    previousValue = BigDecimal.ZERO;
                }
                reservation.getCounterPeriodValues().put(counterPeriod.getId(), previousValue.add(value));
                counterPeriod.setValue(reservation.getCounterPeriodValues().get(counterPeriod.getId()));
            }
            if (!isVirtual) {
                log.debug("Counter period {} was changed {}", counterPeriod.getId(), counterPeriod.getValue());
                counterPeriodService.update(counterPeriod);
            }
            CounterValueChangeInfo counterValueChangeInfo = new CounterValueChangeInfo(previousValue, value, counterPeriod.getValue());
            triggerCounterPeriodEvent(counterValueChangeInfo, counterPeriod);
        }

    }

    /**
     * Accumulate counter multi values, Each value is stored in map with a key evaluated for an EL expression
     * @param counterPeriod the counter period
     * @param walletOperation the wallet operation
     * @return
     */
    private BigDecimal applyMultiAccumulatedValues(CounterPeriod counterPeriod, WalletOperation walletOperation) {
        CounterInstance counterInstance = counterInstanceService.retrieveIfNotManaged(counterPeriod.getCounterInstance());
        CounterTemplate counterTemplate = counterInstance.getCounterTemplate();
        BigDecimal value = evaluateValueElExpression(counterTemplate.getValueEl(), walletOperation);
        log.debug("Extract the multi accumulator counter period value {}", value);
        String key = evaluateKeyElExpression(counterTemplate.getKeyEl(), walletOperation);
        log.debug("Extract the multi accumulator counter period key {}", key);
        if(counterPeriod.getAccumulatedValues() == null){
            Map<String, BigDecimal> accumulatedValues = new HashMap<>();
            accumulatedValues.put(key, value);
            counterPeriod.setAccumulatedValues(accumulatedValues);
        }else{
            BigDecimal accumulatedValue = counterPeriod.getAccumulatedValues().get(key);
            if(accumulatedValue == null){
                counterPeriod.getAccumulatedValues().put(key, value);
            }else{
                counterPeriod.getAccumulatedValues().put(key, accumulatedValue.add(value));
            }
        }
        log.debug("Icrement the multi accumulator counter period values {}", counterPeriod.getAccumulatedValues());
        return value;
    }

    private String evaluateKeyElExpression(String keyEl, WalletOperation walletOperation) {
        if(keyEl == null){
            throw new BusinessException("The key EL for the counter should not be null");
        }
        Map<Object, Object> context = new HashMap<>();
        context.put(WALLET_OPERATION, walletOperation);
        return ValueExpressionWrapper.evaluateExpression(keyEl, context, String.class);
    }

    private BigDecimal evaluateValueElExpression(String valueEl, WalletOperation walletOperation) {
        if(valueEl == null){
            throw new BusinessException("The value EL for the counter should not be null");
        }
        Map<Object, Object> context = new HashMap<>();
        context.put(WALLET_OPERATION, walletOperation);
        return ValueExpressionWrapper.evaluateExpression(valueEl, context, BigDecimal.class);
    }

    private boolean evaluateFilterElExpression(String filterEl, WalletOperation walletOperation) {
        if(filterEl == null){
            return true;
        }
        Map<Object, Object> context = new HashMap<>();
        context.put(WALLET_OPERATION, walletOperation);
        return ValueExpressionWrapper.evaluateToBooleanIgnoreErrors(filterEl, context);
    }
    
    @SuppressWarnings("unchecked")
    public List<Long> findByCounterAndAccount(String counterTemplateCode,CounterTemplateLevel level) { 
    	List<Long> ids=new ArrayList<>();
    	try {
    		if(CounterTemplateLevel.CA.equals(level)){
    			ids = (List<Long>)getEntityManager().createNamedQuery("CounterInstance.findByCounterAndCustomer")
    					.setParameter("counterTemplateCode", counterTemplateCode).getResultList();
    		}
    		if(CounterTemplateLevel.CUST.equals(level)){ 
    			ids = (List<Long>)getEntityManager().createNamedQuery("CounterInstance.findByCounterAndCustomerAccount")
    					.setParameter("counterTemplateCode", counterTemplateCode).getResultList();
    		}
    		if(CounterTemplateLevel.BA.equals(level)) {
    			ids = (List<Long>)getEntityManager().createNamedQuery("CounterInstance.findByCounterAndBillingAccount")
    					.setParameter("counterTemplateCode", counterTemplateCode).getResultList();
    		}
    		if(CounterTemplateLevel.UA.equals(level)){ 
    			ids = (List<Long>)getEntityManager().createNamedQuery("CounterInstance.findByCounterAndUserAccount")
    					.setParameter("counterTemplateCode", counterTemplateCode).getResultList();
    		}
    		if(CounterTemplateLevel.SU.equals(level)){ 
    			ids = (List<Long>)getEntityManager().createNamedQuery("CounterInstance.findByCounterAndSubscription")
    					.setParameter("counterTemplateCode", counterTemplateCode).getResultList();
    		}
    		if(CounterTemplateLevel.SI.equals(level)){
    			ids = (List<Long>)getEntityManager().createNamedQuery("CounterInstance.findByCounterAndService")
    					.setParameter("counterTemplateCode", counterTemplateCode).getResultList();
    		}


    	} catch (Exception e) {
    		log.error("findByCounterAndAccounts error ", e.getMessage());
    	}

    	return ids;
    }
    
    public CounterPeriod instanciateOrRetrieveCounterPeriod(CounterInstance counterInstance, Date date, Date initDate, ChargeInstance chargeInstance, ServiceInstance serviceInstance) throws BusinessException {
        // Same transaction creation of counter period
    	try {
            return getCounterPeriodByDate(counterInstance, date);
        } catch (NoResultException e) {
            return createPeriod(counterInstance, date, initDate, chargeInstance, serviceInstance);
        }
    }
    

}