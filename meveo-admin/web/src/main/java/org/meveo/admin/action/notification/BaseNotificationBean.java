package org.meveo.admin.action.notification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Entity;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.action.UpdateMapTypeFieldBean;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.NotifiableEntity;
import org.meveo.model.ObservableEntity;
import org.meveo.model.notification.Notification;
import org.meveo.model.notification.NotificationEventTypeEnum;

/**
 * @author Tyshan　Shi(tyshan@manaty.net)
 * @date Aug 11, 2016 11:02:44 AM
 **/
public abstract class BaseNotificationBean<T extends Notification>  extends UpdateMapTypeFieldBean<T>{

	private static final long serialVersionUID = 1L;
	
	public BaseNotificationBean(){
	}
	
	public BaseNotificationBean(Class<T> clazz){
		super(clazz);
	}

	/**
     * Autocomplete method for class filter field - search entity type classes with @ObservableEntity annotation
     * 
     * @param query A partial class name (including a package)
     * @return A list of classnames
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List<String> autocompleteClassNames(String query) {

        List<Class> classes = null;
        try {
            classes = ReflectionUtils.getClasses("org.meveo.model");
        } catch (Exception e) {
            log.error("Failed to get a list of classes for a model package", e);
            return null;
        }

        String queryLc = query.toLowerCase();
        List<String> classNames = new ArrayList<String>();
        for (Class clazz : classes) {
            if (((clazz.isAnnotationPresent(Entity.class) && clazz.isAnnotationPresent(ObservableEntity.class))||clazz.isAnnotationPresent(NotifiableEntity.class)) && clazz.getName().toLowerCase().contains(queryLc)) {
                classNames.add(clazz.getName());
            }
        }

        Collections.sort(classNames);
        return classNames;
    }
    /**
     * filter the event type of the notification by class
     */
    public List<NotificationEventTypeEnum> getNotificationEventTypeFilters(){
    	String clazzStr=getEntity().getClassNameFilter();
    	if(StringUtils.isBlank(clazzStr)){
    		clazzStr=(String)filters.get("clazzNameFilter");
    	}
    	return NotificationEventTypeEnum.getEventTypesByClazz(clazzStr);
    }
}

