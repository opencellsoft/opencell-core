package org.meveo.service.base.expressions;

import org.meveo.commons.utils.QueryBuilder;

public class SearchSqlExpression implements Expression {
    private Object value;

    public SearchSqlExpression(Object value) {
        this.value = value;
    }

    @Override
    public void addFilters(QueryBuilder queryBuilder) {
        queryBuilder.accept(this);
    }


    public boolean valueIsAnArray() {
        return value.getClass().isArray();
    }

    public Object getValue() {
        return value;
    }
}
