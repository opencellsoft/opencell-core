package org.meveo.api.security.config;

import org.meveo.api.security.parameter.CodeParser;
import org.meveo.api.security.parameter.SecureMethodParameterParser;
import org.meveo.model.BusinessEntity;

public class SecureMethodParameterConfig {

    private int index = -1;

    private String property = "";

    private Class<? extends BusinessEntity> entityClass;

    private Class<? extends SecureMethodParameterParser<?>> parser;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

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

    public Class<? extends SecureMethodParameterParser<?>> getParser() {
        return parser;
    }

    public void setParser(Class<? extends SecureMethodParameterParser<?>> parser) {
        this.parser = parser;
    }
}
