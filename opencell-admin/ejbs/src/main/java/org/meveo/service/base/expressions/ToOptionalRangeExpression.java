package org.meveo.service.base.expressions;

import org.meveo.commons.utils.QueryBuilder;

public class ToOptionalRangeExpression extends ToRangeExpression {
    public ToOptionalRangeExpression(String tableNameAlias, String fieldName, Object value) {
        super(tableNameAlias, fieldName, value);
    }

    @Override
    public void addFilters(QueryBuilder queryBuilder) {
        queryBuilder.accept(this);
    }
}
