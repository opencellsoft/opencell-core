package org.meveo.export;

import java.util.Map;

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

    public RelatedEntityToExport() {
        super();
    }

    @SuppressWarnings("rawtypes")
    public RelatedEntityToExport(String selection, Map<String, String> parameters, Class entityClass) {
        this.selection = selection;
        this.parameters = parameters;
        this.entityClass = entityClass;
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
}