package org.meveo.service.base.expressions;

import java.util.Arrays;

import org.meveo.service.base.PersistenceService;

public class ExpressionParser {

    private String condition;
    private String fieldName;
    private String fieldName2;
    private String[] fieldInfo;

    public ExpressionParser(String[] fieldInfo) {

        // "AND" is just a prefix to allow multiple conditions on the same field
        if (fieldInfo[0].startsWith(PersistenceService.SEARCH_AND)) {
            fieldInfo = Arrays.copyOfRange(fieldInfo, 1, fieldInfo.length);
        }

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
        if (fieldInfo.length > 1) {
            return Arrays.copyOfRange(fieldInfo, 1, fieldInfo.length);
        } else {
            return fieldInfo;
        }
    }
}