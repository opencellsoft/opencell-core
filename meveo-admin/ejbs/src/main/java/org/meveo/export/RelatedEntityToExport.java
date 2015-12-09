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

    public RelatedEntityToExport() {
        super();
    }

    public RelatedEntityToExport(String selection, Map<String, String> parameters) {
        this.selection = selection;
        this.parameters = parameters;
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
}