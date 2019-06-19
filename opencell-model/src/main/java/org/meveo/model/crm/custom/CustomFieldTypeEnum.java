package org.meveo.model.crm.custom;

import org.apache.commons.lang3.StringUtils;
import org.meveo.model.crm.EntityReferenceWrapper;

import java.util.Date;
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
    DOUBLE(false, Double.class, "numeric(23, 12)"),

    /**
     * String value picked from a list of values
     */
    LIST(false, String.class, "varchar(%length)"),

    /**
     * A reference to an entity
     */
    ENTITY(true, EntityReferenceWrapper.class, StringUtils.EMPTY),

    /**
     * A long string value
     */
    TEXT_AREA(false, String.class, StringUtils.EMPTY),

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
    BOOLEAN(false, Boolean.class, "boolean default false");

    /**
     * Is value stored in a serialized form in DB
     */
    private boolean storedSerialized;

    /**
     * Corresponding class to field type for conversion to json
     */
    @SuppressWarnings("rawtypes")
    private Class dataClass;
    
    private String dataType;

    CustomFieldTypeEnum(boolean storedSerialized, @SuppressWarnings("rawtypes") Class dataClass, String dataType) {
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

    @SuppressWarnings("rawtypes")
    public Class getDataClass() {
        return dataClass;
    }
    
    public String getDataType() {
        return dataType;
    }
}