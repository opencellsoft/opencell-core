package functional.driver.utils;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import functional.stepDefs.generic.BasicConfig;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class ApiUtils {

    public static Object getJsonBody(Class<?> dtoClass, Map<String, String> anInstance) {
        Constructor<?> constructor = null;
        Object newInstance = null;

        try {
            constructor = dtoClass.getDeclaredConstructor();
            newInstance = constructor.newInstance();
        }
        catch (NoSuchMethodException | InvocationTargetException | InstantiationException
                | IllegalAccessException e){
            e.printStackTrace();
        }

        for (Map.Entry<String, String> column : anInstance.entrySet()) {
            if (constructor != null && newInstance != null) {
                ReflectionUtils.invokeSetter(newInstance, column.getKey(), column.getValue());
            }
//                try {
//                    Field field = aClass.getDeclaredField(column.getKey());
//                    FieldMapper annotation = field.getDeclaredAnnotation(FieldMapper.class);
//
//                    JsonObjectGenerator.setJsonPointerValue(rootNode, JsonPointer.compile(annotation.value()),
//                            new TextNode(column.getValue()));
//                }
//                catch (NoSuchFieldException e) {
//                    e.printStackTrace();
//                }
        }

        return newInstance;
    }

    public static String getUrlForGet(String entityName, String baseUrl, Map<String, String> anInstance) {
        StringBuilder completeUrl = new StringBuilder(baseUrl).append("?");

        for (Map.Entry<String, String> column : anInstance.entrySet()) {
            if (entityName.equals("AccessPoint"))
                completeUrl.append(column.getKey()).append("=").append(column.getValue()).append("&");
        }

        return completeUrl.toString();
    }

    public static Object createJson(Map<String, String> anInstance, boolean isArray) {
        ObjectNode rootNode = new ObjectMapper().createObjectNode();

        for (Map.Entry<String, String> entry : anInstance.entrySet()) {
            if (! entry.getKey().equalsIgnoreCase("/name")) {
                String[] arrNames = entry.getValue().split(",");
                int idxArr = 0;
                for (String name : arrNames) {
                    if (BasicConfig.getMapNameAndJsonObject().containsKey(name)) {
                        if (isArray) {
                            JsonObjectGenerator.setJsonPointerValue(rootNode, JsonPointer.compile(entry.getKey() + "/" + idxArr),
                                    (JsonNode) BasicConfig.getMapNameAndJsonObject().get(name));
                            idxArr++;
                        }
                        else
                            JsonObjectGenerator.setJsonPointerValue(rootNode, JsonPointer.compile(entry.getKey()),
                                    (JsonNode) BasicConfig.getMapNameAndJsonObject().get(name));
                    }
                    else {
                        JsonObjectGenerator.setJsonPointerValue(rootNode, JsonPointer.compile(entry.getKey()),
                                new TextNode(name));
                    }
                }
            }
        }

        BasicConfig.getMapNameAndJsonObject().put(anInstance.get("/name"), rootNode);

        return rootNode;
    }
}
