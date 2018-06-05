package org.meveo.admin.job;

import java.util.Map;
import java.util.concurrent.Future;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.async.ScriptingAsync;
import org.meveo.admin.exception.BusinessException;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptInterface;
import org.slf4j.Logger;

@Stateless
public class ScriptingJobBean {

    @Inject
    private Logger log;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Inject
    private ScriptingAsync scriptingAsync;

    @Inject
    private JobExecutionService jobExecutionService;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void init(JobExecutionResultImpl result, String scriptCode, Map<String, Object> context) throws BusinessException {
        ScriptInterface script = null;
        try {
            script = scriptInstanceService.getScriptInstance(scriptCode);
            script.init(context);
        } catch (Exception e) {
            log.error("Exception on init script", e);
            result.registerError("Error in " + scriptCode + " init :" + e.getMessage());
        }
    }

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void execute(JobExecutionResultImpl result, String scriptCode, Map<String, Object> context) throws BusinessException {
        MeveoUser lastCurrentUser = currentUser.unProxy();
        Future<String> future = scriptingAsync.launchAndForget(result, scriptCode, context, lastCurrentUser);
        while (!future.isDone()) {

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }

            // can't stop a running job, Only the job with a sleeping or blocker thread will be stopped
            if (!jobExecutionService.isJobRunningOnThis(result.getJobInstance())) {
                future.cancel(true);
            }
        }

    }

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void finalize(JobExecutionResultImpl result, String scriptCode, Map<String, Object> context) throws BusinessException {
        ScriptInterface script = null;
        try {
            script = scriptInstanceService.getScriptInstance(scriptCode);
            script.finalize(context);

        } catch (Exception e) {
            log.error("Exception on finalize script", e);
            result.registerError("Error in " + scriptCode + " finalize :" + e.getMessage());
        }
    }

}
