package org.meveo.service.notification;

import java.util.HashMap;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.notification.JobTrigger;
import org.meveo.model.notification.NotificationHistoryStatusEnum;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
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

    @Inject
    private CurrentUserProvider currentUserProvider;

    /**
     * Launch job as fired notification result
     * 
     * @param jobTrigger Job type notification that was fired
     * @param entityOrEvent Entity or event that triggered notification
     * @param lastCurrentUser Current user. In case of multitenancy, when user authentication is forced as result of a fired trigger (scheduled jobs, other timed event
     *        expirations), current user might be lost, thus there is a need to reestablish.
     */
    @Asynchronous
    public void launch(JobTrigger jobTrigger, Object entityOrEvent, MeveoUser lastCurrentUser) {
        

        currentUserProvider.reestablishAuthentication(lastCurrentUser);
        
        try {
            log.info("launch jobTrigger:{}", jobTrigger);
            HashMap<Object, Object> params = new HashMap<Object, Object>();
            params.put("event", entityOrEvent);
            
            jobExecutionService.executeJob(jobInstanceService.retrieveIfNotManaged(jobTrigger.getJobInstance()), params);
            
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
