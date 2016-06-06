package org.meveo.service.filter.processor;

import org.apache.commons.validator.routines.IntegerValidator;
import org.meveo.admin.exception.FilterException;
import org.meveo.commons.utils.FilteredQueryBuilder;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.filter.PrimitiveFilterCondition;

import java.util.Map;

public class CustomIntegerProcessor extends IntegerProcessor {

    @Override
    public boolean canProccessCondition(PrimitiveFilterCondition condition) {
        return isPrefixInOperand(condition, "cfInteger:");
    }

    @Override
    public void process(FilteredQueryBuilder queryBuilder, String alias, PrimitiveFilterCondition condition) throws FilterException {
        Map.Entry<CustomFieldTemplate, Object> customFieldEntry = fetchCustomFieldEntry(queryBuilder.getParameterMap(), condition.getOperand());
        if(customFieldEntry != null){
            Integer value = IntegerValidator.getInstance().validate(String.valueOf(customFieldEntry.getValue()));
            if (value != null) {
                buildQuery(queryBuilder, alias, condition, value);
            }
        }
    }
}
