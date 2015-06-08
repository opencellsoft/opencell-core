package org.meveo.admin.job;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.admin.User;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.billing.impl.ReservationService;
import org.meveo.service.job.Job;

@Startup
@Singleton
public class WalletReservationJob extends Job {

    @Inject
    private ReservationService reservationService;

    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @Override
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance, User currentUser) throws BusinessException {
        int rowsUpdated = reservationService.updateExpiredReservation(currentUser.getProvider());
        if (rowsUpdated != 0) {
            log.info(rowsUpdated + " rows updated.");
        }
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return JobCategoryEnum.WALLET;
    }
}