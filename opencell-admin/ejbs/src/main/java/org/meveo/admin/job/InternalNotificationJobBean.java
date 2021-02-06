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

import java.text.SimpleDateFormat;
import java.util.Date;
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
import javax.persistence.Query;

import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.commons.utils.StringUtils;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.notification.Notification;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.job.JobExecutionService.JobSpeedEnum;
import org.meveo.service.notification.GenericNotificationService;
import org.meveo.service.script.ScriptInstanceService;

/**
 * Job implementation to launch notification of entities returned by a filter
 */
@Stateless
public class InternalNotificationJobBean extends IteratorBasedJobBean<Object> {

    private static final long serialVersionUID = -4180210233668446254L;

    /** The df. */
    // iso 8601 date and datetime format
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    /** The tf. */
    SimpleDateFormat tf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:hh");

    @Inject
    @MeveoJpa
    private EntityManagerWrapper emWrapper;

    /** The manager. */
    @Inject
    private BeanManager manager;

    /** The notification service. */
    @Inject
    private GenericNotificationService notificationService;

    /** The script instance service. */
    @Inject
    private ScriptInstanceService scriptInstanceService;

    /**
     * Notification to fire - job execution parameter
     */
    private Notification notification;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        super.execute(jobExecutionResult, jobInstance, this::initJobAndGetDataToProcess, this::executeNotification, null, null, JobSpeedEnum.NORMAL);
        notification = null;
    }

    /**
     * Initialize job settings and retrieve data to process
     * 
     * @param jobExecutionResult Job execution result
     * @return An iterator over a list of entities to execute the script on
     */
    @SuppressWarnings("unchecked")
    private Optional<Iterator<Object>> initJobAndGetDataToProcess(JobExecutionResultImpl jobExecutionResult) {

        String filterCode = (String) this.getParamOrCFValue(jobExecutionResult.getJobInstance(), "InternalNotificationJob_filterCode");
        String notificationCode = (String) this.getParamOrCFValue(jobExecutionResult.getJobInstance(), "InternalNotificationJob_notificationCode");

        if (StringUtils.isBlank(filterCode)) {
            jobExecutionResult.addErrorReport("Job has no filter SQL query set.");
            return Optional.empty();
        }

        notification = notificationService.findByCode(notificationCode);
        if (notification == null) {
            jobExecutionResult.addErrorReport("No notification found for " + notificationCode);
            return Optional.empty();
        }

        if (notification.getScriptInstance() == null) {
            jobExecutionResult.addErrorReport("Notification " + notificationCode + " has no script to execute");
            return Optional.empty();
        }

        String queryStr = null;
        try {
            queryStr = filterCode.replaceAll("#\\{date\\}", df.format(new Date()));
            queryStr = queryStr.replaceAll("#\\{dateTime\\}", tf.format(new Date()));
            Query query = emWrapper.getEntityManager().createNativeQuery(queryStr);

            List<Object> items = query.getResultList();

            return Optional.of(new SynchronizedIterator<Object>(items));

        } catch (Exception e) {
            log.error("Failed to execute a filter query {}", queryStr);
            jobExecutionResult.addErrorReport("Failed to execute a query to filter the data");
            return Optional.empty();
        }
    }

    /**
     * Execute notification on an entity
     * 
     * @param entity Entity to execute notification on
     * @param jobExecutionResult Job execution result
     */
    private void executeNotification(Object entity, JobExecutionResultImpl jobExecutionResult) {

        Map<Object, Object> userMap = new HashMap<Object, Object>();
        userMap.put("event", entity);
        userMap.put("manager", manager);

        if (!StringUtils.isBlank(notification.getElFilter())) {
            boolean applies = ValueExpressionWrapper.evaluateToBoolean(notification.getElFilter(), userMap);
            if (!applies) {
                return;
            }
        }

        Map<String, Object> paramsEvaluated = new HashMap<String, Object>();
        for (Map.Entry<String, String> entry : notification.getParams().entrySet()) {
            paramsEvaluated.put((String) entry.getKey(), ValueExpressionWrapper.evaluateExpression((String) entry.getValue(), userMap, String.class));
        }
        scriptInstanceService.execute(notification.getScriptInstance().getCode(), paramsEvaluated);

    }
}