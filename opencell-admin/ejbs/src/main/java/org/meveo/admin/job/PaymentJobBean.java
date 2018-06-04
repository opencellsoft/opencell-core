package org.meveo.admin.job;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.async.PaymentAsync;
import org.meveo.admin.async.SubListCreator;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.payments.PaymentGateway;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.PaymentGatewayService;
import org.slf4j.Logger;

/**
 * The Class PaymentJobBean, PaymentJob  implementation.
 * 
 * @author anasseh
 * @lastModifiedVersion 5.0
 */
@Stateless
public class PaymentJobBean extends BaseJobBean {

    @Inject
    private Logger log;

    @Inject
    private AccountOperationService accountOperationService;

    @Inject
    private PaymentAsync paymentAsync;

    @Inject
    private PaymentGatewayService paymentGatewayService;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    @SuppressWarnings("unchecked")
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {
        log.debug("Running with parameter={}", jobInstance.getParametres());

        try {
            Long nbRuns = new Long(1);
            Long waitingMillis = new Long(0);
            boolean createAO = true;
            boolean matchingAO = true;

            OperationCategoryEnum operationCategory = OperationCategoryEnum.CREDIT;
            PaymentMethodEnum paymentMethodType = PaymentMethodEnum.CARD;

            PaymentGateway paymentGateway = null;
            if ((EntityReferenceWrapper) this.getParamOrCFValue(jobInstance, "PaymentJob_paymentGateway") != null) {
                paymentGatewayService.findByCode(((EntityReferenceWrapper) this.getParamOrCFValue(jobInstance, "PaymentJob_paymentGateway")).getCode());
            }
            try {
                operationCategory = OperationCategoryEnum.valueOf(((String) this.getParamOrCFValue(jobInstance, "PaymentJob_creditOrDebit")).toUpperCase());
                paymentMethodType = PaymentMethodEnum.valueOf(((String) this.getParamOrCFValue(jobInstance, "PaymentJob_cardOrDD")).toUpperCase());
                nbRuns = (Long) this.getParamOrCFValue(jobInstance, "nbRuns");
                waitingMillis = (Long) this.getParamOrCFValue(jobInstance, "waitingMillis");
                if (nbRuns == -1) {
                    nbRuns = (long) Runtime.getRuntime().availableProcessors();
                }
                createAO = "YES".equals((String) this.getParamOrCFValue(jobInstance, "PaymentJob_createAO"));
                matchingAO = "YES".equals((String) this.getParamOrCFValue(jobInstance, "PaymentJob_createAO"));

            } catch (Exception e) {
                nbRuns = new Long(1);
                waitingMillis = new Long(0);
                log.warn("Cant get customFields for " + jobInstance.getJobTemplate(), e.getMessage());
            }

            List<Long> ids = new ArrayList<Long>();
            if (OperationCategoryEnum.CREDIT == operationCategory) {
                ids = accountOperationService.getAOidsToPay(paymentMethodType);
            } else {
                ids = accountOperationService.getAOidsToRefund(paymentMethodType);
            }

            log.debug("AO to pay:" + ids.size());
            result.setNbItemsToProcess(ids.size());

            List<Future<String>> futures = new ArrayList<Future<String>>();
            SubListCreator subListCreator = new SubListCreator(ids, nbRuns.intValue());
            log.debug("block to run:" + subListCreator.getBlocToRun());
            log.debug("nbThreads:" + nbRuns);
            MeveoUser lastCurrentUser = currentUser.unProxy();
            while (subListCreator.isHasNext()) {
                futures.add(paymentAsync.launchAndForget((List<Long>) subListCreator.getNextWorkSet(), result, createAO, matchingAO, paymentGateway, operationCategory,
                    paymentMethodType, lastCurrentUser));
                if (subListCreator.isHasNext()) {
                    try {
                        Thread.sleep(waitingMillis.longValue());
                    } catch (InterruptedException e) {
                        log.error("", e);
                    }
                }
            }
            // Wait for all async methods to finish
            for (Future<String> future : futures) {
                try {
                    future.get();

                } catch (InterruptedException e) {
                    // It was cancelled from outside - no interest

                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    result.registerError(cause.getMessage());
                    result.addReport(cause.getMessage());
                    log.error("Failed to execute async method", cause);
                }
            }
        } catch (Exception e) {
            log.error("Failed to run usage rating job", e);
            result.registerError(e.getMessage());
            result.addReport(e.getMessage());
        }
    }

}