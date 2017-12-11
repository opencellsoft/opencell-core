package org.meveo.model.crm.custom;

/**
 * Defines map or matrix key field and matrix value field data types
 * 
 * DO NOT CHANGE THE ORDER as in db order position instead of text value is stored
 * 
 * @author Andrius karpavicius
 */
public enum CustomFieldMapKeyEnum {

    /**
     * String
     */
    STRING(true, true),

    /**
     * Long number
     */
    LONG(false, true),

    /**
     * Double number
     */
    DOUBLE(false, true),

    /**
     * Range of numbers
     */
    RON(true, false);

    private boolean keyUse;

    private boolean valueUse;

    public static final CustomFieldMapKeyEnum[] enumValuesForKey = { STRING, RON };
    public static final CustomFieldMapKeyEnum[] enumValuesForValue = { STRING, LONG, DOUBLE };

    private CustomFieldMapKeyEnum(boolean keyUse, boolean valueUse) {
        this.keyUse = keyUse;
        this.valueUse = valueUse;
    }

    public String getLabel() {
        return this.getClass().getSimpleName() + "." + this.name();
    }

    public boolean isKeyUse() {
        return keyUse;
    }

    public boolean isValueUse() {
        return valueUse;
    }
}