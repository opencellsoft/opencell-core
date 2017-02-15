package org.meveo.service.filter.processor;

import java.util.Map;

import org.apache.commons.validator.routines.IntegerValidator;
import org.meveo.admin.exception.FilterException;
import org.meveo.commons.utils.FilteredQueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.filter.FilterParameterTypeEnum;
import org.meveo.model.filter.PrimitiveFilterCondition;

public class CustomIntegerProcessor extends IntegerProcessor {

    @Override
    public boolean canProccessCondition(PrimitiveFilterCondition condition) {
        return isPrefixInOperand(condition, FilterParameterTypeEnum.INTEGER.getPrefix());
    }

    @Override
    public void process(FilteredQueryBuilder queryBuilder, String alias, PrimitiveFilterCondition condition) throws FilterException {
        Map.Entry<CustomFieldTemplate, Object> customFieldEntry = fetchCustomFieldEntry(queryBuilder.getParameterMap(), condition.getOperand());
        if(customFieldEntry != null){
            Integer value = IntegerValidator.getInstance().validate(String.valueOf(customFieldEntry.getValue()));
            if (value != null) {
                buildQuery(queryBuilder, alias, condition, value);
            }
        } else if(!StringUtils.isBlank(condition.getOperand())) {
			buildQuery(queryBuilder, alias, condition, getParameterValue(condition.getOperand()));
        }
    }
    
    private Integer getParameterValue(String operand) {
    	return Integer.valueOf(operand.substring(FilterParameterTypeEnum.INTEGER.getPrefix().length() + 1));    	
    }
}
