package org.meveo.service.filter.processor;

import java.math.BigDecimal;

import org.apache.commons.validator.routines.BigDecimalValidator;
import org.meveo.admin.exception.FilterException;
import org.meveo.commons.utils.FilteredQueryBuilder;
import org.meveo.model.filter.PrimitiveFilterCondition;

public class BigDecimalProcessor extends PrimitiveFilterProcessor {

    @Override
    public boolean canProccessCondition(PrimitiveFilterCondition condition) {
        return isOfNumberType(condition, BigDecimalValidator.getInstance());
    }

    @Override
    public void process(FilteredQueryBuilder queryBuilder, String alias, PrimitiveFilterCondition condition) throws FilterException {
        BigDecimal value = BigDecimalValidator.getInstance().validate(condition.getOperand());
        if (value != null) {
            buildQuery(queryBuilder, alias, condition, value);
        }
    }

    protected void buildQuery(FilteredQueryBuilder queryBuilder, String alias, PrimitiveFilterCondition condition, BigDecimal value) {
        String fieldName = condition.getFieldName();
        if (fieldName.indexOf(".") == -1) {
            fieldName = alias + "." + fieldName;
        }
        queryBuilder.addCriterion(fieldName, condition.getOperator(), value, true);
    }
}
