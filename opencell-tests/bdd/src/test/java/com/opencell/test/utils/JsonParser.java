package com.opencell.test.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.meveo.api.dto.BaseEntityDto;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;

public class JsonParser<T extends BaseEntityDto> {

    public final static String JSON_DIR= "com"+System.getProperty("file.separator")+"opencell"+System.getProperty("file.separator")+"test"+System.getProperty("file.separator")+"feature"+System.getProperty("file.separator");

    public static String writeObjectAsJsonString(Object object) {
        return null;
    }

    public static String writeOnlyRequiredField(Object object) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new SimpleModule() {
            @Override
            public void setupModule(SetupContext context) {
                super.setupModule(context);
                context.addBeanSerializerModifier(new RequiredBeanSerializerModifier());
            }
        });
        return mapper.writeValueAsString(object);

    }

    public static String writeValueAsString(Object object) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new SimpleModule() {
            @Override
            public void setupModule(SetupContext context) {
                super.setupModule(context);
                context.addBeanSerializerModifier(new FullBeanSerializerModifier());
            }
        });
        return mapper.writeValueAsString(object);

    }


    public  T readValue(String fileName, Class<T> clazz) {

        try {
            File json = ResourceUtils.getFileFromClasspathResource(JSON_DIR + fileName.replace("/",System.getProperty("file.separator")));
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, clazz);
        }catch (JsonMappingException mappingException) {
                throw new JSONParserException(mappingException);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



    }

    public JsonNode readValue(String fileName) {

        try {
            System.out.println("USER DIRECTORY: " + System.getProperty("user.dir"));
            System.out.println("JSON_DIR: " + JSON_DIR);
            System.out.println("FILENAME: " + fileName);
            File json = ResourceUtils.getFileFromClasspathResource(JSON_DIR + fileName.replace("/",System.getProperty("file.separator")));
            ObjectMapper objectMapper = new ObjectMapper();
            return  objectMapper.readValue(json, JsonNode.class);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
