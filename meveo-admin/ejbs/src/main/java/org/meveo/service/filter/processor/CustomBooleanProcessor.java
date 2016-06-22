package org.meveo.service.filter.processor;

import org.apache.commons.lang3.BooleanUtils;
import org.meveo.admin.exception.FilterException;
import org.meveo.commons.utils.FilteredQueryBuilder;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.filter.FilterParameterTypeEnum;
import org.meveo.model.filter.PrimitiveFilterCondition;

import java.util.Map;

public class CustomBooleanProcessor extends BooleanProcessor {

    @Override
    public boolean canProccessCondition(PrimitiveFilterCondition condition) {
        return isPrefixInOperand(condition, FilterParameterTypeEnum.BOOLEAN.getPrefix());
    }

    @Override
    public void process(FilteredQueryBuilder queryBuilder, String alias, PrimitiveFilterCondition condition) throws FilterException {
        Map.Entry<CustomFieldTemplate, Object> customFieldEntry = fetchCustomFieldEntry(queryBuilder.getParameterMap(), condition.getOperand());
        if (customFieldEntry != null) {
            String stringValue = String.valueOf(customFieldEntry.getValue());
            Boolean value = BooleanUtils.toBooleanObject(stringValue);
            if (value != null) {
                buildQuery(queryBuilder, condition, value);
            }
        }
    }
}
