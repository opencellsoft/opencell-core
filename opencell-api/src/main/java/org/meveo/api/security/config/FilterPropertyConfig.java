package org.meveo.api.security.config;

import org.meveo.model.BusinessEntity;

public class FilterPropertyConfig {

    private String property;
    private Class<? extends BusinessEntity> entityClass;
    private boolean allowAccessIfNull = false;

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public Class<? extends BusinessEntity> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<? extends BusinessEntity> entityClass) {
        this.entityClass = entityClass;
    }

    public boolean isAllowAccessIfNull() {
        return allowAccessIfNull;
    }

    public void setAllowAccessIfNull(boolean allowAccessIfNull) {
        this.allowAccessIfNull = allowAccessIfNull;
    }
}
