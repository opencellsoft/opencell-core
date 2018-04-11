package org.meveo.api.notification;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.notification.WebHookDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.notification.HttpProtocol;
import org.meveo.model.notification.WebHook;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.catalog.impl.CounterTemplateService;
import org.meveo.service.notification.WebHookService;
import org.meveo.service.script.ScriptInstanceService;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 **/
@Stateless
public class WebHookApi extends BaseCrudApi<WebHook, WebHookDto> {

    @Inject
    private WebHookService webHookService;

    @Inject
    private CounterTemplateService counterTemplateService;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    public WebHook create(WebHookDto postData) throws MeveoApiException, BusinessException {

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

        if (webHookService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(WebHook.class, postData.getCode());
        }
        ScriptInstance scriptInstance = null;
        if (!StringUtils.isBlank(postData.getScriptInstanceCode())) {
            scriptInstance = scriptInstanceService.findByCode(postData.getScriptInstanceCode());
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
            counterTemplate = counterTemplateService.findByCode(postData.getCounterTemplate());
            if (counterTemplate == null) {
                throw new EntityDoesNotExistsException(CounterTemplate.class, postData.getCounterTemplate());
            }
        }

        WebHook webHook = new WebHook();
        webHook.setCode(postData.getCode());
        webHook.setClassNameFilter(postData.getClassNameFilter());
        webHook.setEventTypeFilter(postData.getEventTypeFilter());
        webHook.setScriptInstance(scriptInstance);
        webHook.setParams(postData.getScriptParams());
        webHook.setElFilter(postData.getElFilter());
        webHook.setCounterTemplate(counterTemplate);

        if (!StringUtils.isBlank(postData.getHttpProtocol())) {
            webHook.setHttpProtocol(postData.getHttpProtocol());
        } else {
            webHook.setHttpProtocol(HttpProtocol.HTTP);
        }

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

        if (postData.isDisabled() != null) {
            webHook.setDisabled(postData.isDisabled());
        }
        webHookService.create(webHook);

        return webHook;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.meveo.api.ApiService#find(java.lang.String)
     */
    @Override
    public WebHookDto find(String notificationCode) throws EntityDoesNotExistsException, MissingParameterException, InvalidParameterException, MeveoApiException {
        WebHookDto result = new WebHookDto();

        if (!StringUtils.isBlank(notificationCode)) {
            WebHook notif = webHookService.findByCode(notificationCode);

            if (notif == null) {
                throw new EntityDoesNotExistsException(WebHook.class, notificationCode);
            }

            result = new WebHookDto(notif);
        } else {
            missingParameters.add("code");

            handleMissingParameters();
        }

        return result;
    }

    public WebHook update(WebHookDto postData) throws MeveoApiException, BusinessException {

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

        WebHook webHook = webHookService.findByCode(postData.getCode());
        if (webHook == null) {
            throw new EntityDoesNotExistsException(WebHook.class, postData.getCode());
        }

        ScriptInstance scriptInstance = null;
        if (!StringUtils.isBlank(postData.getScriptInstanceCode())) {
            scriptInstance = scriptInstanceService.findByCode(postData.getScriptInstanceCode());
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
            counterTemplate = counterTemplateService.findByCode(postData.getCounterTemplate());
            if (counterTemplate == null) {
                throw new EntityDoesNotExistsException(CounterTemplate.class, postData.getCounterTemplate());
            }
        }

        webHook.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());
        webHook.setClassNameFilter(postData.getClassNameFilter());
        webHook.setEventTypeFilter(postData.getEventTypeFilter());
        webHook.setScriptInstance(scriptInstance);
        webHook.setParams(postData.getScriptParams());
        webHook.setElFilter(postData.getElFilter());
        webHook.setCounterTemplate(counterTemplate);

        if (!StringUtils.isBlank(postData.getHttpProtocol())) {
            webHook.setHttpProtocol(postData.getHttpProtocol());
        }
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

        webHook = webHookService.update(webHook);

        return webHook;
    }

    public void remove(String notificationCode) throws MeveoApiException, BusinessException {
        if (!StringUtils.isBlank(notificationCode)) {
            WebHook webHook = webHookService.findByCode(notificationCode);

            if (webHook == null) {
                throw new EntityDoesNotExistsException(WebHook.class, notificationCode);
            }

            webHookService.remove(webHook);
        } else {
            missingParameters.add("code");

            handleMissingParameters();
        }
    }

    @Override
    public WebHook createOrUpdate(WebHookDto postData) throws MeveoApiException, BusinessException {
        if (webHookService.findByCode(postData.getCode()) == null) {
            return create(postData);
        } else {
            return update(postData);
        }
    }
}
