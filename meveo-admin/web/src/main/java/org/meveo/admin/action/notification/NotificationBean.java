package org.meveo.admin.action.notification;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.util.IOUtils;
import org.meveo.admin.action.BaseBean;
import org.meveo.commons.utils.CsvBuilder;
import org.meveo.model.notification.Notification;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.notification.NotificationService;

@Named
@ConversationScoped
public class NotificationBean extends BaseBean<Notification>{


	private static final long serialVersionUID = 6473465285480945644L;

	@Inject
	NotificationService notificationService;
	

	public NotificationBean(){
		super(Notification.class);
	}
	
	@Override
	protected IPersistenceService<Notification> getPersistenceService() {
		return notificationService;
	}

	protected String getDefaultViewName() {
		return "notifications";
	}

	@Override
	protected String getListViewName() {
		return "notifications";
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
        csv.appendValue("El filter");
        csv.appendValue("Active");
        csv.appendValue("El action"); 
        csv.appendValue("Event type filter"); 
        csv.startNewLine();
        for( Notification  notification:notificationService.list()){ 
        	 csv.appendValue(notification.getCode());
        	 csv.appendValue(notification.getClassNameFilter());
        	 csv.appendValue(notification.getElFilter());
        	 csv.appendValue(notification.isDisabled()+"");
        	 csv.appendValue(notification.getElAction());
        	 csv.appendValue(notification.getEventTypeFilter()+""); 
        	 csv.startNewLine();
        }
        InputStream inputStream=new ByteArrayInputStream(csv.toString().getBytes());
        csv.download(inputStream, "Notifications.csv");
    }
    
   
}
