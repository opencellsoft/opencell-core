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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.beanutils.ConvertUtils;
import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptInterface;
import org.slf4j.Logger;

@Stateless
public class ScriptingJobBean extends IteratorBasedJobBean<ScriptInterface> {

    private static final long serialVersionUID = 4521052615288928077L;

    @Inject
    private Logger log;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    private ScriptInterface script;
    private Map<String, Object> context;

    private boolean newTx;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        super.execute(jobExecutionResult, jobInstance, this::initJobAndGetDataToProcess, this::executeScript, null, this::finalizeScript);
        context = null;
        script = null;
    }

    /**
     * Initialize job settings and retrieve data to process
     * 
     * @param jobExecutionResult Job execution result
     * @return An iterator over a list (of a single item) Script to run
     */
    @SuppressWarnings("unchecked")
    private Optional<Iterator<ScriptInterface>> initJobAndGetDataToProcess(JobExecutionResultImpl jobExecutionResult) {

        String txType = (String) this.getParamOrCFValue(jobExecutionResult.getJobInstance(), "ScriptingJob_TransactionType", "REQUIRES_NEW");
        newTx = (StringUtils.isBlank(txType) || "REQUIRES_NEW".equals(txType));

        String scriptCode = null;
        try {
            scriptCode = ((EntityReferenceWrapper) this.getParamOrCFValue(jobExecutionResult.getJobInstance(), "ScriptingJob_script")).getCode();
            context = (Map<String, Object>) this.getParamOrCFValue(jobExecutionResult.getJobInstance(), "ScriptingJob_variables");
            if (context == null) {
                context = new HashMap<String, Object>();
            }
            context.put(Script.CONTEXT_ENTITY, jobExecutionResult.getJobInstance());
            context.put(Script.CONTEXT_ACTION, scriptCode);
            context.put(Script.CONTEXT_CURRENT_USER, currentUser);
            context.put(Script.CONTEXT_APP_PROVIDER, appProvider);

            script = scriptInstanceService.getScriptInstance(scriptCode);
            script.init(context);

            return Optional.of(new SynchronizedIterator<ScriptInterface>(Arrays.asList(script)));

        } catch (Exception e) {
            log.error("Exception on initialization of script {}", scriptCode, e);
            jobExecutionResult.addErrorReport("Error in " + scriptCode + " init :" + e.getMessage());
        }

        return Optional.empty();
    }

    /**
     * Execute a script
     * 
     * @param script Script to execute
     * @param jobExecutionResult Job execution result
     */
    private void executeScript(ScriptInterface script, JobExecutionResultImpl jobExecutionResult) {

        script.execute(context);

        if (context.containsKey(Script.JOB_RESULT_NB_OK)) {
            jobExecutionResult.unRegisterSucces();// Reduce success as success is added automatically in main loop of IteratorBasedJobBean
            jobExecutionResult.setNbItemsCorrectlyProcessed(convert(context.get(Script.JOB_RESULT_NB_OK)));
            if (context.containsKey(Script.JOB_RESULT_NB_WARN)) {
                jobExecutionResult.setNbItemsProcessedWithWarning(convert(context.get(Script.JOB_RESULT_NB_WARN)));
            }
            if (context.containsKey(Script.JOB_RESULT_NB_KO)) {
                jobExecutionResult.setNbItemsProcessedWithError(convert(context.get(Script.JOB_RESULT_NB_KO)));
            }
            if (context.containsKey(Script.JOB_RESULT_TO_PROCESS)) {
                jobExecutionResult.setNbItemsToProcess(convert(context.get(Script.JOB_RESULT_TO_PROCESS)));
            }
            if (context.containsKey(Script.JOB_RESULT_REPORT)) {
                jobExecutionResult.addReport(context.get(Script.JOB_RESULT_REPORT) + "");
            }
            // } else {
            // jobExecutionResult.registerSucces();
        }

    }

    /**
     * Finalize script execution
     * 
     * @param result Job execution result
     */
    private void finalizeScript(JobExecutionResultImpl result) {
        try {
            if (script != null) {
                script.terminate(context);
            }

        } catch (Exception e) {
            log.error("Exception on finalize script", e);
            result.registerError(script.getClass().getName() + " finalize", e.getMessage());
        }
    }

    long convert(Object s) {
        long result = (long) ((StringUtils.isBlank(s)) ? 0l : ConvertUtils.convert(s + "", Long.class));
        return result;
    }

    @Override
    protected boolean isProcessItemInNewTx() {
        return newTx;
    }
}