/**
 * 
 */
package org.meveo.admin.async;

import java.util.List;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.job.UnitPaymentCardJobBean;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.payments.PaymentGateway;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.job.JobExecutionService;

/**
 * @author anasseh
 * 
 */

@Stateless
public class PaymentCardAsync {

    @Inject
    private UnitPaymentCardJobBean unitPaymentCardJobBean;

    /** The JobExecution service. */
    @Inject
    private JobExecutionService jobExecutionService;

    @Inject
    private CurrentUserProvider currentUserProvider;

    /**
     * Process card payments for a list of given account operation ids. One account operation at a time in a separate transaction.
     * 
     * @param ids List of account operation ids
     * @param result Job execution result
     * @param createAO True/ false to create account operation
     * @param matchingAO Matching account operation
     * @param paymentGateway PaymentGateway to use
     * @param operationCategory Operation category.
     * @param paymentMethodType Payment method type to use
     * @param lastCurrentUser Current user. In case of multitenancy, when user authentication is forced as result of a fired trigger (scheduled jobs, other timed event
     *        expirations), current user might be lost, thus there is a need to reestablish.
     * @return future result
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<String> launchAndForget(List<Long> ids, JobExecutionResultImpl result, boolean createAO, boolean matchingAO, PaymentGateway paymentGateway,
            OperationCategoryEnum operationCategory, PaymentMethodEnum paymentMethodType, MeveoUser lastCurrentUser) {

        currentUserProvider.reestablishAuthentication(lastCurrentUser);

        for (Long id : ids) {
            if (!jobExecutionService.isJobRunningOnThis(result.getJobInstance().getId())) {
                break;
            }
            unitPaymentCardJobBean.execute(result, id, createAO, matchingAO, operationCategory, paymentGateway, paymentMethodType);
        }
        return new AsyncResult<String>("OK");
    }
}
