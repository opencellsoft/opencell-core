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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;

import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.admin.exception.InvalidScriptException;
import org.meveo.model.IEntity;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.filter.Filter;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.filter.FilterService;
import org.meveo.service.job.JobExecutionService.JobSpeedEnum;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptInterface;

/**
 * Job implementation to execute the given script for each entity returned from the given filter.
 * 
 * @author anasseh
 * @author Andrius Karpavicius
 */
@Stateless
public class FilteringJobBean extends IteratorBasedJobBean<IEntity> {

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

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        super.execute(jobExecutionResult, jobInstance, this::initJobAndGetDataToProcess, this::applyScriptOnEntity, null, this::finalizeScript, JobSpeedEnum.NORMAL);

        scriptInterface = null;
        scriptContext = null;
        recordVariableName = null;
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

        String filterCode = ((EntityReferenceWrapper) this.getParamOrCFValue(jobInstance, "FilteringJob_filter")).getCode();
        String scriptCode = ((EntityReferenceWrapper) this.getParamOrCFValue(jobInstance, "FilteringJob_script")).getCode();
        recordVariableName = (String) this.getParamOrCFValue(jobInstance, "FilteringJob_recordVariableName");

        Filter filter = filterService.findByCode(filterCode);
        if (filter == null) {
            jobExecutionResult.registerError("Cant find filter : " + filterCode);
            return Optional.empty();
        }

        try {
            scriptInterface = scriptInstanceService.getScriptInstance(scriptCode);

        } catch (EntityNotFoundException | InvalidScriptException e) {
            jobExecutionResult.registerError(e.getMessage());
            return Optional.empty();
        }

        Map<String, Object> scriptParams = (Map<String, Object>) this.getParamOrCFValue(jobInstance, "FilteringJob_variables");
        if (scriptParams != null) {
            Map<Object, Object> elContext = new HashMap<>();
            elContext.put("manager", manager);
            elContext.put("currentUser", currentUser);
            elContext.put("appProvider", appProvider);

            for (Map.Entry<String, Object> entry : scriptParams.entrySet()) {
                if (entry.getValue() instanceof String) {
                    scriptContext.put(entry.getKey(), ValueExpressionWrapper.evaluateExpression((String) entry.getValue(), elContext, Object.class));
                } else {
                    scriptContext.put(entry.getKey(), entry.getValue());
                }
            }
        }

        scriptInterface.init(scriptContext);

        Map<String, Object> sqlVariables = (Map<String, Object>) this.getParamOrCFValue(jobInstance, "FilteringJob_sql_variables");

        Map<String, Object> sqlParams = new HashMap<String, Object>();

        if (sqlVariables != null) {
            Map<Object, Object> elContext = new HashMap<>();
            elContext.put("manager", manager);
            elContext.put("currentUser", currentUser);
            elContext.put("appProvider", appProvider);

            for (Map.Entry<String, Object> entry : sqlVariables.entrySet()) {
                if (entry.getValue() instanceof String) {
                    sqlParams.put(entry.getKey(), ValueExpressionWrapper.evaluateExpression((String) entry.getValue(), elContext, Object.class));
                } else {
                    sqlParams.put(entry.getKey(), entry.getValue());
                }
            }
        }

        List filtredEntities = filterService.filteredListAsObjects(filter, sqlParams);

        return Optional.of(new SynchronizedIterator<IEntity>(filtredEntities));
    }

    /**
     * Apply script on entity
     * 
     * @param entity Entity
     * @param jobExecutionResult Job execution result
     */
    private void applyScriptOnEntity(IEntity entity, JobExecutionResultImpl jobExecutionResult) {

        Map<String, Object> context = new HashMap<String, Object>();
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
            scriptInterface.terminate(scriptContext);

        } catch (Exception e) {
            log.error("Error on script finalize execute", e);
            jobExecutionResult.setReport("Finalize error:" + e.getMessage());
        }
    }
}
