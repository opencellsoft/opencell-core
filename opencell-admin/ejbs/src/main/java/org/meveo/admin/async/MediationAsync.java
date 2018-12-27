/**
 * 
 */
package org.meveo.admin.async;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.collections.map.HashedMap;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ElementNotFoundException;
import org.meveo.admin.exception.InvalidScriptException;
import org.meveo.admin.job.MediationJobBean;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptInterface;
import org.slf4j.Logger;

/**
 * @author anasseh
 * @author HORRI Khalid
 * @lastModifiedVersion 5.4
 * 
 */

@Stateless
public class MediationAsync {

    @Inject
    private MediationJobBean mediationJobBean;

    @Inject
    protected Logger log;

    @Inject
    private JobExecutionService jobExecutionService;

    @Inject
    private CurrentUserProvider currentUserProvider;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    /**
     * <p>Process mediation files, one file at a time in a separate transaction is the default behavior.</p>
     * <p>If the script code is not null a personalized mediation script can be precessed</p>
     * 
     * @param files Files to process
     * @param result Job execution result
     * @param parameter Parameter
     * @param lastCurrentUser Current user. In case of multitenancy, when user authentication is forced as result of a fired trigger (scheduled jobs, other timed event
     *        expirations), current user might be lost, thus there is a need to reestablish.
     * @param scriptCode  the script code to be executed.
     * @throws BusinessException thrown if the script code is invalid or an error occurred in the script
     * @return Future String
     *
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<String> launchAndForget(List<File> files, JobExecutionResultImpl result, String parameter, MeveoUser lastCurrentUser, String scriptCode)
            throws BusinessException {

        currentUserProvider.reestablishAuthentication(lastCurrentUser);

        if (scriptCode == null) {
            for (File file : files) {
                if (!jobExecutionService.isJobRunningOnThis(result.getJobInstance())) {
                    break;
                }
                mediationJobBean.execute(result, parameter, file);
            }
        } else {

            Map<String, Object> context = new HashedMap();
            ScriptInterface script = scriptInstanceService.getScriptInstance(scriptCode);
            context.put(Script.CONTEXT_CURRENT_USER, lastCurrentUser);
            context.put("CDR_FILES", files);
            script.execute(context);
        }
        return new AsyncResult<String>("OK");
    }
}