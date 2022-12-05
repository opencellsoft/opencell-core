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
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.RejectedImportException;
import org.meveo.commons.utils.CsvBuilder;
import org.meveo.commons.utils.CsvReader;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.notification.InstantMessagingNotification;
import org.meveo.model.notification.InstantMessagingProviderEnum;
import org.meveo.model.notification.NotificationEventTypeEnum;
import org.meveo.model.notification.StrategyImportTypeEnum;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.CounterTemplateService;
import org.meveo.service.notification.InstantMessagingNotificationService;
import org.meveo.service.script.ScriptInstanceService;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 * Standard backing bean for {@link InstantMessagingNotification} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in
 * datatable, their create, edit, view, delete operations). It works with Manaty custom JSF components.
 * 
 * @author Wassim Drira
 * @lastModifiedVersion 5.0
 * 
 */
@Named
@ViewScoped
public class InstantMessagingNotificationBean extends BaseBean<InstantMessagingNotification> {

    private static final long serialVersionUID = 6473465285480945644L;

    @Inject
    InstantMessagingNotificationService imNotificationService;

    @Inject
    UserService userService;

    @Inject
    ScriptInstanceService scriptInstanceService;

    private StrategyImportTypeEnum strategyImportType;

    @Inject
    CounterTemplateService counterTemplateService;

    CsvBuilder csv = null;
    private String providerDir;
    private String existingEntitiesCsvFile = null;

    CsvReader csvReader = null;
    private UploadedFile file;

    private static final int CODE = 0;
    private static final int CLASS_NAME_FILTER = 1;
    private static final int EVENT_TYPE_FILTER = 2;
    private static final int EL_FILTER = 3;
    private static final int SCRIPT_INSTANCE_CODE = 5;
    private static final int ACTIVE = 4;
    private static final int IM_PROVIDER = 6;
    private static final int IM_IDENTIFIER_EL = 7;
    private static final int IM_IDENTIFIER_LIST = 8;
    private static final int USERS_LIST = 9;
    private static final int MESSAGE = 10;
    private static final int COUNTER_TEMPLATE = 11;

    public InstantMessagingNotificationBean() {
        super(InstantMessagingNotification.class);
    }

    @Override
    protected IPersistenceService<InstantMessagingNotification> getPersistenceService() {
        return imNotificationService;
    }

