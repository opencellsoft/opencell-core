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

/**
 * 
 */
package org.meveo.service.payments.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.PaymentScheduleInstance;
import org.meveo.model.payments.PaymentScheduleInstanceItem;
import org.meveo.model.payments.PaymentScheduleStatusEnum;
import org.meveo.model.payments.PaymentScheduleTemplate;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.BusinessService;
import org.meveo.service.catalog.impl.CalendarBankingService;
import org.meveo.service.catalog.impl.CalendarService;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptInterface;

/**
 * The Class PaymentScheduleInstanceService.
 *
 * @author anasseh
 * @since 5.2
 * @lastModifiedVersion 5.3
 */
@Stateless
public class PaymentScheduleInstanceService extends BusinessService<PaymentScheduleInstance> {

    /** The payment schedule instance item service. */
    @Inject
    private PaymentScheduleInstanceItemService paymentScheduleInstanceItemService;

    /** The payment schedule template service. */
    @Inject
    private PaymentScheduleTemplateService paymentScheduleTemplateService;

    /** The calendar service. */
    @Inject
    private CalendarService calendarService;

    /** The calendar service. */
    @Inject
    private CalendarBankingService calendarBankingService;

    /** The script instance service. */
    @Inject
    private ScriptInstanceService scriptInstanceService;

    /**
     * Terminate when the linked service are terminated.
     *
     * @param serviceInstance the service instance
     * @param terminationDate the termination date
     * @throws BusinessException the business exception
     */
    public void terminate(ServiceInstance serviceInstance, Date terminationDate) throws BusinessException {
        for (PaymentScheduleInstance paymentScheduleInstance : serviceInstance.getPsInstances()) {
            if (paymentScheduleInstance.getStatus() == PaymentScheduleStatusEnum.IN_PROGRESS) {
                terminate(paymentScheduleInstance, terminationDate);
                return;
            }
        }
    }

    /**
     * Terminate paymentScheduleInstance.
     *
     * @param paymentScheduleInstance the payment schedule instance
     * @param terminationDate the termination date
     * @throws BusinessException the business exception
     */
    public void terminate(PaymentScheduleInstance paymentScheduleInstance, Date terminationDate) throws BusinessException {
        if (terminationDate == null) {
            terminationDate = paymentScheduleInstance.getEndDate();
        }
        PaymentScheduleTemplate paymentScheduleTemplate = paymentScheduleTemplateService.refreshOrRetrieve(paymentScheduleInstance.getPaymentScheduleTemplate());

        if (paymentScheduleTemplate.isApplyAgreement()) {
            paymentScheduleInstance = refreshOrRetrieve(paymentScheduleInstance);
            for (PaymentScheduleInstanceItem paymentScheduleInstanceItem : paymentScheduleInstance.getPaymentScheduleInstanceItems()) {
                if (paymentScheduleInstanceItem.getRecordedInvoice() == null) {
                    paymentScheduleInstanceItemService.processItem(paymentScheduleInstanceItem);
                }
            }
        }
        paymentScheduleInstance.setEndDate(terminationDate);
        paymentScheduleInstance.setStatus(PaymentScheduleStatusEnum.TERMINATED);
        paymentScheduleInstance.setStatusDate(terminationDate);
        super.update(paymentScheduleInstance);
    }

    /**
     * Cancel paymentScheduleInstance.
     *
     * @param paymentScheduleInstance the payment schedule instance
     * @throws BusinessException the business exception
     */
    public void cancel(PaymentScheduleInstance paymentScheduleInstance) throws BusinessException {
        PaymentScheduleTemplate paymentScheduleTemplate = paymentScheduleTemplateService.refreshOrRetrieve(paymentScheduleInstance.getPaymentScheduleTemplate());
        if (paymentScheduleTemplate.isApplyAgreement()) {
            throw new BusinessException("Can't cancel a PaymentSchedule when is applyAgreement");
        }
        paymentScheduleInstance.setStatus(PaymentScheduleStatusEnum.CANCELLED);
        paymentScheduleInstance.setStatusDate(new Date());
        super.update(paymentScheduleInstance);
    }

