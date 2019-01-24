package org.meveo.model.crm.custom;

import java.util.Date;
import java.util.Map;

import org.meveo.model.crm.EntityReferenceWrapper;

public enum CustomFieldTypeEnum {
    /**
     * String value
     */
    STRING(false, String.class),

    /**
     * Date value
     */
    DATE(false, Date.class),

    /**
     * Long value
     */
    LONG(false, Long.class),

    /**
     * Double value
     */
    DOUBLE(false, Double.class),

    /**
     * String value picked from a list of values
     */
    LIST(false, String.class),

    /**
     * A reference to an entity
     */
    ENTITY(true, EntityReferenceWrapper.class),

    /**
     * A long string value
     */
    TEXT_AREA(false, String.class),

    /**
     * An embedded entity data
     */
    CHILD_ENTITY(true, EntityReferenceWrapper.class),

    /**
     * Multi value (map) type value
     */
    MULTI_VALUE(true, Map.class),

    /**
     * A boolean value
     */
    BOOLEAN(false, Boolean.class);

    /**
     * Is value stored in a serialized form in DB
     */
    private boolean storedSerialized;

    /**
     * Corresponding class to field type for conversion to json
     */
    @SuppressWarnings("rawtypes")
    private Class dataClass;

    CustomFieldTypeEnum(boolean storedSerialized, @SuppressWarnings("rawtypes") Class dataClass) {
        this.storedSerialized = storedSerialized;
        this.dataClass = dataClass;
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
}