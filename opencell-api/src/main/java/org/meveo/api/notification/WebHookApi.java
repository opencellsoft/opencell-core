/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

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
 * The CRUD Api for WebHook Entity.
 *
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Stateless
public class WebHookApi extends BaseCrudApi<WebHook, WebHookDto> {

    @Inject
    private WebHookService webHookService;

    @Inject
    private CounterTemplateService counterTemplateService;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Override
    public WebHook create(WebHookDto postData) throws MeveoApiException, BusinessException {

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
        WebHook webHook = new WebHook();
        webHook = getWebHookFromDto(webHook, postData);
        webHookService.create(webHook);
        return webHook;
    }

    private WebHook getWebHookFromDto(WebHook webHook, WebHookDto postData) {
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
        webHook.setRunAsync(postData.isRunAsync());
        if (postData.getBodyEl() != null) {
            webHook.setBodyEL(postData.getBodyEl());
        }

        return webHook;
    }

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

    @Override
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
        webHook = getWebHookFromDto(webHook, postData);

        webHook = webHookService.update(webHook);

        return webHook;
    }
}