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
import javax.persistence.EntityNotFoundException;

import org.meveo.admin.async.FiltringJobAsync;
import org.meveo.admin.async.SubListCreator;
import org.meveo.admin.exception.InvalidScriptException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.IEntity;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.filter.Filter;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.filter.FilterService;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptInterface;
import org.slf4j.Logger;

/**
 * The FilteringJobBean have 2 mains inputs :ScriptInstance and Filter. For each filtered entity the scriptInstance are executed.
 * 
 * @author anasseh
 *
 */
@Stateless
public class FilteringJobBean {

    @Inject
    private Logger log;

    @Inject
    private FilterService filterService;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Inject
    private CustomFieldInstanceService customFieldInstanceService;

    @Inject
    private FiltringJobAsync filtringJobAsync;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    /**
     * Execute the jobInstance.
     * 
     * @param result The result execution
     * @param jobInstance the jobInstance to execute
     */
    @SuppressWarnings("unchecked")
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {
        ScriptInterface scriptInterface = null;
        Map<String, Object> context = null;
        try {
            Long nbRuns = new Long(1);
            Long waitingMillis = new Long(0);
            try {
                nbRuns = (Long) customFieldInstanceService.getCFValue(jobInstance, "nbRuns");
                waitingMillis = (Long) customFieldInstanceService.getCFValue(jobInstance, "waitingMillis");
                if (nbRuns == -1) {
                    nbRuns = (long) Runtime.getRuntime().availableProcessors();
                }
            } catch (Exception e) {
                nbRuns = new Long(1);
                waitingMillis = new Long(0);
                log.warn("Cant get customFields for " + jobInstance.getJobTemplate(), e.getMessage());
            }
            String filterCode = ((EntityReferenceWrapper) customFieldInstanceService.getCFValue(jobInstance, "FilteringJob_filter")).getCode();
            String scriptCode = ((EntityReferenceWrapper) customFieldInstanceService.getCFValue(jobInstance, "FilteringJob_script")).getCode();
            String recordVariableName = (String) customFieldInstanceService.getCFValue(jobInstance, "FilteringJob_recordVariableName");

            try {
                scriptInterface = scriptInstanceService.getScriptInstance(scriptCode);

            } catch (EntityNotFoundException | InvalidScriptException e) {
                result.registerError(e.getMessage());
                return;
            }

            context = (Map<String, Object>) customFieldInstanceService.getCFValue(jobInstance, "FilteringJob_variables");
            if (context == null) {
                context = new HashMap<String, Object>();
            }

            Filter filter = filterService.findByCode(filterCode);
            if (filter == null) {
                result.registerError("Cant find filter : " + filterCode);
                return;
            }

            scriptInterface.init(context);

            List<? extends IEntity> filtredEntities = filterService.filteredListAsObjects(filter);
            int nbItemsToProcess = filtredEntities == null ? 0 : filtredEntities.size();
            result.setNbItemsToProcess(nbItemsToProcess);
            List<Future<String>> futures = new ArrayList<Future<String>>();
            SubListCreator subListCreator = new SubListCreator(filtredEntities, nbRuns.intValue());
            log.debug("NbItemsToProcess:{}, block to run{}, nbThreads:{}.", nbItemsToProcess, subListCreator.getBlocToRun(), nbRuns);

            MeveoUser lastCurrentUser = currentUser.unProxy();
            while (subListCreator.isHasNext()) {
                futures
                    .add(filtringJobAsync.launchAndForget((List<? extends IEntity>) subListCreator.getNextWorkSet(), result, scriptInterface, recordVariableName, lastCurrentUser));
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
            log.error("Error on execute", e);
            result.setReport("error:" + e.getMessage());

        } finally {
            try {
                scriptInterface.finalize(context);

            } catch (Exception e) {
                log.error("Error on finally execute", e);
                result.setReport("finalize error:" + e.getMessage());
            }
        }
    }
}