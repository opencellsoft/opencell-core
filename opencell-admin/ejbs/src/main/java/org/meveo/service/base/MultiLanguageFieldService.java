package org.meveo.service.base;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;

import org.meveo.model.IEntity;
import org.reflections.Reflections;

@Stateless
public class MultiLanguageFieldService implements Serializable {

    private static final long serialVersionUID = 2643403930313623679L;

    @SuppressWarnings("rawtypes")
    private static Map<Class, List<String>> multiLanguageFieldMapping;

    @SuppressWarnings("rawtypes")
    public Map<Class, List<String>> getMultiLanguageFieldMapping() {

        if (multiLanguageFieldMapping == null) {

            Map<Class, List<String>> entityMapping = new HashMap<>();

            Reflections reflections = new Reflections("org.meveo.model");
            Set<Class<? extends IEntity>> classes = reflections.getSubTypesOf(IEntity.class);

            for (Class clazz : classes) {
                if (clazz.isInterface() || clazz.isAnnotation() || Modifier.isAbstract(clazz.getModifiers()) || !IEntity.class.isAssignableFrom(clazz)) {
                    continue;
                }

                Class cls = clazz;
                while (!Object.class.equals(cls) && cls != null) {

                    for (Field field : cls.getDeclaredFields()) {

                        if (field.getName().endsWith("I18n")) {
                            if (!entityMapping.containsKey(clazz)) {
                                entityMapping.put(clazz, new ArrayList<>());
                            }
                            // Store a fieldname without I18n suffix
                            entityMapping.get(clazz).add(field.getName().substring(0, field.getName().length() - 4));
                        }
                    }

                    cls = cls.getSuperclass();
                }
            }

            List<Map.Entry<Class, List<String>>> entityMappingList = new LinkedList<>(entityMapping.entrySet());
            Collections.sort(entityMappingList, new Comparator<Map.Entry<Class, List<String>>>() {
                @Override
                public int compare(Map.Entry<Class, List<String>> o1, Map.Entry<Class, List<String>> o2) {
                    return o1.getKey().getSimpleName().compareTo(o2.getKey().getSimpleName());
                }
            });

            Map<Class, List<String>> multiLanguageFieldMappingCopy = new LinkedHashMap<>();
            for (Map.Entry<Class, List<String>> entry : entityMappingList) {
                multiLanguageFieldMappingCopy.put(entry.getKey(), entry.getValue());
            }

            multiLanguageFieldMapping = multiLanguageFieldMappingCopy;
        }
        return multiLanguageFieldMapping;
    }

    /**
     * Get a list of translatable fields for a given entity class
     * 
     * @param entityClass Entity class
     * @return A list of translatable fields
     */
    @SuppressWarnings("rawtypes")
    public List<String> getMultiLanguageFields(Class entityClass) {
        return getMultiLanguageFieldMapping().get(entityClass);
    }
}