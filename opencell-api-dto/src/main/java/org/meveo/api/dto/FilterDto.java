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

package org.meveo.api.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.meveo.model.filter.Filter;

/**
 * DTO class for Filter entity
 *
 * @author Tyshan Shi
 */
@XmlRootElement(name = "Filter")
@XmlAccessorType(XmlAccessType.FIELD)
public class FilterDto extends EnableBusinessDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The shared. */
    private Boolean shared;

    /** The input xml. */
    private String inputXml;

    /**
     * JPQL query
     */
    private String pollingQuery;

    /**
     * Target entity full classname. Used together with pollingQuery. Must match the query result.
     */
    private String entityClass;

    /** The custom fields. */
    private CustomFieldsDto customFields;

    /**
     * Instantiate a new Filter Dto
     */
    public FilterDto() {

    }

    /**
     * Convert Filter entity to DTO
     * 
     * @param filter Filter to convert
     * @param customFieldInstances the custom field instances
     */
    public FilterDto(Filter filter, CustomFieldsDto customFieldInstances) {
        super(filter);

        shared = filter.getShared();
        inputXml = filter.getInputXml();
        pollingQuery = filter.getPollingQuery();
        entityClass = filter.getEntityClass();

        customFields = customFieldInstances;
    }

    /**
     * Gets the shared.
     *
     * @return the shared
     */
    public Boolean getShared() {
        return shared;
    }

    /**
     * Sets the shared.
     *
     * @param shared the new shared
     */
    public void setShared(Boolean shared) {
        this.shared = shared;
    }

    /**
     * Gets the input xml.
     *
     * @return the input xml
     */
    public String getInputXml() {
        return inputXml;
    }

    /**
     * Sets the input xml.
     *
     * @param inputXml the new input xml
     */
    public void setInputXml(String inputXml) {
        this.inputXml = inputXml;
    }

    public String getPollingQuery() {
        return pollingQuery;
    }

    public void setPollingQuery(String pollingQuery) {
        this.pollingQuery = pollingQuery;
    }

    /**
     * @return Target entity full classname. Used together with pollingQuery. Must match the query result
     */
    public String getEntityClass() {
        return entityClass;
    }

    /**
     * @param entityClass Target entity full classname. Used together with pollingQuery. Must match the query result
     */
    public void setEntityClass(String entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * Gets the custom fields.
     *
     * @return the customFields
     */
    public CustomFieldsDto getCustomFields() {
        return customFields;
    }

    /**
     * Sets the custom fields.
     *
     * @param customFields the customFields to set
     */
    public void setCustomFields(CustomFieldsDto customFields) {
        this.customFields = customFields;
    }

    @Override
    public String toString() {
        return "FilterDto [shared=" + shared + ", inputXml=" + inputXml + ", pollingQuery=" + pollingQuery + "]";
    }
}