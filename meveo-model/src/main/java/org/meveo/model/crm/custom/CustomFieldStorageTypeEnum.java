package org.meveo.model.crm.custom;

public enum CustomFieldStorageTypeEnum {
    SINGLE("customFieldStorageTypeEnum.SINGLE"), LIST("customFieldStorageTypeEnum.LIST"), MAP("customFieldStorageTypeEnum.MAP"), MATRIX("customFieldStorageTypeEnum.MATRIX");

    private String label;

    CustomFieldStorageTypeEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }
}