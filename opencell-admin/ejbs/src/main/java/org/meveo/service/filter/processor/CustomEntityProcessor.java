package org.meveo.service.filter.processor;

import java.util.Map;

import org.meveo.admin.exception.FilterException;
import org.meveo.commons.utils.FilteredQueryBuilder;
import org.meveo.model.BusinessEntity;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.filter.FilterParameterTypeEnum;
import org.meveo.model.filter.PrimitiveFilterCondition;

public class CustomEntityProcessor extends PrimitiveFilterProcessor {

    @Override
    public boolean canProccessCondition(PrimitiveFilterCondition condition) {
        return isPrefixInOperand(condition, FilterParameterTypeEnum.ENTITY.getPrefix());
    }

    @Override
    public void process(FilteredQueryBuilder queryBuilder, String alias, PrimitiveFilterCondition condition) throws FilterException {
        Map.Entry<CustomFieldTemplate, Object> customFieldEntry = fetchCustomFieldEntry(queryBuilder.getParameterMap(), condition.getOperand());
        if (customFieldEntry != null) {
            BusinessEntity businessEntity = (BusinessEntity) customFieldEntry.getValue();
            if(businessEntity != null){
                buildQuery(queryBuilder, alias, condition, businessEntity);
            }
        }
    }

    protected void buildQuery(FilteredQueryBuilder queryBuilder, String alias, PrimitiveFilterCondition condition, BusinessEntity value) {
        String fieldName = condition.getFieldName();
        if (condition.getFieldName().indexOf(".") == -1) {
            fieldName = alias + "." + fieldName;
        }
        if(value != null){
            queryBuilder.addCriterionEntity(fieldName, value, condition.getOperator());
        }
    }
}
