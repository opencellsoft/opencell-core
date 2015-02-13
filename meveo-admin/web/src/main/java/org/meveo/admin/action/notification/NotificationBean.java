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
import org.meveo.admin.exception.RejectedImportException;
import org.meveo.commons.utils.CsvBuilder;
import org.meveo.commons.utils.CsvReader;
import org.meveo.model.notification.Notification;
import org.meveo.model.notification.NotificationEventTypeEnum;
import org.meveo.model.notification.StrategyImportTypeEnum;
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
    
    private StrategyImportTypeEnum strategyImportType;
    
    
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

	public void upload() throws IOException, BusinessException {
		if (file != null) {
			csvReader = new CsvReader(file.getInputstream(), ';',
					Charset.forName("ISO-8859-1"));
			csvReader.readHeaders();
			try {
				while (csvReader.readRecord()) {
					String[] values = csvReader.getValues();
					Notification existingEntity = notificationService
							.findByCode(values[CODE], getCurrentProvider());
					if (existingEntity != null) {
						checkSelectedStrategy(values, existingEntity);
					} else {
						Notification notif = new Notification();
						notif.setClassNameFilter(values[CLASS_NAME_FILTER]);
						notif.setElFilter(values[EL_FILTER]);
						notif.setDisabled(Boolean.parseBoolean(values[ACTIVE]));
						notif.setElAction(values[EL_ACTION]);
						notif.setEventTypeFilter(NotificationEventTypeEnum
								.valueOf(values[EVENT_TYPE_FILTER]));
						notificationService.create(notif);
					}
				}
				messages.info(new BundleKey("messages", "commons.csv"));
			} catch (RejectedImportException e) {
				messages.error(new BundleKey("messages", e.getMessage()));
			}}}

	public void checkSelectedStrategy(String[] values,
			Notification existingEntity) throws RejectedImportException {
		if (strategyImportType.equals(StrategyImportTypeEnum.UPDATED)) {
			existingEntity.setClassNameFilter(values[CLASS_NAME_FILTER]);
			existingEntity.setElFilter(values[EL_FILTER]);
			existingEntity.setDisabled(Boolean.parseBoolean(values[ACTIVE]));
			existingEntity.setElAction(values[EL_ACTION]);
			existingEntity.setEventTypeFilter(NotificationEventTypeEnum
					.valueOf(values[EVENT_TYPE_FILTER]));
			notificationService.update(existingEntity);
		} else if (strategyImportType
				.equals(StrategyImportTypeEnum.REJECT_EXISTING_RECORDS)) {
			// add to a new csv
		} else if (strategyImportType
				.equals(StrategyImportTypeEnum.REJECTE_IMPORT)) {
			throw new RejectedImportException("notification.rejectImport");
		}
	}
	public StrategyImportTypeEnum getStrategyImportType() {
		return strategyImportType;
	}

	public void setStrategyImportType(StrategyImportTypeEnum strategyImportType) {
		this.strategyImportType = strategyImportType;
	}




	
	
	
}
