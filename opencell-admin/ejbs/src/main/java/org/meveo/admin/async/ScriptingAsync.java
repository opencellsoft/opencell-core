/**
 * 
 */
package org.meveo.admin.async;

import java.util.Map;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.ConvertUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptInterface;

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

        ScriptInterface script = null;
        try {
            script = scriptInstanceService.getScriptInstance(scriptCode);
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
        return new AsyncResult<String>("OK");
    }

    long convert(Object s) {
        long result = (long) ((StringUtils.isBlank(s)) ? 0l : ConvertUtils.convert(s + "", Long.class));
        return result;
    }
}
