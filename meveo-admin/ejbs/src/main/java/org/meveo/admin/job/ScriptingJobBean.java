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
	
	private final String RESULT_REPORT="RESULT_REPORT";
	private final String RESULT_TO_PROCESS="RESULT_TO_PROCESS";
	private final String RESULT_NB_OK="RESULT_NB_OK";
	private final String RESULT_NB_WARN="RESULT_NB_WARN";
	private final String RESULT_NB_KO="RESULT_NB_KO";

	
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
			if(context.containsKey(RESULT_NB_OK)){
				result.setNbItemsCorrectlyProcessed((long) context.get(RESULT_NB_OK));
			} else {
				result.registerSucces();
			}
			if(context.containsKey(RESULT_NB_WARN)){
				result.setNbItemsProcessedWithWarning((long) context.get(RESULT_NB_WARN));
			}
			if(context.containsKey(RESULT_NB_KO)){
				result.setNbItemsProcessedWithError((long) context.get(RESULT_NB_KO));
			}
			if(context.containsKey(RESULT_TO_PROCESS)){
				result.setNbItemsToProcess((long) context.get(RESULT_TO_PROCESS));
			}
			if(context.containsKey(RESULT_REPORT)){
				result.setNbItemsToProcess((long) context.get(RESULT_REPORT));
			}
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
