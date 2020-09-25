package org.meveo.service.base.expressions;

import org.meveo.commons.utils.QueryBuilder;

public class DummyExpression implements Expression {

    @Override
    public void addFilters(QueryBuilder queryBuilder) {
        // do nothing
    }
}
