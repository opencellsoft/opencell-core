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

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
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
import org.meveo.model.crm.Provider;
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

    public CounterInstance counterInstanciation(UserAccount userAccount, CounterTemplate counterTemplate, boolean isVirtual, User creator) throws BusinessException {
        CounterInstance result = null;

        if (userAccount == null) {
            throw new BusinessException("userAccount is null");
        }

        if (counterTemplate == null) {
            throw new BusinessException("counterTemplate is null");
        }

        if (creator == null) {
            throw new BusinessException("creator is null");
        }

        // we instanciate the counter only if there is no existing instance for
        // the same template
        if (counterTemplate.getCounterLevel() == CounterTemplateLevel.BA) {
            BillingAccount billingAccount = userAccount.getBillingAccount();
            if (!billingAccount.getCounters().containsKey(counterTemplate.getCode())) {
                result = new CounterInstance();
                result.setCounterTemplate(counterTemplate);
                result.setBillingAccount(billingAccount);
                
                if (!isVirtual){
                    create(result, creator); // AKK was with billingAccount.getProvider()
                }
                
                billingAccount.getCounters().put(counterTemplate.getCode(), result);
                
                if (!isVirtual){
                    billingAccountService.update(billingAccount, creator);
                }
            } else {
                result = userAccount.getBillingAccount().getCounters().get(counterTemplate.getCode());
            }
        } else {
            if (!userAccount.getCounters().containsKey(counterTemplate.getCode())) {
                result = new CounterInstance();
                result.setCounterTemplate(counterTemplate);
                result.setUserAccount(userAccount);

                if (!isVirtual){
                    create(result, creator); // AKK was with userAccount.getProvider()
                }
                userAccount.getCounters().put(counterTemplate.getCode(), result);

                if (!isVirtual){
                    userAccountService.update(userAccount, creator);
                }
            } else {
                result = userAccount.getCounters().get(counterTemplate.getCode());
            }
        }

        return result;
    }

    public CounterInstance counterInstanciation(Notification notification, CounterTemplate counterTemplate, User creator) throws BusinessException {
        return counterInstanciation(getEntityManager(), notification, counterTemplate, creator);
    }

    public CounterInstance counterInstanciation(EntityManager em, Notification notification, CounterTemplate counterTemplate, User creator) throws BusinessException {
        CounterInstance counterInstance = null;

        if (notification == null) {
            throw new BusinessException("notification is null");
        }

        if (counterTemplate == null) {
            throw new BusinessException("counterTemplate is null");
        }

        if (creator == null) {
            throw new BusinessException("creator is null");
        }

        // Remove current counter instance if it does not match the counter
        // template to be instantiated
        if (notification.getCounterInstance() != null && !counterTemplate.getId().equals(notification.getCounterInstance().getCounterTemplate().getId())) {
            CounterInstance ci = notification.getCounterInstance();
            notification.setCounterInstance(null);
            remove(ci, creator);
        }

        // Instantiate counter instance if there is not one yet
        if (notification.getCounterInstance() == null) {
            counterInstance = new CounterInstance();
            counterInstance.setCounterTemplate(counterTemplate);
            create(counterInstance, creator); // AKK was with notification.getProvider()

            notification.setCounterTemplate(counterTemplate);
            notification.setCounterInstance(counterInstance);
        } else {
            counterInstance = notification.getCounterInstance();
        }

        return counterInstance;
    }

    //we must make sure the counter period is persisted in db before storing it in cache
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public CounterPeriod createPeriod(CounterInstance counterInstance, Date chargeDate, Date initDate, UsageChargeInstance usageChargeInstance, User currentUser)
            throws BusinessException {
        refresh(counterInstance);
        counterInstance = (CounterInstance) attach(counterInstance);

        CounterPeriod counterPeriod = instantiateCounterPeriod(counterInstance.getCounterTemplate(), chargeDate, initDate, usageChargeInstance);                                
        counterPeriod.setCounterInstance(counterInstance);
        counterPeriod.setProvider(counterInstance.getProvider());
        counterPeriodService.create(counterPeriod, counterInstance.getAuditable().getCreator()); // AKK was with counterInstance.getProvider()

        counterInstance.getCounterPeriods().add(counterPeriod);
        counterInstance.updateAudit(currentUser);

        return counterPeriod;
    }

    public CounterPeriod instantiateCounterPeriod(CounterTemplate counterTemplate, Date chargeDate, Date initDate, UsageChargeInstance usageChargeInstance) throws BusinessException {
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
            initialValue = evaluateCeilingElExpression(counterTemplate.getCeilingExpressionEl(), usageChargeInstance,
                usageChargeInstance.getServiceInstance(), usageChargeInstance.getSubscription());
        }
        counterPeriod.setPeriodStartDate(startDate);
        counterPeriod.setPeriodEndDate(endDate);
        counterPeriod.setValue(initialValue);
        counterPeriod.setCode(counterTemplate.getCode());
        counterPeriod.setDescription(counterTemplate.getDescription());
        counterPeriod.setLevel(initialValue);
        counterPeriod.setCounterType(counterTemplate.getCounterType());
        counterPeriod.setProvider(counterTemplate.getProvider());
        counterPeriod.setNotificationLevels(counterTemplate.getNotificationLevels(), initialValue);
        return counterPeriod;
    }

    /**
     * Find or create a counter period for a given date
     * 
     * @param counterInstance Counter instance
     * @param date Date to match
     * @param currentUser User performing operation
     * @return Found or created counter period
     * @throws BusinessException
     */
    public CounterPeriod getCounterPeriod(CounterInstance counterInstance, Date date, Date initDate, User currentUser) throws BusinessException {
        Query query = getEntityManager().createNamedQuery("CounterPeriod.findByPeriodDate");
        query.setParameter("counterInstance", counterInstance);
        query.setParameter("date", date, TemporalType.TIMESTAMP);

        try {
            return (CounterPeriod) query.getSingleResult();
        } catch (NoResultException e) {
            return createPeriod(counterInstance, date, initDate, null, currentUser);
        }
    }

    /**
     * Update counter period value
     * 
     * @param counterPeriodId Counter period identifier
     * @param value Value to set to
     * @param currentUser User performing an action
     * @throws BusinessException
     */
    public void updatePeriodValue(Long counterPeriodId, BigDecimal value, User currentUser) throws BusinessException {
        CounterPeriod counterPeriod = counterPeriodService.findById(counterPeriodId);

        if (counterPeriod == null) {
            throw new BusinessException("CounterPeriod with id=" + counterPeriodId + " does not exists.");
        }

        counterPeriod.setValue(value);
        counterPeriod.updateAudit(currentUser);
    }

    /**
     * Deduce a given value from a counter
     * 
     * @param counterInstance Counter instance
     * @param date Date of event
     * @param value Value to deduce
     * @param currentUser User performing an action
     * @return
     * @throws CounterValueInsufficientException
     * @throws BusinessException
     */
    public BigDecimal deduceCounterValue(CounterInstance counterInstance, Date date, Date initDate, BigDecimal value, User currentUser) throws CounterValueInsufficientException,
            BusinessException {

        CounterPeriod counterPeriod = getCounterPeriod(counterInstance, date, initDate, currentUser);

        if (counterPeriod == null || counterPeriod.getValue().compareTo(value) < 0) {
            throw new CounterValueInsufficientException();

        } else {
            counterPeriod.setValue(counterPeriod.getValue().subtract(value));
            counterPeriod.updateAudit(currentUser);
            return counterPeriod.getValue();
        }
    }

    @SuppressWarnings("unchecked")
    public List<CounterInstance> findByCounterTemplate(CounterTemplate counterTemplate) {
        QueryBuilder qb = new QueryBuilder(CounterInstance.class, "c");
        qb.addCriterionEntity("counterTemplate", counterTemplate);
        return qb.find(getEntityManager());
    }

    public BigDecimal evaluateCeilingElExpression(String expression, ChargeInstance charge, ServiceInstance serviceInstance, Subscription subscription) throws BusinessException {
        int rounding = subscription.getProvider().getRounding() == null ? 3 : subscription.getProvider().getRounding();
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
     * Count counter periods which end date is older then a given date
     * 
     * @param date Date to check
     * @param provider Provider
     * @return A number of counter periods which end date is older then a given date
     */
    public long countCounterPeriodsToDelete(Date date, Provider provider) {
        long result = 0;
        String sql = "select cp from CounterPeriod cp";
        QueryBuilder qb = new QueryBuilder(sql);
        qb.addCriterion("cp.periodEndDate", "<", date, false);
        qb.addCriterionEntity("cp.provider", provider);
        result = qb.count(getEntityManager());

        return result;
    }

    /**
     * Remove counter periods which end date is older then a given date
     * 
     * @param date Date to check
     * @param provider Provider
     * @return A number of counter periods that were removed
     */
    @SuppressWarnings("unchecked")
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public long deleteCounterPeriods(Date date, Provider provider) {
        log.trace("Removing counter periods which end date is older then a {} date for provider {}", date, provider);
        long itemsDeleted = 0;
        String sql = "select cp from CounterPeriod cp";
        QueryBuilder qb = new QueryBuilder(sql);
        qb.addCriterion("cp.periodEndDate", "<", date, false);
        qb.addCriterionEntity("cp.provider", provider);
        EntityManager em = getEntityManager();
        List<CounterPeriod> periods = qb.find(em);
        for (CounterPeriod counterPeriod : periods) {
            em.remove(counterPeriod);
            itemsDeleted++;
        }

        log.info("Removed {} counter periods which end date is older then a {} date for provider {}", itemsDeleted, date, provider);

        return itemsDeleted;
    }
}