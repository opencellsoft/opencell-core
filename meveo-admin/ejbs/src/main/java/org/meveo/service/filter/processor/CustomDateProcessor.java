package org.meveo.service.filter.processor;

import java.util.Date;
import java.util.Map;

import org.meveo.admin.exception.FilterException;
import org.meveo.commons.utils.FilteredQueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.filter.FilterParameterTypeEnum;
import org.meveo.model.filter.PrimitiveFilterCondition;
import org.meveo.model.shared.DateUtils;

public class CustomDateProcessor extends DateProcessor {

    @Override
    public boolean canProccessCondition(PrimitiveFilterCondition condition) {
        return isPrefixInOperand(condition, FilterParameterTypeEnum.DATE.getPrefix());
    }

    @Override
    public void process(FilteredQueryBuilder queryBuilder, String alias, PrimitiveFilterCondition condition) throws FilterException {
        Map.Entry<CustomFieldTemplate, Object> customFieldEntry = fetchCustomFieldEntry(queryBuilder.getParameterMap(), condition.getOperand());
        if (customFieldEntry != null) {
            buildQuery(queryBuilder, condition, (Date) customFieldEntry.getValue());
        } else if(!StringUtils.isBlank(condition.getOperand())) {
			buildQuery(queryBuilder, condition, getParameterValue(condition.getOperand()));
        }
    }
    
	private Date getParameterValue(String operand) {
    	return DateUtils.parseDateWithPattern(operand.substring(FilterParameterTypeEnum.DATE.getPrefix().length() + 1), DateUtils.DATE_PATTERN);    	
    }
}
