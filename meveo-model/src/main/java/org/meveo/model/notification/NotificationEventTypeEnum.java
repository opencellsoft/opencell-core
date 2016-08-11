package org.meveo.model.notification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Entity;

import org.meveo.model.NotifiableEntity;
import org.meveo.model.ObservableEntity;
import org.meveo.model.billing.CounterPeriod;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.mediation.Access;
import org.meveo.model.mediation.MeveoFtpFile;
import org.meveo.model.rating.EDR;

public enum NotificationEventTypeEnum {
CREATED (1,"enum.notificationEventTypeEnum.CREATED"),
UPDATED(2,"enum.notificationEventTypeEnum.UPDATED"),
REMOVED(3,"enum.notificationEventTypeEnum.REMOVED"),
TERMINATED(4,"enum.notificationEventTypeEnum.TERMINATED"),
DISABLED(5,"enum.notificationEventTypeEnum.DISABLED"),
PROCESSED(6,"enum.notificationEventTypeEnum.PROCESSED"),
REJECTED(7,"enum.notificationEventTypeEnum.REJECTED"),
REJECTED_CDR(8,"enum.notificationEventTypeEnum.REJECTED_CDR"),
LOGGED_IN(9,"enum.notificationEventTypeEnum.LOGGED_IN"),
INBOUND_REQ(10,"enum.notificationEventTypeEnum.INBOUND_REQ"),
ENABLED(11,"enum.notificationEventTypeEnum.ENABLED"),
LOW_BALANCE(12,"enum.notificationEventTypeEnum.LOW_BALANCE"),
FILE_UPLOAD(13,"enum.notificationEventTypeEnum.FILE_UPLOAD"),
FILE_DOWNLOAD(14,"enum.notificationEventTypeEnum.FILE_DOWNLOAD"),
FILE_RENAME(15,"enum.notificationEventTypeEnum.FILE_RENAME"),
FILE_DELETE(16,"enum.notificationEventTypeEnum.FILE_DELETE"),
COUNTER_UPDATED(17,"enum.notificationEventTypeEnum.COUNTER_UPDATED");

private Integer id;
private String label;

NotificationEventTypeEnum(Integer id, String label) {
    this.id = id;
    this.label = label;
}

public Integer getId() {
    return id;
}

public String getLabel() {
    return this.label;
}


public static NotificationEventTypeEnum getValue(Integer id) {
    if (id != null) {
        for (NotificationEventTypeEnum type : values()) {
            if (id.equals(type.getId())) {
                return type;
            }
        }
    }
    return null;
}

	public static List<NotificationEventTypeEnum> getEventTypesByClazz(String clazzStr){
		Class<?> clazz=null;
    	try{
    		clazz=Class.forName(clazzStr);
    	}catch(Exception e){return null;}
    	
		List<NotificationEventTypeEnum> events=new ArrayList<NotificationEventTypeEnum>();
		if(hasObservableEntity(clazz)){
    		events.addAll(Arrays.asList(NotificationEventTypeEnum.CREATED,NotificationEventTypeEnum.UPDATED,NotificationEventTypeEnum.REMOVED,
    				NotificationEventTypeEnum.DISABLED,NotificationEventTypeEnum.ENABLED));
    		if(clazz.equals(WalletInstance.class)){
    			events.add(NotificationEventTypeEnum.LOW_BALANCE);
    		}else if(clazz.equals(org.meveo.model.admin.User.class)){
    			events.add(NotificationEventTypeEnum.LOGGED_IN);
    		}else if(clazz.equals(InboundRequest.class)){
    			events.add(NotificationEventTypeEnum.INBOUND_REQ);
    		}else if(clazz.equals(WalletOperation.class)||clazz.equals(EDR.class)||clazz.equals(RecurringChargeTemplate.class)){
    			events.add(NotificationEventTypeEnum.REJECTED);
    		}else if(clazz.equals(Access.class)){
    			events.add(NotificationEventTypeEnum.REJECTED_CDR);
    		}else if(clazz.equals(CounterPeriod.class)){
    			events.add(NotificationEventTypeEnum.COUNTER_UPDATED);
    		}
    	}else if(hasNotificableEntity(clazz)){
    		if(clazz.equals(MeveoFtpFile.class)){
    			events=Arrays.asList(NotificationEventTypeEnum.FILE_UPLOAD,NotificationEventTypeEnum.FILE_DOWNLOAD,NotificationEventTypeEnum.FILE_DELETE,NotificationEventTypeEnum.FILE_RENAME);
    		}
    	}
		return events;
	}
	
	private static boolean hasObservableEntity(Class<?> clazz){
    	return clazz.isAnnotationPresent(Entity.class)&&clazz.isAnnotationPresent(ObservableEntity.class);
    }
	private static boolean hasNotificableEntity(Class<?> clazz){
    	return clazz.isAnnotationPresent(NotifiableEntity.class);
    }
}
