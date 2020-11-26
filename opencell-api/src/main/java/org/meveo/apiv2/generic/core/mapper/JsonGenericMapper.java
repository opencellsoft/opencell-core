package org.meveo.apiv2.generic.core.mapper;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.meveo.apiv2.generic.GenericPaginatedResource;
import org.meveo.apiv2.generic.core.mapper.module.GenericModule;
import org.meveo.model.IEntity;
import org.meveo.model.crm.custom.CustomFieldValue;
import org.meveo.model.crm.custom.CustomFieldValues;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class JsonGenericMapper extends ObjectMapper{
    private SimpleFilterProvider simpleFilterProvider;

    public JsonGenericMapper(Module module, SimpleFilterProvider simpleFilterProvider) {
        setUpConfig();
        registerModule(module);
        this.simpleFilterProvider = simpleFilterProvider;
        addMixIn(IEntity.class, BaseEntityMixIn.class);
        addMixIn(GenericPaginatedResource.class, GenericPaginatedResourceMixIn.class);
        addMixIn(CustomFieldValues.class, EntityCustomFieldValuesFilterMixIn.class);
        addMixIn(CustomFieldValue.class, EntityCustomFieldValueFilterMixIn.class);
    }

    private void setUpConfig() {
        setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true);
        configure(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS, true);
    }

    public String toJson(Set<String> fields, Class entityClass, Object dtoToSerialize) {
        if(fields != null && !fields.isEmpty()){
            addMixIn(entityClass, EntityFieldsFilterMixIn.class);
            simpleFilterProvider.addFilter("EntityFieldsFilter", SimpleBeanPropertyFilter.filterOutAllExcept(fields));
            addMixIn(IEntity.class, EntitySubObjectFieldFilterMixIn.class);
            this.simpleFilterProvider.addFilter("EntitySubObjectFieldFilter", new GenericSimpleBeanPropertyFilter(getEntitySubFieldsToInclude(fields)));
        }
        setFilterProvider(this.simpleFilterProvider);
        try {
            return writeValueAsString(dtoToSerialize);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("json formatting exception", e);
        }
    }

    public IEntity parseFromJson(String jsonDto, Class entityClass) {
        return  (IEntity) readValue(jsonDto, entityClass);
    }

    @Override
    public <T> T readValue(String content, Class<T> valueType){
        try {
            return super.readValue(regularizeJsonDtoArrayIds(content), valueType);
        } catch (IOException e) {
            throw new IllegalArgumentException("The given string value: " + content + " cannot be transformed to Json object", e);
        }
    }

    private String regularizeJsonDtoArrayIds(String jsonDto) throws IOException {
        JsonNode rootJsonNode = readTree(jsonDto);
        Iterator<JsonNode> elements = rootJsonNode.elements();
        while(elements.hasNext()){
            JsonNode next = elements.next();
            if(next.isArray()){
                ArrayNode arrayJsonNodes = JsonNodeFactory.instance.arrayNode();
                Iterator<JsonNode> subNext = next.elements();
                while(subNext.hasNext()){
                    JsonNode jsonNode = subNext.next();
                    if(jsonNode.isInt()){
                        ObjectNode objectJsonNode = JsonNodeFactory.instance.objectNode();
                        objectJsonNode.put("id", jsonNode.intValue());
                        arrayJsonNodes.add(objectJsonNode);
                    }
                }
                if(arrayJsonNodes.size()>0){
                    ((ArrayNode)next).removeAll();
                    ((ArrayNode)next).addAll(arrayJsonNodes);
                }
            }
        }
        return rootJsonNode.toString();
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

    @JsonFilter("EntityCustomFieldValuesFilter")
    private abstract class EntityCustomFieldValuesFilterMixIn {}

    @JsonFilter("EntityCustomFieldValueFilter")
    private abstract class EntityCustomFieldValueFilterMixIn {}

    @JsonFilter("EntitySubObjectFieldFilter")
    private abstract class EntitySubObjectFieldFilterMixIn {}

    @JsonFilter("EntityForbiddenFieldsFilter")
    private interface ForbiddenFieldsMixIn {}

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    private interface InfiniteRecursionMixIn {}

    @JsonFilter("EntityForbiddenFieldsFilter")
    //@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    private class BaseEntityMixIn{}

    @JsonFilter("GenericPaginatedResourceFilter")
    private abstract class GenericPaginatedResourceMixIn {}

    public static class Builder {
        private SimpleFilterProvider simpleFilterProvider;
        private Module module;
        private Set<String> nestedEntities;
        private Long nestedDepth;

        public static Builder getBuilder(){
            return new Builder();
        }

        public Builder withModule(Module module){
            this.module = module;
            return this;
        }

        public Builder withSimpleFilterProvider(SimpleFilterProvider simpleFilterProvider){
            this.simpleFilterProvider = simpleFilterProvider;
            return this;
        }

        public Builder withNestedEntities(Set<String> nestedEntities){
            this.nestedEntities = nestedEntities;
            return this;
        }

        public Builder withNestedDepth(Long nestedDepth){
            this.nestedDepth = nestedDepth;
            return this;
        }

        public JsonGenericMapper build(){
            module = GenericModule.Builder.getBuilder()
                    .withEntityToLoad(nestedEntities)
                    .withNestedDepth(nestedDepth)
                    .build();
            if(simpleFilterProvider == null){
                simpleFilterProvider = new GenericSimpleFilterProvider();
            }
            return new JsonGenericMapper(module, simpleFilterProvider);
        }
    }
}
