/**
 * 
 */
package org.meveo.admin.async;

import static org.meveo.service.script.payment.AccountOperationFilterScript.LIST_AO_TO_PAY;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.job.UnitPaymentJobBean;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.payments.PaymentGateway;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.script.payment.AccountOperationFilterScript;

/**
 * The Class PaymentAsync.
 *
 * @author anasseh
 * @author Said Ramli
 * @lastModifiedVersion 5.2
 */

@Stateless
public class PaymentAsync {

    /** The unit payment job bean. */
    @Inject
    private UnitPaymentJobBean unitPaymentJobBean;

    /** The JobExecution service. */
    @Inject
    private JobExecutionService jobExecutionService;

    /** The current user provider. */
    @Inject
    private CurrentUserProvider currentUserProvider;

    /** The account operation service. */
    @Inject
    private AccountOperationService accountOperationService;

    /**
     * Process card payments for a list of given account operation ids. One account operation at a time in a separate transaction.
     *
     * @param caIds List of customerAccount ids
     * @param result Job execution result
     * @param createAO True/ false to create account operation
     * @param matchingAO Matching account operation
     * @param paymentGateway PaymentGateway to use
     * @param operationCategory Operation category.
     * @param paymentMethodType Payment method type to use
     * @param lastCurrentUser Current user. In case of multitenancy, when user authentication is forced as result of a fired trigger (scheduled jobs, other timed event
     *        expirations), current user might be lost, thus there is a need to reestablish.
     * @param paymentPerAOorCA make payment for each AO or all AO for each CA
     * @param fromDueDate the from due date
     * @param toDueDate the to due date
     * @return future result
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<String> launchAndForget(List<Long> caIds, JobExecutionResultImpl result, boolean createAO, boolean matchingAO, PaymentGateway paymentGateway,
            OperationCategoryEnum operationCategory, PaymentMethodEnum paymentMethodType, MeveoUser lastCurrentUser, String paymentPerAOorCA, Date fromDueDate, Date toDueDate,
            AccountOperationFilterScript aoFilterScript) { // TODO : nbr of method arguments is disturbing , refactor it by using a dedicated bean/dto

        currentUserProvider.reestablishAuthentication(lastCurrentUser);
        BigDecimal oneHundred = new BigDecimal("100");
        for (Long caID : caIds) {
            if (!jobExecutionService.isJobRunningOnThis(result.getJobInstance().getId())) {
                break;
            }
           
            List<AccountOperation> listAoToPayOrRefund = null;
            if (operationCategory == OperationCategoryEnum.CREDIT) {
                listAoToPayOrRefund = filterAoToPay(aoFilterScript, accountOperationService.getAOsToPay(paymentMethodType, fromDueDate,toDueDate, caID));
            } else {
                listAoToPayOrRefund = accountOperationService.getAOsToRefund(paymentMethodType, fromDueDate,toDueDate, caID);
            }
            if ("CA".equals(paymentPerAOorCA)) {
                List<Long> aoIds = new ArrayList<Long>();
                BigDecimal amountToPay = BigDecimal.ZERO;
                for (AccountOperation ao : listAoToPayOrRefund) {
                    aoIds.add(ao.getId());
                    amountToPay = amountToPay.add(ao.getUnMatchingAmount());
                }
                if (amountToPay.compareTo(BigDecimal.ZERO) != 0) {
                    unitPaymentJobBean.execute(result, caID, aoIds, amountToPay.multiply(oneHundred).longValue(), createAO, matchingAO, operationCategory, paymentGateway,
                        paymentMethodType);
                }
            } else {
                for (AccountOperation ao : listAoToPayOrRefund) {
                    if (ao.getUnMatchingAmount().compareTo(BigDecimal.ZERO) != 0) {
                        List<Long> aoIds = new ArrayList<Long>();
                        aoIds.add(ao.getId());
                        unitPaymentJobBean.execute(result, caID, aoIds, ao.getUnMatchingAmount().multiply(oneHundred).longValue(), createAO, matchingAO, operationCategory,
                            paymentGateway, paymentMethodType);
                    }
                }
            }

        }
        return new AsyncResult<String>("OK");
    }

    private List<AccountOperation> filterAoToPay(AccountOperationFilterScript aoFilterScript, List<AccountOperation> listAoToPayOrRefund) {
        if (aoFilterScript != null){
            Map<String, Object> methodContext = new HashMap<>();
            methodContext.put(LIST_AO_TO_PAY, listAoToPayOrRefund);
            return aoFilterScript.filterAoToPay(methodContext);
        }
        return listAoToPayOrRefund;
    }
}
