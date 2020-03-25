/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.apiv2.services.generic.JsonGenericApiMapper;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.meveo.apiv2.generic.GenericPaginatedResource;
import org.meveo.model.BaseEntity;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.crm.custom.CustomFieldValue;
import org.meveo.model.crm.custom.CustomFieldValues;
import org.meveo.model.payments.CustomerAccount;

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
        addMixIn(OfferTemplateCategory.class, InfiniteRecursionMixIn.class);
        addMixIn(CustomerAccount.class, InfiniteRecursionMixIn.class);
        addMixIn(OfferTemplate.class, InfiniteRecursionMixIn.class);
        addMixIn(WalletOperation.class, InfiniteRecursionMixIn.class);
        addMixIn(ChargeInstance.class, InfiniteRecursionMixIn.class);
        addMixIn(BaseEntity.class, ForbiddenFieldsMixIn.class);
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
        return  (BaseEntity) readValue(jsonDto, entityClass);
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
    private abstract class ForbiddenFieldsMixIn {}

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    private abstract class InfiniteRecursionMixIn {}

    @JsonFilter("GenericPaginatedResourceFilter")
    private abstract class GenericPaginatedResourceMixIn {}

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
                simpleFilterProvider = new GenericSimpleFilterProvider();
            }
            return new JsonGenericMapper(module, simpleFilterProvider);
        }
    }
}
