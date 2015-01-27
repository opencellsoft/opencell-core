package org.meveo.service.notification;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.event.qualifier.CDR;
import org.meveo.event.qualifier.Created;
import org.meveo.event.qualifier.Disabled;
import org.meveo.event.qualifier.InboundRequestReceived;
import org.meveo.event.qualifier.LoggedIn;
import org.meveo.event.qualifier.Processed;
import org.meveo.event.qualifier.Rejected;
import org.meveo.event.qualifier.Removed;
import org.meveo.event.qualifier.Terminated;
import org.meveo.event.qualifier.Updated;
import org.meveo.model.BusinessEntity;
import org.meveo.model.IEntity;
import org.meveo.model.admin.User;
import org.meveo.model.notification.EmailNotification;
import org.meveo.model.notification.InboundRequest;
import org.meveo.model.notification.InstantMessagingNotification;
import org.meveo.model.notification.Notification;
import org.meveo.model.notification.NotificationEventTypeEnum;
import org.meveo.model.notification.NotificationHistory;
import org.meveo.model.notification.NotificationHistoryStatusEnum;
import org.meveo.model.notification.WebHook;
import org.meveo.service.billing.impl.RatingService;
import org.slf4j.Logger;

@Singleton
@Startup
public class DefaultObserver {

	@Inject
	Logger log;
	
	@Inject 
	BeanManager manager;
	
	@Inject
	NotificationService notificationService;

	@Inject 
	NotificationHistoryService notificationHistoryService;
	
	@Inject
	EmailNotifier emailNotifier;

	@Inject
	WebHookNotifier webHookNotifier;
	
	@Inject
	InstantMessagingNotifier imNotifier;
	
	HashMap<NotificationEventTypeEnum,HashMap<Class<BusinessEntity>,List<Notification>>> classNotificationMap=new HashMap<>();
	
	
	@PostConstruct
	private void init(){
		for(NotificationEventTypeEnum type:NotificationEventTypeEnum.values()){
			classNotificationMap.put(type,new HashMap<Class<BusinessEntity>,List<Notification>>());
		}
		List<Notification> allNotif = notificationService.listAll();
		log.debug("Found {} notifications to map",allNotif.size());
		for(Notification notif:allNotif){
			addNotificationToCache(notif);
		}
	}
	
