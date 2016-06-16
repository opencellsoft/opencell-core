package org.meveo.service.filter.processor;

import org.meveo.admin.exception.FilterException;
import org.meveo.commons.utils.FilteredQueryBuilder;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.filter.PrimitiveFilterCondition;

public class StringProcessor extends PrimitiveFilterProcessor {

    @Override
    public boolean canProccessCondition(PrimitiveFilterCondition condition) {
        // This is the default condition. It must not match any condition.
        return false;
    }

    @Override
    public void process(FilteredQueryBuilder queryBuilder, String alias, PrimitiveFilterCondition condition) throws FilterException {
        if (condition != null) {
            buildQuery(queryBuilder, alias, condition, condition.getOperand());
        }
    }

    protected void buildQuery(FilteredQueryBuilder queryBuilder, String alias, PrimitiveFilterCondition condition, String value) {
        String fieldName = condition.getFieldName();
        if (condition.getFieldName().indexOf(".") == -1) {
            fieldName = alias + "." + fieldName;
        }
        if ("LIKE".equalsIgnoreCase(condition.getOperator())) {
            queryBuilder.like(fieldName, value, QueryBuilder.QueryLikeStyleEnum.MATCH_BEGINNING, true);
        } else {
            queryBuilder.addCriterion(fieldName, condition.getOperator(), value, true);
        }
    }
}
