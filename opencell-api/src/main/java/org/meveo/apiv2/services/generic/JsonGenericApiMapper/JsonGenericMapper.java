package org.meveo.apiv2.services.generic.JsonGenericApiMapper;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.meveo.apiv2.generic.GenericPaginatedResource;
import org.meveo.model.BaseEntity;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class JsonGenericMapper extends ObjectMapper{
    private static final String[] forbiddenFieldNames = {
            "NB_DECIMALS", "historized", "notified", "NB_PRECISION", "appendGeneratedCode", "serialVersionUID", "transient", "codeChanged",
            "version", "uuid", "cfValuesNullSafe", "cfAccumulatedValuesNullSafe", "descriptionOrCode", "descriptionAndCode", "referenceCode",
            "referenceDescription"
    };
    private final SimpleFilterProvider simpleFilterProvider;

    public JsonGenericMapper(HashSet<String> nestedEntities) {
        setUpConfig(nestedEntities);
        simpleFilterProvider = new SimpleFilterProvider();
        addMixIn(BaseEntity.class, ForbiddenFieldsMixIn.class);
        simpleFilterProvider.addFilter("ForbiddenFieldsFilter", SimpleBeanPropertyFilter.serializeAllExcept(forbiddenFieldNames));
        addMixIn(GenericPaginatedResource.class, GenericPaginatedResourceMixIn.class);
        simpleFilterProvider.addFilter("GenericPaginatedResourceFilter", SimpleBeanPropertyFilter.filterOutAllExcept("data","total","offset","limit"));
    }

    private void setUpConfig(HashSet<String> nestedEntities) {
        registerModule(new GenericModule(nestedEntities));
        setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true);
        configure(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS, true);
    }

    public String toJson(Set<String> fields, Class entityClass, Object dtoToSerialize) {
        if(fields != null && !fields.isEmpty()){
            addMixIn(entityClass, EntityFieldsFilterMixIn.class);
            simpleFilterProvider.addFilter("EntityFieldsFilter", SimpleBeanPropertyFilter.filterOutAllExcept(fields));
            addMixIn(BaseEntity.class, EntitySubObjectFieldFilterMixIn.class);
            simpleFilterProvider.addFilter("EntitySubObjectFieldFilter", new GenericSimpleBeanPropertyFilter(getEntitySubFieldsToInclude(fields)));
        }
        setFilterProvider(simpleFilterProvider);
        try {
            return writeValueAsString(dtoToSerialize);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("json formatting exception", e);
        }
    }

    public BaseEntity parseFromJson(String jsonDto, Class entityClass) {
        try {
            return  (BaseEntity) readValue(jsonDto, entityClass);
        } catch (IOException e) {
            throw new IllegalArgumentException("The given string value: " + jsonDto + " cannot be transformed to Json object", e);
        }
    }

    private Set<String>  getEntitySubFieldsToInclude(Set<String> fields) {
        if(fields == null ){
            return Collections.emptySet();
        }
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
    private abstract class EntityFieldsFilterMixIn {}

    @JsonFilter("EntitySubObjectFieldFilter")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    private abstract class EntitySubObjectFieldFilterMixIn {}

    @JsonFilter("ForbiddenFieldsFilter")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    private abstract class ForbiddenFieldsMixIn {}

    private @JsonFilter("GenericPaginatedResourceFilter")
    abstract class GenericPaginatedResourceMixIn {}
}
