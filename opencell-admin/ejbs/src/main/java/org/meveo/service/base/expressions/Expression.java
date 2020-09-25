package org.meveo.service.base.expressions;

import org.meveo.commons.utils.QueryBuilder;

public interface Expression {

    void addFilters(QueryBuilder queryBuilder);

}
