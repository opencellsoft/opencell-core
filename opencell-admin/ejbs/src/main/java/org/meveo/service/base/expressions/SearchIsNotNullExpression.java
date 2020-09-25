package org.meveo.service.base.expressions;

import org.meveo.commons.utils.QueryBuilder;

public class SearchIsNotNullExpression extends SearchIsNullExpression {

    public SearchIsNotNullExpression(String tableNameAlias, String fieldName) {
        super(tableNameAlias, fieldName);
    }

    @Override
    public void addFilters(QueryBuilder queryBuilder) {
        queryBuilder.accept(this);
    }
}
