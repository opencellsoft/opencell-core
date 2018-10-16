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

import org.meveo.admin.job.UnitPaymentScheduleJobBean;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.payments.PaymentScheduleInstanceItem;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.job.JobExecutionService;

/**
 * The Class PaymentScheduleProcessingAsync.
 *
 * @author anasseh
 * @lastModifiedVersion 5.1
 */

@Stateless
public class PaymentScheduleProcessingAsync {

    /** The unit toto ps job bean. */
    @Inject
    private UnitPaymentScheduleJobBean unitPaymentScheduleJobBean;

    /** The JobExecution service. */
    @Inject
    private JobExecutionService jobExecutionService;

    /** The current user provider. */
    @Inject
    private CurrentUserProvider currentUserProvider;

   

    /**
     * Process payment schedule.
     * 
     *
     * @param paymentScheduleInstanceItems the payment schedule instance items
     * @param result the result
     * @param lastCurrentUser the last current user
     * @return future result
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<String> launchAndForget(List<PaymentScheduleInstanceItem> paymentScheduleInstanceItems, JobExecutionResultImpl result,  MeveoUser lastCurrentUser) {

        currentUserProvider.reestablishAuthentication(lastCurrentUser);

        for (PaymentScheduleInstanceItem item : paymentScheduleInstanceItems) {
            if (!jobExecutionService.isJobRunningOnThis(result.getJobInstance().getId())) {
                break;
            }
            unitPaymentScheduleJobBean.execute(result, item);

        }
        return new AsyncResult<String>("OK");
    }
}
