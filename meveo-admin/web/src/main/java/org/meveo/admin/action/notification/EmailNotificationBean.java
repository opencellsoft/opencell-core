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
import org.meveo.model.notification.WebHook;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.notification.EmailNotificationService;

@Named
@ConversationScoped
public class EmailNotificationBean extends BaseBean<EmailNotification> {

	private static final long serialVersionUID = 6473465285480945644L;

	@Inject
	private EmailNotificationService emailNotificationService;

	public EmailNotificationBean() {
		super(EmailNotification.class);
	}

	@Override
	protected IPersistenceService<EmailNotification> getPersistenceService() {
		return emailNotificationService;
	}

	protected String getDefaultViewName() {
		return "emailNotifications";
	}

	@Override
	protected String getListViewName() {
		return "emailNotifications";
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
        csv.appendValue("Active"); 
        csv.appendValue("El action");
        csv.appendValue("Sent from");
        csv.appendValue("Send to EL");
        csv.appendValue("Send to mailing list");
        csv.appendValue("Subject");
        csv.appendValue("Text body");
        csv.appendValue("HTML body");
        csv.appendValue("Attachments EL");
        csv.startNewLine();
        for(EmailNotification  emailNotification:emailNotificationService.list()){ 
        	 csv.appendValue(emailNotification.getCode());
        	 csv.appendValue(emailNotification.getClassNameFilter());
        	 csv.appendValue(emailNotification.getEventTypeFilter()+"");
        	 csv.appendValue(emailNotification.getElFilter());
        	 csv.appendValue(emailNotification.isDisabled()+"");
        	 csv.appendValue(emailNotification.getElAction());
        	 csv.appendValue(emailNotification.getEmailFrom());
        	 csv.appendValue(emailNotification.getEmailToEl());
        	 csv.appendValue(emailNotification.getEmails()+"");
        	 csv.appendValue(emailNotification.getSubject());
        	 csv.appendValue(emailNotification.getBody());
        	 csv.appendValue(emailNotification.getHtmlBody());
        	 csv.appendValue(emailNotification.getAttachmentExpressions()+"");
        	 csv.startNewLine();
        }
        InputStream inputStream=new ByteArrayInputStream(csv.toString().getBytes());
        csv.download(inputStream, "EmailNotifications.csv");
    }

}
