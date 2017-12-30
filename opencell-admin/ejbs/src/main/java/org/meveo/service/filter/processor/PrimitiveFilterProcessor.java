package org.meveo.service.filter.processor;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.validator.routines.AbstractNumberValidator;
import org.meveo.admin.exception.FilterException;
import org.meveo.commons.utils.FilteredQueryBuilder;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.filter.FilterCondition;
import org.meveo.model.filter.PrimitiveFilterCondition;

public abstract class PrimitiveFilterProcessor {

    /**
     * Checks the {@link PrimitiveFilterCondition} if the processor
     * implementation can process it.
     *
     * @param condition The {@link PrimitiveFilterCondition} to be processed.
     * @return true if the processor matches the condition.
     */
    public abstract boolean canProccessCondition(PrimitiveFilterCondition condition);

    /**
     * Processes the {@link PrimitiveFilterCondition} and adds a criterion to
     * the {@link FilteredQueryBuilder}.
     *
     * @param queryBuilder The {@link FilteredQueryBuilder} instance where the criterion
     *                     based on the condition will be added.
     * @param alias        The value of the {@link FilterCondition}'s primarySelector
     *                     property.
     * @param condition    The {@link PrimitiveFilterCondition} that will be processed.
     * @throws FilterException filter exception.
     */
    public abstract void process(FilteredQueryBuilder queryBuilder, String alias, PrimitiveFilterCondition condition)
        throws FilterException;

    /**
     * Tests the existence of a string prefix on the
     * {@link PrimitiveFilterCondition}'s operand property.
     *
     * @param condition The condition to be tested
     * @param prefix    The string prefix that will be checked.
     * @return true if the operand contains the prefix.
     */
    protected boolean isPrefixInOperand(PrimitiveFilterCondition condition, String prefix) {
        boolean containsPrefix = false;
        if (condition != null && condition.getOperand() != null) {
            containsPrefix = StringUtils.trimToEmpty(condition.getOperand()).indexOf(prefix) == 0;
        }
        return containsPrefix;
    }

    /**
     * Validates if the {@link PrimitiveFilterCondition}'s operand is numeric and is of a specific type.
     *
     * @param condition The condition whose operand is being tested.
     * @param validator The {@link AbstractNumberValidator} instace being used to validate the numeric type.
     * @return true if the operand is of the specified numeric type.
     */
    protected boolean isOfNumberType(PrimitiveFilterCondition condition, AbstractNumberValidator validator) {
        String operand = condition.getOperand();
        boolean isNumber = NumberUtils.isNumber(operand);
        boolean isValid = validator.isValid(operand);
        return isNumber && isValid;
    }

    protected Map.Entry<CustomFieldTemplate, Object> fetchCustomFieldEntry(Map<CustomFieldTemplate, Object> parameterMap, String operand) {
        Map.Entry<CustomFieldTemplate, Object> customFieldEntry = null;
        if (parameterMap != null && !parameterMap.isEmpty()) {
            String fieldName = getFieldName(operand);
            if (fieldName != null) {
                CustomFieldTemplate customField = null;
                for (Map.Entry<CustomFieldTemplate, Object> parameter : parameterMap.entrySet()) {
                    customField = parameter.getKey();
                    if (fieldName != null && fieldName.equals(customField.getCode())) {
                        customFieldEntry = parameter;
                        break;
                    }
                }
            }
        }
        return customFieldEntry;
    }

    protected Object fetchCustomFieldParameterValue(Map<String, String> parameters, String key) {
        return null;
    }

    private String getFieldName(String operand) {
        String fieldName = null;
        if (operand != null && operand.contains(":")) {
            fieldName = operand.substring(operand.indexOf(":") + 1);
        }
        return fieldName;
    }
}
