package org.meveo.admin.action.notification;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.commons.utils.CsvBuilder;
import org.meveo.model.notification.EmailNotification;
import org.meveo.model.notification.InstantMessagingNotification;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.notification.InstantMessagingNotificationService;

@Named
@ConversationScoped
public class InstantMessagingNotificationBean extends BaseBean<InstantMessagingNotification>{


	private static final long serialVersionUID = 6473465285480945644L;

	@Inject
	InstantMessagingNotificationService imNotificationService;
	

	public InstantMessagingNotificationBean(){
		super(InstantMessagingNotification.class);
	}
	
	@Override
	protected IPersistenceService<InstantMessagingNotification> getPersistenceService() {
		return imNotificationService;
	}

	protected String getDefaultViewName() {
		return "instantMessagingNotifications";
	}

	@Override
	protected String getListViewName() {
		return "instantMessagingNotifications";
	}

	@Override
	protected List<String> getFormFieldsToFetch() {
		return Arrays.asList("provider");
	}

	@Override
	protected List<String> getListFieldsToFetch() {
		return Arrays.asList("provider");
	}
		
public void exportToFile() throws Exception {
		
        CsvBuilder csv = new CsvBuilder();
        csv.appendValue("Code"); 
        csv.appendValue("Classename filter"); 
        csv.appendValue("Event type filter"); 
        csv.appendValue("El filter");
        csv.appendValue("El action");
        csv.appendValue("Active"); 
        csv.appendValue("IM provider");
        csv.appendValue("IM identifier EL");
        csv.appendValue("IM identifiers list");
        csv.appendValue("Users list");
        csv.appendValue("Message");
        csv.startNewLine();
        for(InstantMessagingNotification  imNotification:imNotificationService.list()){ 
        	 csv.appendValue(imNotification.getCode());
        	 csv.appendValue(imNotification.getClassNameFilter());
        	 csv.appendValue(imNotification.getEventTypeFilter()+"");
        	 csv.appendValue(imNotification.getElFilter());
        	 csv.appendValue(imNotification.getElAction());
        	 csv.appendValue(imNotification.isDisabled()+"");
        	 csv.appendValue(imNotification.getImProvider()+"");
        	 csv.appendValue(imNotification.getIdEl());
        	 csv.appendValue(imNotification.getIds()+"");
        	 csv.appendValue(imNotification.getUsers()+"");
        	 csv.appendValue(imNotification.getMessage());
        	 csv.startNewLine();
        }
        InputStream inputStream=new ByteArrayInputStream(csv.toString().getBytes());
        csv.download(inputStream, "InstancemNotifications.csv");
    }
}
