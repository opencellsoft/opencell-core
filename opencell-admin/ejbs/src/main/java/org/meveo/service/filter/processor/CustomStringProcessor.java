package org.meveo.service.filter.processor;

import java.util.Map;

import org.meveo.admin.exception.FilterException;
import org.meveo.commons.utils.FilteredQueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.filter.FilterParameterTypeEnum;
import org.meveo.model.filter.PrimitiveFilterCondition;

public class CustomStringProcessor extends StringProcessor {

    @Override
    public boolean canProccessCondition(PrimitiveFilterCondition condition) {
        return isPrefixInOperand(condition, FilterParameterTypeEnum.STRING.getPrefix());
    }

    @Override
    public void process(FilteredQueryBuilder queryBuilder, String alias, PrimitiveFilterCondition condition) throws FilterException {
        Map.Entry<CustomFieldTemplate, Object> customFieldEntry = fetchCustomFieldEntry(queryBuilder.getParameterMap(), condition.getOperand());
        if(customFieldEntry != null){
            buildQuery(queryBuilder, alias, condition, String.valueOf(customFieldEntry.getValue()));
        } else if(!StringUtils.isBlank(condition.getOperand())) {
			buildQuery(queryBuilder, alias, condition, getParameterValue(condition.getOperand()));
        }
    }
    
    private String getParameterValue(String operand) {
    	return operand.substring(FilterParameterTypeEnum.STRING.getPrefix().length() + 1);    	
    }
}
