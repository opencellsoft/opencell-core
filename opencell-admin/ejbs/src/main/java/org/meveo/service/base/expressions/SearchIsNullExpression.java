package org.meveo.service.base.expressions;

import org.meveo.commons.utils.QueryBuilder;

public class SearchIsNullExpression implements Expression {
    private final String tableNameAlias;
    private final String fieldName;

    public SearchIsNullExpression(String tableNameAlias, String fieldName) {
        this.tableNameAlias = tableNameAlias;
        this.fieldName = fieldName;
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
}
