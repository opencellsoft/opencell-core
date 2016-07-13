package org.meveo.admin.job;

import java.io.Serializable;
import java.lang.reflect.Constructor;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.admin.wf.DunningWF;
import org.meveo.admin.wf.IWorkflowType;
import org.meveo.admin.wf.WorkflowType;
import org.meveo.event.qualifier.Rejected;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.BaseEntity;
import org.meveo.model.admin.User;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.rating.EDR;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.model.wf.Workflow;
import org.meveo.service.billing.impl.EdrService;
import org.meveo.service.billing.impl.UsageRatingService;
import org.slf4j.Logger;

/**
 * 
 * @author anasseh
 */

@Stateless
public class UnitWorkflowJobBean<E extends BaseEntity> {

    @Inject
    private Logger log;

    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void execute(JobExecutionResultImpl result, User currentUser, Object object,Workflow workflow) {    	
    	try {
    		
    		Constructor constructor = Class.forName(workflow.getWfType()).getConstructor(object.getClass());
    		IWorkflowType  wfType =  (IWorkflowType) constructor.newInstance((CustomerAccount) object);
            log.info("ActualStatus:"+wfType.getActualStatus());
            log.info("StatusList:"+wfType.getStatusList());
            log.info("ActualStatus:"+wfType.getActualStatus());
    		
        } catch (Exception e) {
            log.error("Failed to unit usage rate for {}", object, e);           
          //  result.registerError(edrId, e.getMessage());
        }
    }
}