    /* (non-Javadoc)
     * @see org.meveo.service.base.PersistenceService#update(org.meveo.model.IEntity)
     */
    @Override
    public PaymentScheduleInstance update(PaymentScheduleInstance paymentScheduleInstance) throws BusinessException {
        if (paymentScheduleInstance.getStatus() != PaymentScheduleStatusEnum.IN_PROGRESS) {
            throw new BusinessException("Can only update instance that in progress");
        }
        return super.update(paymentScheduleInstance);

    }

    /**
     * Instanciate from service.
     *
     * @param paymentScheduleTemplate the payment schedule template
     * @param serviceInstance the service instance
     * @return the payment schedule instance
     * @throws BusinessException the business exception
     */
    public PaymentScheduleInstance instanciateFromService(PaymentScheduleTemplate paymentScheduleTemplate, ServiceInstance serviceInstance) throws BusinessException {
        BigDecimal amount = paymentScheduleTemplate.getAmount();
        if (!StringUtils.isBlank(paymentScheduleTemplate.getAmountEl())) {
            amount = paymentScheduleTemplateService.evaluateAmountExpression(paymentScheduleTemplate.getAmountEl(), serviceInstance);
        }
        amount = serviceInstance.getAmountPS() == null ? amount : serviceInstance.getAmountPS();

        Integer dueDateDelay = serviceInstance.getPaymentDayInMonthPS() == null ? paymentScheduleTemplate.getPaymentDayInMonth() : serviceInstance.getPaymentDayInMonthPS();
        Long result = paymentScheduleTemplateService.evaluatePaymentDayInMonthExpression(paymentScheduleTemplate.getPaymentDayInMonthEl(), serviceInstance);
        if (result != null) {
            dueDateDelay = Math.toIntExact(result);
        }
        return instanciate(paymentScheduleTemplate, serviceInstance, amount,
                serviceInstance.getCalendarPS() == null ? paymentScheduleTemplate.getCalendar() : serviceInstance.getCalendarPS(), serviceInstance.getSubscriptionDate(),
                serviceInstance.getEndAgreementDate() == null ? serviceInstance.getSubscription().getEndAgreementDate() : serviceInstance.getEndAgreementDate(), dueDateDelay);

    }

    /**
     * Instanciate from PS instance.
     *
     * @param paymentScheduleTemplate the payment schedule template
     * @param paymentScheduleInstance the payment schedule instance
     * @return the payment schedule instance
     * @throws BusinessException the business exception
     */
    public PaymentScheduleInstance instanciateFromPsInstance(PaymentScheduleTemplate paymentScheduleTemplate, PaymentScheduleInstance paymentScheduleInstance)
            throws BusinessException {
        return instanciate(paymentScheduleTemplate, paymentScheduleInstance.getServiceInstance(), paymentScheduleInstance.getAmount(), paymentScheduleInstance.getCalendar(),
                paymentScheduleInstance.getStartDate(), paymentScheduleInstance.getEndDate(), paymentScheduleInstance.getPaymentDayInMonth());
    }

