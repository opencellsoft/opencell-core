package org.meveo.apiv2.generic.core.mapper;

import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

class GenericSimpleFilterProvider extends SimpleFilterProvider {
    private static final String[] forbiddenFieldNames = {
            "NB_DECIMALS", "historized", "notified", "NB_PRECISION", "appendGeneratedCode", "serialVersionUID", "transient", "codeChanged",
            "version", "uuid", "cfValuesNullSafe", "cfAccumulatedValuesNullSafe", "descriptionOrCode", "descriptionAndCode", "referenceCode",
            "referenceDescription", "cfAccumulatedValues"
    };
    public GenericSimpleFilterProvider() {
        addFilter("EntityForbiddenFieldsFilter", SimpleBeanPropertyFilter.serializeAllExcept(forbiddenFieldNames));
        addFilter("GenericPaginatedResourceFilter", SimpleBeanPropertyFilter.filterOutAllExcept("data","total","offset","limit"));
        addFilter("EntityCustomFieldValuesFilter", SimpleBeanPropertyFilter.filterOutAllExcept("valuesByCode"));
        addFilter("EntityCustomFieldValueFilter", SimpleBeanPropertyFilter.filterOutAllExcept("value", "priority", "period"));
    }
}
