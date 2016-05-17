package org.meveo.admin.job;

import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.User;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptInterface;
import org.slf4j.Logger;

@Stateless
public class ScriptingJobBean {

	@Inject
	private Logger log;

	@Inject
	ScriptInstanceService scriptInstanceService;

	
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void init(JobExecutionResultImpl result, User currentUser, String scriptCode, Map<String, Object> context) throws BusinessException {
		ScriptInterface script = null;
		try {
			script = scriptInstanceService.getScriptInstance(currentUser.getProvider(), scriptCode);			
			script.init(context, currentUser);
		} catch (Exception e) {
			log.error("Exception on init script", e);
			result.registerError("Error in " + scriptCode + " init :" + e.getMessage());
		} 
	}
	

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void execute( JobExecutionResultImpl result, User currentUser, String scriptCode, Map<String, Object> context) throws BusinessException {
		ScriptInterface script = null;
		try {
			script = scriptInstanceService.getScriptInstance(currentUser.getProvider(), scriptCode);			
			script.execute(context, currentUser);
		} catch (Exception e) {
			log.error("Exception on execute script", e);
			result.registerError("Error in " + scriptCode + " execution :" + e.getMessage());
		} 
	}

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void finalize(JobExecutionResultImpl result, User currentUser, String scriptCode, Map<String, Object> context) throws BusinessException {
		ScriptInterface script = null;
		try {
			script = scriptInstanceService.getScriptInstance(currentUser.getProvider(), scriptCode);
			script.finalize(context, currentUser);
			
		} catch (Exception e) {
			log.error("Exception on finalize script", e);
			result.registerError("Error in " + scriptCode + " finalize :" + e.getMessage());
		} 
	}

}
