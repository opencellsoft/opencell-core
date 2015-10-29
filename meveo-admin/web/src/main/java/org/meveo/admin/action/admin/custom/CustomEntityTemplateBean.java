package org.meveo.admin.action.admin.custom;

import java.util.List;
import java.util.Map;

import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.LazyDataModelWSize;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.service.custom.CustomEntityTemplateService;
import org.meveo.service.job.Job;
import org.meveo.service.job.JobInstanceService;

@Named
@ViewScoped
public class CustomEntityTemplateBean extends BaseBean<CustomEntityTemplate> {

    private static final long serialVersionUID = 1187554162639618526L;

    private String entityClassName;

    private Class entityClass;

    private CustomizedEntity customizedEntity;

    private String cetPrefix;

    private List<CustomFieldTemplate> fields;

    private LazyDataModelWSize<CustomFieldTemplate> cetDataModel = null;

    @Inject
    private CustomEntityTemplateService customEntityTemplateService;

    @Inject
    private JobInstanceService jobInstanceService;

    private LazyDataModelWSize<CustomizedEntity> customizedEntitedDM = null;

    public CustomEntityTemplateBean() {
        super(CustomEntityTemplate.class);
    }

    public String getEntityClassName() {
        return entityClassName;
    }

    public void setEntityClassName(String entityClassName) {

        if (entityClassName != null) {
            int pos = entityClassName.indexOf("$$");
            if (pos > 0) {
                entityClassName = entityClassName.substring(0, pos);
            }
        }

        this.entityClassName = entityClassName;
    }

    @Override
    protected CustomEntityTemplateService getPersistenceService() {
        return customEntityTemplateService;
    }

    public boolean isCustomEntityTemplate() {
        return entityClassName == null || CustomEntityTemplate.class.getName().equals(entityClassName);
    }

    @SuppressWarnings("unchecked")
    public CustomizedEntity getCustomizedEntity() throws ClassNotFoundException {
        if (customizedEntity == null && entityClassName != null && !CustomEntityTemplate.class.getName().equals(entityClassName)) {
            entityClass = Class.forName(entityClassName);
            customizedEntity = new CustomizedEntity(entityClass.getSimpleName(), entityClass, null, null);
            if (Job.class.isAssignableFrom(entityClass)) {
                cetPrefix = "JOB_" + entityClass.getSimpleName();

                // Check and instantiate missing custom field templates for a given job
                Job job = jobInstanceService.getJobByName(entityClass.getSimpleName());
                Map<String, CustomFieldTemplate> jobCustomFields = job.getCustomFields();

                // Create missing custom field templates if needed
                customFieldTemplateService.createMissingTemplates(cetPrefix, jobCustomFields.values(), getCurrentProvider());

            } else {

                cetPrefix = ((CustomFieldEntity) entityClass.getAnnotation(CustomFieldEntity.class)).cftCodePrefix();
            }
        }

        return customizedEntity;
    }

    public List<CustomFieldTemplate> getFields() {
        if (fields != null || cetPrefix == null) {
            return fields;
        }
        
        fields = customFieldTemplateService.findByAppliesTo(cetPrefix, getCurrentProvider());
        return fields;
    }

    public void refreshFields() {
        fields = null;
    }

    @Override
    public String saveOrUpdate(boolean killConversation) throws BusinessException {
        super.saveOrUpdate(killConversation);
        return getEditViewName();
    }

    @Override
    public String getEditViewName() {
        return "customizedEntity";
    }

    @Override
    public String getListViewName() {
        return "customizedEntities";
    }

    public String getCetPrefix() {
        if (cetPrefix != null) {
            return cetPrefix;
        } else if (entity != null && entity.getCode() != null) {
            cetPrefix = entity.getCFTPrefix();
            return cetPrefix;
        }
        return null;
    }
}