package org.meveo.service.filter.processor;

import org.apache.commons.validator.routines.BigDecimalValidator;
import org.meveo.admin.exception.FilterException;
import org.meveo.commons.utils.FilteredQueryBuilder;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.filter.FilterParameterTypeEnum;
import org.meveo.model.filter.PrimitiveFilterCondition;

import java.math.BigDecimal;
import java.util.Map;

public class CustomBigDecimalProcessor extends BigDecimalProcessor {

    @Override
    public boolean canProccessCondition(PrimitiveFilterCondition condition) {
        return isPrefixInOperand(condition, FilterParameterTypeEnum.BIG_DECIMAL.getPrefix());
    }

    @Override
    public void process(FilteredQueryBuilder queryBuilder, String alias, PrimitiveFilterCondition condition) throws FilterException {
        Map.Entry<CustomFieldTemplate, Object> customFieldEntry = fetchCustomFieldEntry(queryBuilder.getParameterMap(), condition.getOperand());
        if (customFieldEntry != null) {
            String stringValue = String.valueOf(customFieldEntry.getValue());
            BigDecimal value = BigDecimalValidator.getInstance().validate(stringValue);
            if (value != null) {
                buildQuery(queryBuilder, alias, condition, value);
            }
        }
    }
}
