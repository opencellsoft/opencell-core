package org.meveo.apiv2.services;

import org.meveo.api.dto.generic.GenericRequestDto;
import org.meveo.api.dto.response.generic.GenericResponseDto;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BaseEntity;
import org.meveo.model.persistence.JacksonUtil;

import javax.ejb.Stateless;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.meveo.apiv2.ValidationUtils.checkEntityClass;
import static org.meveo.apiv2.ValidationUtils.checkEntityName;

@Stateless
public class GenericApiLoadService extends GenericApiService {
    
    public GenericResponseDto findByClassNameAndId(String entityName, Long id, GenericRequestDto requestedDto) {
        checkEntityName(entityName).checkId(id);
        Class entityClass = entitiesByName.get(entityName.toLowerCase());
        checkEntityClass(entityClass);
        
        List<String> fields = Optional.ofNullable(requestedDto).map(GenericRequestDto::getFields).orElse(Collections.emptyList());
        
        return load(entityClass, id, fields);
    }
    
    private GenericResponseDto load(Class entityClass, Long id, List<String> requestedFields) {
        Object result = find(entityClass, id);
        return buildGenericResponse(result, requestedFields);
    }
    
    GenericResponseDto buildGenericResponse(Object result, List<String> fields) {
        Map<String, String> values = Optional.ofNullable(result).map(r -> getAllNonStaticFieldValues(r, fields)).orElse(new HashMap<>());
        return new GenericResponseDto(values);
    }
    
    private Map<String, String> getAllNonStaticFieldValues(Object result, List<String> fields) {
        if (fields.isEmpty()) {
            return ReflectionUtils.getAllFields(result.getClass()).stream().filter(field -> !Modifier.isStatic(field.getModifiers())).filter(Objects::nonNull)
                    .collect(Collectors.toMap(Field::getName, field -> toJson(extractValueFromField(result, field))));
        }
        return fields.stream().collect(Collectors.toMap(field -> field, field -> ReflectionUtils.getPropertyValueOrNull(result, field))).entrySet().stream()
                .filter(e -> e.getValue().isPresent()).collect(Collectors.toMap(Map.Entry::getKey, e -> toJson(e.getValue().get())));
    }
    
    Object extractValueFromField(Object result, Field field) {
        field.setAccessible(true);
        try {
            return Optional.ofNullable(field.get(result)).orElse(StringUtils.EMPTY);
        } catch (IllegalAccessException e) {
            return StringUtils.EMPTY;
        }
    }
    
    String toJson(Object property) {
        if (Collection.class.isAssignableFrom(property.getClass())) {
            Collection<BaseEntity> value = (Collection) property;
            return value.stream().map(BaseEntity::getId).map(String::valueOf).collect(Collectors.toList()).toString();
        } else if (BaseEntity.class.isAssignableFrom(property.getClass())) {
            return ((BaseEntity) property).getId().toString();
        }
        return property instanceof String ? property.toString() : JacksonUtil.toString(property);
    }
    
}
