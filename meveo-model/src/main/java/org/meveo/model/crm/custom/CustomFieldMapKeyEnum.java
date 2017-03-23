package org.meveo.model.crm.custom;

public enum CustomFieldMapKeyEnum {
    /**
     * String
     */
    STRING,

    /**
     * Range of numbers
     */
    RON;

    public String getLabel() {
        return this.getClass().getSimpleName() + "." + this.name();
    }
}