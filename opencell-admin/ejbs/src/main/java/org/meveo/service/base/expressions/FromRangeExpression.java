package org.meveo.service.base.expressions;

import org.meveo.commons.utils.QueryBuilder;

public class FromRangeExpression implements Expression {

    private String tableNameAlias;
    private final String fieldName;
    private final Object value;

    public FromRangeExpression(String tableNameAlias, String fieldName, Object value) {
        this.tableNameAlias = tableNameAlias;
        this.fieldName = fieldName;
        this.value = value;
    }

    public String tableNameAlias() {
        return tableNameAlias;
    }

    public String fieldName() {
        return fieldName;
    }

    public Object value() {
        return value;
    }

    @Override
    public void addFilters(QueryBuilder queryBuilder) {
        queryBuilder.accept(this);
    }
}
