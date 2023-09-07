package org.meveo.apiv2.generic.core.filter;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;

import org.meveo.apiv2.generic.core.filter.filtermapper.AuditableMapper;
import org.meveo.apiv2.generic.core.filter.filtermapper.CustomFieldMapper;
import org.meveo.apiv2.generic.core.filter.filtermapper.DateMapper;
import org.meveo.apiv2.generic.core.filter.filtermapper.DefaultMapper;
import org.meveo.apiv2.generic.core.filter.filtermapper.EnumMapper;
import org.meveo.apiv2.generic.core.filter.filtermapper.NumberMapper;
import org.meveo.apiv2.generic.core.filter.filtermapper.ObjectMapper;
import org.meveo.apiv2.generic.core.filter.filtermapper.ReferenceMapper;
import org.meveo.apiv2.generic.core.filter.filtermapper.TypeClassMapper;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.Auditable;
import org.meveo.model.BaseEntity;
import org.meveo.service.base.PersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface FactoryMapper {
	Logger log = LoggerFactory.getLogger(FactoryMapper.class);
    Set<String> simpleField = new HashSet<>(Arrays.asList("id", "type_class", "cfValues"));
    default FilterMapper create(String property, Object value, String cetCode, Function<Class, PersistenceService> entityManagerResolver, Class clazz) {
        if(simpleField.contains(property) || clazz != null && clazz.getSimpleName().equalsIgnoreCase(property)){
            return resolveFilterMapperType(property, value, clazz, cetCode, entityManagerResolver);
        }
        try {
            if(property.endsWith(".cfValues")) {
                Field field = ReflectionUtils.getFieldThrowException(clazz, property.replace(".cfValues", ""), true);
                if(field != null){
                    if(Collection.class.isAssignableFrom(field.getType())){
                        Type type = field.getGenericType();
                        if (type instanceof ParameterizedType) {
                            Type[] pt = ((ParameterizedType) type).getActualTypeArguments();
                            return resolveFilterMapperType("cfValues", value, (Class) pt[0], cetCode, entityManagerResolver);
                        }
                    }
                    return resolveFilterMapperType("cfValues", value, field.getType(), cetCode, entityManagerResolver);
                }
            }

            Field field = ReflectionUtils.getFieldThrowException(clazz, property, true);
            if(field != null){
                if(Collection.class.isAssignableFrom(field.getType())){
                    Type type = field.getGenericType();
                    if (type instanceof ParameterizedType) {
                        Type[] pt = ((ParameterizedType) type).getActualTypeArguments();
                        return resolveFilterMapperType(property, value, (Class) pt[0], cetCode, entityManagerResolver);
                    }
                }
                return resolveFilterMapperType(property.substring(property.lastIndexOf(".")+1), value, field.getType(), cetCode, entityManagerResolver);
            }
        } catch (NoSuchFieldException e) {
            log.error("error = {}", e);
            throw new IllegalArgumentException("Invalid argument : " + property);
        }
        return null;
    }

    default FilterMapper resolveFilterMapperType(String property, Object value, Class clazz, String cetCode, Function<Class, PersistenceService> entityManagerResolver){
        // TODO : to switch
        if(value instanceof Map && ((Map) value).containsKey("id")){
            return new ReferenceMapper(property, ((Map) value).get("id"), clazz, entityManagerResolver);
        }
        if("cfValues".equalsIgnoreCase(property)){
            return new CustomFieldMapper(property, value, clazz, cetCode, entityManagerResolver);
        }
        if(Date.class.isAssignableFrom(clazz)){
            return new DateMapper(property, value);
        }
        if(Number.class.isAssignableFrom(clazz) || "id".equalsIgnoreCase(property)){
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
