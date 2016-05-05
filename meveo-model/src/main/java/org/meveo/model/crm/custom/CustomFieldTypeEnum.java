package org.meveo.model.crm.custom;

import java.util.Date;

import org.meveo.model.crm.EntityReferenceWrapper;

public enum CustomFieldTypeEnum {
    /**
     * String value
     */
    STRING("customFieldTypeEnum.string", false, String.class),

    /**
     * Date value
     */
    DATE("customFieldTypeEnum.date", false, Date.class),

    /**
     * Long value
     */
    LONG("customFieldTypeEnum.long", false, Long.class),

    /**
     * Double value
     */
    DOUBLE("customFieldTypeEnum.double", false, Double.class),

    /**
     * String value picked from a list of values
     */
    LIST("customFieldTypeEnum.list", false, String.class),

    /**
     * A reference to an entity
     */
    ENTITY("customFieldTypeEnum.entity", true, EntityReferenceWrapper.class),

    /**
     * A long string value
     */
    TEXT_AREA("customFieldTypeEnum.textArea", false, String.class),

    /**
     * An embedded entity data
     */
    CHILD_ENTITY("customFieldTypeEnum.childEntity", true, EntityReferenceWrapper.class);

    /**
     * Label for display in GUI
     */
    private String label;

    /**
     * Is value stored in a serialized form in DB
     */
    private boolean storedSerialized;

    /**
     * Corresponding class to field type for conversion to json
     */
    @SuppressWarnings("rawtypes")
    private Class dataClass;

    CustomFieldTypeEnum(String label, boolean storedSerialized, @SuppressWarnings("rawtypes") Class dataClass) {
        this.label = label;
        this.storedSerialized = storedSerialized;
        this.dataClass = dataClass;
    }

    public String getLabel() {
        return this.label;
    }

    public boolean isStoredSerialized() {
        return storedSerialized;
    }

    @SuppressWarnings("rawtypes")
    public Class getDataClass() {
        return dataClass;
    }
}