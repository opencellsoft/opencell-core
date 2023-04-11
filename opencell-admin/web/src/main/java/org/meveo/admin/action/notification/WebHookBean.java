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

package org.meveo.admin.action.notification;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.RejectedImportException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.commons.utils.CsvBuilder;
import org.meveo.commons.utils.CsvReader;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.notification.NotificationEventTypeEnum;
import org.meveo.model.notification.StrategyImportTypeEnum;
import org.meveo.model.notification.WebHook;
import org.meveo.model.notification.WebHookMethodEnum;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.CounterTemplateService;
import org.meveo.service.notification.WebHookService;
import org.meveo.service.script.ScriptInstanceService;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 * Standard backing bean for {@link WebHook} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their create,
 * edit, view, delete operations). It works with Manaty custom JSF components.
 * 
 * @author Wassim Drira
 * @lastModifiedVersion 5.0
 * 
 */
@Named

public class WebHookBean extends BaseNotificationBean<WebHook> {

    private static final long serialVersionUID = -5605274745661054861L;

    @Inject
    WebHookService webHookService;

    @Inject
    CounterTemplateService counterTemplateService;

    @Inject
    ScriptInstanceService scriptInstanceService;

    private StrategyImportTypeEnum strategyImportType;
    CsvBuilder csv = null;
    private String existingEntitiesCsvFile = null;

    CsvReader csvReader = null;
    private UploadedFile file;

