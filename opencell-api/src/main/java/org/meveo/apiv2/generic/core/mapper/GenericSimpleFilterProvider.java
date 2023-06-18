package org.meveo.apiv2.generic.core.mapper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Persistence;

import org.hibernate.collection.internal.PersistentBag;

import static com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter.*;

class GenericSimpleFilterProvider extends SimpleFilterProvider {
    private static final String[] forbiddenFieldNames = {
            "NB_DECIMALS", "historized", "notified", "NB_PRECISION", "appendGeneratedCode", "serialVersionUID", "transient", "codeChanged",
            "version", "uuid", "cfValuesNullSafe", "cfAccumulatedValuesNullSafe", "descriptionOrCode", "descriptionAndCode", "referenceCode",
            "referenceDescription", "cfAccumulatedValues"
    };
    public GenericSimpleFilterProvider(boolean shouldExtractList, Set<String> nestedEntities) {
        addFilter("GenericPaginatedResourceFilter", filterOutAllExcept("data","total","offset","limit"));
        addFilter("EntityCustomFieldValuesFilter", filterOutAllExcept("valuesByCode"));
        addFilter("EntityCustomFieldValueFilter", filterOutAllExcept("value", "priority", "period"));
        addFilter("CollectionFilter", new SimpleBeanPropertyFilter() {
            @Override
            protected boolean include(PropertyWriter writer) {
                return writer.getType().isCollectionLikeType() ? shouldExtractList : !Arrays.asList(forbiddenFieldNames).contains(writer.getName()) && super.include(writer);
            }
            
            @Override
            public void serializeAsField(Object pojo, JsonGenerator jgen, SerializerProvider provider, PropertyWriter writer) throws Exception
            {
                if (include(writer)) {
                	Object prop = ((BeanPropertyWriter)writer).get(pojo);
                	if(!isNestedEntityCandidate(nestedEntities, writer.getName()) && prop instanceof PersistentBag && !Persistence.getPersistenceUtil().isLoaded(prop)) {
                		return;
                	}
                    writer.serializeAsField(pojo, jgen, provider);
                } else if (!jgen.canOmitFields()) { // since 2.3
                    writer.serializeAsOmittedField(pojo, jgen, provider);
                }
            }

            @Override
            protected boolean includeElement(Object elementValue) {
                return super.includeElement(elementValue);
            }
        });
    }
    
    boolean isNestedEntityCandidate(Set<String> nestedEntities, String current) {
        return nestedEntities != null && nestedEntities.contains(current);
    }
    String getPathToRoot(JsonGenerator gen){
        return gen.getOutputContext().pathAsPointer(false).toString().replaceFirst("/", "").replaceAll("\\d+/", "").replaceAll("/", ".");
    }
}
