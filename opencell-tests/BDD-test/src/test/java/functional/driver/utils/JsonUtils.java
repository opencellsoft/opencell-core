package functional.driver.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import com.ibm.icu.text.RuleBasedNumberFormat;
import org.meveo.api.dto.billing.ActivateServicesRequestDto;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class JsonUtils {

    private static final String JSON_POINTER_START = "/";

    static Map<String, String> mapJsonPaths = new HashMap<>();

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

    public static Object defineJson(Map<String, String> anInstance, String dataFields, String purpose, boolean isArray) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();

        JsonSchemaGenerator schemaGen = new JsonSchemaGenerator(mapper);

        try {
            JsonSchema schema = schemaGen.generateSchema(ActivateServicesRequestDto.class);

            String outputSchemaJson = mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(schema);

            constructJsonPaths(outputSchemaJson);

            System.out.println("--------------MAP DAY NE -------------------");
//            for (Map.Entry<String, String> entry : mapJsonPaths.entrySet()) {
//                System.out.println("key : " + entry.getKey()
//                        + " and its value : " + entry.getValue());
//            }
        }
        catch(JsonProcessingException e) {

        }

        String[] dataFieldsArr = Arrays.stream(dataFields.split("for ")).filter(e -> e.trim().length() > 0).toArray(String[]::new);

        for (String dataFieldWithOrder : dataFieldsArr) {
            String[] orderAndField = Arrays.stream(dataFieldWithOrder.split("\\s+")).filter(e -> e.trim().length() > 0).toArray(String[]::new);

            String order = orderAndField[0];
            RuleBasedNumberFormat nf = new RuleBasedNumberFormat(Locale.UK, RuleBasedNumberFormat.SPELLOUT);
            try {
                System.out.println(order + " -> " + nf.parse(order).intValue());
            }
            catch (ParseException e) {
                System.out.println("ParseException : " + e);
            }
            String field = orderAndField[1];
        }

        ActivateServicesRequestDto dto = new ActivateServicesRequestDto();

        dto.setSubscription("a desciprtion day ne");

        ObjectWriter ow = new ObjectMapper().writer();

//        try {
//            String jsonDto = ow.writeValueAsString(dto);
//            System.out.println("json ActivateServicesRequestDto : " + jsonDto);
//
//            JsonParser parser = new JsonParser(jsonDto);
//            List<String> jsonPaths = parser.getPathList();
//
//            for (String path : jsonPaths) {
//                System.out.println("all jsonPath here : " + path);
//            }
//        }
//        catch (JsonProcessingException e) {
//            System.out.println("JsonProcessingException : " + e);
//        }

//        Given the phrase as "first customField, second service, etc.",
//        the objective is to find the full json path as follows for the attribute customField
//        Maybe we need to require the deep for for searching an attribute inside of a Java object
//        Input : ActivateServicesRequestDto, first customField
//        Output : "/servicesToActivate/service/0/customFields/customField/0"
//        Or as follows :
//        Input : ActivateServicesRequestDto, second service
//        Output : "/servicesToActivate/service/1"

        return rootNode;
    }

    public static void constructJsonPaths(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(json);
            JsonNode rootNode = root.path("properties");
            fillMapJsonPath(rootNode, "");
        }
        catch (JsonProcessingException e){
            System.out.println(e);
        }
    }

    private static void fillMapJsonPath(JsonNode rootNode, String path) {
        for (Iterator<Map.Entry<String, JsonNode>> it = rootNode.fields(); it.hasNext(); ) {
            Map.Entry<String, JsonNode> entry = it.next();
            JsonNode cNode = entry.getValue();
            if (cNode.path("type").toString().toLowerCase().contains("array")) {
                for (JsonNode ccNode : cNode.path("items")) {
                    fillMapJsonPath(ccNode, path + entry.getKey() + "/[0]/");
                }
            }
            else if (cNode.path("type").toString().toLowerCase().contains("object")) {
                fillMapJsonPath(cNode.path("properties"), path + entry.getKey() + "/");
            }
            else {
                System.out.println("key : " + entry.getKey()
                        + " and its value : " + path + entry.getKey());
                mapJsonPaths.put(entry.getKey(), path + entry.getKey());
            }
        }
    }


        public static void mapValueToAttribute(Map<String, String> anInstance, String dataFields, String purpose, boolean isArray) {

//        String realJsonPath = "/servicesToActivate/service/0/customFields/customField/0";
//
//        for (Map.Entry<String, String> entry : anInstance.entrySet()) {
//            if (! entry.getKey().equalsIgnoreCase("name")) {
//                String[] arrNames = entry.getValue().split(",");
//                int idxArr = 0;
//                for (String name : arrNames) {
//                    if (BasicConfig.getMapNameAndJsonObject().containsKey(name)) {
//                        if (isArray) {
//                            if (intention.equalsIgnoreCase("activate services on subscription"))
//                                JsonObjectGenerator.setJsonPointerValue(rootNode,
//                                        JsonPointer.compile(realJsonPath + JSON_POINTER_START + entry.getKey() + "/" + idxArr),
//                                        (JsonNode) BasicConfig.getMapNameAndJsonObject().get(name));
//                            else
//                                JsonObjectGenerator.setJsonPointerValue(rootNode,
//                                        JsonPointer.compile(JSON_POINTER_START + entry.getKey() + "/" + idxArr),
//                                        (JsonNode) BasicConfig.getMapNameAndJsonObject().get(name));
//                            idxArr++;
//                        }
//                        else {
//                            if (intention.equalsIgnoreCase("activate services on subscription")) {
//                                JsonObjectGenerator.setJsonPointerValue(rootNode,
//                                        JsonPointer.compile(realJsonPath + JSON_POINTER_START + entry.getKey()),
//                                        (JsonNode) BasicConfig.getMapNameAndJsonObject().get(name));
//                            }
//                            else
//                                JsonObjectGenerator.setJsonPointerValue(rootNode,
//                                        JsonPointer.compile(JSON_POINTER_START + entry.getKey()),
//                                        (JsonNode) BasicConfig.getMapNameAndJsonObject().get(name));
//                        }
//                    }
//                    else {
//System.out.println( "DAY NE : " + realJsonPath + JSON_POINTER_START + entry.getKey() );
//                        if (intention.equalsIgnoreCase("activate services on subscription"))
//                            JsonObjectGenerator.setJsonPointerValue(rootNode,
//                                    JsonPointer.compile(realJsonPath + JSON_POINTER_START + entry.getKey()),
//                                    new TextNode(name));
//                        else
//                            JsonObjectGenerator.setJsonPointerValue(rootNode,
//                                    JsonPointer.compile(JSON_POINTER_START + entry.getKey()),
//                                    new TextNode(name));
//                    }
//                }
//            }
//        }
//System.out.println( "rootNode DAY NE : " + rootNode );
//        BasicConfig.getMapNameAndJsonObject().put(anInstance.get("/name"), rootNode);
    }

}
