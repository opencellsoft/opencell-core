package org.meveo.admin.job;

import java.io.Serializable;
import java.util.Date;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.billing.impl.CounterInstanceService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.notification.InboundRequestService;
import org.meveo.service.notification.NotificationHistoryService;
import org.slf4j.Logger;

@Stateless
public class PurgeJobBean implements Serializable {

    private static final long serialVersionUID = 2226065462536318643L;

    @Inject
    private CounterInstanceService counterInstanceService;

    @Inject
    private JobExecutionService jobExecutionService;

    @Inject
    private CustomFieldInstanceService customFieldInstanceService;

    @Inject
    private InboundRequestService inboundRequestService;

    @Inject
    private NotificationHistoryService notificationHistoryService;

    @Inject
    private Logger log;

    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {

        try {
            // Purge job execution history
            String jobCode = (String) customFieldInstanceService.getCFValue(jobInstance, "PurgeJob_jobExecHistory_jobName");
            Long nbDays = (Long) customFieldInstanceService.getCFValue(jobInstance, "PurgeJob_jobExecHistory_nbDays");
            if (nbDays != null) {
                Date date = DateUtils.addDaysToDate(new Date(), nbDays.intValue() * (-1));
                long nbItemsToProcess = jobExecutionService.countJobExecutionHistoryToDelete(jobCode, date);
                if (nbItemsToProcess > 0) {
                    result.setNbItemsToProcess(nbItemsToProcess);
                    long nbSuccess = jobExecutionService.deleteJobExecutionHistory(jobCode, date);
                    result.setNbItemsCorrectlyProcessed(nbSuccess);
                    result.setNbItemsProcessedWithError(nbItemsToProcess - nbSuccess);
                    if (nbSuccess > 0) {
                        result.setReport("Purged " + nbSuccess + (jobCode != null ? " " + jobCode : "") + " job history records");
                    }
                }
            }

            // Purge counter periods
            nbDays = (Long) customFieldInstanceService.getCFValue(jobInstance, "PurgeJob_counterPeriod_nbDays");
            if (nbDays != null) {
                Date date = DateUtils.addDaysToDate(new Date(), nbDays.intValue() * (-1));
                long nbItemsToProcess = counterInstanceService.countCounterPeriodsToDelete(date);
                if (nbItemsToProcess > 0) {
                    result.addNbItemsToProcess(nbItemsToProcess);
                    long nbSuccess = counterInstanceService.deleteCounterPeriods(date);
                    result.addNbItemsCorrectlyProcessed(nbSuccess);
                    result.addNbItemsProcessedWithError(nbItemsToProcess - nbSuccess);
                    if (nbSuccess > 0) {
                        result.addReport("Purged " + nbSuccess + " counter period records");
                    }
                }
            }

            // Purge notification history
            String notificationCode = (String) customFieldInstanceService.getCFValue(jobInstance, "PurgeJob_notificationHistory_notifCode");
            nbDays = (Long) customFieldInstanceService.getCFValue(jobInstance, "PurgeJob_notificationHistory_nbDays");
            if (nbDays != null) {
                Date date = DateUtils.addDaysToDate(new Date(), nbDays.intValue() * (-1));
                long nbItemsToProcess = notificationHistoryService.countHistoryToDelete(notificationCode, date);
                if (nbItemsToProcess > 0) {
                    result.addNbItemsToProcess(nbItemsToProcess);
                    long nbSuccess = notificationHistoryService.deleteHistory(notificationCode, date);
                    result.addNbItemsCorrectlyProcessed(nbSuccess);
                    result.addNbItemsProcessedWithError(nbItemsToProcess - nbSuccess);
                    if (nbSuccess > 0) {
                        result.addReport("Purged " + nbSuccess + (notificationCode != null ? " " + notificationCode : "") + " notification history records");
                    }
                }
            }

            // Purge inbound requests
            nbDays = (Long) customFieldInstanceService.getCFValue(jobInstance, "PurgeJob_inboundRequests_nbDays");
            if (nbDays != null) {
                Date date = DateUtils.addDaysToDate(new Date(), nbDays.intValue() * (-1));
                long nbItemsToProcess = inboundRequestService.countRequestsToDelete(date);
                if (nbItemsToProcess > 0) {
                    result.addNbItemsToProcess(nbItemsToProcess);
                    long nbSuccess = inboundRequestService.deleteRequests(date);
                    result.addNbItemsCorrectlyProcessed(nbSuccess);
                    result.addNbItemsProcessedWithError(nbItemsToProcess - nbSuccess);
                    if (nbSuccess > 0) {
                        result.addReport("Purged " + nbSuccess + " inbound request records");
                    }
                }
            }

        } catch (Exception e) {
            log.error("Failed to purge database", e);
            result.registerError(e.getClass().getName() + " " + e.getMessage());
        }
    }
}
