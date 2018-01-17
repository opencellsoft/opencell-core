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
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.commons.utils.StringUtils;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.notification.Notification;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.notification.NotificationService;
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

    /** The entity manager. */
    @PersistenceContext(unitName = "MeveoAdmin")
    private EntityManager em;

    /** The manager. */
    @Inject
    private BeanManager manager;

    /** The notification service. */
    @Inject
    NotificationService notificationService;

    /** The script instance service. */
    @Inject
    ScriptInstanceService scriptInstanceService;

    /**
     * Execute.
     *
     * @param filterCode the filter code
     * @param notificationCode the notification code
     * @param result the result
     */
    @SuppressWarnings("rawtypes")
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void execute(String filterCode, String notificationCode, JobExecutionResultImpl result) {
        log.debug("Running with filterCode={}", filterCode);
        if (StringUtils.isBlank(filterCode)) {
            result.registerError("filterCode has no SQL query set.");
            return;
        }

        Notification notification = notificationService.findByCode(notificationCode);
        if (notification == null) {
            result.registerError("no notification found for " + notificationCode);
            return;
        }
        try {

            String queryStr = filterCode.replaceAll("#\\{date\\}", df.format(new Date()));
            queryStr = queryStr.replaceAll("#\\{dateTime\\}", tf.format(new Date()));
            log.debug("execute query:{}", queryStr);
            Query query = em.createNativeQuery(queryStr);
            @SuppressWarnings("unchecked")
            List<Object> results = query.getResultList();
            result.setNbItemsToProcess(results.size());
            for (Object res : results) {
                if (!jobExecutionService.isJobRunningOnThis(result.getJobInstance().getId())) {
                    break;
                }

                Map<Object, Object> userMap = new HashMap<Object, Object>();
                userMap.put("event", res);
                userMap.put("manager", manager);
                if (!StringUtils.isBlank(notification.getElFilter())) {
                    Object o = ValueExpressionWrapper.evaluateExpression(notification.getElFilter(), userMap, Boolean.class);
                    try {
                        if (!(Boolean) o) {
                            result.registerSucces();
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
                        result.registerSucces();
                    } else {
                        log.debug("No script instance on this Notification");
                    }
                } catch (Exception e) {
                    result.registerError("Error execution " + notification.getScriptInstance() + " on " + res);
                    throw new BusinessException("Expression " + notification.getElFilter() + " do not evaluate to boolean but " + res);
                }
            }

        } catch (Exception e) {
            result.registerError("filterCode contain invalid SQL query: " + e.getMessage());
        }
    }

}
