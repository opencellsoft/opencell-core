package org.meveo.admin.action.notification;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.CsvBuilder;
import org.meveo.commons.utils.CsvReader;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.notification.NotificationEventTypeEnum;
import org.meveo.model.notification.WebHook;
import org.meveo.model.notification.WebHookMethodEnum;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.CounterTemplateService;
import org.meveo.service.notification.WebHookService;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

@Named
@ConversationScoped
public class WebHookBean extends BaseBean<WebHook> {

    private static final long serialVersionUID = -5605274745661054861L;

    @Inject
    WebHookService webHookService;
    
    @Inject
    CounterTemplateService counterTemplateService;

    CsvReader csvReader = null;
    private UploadedFile file; 
    
    private static final int CODE= 0;
    private static final int CLASS_NAME_FILTER= 1;
    private static final int EVENT_TYPE_FILTER= 2;
    private static final int EL_FILTER=3;
    private static final int ACTIVE= 4; 
    private static final int EL_ACTION= 5; 
    private static final int HOST= 6; 
    private static final int PORT= 7; 
    private static final int PAGE= 8; 
    private static final int HTTP_METHOD= 9; 
    private static final int USERNAME= 10; 
    private static final int PASSWORD= 11;
    private static final int COUNTER_TEMPLATE= 11;
    
    
    

    
    
    public WebHookBean() {
        super(WebHook.class);
    }

    @Override
    protected IPersistenceService<WebHook> getPersistenceService() {
        return webHookService;
    }

    @Override
    public WebHook initEntity() {
        WebHook webhook = super.initEntity();

        extractMapTypeFieldFromEntity(webhook.getHeaders(), "headers");
        extractMapTypeFieldFromEntity(webhook.getParams(), "params");

        return webhook;
    }

    @Override
    public String saveOrUpdate(boolean killConversation) throws BusinessException {

        updateMapTypeFieldInEntity(entity.getHeaders(), "headers");
        updateMapTypeFieldInEntity(entity.getParams(), "params");

        return super.saveOrUpdate(killConversation);
    }
    
	public void exportToFile() throws Exception {

		CsvBuilder csv = new CsvBuilder();
		csv.appendValue("Code");
		csv.appendValue("Classename filter");
		csv.appendValue("Event type filter");
		csv.appendValue("El filter");
		csv.appendValue("Active");
		csv.appendValue("El action");
		csv.appendValue("Host");
		csv.appendValue("Port");
		csv.appendValue("Page");
		csv.appendValue("HTTP method");
		csv.appendValue("Username");
		csv.appendValue("Password");
		csv.appendValue("Headers");
		csv.appendValue("Parameters");
		csv.appendValue("Counter template");
		csv.startNewLine();
		for (WebHook webHook : webHookService.list()) {
			csv.appendValue(webHook.getCode());
			csv.appendValue(webHook.getClassNameFilter());
			csv.appendValue(webHook.getEventTypeFilter() + "");
			csv.appendValue(webHook.getElFilter());
			csv.appendValue(webHook.isDisabled() + "");
			csv.appendValue(webHook.getElAction());
			csv.appendValue(webHook.getHost());
			csv.appendValue(webHook.getPort() + "");
			csv.appendValue(webHook.getPage());
			csv.appendValue(webHook.getHttpMethod() + "");
			csv.appendValue(webHook.getUsername());
			csv.appendValue(webHook.getPassword());
			csv.appendValue(webHook.getHeaders() + "");
			csv.appendValue(webHook.getParams() + "");
			csv.appendValue(webHook.getCounterTemplate()!=null?  webHook.getCounterTemplate().getCode(): null);
			csv.startNewLine();
		}
		InputStream inputStream = new ByteArrayInputStream(csv.toString()
				.getBytes());
		csv.download(inputStream, "WebHooks.csv");
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

			WebHook webHook = null;
			while (csvReader.readRecord()) {
				String[] values = csvReader.getValues();
				webHook = new WebHook();
				webHook.setCode(values[CODE]);
				webHook.setClassNameFilter(values[CLASS_NAME_FILTER]);
				webHook.setEventTypeFilter(NotificationEventTypeEnum
						.valueOf(values[EVENT_TYPE_FILTER]));
				webHook.setElFilter(values[EL_FILTER]);
				webHook.setDisabled(Boolean.parseBoolean(values[ACTIVE]));
				webHook.setElAction(values[EL_ACTION]);
				webHook.setHost(values[HOST]);
				webHook.setPort(Integer.parseInt(values[PORT]));
				webHook.setPage(values[PAGE]);
				webHook.setHttpMethod(WebHookMethodEnum
						.valueOf(values[HTTP_METHOD]));
				webHook.setUsername(values[USERNAME]);
				webHook.setPassword(values[PASSWORD]);
				if(!StringUtils.isBlank(values[COUNTER_TEMPLATE])){
					CounterTemplate counterTemplate=counterTemplateService.findByCode(values[COUNTER_TEMPLATE], getCurrentProvider());
						webHook.setCounterTemplate(counterTemplate!=null ?counterTemplate: null);
				}
				
				webHookService.create(webHook);
				messages.info(new BundleKey("messages", "commons.csv"));
			}

		}
	}

}
