package org.meveo.model.crm.custom;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Defines a column inside a custom field key and values matrix. e.g. as table column.
 */
@Embeddable
@Access(AccessType.FIELD)
public class CustomFieldMatrixColumn implements Serializable {

    private static final long serialVersionUID = 4307211518190785915L;

    /**
     * How column will be used - as key or as value
     * 
     * DO NOT CHANGE THE ORDER as in db order position instead of text value is stored
     */
    public enum CustomFieldColumnUseEnum {
        /**
         * Column is used as a key
         */
        USE_KEY,

        /**
         * Column is used as a value
         */
        USE_VALUE;
    }

    /**
     * Is column to be used as key or as value field
     */
    // @Column(name = "columnUse", nullable = false)
    // @Enumerated(EnumType.ORDINAL)
    @NotNull
    private CustomFieldColumnUseEnum columnUse;

    /**
     * Column ordering position
     */
    private int position;

    /**
     * Column code
     */
    // @Column(name = "code", nullable = false, length = 20)
    @Size(max = 20)
    @NotNull
    private String code;

    /**
     * Label
     */
    // @Column(name = "label", nullable = false, length = 50)
    @Size(max = 50)
    @NotNull
    private String label;

    /**
     * Data entry type
     */
    // @Column(name = "key_type", nullable = false, length = 10)
    // @Enumerated(EnumType.ORDINAL)
    @NotNull
    private CustomFieldMapKeyEnum keyType;

    public CustomFieldMatrixColumn() {

    }

    public CustomFieldMatrixColumn(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public CustomFieldMapKeyEnum getKeyType() {
        return keyType;
    }

    public void setKeyType(CustomFieldMapKeyEnum keyType) {
        this.keyType = keyType;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof CustomFieldMatrixColumn)) {
            return false;
        }

        CustomFieldMatrixColumn other = (CustomFieldMatrixColumn) obj;

        if (code == null) {
            if (other.getCode() != null) {
                return false;
            }
        } else if (!code.equals(other.getCode())) {
            return false;
        }
        return true;
    }

    public CustomFieldColumnUseEnum getColumnUse() {
        return columnUse;
    }

    public void setColumnUse(CustomFieldColumnUseEnum columnUse) {
        this.columnUse = columnUse;
    }

    public boolean isColumnForKey() {
        return columnUse == CustomFieldColumnUseEnum.USE_KEY;
    }
}