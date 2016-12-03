package org.meveo.service.notification;

import java.util.HashMap;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.notification.JobTrigger;
import org.meveo.model.notification.NotificationHistoryStatusEnum;
import org.meveo.service.job.JobInstanceService;
import org.slf4j.Logger;

/**
 * Lauch a jobInstance and create a notificationHistory
 * 
 * @author anasseh
 * @created 19.06.2015
 * 
 */
@Stateless
public class JobTriggerLauncher {

    @Inject
    private NotificationHistoryService notificationHistoryService;

    @Inject
    private JobInstanceService jobInstanceService;

    @Inject
    private Logger log;

    @Asynchronous
    public void launch(JobTrigger jobTrigger, Object entityOrEvent) {
        try {
            log.debug("launch jobTrigger:{}", jobTrigger);
            HashMap<Object, Object> userMap = new HashMap<Object, Object>();
            userMap.put("event", entityOrEvent);
            jobInstanceService.triggerExecution(jobTrigger.getJobInstance().getCode(), jobTrigger.getJobParams(), jobTrigger.getAuditable().getCreator());
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
