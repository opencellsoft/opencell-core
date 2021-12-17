package functional.driver.utils;

import io.cucumber.datatable.DataTable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public class ApiUtils {

    public static String getJsonBody(Class<?> dtoMapperClass, DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        Constructor<?> constructor = null;
        Object newInstance = null;

        try {
            constructor = dtoMapperClass.getDeclaredConstructor();
            newInstance = constructor.newInstance();
        }
        catch (NoSuchMethodException | InvocationTargetException | InstantiationException
                | IllegalAccessException e){
            e.printStackTrace();
        }

        for (Map<String, String> columns : rows) {
            for (Map.Entry<String, String> column : columns.entrySet()) {
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
        }

        return null;
    }

    public static String getUrlForGet(String entityName, String baseUrl, Map<String, String> anInstance) {
        StringBuilder completeUrl = new StringBuilder(baseUrl).append("?");

        for (Map.Entry<String, String> column : anInstance.entrySet()) {
            if (entityName.equals("AccessPoint"))
                completeUrl.append(column.getKey()).append("=").append(column.getValue()).append("&");
        }

        return completeUrl.toString();
    }
}
