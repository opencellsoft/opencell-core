package org.meveo.service.base.expressions;

import java.util.Arrays;

public class ExpressionParser {

    private String condition;
    private String fieldName;
    private String fieldName2;
    private String[] fieldInfo;

    public ExpressionParser(String[] fieldInfo) {
        this.fieldInfo = fieldInfo;
        this.condition = "eq";
        this.fieldName = fieldInfo[0];
        this.fieldName2 = null;

        if (fieldInfo.length == 2) {
            condition = fieldInfo[0];
            fieldName = fieldInfo[1];
        } else if (fieldInfo.length > 2) {
            condition = fieldInfo[0];
            fieldName = fieldInfo[1];
            this.fieldName2 = fieldInfo[2];
        }
    }

    public String getCondition() {
        return condition;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFieldName2() {
        return fieldName2;
    }

    public String[] getAllFields() {
        return Arrays.copyOfRange(fieldInfo, 1, fieldInfo.length);
    }
}
