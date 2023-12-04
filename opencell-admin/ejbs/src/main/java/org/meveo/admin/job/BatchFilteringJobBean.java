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

import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.admin.exception.InvalidScriptException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.IEntity;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.filter.Filter;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.filter.FilterService;
import org.meveo.service.job.Job;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptInterface;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Job implementation to execute the given script for a list of entities returned from the given filter.
 * 
 * @author a.rouaguebe
 */
@Stateless
public class BatchFilteringJobBean extends IteratorBasedJobBean<IEntity> {

    private static final long serialVersionUID = 3279519649411448927L;

    @Inject
    private FilterService filterService;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Inject
    private BeanManager manager;

    /**
     * Script to run - Job execution parameter
     */
    private ScriptInterface scriptInterface;

    /**
     * Script context - Job execution parameter
     */
    private Map<String, Object> scriptContext;

    /**
     * Script record variable name - Job execution parameter
     */
    private String recordVariableName;

    @Inject
    @MeveoJpa
    private EntityManagerWrapper emWrapper;

    private StatelessSession statelessSession;

    private ScrollableResults scrollableResults;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        super.execute(jobExecutionResult, jobInstance, this::initJobAndGetDataToProcess, null, this::applyScriptOnListEntity, null, this::finalizeScript, null);

        scriptInterface = null;
        scriptContext = null;
        recordVariableName = null;
        scrollableResults = null;
        statelessSession = null;
    }

    private void applyScriptOnListEntity(List<IEntity> entities, JobExecutionResultImpl jobExecutionResult) {
        Map<String, Object> context = new HashMap<>(scriptContext);
        context.put(recordVariableName, entities.size() == 1 ? entities.get(0) : entities);
        context.put(Script.CONTEXT_CURRENT_USER, currentUser);
        context.put(Script.CONTEXT_APP_PROVIDER, appProvider);

        scriptInterface.execute(context);
    }

    /**
     * Initialize job settings and retrieve data to process
     * 
     * @param jobExecutionResult Job execution result
     * @return An iterator over a list of entities to execute the script on
     */
    @SuppressWarnings("unchecked")
    private Optional<Iterator<IEntity>> initJobAndGetDataToProcess(JobExecutionResultImpl jobExecutionResult) {

        JobInstance jobInstance = jobExecutionResult.getJobInstance();

        scriptInterface = null;
        scriptContext = new HashMap<String, Object>();
        scriptContext.put(Script.JOB_EXECUTION_RESULT, jobExecutionResult);

        String filterCode = ((EntityReferenceWrapper) this.getParamOrCFValue(jobInstance, "BatchFilteringJob_filter")).getCode();
        String scriptCode = ((EntityReferenceWrapper) this.getParamOrCFValue(jobInstance, "BatchFilteringJob_script")).getCode();
        recordVariableName = (String) this.getParamOrCFValue(jobInstance, "BatchFilteringJob_recordVariableName");

        Filter filter = filterService.findByCode(filterCode);
        if (filter == null) {
            jobExecutionResult.registerError("Cant find filter : " + filterCode);
            return Optional.empty();
        }

        if(StringUtils.isBlank(filter.getPollingQuery())) {
            jobExecutionResult.registerError("Filter : " + filterCode + " has no polling query. Check filter configuration.");
            return Optional.empty();
        }

        try {
            scriptInterface = scriptInstanceService.getScriptInstance(scriptCode);

        } catch (EntityNotFoundException | InvalidScriptException e) {
            jobExecutionResult.registerError(e.getMessage());
            return Optional.empty();
        }

        Map<Object, Object> elContext = new HashMap<>();
        elContext.put("manager", manager);
        elContext.put("currentUser", currentUser);
        elContext.put("appProvider", appProvider);

        Map<String, Object> scriptParams = (Map<String, Object>) this.getParamOrCFValue(jobInstance, "BatchFilteringJob_variables");
        if (scriptParams != null) {
            for (Map.Entry<String, Object> entry : scriptParams.entrySet()) {
                if (entry.getValue() instanceof String) {
                    scriptContext.put(entry.getKey(), ValueExpressionWrapper.evaluateExpression((String) entry.getValue(), elContext, Object.class));
                } else {
                    scriptContext.put(entry.getKey(), entry.getValue());
                }
            }
        }

        scriptInterface.init(scriptContext);

        Map<String, Object> sqlVariables = (Map<String, Object>) this.getParamOrCFValue(jobInstance, "BatchFilteringJob_sql_variables");
        Map<String, Object> sqlParams = new HashMap<>();
        if (sqlVariables != null) {
            for (Map.Entry<String, Object> entry : sqlVariables.entrySet()) {
                if (entry.getValue() instanceof String) {
                    sqlParams.put(entry.getKey(), ValueExpressionWrapper.evaluateExpression((String) entry.getValue(), elContext, Object.class));
                } else {
                    sqlParams.put(entry.getKey(), entry.getValue());
                }
            }
        }
        
        String pollingQuery = filter.getPollingQuery();
        Query countQuery = emWrapper.getEntityManager()
                                    .createQuery("SELECT count(*) " + pollingQuery.substring(pollingQuery.toUpperCase().indexOf("FROM")));
        sqlParams.forEach(countQuery::setParameter);
        var count = (Long) countQuery.getSingleResult();
        
        statelessSession = emWrapper.getEntityManager().unwrap(Session.class).getSessionFactory().openStatelessSession();
        org.hibernate.query.Query<IEntity> query = (org.hibernate.query.Query<IEntity>) statelessSession.createQuery(filter.getPollingQuery());
        for (Map.Entry<String, Object> entry : sqlParams.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        Long batchSize = (Long) getParamOrCFValue(jobInstance, Job.CF_BATCH_SIZE, 10000L);
        Long nbThreads = (Long) this.getParamOrCFValue(jobInstance, Job.CF_NB_RUNS, -1L);
        if (nbThreads == -1) {
            nbThreads = (long) Runtime.getRuntime().availableProcessors();
        }
        scrollableResults = query.setReadOnly(true).setCacheable(false).setFetchSize(Optional.ofNullable(batchSize).map(Long::intValue).orElse(1)).scroll(ScrollMode.FORWARD_ONLY);
        
        

        return Optional.of(new SynchronizedIterator<>(scrollableResults, count.intValue()));
    }



    /**
     * Apply script on entity
     * 
     * @param entity Entity
     * @param jobExecutionResult Job execution result
     */
    private void applyScriptOnEntity(IEntity entity, JobExecutionResultImpl jobExecutionResult) {
        
        Map<String, Object> context = new HashMap<>(scriptContext);
        context.put(recordVariableName, entity);
        context.put(Script.CONTEXT_CURRENT_USER, currentUser);
        context.put(Script.CONTEXT_APP_PROVIDER, appProvider);

        scriptInterface.execute(context);
    }

    /**
     * Finalize script
     * 
     * @param jobExecutionResult Job execution result
     */
    private void finalizeScript(JobExecutionResultImpl jobExecutionResult) {
        try {
            log.info("Finalize script");
            scriptInterface.terminate(scriptContext);
            
            if(scrollableResults != null) {
                scrollableResults.close();
            }
            
            if(statelessSession != null) {
                statelessSession.close();
            }

        } catch (Exception e) {
            log.error("Error on script finalize execute", e);
            jobExecutionResult.setReport("Finalize error:" + e.getMessage());
        }
    }
}
