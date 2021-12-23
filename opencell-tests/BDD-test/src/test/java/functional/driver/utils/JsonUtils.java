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

public class JsonUtils {

    private static final String JSON_POINTER_START = "/";

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

    public static Object defineJson(Map<String, String> anInstance, String dataField, String intention, boolean isArray) {
        ObjectNode rootNode = new ObjectMapper().createObjectNode();


// Need to create a map which predefine couples (key, value) defined as follows : (intention.dataField, realJsonPathOfDataField)
// for example: (activate services on subscription.custom field, /servicesToActivate/service/0/customFields/customField)
// Then the Json that will be created will be as follows :
//
//         "servicesToActivate": {
//            "service": [
//            {
//              "customFields": {
//                "customField": [
//                {
//                    "code": "CF_SE_DOUBLE",
//                     "doubleValue": 150
//                }
//                ]
//            }
//            }
//          ]
//        }
        String realJsonPath = "/servicesToActivate/service/0/customFields/customField/0";

        for (Map.Entry<String, String> entry : anInstance.entrySet()) {
            if (! entry.getKey().equalsIgnoreCase("name")) {
                String[] arrNames = entry.getValue().split(",");
                int idxArr = 0;
                for (String name : arrNames) {
                    if (BasicConfig.getMapNameAndJsonObject().containsKey(name)) {
                        if (isArray) {
                            if (intention.equalsIgnoreCase("activate services on subscription"))
                                JsonObjectGenerator.setJsonPointerValue(rootNode,
                                        JsonPointer.compile(realJsonPath + JSON_POINTER_START + entry.getKey() + "/" + idxArr),
                                        (JsonNode) BasicConfig.getMapNameAndJsonObject().get(name));
                            else
                                JsonObjectGenerator.setJsonPointerValue(rootNode,
                                        JsonPointer.compile(JSON_POINTER_START + entry.getKey() + "/" + idxArr),
                                        (JsonNode) BasicConfig.getMapNameAndJsonObject().get(name));
                            idxArr++;
                        }
                        else {
                            if (intention.equalsIgnoreCase("activate services on subscription")) {
                                JsonObjectGenerator.setJsonPointerValue(rootNode,
                                        JsonPointer.compile(realJsonPath + JSON_POINTER_START + entry.getKey()),
                                        (JsonNode) BasicConfig.getMapNameAndJsonObject().get(name));
                            }
                            else
                                JsonObjectGenerator.setJsonPointerValue(rootNode,
                                        JsonPointer.compile(JSON_POINTER_START + entry.getKey()),
                                        (JsonNode) BasicConfig.getMapNameAndJsonObject().get(name));
                        }
                    }
                    else {
System.out.println( "DAY NE : " + realJsonPath + JSON_POINTER_START + entry.getKey() );
                        if (intention.equalsIgnoreCase("activate services on subscription"))
                            JsonObjectGenerator.setJsonPointerValue(rootNode,
                                    JsonPointer.compile(realJsonPath + JSON_POINTER_START + entry.getKey()),
                                    new TextNode(name));
                        else
                            JsonObjectGenerator.setJsonPointerValue(rootNode,
                                    JsonPointer.compile(JSON_POINTER_START + entry.getKey()),
                                    new TextNode(name));
                    }
                }
            }
        }
System.out.println( "rootNode DAY NE : " + rootNode );
        BasicConfig.getMapNameAndJsonObject().put(anInstance.get("/name"), rootNode);

        return rootNode;
    }
}