    private static final int CODE = 0;
    private static final int CLASS_NAME_FILTER = 1;
    private static final int EVENT_TYPE_FILTER = 2;
    private static final int EL_FILTER = 3;
    private static final int ACTIVE = 4;
    private static final int SCRIPT_INSTANCE_CODE = 5;
    private static final int HOST = 6;
    private static final int PORT = 7;
    private static final int PAGE = 8;
    private static final int HTTP_METHOD = 9;
    private static final int USERNAME = 10;
    private static final int PASSWORD = 11;
    private static final int HEADERS = 12;
    private static final int PARAMS = 13;
    private static final int COUNTER_TEMPLATE = 14;

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
        extractMapTypeFieldFromEntity(webhook.getWebhookParams(), "webhookParams");
        return webhook;
    }

    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {

        updateMapTypeFieldInEntity(entity.getHeaders(), "headers");
        updateMapTypeFieldInEntity(entity.getParams(), "params");
        updateMapTypeFieldInEntity(entity.getWebhookParams(), "webhookParams");

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
        for (WebHook webHook : (!filters.isEmpty() && filters.size() > 0) ? getLazyDataModel() : webHookService.list()) {
            csv.appendValue(webHook.getCode());
            csv.appendValue(webHook.getClassNameFilter());
            csv.appendValue(webHook.getEventTypeFilter() + "");
            csv.appendValue(webHook.getElFilter());
            csv.appendValue(webHook.isDisabled() + "");
            csv.appendValue((webHook.getScriptInstance() == null ? "" : webHook.getScriptInstance().getCode()));
            csv.appendValue(webHook.getHost());
            csv.appendValue(webHook.getPort() + "");
            csv.appendValue(webHook.getPage());
            csv.appendValue(webHook.getHttpMethod() + "");
            csv.appendValue(webHook.getUsername());
            csv.appendValue(webHook.getPassword());

            StringBuffer headers = new StringBuffer();
            if (webHook.getHeaders() != null) {
                String sep = "";
                for (String key : webHook.getHeaders().keySet()) {
                    String valueHeaders = webHook.getHeaders().get(key);
                    headers.append(sep).append(key).append(":").append(Base64.encodeBase64String(valueHeaders.getBytes()));
                    sep = "|";
                }
                csv.appendValue(headers.toString());
            }
            StringBuffer params = new StringBuffer();
            if (webHook.getParams() != null) {
                String sep = "";
                for (String key : webHook.getParams().keySet()) {
                    String valueParams = webHook.getParams().get(key);
                    params.append(sep).append(key).append(":").append(Base64.encodeBase64String(valueParams.getBytes()));
                    sep = "|";
                }
                csv.appendValue(params.toString());
            }
            csv.appendValue(webHook.getCounterTemplate() != null ? webHook.getCounterTemplate().getCode() : null);
            csv.startNewLine();
        }
        InputStream inputStream = new ByteArrayInputStream(csv.toString().getBytes());
        csv.download(inputStream, "WebHooks.csv");
    }

    public void handleFileUpload(FileUploadEvent event) throws Exception {
        try {
            file = event.getFile();
            log.debug("File uploaded " + file.getFileName());
            upload();
            messages.info(new BundleKey("messages", "import.csv.successful"));
        } catch (Exception e) {
            log.error("Failed to handle uploaded file {}", event.getFile().getFileName(), e);
            messages.error(new BundleKey("messages", "import.csv.failed"), e.getClass().getSimpleName() + " " + e.getMessage());
        }
    }

    private void upload() throws IOException, BusinessException {
        if (file == null) {
            return;
        }
        csvReader = new CsvReader(file.getInputstream(), ';', Charset.forName("ISO-8859-1"));
        csvReader.readHeaders();

        String existingEntitiesCSV = paramBeanFactory.getInstance().getProperty("existingEntities.csv.dir", "existingEntitiesCSV");
        File dir = new File(paramBeanFactory.getChrootDir() + File.separator + existingEntitiesCSV);
        dir.mkdirs();
        existingEntitiesCsvFile = dir.getAbsolutePath() + File.separator + "WebHooks_" + new SimpleDateFormat("ddMMyyyyHHmmSS").format(new Date()) + ".csv";
        csv = new CsvBuilder();
        boolean isEntityAlreadyExist = false;
        while (csvReader.readRecord()) {
            String[] values = csvReader.getValues();
            WebHook existingEntity = webHookService.findByCode(values[CODE]);
            if (existingEntity != null) {
                checkSelectedStrategy(values, existingEntity, isEntityAlreadyExist);
                isEntityAlreadyExist = true;
            } else {
                WebHook webHook = new WebHook();
                webHook.setCode(values[CODE]);
                webHook.setClassNameFilter(values[CLASS_NAME_FILTER]);
                webHook.setEventTypeFilter(NotificationEventTypeEnum.valueOf(values[EVENT_TYPE_FILTER]));
                webHook.setElFilter(values[EL_FILTER]);
                webHook.setDisabled(Boolean.parseBoolean(values[ACTIVE]));
                if (!StringUtils.isBlank(values[SCRIPT_INSTANCE_CODE])) {
                    ScriptInstance scriptInstance = scriptInstanceService.findByCode(values[SCRIPT_INSTANCE_CODE]);
                    webHook.setScriptInstance(scriptInstance);
                }
                webHook.setHost(values[HOST]);
                webHook.setPort(Integer.parseInt(values[PORT]));
                webHook.setPage(values[PAGE]);
                webHook.setHttpMethod(WebHookMethodEnum.valueOf(values[HTTP_METHOD]));
                webHook.setUsername(values[USERNAME]);
                webHook.setPassword(values[PASSWORD]);

                if (values[HEADERS] != null && values[HEADERS].length() > 0) {
                    String[] mapElements = values[HEADERS].split("\\|");
                    if (mapElements != null && mapElements.length > 0) {
                        Map<String, String> headers = new HashMap<String, String>();
                        for (String element : mapElements) {
                            String[] param = element.split(":");
                            String value = new String(Base64.decodeBase64(param[1]));
                            headers.put(param[0], value);
                        }
                        webHook.setHeaders(headers);
                    }
                }
                if (values[PARAMS] != null && values[PARAMS].length() > 0) {
                    String[] mapElements = values[PARAMS].split("\\|");
                    if (mapElements != null && mapElements.length > 0) {
                        Map<String, String> params = new HashMap<String, String>();
                        for (String element : mapElements) {
                            String[] param = element.split(":");
                            String value = new String(Base64.decodeBase64(param[1]));
                            params.put(param[0], value);
                        }
                        webHook.setParams(params);
                    }
                }
                if (!StringUtils.isBlank(values[COUNTER_TEMPLATE])) {
                    CounterTemplate counterTemplate = counterTemplateService.findByCode(values[COUNTER_TEMPLATE]);
                    webHook.setCounterTemplate(counterTemplate != null ? counterTemplate : null);
                }
                webHookService.create(webHook);
            }
        }
        if (isEntityAlreadyExist && strategyImportType.equals(StrategyImportTypeEnum.REJECT_EXISTING_RECORDS)) {
            csv.writeFile(csv.toString().getBytes(), existingEntitiesCsvFile);
        }
    }

    public void checkSelectedStrategy(String[] values, WebHook existingEntity, boolean isEntityAlreadyExist) throws BusinessException {
        if (strategyImportType.equals(StrategyImportTypeEnum.UPDATED)) {
            existingEntity.setClassNameFilter(values[CLASS_NAME_FILTER]);
            existingEntity.setEventTypeFilter(NotificationEventTypeEnum.valueOf(values[EVENT_TYPE_FILTER]));
            existingEntity.setElFilter(values[EL_FILTER]);
            existingEntity.setDisabled(Boolean.parseBoolean(values[ACTIVE]));
            if (!StringUtils.isBlank(values[SCRIPT_INSTANCE_CODE])) {
                ScriptInstance scriptInstance = scriptInstanceService.findByCode(values[SCRIPT_INSTANCE_CODE]);
                existingEntity.setScriptInstance(scriptInstance);
            }
            existingEntity.setHost(values[HOST]);
            existingEntity.setPort(Integer.parseInt(values[PORT]));
            existingEntity.setPage(values[PAGE]);
            existingEntity.setHttpMethod(WebHookMethodEnum.valueOf(values[HTTP_METHOD]));
            existingEntity.setUsername(values[USERNAME]);
            existingEntity.setPassword(values[PASSWORD]);
            if (values[HEADERS] != null && values[HEADERS].length() > 0) {
                String[] mapElements = values[HEADERS].split("\\|");
                if (mapElements != null && mapElements.length > 0) {
                    Map<String, String> headers = new HashMap<String, String>();
                    for (String element : mapElements) {
                        String[] param = element.split(":");
                        String value = new String(Base64.decodeBase64(param[1]));
                        headers.put(param[0], value);

                    }
                    existingEntity.setHeaders(headers);
                }
            }
            if (values[PARAMS] != null && values[PARAMS].length() > 0) {
                String[] mapElements = values[PARAMS].split("\\|");
                if (mapElements != null && mapElements.length > 0) {
                    Map<String, String> params = new HashMap<String, String>();
                    for (String element : mapElements) {
                        String[] param = element.split(":");
                        String value = new String(Base64.decodeBase64(param[1]));
                        params.put(param[0], value);
                    }
                    existingEntity.setParams(params);
                }
            }
            if (!StringUtils.isBlank(values[COUNTER_TEMPLATE])) {
                CounterTemplate counterTemplate = counterTemplateService.findByCode(values[COUNTER_TEMPLATE]);
                existingEntity.setCounterTemplate(counterTemplate != null ? counterTemplate : null);
            }
            webHookService.update(existingEntity);

        } else if (strategyImportType.equals(StrategyImportTypeEnum.REJECTE_IMPORT)) {
            throw new RejectedImportException("notification.rejectImport");
        } else if (strategyImportType.equals(StrategyImportTypeEnum.REJECT_EXISTING_RECORDS)) {
            if (!isEntityAlreadyExist) {
                csv.appendValue("Code");
                csv.appendValue("Classename filter");
                csv.appendValue("Event type filter");
                csv.appendValue("El filter");
                csv.appendValue("Active");
                csv.appendValue("Script instance code");
                csv.appendValue("Host");
                csv.appendValue("Port");
                csv.appendValue("Page");
                csv.appendValue("HTTP method");
                csv.appendValue("Username");
                csv.appendValue("Password");
                csv.appendValue("Headers");
                csv.appendValue("Parameters");
                csv.appendValue("Counter template");
            }
            csv.startNewLine();
            csv.appendValue(values[CODE]);
            csv.appendValue(values[CLASS_NAME_FILTER]);
            csv.appendValue(values[EVENT_TYPE_FILTER]);
            csv.appendValue(values[EL_FILTER]);
            csv.appendValue(values[ACTIVE]);
            csv.appendValue(values[SCRIPT_INSTANCE_CODE]);
            csv.appendValue(values[HOST]);
            csv.appendValue(values[PORT]);
            csv.appendValue(values[PAGE]);
            csv.appendValue(values[HTTP_METHOD]);
            csv.appendValue(values[USERNAME]);
            csv.appendValue(values[PASSWORD]);
            csv.appendValue(values[COUNTER_TEMPLATE]);
        }
    }

    public StrategyImportTypeEnum getStrategyImportType() {
        return strategyImportType;
    }

    public void setStrategyImportType(StrategyImportTypeEnum strategyImportType) {
        this.strategyImportType = strategyImportType;
    }
}
