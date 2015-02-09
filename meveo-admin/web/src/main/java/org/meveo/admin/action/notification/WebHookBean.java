package org.meveo.admin.action.notification;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.CsvBuilder;
import org.meveo.model.notification.WebHook;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.notification.WebHookService;

@Named
@ConversationScoped
public class WebHookBean extends BaseBean<WebHook> {

    private static final long serialVersionUID = -5605274745661054861L;

    @Inject
    WebHookService webHookService;

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
        csv.startNewLine();
        for(WebHook  webHook:webHookService.list()){ 
        	 csv.appendValue(webHook.getCode());
        	 csv.appendValue(webHook.getClassNameFilter());
        	 csv.appendValue(webHook.getEventTypeFilter()+"");
        	 csv.appendValue(webHook.getElFilter());
        	 csv.appendValue(webHook.isDisabled()+"");
        	 csv.appendValue(webHook.getElAction());
        	 csv.appendValue(webHook.getHost());
        	 csv.appendValue(webHook.getPort()+"");
        	 csv.appendValue(webHook.getPage());
        	 csv.appendValue(webHook.getHttpMethod()+"");
        	 csv.appendValue(webHook.getUsername());
        	 csv.appendValue(webHook.getPassword());
        	 csv.appendValue(webHook.getHeaders()+"");
        	 csv.appendValue(webHook.getParams()+"");
        	 csv.startNewLine();
        }
        InputStream inputStream=new ByteArrayInputStream(csv.toString().getBytes());
        csv.download(inputStream, "WebHooks.csv");
    }
    
}
