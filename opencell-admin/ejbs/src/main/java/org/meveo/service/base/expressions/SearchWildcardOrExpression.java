package org.meveo.service.base.expressions;

import org.meveo.commons.utils.QueryBuilder;

public class SearchWildcardOrExpression implements Expression {
    private final String tableNameAlias;
    private final String[] fields;
    private final Object value;

    public SearchWildcardOrExpression(String tableNameAlias, String[] fields, Object value) {
        this.tableNameAlias = tableNameAlias;
        this.fields = fields;
        this.value = value;
    }

    @Override
    public void addFilters(QueryBuilder queryBuilder) {
        queryBuilder.accept(this);
    }

    public String getTableNameAlias() {
        return tableNameAlias;
    }

    public String[] getFields() {
        return fields;
    }

    public Object getValue() {
        return value;
    }
}
