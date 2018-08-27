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
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.PaymentScheduleInstance;
import org.meveo.model.payments.PaymentScheduleInstanceItem;
import org.meveo.model.payments.PaymentScheduleStatusEnum;
import org.meveo.model.payments.PaymentScheduleTemplate;
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

    public void updateAndInstanciate(PaymentScheduleInstance paymentScheduleInstance) throws BusinessException {
        paymentScheduleInstance.setStatus(PaymentScheduleStatusEnum.UPDATED);
        paymentScheduleInstance.setStatusDate(new Date());
        instanciate(paymentScheduleInstance.getPaymentScheduleTemplate(), paymentScheduleInstance.getServiceInstance());
        
    }
    public void instanciate(PaymentScheduleTemplate paymentScheduleTemplate, ServiceInstance serviceInstance) throws BusinessException {

        PaymentScheduleInstance paymentScheduleInstance = new PaymentScheduleInstance();
        paymentScheduleInstance.setPaymentScheduleTemplate(paymentScheduleTemplate);
        paymentScheduleInstance.setServiceInstance(serviceInstance);
        paymentScheduleInstance.setStatus(PaymentScheduleStatusEnum.IN_PROGRESS);
        paymentScheduleInstance.setStatusDate(new Date());
        paymentScheduleInstance.setCode(paymentScheduleTemplate.getCode());
        paymentScheduleInstance.setDescription(paymentScheduleTemplate.getDescription());
        create(paymentScheduleInstance);

        Calendar cal = paymentScheduleTemplate.getCalendar();
        cal.setInitDate(paymentScheduleTemplate.getStartDate());
        Date date = paymentScheduleTemplate.getStartDate();
        int i = 1;
        while (i <= paymentScheduleTemplate.getNumberPayments()) {

            PaymentScheduleInstanceItem paymentScheduleInstanceItem = new PaymentScheduleInstanceItem();
            paymentScheduleInstanceItem.setDueDate(DateUtils.addDaysToDate(date, paymentScheduleTemplate.getDueDateDays()));
            paymentScheduleInstanceItem.setPaymentScheduleInstance(paymentScheduleInstance);
            paymentScheduleInstanceItem.setRequestPaymentDate(
                computeRequestPaymentDate(serviceInstance.getSubscription().getUserAccount().getBillingAccount().getCustomerAccount(), paymentScheduleInstanceItem.getDueDate()));
            date = cal.nextCalendarDate(date);
            if(i == paymentScheduleTemplate.getNumberPayments()) {
                paymentScheduleInstanceItem.setLast(true);
            }else {
                paymentScheduleInstanceItem.setLast(false);
            }
            i++;
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