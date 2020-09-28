package org.meveo.service.base.expressions;

import org.meveo.commons.utils.QueryBuilder;

public class MinMaxRangeExpression implements Expression {
    private final String tableNameAlias;
    private final String fieldName;
    private final String fieldName2;
    private final Object value;

    public MinMaxRangeExpression(String tableNameAlias, String fieldName, String fieldName2, Object value) {
        this.tableNameAlias = tableNameAlias;
        this.fieldName = fieldName;
        this.fieldName2 = fieldName2;
        this.value = value;
    }

    @Override
    public void addFilters(QueryBuilder queryBuilder) {
        queryBuilder.accept(this);
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
