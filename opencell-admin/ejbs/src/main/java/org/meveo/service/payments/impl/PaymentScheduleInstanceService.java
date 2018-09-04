/**
 * 
 */
package org.meveo.service.payments.impl;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CheckPaymentMethod;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DDPaymentMethod;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.PaymentScheduleInstance;
import org.meveo.model.payments.PaymentScheduleInstanceItem;
import org.meveo.model.payments.PaymentScheduleStatusEnum;
import org.meveo.model.payments.PaymentScheduleTemplate;
import org.meveo.model.payments.WirePaymentMethod;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.BusinessService;

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
    
    @Override
    public PaymentScheduleInstance update(PaymentScheduleInstance paymentScheduleInstance) throws BusinessException {
        switch (paymentScheduleInstance.getStatus()) {
        case CANCELLED:
            cancel(paymentScheduleInstance);
            break;

        case UPDATED:           
            break;

        default:
            break;
        }
        
        
        
        return super.update(entity);
    }

    /**
     * @param paymentScheduleInstance
     */
    private void cancel(PaymentScheduleInstance paymentScheduleInstance) {
        // TODO Auto-generated method stub
        
    }

    public void updateAndInstanciate(PaymentScheduleInstance paymentScheduleInstance) throws BusinessException {
        paymentScheduleInstance.setStatus(PaymentScheduleStatusEnum.UPDATED);
        paymentScheduleInstance.setStatusDate(new Date());
        instanciate(paymentScheduleInstance.getPaymentScheduleTemplate(), paymentScheduleInstance.getServiceInstance());
        
    }
    public void instanciate(PaymentScheduleTemplate paymentScheduleTemplate, ServiceInstance serviceInstance) throws BusinessException {

        PaymentScheduleInstance paymentScheduleInstance = new PaymentScheduleInstance();
        paymentScheduleInstance.setAmount(serviceInstance.getAmountPS() == null ? paymentScheduleTemplate.getAmount() :  serviceInstance.getAmountPS());
        paymentScheduleInstance.setCalendar(serviceInstance.getCalendarPS() == null ? paymentScheduleTemplate.getCalendar() : serviceInstance.getCalendarPS() );
        paymentScheduleInstance.setPaymentScheduleTemplate(paymentScheduleTemplate);        
        paymentScheduleInstance.setStatus(PaymentScheduleStatusEnum.IN_PROGRESS);
        paymentScheduleInstance.setStatusDate(new Date());
        paymentScheduleInstance.setStartDate(serviceInstance.getSubscriptionDate());
        paymentScheduleInstance.setEndDate(serviceInstance.getEndAgreementDate() == null ? serviceInstance.getSubscription().getEndAgreementDate(): serviceInstance.getEndAgreementDate());
        paymentScheduleInstance.setCode(paymentScheduleTemplate.getCode());
        paymentScheduleInstance.setDescription(paymentScheduleTemplate.getDescription());
        paymentScheduleInstance.setServiceInstance(serviceInstance);
        create(paymentScheduleInstance);

        Calendar cal = paymentScheduleInstance.getCalendar();
        cal.setInitDate(paymentScheduleInstance.getStartDate());
        Date date = serviceInstance.getSubscriptionDate();
       
        while (date.before(paymentScheduleInstance.getEndDate())) {

            PaymentScheduleInstanceItem paymentScheduleInstanceItem = new PaymentScheduleInstanceItem();
            paymentScheduleInstanceItem.setDueDate(DateUtils.addDaysToDate(date, paymentScheduleTemplate.getDueDateDays()));
            paymentScheduleInstanceItem.setPaymentScheduleInstance(paymentScheduleInstance);
            paymentScheduleInstanceItem.setRequestPaymentDate(
                computeRequestPaymentDate(serviceInstance.getSubscription().getUserAccount().getBillingAccount().getCustomerAccount(), paymentScheduleInstanceItem.getDueDate()));
            date = cal.nextCalendarDate(date);
            if(!date.before(serviceInstance.getEndAgreementDate())) {
                paymentScheduleInstanceItem.setLast(true);
            }else {
                paymentScheduleInstanceItem.setLast(false);
            }            
            paymentScheduleInstanceItemService.create(paymentScheduleInstanceItem);

        }

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