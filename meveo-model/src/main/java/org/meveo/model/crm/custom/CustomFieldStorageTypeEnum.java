package org.meveo.model.crm.custom;

public enum CustomFieldStorageTypeEnum {

    /**
     * Single value
     */
    SINGLE,

    /**
     * A list of values
     */
    LIST,

    /**
     * A map of values
     */
    MAP,

    /**
     * A matrix of values
     */
    MATRIX;

    public String getLabel() {
        return this.getClass().getSimpleName() + "." + this.name();
    }
}