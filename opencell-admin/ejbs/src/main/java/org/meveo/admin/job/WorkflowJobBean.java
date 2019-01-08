package org.meveo.admin.job;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.async.SubListCreator;
import org.meveo.admin.async.WorkflowAsync;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.InvalidScriptException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.admin.wf.WorkflowType;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.BusinessEntity;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.filter.Filter;
import org.meveo.model.filter.FilterSelector;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.wf.Workflow;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.filter.FilterService;
import org.meveo.service.script.wf.WFTypeScript;
import org.meveo.service.wf.WorkflowService;
import org.slf4j.Logger;

@Stateless
public class WorkflowJobBean extends BaseJobBean {

    @Inject
    private Logger log;

    @Inject
    private FilterService filterService;

    @Inject
    private WorkflowService workflowService;

    @Inject
    private WorkflowAsync workflowAsync;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    @SuppressWarnings("unchecked")
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {
        log.debug("Running with parameter={}", jobInstance.getParametres());

        try {
            Long nbRuns = new Long(1);
            Long waitingMillis = new Long(0);
            String filterCode = null;
            String workflowCode = null;
            try {
                nbRuns = (Long) this.getParamOrCFValue(jobInstance, "wfJob_nbRuns");
                waitingMillis = (Long) this.getParamOrCFValue(jobInstance, "wfJob_waitingMillis$");
                if (nbRuns == -1) {
                    nbRuns = (long) Runtime.getRuntime().availableProcessors();
                }
                filterCode   = fetchCFEntityReferenceCode(jobInstance, "wfJob_filter");
                workflowCode = fetchCFEntityReferenceCode(jobInstance, "wfJob_workflow");

            } catch (Exception e) {
                log.warn("Cant get customFields for " + jobInstance.getJobTemplate(), e.getMessage());
                log.error("error:", e);
                nbRuns = new Long(1);
                waitingMillis = new Long(0);
            }
            
            Workflow workflow = workflowService.findByCode(workflowCode);
            if (workflow == null) {
               throw new BusinessException(String.format("No Workflow found with code = [%s]", workflowCode));  
            }
            
            Filter filter = filterService.findByCode(filterCode);
            if (filter == null) {
                filter = this.filterByWfType(workflow.getWfType()); 
            }

            log.debug("filter:{}", filter == null ? null : filter.getCode());
            List<BusinessEntity> entities = (List<BusinessEntity>) filterService.filteredListAsObjects(filter);
            log.debug("entities:" + entities.size());
            result.setNbItemsToProcess(entities.size());

            List<Future<String>> futures = new ArrayList<Future<String>>();
            SubListCreator subListCreator = new SubListCreator(entities, nbRuns.intValue());
            log.debug("block to run:" + subListCreator.getBlocToRun());
            log.debug("nbThreads:" + nbRuns);

            MeveoUser lastCurrentUser = currentUser.unProxy();
            while (subListCreator.isHasNext()) {
                futures.add(workflowAsync.launchAndForget((List<BusinessEntity>) subListCreator.getNextWorkSet(), workflow, result, lastCurrentUser));

                if (subListCreator.isHasNext()) {
                    try {
                        Thread.sleep(waitingMillis.longValue());
                    } catch (InterruptedException e) {
                        log.error("", e);
                    }
                }
            }
            // Wait for all async methods to finish
            for (Future<String> future : futures) {
                try {
                    future.get();

                } catch (InterruptedException e) {
                    // It was cancelled from outside - no interest

                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    result.registerError(cause.getMessage());
                    log.error("Failed to execute async method", cause);
                }
            }
        } catch (Exception e) {
            log.error("Failed to run workflow job", e);
            result.registerError(e.getMessage());
        }
    }
    
    private String fetchCFEntityReferenceCode(JobInstance jobInstance, String cfKey) {
        try {
            EntityReferenceWrapper reference = (EntityReferenceWrapper) customFieldInstanceService.getCFValue(jobInstance, cfKey);
            if (reference != null) {
                return reference.getCode();
            }
        } catch (Exception e) {
            log.error("Error on fetchCFEntityReferenceCode : {}", e.getMessage());
        } 
        return null;
    }

    /**
     * @param wfType
     * @return A Filter to retrieve all Entities of the given WorkflowType subClass generic type : e.g for DunningWF it will be a filter for all CustomerAccount
     * @throws BusinessException
     */
    private Filter filterByWfType(String wfType) throws BusinessException {
        Type entityType =  this.getEntityClassByWfType(wfType);
        try {
            FilterSelector filterSelector = new FilterSelector();
            filterSelector.setTargetEntity(entityType.getTypeName());
            filterSelector.setAlias("e");
            Filter filter = new Filter();
            filter.setPrimarySelector(filterSelector);

            return filter;  
        } catch (Exception e) {
            throw new BusinessException(String.format("Error on filterByWfType : [%s]", e.getMessage()));
        }
    }

    /**
     * @param wfType
     * @return The SubClass generic type of the given workflow type. e.g : for DunningWF it will return CustomerAccount.class
     * @throws BusinessException
     */
    private Type getEntityClassByWfType(String wfType) throws BusinessException {
        try {
            Class<?> clazz = workflowService.getWFTypeClassForName(wfType);
            Type type = clazz.getGenericSuperclass();
            while (isNotWFSuperClass(type)) {
                if (type instanceof ParameterizedType) {
                    type = ((Class<?>) ((ParameterizedType) type).getRawType()).getGenericSuperclass();
                } else {
                    type = ((Class<?>) type).getGenericSuperclass();
                }
            }
            return ((ParameterizedType) type).getActualTypeArguments()[0];
        } catch (ClassNotFoundException | InvalidScriptException e) {
            throw new BusinessException(String.format("Error getting Entity Class By Wf Type : [%s]", e.getMessage()));
        }
    }

    private boolean isNotWFSuperClass(Type type) {
        if (!(type instanceof ParameterizedType)) {
            return true;
        }
        Type rawType = ((ParameterizedType) type).getRawType();
        return rawType != WorkflowType.class  && rawType != WFTypeScript.class;
    }

}
