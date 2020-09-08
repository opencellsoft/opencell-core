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
			result.registerError("Error in " + scriptCode + " init :" + e.getMessage());
		}
	}

	@JpaAmpNewTx
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void execute(JobExecutionResultImpl result, String scriptCode, Map<String, Object> context)
			throws BusinessException {
		Callable<String> task = () -> scriptingAsync.runScript(result, scriptCode, context);
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
		Callable<String> task = () -> scriptingAsync.runScriptWithoutTx(result, scriptCode, context);
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
	public void finalize(JobExecutionResultImpl result, String scriptCode, Map<String, Object> context)
			throws BusinessException {
		ScriptInterface script = null;
		try {
			script = scriptInstanceService.getScriptInstance(scriptCode);
			script.finalize(context);

		} catch (Exception e) {
			log.error("Exception on finalize script", e);
			result.registerError("Error in " + scriptCode + " finalize :" + e.getMessage());
		}
	}

	long convert(Object s) {
		long result = (long) ((StringUtils.isBlank(s)) ? 0l : ConvertUtils.convert(s + "", Long.class));
		return result;
	}
}
