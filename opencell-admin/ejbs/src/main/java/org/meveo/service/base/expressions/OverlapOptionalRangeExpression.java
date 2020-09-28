package org.meveo.service.base.expressions;

import org.meveo.commons.utils.QueryBuilder;

import java.util.List;

public class OverlapOptionalRangeExpression implements Expression {
    private final String tableNameAlias;
    private final String fieldName;
    private final String fieldName2;
    private final Object value;

    public OverlapOptionalRangeExpression(String tableNameAlias, String fieldName, String fieldName2, Object value) {
        this.tableNameAlias = tableNameAlias;
        this.fieldName = fieldName;
        this.fieldName2 = fieldName2;
        this.value = value;
    }

    @Override
    public void addFilters(QueryBuilder queryBuilder) {
        queryBuilder.accept(this);
    }

    public Object valueFrom() {
        if (value.getClass().isArray()) {
            return ((Object[]) value)[0];

        } else if (value instanceof List) {
            return ((List) value).get(0);
        }
        return null;
    }

    public Object toValue() {
        if (value.getClass().isArray()) {
            return ((Object[]) value)[1];

        } else if (value instanceof List) {
            return ((List) value).get(1);
        }
        return null;
    }

    public String getTableNameAlias() {
        return tableNameAlias;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFieldName2() {
        return fieldName2;
    }

    public Object getValue() {
        return value;
    }
}
