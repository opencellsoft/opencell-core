/**
 * 
 */
package org.meveo.admin.async;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;

import org.apache.commons.beanutils.ConvertUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptInterface;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;

/**
 * @author anasseh
 *
 */
@Stateless
public class ScriptingAsync {

    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Inject
    private CurrentUserProvider currentUserProvider;
    
    @Inject
    @CurrentUser
    protected MeveoUser currentUser;
    
    @Inject
    @ApplicationProvider
    protected Provider appProvider;
    
	@Resource(lookup = "java:jboss/ee/concurrency/executor/default")
	ManagedExecutorService executor;
	
	@Inject
	private JobExecutionService jobExecutionService;
	
	@Inject
	private Logger log;

    /**
     * Run a script
     * 
     * @param result Job execution result
     * @param scriptCode Script to run
     * @param context Script context
     * @param lastCurrentUser Current user. In case of multitenancy, when user authentication is forced as result of a fired trigger (scheduled jobs, other timed event
     *        expirations), current user might be lost, thus there is a need to reestablish.
     * @return Future String
     */
    @Asynchronous
    public Future<String> launchAndForget(JobExecutionResultImpl result, String scriptCode, Map<String, Object> context, MeveoUser lastCurrentUser) {

        currentUserProvider.reestablishAuthentication(lastCurrentUser);
        
		Callable<String> task = () -> runScript(result, scriptCode, context);
		Future<String> futureResult = executor.submit(task);
		while (!futureResult.isDone()) {
				try {
					Thread.sleep(2000);
					if (!jobExecutionService.isJobRunningOnThis(result.getJobInstance())) {
						futureResult.cancel(true);
					}
				} catch (InterruptedException e) {
					log.error("Failed to complete script execution : ", e);
				}
		}

        return futureResult;
    }

	public String runScript(JobExecutionResultImpl result, String scriptCode, Map<String, Object> context) {
		ScriptInterface script = null;
        try {
        	
            script = scriptInstanceService.getScriptInstance(scriptCode);
            context.put(Script.CONTEXT_CURRENT_USER, currentUser);
            context.put(Script.CONTEXT_APP_PROVIDER, appProvider);
            script.execute(context);
            if (context.containsKey(Script.JOB_RESULT_NB_OK)) {
                result.setNbItemsCorrectlyProcessed(convert(context.get(Script.JOB_RESULT_NB_OK)));
            } else {
                result.registerSucces();
            }
            if (context.containsKey(Script.JOB_RESULT_NB_WARN)) {
                result.setNbItemsProcessedWithWarning(convert(context.get(Script.JOB_RESULT_NB_WARN)));
            }
            if (context.containsKey(Script.JOB_RESULT_NB_KO)) {
                result.setNbItemsProcessedWithError(convert(context.get(Script.JOB_RESULT_NB_KO)));
            }
            if (context.containsKey(Script.JOB_RESULT_TO_PROCESS)) {
                result.setNbItemsToProcess(convert(context.get(Script.JOB_RESULT_TO_PROCESS)));
            }
            if (context.containsKey(Script.JOB_RESULT_REPORT)) {
                result.setReport(context.get(Script.JOB_RESULT_REPORT) + "");
            }

        } catch (Exception e) {
            result.registerError("Error in " + scriptCode + " execution :" + e.getMessage());
        }
		
        return "OK";
	}

    long convert(Object s) {
        long result = (long) ((StringUtils.isBlank(s)) ? 0l : ConvertUtils.convert(s + "", Long.class));
        return result;
    }
}
