package org.meveo.apiv2.services.generic.JsonGenericApiMapper;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
    private SimpleFilterProvider simpleFilterProvider;

    public JsonGenericMapper(Module module, SimpleFilterProvider simpleFilterProvider) {
        setUpConfig();
        registerModule(module);
        this.simpleFilterProvider = simpleFilterProvider;
        this.simpleFilterProvider.addFilter("ForbiddenFieldsFilter", SimpleBeanPropertyFilter.serializeAllExcept(forbiddenFieldNames));
        this.simpleFilterProvider.addFilter("GenericPaginatedResourceFilter", SimpleBeanPropertyFilter.filterOutAllExcept("data","total","offset","limit"));
        addMixIn(BaseEntity.class, ForbiddenFieldsMixIn.class);
        addMixIn(GenericPaginatedResource.class, GenericPaginatedResourceMixIn.class);
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
            addMixIn(BaseEntity.class, EntitySubObjectFieldFilterMixIn.class);
            this.simpleFilterProvider.addFilter("EntitySubObjectFieldFilter", new GenericSimpleBeanPropertyFilter(getEntitySubFieldsToInclude(fields)));
        }
        setFilterProvider(this.simpleFilterProvider);
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

    @Override
    public <T> T readValue(String content, Class<T> valueType) throws IOException{
        return super.readValue(regularizeJsonDtoArrayIds(content), valueType);
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
                ((ArrayNode)next).removeAll();
                ((ArrayNode)next).addAll(arrayJsonNodes);
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

    @JsonFilter("EntitySubObjectFieldFilter")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    private abstract class EntitySubObjectFieldFilterMixIn {}

    @JsonFilter("ForbiddenFieldsFilter")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    private abstract class ForbiddenFieldsMixIn {}

    private @JsonFilter("GenericPaginatedResourceFilter")
    abstract class GenericPaginatedResourceMixIn {}

    public static class Builder {
        private SimpleFilterProvider simpleFilterProvider;
        private Module module;
        private HashSet<String> nestedEntities;

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

        public Builder withNestedEntities(HashSet<String> nestedEntities){
            this.nestedEntities = nestedEntities;
            return this;
        }

        public JsonGenericMapper build(){
            module = GenericModule.Builder.getBuilder().withEntityToLoad(nestedEntities).build();
            if(simpleFilterProvider == null){
                simpleFilterProvider = new SimpleFilterProvider();
            }
            return new JsonGenericMapper(module, simpleFilterProvider);
        }
    }
}
