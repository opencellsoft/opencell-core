package org.meveo.admin.action.notification;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.CsvBuilder;
import org.meveo.commons.utils.CsvReader;
import org.meveo.model.notification.Notification;
import org.meveo.model.notification.NotificationEventTypeEnum;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.notification.NotificationService;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

@Named
@ConversationScoped
public class NotificationBean extends BaseBean<Notification>{


	private static final long serialVersionUID = 6473465285480945644L;

	@Inject
	NotificationService notificationService;
	
	CsvReader csvReader = null;
    private UploadedFile file; 
    
    private static final int CODE= 0;
    private static final int CLASS_NAME_FILTER= 1;
    private static final int EL_FILTER= 2;
    private static final int ACTIVE= 3; 
    private static final int EL_ACTION= 4; 
    private static final int EVENT_TYPE_FILTER= 5;
    

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
		for (Notification notification : notificationService.list()) {
			csv.appendValue(notification.getCode());
			csv.appendValue(notification.getClassNameFilter());
			csv.appendValue(notification.getElFilter());
			csv.appendValue(notification.isDisabled() + "");
			csv.appendValue(notification.getElAction());
			csv.appendValue(notification.getEventTypeFilter() + "");
			csv.startNewLine();
		}
		InputStream inputStream = new ByteArrayInputStream(csv.toString()
				.getBytes());
		csv.download(inputStream, "Notifications.csv");
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
	

	private void upload() throws IOException, BusinessException {
		if (file != null) {

			csvReader = new CsvReader(file.getInputstream(), ';',
					Charset.forName("ISO-8859-1"));
			csvReader.readHeaders();

			Notification notification = null;
			while (csvReader.readRecord()) {
				String[] values = csvReader.getValues();
				notification = new Notification();
				notification.setCode(values[CODE]);
				notification.setClassNameFilter(values[CLASS_NAME_FILTER]);
				notification.setElFilter(values[EL_FILTER]);
				notification.setDisabled(Boolean.parseBoolean(values[ACTIVE]));
				notification.setElAction(values[EL_ACTION]);
				notification.setEventTypeFilter(NotificationEventTypeEnum
						.valueOf(values[EVENT_TYPE_FILTER]));
				notificationService.create(notification);
				messages.info(new BundleKey("messages", "commons.csv"));
			}

		}
	}
}
