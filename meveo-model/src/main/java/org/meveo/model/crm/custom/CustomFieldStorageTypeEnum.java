package org.meveo.model.crm.custom;

public enum CustomFieldStorageTypeEnum {

    /**
     * Single value
     */
    SINGLE("customFieldStorageTypeEnum.SINGLE"),

    /**
     * A list of values
     */
    LIST("customFieldStorageTypeEnum.LIST"),

    /**
     * A map of values
     */
    MAP("customFieldStorageTypeEnum.MAP"),

    /**
     * A matrix of values
     */
    MATRIX("customFieldStorageTypeEnum.MATRIX");

    private String label;

    CustomFieldStorageTypeEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }
}