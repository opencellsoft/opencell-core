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
 * @author Edward P. Legaspi
 * @lastMofiedVersion 7.0
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
    public void launchAsync(JobTrigger jobTrigger, Object entityOrEvent, MeveoUser lastCurrentUser) {
        launch(jobTrigger, entityOrEvent, lastCurrentUser);
    }

    /**
     * Launch job as fired notification result
     * 
     * @param jobTrigger Job type notification that was fired
     * @param entityOrEvent Entity or event that triggered notification
     * @param lastCurrentUser Current user. In case of multitenancy, when user authentication is forced as result of a fired trigger (scheduled jobs, other timed event
     *        expirations), current user might be lost, thus there is a need to reestablish.
     */
    public void launch(JobTrigger jobTrigger, Object entityOrEvent, MeveoUser lastCurrentUser) {

        currentUserProvider.reestablishAuthentication(lastCurrentUser);

        try {
            log.info("launch jobTrigger:{}", jobTrigger);
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("event", entityOrEvent);

            jobExecutionService.executeJob(jobInstanceService.retrieveIfNotManaged(jobTrigger.getJobInstance()), params, null);

            log.debug("launch jobTrigger:{} launched", jobTrigger);
            if (jobTrigger.isSaveSuccessfulNotifications()) {
                notificationHistoryService.create(jobTrigger, entityOrEvent, "", NotificationHistoryStatusEnum.SENT);
            }
        } catch (Exception e) {
            try {
                notificationHistoryService.create(jobTrigger, entityOrEvent, e.getMessage(), NotificationHistoryStatusEnum.FAILED);
            } catch (BusinessException e2) {
                log.error("Failed to create notification history", e2);
            }
        }
    }
}
