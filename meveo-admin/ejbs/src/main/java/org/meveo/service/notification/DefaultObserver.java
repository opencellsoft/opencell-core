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
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.event.qualifier.CDR;
import org.meveo.event.qualifier.Created;
import org.meveo.event.qualifier.Disabled;
import org.meveo.event.qualifier.Processed;
import org.meveo.event.qualifier.Rejected;
import org.meveo.event.qualifier.Removed;
import org.meveo.event.qualifier.Terminated;
import org.meveo.event.qualifier.Updated;
import org.meveo.model.IEntity;
import org.meveo.model.notification.EmailNotification;
import org.meveo.model.notification.Notification;
import org.meveo.model.notification.NotificationEventTypeEnum;
import org.meveo.model.notification.WebHook;
import org.meveo.service.billing.impl.RatingService;
import org.slf4j.Logger;

@Singleton
@Startup
public class DefaultObserver {

	@Inject
	Logger log;
	
	@Inject
	NotificationService notificationService;
	
	@Inject
	EmailNotifier emailNotifier;
	
	@Inject
	WebHookNotifier webHookNotifier;
	
	@SuppressWarnings("rawtypes")
	HashMap<NotificationEventTypeEnum,HashMap<Class,List<Notification>>> classNotificationMap=new HashMap<>();
	
	
	@PostConstruct
	private void init(){
		for(NotificationEventTypeEnum type:NotificationEventTypeEnum.values()){
			classNotificationMap.put(type,new HashMap<Class,List<Notification>>());
		}
		List<Notification> allNotif = notificationService.listAll();
		log.debug("Found {} notifications to map",allNotif.size());
		for(Notification notif:allNotif){
			addNotificationToCache(notif);
		}
	}
	
	private void addNotificationToCache(Notification notif){
		try {
			@SuppressWarnings("rawtypes")
			Class c = Class.forName(notif.getClassNameFilter());
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
		for(NotificationEventTypeEnum type:NotificationEventTypeEnum.values()){
			for(Class c:classNotificationMap.get(notif.getEventTypeFilter()).keySet()){
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
	
	private void executeAction(String expression,Object o){
		log.debug("execute notification action:{}",expression);
	}

	private void fireNotification(Notification notif, IEntity e) {
		log.debug("Fire Notification for notif {} and  enity {}",notif, e);
		try {
			if(matchExpression(notif.getElFilter(),e)){
				if(notif instanceof EmailNotification){
					emailNotifier.sendEmail((EmailNotification) notif, e);
				} else if(notif instanceof WebHook){
					webHookNotifier.sendRequest((WebHook) notif, e);
				} else {
					executeAction(notif.getElAction(),e);
				}
			}
		} catch (BusinessException e1) {
			log.error("Error while firing notification {} for provider {}: {} "
					,notif.getCode(),notif.getProvider().getCode(),e1.getMessage());
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
		for(Class c:classNotificationMap.get(type).keySet()){
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
		for(Class c:classNotificationMap.get(NotificationEventTypeEnum.REJECTED_CDR).keySet()){
			log.debug("try class {} ",c);
			if( c.isAssignableFrom(cdr.getClass()) ){
				log.debug("{} is assignable from {}",c,cdr.getClass());
				for(Notification notif:classNotificationMap.get(NotificationEventTypeEnum.REJECTED_CDR).get(c)){
					fireCdrNotification(notif,cdr);
				}
			}
		}
	}


}
