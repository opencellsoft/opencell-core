package org.meveo.service.notification;

import java.util.HashMap;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.notification.JobTrigger;
import org.meveo.model.notification.NotificationHistoryStatusEnum;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.job.JobInstanceService;
import org.slf4j.Logger;

/**
 * Lauch a jobInstance and create a notificationHistory
 * 
 * @author anasseh
 * @since 19.06.2015
 * 
 */
@Stateless
public class JobTriggerLauncher {

    @Inject
    private NotificationHistoryService notificationHistoryService;

    @Inject
    private JobExecutionService jobExecutionService;
    
    @Inject
    private JobInstanceService jobInstanceService;

    @Inject
    private Logger log;

    @Asynchronous
    public void launch(JobTrigger jobTrigger, Object entityOrEvent) {
        try {
            log.info("launch jobTrigger:{}", jobTrigger);
            HashMap<Object, Object> params = new HashMap<Object, Object>();
            params.put("event", entityOrEvent);
            
            jobExecutionService.executeJob(jobInstanceService.refreshOrRetrieve(jobTrigger.getJobInstance()), params);
            
            log.debug("launch jobTrigger:{} launched", jobTrigger);

            notificationHistoryService.create(jobTrigger, entityOrEvent, "", NotificationHistoryStatusEnum.SENT);

        } catch (Exception e) {
            try {
                notificationHistoryService.create(jobTrigger, entityOrEvent, e.getMessage(), NotificationHistoryStatusEnum.FAILED);
            } catch (BusinessException e2) {
                log.error("Failed to create notification history", e2);
            }
        }
    }
}