    /**
     * Instanciate PaymentScheduleInstance.
     *
     * @param paymentScheduleTemplate the payment schedule template
     * @param serviceInstance the service instance
     * @param amount the amount
     * @param calendar the calendar
     * @param startDate the start date
     * @param endDate the end date
     * @param dueDateDelay the due date delay
     * @return the payment schedule instance
     * @throws BusinessException the business exception
     */
    private PaymentScheduleInstance instanciate(PaymentScheduleTemplate paymentScheduleTemplate, ServiceInstance serviceInstance, BigDecimal amount, Calendar calendar,
            Date startDate, Date endDate, int dueDateDelay) throws BusinessException {

        if (endDate == null) {
            throw new BusinessException("Can't instanciate PaymentSchedule when EndAgreementDate is null on subscription and serviceInstance");
        }
        paymentScheduleTemplate = paymentScheduleTemplateService.refreshOrRetrieve(paymentScheduleTemplate);
        Subscription subscription = serviceInstance.getSubscription();
        calendar = calendarService.refreshOrRetrieve(calendar);
        PaymentScheduleInstance paymentScheduleInstance = new PaymentScheduleInstance();
        paymentScheduleInstance.setAmount(amount);
        paymentScheduleInstance.setCalendar(calendar);
        paymentScheduleInstance.setPaymentScheduleTemplate(paymentScheduleTemplate);
        paymentScheduleInstance.setStatus(PaymentScheduleStatusEnum.IN_PROGRESS);
        paymentScheduleInstance.setStatusDate(new Date());
        paymentScheduleInstance.setStartDate(startDate);
        paymentScheduleInstance.setEndDate(endDate);
        paymentScheduleInstance.setCode(paymentScheduleTemplate.getCode());
        paymentScheduleInstance.setDescription(paymentScheduleTemplate.getDescription());
        paymentScheduleInstance.setServiceInstance(serviceInstance);
        paymentScheduleInstance.setPaymentDayInMonth(dueDateDelay);
        create(paymentScheduleInstance);

        // Evol #3770
        ScriptInstance scriptInstance = paymentScheduleTemplate.getScriptInstance();
        if (scriptInstance != null) {
            String scriptCode = scriptInstance.getCode();
            ScriptInterface script = scriptInstanceService.getScriptInstance(scriptCode);
            Map<String, Object> methodContext = new HashMap<String, Object>();
            methodContext.put(Script.CONTEXT_ENTITY, paymentScheduleInstance);
            methodContext.put(Script.CONTEXT_ACTION, scriptCode);
            methodContext.put(Script.CONTEXT_CURRENT_USER, currentUser);
            methodContext.put(Script.CONTEXT_APP_PROVIDER, appProvider);
            if (script == null) {
                log.error("Script is null");
                throw new BusinessException("script is null");
            }

            script.execute(methodContext);
            List<PaymentScheduleInstanceItem> paymentScheduleInstanceItems = (List<PaymentScheduleInstanceItem>) methodContext.get(Script.RESULT_VALUE);

            if (paymentScheduleInstanceItems == null) {
                log.error("Script value is null");
                throw new BusinessException("Script value is null");
            }
            for (PaymentScheduleInstanceItem paymentScheduleInstanceItem : paymentScheduleInstanceItems) {
                paymentScheduleInstanceItem.setPaymentScheduleInstance(paymentScheduleInstance);
                paymentScheduleInstanceItemService.create(paymentScheduleInstanceItem);
            }

            return paymentScheduleInstance;
        }

        Calendar cal = paymentScheduleInstance.getCalendar();
        cal = CalendarService.initializeCalendar(cal, paymentScheduleInstance.getStartDate(), serviceInstance, subscription);

        Date date = paymentScheduleInstance.getStartDate();
        CustomerAccount customerAccount = subscription.getUserAccount().getBillingAccount().getCustomerAccount();
        while (date.before(paymentScheduleInstance.getEndDate())) {
            PaymentScheduleInstanceItem paymentScheduleInstanceItem = new PaymentScheduleInstanceItem();
            paymentScheduleInstanceItem.setDueDate(calendarBankingService.getNextBankWorkingDate(DateUtils.addDaysToDate(date, dueDateDelay)));
            if (paymentScheduleInstance.getPaymentScheduleTemplate().getUseBankingCalendar() == null || paymentScheduleInstance.getPaymentScheduleTemplate()
                    .getUseBankingCalendar()) {
                paymentScheduleInstanceItem.setDueDate(computeDueDate(customerAccount, paymentScheduleInstanceItem.getRequestPaymentDate()));
            }
            paymentScheduleInstanceItem.setPaymentScheduleInstance(paymentScheduleInstance);
            paymentScheduleInstanceItem.setRequestPaymentDate(computeRequestPaymentDate(customerAccount, date, dueDateDelay));
            paymentScheduleInstanceItem.setAmount(paymentScheduleInstance.getAmount());
            date = cal.nextCalendarDate(date);
            if (!date.before(paymentScheduleInstance.getEndDate())) {
                paymentScheduleInstanceItem.setLast(true);
            } else {
                paymentScheduleInstanceItem.setLast(false);
            }
            paymentScheduleInstanceItemService.create(paymentScheduleInstanceItem);

        }
        return paymentScheduleInstance;
    }

