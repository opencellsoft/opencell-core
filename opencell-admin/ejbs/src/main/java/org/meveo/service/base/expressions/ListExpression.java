package org.meveo.service.base.expressions;

import org.meveo.commons.utils.QueryBuilder;

public class ListExpression implements Expression {

    private final String tableNameAlias;
    private final String fieldName;
    private final Object value;

    public ListExpression(String tableNameAlias, String fieldName, Object value) {
        this.tableNameAlias = tableNameAlias;
        this.fieldName = fieldName;
        this.value = value;
    }

    @Override
    public void addFilters(QueryBuilder queryBuilder) {
        queryBuilder.accept(this);
    }

    public String tableNameAlias(){
        return tableNameAlias;
    }

    public String fieldName() {
        return fieldName;
    }

    public Object value() {
        return value;
    }
}
