package org.meveo.admin.action.notification;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.CsvBuilder;
import org.meveo.commons.utils.CsvReader;
import org.meveo.model.notification.EmailNotification;
import org.meveo.model.notification.NotificationEventTypeEnum;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.notification.EmailNotificationService;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

@Named
@ConversationScoped
public class EmailNotificationBean extends BaseBean<EmailNotification> {

	private static final long serialVersionUID = 6473465285480945644L;

	@Inject
	private EmailNotificationService emailNotificationService;
	
	CsvReader csvReader = null;
    private UploadedFile file; 
    
    private static final int CODE= 0;
    private static final int CLASS_NAME_FILTER= 1;
    private static final int EVENT_TYPE_FILTER= 2; 
    private static final int EL_FILTER= 3;
    private static final int ACTIVE= 4; 
    private static final int EL_ACTION= 5;
    private static final int SENT_FROM= 6;
    private static final int SEND_TO_EL= 7;
    private static final int SEND_TO_MAILING_LIST= 8;
    private static final int SUBJECT= 9;
    private static final int TEXT_BODY= 10;
    private static final int HTML_BODY= 11;

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
	
	public void handleFileUpload(FileUploadEvent event) throws Exception {
		try {
			file = event.getFile();
		    log.info("handleFileUpload " + file);
		    upload();
		} catch (BusinessException e) {
			log.error(e.getMessage(),e);
			messages.error(e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage(),e);
			messages.error(e.getMessage());
		}
	    
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

	
	public void upload() throws IOException, BusinessException {
		if (file != null) {

			csvReader = new CsvReader(file.getInputstream(), ';',
					Charset.forName("ISO-8859-1"));
			csvReader.readHeaders();
			EmailNotification emailNotification = null;
			while (csvReader.readRecord()) {
				String[] values = csvReader.getValues();
				emailNotification = new EmailNotification();
				emailNotification.setCode(values[CODE]);
				emailNotification.setClassNameFilter(values[CLASS_NAME_FILTER]);
				emailNotification.setEventTypeFilter(NotificationEventTypeEnum
						.valueOf(values[EVENT_TYPE_FILTER]));
				emailNotification.setElFilter(values[EL_FILTER]);
				emailNotification.setDisabled(Boolean
						.parseBoolean(values[ACTIVE]));
				emailNotification.setElAction(values[EL_ACTION]);
				emailNotification.setEmailFrom(values[SENT_FROM]);
				emailNotification.setEmailToEl(values[SEND_TO_EL]);
				emailNotification.setEmailToEl(values[SEND_TO_MAILING_LIST]);
				emailNotification.setSubject(values[SUBJECT]);
				emailNotification.setBody(values[TEXT_BODY]);
				emailNotification.setElAction(values[HTML_BODY]);
				emailNotificationService.create(emailNotification);
				messages.info(new BundleKey("messages", "commons.csv"));
			}
		}
	}
}