    /**
     * Compute request payment date.
     *
     * @param customerAccount the customer account
     * @param startDate       the start date
     * @param dayInMonth      the day in month
     * @return the request payment date
     */
    private Date computeRequestPaymentDate(CustomerAccount customerAccount, Date startDate, int dayInMonth) {
        Date requestPaymentDate = null;

        if (dayInMonth >= DateUtils.getDayFromDate(startDate)) {
            requestPaymentDate = DateUtils.setDayToDate(startDate, dayInMonth);
        } else {
            requestPaymentDate = DateUtils.setMonthToDate(startDate, DateUtils.getDayFromDate(startDate) + 1);
            requestPaymentDate = DateUtils.setMonthToDate(requestPaymentDate, dayInMonth);
        }

        return calendarBankingService.getPreviousBankWorkingDate(requestPaymentDate);
    }

    /**
     * Compute due date.
     *
     * @param customerAccount    the customer account
     * @param requestPaymentDate the request payment date
     * @return the due date
     * @throws BusinessException the business exception
     */
    private Date computeDueDate(CustomerAccount customerAccount, Date requestPaymentDate) throws BusinessException {
        PaymentMethod preferredMethod = customerAccount.getPreferredPaymentMethod();
        if (preferredMethod == null) {
            throw new BusinessException("preferredMethod is null");
        }
        int ndDaysBeforeDueDate = 0;
        ParamBean paramBean = paramBeanFactory.getInstance();

        if (preferredMethod.getPaymentType() == PaymentMethodEnum.CARD) {
            ndDaysBeforeDueDate = Integer.parseInt(paramBean.getProperty("paymentSchedule.nbDaysBeforeDueDate.card", "1"));
        }
        if (preferredMethod.getPaymentType() == PaymentMethodEnum.DIRECTDEBIT) {
            ndDaysBeforeDueDate = Integer.parseInt(paramBean.getProperty("paymentSchedule.nbDaysBeforeDueDate.dd", "3"));
        }
        return DateUtils.addDaysToDate(requestPaymentDate,  ndDaysBeforeDueDate);

    }

    /**
     * Find by service.
     *
     * @param serviceInstance the service instance
     * @param status the status
     * @return the list
     */
	public List<PaymentScheduleInstance> findByService(ServiceInstance serviceInstance, PaymentScheduleStatusEnum status) {
		try {
			String strQuery = "from " + PaymentScheduleInstance.class.getSimpleName() + " where serviceInstance.id =:serviceInstanceID";
			if (status != null) {
				strQuery += " and status =:statusIN";
			}
			strQuery += " order by status";
			Query query = getEntityManager().createQuery(strQuery);
			query = query.setParameter("serviceInstanceID", serviceInstance.getId());
			if (status != null) {
				query = query.setParameter("statusIN", status);
			}
			return (List<PaymentScheduleInstance>) query.getResultList();
		} catch (Exception e) {
		}
		return null;
	}

