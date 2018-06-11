package org.meveo.admin.job;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.job.Job;

/**
 * Handles subscription renewal or termination once subscription expires, fire handles renewal notice events
 * 
 * @author Andrius Karpavicius
 *
 */
@Stateless
public class SubscriptionStatusJob extends Job {

    @Inject
    private SubscriptionStatusJobBean subscriptionStatusJobBean;

    @Inject
    private SubscriptionService subscriptionService;
    
    @Override
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {

        try {

            List<Long> subscriptionIds = subscriptionService.getSubscriptionsToRenewOrNotify();
            for (Long subscriptionId : subscriptionIds) {
                if (!jobExecutionService.isJobRunningOnThis(result.getJobInstance())) {
                    break;
                }
                subscriptionStatusJobBean.updateSubscriptionStatus(result, subscriptionId);
            }

        } catch (Exception e) {
            log.error("Failed to run subscription status job {}", jobInstance.getCode(), e);
            result.registerError(e.getMessage());
        }
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return JobCategoryEnum.UTILS;
    }
}