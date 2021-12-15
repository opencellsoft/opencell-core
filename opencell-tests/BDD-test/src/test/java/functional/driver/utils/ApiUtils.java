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

    public static String getCompleteUrl(String className, DataTable dataTable) {
        String completeUrl = null;
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);

        for (Map<String, String> columns : rows) {
            for (Map.Entry<String, String> column : columns.entrySet()) {
                System.out.println("column key : " + column.getKey() + " and its value : " +
                        column.getValue());
            }
        }


        return completeUrl;
    }
}
