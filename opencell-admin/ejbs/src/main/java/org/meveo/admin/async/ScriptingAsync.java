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

package org.meveo.admin.async;

import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.apache.commons.beanutils.ConvertUtils;
import org.meveo.admin.job.logging.JobMultithreadingHistoryInterceptor;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptInterface;

/**
 * 
 * @author anasseh
 *
 */

@Stateless
public class ScriptingAsync {

    @Inject
    protected ScriptInstanceService scriptInstanceService;

    @Inject
    private CurrentUserProvider currentUserProvider;

    @Inject
    protected JobExecutionService jobExecutionService;
    
    /**
     * 
     * @param result
     * @param scriptCode
     * @param context
     * @param currentUser
     * @param script
     * @return
     */
    @Interceptors({ JobMultithreadingHistoryInterceptor.class })
    public String runScript(JobExecutionResultImpl result, String scriptCode, Map<String, Object> context, MeveoUser currentUser, ScriptInterface script) {

        try {
            if (currentUserProvider.getCurrentUserProviderCode() == null) {
                currentUserProvider.forceAuthentication(currentUser.getUserName(), currentUser.getProviderCode());
            }

            context.put(Script.JOB_EXECUTION_RESULT, result);
            script.execute(context);
            if (context.containsKey(Script.JOB_RESULT_NB_OK)) {
                result.setNbItemsCorrectlyProcessed(convert(context.get(Script.JOB_RESULT_NB_OK)));
            } else {
                jobExecutionService.registerSucces(result);
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
            jobExecutionService.registerError(result, "Error in " + scriptCode + " execution :" + e.getMessage());
        }

        return "OK";
    }

	@TransactionAttribute(TransactionAttributeType.NEVER)
	public String runScriptWithoutTx(JobExecutionResultImpl result, String scriptCode, Map<String, Object> context, MeveoUser currentUser, ScriptInterface script) {

		try {
		    if (currentUserProvider.getCurrentUserProviderCode() == null) {
                currentUserProvider.forceAuthentication(currentUser.getUserName(), currentUser.getProviderCode());
            }

            context.put(Script.JOB_EXECUTION_RESULT, result);
            script.execute(context);
			if (context.containsKey(Script.JOB_RESULT_NB_OK)) {
				result.setNbItemsCorrectlyProcessed(convert(context.get(Script.JOB_RESULT_NB_OK)));
			} else {
				jobExecutionService.registerSucces(result);
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
			jobExecutionService.registerError(result, "Error in " + scriptCode + " execution :" + e.getMessage());
		}

		return "OK";
	}

    long convert(Object s) {
        long result = (long) ((StringUtils.isBlank(s)) ? 0l : ConvertUtils.convert(s + "", Long.class));
        return result;
    }

}