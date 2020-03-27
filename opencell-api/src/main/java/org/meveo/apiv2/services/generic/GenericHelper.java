package org.meveo.apiv2.services.generic;

import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.catalog.OfferServiceTemplate;

import javax.persistence.Entity;
import java.util.Map;
import java.util.stream.Collectors;

import static org.meveo.apiv2.ValidationUtils.checkEntityClass;
import static org.meveo.apiv2.ValidationUtils.checkEntityName;

public class GenericHelper {
    public final static Map<String, Class> entitiesByName;

    static {
        entitiesByName = populateEntitiesToHandleByGenericApi();
    }

    private static Map<String, Class> populateEntitiesToHandleByGenericApi() {
        Map<String, Class> entitiesByName  = ReflectionUtils.getClassesAnnotatedWith(Entity.class).stream().collect(Collectors.toMap(clazz -> clazz.getSimpleName().toLowerCase(), clazz -> clazz));
        populateNonBaseEntityClass(entitiesByName);
        return entitiesByName;
    }

    private static void populateNonBaseEntityClass(Map<String, Class> entitiesByName) {
        entitiesByName.put(OfferServiceTemplate.class.getSimpleName().toLowerCase(), OfferServiceTemplate.class);
    }

    public static Class getEntityClass(String entityName) {
        checkEntityName(entityName);
        Class entityClass = entitiesByName.get(entityName.toLowerCase());
        checkEntityClass(entityClass);
        return entityClass;
    }
}
