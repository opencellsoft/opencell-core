package org.meveo.util.view;

public class FieldInformation {

    public enum FieldTypeEnum {
        Text, Boolean, Date, Enum, Number, Entity, List, Map;
    }

    public enum FieldNumberTypeEnum {
        Integer, Double, Long, Byte, Short, Float, BigDecimal
    }

    protected FieldTypeEnum fieldType;

    protected FieldNumberTypeEnum numberType;

    protected String numberConverter;

    protected Object[] enumListValues;

    @SuppressWarnings("rawtypes")
    protected Class fieldGenericsType;

    public FieldTypeEnum getFieldType() {
        return fieldType;
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
}