package org.meveo.admin.job;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.admin.wf.IWorkflowType;
import org.meveo.commons.utils.StringUtils;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.IEntity;
import org.meveo.model.admin.User;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.wf.WFAction;
import org.meveo.model.wf.WFTransition;
import org.meveo.model.wf.Workflow;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.wf.BaseEntityService;
import org.meveo.service.wf.WFTransitionService;
import org.slf4j.Logger;

/**
 * 
 * @author anasseh
 */

@Stateless
public class UnitWorkflowJobBean{

    @Inject
    private Logger log;
    
    @Inject
    private WFTransitionService wfTransitionService;
    
    @Inject
    private BaseEntityService baseEntityService;
    


    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void execute(JobExecutionResultImpl result, User currentUser, IEntity entity,Workflow workflow) {    	
    	try {    		
    		Constructor<?> constructor = Class.forName(workflow.getWfType()).getConstructor(entity.getClass());
    		IWorkflowType  wfType =  (IWorkflowType) constructor.newInstance(entity);
            log.debug("ActualStatus:"+wfType.getActualStatus());
            log.debug("StatusList:"+wfType.getStatusList());
            List<WFTransition> listByFromStatus = wfTransitionService.listByFromStatus(wfType.getActualStatus(), workflow);            
            log.debug("listByFromStatus.size:"+(listByFromStatus == null ? null : listByFromStatus.size()));
            for(WFTransition wfTransition :listByFromStatus ){
            	 log.debug("processing transition:"+wfTransition);
            	if(matchExpression(wfTransition.getConditionEl(), entity)){
            		 log.debug("conditionEl is true");
            		 log.debug("listWfActions.size:"+(wfTransition.getWfActions() == null ? null : wfTransition.getWfActions().size()));
            		for(WFAction wfAction : wfTransition.getWfActions()){
            			log.debug("matchExpression wfAction:"+wfAction);
            			if(matchExpression(wfAction.getConditionEl(), entity)){
            				log.debug("wfAction conditionEl is true");
            				matchExpression(wfAction.getActionEl(), entity);
            				log.debug("wfAction executed");
            				//TODO es history
            			}            			
            		}
            		wfType.changeStatus(wfTransition.getToStatus());   
            		log.debug("wfType.changeStatus({}) done",wfTransition.getToStatus());
            		baseEntityService.update(entity, currentUser);
            		log.debug("entity updated");            		
            		break;
            	}            	
            }
            
        } catch (Exception e) {
            log.error("Failed to unit workflow for {}", entity, e);           
            result.registerError(entity.getClass().getName()+entity.getId(), e.getMessage());
        }
    }
    
	private boolean matchExpression(String expression, Object object) throws BusinessException {
		Boolean result = true;
		if (StringUtils.isBlank(expression)) {
			return result;
		}
		Map<Object, Object> userMap = new HashMap<Object, Object>();
		if(expression.indexOf("entity") >= 0 ){			
            userMap.put("entity", object);
		}

		Object res = ValueExpressionWrapper.evaluateExpression(expression, userMap, Boolean.class);
		try {
			result = (Boolean) res;
		} catch (Exception e) {
			throw new BusinessException("Expression " + expression + " do not evaluate to boolean but " + res);
		}
		return result;
	}
}