package org.meveo.model.crm.custom;

/**
 * How custom field values are stored
 * 
 * @author Andrius Karpavicius
 */
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

    /**
     * @return Message key for display in GUI
     */
    public String getLabel() {
        return this.getClass().getSimpleName() + "." + this.name();
    }
}