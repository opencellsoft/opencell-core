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

import org.apache.commons.beanutils.ConvertUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptInterface;
import org.meveo.util.ApplicationProvider;

@Stateless
public class ScriptingAsync {

	@Inject
	protected ScriptInstanceService scriptInstanceService;

	@Inject
	@CurrentUser
	protected MeveoUser currentUser;

	@Inject
	@ApplicationProvider
	protected Provider appProvider;

	@JpaAmpNewTx
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
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

	@TransactionAttribute(TransactionAttributeType.NEVER)
	public String runScriptWithoutTx(JobExecutionResultImpl result, String scriptCode, Map<String, Object> context) {
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