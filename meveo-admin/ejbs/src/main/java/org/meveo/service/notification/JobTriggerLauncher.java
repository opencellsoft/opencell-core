package org.meveo.service.notification;

import java.util.HashMap;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.IEntity;
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
	

	public void launch(JobTrigger jobTrigger, IEntity e){
		try {
			log.debug("launch jobTrigger:{}",jobTrigger);
			HashMap<Object,Object> userMap = new HashMap<Object, Object>();
			userMap.put("event", e);					
			jobInstanceService.triggerExecution(jobTrigger.getJobInstance().getCode(),jobTrigger.getJobParams(),jobTrigger.getAuditable().getCreator());
			log.debug("launch jobTrigger:{} launched",jobTrigger);
			notificationHistoryService.create(jobTrigger, e, "", NotificationHistoryStatusEnum.SENT);
			log.debug("launch jobTrigger:{} notificationHistory created",jobTrigger);

		} catch (BusinessException e1) {
			try {
				notificationHistoryService.create(jobTrigger, e, e1.getMessage(), NotificationHistoryStatusEnum.FAILED);
			} catch (BusinessException e2) {
				log.error("Failed to create notification history business",e);
			}
			
		} catch (Exception e1) {
			try {
				notificationHistoryService.create(jobTrigger, e, e1.getMessage(), NotificationHistoryStatusEnum.FAILED);
			} catch (BusinessException e2) {
				log.error("Failed to create notification history address",e);
			}
		} 
	}
}
