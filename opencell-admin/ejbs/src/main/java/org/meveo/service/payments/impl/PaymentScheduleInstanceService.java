/**
 * 
 */
package org.meveo.service.payments.impl;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.ParamBean;
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
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.BusinessService;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.catalog.impl.CalendarService;

/**
 * The Class PaymentScheduleInstanceService.
 *
 * @author anasseh
 * @since 5.2
 */
@Stateless
public class PaymentScheduleInstanceService extends BusinessService<PaymentScheduleInstance> {

    @Inject
    private PaymentScheduleInstanceItemService paymentScheduleInstanceItemService;

    @Inject
    private PaymentScheduleTemplateService paymentScheduleTemplateService;

    @Inject
    private SubscriptionService subscriptionService;
    
    @Inject
    private  CalendarService calendarService;

    @Override
    public PaymentScheduleInstance update(PaymentScheduleInstance paymentScheduleInstance) throws BusinessException {
        PaymentScheduleInstance oldPaymentScheduleInstance = findById(paymentScheduleInstance.getId());
        if (oldPaymentScheduleInstance.getStatus() != PaymentScheduleStatusEnum.IN_PROGRESS) {
            throw new BusinessException("Can only update instance that in progress");
        }
        if (paymentScheduleInstance.getStatus() == PaymentScheduleStatusEnum.IN_PROGRESS) {
            oldPaymentScheduleInstance.setStatus(PaymentScheduleStatusEnum.OBSOLETE);
            oldPaymentScheduleInstance.setStatusDate(new Date());
            super.update(oldPaymentScheduleInstance);
            return instanciateFromInstance(paymentScheduleInstance.getPaymentScheduleTemplate(), paymentScheduleInstance);
        } else {
            return super.update(paymentScheduleInstance);
        }

    }

    /**
     * @param paymentScheduleInstance
     * @throws BusinessException
     */
    public void cancel(PaymentScheduleInstance paymentScheduleInstance) throws BusinessException {

        paymentScheduleInstance.setStatus(PaymentScheduleStatusEnum.CANCELLED);
        paymentScheduleInstance.setStatusDate(new Date());
        update(paymentScheduleInstance);
    }

    public PaymentScheduleInstance instanciateFromService(PaymentScheduleTemplate paymentScheduleTemplate, ServiceInstance serviceInstance) throws BusinessException {
        return instanciate(paymentScheduleTemplate, serviceInstance, serviceInstance.getAmountPS() == null ? paymentScheduleTemplate.getAmount() : serviceInstance.getAmountPS(),
            serviceInstance.getCalendarPS() == null ? paymentScheduleTemplate.getCalendar() : serviceInstance.getCalendarPS(), serviceInstance.getSubscriptionDate(),
            serviceInstance.getEndAgreementDate() == null ? serviceInstance.getSubscription().getEndAgreementDate() : serviceInstance.getEndAgreementDate(),
            serviceInstance.getDueDateDaysPS() == null ? paymentScheduleTemplate.getDueDateDays() : serviceInstance.getDueDateDaysPS());

    }

    public PaymentScheduleInstance instanciateFromInstance(PaymentScheduleTemplate paymentScheduleTemplate, PaymentScheduleInstance paymentScheduleInstance)
            throws BusinessException {
        return instanciate(paymentScheduleTemplate, paymentScheduleInstance.getServiceInstance(), paymentScheduleInstance.getAmount(), paymentScheduleInstance.getCalendar(),
            paymentScheduleInstance.getStartDate(), paymentScheduleInstance.getEndDate(), paymentScheduleInstance.getDueDateDays());
    }

    private PaymentScheduleInstance instanciate(PaymentScheduleTemplate paymentScheduleTemplate, ServiceInstance serviceInstance, BigDecimal amount, Calendar calendar,
            Date startDate, Date endDate, int dueDateDelay) throws BusinessException {

        paymentScheduleTemplate = paymentScheduleTemplateService.refreshOrRetrieve(paymentScheduleTemplate);
        Subscription subscription = subscriptionService.refreshOrRetrieve(serviceInstance.getSubscription());
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
        paymentScheduleInstance.setDueDateDays(dueDateDelay);
        create(paymentScheduleInstance);

        Calendar cal = paymentScheduleInstance.getCalendar();
        cal.setInitDate(paymentScheduleInstance.getStartDate());
        Date date = serviceInstance.getSubscriptionDate();
        CustomerAccount customerAccount = subscription.getUserAccount().getBillingAccount().getCustomerAccount();
        while (date.before(paymentScheduleInstance.getEndDate())) {

            PaymentScheduleInstanceItem paymentScheduleInstanceItem = new PaymentScheduleInstanceItem();
            paymentScheduleInstanceItem.setDueDate(DateUtils.addDaysToDate(date, dueDateDelay));
            paymentScheduleInstanceItem.setPaymentScheduleInstance(paymentScheduleInstance);
            paymentScheduleInstanceItem.setRequestPaymentDate(computeRequestPaymentDate(customerAccount, paymentScheduleInstanceItem.getDueDate()));
            date = cal.nextCalendarDate(date);
            if (!date.before(serviceInstance.getEndAgreementDate())) {
                paymentScheduleInstanceItem.setLast(true);
            } else {
                paymentScheduleInstanceItem.setLast(false);
            }
            paymentScheduleInstanceItemService.create(paymentScheduleInstanceItem);

        }
        return paymentScheduleInstance;
    }

    /**
     * @param customerAccount
     * @param dueDate
     * @return
     * @throws BusinessException
     */
    private Date computeRequestPaymentDate(CustomerAccount customerAccount, Date dueDate) throws BusinessException {
        Date requestPaymentDate = null;
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

        requestPaymentDate = DateUtils.addDaysToDate(dueDate, (-1 * ndDaysBeforeDueDate));
        return requestPaymentDate;
    }
}