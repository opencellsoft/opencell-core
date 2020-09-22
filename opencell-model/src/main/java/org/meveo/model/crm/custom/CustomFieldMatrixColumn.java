/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.model.crm.custom;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
    @Column(name = "column_use", nullable = false)
    @Enumerated(value = EnumType.ORDINAL)
    @NotNull
    private CustomFieldColumnUseEnum columnUse;

    /**
     * Column ordering position
     */
    @Column(name = "position", nullable = false)
    private int position;

    /**
     * Column code
     */
    @Column(name = "code", nullable = false, length = 20)
    @Size(max = 20)
    @NotNull
    private String code;

    /**
     * Label
     */
    @Column(name = "label", nullable = false, length = 50)
    @Size(max = 50)
    @NotNull
    private String label;

    /**
     * Data entry type
     */
    @Column(name = "key_type", nullable = false, length = 10)
    @Enumerated(value = EnumType.ORDINAL)
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

        if (this.getCode() == null) {
            if (other.getCode() != null) {
                return false;
            }
        } else if (!this.getCode().equals(other.getCode())) {
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