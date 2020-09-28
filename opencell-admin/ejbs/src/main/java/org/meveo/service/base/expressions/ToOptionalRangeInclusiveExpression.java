package org.meveo.service.base.expressions;

import org.meveo.commons.utils.QueryBuilder;

public class ToOptionalRangeInclusiveExpression extends ToRangeExpression {
    public ToOptionalRangeInclusiveExpression(String tableNameAlias, String fieldName, Object value) {
        super(tableNameAlias, fieldName, value);
    }

    @Override
    public void addFilters(QueryBuilder queryBuilder) {
        queryBuilder.accept(this);
    }
}
