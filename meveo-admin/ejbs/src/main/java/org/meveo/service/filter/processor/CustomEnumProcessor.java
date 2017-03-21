package org.meveo.service.filter.processor;

import java.util.Map;

import org.meveo.admin.exception.FilterException;
import org.meveo.commons.utils.FilteredQueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.filter.FilterParameterTypeEnum;
import org.meveo.model.filter.PrimitiveFilterCondition;

public class CustomEnumProcessor extends EnumProcessor {

    @Override
    public boolean canProccessCondition(PrimitiveFilterCondition condition) {
        return isPrefixInOperand(condition, FilterParameterTypeEnum.ENUM.getPrefix());
    }

    @Override
    public void process(FilteredQueryBuilder queryBuilder, String alias, PrimitiveFilterCondition condition) throws FilterException {
        Map.Entry<CustomFieldTemplate, Object> customFieldEntry = fetchCustomFieldEntry(queryBuilder.getParameterMap(), condition.getOperand());
        if (customFieldEntry != null) {
            String stringValue = String.valueOf(customFieldEntry.getValue());
            String enumClassName = stringValue.substring(0, stringValue.lastIndexOf("."));
            String enumValue = stringValue.substring(stringValue.lastIndexOf(".") + 1);
            buildQuery(queryBuilder, alias, condition, enumClassName, enumValue);
        } else if(!StringUtils.isBlank(condition.getOperand())) {
        	String stringValue = condition.getOperand().substring(FilterParameterTypeEnum.ENUM.getPrefix().length() + 1);
            String enumClassName = stringValue.substring(0, stringValue.lastIndexOf("."));
            String enumValue = stringValue.substring(stringValue.lastIndexOf(".") + 1);
            
			buildQuery(queryBuilder, alias, condition, enumClassName, enumValue);
        }
    }
}
