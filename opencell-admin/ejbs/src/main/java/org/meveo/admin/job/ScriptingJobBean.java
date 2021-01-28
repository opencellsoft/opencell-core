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

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;

import org.apache.commons.beanutils.ConvertUtils;
import org.meveo.admin.async.ScriptingAsync;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptInterface;
import org.slf4j.Logger;

@Stateless
public class ScriptingJobBean extends BaseJobBean {

	@Inject
	private Logger log;

	@Inject
	private ScriptInstanceService scriptInstanceService;

	@Inject
	private JobExecutionService jobExecutionService;

	@Inject
	@CurrentUser
	protected MeveoUser currentUser;
	
	@Resource(lookup = "java:jboss/ee/concurrency/executor/default")
	ManagedExecutorService executor;
	
	@Inject
	private ScriptingAsync scriptingAsync;

	@JpaAmpNewTx
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void init(JobExecutionResultImpl result, String scriptCode, Map<String, Object> context)
			throws BusinessException {
		ScriptInterface script = null;
		try {
			script = scriptInstanceService.getScriptInstance(scriptCode);
			script.init(context);
		} catch (Exception e) {
			log.error("Exception on init script", e);
			jobExecutionService.registerError(result, "Error in " + scriptCode + " init :" + e.getMessage());
		}
	}

	@JpaAmpNewTx
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void execute(JobExecutionResultImpl result, String scriptCode, Map<String, Object> context)
			throws BusinessException {
		ScriptInterface script = scriptInstanceService.getScriptInstance(scriptCode);
		MeveoUser lastCurrentUser = currentUser.unProxy();
		Callable<String> task = () -> scriptingAsync.runScript(result, scriptCode, context, lastCurrentUser,script);
		Future<String> futureResult = executor.submit(task);
		while (!futureResult.isDone()) {
			try {
				Thread.sleep((long) 2000);
				if (!jobExecutionService.isJobRunningOnThis(result.getJobInstance())) {
					futureResult.cancel(true);
				}
			} catch (InterruptedException e) {
				log.error("Failed to complete script execution : ", e);
			}
		}

	}

	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void executeWithoutTx(JobExecutionResultImpl result, String scriptCode, Map<String, Object> context)
			throws BusinessException {
	    ScriptInterface script = scriptInstanceService.getScriptInstance(scriptCode);
        MeveoUser lastCurrentUser = currentUser.unProxy();
		Callable<String> task = () -> scriptingAsync.runScriptWithoutTx(result, scriptCode, context, lastCurrentUser,script);
		Future<String> futureResult = executor.submit(task);
		while (!futureResult.isDone()) {
			try {
				Thread.sleep((long) 2000);
				if (!jobExecutionService.isJobRunningOnThis(result.getJobInstance())) {
					futureResult.cancel(true);
				}
			} catch (InterruptedException e) {
				log.error("Failed to complete script execution : ", e);
			}
		}
	}

	@JpaAmpNewTx
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void complete(JobExecutionResultImpl result, String scriptCode, Map<String, Object> context)
			throws BusinessException {
		ScriptInterface script = null;
		try {
			script = scriptInstanceService.getScriptInstance(scriptCode);
			script.terminate(context);

		} catch (Exception e) {
			log.error("Exception on finalize script", e);
			jobExecutionService.registerError(result, "Error in " + scriptCode + " finalize :" + e.getMessage());
		}
	}

	long convert(Object s) {
		long result = (long) ((StringUtils.isBlank(s)) ? 0l : ConvertUtils.convert(s + "", Long.class));
        return result;
    }
}