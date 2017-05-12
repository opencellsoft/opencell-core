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