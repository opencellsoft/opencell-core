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

    private String pathToEntityRelatedTo;

    private String condition;

    private String selection;

    private Map<String, String> parameters;

    @SuppressWarnings("rawtypes")
    private Class entityClass;

    private String templateName;

    public RelatedEntityToExport() {
        super();
    }

    @SuppressWarnings("rawtypes")
    public RelatedEntityToExport(String pathToEntityRelatedTo, String condition, String selection, Map<String, String> parameters, Class entityClass) {
        this.pathToEntityRelatedTo = pathToEntityRelatedTo;
        this.condition = condition;
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

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    @Override
    public String toString() {
        return String.format("RelatedEntityToExport [pathToEntity=%s, relatedEntityCondition=%s entityClass=%s, templateName=%s]", pathToEntityRelatedTo, condition,
            entityClass, templateName);
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        if (entityClass != null) {
            hash = entityClass.hashCode();

        } else if (templateName != null) {
            hash = templateName.hashCode();
        }

        if (pathToEntityRelatedTo != null) {
            hash = hash + pathToEntityRelatedTo.hashCode();
        }
        if (condition != null) {
            hash = hash + condition.hashCode();
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

        if (StringUtils.compare(getPathToEntityRelatedTo(), other.getPathToEntityRelatedTo()) == 0
                && StringUtils.compare(getCondition(), other.getCondition()) == 0
                && StringUtils.compare(getTemplateName(), other.getTemplateName()) == 0 && getEntityClass() == other.getEntityClass()
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

    public String getPathToEntityRelatedTo() {
        return pathToEntityRelatedTo;
    }

    public String getCondition() {
        return condition;
    }
}