package org.meveo.apiv2.services.generic.JsonGenericApiMapper;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.meveo.apiv2.generic.GenericPaginatedResource;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class JsonGenericMapper extends ObjectMapper{
    private static final String[] forbiddenFieldNames = {
            "NB_DECIMALS",
            "historized",
            "notified",
            "NB_PRECISION",
            "appendGeneratedCode",
            "serialVersionUID",
            "transient",
            "codeChanged"
    };
    private final SimpleFilterProvider simpleFilterProvider;

    public JsonGenericMapper() {
        registerModule(new LazyProxyModule());
        setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        addMixIn(Object.class, ForbiddenFieldsMixIn.class);
        simpleFilterProvider = new SimpleFilterProvider();
        simpleFilterProvider
                .addFilter("ForbiddenFieldsFilter", SimpleBeanPropertyFilter.serializeAllExcept(forbiddenFieldNames));
    }

    public String toJson(Set<String> fields, Class entityClass, Object dtoToSerialize) {
        addMixIn(GenericPaginatedResource.class, GenericPaginatedResourceMixIn.class);
        simpleFilterProvider
                .addFilter("GenericPaginatedResourceFilter", SimpleBeanPropertyFilter.filterOutAllExcept("data","total","offset","limit"));

        if(fields != null && !fields.isEmpty()){
            addMixIn(entityClass, EntityFieldsFilterMixIn.class);
            simpleFilterProvider
                    .addFilter("EntityFieldsFilter", SimpleBeanPropertyFilter.filterOutAllExcept(fields));

            addMixIn(Object.class, EntitySubObjectFieldFilterMixIn.class);
            simpleFilterProvider
                    .addFilter("EntitySubObjectFieldFilter", new GenericSimpleBeanPropertyFilter(getEntitySubFieldsToInclude(fields)));

        }
        setFilterProvider(simpleFilterProvider);
        try {
            return writeValueAsString(dtoToSerialize);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    private Set<String>  getEntitySubFieldsToInclude(Set<String> fields) {
        Set<String> filteredSubFields = new HashSet<>();
        Iterator<String> iterator = fields.iterator();
        iterator.forEachRemaining(s -> {
            if(s.contains(".")){
                filteredSubFields.add(s);
            }
        });
        return filteredSubFields;
    }

    @JsonFilter("EntityFieldsFilter")
    abstract class EntityFieldsFilterMixIn {}
    @JsonFilter("GenericPaginatedResourceFilter")
    abstract class GenericPaginatedResourceMixIn {}
    @JsonFilter("EntitySubObjectFieldFilter")
    abstract class EntitySubObjectFieldFilterMixIn {}
    @JsonFilter("ForbiddenFieldsFilter")
    abstract class ForbiddenFieldsMixIn {}
}
