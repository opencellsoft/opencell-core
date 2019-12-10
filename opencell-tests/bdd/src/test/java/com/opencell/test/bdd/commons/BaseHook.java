package com.opencell.test.bdd.commons;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.gson.JsonObject;

import cucumber.api.java.Before;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.BaseEntityDto;
import org.meveo.api.dto.BusinessEntityDto;
import org.reflections.Reflections;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class BaseHook {

    private ApiResponse response;
    private WebResponse webResponse;
    private BaseEntityDto entityDto;
    private JsonNode jsonObject;
    private static Set<Class<? extends BaseEntityDto>> dtoClasses;

    @Before
    public void before() {
        this.response = new ApiResponse();
    }

    @Before
    public void loadDtoClasses() {
        if(dtoClasses == null || dtoClasses.isEmpty()){
            Reflections reflections = new Reflections("org.meveo.api.dto");
            dtoClasses = reflections.getSubTypesOf(BaseEntityDto.class);
        }
    }

    public ApiResponse getResponse() {
        return response;
    }

    public void setResponse(ApiResponse response) {
        this.response = response;
    }
    

    public WebResponse getWebResponse() {
        return webResponse;
    }

    public void setWebResponse(WebResponse webResponse) {
        this.webResponse = webResponse;
    }

    public BaseEntityDto getEntityDto() {
        return entityDto;
    }

    public void setEntityDto(BaseEntityDto entityDto) {
        this.entityDto = entityDto;
    }

    public JsonNode getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JsonNode jsonObject) {
        this.jsonObject = jsonObject;
    }

    /**
     *
     * @param dto
     * @return
     */
    public Optional<? extends Class> getEntityClass(String dto){
        return  dtoClasses.stream().filter(clazz ->{
            return clazz.getSimpleName().equals(dto);
        }).findFirst();

    }

    public Optional<String> getCode(){
        if(entityDto != null && ((BusinessEntityDto)entityDto).getCode()!= null){
            return  Optional.of(((BusinessEntityDto)entityDto).getCode());
        }
        if(jsonObject != null && jsonObject.get("code") != null){
            return Optional.of(jsonObject.get("code").asText());
        }
        if(response != null && response.getActionStatus() != null && response.getActionStatus().getEntityCode() != null){
            return Optional.of(response.getActionStatus().getEntityCode());
        }
        return Optional.empty();
    }

    public Optional<String> getField(String field){
        if(entityDto != null) {
            ObjectMapper mapper = new ObjectMapper();
            jsonObject = mapper.valueToTree(entityDto);
        }
        if(jsonObject != null && jsonObject.get(field) != null){
            return Optional.of(jsonObject.get(field).asText());
        }
        return Optional.empty();
    }
}


