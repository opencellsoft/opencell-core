package org.meveo.admin.job;

import java.util.ArrayList;
import java.util.Date;
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
import org.meveo.model.shared.DateUtils;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.PaymentGatewayService;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptInterface;
import org.meveo.service.script.payment.AccountOperationFilterScript;
import org.slf4j.Logger;

/**
 * The Class PaymentJobBean, PaymentJob implementation.
 * 
 * @author anasseh
 * @author Said Ramli
 * @lastModifiedVersion 5.2
 */
@Stateless
public class PaymentJobBean extends BaseJobBean {

    @Inject
    private Logger log;

    @Inject
    private CustomerAccountService customerAccountService;

    @Inject
    private PaymentAsync paymentAsync;

    @Inject
    private PaymentGatewayService paymentGatewayService;
    
    @Inject
    private ScriptInstanceService scriptInstanceService;

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
            Date fromDueDate = null;
            Date toDueDate = null;
            String paymentPerAOorCA = "CA";

            OperationCategoryEnum operationCategory = OperationCategoryEnum.CREDIT;
            PaymentMethodEnum paymentMethodType = PaymentMethodEnum.CARD;

            PaymentGateway paymentGateway = null;
            if ((EntityReferenceWrapper) this.getParamOrCFValue(jobInstance, "PaymentJob_paymentGateway") != null) {
                paymentGateway =  paymentGatewayService.findByCode(((EntityReferenceWrapper) this.getParamOrCFValue(jobInstance, "PaymentJob_paymentGateway")).getCode());
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
                matchingAO = "YES".equals((String) this.getParamOrCFValue(jobInstance, "PaymentJob_matchingAO"));
                fromDueDate = (Date) this.getParamOrCFValue(jobInstance, "PaymentJob_fromDueDate");
                toDueDate = (Date) this.getParamOrCFValue(jobInstance, "PaymentJob_toDueDate");
                paymentPerAOorCA = (String) this.getParamOrCFValue(jobInstance, "PaymentJob_AOorCA");
                
                
            } catch (Exception e) {
                nbRuns = new Long(1);
                waitingMillis = new Long(0);
                log.warn("Cant get customFields for " + jobInstance.getJobTemplate(), e.getMessage());
            }
           
            if (fromDueDate == null) {
                fromDueDate = new Date(1);
            }
            if (toDueDate == null) {
                toDueDate = DateUtils.addYearsToDate(fromDueDate, 1000);
            }
            
            List<Long> caIds = new ArrayList<Long>();
            if (OperationCategoryEnum.CREDIT == operationCategory) {
                caIds = customerAccountService.getCAidsForPayment(paymentMethodType, fromDueDate,toDueDate);
            } else {
                caIds = customerAccountService.getCAidsForRefund(paymentMethodType, fromDueDate,toDueDate);
            }

            log.debug("nb CA for payment:" + caIds.size());
            result.setNbItemsToProcess(caIds.size());

            List<Future<String>> futures = new ArrayList<Future<String>>();
            SubListCreator subListCreator = new SubListCreator(caIds, nbRuns.intValue());
            log.debug("block to run:" + subListCreator.getBlocToRun());
            log.debug("nbThreads:" + nbRuns);
            MeveoUser lastCurrentUser = currentUser.unProxy();
            
            AccountOperationFilterScript aoFilterScript = getAOScriptInstance(jobInstance);
            while (subListCreator.isHasNext()) {
                futures.add(paymentAsync.launchAndForget((List<Long>) subListCreator.getNextWorkSet(), result, createAO, matchingAO, paymentGateway, operationCategory,
                    paymentMethodType, lastCurrentUser, paymentPerAOorCA, fromDueDate,toDueDate, aoFilterScript));
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

    private AccountOperationFilterScript getAOScriptInstance(JobInstance jobInstance) {
        try {
            final  String aoFilterScriptCode =  ((EntityReferenceWrapper) this.getParamOrCFValue(jobInstance, "PaymentJob_aoFilterScript")).getCode();
           
            if (aoFilterScriptCode != null) {
                log.debug(" looking for ScriptInstance with aoFilterScriptCode :  [{}] ", aoFilterScriptCode);
                ScriptInterface si = scriptInstanceService.getScriptInstance(aoFilterScriptCode);
                if (si != null && si instanceof AccountOperationFilterScript) {
                    return (AccountOperationFilterScript) si;
                }
            }
        } catch (Exception e) {
            log.error(" Error on newAoFilterScriptInstance : [{}]" , e.getMessage());
        }
        return null;
    }

}