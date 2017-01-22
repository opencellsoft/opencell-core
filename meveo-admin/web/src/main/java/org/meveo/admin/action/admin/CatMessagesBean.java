/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.admin.action.admin;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.commons.utils.CsvReader;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.MultilanguageEntity;
import org.meveo.model.billing.CatMessages;
import org.meveo.service.base.MultilanguageEntityService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.CatMessagesService;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 * Standard backing bean for {@link CatMessages} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their create,
 * edit, view, delete operations). It works with Manaty custom JSF components.
 */
@Named
@ViewScoped
public class CatMessagesBean extends BaseBean<CatMessages> {

    private static final String INVALID_CLASS_TYPE = "Invalid class type!";

    private static final long serialVersionUID = 1L;

    /**
     * Injected @{link CatMessages} service. Extends {@link PersistenceService} .
     */
    @Inject
    private CatMessagesService catMessagesService;

    private BusinessEntity businessEntity;
    private String popupId;
    private String objectType;
    private Map<String, String> objectTypeMap;

    CsvReader csvReader = null;
    private UploadedFile file;
    private static final int ENTITY_CLASS = 1;
    private static final int CODE = 2;
    private static final int LANGUAGE_CODE = 4;
    private static final int DESCRIPTION_TRANSLATION = 5;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public CatMessagesBean() {
        super(CatMessages.class);
        objectTypeMap = new HashMap<>();
        Set<Class<?>> multilanguageEntities = ReflectionUtils.getClassesAnnotatedWith(MultilanguageEntity.class);
        MultilanguageEntity annotation = null;
        for (Class<?> entityClass : multilanguageEntities) {
            annotation = entityClass.getAnnotation(MultilanguageEntity.class);
            objectTypeMap.put("*" + annotation.group() + "_*", annotation.key());
        }
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<CatMessages> getPersistenceService() {
        return catMessagesService;
    }

    @Override
    protected String getListViewName() {
        return "catMessagess";
    }

    @Override
    public void preRenderView() {
        super.preRenderView();
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String entityCode = request.getParameter("entityCode");
        String entityClass = request.getParameter("entityClass");
        String objectType = request.getParameter("objectType");
        try {
            if (objectType != null) {
                setObjectType(getObjectTypeValue(objectType));
            }
            if (!StringUtils.isBlank(entityCode) && !StringUtils.isBlank(entityClass)) {
                BusinessEntity businessEntity = catMessagesService.findBusinessEntityByCodeAndClass(entityCode, entityClass, currentProvider);
                setBusinessEntity(businessEntity);
            }
        } catch (BusinessException e) {
            log.warn("Unable to retrieve entity with code: %s and class %s", entityCode, entityClass);
        }
    }

    protected Map<String, String> getObjectTypes() {
        return this.objectTypeMap;
    }

    private String getObjectTypeValue(String objectType) {
        String value = null;
        String result = null;
        for (String key : objectTypeMap.keySet()) {
            value = objectTypeMap.get(key);
            if (value.equalsIgnoreCase(objectType)) {
                result = key;
                break;
            }
        }
        return result;
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
        String entityCode = null;
        String entityClass = null;
        String[] values = null;
        MultilanguageEntityService<?> service = null;
        BusinessEntity entity = null;
        CatMessages existingDescription = null;
        CatMessages newDescription = null;
        while (csvReader.readRecord()) {
            values = csvReader.getValues();
            service = catMessagesService.getMultilanguageEntityService(values[ENTITY_CLASS]);
            entity = service.findByCode(values[CODE], getCurrentUser().getProvider());
            if (entity != null) {
                entityCode = entity.getCode();
                entityClass = catMessagesService.getEntityClass(entity);
            }
            if (entityCode != null && entityClass != null) {
                existingDescription = catMessagesService.getCatMessages(entity, values[LANGUAGE_CODE]);
                if (existingDescription != null) {
                    existingDescription.setDescription(values[DESCRIPTION_TRANSLATION]);
                    catMessagesService.update(existingDescription, getCurrentUser());
                } else {
                    newDescription = new CatMessages();
                    newDescription.setEntityCode(entityCode);
                    newDescription.setEntityClass(entityClass);
                    newDescription.setLanguageCode(values[LANGUAGE_CODE]);
                    newDescription.setDescription(values[DESCRIPTION_TRANSLATION]);
                    catMessagesService.create(newDescription, getCurrentUser());
                }
            }
        }

    }

    public BusinessEntity getBusinessEntity() {
        return businessEntity;
    }

    public void setBusinessEntity(BusinessEntity businessEntity) throws BusinessException {
        this.businessEntity = businessEntity;
        if (businessEntity != null) {
            List<CatMessages> catMessagesList = catMessagesService.getCatMessagesList(businessEntity);
            this.getLanguageMessagesMap().clear();
            for (CatMessages catMessages : catMessagesList) {
                this.getLanguageMessagesMap().put(catMessages.getLanguageCode(), catMessages.getDescription());
            }
        }
    }

    public String getPopupId() {
        return popupId;
    }

    public void clearBusinessEntity() {
        this.businessEntity = null;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
        if (!StringUtils.isBlank(objectType)) {
            popupId = objectType.replaceAll("\\*", "");
        } else {
            popupId = "";
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {

        String description = null;
        CatMessages catMsg = null;

        for (String key : languageMessagesMap.keySet()) {
            description = languageMessagesMap.get(key);
            catMsg = catMessagesService.getCatMessages(businessEntity, key);
            if (catMsg != null) {
                if (StringUtils.isBlank(description)) {
                    catMessagesService.remove(catMsg, getCurrentUser());
                } else {
                    catMsg.setDescription(description);
                    catMessagesService.update(catMsg, getCurrentUser());
                }
            } else if (!StringUtils.isBlank(description)) {
                catMsg = new CatMessages(businessEntity, key, description);
                catMessagesService.create(catMsg, getCurrentUser());
            }
        }

        getEntityService().update(businessEntity, getCurrentUser());
        messages.info(new BundleKey("messages", "update.successful"));
        return back();
    }

    @SuppressWarnings("rawtypes")
    private IPersistenceService getEntityService() throws BusinessException {
        MultilanguageEntityService<?> service = null;
        if (businessEntity != null) {
            service = catMessagesService.getMultilanguageEntityService(businessEntity.getClass().getSimpleName());
        }
        if (service == null) {
            throw new BusinessException(INVALID_CLASS_TYPE);
        }
        return service;
    }

    @Override
    protected String getDefaultSort() {
        return "entityCode";
    }
}