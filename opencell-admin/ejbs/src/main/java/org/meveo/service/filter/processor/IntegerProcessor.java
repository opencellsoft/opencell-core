package org.meveo.service.filter.processor;

import org.apache.commons.validator.routines.IntegerValidator;
import org.meveo.admin.exception.FilterException;
import org.meveo.commons.utils.FilteredQueryBuilder;
import org.meveo.model.filter.PrimitiveFilterCondition;

public class IntegerProcessor extends PrimitiveFilterProcessor {

    @Override
    public boolean canProccessCondition(PrimitiveFilterCondition condition) {
        return isOfNumberType(condition, IntegerValidator.getInstance());
    }

    @Override
    public void process(FilteredQueryBuilder queryBuilder, String alias, PrimitiveFilterCondition condition) throws FilterException {
        Integer value = IntegerValidator.getInstance().validate(condition.getOperand());
        if (value != null) {
            buildQuery(queryBuilder, alias, condition, value);
        }
    }

    protected void buildQuery(FilteredQueryBuilder queryBuilder, String alias, PrimitiveFilterCondition condition, Integer value) {
        String fieldName = condition.getFieldName();
        if (fieldName.indexOf(".") == -1) {
            fieldName = alias + "." + fieldName;
        }
        queryBuilder.addCriterion(fieldName, condition.getOperator(), value, true);
    }
}
