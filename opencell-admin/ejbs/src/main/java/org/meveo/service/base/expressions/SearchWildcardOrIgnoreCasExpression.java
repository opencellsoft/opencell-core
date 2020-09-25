package org.meveo.service.base.expressions;

import org.meveo.commons.utils.QueryBuilder;

public class SearchWildcardOrIgnoreCasExpression extends SearchWildcardOrExpression {
    public SearchWildcardOrIgnoreCasExpression(String tableNameAlias, String[] fields, Object value) {
        super(tableNameAlias, fields, value);

    }

    @Override
    public void addFilters(QueryBuilder queryBuilder) {
        queryBuilder.accept(this);
    }
}
