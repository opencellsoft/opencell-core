package org.meveo.apiv2.services.generic.filter;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.meveo.apiv2.services.generic.filter.filtermapper.AuditableMapper;
import org.meveo.apiv2.services.generic.filter.filtermapper.CustomFieldMapper;
import org.meveo.apiv2.services.generic.filter.filtermapper.DateMapper;
import org.meveo.apiv2.services.generic.filter.filtermapper.DefaultMapper;
import org.meveo.apiv2.services.generic.filter.filtermapper.EnumMapper;
import org.meveo.apiv2.services.generic.filter.filtermapper.NumberMapper;
import org.meveo.apiv2.services.generic.filter.filtermapper.ObjectMapper;
import org.meveo.apiv2.services.generic.filter.filtermapper.ReferenceMapper;
import org.meveo.apiv2.services.generic.filter.filtermapper.TypeClassMapper;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.Auditable;
import org.meveo.model.IEntity;
import org.meveo.service.base.PersistenceService;
import org.slf4j.LoggerFactory;

public interface FactoryMapper {
	static final org.slf4j.Logger log = LoggerFactory.getLogger(FactoryMapper.class);
    Set<String> simpleField = new HashSet<>(Arrays.asList("id", "type_class", "cfValues"));
    default FilterMapper create(String property, Object value, Class clazz, Function<Class, PersistenceService> entityManagerResolver) {
        if(simpleField.contains(property) || clazz != null && clazz.getSimpleName().equalsIgnoreCase(property)){
            return resolveFilterMapperType(property, value, clazz, entityManagerResolver);
        }
        try {
            Field field = ReflectionUtils.getFieldThrowException(clazz, property);
            if(field != null){
                if(List.class.isAssignableFrom(field.getType())){
                    Type type = field.getGenericType();
                    if (type instanceof ParameterizedType) {
                        Type[] pt = ((ParameterizedType) type).getActualTypeArguments();
                        return resolveFilterMapperType(property, value, (Class) pt[0], entityManagerResolver);
                    }
                }
                return resolveFilterMapperType(property.substring(property.lastIndexOf(".")+1), value, field.getType(), entityManagerResolver);
            }
        } catch (NoSuchFieldException e) {
            log.error("error = {}", e);
            throw new IllegalArgumentException("Invalid argument : " + property);
        }
        return null;
    }

    default FilterMapper resolveFilterMapperType(String property, Object value, Class clazz, Function<Class, PersistenceService> entityManagerResolver){
        // TODO : to switch
        if(value instanceof Map && ((Map) value).containsKey("id")){
            return new ReferenceMapper(property, ((Map) value).get("id"), clazz, entityManagerResolver);
        }
        if("cfValues".equalsIgnoreCase(property)){
            return new CustomFieldMapper(property, value, clazz, entityManagerResolver);
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
        if(Auditable.class.isAssignableFrom(clazz)){ // todo : use clazz.isAnnotationPresent(Embeddable.class) instead
            return new AuditableMapper(property, value, clazz);
        }
        if("type_class".equalsIgnoreCase(property)){
            return new TypeClassMapper(property, value);
        }
        if(value instanceof Map || IEntity.class.isAssignableFrom(clazz)){
            return new ObjectMapper(property, value, clazz, entityManagerResolver);
        }
        return new DefaultMapper(property, value);
    }
}
