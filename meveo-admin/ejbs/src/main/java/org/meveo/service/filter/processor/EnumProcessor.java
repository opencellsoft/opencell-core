package org.meveo.service.filter.processor;

import org.meveo.admin.exception.FilterException;
import org.meveo.commons.utils.FilteredQueryBuilder;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.filter.PrimitiveFilterCondition;

public class EnumProcessor extends PrimitiveFilterProcessor {

    public static final String PREFIX = "enum:";

    @Override
    public boolean canProccessCondition(PrimitiveFilterCondition condition) {
        return isPrefixInOperand(condition, PREFIX);
    }


    @Override
    public void process(FilteredQueryBuilder queryBuilder, String alias, PrimitiveFilterCondition condition) throws FilterException {
        String enumClassName = (condition.getOperand().substring(PREFIX.length(), condition.getOperand().lastIndexOf(".")));
        String enumValue = condition.getOperand().substring(condition.getOperand().lastIndexOf(".") + 1);
        buildQuery(queryBuilder, alias, condition, enumClassName, enumValue);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void buildQuery(FilteredQueryBuilder queryBuilder, String alias, PrimitiveFilterCondition condition, String enumClassName, String enumValue) {
        Class<? extends Enum> enumClass = null;
        try {
            enumClass = (Class<? extends Enum>) Class.forName(enumClassName);
            String fieldName = condition.getFieldName();
            if (fieldName.indexOf(".") == -1) {
                fieldName = alias + "." + fieldName;
            }
            queryBuilder.addCriterionEntity(fieldName, ReflectionUtils.getEnumFromString(enumClass, enumValue));
        } catch (ClassNotFoundException e) {
            throw new FilterException(e.getMessage());
        }
    }

}
