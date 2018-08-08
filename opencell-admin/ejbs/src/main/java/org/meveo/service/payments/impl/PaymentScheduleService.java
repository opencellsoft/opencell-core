/**
 * 
 */
package org.meveo.service.payments.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.Subscription;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.payments.AccountOperationPS;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentSchedule;
import org.meveo.model.payments.PaymentScheduleStatusEnum;
import org.meveo.service.base.BusinessService;

/**
 * The Class PaymentScheduleService.
 *
 * @author anasseh
 */
@Stateless
public class PaymentScheduleService extends BusinessService<PaymentSchedule> {

    @Inject
    private AccountOperationPSService accountOperationPSService;

    public void start(PaymentSchedule paymentSchedule, Subscription subscription, CustomerAccount customerAccount) throws BusinessException {

        Calendar cal = paymentSchedule.getCalendar();
        cal.setInitDate(paymentSchedule.getStartDate());
        Date date = paymentSchedule.getStartDate();
        int i = 1;
        while (i <= paymentSchedule.getNumberPayments()) {

            AccountOperationPS aoPS = new AccountOperationPS();
            aoPS.setCustomerAccount(customerAccount);
            aoPS.setAmount(paymentSchedule.getAmount());
            aoPS.setDueDate(date);
            aoPS.setMatchingStatus(MatchingStatusEnum.O);
            aoPS.setUnMatchingAmount(paymentSchedule.getAmount());
            aoPS.setAccountingCode(paymentSchedule.getOccTemplate().getAccountingCode());
            aoPS.setOccCode(paymentSchedule.getOccTemplate().getCode());
            aoPS.setOccDescription(paymentSchedule.getOccTemplate().getDescription());
            aoPS.setTransactionCategory(paymentSchedule.getOccTemplate().getOccCategory());
            aoPS.setAccountCodeClientSide(paymentSchedule.getOccTemplate().getAccountCodeClientSide());
            aoPS.setMatchingAmount(BigDecimal.ZERO);
            aoPS.setPaymentSchedule(paymentSchedule);

            PaymentMethod preferedPaymentMethod = customerAccount.getPreferredPaymentMethod();
            if (preferedPaymentMethod != null) {
                aoPS.setPaymentMethod(preferedPaymentMethod.getPaymentType());
            }

            date = cal.nextCalendarDate(date);
            i++;
            accountOperationPSService.create(aoPS);

        }
        paymentSchedule.setStatus(PaymentScheduleStatusEnum.IN_PROGGRESS);
    }

    public PaymentSchedule findByServiceTemplate(ServiceTemplate serviceTemplate) {
        try {
            QueryBuilder qb = new QueryBuilder(PaymentSchedule.class, "ps");
            qb.addCriterionEntity("serviceTemplate", serviceTemplate);
            return (PaymentSchedule) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (Exception e) {

        }
        return null;
    }

}