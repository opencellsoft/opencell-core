package org.meveo.apiv2.services.generic.filter;

import org.apache.commons.lang.reflect.FieldUtils;
import org.meveo.apiv2.services.generic.filter.filtermapper.*;
import org.meveo.model.Auditable;
import org.meveo.model.BaseEntity;

import javax.persistence.EntityManager;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

public interface FactoryMapper {
    default FilterMapper create(String property, Object value, Class clazz, Function<Class, EntityManager> entityManagerResolver) {
        Field field = FieldUtils.getField(clazz, property, true);
        if("id".equalsIgnoreCase(property) || "type_class".equalsIgnoreCase(property) || clazz != null && clazz.getSimpleName().equalsIgnoreCase(property)){
            return resolveType(property, value, clazz, entityManagerResolver);
        }
        if(field != null){
            return resolveType(property, value, field.getType(), entityManagerResolver);
        }
        throw new IllegalArgumentException("Invalid argument : " + property);
    }

    default FilterMapper resolveType(String property, Object value, Class clazz, Function<Class, EntityManager> entityManagerResolver){
        if("id".equalsIgnoreCase(property)){
            return new ReferenceMapper(property, value, clazz, entityManagerResolver);
        }
        if(Date.class.isAssignableFrom(clazz)){
            return new DateMapper(property, value);
        }
        if(Number.class.isAssignableFrom(clazz)){
            return new NumberMapper(property, value, clazz);
        }
        if(clazz.isEnum()){
            return new EnumMapper(property, value, clazz);
        }
        if(Auditable.class.isAssignableFrom(clazz)){
            return new AuditableMapper(property, value, clazz);
        }
        if("type_class".equalsIgnoreCase(property)){
            return new TypeClassMapper(property, value);
        }
        if(value instanceof Map || BaseEntity.class.isAssignableFrom(clazz)){
            return new ObjectMapper(property, value, clazz, entityManagerResolver);
        }
        return new DefaultMapper(property, value);
    }
}
