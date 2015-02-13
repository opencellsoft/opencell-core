package org.meveo.admin.action.notification;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.CsvBuilder;
import org.meveo.commons.utils.CsvReader;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.notification.InstantMessagingNotification;
import org.meveo.model.notification.InstantMessagingProviderEnum;
import org.meveo.model.notification.NotificationEventTypeEnum;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.CounterTemplateService;
import org.meveo.service.notification.InstantMessagingNotificationService;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

@Named
@ConversationScoped
public class InstantMessagingNotificationBean extends BaseBean<InstantMessagingNotification>{


	private static final long serialVersionUID = 6473465285480945644L;

	@Inject
	InstantMessagingNotificationService imNotificationService;
	
	@Inject
	UserService userService;

    @Inject
    CounterTemplateService counterTemplateService;
    
	CsvReader csvReader = null;
    private UploadedFile file; 
    
    private static final int CODE= 0;
    private static final int CLASS_NAME_FILTER= 1;
    private static final int EVENT_TYPE_FILTER= 2; 
    private static final int EL_FILTER= 3;
    private static final int EL_ACTION= 5;
    private static final int ACTIVE= 4; 
    private static final int IM_PROVIDER= 6;
    private static final int IM_IDENTIFIER_EL= 7;
    private static final int IM_IDENTIFIER_LIST= 8;
    private static final int USERS_LIST= 9;
    private static final int MESSAGE= 10;
    private static final int COUNTER_TEMPLATE= 11;


	

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
		csv.appendValue("Counter template");
		csv.startNewLine();
		for (InstantMessagingNotification imNotification : imNotificationService
				.list()) {
			csv.appendValue(imNotification.getCode());
			csv.appendValue(imNotification.getClassNameFilter());
			csv.appendValue(imNotification.getEventTypeFilter() + "");
			csv.appendValue(imNotification.getElFilter());
			csv.appendValue(imNotification.getElAction());
			csv.appendValue(imNotification.isDisabled() + "");
			csv.appendValue(imNotification.getImProvider() + "");
			csv.appendValue(imNotification.getIdEl());

			String sep = "";
			StringBuffer ids = new StringBuffer();
			for (String id : imNotification.getIds()) {
				ids.append(sep).append(id);
				sep = ",";
			}

			csv.appendValue(ids.toString());

			String sepUser = "";
			StringBuffer users = new StringBuffer();
			for (User user : imNotification.getUsers()) {
				users.append(sepUser).append(user.getId());
				sepUser = ",";
			}
			csv.appendValue(users.toString());
			csv.appendValue(imNotification.getMessage());
			csv.appendValue(imNotification.getCounterTemplate()!=null?  imNotification.getCounterTemplate().getCode(): null);
			csv.startNewLine();
		}
		InputStream inputStream = new ByteArrayInputStream(csv.toString()
				.getBytes());
		csv.download(inputStream, "InstantMessagingNotification.csv");
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
			InstantMessagingNotification inMessagingNotification = null;
			while (csvReader.readRecord()) {
				String[] values = csvReader.getValues();
				inMessagingNotification = new InstantMessagingNotification();
				inMessagingNotification.setCode(values[CODE]);
				inMessagingNotification
						.setClassNameFilter(values[CLASS_NAME_FILTER]);
				inMessagingNotification
						.setEventTypeFilter(NotificationEventTypeEnum
								.valueOf(values[EVENT_TYPE_FILTER]));
				inMessagingNotification.setElFilter(values[EL_FILTER]);
				inMessagingNotification.setElAction(values[EL_ACTION]);
				inMessagingNotification.setDisabled(Boolean
						.parseBoolean(values[ACTIVE]));
				inMessagingNotification
						.setImProvider(InstantMessagingProviderEnum
								.valueOf(values[IM_PROVIDER]));
				inMessagingNotification.setIdEl(values[IM_IDENTIFIER_EL]);
				String identifiers = values[IM_IDENTIFIER_LIST];
				if (!StringUtils.isBlank(identifiers)) {
					String[] ids = identifiers.split(",");
					List<String> idList = Arrays.asList(ids);
					for (String id : idList) {
						if (inMessagingNotification.getIds() == null) {
							inMessagingNotification
									.setIds(new HashSet<String>());
						}
						inMessagingNotification.getIds().add(id);
					}
				}
				String users = values[USERS_LIST];
				if (!StringUtils.isBlank(users)) {

					String[] userIds = users.split(",");
					List<String> userIdList = Arrays.asList(userIds);
					User user = null;
					for (String id : userIdList) {
						user = userService.findById(Long.valueOf(id));
						if (user != null) {
							if (inMessagingNotification.getUsers() == null) {
								inMessagingNotification
										.setUsers(new HashSet<User>());
							}
							inMessagingNotification.getUsers().add(user);
						}
					}
				}
				inMessagingNotification.setMessage(values[MESSAGE]);
				if(!StringUtils.isBlank(values[COUNTER_TEMPLATE])){
					CounterTemplate counterTemplate=counterTemplateService.findByCode(values[COUNTER_TEMPLATE], getCurrentProvider());
					inMessagingNotification.setCounterTemplate(counterTemplate!=null ?counterTemplate: null);
				}
				imNotificationService.create(inMessagingNotification);
				messages.info(new BundleKey("messages", "commons.csv"));
			}
		}
	}
}




