package org.meveo.export;

import java.util.Map;

import org.meveo.commons.utils.StringUtils;

/**
 * Contains information to retrieve related entities for export once primary entity was retrieved
 * 
 * @author Andrius Karpavicius
 * 
 */
public class RelatedEntityToExport {

    private String selection;

    private Map<String, String> parameters;

    @SuppressWarnings("rawtypes")
    private Class entityClass;

    private String templateName;

    public RelatedEntityToExport() {
        super();
    }

    @SuppressWarnings("rawtypes")
    public RelatedEntityToExport(String selection, Map<String, String> parameters, Class entityClass) {
        this.selection = selection;
        this.parameters = parameters;
        this.entityClass = entityClass;
    }

    public RelatedEntityToExport(String selection, Map<String, String> parameters, String templateName) {
        this.selection = selection;
        this.parameters = parameters;
        this.templateName = templateName;
    }

    public String getSelection() {
        return selection;
    }

    public void setSelection(String selectSql) {
        this.selection = selectSql;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    @SuppressWarnings("rawtypes")
    public Class getEntityClass() {
        return entityClass;
    }

    @SuppressWarnings("rawtypes")
    public void setEntityClass(Class entityClass) {
        this.entityClass = entityClass;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    @Override
    public String toString() {
        return String.format("RelatedEntityToExport [entityClass=%s, templateName=%s]", entityClass, templateName);
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        if (entityClass != null) {
            hash = entityClass.hashCode();

        } else if (templateName != null) {
            hash = templateName.hashCode();
        }

        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        } else if (!(obj instanceof RelatedEntityToExport)) { // Fails with proxed objects: getClass() != obj.getClass()){
            return false;
        }

        RelatedEntityToExport other = (RelatedEntityToExport) obj;

        if (StringUtils.compare(getTemplateName(), other.getTemplateName()) == 0 && getEntityClass() == other.getEntityClass()
                && StringUtils.compare(getSelection(), other.getSelection()) == 0) {
            return true;
        }
        return false;
    }

    public String getEntityClassNameOrTemplateName() {
        if (entityClass != null) {
            return entityClass.getSimpleName();
        } else {
            return templateName;
        }
    }
}