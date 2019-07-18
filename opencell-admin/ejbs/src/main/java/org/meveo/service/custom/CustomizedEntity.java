package org.meveo.service.custom;

import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.crm.CustomFieldTemplate;

public class CustomizedEntity {

    private Long id;

    private String entityCode;

    @SuppressWarnings("rawtypes")
    private Class entityClass;

    private Long customEntityId;

    private String description;
    
    public CustomizedEntity(String entityCode, Class entityClass) {
        this.entityCode = entityCode;
        this.entityClass = entityClass;
    }
    
    @SuppressWarnings("rawtypes")
    public CustomizedEntity(Class entityClass) {
        super();
        // this.entityName = ReflectionUtils.getCleanClassName(entityClass.getSimpleName());
        this.entityClass = entityClass;
    }

    @SuppressWarnings("rawtypes")
    public CustomizedEntity(String entityCode, Class entityClass, Long customEntityId, String description) {
        super();
        this.entityCode = entityCode;
        this.entityClass = entityClass;
        this.customEntityId = customEntityId;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEntityCode() {
        return entityCode;
    }

    @SuppressWarnings("rawtypes")
    public Class getEntityClass() {
        return entityClass;
    }

    public Long getCustomEntityId() {
        return customEntityId;
    }

    public boolean isCustomEntity() {
        return customEntityId != null;
    }

    public String getDescription() {
        return description;
    }

    public boolean isStandardEntity() {
        return customEntityId == null;
    }

    public String getClassnameToDisplay() {
        String classNameToDisplay = ReflectionUtils.getCleanClassName(getEntityClass().getName());
        if (!isStandardEntity()) {
            classNameToDisplay = classNameToDisplay + CustomFieldTemplate.ENTITY_REFERENCE_CLASSNAME_CETCODE_SEPARATOR + getEntityCode();
        }
        return classNameToDisplay;
    }

    public String getClassnameToDisplayHuman() {
        String classNameToDisplay = ReflectionUtils.getHumanClassName(getEntityClass().getSimpleName());
        if (!isStandardEntity()) {
            classNameToDisplay = classNameToDisplay + CustomFieldTemplate.ENTITY_REFERENCE_CLASSNAME_CETCODE_SEPARATOR + getEntityCode();
        }
        return classNameToDisplay;
    }

    @Override
    public String toString() {
        return String.format("CustomizedEntity [entityClass=%s, entityCode=%s, customEntityId=%s]", entityClass, entityCode, customEntityId);
    }
}