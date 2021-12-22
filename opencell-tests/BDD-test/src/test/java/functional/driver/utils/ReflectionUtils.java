package functional.driver.utils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class ReflectionUtils {

    private static final String DTO_PREFIX = "org.meveo.api.dto.billing.";
    private static final String DTO_SUFFIX = "Dto";

    public static Class<?> getDtoClassByName(String className) {
        Class<?> dtoClass = null;

        try {
            dtoClass = Class.forName(DTO_PREFIX + className + DTO_SUFFIX);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return dtoClass;
    }

    private static Map removeEmptyValues(Map<String, Object> data) {
        for (Iterator<Map.Entry<String, Object>> it = data.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, Object> entry = it.next();
            if (entry.getValue() == null) {
                it.remove();
            } else if (entry.getValue().getClass().equals(ArrayList.class)) {
                if (((ArrayList<?>) entry.getValue()).size() == 0) {
                    it.remove();
                }
            } else if (entry.getValue() instanceof Map){ //removes empty json objects {}
                Map<?, ?> m = (Map<?, ?>)entry.getValue();
                if(m.isEmpty()) {
                    it.remove();
                }
            }
        }

        return data;
    }

    protected static void invokeSetter(Object obj, String fieldName, Object value) {
        try {
            PropertyDescriptor pd = new PropertyDescriptor(fieldName, obj.getClass());
            // Call setter on specified property
            pd.getWriteMethod().invoke(obj, value);
        } catch (IntrospectionException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    protected static void invokeGetter(Object obj, String fieldName) {
        PropertyDescriptor pd;
        try {
            pd = new PropertyDescriptor(fieldName, obj.getClass());
            // Call getter on specified property
            System.out.println(pd.getDisplayName()+"- " + pd.getReadMethod().invoke(obj));
        } catch (IntrospectionException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