    /**
     * Replace (Remove/create) payment Schedule Instance Items.
     *
     * @param paymentScheduleInstance      the payment schedule instance.
     * @param paymentScheduleInstanceItems new Items to create
     */
    public void replacePaymentScheduleInstanceItems(PaymentScheduleInstance paymentScheduleInstance, List<PaymentScheduleInstanceItem> paymentScheduleInstanceItems) {
        List<PaymentScheduleInstanceItem> unPaidPaymentScheduleInstanceItems = null;

        if (paymentScheduleInstance.getPaymentScheduleInstanceItems() != null) {
            unPaidPaymentScheduleInstanceItems = paymentScheduleInstance.getPaymentScheduleInstanceItems().stream().filter(item -> !item.isPaid()).collect(Collectors.toList());
        }
        Date startDate = paymentScheduleInstance.getStartDate();
        if (unPaidPaymentScheduleInstanceItems != null) {
            startDate = getStartDate(unPaidPaymentScheduleInstanceItems.get(0).getDueDate());
            Set<Long> paymentScheduleInstanceItemIds = unPaidPaymentScheduleInstanceItems.stream().map(PaymentScheduleInstanceItem::getId).collect(Collectors.toSet());
            paymentScheduleInstanceItemService.remove(new HashSet<>(paymentScheduleInstanceItemIds));
        }
        ServiceInstance serviceInstance = paymentScheduleInstance.getServiceInstance();
        Subscription subscription = serviceInstance.getSubscription();

        Integer dueDateDelay = serviceInstance.getPaymentDayInMonthPS() == null ?
                paymentScheduleInstance.getPaymentScheduleTemplate().getPaymentDayInMonth() :
                serviceInstance.getPaymentDayInMonthPS();
        Long result = paymentScheduleTemplateService
                .evaluatePaymentDayInMonthExpression(paymentScheduleInstance.getPaymentScheduleTemplate().getPaymentDayInMonthEl(), serviceInstance);
        if (result != null) {
            dueDateDelay = Math.toIntExact(result);
        }
        Calendar cal = paymentScheduleInstance.getCalendar();
        cal = CalendarService.initializeCalendar(cal, paymentScheduleInstance.getStartDate(), serviceInstance, subscription);

        Date date = startDate;
        CustomerAccount customerAccount = subscription.getUserAccount().getBillingAccount().getCustomerAccount();

        int i = 0;
        for (PaymentScheduleInstanceItem paymentScheduleInstanceItem : paymentScheduleInstanceItems) {
            Date dueDate = calendarBankingService.getNextBankWorkingDate(DateUtils.addDaysToDate(date, dueDateDelay));
            if (paymentScheduleInstance.getPaymentScheduleTemplate().getUseBankingCalendar() == null || paymentScheduleInstance.getPaymentScheduleTemplate()
                    .getUseBankingCalendar()) {
                dueDate = computeDueDate(customerAccount, paymentScheduleInstanceItem.getRequestPaymentDate());
            }
            if (dueDate.before(new Date())) {
                throw new BusinessException("The due date is in past for the payment schedule item: " + paymentScheduleInstanceItem);
            }

            paymentScheduleInstanceItem.setDueDate(dueDate);
            paymentScheduleInstanceItem.setPaymentScheduleInstance(paymentScheduleInstance);

            date = cal.nextCalendarDate(date);
            if (i == paymentScheduleInstanceItems.size() - 1) {
                paymentScheduleInstanceItem.setLast(true);
                paymentScheduleInstance.setEndDate(paymentScheduleInstanceItem.getDueDate());
            } else {
                paymentScheduleInstanceItem.setLast(false);
            }
            paymentScheduleInstanceItemService.create(paymentScheduleInstanceItem);
            paymentScheduleInstance.getPaymentScheduleInstanceItems().add(paymentScheduleInstanceItem);
            i++;
        }
        update(paymentScheduleInstance);

    }

    private Date getStartDate(Date dueDate) {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTime(dueDate);
        calendar.set(java.util.Calendar.DAY_OF_MONTH, calendar.getActualMinimum(java.util.Calendar.DAY_OF_MONTH));
        return calendar.getTime();
    }
}