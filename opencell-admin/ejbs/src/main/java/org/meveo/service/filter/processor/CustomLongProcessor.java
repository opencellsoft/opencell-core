package org.meveo.service.filter.processor;

import java.util.Map;

import org.apache.commons.validator.routines.LongValidator;
import org.meveo.admin.exception.FilterException;
import org.meveo.commons.utils.FilteredQueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.filter.FilterParameterTypeEnum;
import org.meveo.model.filter.PrimitiveFilterCondition;

public class CustomLongProcessor extends LongProcessor {

    @Override
    public boolean canProccessCondition(PrimitiveFilterCondition condition) {
        return isPrefixInOperand(condition, FilterParameterTypeEnum.LONG.getPrefix());
    }

    @Override
    public void process(FilteredQueryBuilder queryBuilder, String alias, PrimitiveFilterCondition condition) throws FilterException {
        Map.Entry<CustomFieldTemplate, Object> customFieldEntry = fetchCustomFieldEntry(queryBuilder.getParameterMap(), condition.getOperand());
        if(customFieldEntry != null ){
            Long value = LongValidator.getInstance().validate(String.valueOf(customFieldEntry.getValue()));
            if (value != null) {
                buildQuery(queryBuilder, alias, condition, value);
            }
        } else if(!StringUtils.isBlank(condition.getOperand())) {
			buildQuery(queryBuilder, alias, condition, getParameterValue(condition.getOperand()));
        }
    }
    
    private Long getParameterValue(String operand) {
    	return Long.valueOf(operand.substring(FilterParameterTypeEnum.LONG.getPrefix().length() + 1));    	
    }
}
