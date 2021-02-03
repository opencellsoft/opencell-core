package org.meveo.apiv2.generic.core.mapper;

import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter.*;

class GenericSimpleFilterProvider extends SimpleFilterProvider {
    private static final String[] forbiddenFieldNames = {
            "NB_DECIMALS", "historized", "notified", "NB_PRECISION", "appendGeneratedCode", "serialVersionUID", "transient", "codeChanged",
            "version", "uuid", "cfValuesNullSafe", "cfAccumulatedValuesNullSafe", "descriptionOrCode", "descriptionAndCode", "referenceCode",
            "referenceDescription", "cfAccumulatedValues"
    };
    public GenericSimpleFilterProvider(boolean shouldExtractList) {
        addFilter("GenericPaginatedResourceFilter", filterOutAllExcept("data","total","offset","limit"));
        addFilter("EntityCustomFieldValuesFilter", filterOutAllExcept("valuesByCode"));
        addFilter("EntityCustomFieldValueFilter", filterOutAllExcept("value", "priority", "period"));
        addFilter("CollectionFilter", new SimpleBeanPropertyFilter() {
            @Override
            protected boolean include(PropertyWriter writer) {
                return writer.getType().isCollectionLikeType() ? shouldExtractList : !Arrays.asList(forbiddenFieldNames).contains(writer.getName()) && super.include(writer);
            }

            @Override
            protected boolean includeElement(Object elementValue) {
                return super.includeElement(elementValue);
            }
        });
    }
}
