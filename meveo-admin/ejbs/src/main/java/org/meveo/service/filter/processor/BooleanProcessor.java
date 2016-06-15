package org.meveo.service.filter.processor;

import org.apache.commons.lang3.BooleanUtils;
import org.meveo.admin.exception.FilterException;
import org.meveo.commons.utils.FilteredQueryBuilder;
import org.meveo.model.filter.PrimitiveFilterCondition;

public class BooleanProcessor extends PrimitiveFilterProcessor {

    public static final String PREFIX = "bool:";

    @Override
    public boolean canProccessCondition(PrimitiveFilterCondition condition) {
        return isPrefixInOperand(condition, PREFIX);
    }

    @Override
    public void process(FilteredQueryBuilder queryBuilder, String alias, PrimitiveFilterCondition condition) throws FilterException {
        Boolean value = BooleanUtils.toBooleanObject(condition.getOperand().substring(PREFIX.length()));
        if (value != null) {
            buildQuery(queryBuilder, condition, value);
        }
    }

    protected void buildQuery(FilteredQueryBuilder queryBuilder, PrimitiveFilterCondition condition, Boolean value) {
        queryBuilder.addBooleanCriterion(condition.getFieldName(), value);
    }
}
