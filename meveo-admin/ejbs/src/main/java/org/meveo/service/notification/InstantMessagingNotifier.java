package org.meveo.service.notification;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.Session;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.IEntity;
import org.meveo.model.notification.InstantMessagingNotification;
import org.meveo.model.notification.NotificationHistoryStatusEnum;
import org.meveo.service.base.ValueExpressionWrapper;
import org.slf4j.Logger;

import com.skype.Skype;

//TODO : transform that into MDB to correctly handle retries
@Stateless
public class InstantMessagingNotifier {

	@Inject
	Logger log;
	
	@Resource(lookup = "java:/MeveoMail")
	private Session mailSession;

	@Inject 
	NotificationHistoryService notificationHistoryService;
	
	//Jabber jabber = new Jabber();
	
	@Asynchronous
	public void sendInstantMessage(InstantMessagingNotification notification, IEntity e){
		try {
			HashMap<Object,Object> userMap = new HashMap<Object, Object>();
			userMap.put("event", e);
			Set<String> imIdSet = notification.getIds();
			if(imIdSet==null){
				imIdSet = new HashSet<String>();
			}
			if(!StringUtils.isBlank(notification.getIdEl())){
				imIdSet.add((String)ValueExpressionWrapper.evaluateExpression(notification.getIdEl(), userMap, String.class));
			}
			String message = (String)ValueExpressionWrapper.evaluateExpression(notification.getMessage(), userMap, String.class);
			
			switch(notification.getImProvider()){
			case SKYPE:
				for(String imId:imIdSet){
					log.debug("send skype message to {}, mess={}",imId,message);
					Skype.chat(imId).send(message);
				}
				break;
			case FACEBOOK:
				break;
			case GTALK:
				
				break;
			case TWITTER:
				break;
			case YAHOO_MESSENGER:
				break;
			}
			notificationHistoryService.create(notification, e, "", NotificationHistoryStatusEnum.SENT);

		} catch (BusinessException e1) {
			try {
				notificationHistoryService.create(notification, e, e1.getMessage(), NotificationHistoryStatusEnum.FAILED);
			} catch (BusinessException e2) {
				log.error("Failed to create notification history business",e);
			}
			
		} catch (Exception e1) {
			try {
				notificationHistoryService.create(notification, e, e1.getMessage(), NotificationHistoryStatusEnum.FAILED);
			} catch (BusinessException e2) {
				log.error("Failed to create notification history exception",e);
			}
		} 
	}
}
