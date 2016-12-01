package org.meveo.admin.job;

import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.beanutils.ConvertUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.script.Script;
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
	
    
    long convert(Object s){
    	long result= (long) ((StringUtils.isBlank(s))?0l:ConvertUtils.convert(s+"",Long.class));
    	return result;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void execute( JobExecutionResultImpl result, User currentUser, String scriptCode, Map<String, Object> context) throws BusinessException {
		ScriptInterface script = null;
		try {
			script = scriptInstanceService.getScriptInstance(currentUser.getProvider(), scriptCode);			
			script.execute(context, currentUser);
			if(context.containsKey(Script.JOB_RESULT_NB_OK)){
				result.setNbItemsCorrectlyProcessed(convert(context.get(Script.JOB_RESULT_NB_OK)));
			} else {
				result.registerSucces();
			}
			if(context.containsKey(Script.JOB_RESULT_NB_WARN)){
				result.setNbItemsProcessedWithWarning(convert(context.get(Script.JOB_RESULT_NB_WARN)));
			}
			if(context.containsKey(Script.JOB_RESULT_NB_KO)){
				result.setNbItemsProcessedWithError(convert(context.get(Script.JOB_RESULT_NB_KO)));
			}
			if(context.containsKey(Script.JOB_RESULT_TO_PROCESS)){
				result.setNbItemsToProcess(convert(context.get(Script.JOB_RESULT_TO_PROCESS)));
			}
			if(context.containsKey(Script.JOB_RESULT_REPORT)){
				result.setReport(context.get(Script.JOB_RESULT_REPORT)+"");
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
