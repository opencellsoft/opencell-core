package org.meveo.service.base.expressions;

import org.meveo.commons.utils.QueryBuilder;

public class MinMaxOptionalRangeExpression extends MinMaxRangeExpression {
    public MinMaxOptionalRangeExpression(String tableNameAlias, String fieldName, String fieldName2, Object value) {
        super(tableNameAlias, fieldName, fieldName2, value);
    }

    @Override
    public void addFilters(QueryBuilder queryBuilder) {
        queryBuilder.accept(this);
    }
}
