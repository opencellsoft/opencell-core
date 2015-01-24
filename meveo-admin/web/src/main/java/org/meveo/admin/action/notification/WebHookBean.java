package org.meveo.admin.action.notification;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
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

    protected String getDefaultViewName() {
        return "webHooks";
    }

    @Override
    protected String getListViewName() {
        return "webHooks";
    }

    @Override
    protected List<String> getFormFieldsToFetch() {
        return Arrays.asList("provider");
    }

    @Override
    protected List<String> getListFieldsToFetch() {
        return Arrays.asList("provider");
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
}