    public void exportToFile() throws Exception {
        CsvBuilder csv = new CsvBuilder();
        csv.appendValue("Code");
        csv.appendValue("Classename filter");
        csv.appendValue("Event type filter");
        csv.appendValue("El filter");
        csv.appendValue("Script instance code");
        csv.appendValue("Active");
        csv.appendValue("IM provider");
        csv.appendValue("IM identifier EL");
        csv.appendValue("IM identifiers list");
        csv.appendValue("Users list");
        csv.appendValue("Message");
        csv.appendValue("Counter template");
        csv.startNewLine();
        for (InstantMessagingNotification imNotification : imNotificationService.list()) {
            csv.appendValue(imNotification.getCode());
            csv.appendValue(imNotification.getClassNameFilter());
            csv.appendValue(imNotification.getEventTypeFilter() + "");
            csv.appendValue(imNotification.getElFilter());
            csv.appendValue((imNotification.getScriptInstance() == null ? "" : imNotification.getScriptInstance().getCode()));
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
            csv.appendValue(imNotification.getCounterTemplate() != null ? imNotification.getCounterTemplate().getCode() : null);
            csv.startNewLine();
        }

        InputStream inputStream = new ByteArrayInputStream(csv.toString().getBytes());
        csv.download(inputStream, "InstantMessagingNotification.csv");
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

        ParamBean paramBean = paramBeanFactory.getInstance();
        String existingEntitiesCSV = paramBean.getProperty("existingEntities.csv.dir", "existingEntitiesCSV");
        providerDir = paramBean.getChrootDir(currentUser.getProviderCode());
        File dir = new File(providerDir + File.separator + existingEntitiesCSV);
        dir.mkdirs();
        existingEntitiesCsvFile = dir.getAbsolutePath() + File.separator + "InstantMessagingNotifications_" + new SimpleDateFormat("ddMMyyyyHHmmSS").format(new Date()) + ".csv";
        csv = new CsvBuilder();
        boolean isEntityAlreadyExist = false;
        while (csvReader.readRecord()) {
            String[] values = csvReader.getValues();
            InstantMessagingNotification existingEntity = imNotificationService.findByCode(values[CODE]);
            if (existingEntity != null) {
                checkSelectedStrategy(values, existingEntity, isEntityAlreadyExist);
                isEntityAlreadyExist = true;
            } else {
                InstantMessagingNotification instMessNotif = new InstantMessagingNotification();
                instMessNotif.setCode(values[CODE]);
                instMessNotif.setClassNameFilter(values[CLASS_NAME_FILTER]);
                instMessNotif.setEventTypeFilter(NotificationEventTypeEnum.valueOf(values[EVENT_TYPE_FILTER]));
                instMessNotif.setElFilter(values[EL_FILTER]);
                if (!StringUtils.isBlank(values[SCRIPT_INSTANCE_CODE])) {
                    ScriptInstance scriptInstance = scriptInstanceService.findByCode(values[SCRIPT_INSTANCE_CODE]);
                    instMessNotif.setScriptInstance(scriptInstance);
                }
                instMessNotif.setDisabled(Boolean.parseBoolean(values[ACTIVE]));
                instMessNotif.setImProvider(InstantMessagingProviderEnum.valueOf(values[IM_PROVIDER]));
                instMessNotif.setIdEl(values[IM_IDENTIFIER_EL]);
                String identifiers = values[IM_IDENTIFIER_LIST];
                if (!StringUtils.isBlank(identifiers)) {
                    String[] ids = identifiers.split(",");
                    List<String> idList = Arrays.asList(ids);
                    for (String id : idList) {
                        if (instMessNotif.getIds() == null) {
                            instMessNotif.setIds(new HashSet<String>());
                        }
                        instMessNotif.getIds().add(id);
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
                            if (instMessNotif.getUsers() == null) {
                                instMessNotif.setUsers(new HashSet<User>());
                            }
                            instMessNotif.getUsers().add(user);
                        }
                    }
                }
                instMessNotif.setMessage(values[MESSAGE]);
                if (!StringUtils.isBlank(values[COUNTER_TEMPLATE])) {
                    CounterTemplate counterTemplate = counterTemplateService.findByCode(values[COUNTER_TEMPLATE]);
                    instMessNotif.setCounterTemplate(counterTemplate != null ? counterTemplate : null);
                }
                imNotificationService.create(instMessNotif);
            }
        }
        if (isEntityAlreadyExist && strategyImportType.equals(StrategyImportTypeEnum.REJECT_EXISTING_RECORDS)) {
            csv.writeFile(csv.toString().getBytes(), existingEntitiesCsvFile);
        }
    }

    public void checkSelectedStrategy(String[] values, InstantMessagingNotification existingEntity, boolean isEntityAlreadyExist) throws BusinessException {
        if (strategyImportType.equals(StrategyImportTypeEnum.UPDATED)) {
            existingEntity.setClassNameFilter(values[CLASS_NAME_FILTER]);
            existingEntity.setEventTypeFilter(NotificationEventTypeEnum.valueOf(values[EVENT_TYPE_FILTER]));
            existingEntity.setElFilter(values[EL_FILTER]);
            if (!StringUtils.isBlank(values[SCRIPT_INSTANCE_CODE])) {
                ScriptInstance scriptInstance = scriptInstanceService.findByCode(values[SCRIPT_INSTANCE_CODE]);
                existingEntity.setScriptInstance(scriptInstance);
            }
            existingEntity.setDisabled(Boolean.parseBoolean(values[ACTIVE]));
            existingEntity.setImProvider(InstantMessagingProviderEnum.valueOf(values[IM_PROVIDER]));
            existingEntity.setIdEl(values[IM_IDENTIFIER_EL]);
            String identifiers = values[IM_IDENTIFIER_LIST];
            if (!StringUtils.isBlank(identifiers)) {
                String[] ids = identifiers.split(",");
                List<String> idList = Arrays.asList(ids);
                for (String id : idList) {
                    if (existingEntity.getIds() == null) {
                        existingEntity.setIds(new HashSet<String>());
                    }
                    existingEntity.getIds().add(id);
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
                        if (existingEntity.getUsers() == null) {
                            existingEntity.setUsers(new HashSet<User>());
                        }
                        existingEntity.getUsers().add(user);
                    }
                }
            }
            existingEntity.setMessage(values[MESSAGE]);
            if (!StringUtils.isBlank(values[COUNTER_TEMPLATE])) {
                CounterTemplate counterTemplate = counterTemplateService.findByCode(values[COUNTER_TEMPLATE]);
                existingEntity.setCounterTemplate(counterTemplate != null ? counterTemplate : null);
            }
            imNotificationService.update(existingEntity);
        } else if (strategyImportType.equals(StrategyImportTypeEnum.REJECTE_IMPORT)) {
            throw new RejectedImportException("notification.rejectImport");
        } else if (strategyImportType.equals(StrategyImportTypeEnum.REJECT_EXISTING_RECORDS)) {
            if (!isEntityAlreadyExist) {
                csv.appendValue("Code");
                csv.appendValue("Classename filter");
                csv.appendValue("Event type filter");
                csv.appendValue("El filter");
                csv.appendValue("Script instance code");
                csv.appendValue("Active");
                csv.appendValue("IM provider");
                csv.appendValue("IM identifier EL");
                csv.appendValue("IM identifiers list");
                csv.appendValue("Users list");
                csv.appendValue("Message");
                csv.appendValue("Counter template");
            }
            csv.startNewLine();
            csv.appendValue(values[CODE]);
            csv.appendValue(values[CLASS_NAME_FILTER]);
            csv.appendValue(values[EVENT_TYPE_FILTER]);
            csv.appendValue(values[EL_FILTER]);
            csv.appendValue(values[SCRIPT_INSTANCE_CODE]);
            csv.appendValue(values[ACTIVE]);
            csv.appendValue(values[IM_PROVIDER]);
            csv.appendValue(values[IM_IDENTIFIER_EL]);
            csv.appendValue(values[IM_IDENTIFIER_LIST]);
            csv.appendValue(values[USERS_LIST]);
            csv.appendValue(values[MESSAGE]);
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
