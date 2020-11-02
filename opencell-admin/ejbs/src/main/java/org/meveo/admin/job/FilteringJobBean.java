/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

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
import javax.enterprise.inject.spi.BeanManager;
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
import org.meveo.security.MeveoUser;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.filter.FilterService;
import org.meveo.service.job.Job;
import org.meveo.service.job.JobExecutionErrorService;
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
public class FilteringJobBean extends BaseJobBean {

    @Inject
    private Logger log;

    @Inject
    private FilterService filterService;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Inject
    private FiltringJobAsync filtringJobAsync;

    @Inject
    private BeanManager manager;

    @Inject
    private JobExecutionErrorService jobExecutionErrorService;

    /**
     * Execute the jobInstance.
     * 
     * @param result The result execution
     * @param jobInstance the jobInstance to execute
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {

        jobExecutionErrorService.purgeJobErrors(jobInstance);

        ScriptInterface scriptInterface = null;
        Map<String, Object> scriptContext = new HashMap<String, Object>();

        Long nbRuns = (Long) this.getParamOrCFValue(jobInstance, Job.CF_NB_RUNS, -1L);
        if (nbRuns == -1) {
            nbRuns = (long) Runtime.getRuntime().availableProcessors();
        }
        Long waitingMillis = (Long) this.getParamOrCFValue(jobInstance, Job.CF_WAITING_MILLIS, 0L);

        try {

            String filterCode = ((EntityReferenceWrapper) this.getParamOrCFValue(jobInstance, "FilteringJob_filter")).getCode();
            String scriptCode = ((EntityReferenceWrapper) this.getParamOrCFValue(jobInstance, "FilteringJob_script")).getCode();
            String recordVariableName = (String) this.getParamOrCFValue(jobInstance, "FilteringJob_recordVariableName");

            Filter filter = filterService.findByCode(filterCode);
            if (filter == null) {
                result.registerError("Cant find filter : " + filterCode);
                return;
            }

            try {
                scriptInterface = scriptInstanceService.getScriptInstance(scriptCode);

            } catch (EntityNotFoundException | InvalidScriptException e) {
                result.registerError(e.getMessage());
                return;
            }

            Map<String, Object> scriptParams = (Map<String, Object>) this.getParamOrCFValue(jobInstance, "FilteringJob_variables");
            if (scriptParams != null) {
                Map<Object, Object> elContext = new HashMap<>();
                elContext.put("manager", manager);
                elContext.put("currentUser", currentUser);
                elContext.put("appProvider", appProvider);

                for (Map.Entry<String, Object> entry : scriptParams.entrySet()) {
                    if (entry.getValue() instanceof String) {
                        scriptContext.put(entry.getKey(), ValueExpressionWrapper.evaluateExpression((String) entry.getValue(), elContext, Object.class));
                    } else {
                        scriptContext.put(entry.getKey(), entry.getValue());
                    }
                }
            }

            scriptInterface.init(scriptContext);

            Map<String, Object> sqlVariables = (Map<String, Object>) this.getParamOrCFValue(jobInstance, "FilteringJob_sql_variables");

            Map<String, Object> sqlParams = new HashMap<String, Object>();

            if (sqlVariables != null) {
                Map<Object, Object> elContext = new HashMap<>();
                elContext.put("manager", manager);
                elContext.put("currentUser", currentUser);
                elContext.put("appProvider", appProvider);

                for (Map.Entry<String, Object> entry : sqlVariables.entrySet()) {
                    if (entry.getValue() instanceof String) {
                        sqlParams.put(entry.getKey(), ValueExpressionWrapper.evaluateExpression((String) entry.getValue(), elContext, Object.class));
                    } else {
                        sqlParams.put(entry.getKey(), entry.getValue());
                    }
                }
            }

            List<? extends IEntity> filtredEntities = filterService.filteredListAsObjects(filter, sqlParams);
            int nbItemsToProcess = filtredEntities == null ? 0 : filtredEntities.size();
            result.setNbItemsToProcess(nbItemsToProcess);
            List<Future<String>> futures = new ArrayList<Future<String>>();
            SubListCreator subListCreator = new SubListCreator(filtredEntities, nbRuns.intValue());
            log.debug("NbItemsToProcess:{}, block to run{}, nbThreads:{}.", nbItemsToProcess, subListCreator.getBlocToRun(), nbRuns);

            MeveoUser lastCurrentUser = currentUser.unProxy();
            while (subListCreator.isHasNext()) {
                futures.add(filtringJobAsync.launchAndForget((List<? extends IEntity>) subListCreator.getNextWorkSet(), result, scriptInterface, recordVariableName, lastCurrentUser));
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
                scriptInterface.terminate(scriptContext);

            } catch (Exception e) {
                log.error("Error on finally execute", e);
                result.setReport("finalize error:" + e.getMessage());
            }
        }
    }
}