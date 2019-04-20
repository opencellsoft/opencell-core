package org.meveo.admin.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.async.AccOpGenerationAsync;
import org.meveo.admin.async.SubListCreator;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.commons.utils.StringUtils;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptInterface;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 * @author Said Ramli
 * @author Abdellatif BARI
 * @lastModifiedVersion 5.2
 **/
@Stateless
public class AccountOperationsGenerationJobBean extends BaseJobBean {

    @Inject
    private Logger log;

    @Inject
    private InvoiceService invoiceService;

    @Inject
    private AccOpGenerationAsync accOpGenerationAsync;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    /** The script instance service. */
    @Inject
    private ScriptInstanceService scriptInstanceService;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {

        Long nbRuns = (Long) this.getParamOrCFValue(jobInstance, "nbRuns", -1L);
        if (nbRuns == -1) {
            nbRuns = (long) Runtime.getRuntime().availableProcessors();
        }
        Long waitingMillis = (Long) this.getParamOrCFValue(jobInstance, "waitingMillis", 0L);

        try {
            boolean excludeInvoicesWithoutAmount = jobInstance.getExcludeInvoicesWithoutAmount() == null ? false : jobInstance.getExcludeInvoicesWithoutAmount();
            List<Long> ids = invoiceService.queryInvoiceIdsWithNoAccountOperation(null, excludeInvoicesWithoutAmount);
            log.debug("invoices to traite:" + (ids == null ? null : ids.size()));

            String scriptInstanceCode = null;
            Map<String, Object> context = new HashMap<String, Object>();
            try {

                scriptInstanceCode = (String) this.getParamOrCFValue(jobInstance, "scriptInstanceCode");
                if (this.getParamOrCFValue(jobInstance, "scriptInstanceVariables") != null) {
                    context = (Map<String, Object>) this.getParamOrCFValue(jobInstance, "scriptInstanceVariables");
                }
            } catch (Exception e) {
                log.warn("Cant get customFields for " + jobInstance.getJobTemplate(), e.getMessage());
            }
            List<Future<String>> futures = new ArrayList<Future<String>>();
            SubListCreator subListCreator = new SubListCreator(ids, nbRuns.intValue());
            log.debug("block to run:" + subListCreator.getBlocToRun());
            log.debug("nbThreads:" + nbRuns);
            ScriptInterface script = null;
            if (!StringUtils.isBlank(scriptInstanceCode)) {
                script = scriptInstanceService.getScriptInstance(scriptInstanceCode);
                script.init(context);
            }

            MeveoUser lastCurrentUser = currentUser.unProxy();
            while (subListCreator.isHasNext()) {
                futures.add(accOpGenerationAsync.launchAndForget((List<Long>) subListCreator.getNextWorkSet(), result, lastCurrentUser, script));
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
                    log.error("Failed to execute async method", cause);
                }
            }
        } catch (Exception e) {
            log.error("Failed to run accountOperation generation  job", e);
            result.registerError(e.getMessage());
        }
    }

}