	private void addNotificationToCache(Notification notif){
		try {
			@SuppressWarnings("unchecked")
			Class<BusinessEntity> c = (Class<BusinessEntity>) Class.forName(notif.getClassNameFilter());
			if(!classNotificationMap.get(notif.getEventTypeFilter()).containsKey(c)){
				classNotificationMap.get(notif.getEventTypeFilter()).put(c, new ArrayList<Notification>());
			}
			log.debug("Add notification {} to class map {}",notif,c);
			classNotificationMap.get(notif.getEventTypeFilter()).get(c).add(notif);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void removeNotificationFromCache(Notification notif){
		for(@SuppressWarnings("unused") NotificationEventTypeEnum type:NotificationEventTypeEnum.values()){
			for(Class<BusinessEntity> c:classNotificationMap.get(notif.getEventTypeFilter()).keySet()){
				if(classNotificationMap.get(notif.getEventTypeFilter()).get(c).contains(notif)){
					classNotificationMap.get(notif.getEventTypeFilter()).get(c).remove(notif);
					log.debug("remove notification {} from class map {}",notif,c);
				}
			}
		}
	}
	
	private void updateNotificationInCache(Notification notif){
		removeNotificationFromCache(notif);
		addNotificationToCache(notif);
	}
	
	private boolean matchExpression(String expression,Object o) throws BusinessException {
		Boolean result=true;
		if (StringUtils.isBlank(expression)) {
			return result;
		}
		Map<Object, Object> userMap = new HashMap<Object, Object>();
		userMap.put("event", o);
		
		Object res = RatingService.evaluateExpression(expression, userMap, Boolean.class);
		try{
			result=(Boolean) res;
		} catch(Exception e){
			throw new BusinessException("Expression "+expression+" do not evaluate to boolean but "+res);
		}
		return result;
	}
	
	private String executeAction(String expression,Object o) throws BusinessException{
		log.debug("execute notification action:{}",expression);
		if (StringUtils.isBlank(expression)) {
			return "";
		}
		Map<Object, Object> userMap = new HashMap<Object, Object>();
		userMap.put("event", o);
		userMap.put("manager", manager);
		return (String)RatingService.evaluateExpression(expression, userMap, String.class);	
	}

	private void fireNotification(Notification notif, IEntity e) {
		log.debug("Fire Notification for notif {} and  enity {}",notif, e);
		try {
			if(matchExpression(notif.getElFilter(),e)){
				
				//we first perform the EL actions 
				executeAction(notif.getElAction(),e);
				
				//then the notification itself
				if(notif instanceof EmailNotification){
					emailNotifier.sendEmail((EmailNotification) notif, e);
				} else if(notif instanceof WebHook){
					webHookNotifier.sendRequest((WebHook) notif, e);
				} else if(notif instanceof InstantMessagingNotification){
					imNotifier.sendInstantMessage((InstantMessagingNotification) notif, e);
				}  if(notif.getEventTypeFilter()==NotificationEventTypeEnum.INBOUND_REQ){
					NotificationHistory histo=notificationHistoryService.create(notif, e, "", NotificationHistoryStatusEnum.SENT);
					((InboundRequest)e).add(histo);
				}
			}
		} catch (BusinessException e1) {
			log.error("Error while firing notification {} for provider {}: {} "
					,notif.getCode(),notif.getProvider().getCode(),e1.getMessage());
			try {
				NotificationHistory notificationHistory = notificationHistoryService.create(notif, e, e1.getMessage(), NotificationHistoryStatusEnum.FAILED);
				if(e instanceof InboundRequest){
					((InboundRequest)e).add(notificationHistory);
				}
			} catch (BusinessException e2) {
				e2.printStackTrace();
			}
			
		}
	}

	private void fireCdrNotification(Notification notif, Serializable cdr) {
		log.debug("Fire Cdr Notification for notif {} and  cdr {}",notif, cdr);
		try {
			if(!StringUtils.isBlank(notif.getElAction()) && matchExpression(notif.getElFilter(),cdr)){
				executeAction(notif.getElAction(),cdr);
			}
		} catch (BusinessException e1) {
			log.error("Error while firing notification {} for provider {}: {} "
					,notif.getCode(),notif.getProvider().getCode(),e1.getMessage());
		}
		
	}
	
	private void checkEvent(NotificationEventTypeEnum type,IEntity e){
		for(Class<BusinessEntity> c:classNotificationMap.get(type).keySet()){
			log.debug("try class {} ",c);
			if( c.isAssignableFrom(e.getClass()) ){
				log.debug("{} is assignable from {}",c,e.getClass());
				for(Notification notif:classNotificationMap.get(type).get(c)){
					fireNotification(notif,e);
				}
			}
		}
	}
	
	public void entityCreated(@Observes @Created IEntity e){
		log.debug("Defaut observer : Entity {} with id {} created",e.getClass().getName(),e.getId());
		checkEvent(NotificationEventTypeEnum.CREATED, e);
		if(Notification.class.isAssignableFrom(e.getClass())){
			addNotificationToCache((Notification) e);
		}
	}



	public void entityUpdated(@Observes @Updated IEntity e){
		log.debug("Defaut observer : Entity {} with id {} updated",e.getClass().getName(),e.getId());
		checkEvent(NotificationEventTypeEnum.UPDATED, e);
		if(Notification.class.isAssignableFrom(e.getClass())){
			updateNotificationInCache((Notification) e);
		}
	}

	public void entityRemoved(@Observes @Removed IEntity e){
		log.debug("Defaut observer : Entity {} with id {} removed",e.getClass().getName(),e.getId());
		checkEvent(NotificationEventTypeEnum.REMOVED, e);
		if(Notification.class.isAssignableFrom(e.getClass())){
			removeNotificationFromCache((Notification) e);
		}
	}

	public void entityDisabled(@Observes @Disabled IEntity e){
		log.debug("Defaut observer : Entity {} with id {} disabled",e.getClass().getName(),e.getId());
		checkEvent(NotificationEventTypeEnum.DISABLED, e);
	}

	public void entityTerminated(@Observes @Terminated IEntity e){
		log.debug("Defaut observer : Entity {} with id {} terminated",e.getClass().getName(),e.getId());
		checkEvent(NotificationEventTypeEnum.TERMINATED, e);
	}
	
	public void entityProcessed(@Observes @Processed IEntity e){
		log.debug("Defaut observer : Entity {} with id {} processed",e.getClass().getName(),e.getId());
		checkEvent(NotificationEventTypeEnum.PROCESSED, e);
	}
	
	public void entityRejected(@Observes @Rejected IEntity e){
		log.debug("Defaut observer : Entity {} with id {} rejected",e.getClass().getName(),e.getId());
		checkEvent(NotificationEventTypeEnum.REJECTED, e);
	}
	
	public void cdrRejected(@Observes @Rejected @CDR Serializable cdr){
		log.debug("Defaut observer : cdr {} rejected",cdr);
		for(Class<BusinessEntity> c:classNotificationMap.get(NotificationEventTypeEnum.REJECTED_CDR).keySet()){
			log.debug("try class {} ",c);
			if( c.isAssignableFrom(cdr.getClass()) ){
				log.debug("{} is assignable from {}",c,cdr.getClass());
				for(Notification notif:classNotificationMap.get(NotificationEventTypeEnum.REJECTED_CDR).get(c)){
					fireCdrNotification(notif,cdr);
				}
			}
		}
	}

	public void loggedIn(@Observes @LoggedIn User e){
		log.debug("Defaut observer : logged in class={} ",e.getClass().getName());
		checkEvent(NotificationEventTypeEnum.LOGGED_IN, e);
	}


	public void inboundRequest(@Observes @InboundRequestReceived InboundRequest e){
		log.debug("Defaut observer : inbound request {} ",e.getCode());
		checkEvent(NotificationEventTypeEnum.INBOUND_REQ, e);	
	}
	
}
