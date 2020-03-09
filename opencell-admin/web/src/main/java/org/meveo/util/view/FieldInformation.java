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

package org.meveo.util.view;

public class FieldInformation {

    public enum FieldTypeEnum {
        Text, Boolean, Date, DatePeriod, Enum, Number, Entity, List, Map, Image;
    }

    public enum FieldNumberTypeEnum {
        Integer, Double, Long, Byte, Short, Float, BigDecimal
    }

    protected FieldTypeEnum fieldType;

    protected Integer maxLength;
    
    protected FieldNumberTypeEnum numberType;

    protected String numberConverter;

    protected Object[] enumListValues;
    
    protected String enumClassname;

    @SuppressWarnings("rawtypes")
    protected Class fieldGenericsType;
    
    protected boolean required;

    public FieldTypeEnum getFieldType() {
        return fieldType;
    }
    
    public Integer getMaxLength() {
        return maxLength;
    }

    public String getNumberConverter() {
        return numberConverter;
    }

    public Object[] getEnumListValues() {
        return enumListValues;
    }

    public FieldNumberTypeEnum getNumberType() {
        return numberType;
    }

    @SuppressWarnings("rawtypes")
    public Class getFieldGenericsType() {
        return fieldGenericsType;
    }
    
    public String getEnumClassname() {
        return enumClassname;
    }
    
    public boolean isRequired() {
        return required;
    }
}