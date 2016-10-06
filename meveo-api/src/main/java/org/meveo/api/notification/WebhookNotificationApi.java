package org.meveo.api.notification;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.notification.WebhookNotificationDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.notification.WebHook;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.catalog.impl.CounterTemplateService;
import org.meveo.service.notification.WebHookService;
import org.meveo.service.script.ScriptInstanceService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class WebhookNotificationApi extends BaseApi {

    @Inject
    private WebHookService webHookService;

    @SuppressWarnings("rawtypes")
    @Inject
    private CounterTemplateService counterTemplateService;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    public void create(WebhookNotificationDto postData, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getClassNameFilter())) {
            missingParameters.add("classNameFilter");
        }
        if (postData.getEventTypeFilter() == null) {
            missingParameters.add("eventTypeFilter");
        }
        if (StringUtils.isBlank(postData.getHost())) {
            missingParameters.add("host");
        }
        if (StringUtils.isBlank(postData.getPage())) {
            missingParameters.add("page");
        }
        if (postData.getHttpMethod() == null) {
            missingParameters.add("httpMethod");
        }

        handleMissingParameters();

        if (webHookService.findByCode(postData.getCode(), currentUser.getProvider()) != null) {
            throw new EntityAlreadyExistsException(WebHook.class, postData.getCode());
        }
        ScriptInstance scriptInstance = null;
        if (!StringUtils.isBlank(postData.getScriptInstanceCode())) {
            scriptInstance = scriptInstanceService.findByCode(postData.getScriptInstanceCode(), currentUser.getProvider());
            if (scriptInstance == null) {
                throw new EntityDoesNotExistsException(ScriptInstance.class, postData.getScriptInstanceCode());
            }
        }
        // check class
        try {
            Class.forName(postData.getClassNameFilter());
        } catch (Exception e) {
            throw new InvalidParameterException("classNameFilter", postData.getClassNameFilter());
        }

        CounterTemplate counterTemplate = null;
        if (!StringUtils.isBlank(postData.getCounterTemplate())) {
            counterTemplate = counterTemplateService.findByCode(postData.getCounterTemplate(), currentUser.getProvider());
            if (counterTemplate == null) {
                throw new EntityDoesNotExistsException(CounterTemplate.class, postData.getCounterTemplate());
            }
        }

        WebHook webHook = new WebHook();
        webHook.setProvider(currentUser.getProvider());
        webHook.setCode(postData.getCode());
        webHook.setClassNameFilter(postData.getClassNameFilter());
        webHook.setEventTypeFilter(postData.getEventTypeFilter());
        webHook.setScriptInstance(scriptInstance);
        webHook.setParams(postData.getScriptParams());
        webHook.setElFilter(postData.getElFilter());
        webHook.setCounterTemplate(counterTemplate);

        webHook.setHost(postData.getHost());
        webHook.setPort(postData.getPort());
        webHook.setPage(postData.getPage());
        webHook.setHttpMethod(postData.getHttpMethod());
        webHook.setUsername(postData.getUsername());
        webHook.setPassword(postData.getPassword());
        if (postData.getHeaders() != null) {
            webHook.getHeaders().putAll(postData.getHeaders());
        }
        if (postData.getParams() != null) {
            webHook.getWebhookParams().putAll(postData.getParams());
        }

        webHookService.create(webHook, currentUser);
    }

    public WebhookNotificationDto find(String notificationCode, Provider provider) throws MeveoApiException {
        WebhookNotificationDto result = new WebhookNotificationDto();

        if (!StringUtils.isBlank(notificationCode)) {
            WebHook notif = webHookService.findByCode(notificationCode, provider);

            if (notif == null) {
                throw new EntityDoesNotExistsException(WebHook.class, notificationCode);
            }

            result = new WebhookNotificationDto(notif);
        } else {
            missingParameters.add("code");

            handleMissingParameters();
        }

        return result;
    }

    public void update(WebhookNotificationDto postData, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getClassNameFilter())) {
            missingParameters.add("classNameFilter");
        }
        if (postData.getEventTypeFilter()==null) {
            missingParameters.add("eventTypeFilter");
        }
        if (StringUtils.isBlank(postData.getHost())) {
            missingParameters.add("host");
        }
        if (StringUtils.isBlank(postData.getPage())) {
            missingParameters.add("page");
        }
        if (postData.getHttpMethod()==null) {
            missingParameters.add("httpMethod");
        }

        handleMissingParameters();
        
     WebHook webHook = webHookService.findByCode(postData.getCode(), currentUser.getProvider());
            if (webHook == null) {
                throw new EntityDoesNotExistsException(WebHook.class, postData.getCode());
            }

            ScriptInstance scriptInstance = null;
            if (!StringUtils.isBlank(postData.getScriptInstanceCode())) {
                scriptInstance = scriptInstanceService.findByCode(postData.getScriptInstanceCode(), currentUser.getProvider());
                if (scriptInstance == null) {
                    throw new EntityDoesNotExistsException(ScriptInstance.class, postData.getScriptInstanceCode());
                }
            }

            // check class
            try {
                Class.forName(postData.getClassNameFilter());
            } catch (Exception e) {
                throw new InvalidParameterException("classNameFilter", postData.getClassNameFilter());
            }

            CounterTemplate counterTemplate = null;
            if (!StringUtils.isBlank(postData.getCounterTemplate())) {
                counterTemplate = counterTemplateService.findByCode(postData.getCounterTemplate(), currentUser.getProvider());
                if (counterTemplate == null) {
                    throw new EntityDoesNotExistsException(CounterTemplate.class, postData.getCounterTemplate());
                }
            }

            webHook.setClassNameFilter(postData.getClassNameFilter());
            webHook.setEventTypeFilter(postData.getEventTypeFilter());
            webHook.setScriptInstance(scriptInstance);
            webHook.setParams(postData.getScriptParams());
            webHook.setElFilter(postData.getElFilter());
            webHook.setCounterTemplate(counterTemplate);

            webHook.setHost(postData.getHost());
            webHook.setPort(postData.getPort());
            webHook.setPage(postData.getPage());
            webHook.setHttpMethod(postData.getHttpMethod());
            webHook.setUsername(postData.getUsername());
            webHook.setPassword(postData.getPassword());
            if (postData.getHeaders() != null) {
                webHook.getHeaders().putAll(postData.getHeaders());
            }
            if (postData.getParams() != null) {
                webHook.getWebhookParams().putAll(postData.getParams());
            }

            webHookService.update(webHook, currentUser);
    }

    public void remove(String notificationCode, User currentUser) throws MeveoApiException, BusinessException {
        if (!StringUtils.isBlank(notificationCode)) {
            WebHook webHook = webHookService.findByCode(notificationCode, currentUser.getProvider());

            if (webHook == null) {
                throw new EntityDoesNotExistsException(WebHook.class, notificationCode);
            }

            webHookService.remove(webHook, currentUser);
        } else {
            missingParameters.add("code");

            handleMissingParameters();
        }
    }

    public void createOrUpdate(WebhookNotificationDto postData, User currentUser) throws MeveoApiException, BusinessException {
        if (webHookService.findByCode(postData.getCode(), currentUser.getProvider()) == null) {
            create(postData, currentUser);
        } else {
            update(postData, currentUser);
        }
    }
}
