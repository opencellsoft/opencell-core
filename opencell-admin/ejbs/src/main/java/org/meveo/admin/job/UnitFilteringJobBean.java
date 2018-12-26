package org.meveo.admin.job;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptInterface;
import org.meveo.util.ApplicationProvider;

@Stateless
public class UnitFilteringJobBean {

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;
    
    @Inject
    @ApplicationProvider
    protected Provider appProvider;
    
    /**
     * Excute the script for the filtered entity in a single transaction.
     * 
     * @param result the result exception
     * @param obj the filtered entity
     * @param scriptInterface the script to execute
     * @param recordVariableName the recordVariableName
     */
    @JpaAmpNewTx
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void execute(JobExecutionResultImpl result, Object obj, ScriptInterface scriptInterface, String recordVariableName) {

        Map<String, Object> context = new HashMap<String, Object>();
        context.put(recordVariableName, obj);
        context.put(Script.CONTEXT_CURRENT_USER, currentUser);
        context.put(Script.CONTEXT_APP_PROVIDER, appProvider);
        try {
            scriptInterface.execute(context);
            result.registerSucces();
        } catch (BusinessException ex) {
            result.registerError(ex.getMessage());
        }
    }
}
