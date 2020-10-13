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

package org.meveo.model.crm.custom;

import org.apache.commons.lang3.StringUtils;
import org.meveo.model.crm.CustomTableWrapper;
import org.meveo.model.crm.EntityReferenceWrapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 7.0
 */
public enum CustomFieldTypeEnum {
    /**
     * String value
     */
    STRING(false, String.class, "varchar(%length)"),

    /**
     * Date value
     */
    DATE(false, Date.class, "datetime"),

    /**
     * Long value
     */
    LONG(false, Long.class, "bigInt"),

    /**
     * Double value
     */
    DOUBLE(false, Double.class, "numeric(23,12)"),

    /**
     * String value picked from a list of values
     */
    LIST(false, String.class, "varchar(%length)"),

    /**
     * String value picked from a list of values, with possibility of multi select
     */
    CHECKBOX_LIST(false, List.class, StringUtils.EMPTY),

    /**
     * A reference to an entity
     */
    ENTITY(true, EntityReferenceWrapper.class, "bigint"),

    /**
     * A long string value
     */
    TEXT_AREA(false, String.class, "text"),

    /**
     * An embedded entity data
     */
    CHILD_ENTITY(true, EntityReferenceWrapper.class, StringUtils.EMPTY),

    /**
     * Multi value (map) type value
     */
    MULTI_VALUE(true, Map.class, StringUtils.EMPTY),

    /**
     * A boolean value
     */
    BOOLEAN(false, Boolean.class, "boolean default false"),

    /**
     * A reference to an entity
     */
    CUSTOM_TABLE_WRAPPER(true, CustomTableWrapper.class, StringUtils.EMPTY);

    /**
     * Is value stored in a serialized form in DB
     */
    private boolean storedSerialized;

    /**
     * Corresponding class to field type for conversion to json
     */
    private Class dataClass;
    
    private String dataType;

    CustomFieldTypeEnum(boolean storedSerialized, Class dataClass, String dataType) {
        this.storedSerialized = storedSerialized;
        this.dataClass = dataClass;
        this.dataType = dataType;
    }

    public String getLabel() {
        return this.getClass().getSimpleName() + "." + this.name();
    }

    public boolean isStoredSerialized() {
        return storedSerialized;
    }

    public Class getDataClass() {
        return dataClass;
    }
    
    public String getDataType() {
        return dataType;
    }
}