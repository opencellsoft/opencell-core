/**
 * 
 */
package org.meveo.service.payments.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Query;

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

    /** The payment schedule instance item service. */
    @Inject
    private PaymentScheduleInstanceItemService paymentScheduleInstanceItemService;

    /** The payment schedule template service. */
    @Inject
    private PaymentScheduleTemplateService paymentScheduleTemplateService;

    /** The subscription service. */
    @Inject
    private SubscriptionService subscriptionService;

    /** The calendar service. */
    @Inject
    private CalendarService calendarService;
    
    /**
     * Terminate when the linked service are terminated.
     *
     * @param serviceInstance the service instance
     * @param terminationDate the termination date
     * @throws BusinessException the business exception
     */
    public void terminate(ServiceInstance serviceInstance, Date terminationDate) throws BusinessException {
        for(PaymentScheduleInstance paymentScheduleInstance : serviceInstance.getPsInstances()){
            if(paymentScheduleInstance.getStatus() == PaymentScheduleStatusEnum.IN_PROGRESS) {
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
    public void terminate(PaymentScheduleInstance paymentScheduleInstance,Date terminationDate) throws BusinessException {
        if(terminationDate == null) {
            terminationDate = paymentScheduleInstance.getEndDate();
        }
        PaymentScheduleTemplate  paymentScheduleTemplate = paymentScheduleTemplateService.refreshOrRetrieve(paymentScheduleInstance.getPaymentScheduleTemplate());
        
        if(paymentScheduleTemplate.isApplyAgreement()) {
            paymentScheduleInstance = refreshOrRetrieve(paymentScheduleInstance);
            for(PaymentScheduleInstanceItem paymentScheduleInstanceItem : paymentScheduleInstance.getPaymentScheduleInstanceItems()) {
               if(paymentScheduleInstanceItem.getRecordedInvoice() == null) {
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
        if(paymentScheduleInstance.getPaymentScheduleTemplate().isApplyAgreement()) {
            throw new BusinessException("Can't cancel a PaymentSchedule when is applyAgreement");            
        }
        paymentScheduleInstance.setStatus(PaymentScheduleStatusEnum.CANCELLED);
        paymentScheduleInstance.setStatusDate(new Date());
        super.update(paymentScheduleInstance);
    }


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
     * Instanciate from service.
     *
     * @param paymentScheduleTemplate the payment schedule template
     * @param serviceInstance the service instance
     * @return the payment schedule instance
     * @throws BusinessException the business exception
     */
    public PaymentScheduleInstance instanciateFromService(PaymentScheduleTemplate paymentScheduleTemplate, ServiceInstance serviceInstance) throws BusinessException {
        return instanciate(paymentScheduleTemplate, serviceInstance, serviceInstance.getAmountPS() == null ? paymentScheduleTemplate.getAmount() : serviceInstance.getAmountPS(),
            serviceInstance.getCalendarPS() == null ? paymentScheduleTemplate.getCalendar() : serviceInstance.getCalendarPS(), serviceInstance.getSubscriptionDate(),
            serviceInstance.getEndAgreementDate() == null ? serviceInstance.getSubscription().getEndAgreementDate() : serviceInstance.getEndAgreementDate(),
            serviceInstance.getDueDateDaysPS() == null ? paymentScheduleTemplate.getDueDateDays() : serviceInstance.getDueDateDaysPS());

    }

    /**
     * Instanciate from instance.
     *
     * @param paymentScheduleTemplate the payment schedule template
     * @param paymentScheduleInstance the payment schedule instance
     * @return the payment schedule instance
     * @throws BusinessException the business exception
     */
    public PaymentScheduleInstance instanciateFromInstance(PaymentScheduleTemplate paymentScheduleTemplate, PaymentScheduleInstance paymentScheduleInstance)
            throws BusinessException {
        return instanciate(paymentScheduleTemplate, paymentScheduleInstance.getServiceInstance(), paymentScheduleInstance.getAmount(), paymentScheduleInstance.getCalendar(),
            paymentScheduleInstance.getStartDate(), paymentScheduleInstance.getEndDate(), paymentScheduleInstance.getDueDateDays());
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
        Date date = paymentScheduleInstance.getStartDate();
        CustomerAccount customerAccount = subscription.getUserAccount().getBillingAccount().getCustomerAccount();
        while (date.before(paymentScheduleInstance.getEndDate())) {

            PaymentScheduleInstanceItem paymentScheduleInstanceItem = new PaymentScheduleInstanceItem();
            paymentScheduleInstanceItem.setDueDate(DateUtils.addDaysToDate(date, dueDateDelay));
            paymentScheduleInstanceItem.setPaymentScheduleInstance(paymentScheduleInstance);
            paymentScheduleInstanceItem.setRequestPaymentDate(computeRequestPaymentDate(customerAccount, paymentScheduleInstanceItem.getDueDate()));
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
     * @param dueDate the due date
     * @return the date
     * @throws BusinessException the business exception
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

    /**
     * Find by service.
     *
     * @param serviceInstance the service instance
     * @param status the status
     * @return the list
     */
    public List<PaymentScheduleInstance> findByService(ServiceInstance serviceInstance, PaymentScheduleStatusEnum status) {
        try {
            String strQuery = "from " + PaymentScheduleInstance.class.getSimpleName() + " where serviceInstance.id :=serviceInstanceID";
            if (status != null) {
                strQuery += " and status =:statusIN";
            }
            strQuery += " order by status";
            Query query = getEntityManager().createQuery(strQuery);
            query = query.setParameter("serviceInstanceID", serviceInstance.getId());
            if (status != null) {
                query = query.setParameter("status", status);
            }
            return (List<PaymentScheduleInstance>) query.getResultList();
        } catch (Exception e) {            
        }
        return null;
    }


}