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
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.persistence.Query;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.commons.utils.StringUtils;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.notification.Notification;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.notification.GenericNotificationService;
import org.meveo.service.script.ScriptInstanceService;
import org.slf4j.Logger;

/**
 * The Class InternalNotificationJobBean.
 */
@Stateless
public class InternalNotificationJobBean {

    /** The log. */
    @Inject
    protected Logger log;

    /** The job execution service . */
    @Inject
    private JobExecutionService jobExecutionService;

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
     * Execute.
     *
     * @param filterCode the filter code
     * @param notificationCode the notification code
     * @param result the result
     */
    @SuppressWarnings("rawtypes")
    @JpaAmpNewTx
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void execute(String filterCode, String notificationCode, JobExecutionResultImpl result) {
        log.debug("Running with filterCode={}", filterCode);
        if (StringUtils.isBlank(filterCode)) {
            jobExecutionService.registerError(result, "filterCode has no SQL query set.");
            return;
        }

        Notification notification = notificationService.findByCode(notificationCode);
        if (notification == null) {
            jobExecutionService.registerError(result, "no notification found for " + notificationCode);
            return;
        }
        try {

            String queryStr = filterCode.replaceAll("#\\{date\\}", df.format(new Date()));
            queryStr = queryStr.replaceAll("#\\{dateTime\\}", tf.format(new Date()));
            log.debug("execute query:{}", queryStr);
            Query query = emWrapper.getEntityManager().createNativeQuery(queryStr);
            @SuppressWarnings("unchecked")
            List<Object> results = query.getResultList();
            result.setNbItemsToProcess(results.size());
            jobExecutionService.initCounterElementsRemaining(result, results.size());
            int i = 0;
            for (Object res : results) {
                i++;
                if (i % JobExecutionService.CHECK_IS_JOB_RUNNING_EVERY_NR == 0 && !jobExecutionService.isJobRunningOnThis(result.getJobInstance().getId())) {
                    break;
                }

                Map<Object, Object> userMap = new HashMap<Object, Object>();
                userMap.put("event", res);
                userMap.put("manager", manager);
                if (!StringUtils.isBlank(notification.getElFilter())) {
                    Object o = ValueExpressionWrapper.evaluateExpression(notification.getElFilter(), userMap, Boolean.class);
                    try {
                        if (!(Boolean) o) {
                            jobExecutionService.registerSucces(result);
                            continue;
                        }
                    } catch (Exception e) {
                        throw new BusinessException("Expression " + notification.getElFilter() + " do not evaluate to boolean but " + res);
                    }
                }
                try {
                    if (notification.getScriptInstance() != null) {
                        Map<String, Object> paramsEvaluated = new HashMap<String, Object>();
                        for (Map.Entry entry : notification.getParams().entrySet()) {
                            paramsEvaluated.put((String) entry.getKey(), ValueExpressionWrapper.evaluateExpression((String) entry.getValue(), userMap, String.class));
                        }
                        scriptInstanceService.execute(notification.getScriptInstance().getCode(), paramsEvaluated);
                        jobExecutionService.registerSucces(result);
                    } else {
                        log.debug("No script instance on this Notification");
                    }
                } catch (Exception e) {
                    jobExecutionService.registerError(result, "Error execution " + notification.getScriptInstance() + " on " + res);
                    throw new BusinessException("Expression " + notification.getElFilter() + " do not evaluate to boolean but " + res);
                }
                jobExecutionService.decCounterElementsRemaining(result);
            }

        } catch (Exception e) {
            jobExecutionService.registerError(result, "filterCode contain invalid SQL query: " + e.getMessage());
        }
    }
}