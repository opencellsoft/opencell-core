package org.meveo.apiv2.services;

import org.hibernate.Hibernate;
import org.meveo.api.dto.generic.GenericRequestDto;
import org.meveo.api.dto.response.generic.GenericResponseDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.BaseEntity;
import org.meveo.model.persistence.JacksonUtil;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Entity;
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

import static org.meveo.api.MeveoApiErrorCodeEnum.ENTITY_DOES_NOT_EXISTS_EXCEPTION;
import static org.meveo.api.MeveoApiErrorCodeEnum.MISSING_PARAMETER;
import static org.meveo.apiv2.ValidationUtils.requireNonEmpty;
import static org.meveo.apiv2.ValidationUtils.requireNonNull;

@Stateless
public class GenericApiService {
    private static final String ENTITY_NAME_ERROR_MESSAGE = "The requested entity does not exist";
    private static final String EMPTY_ID_ERROR_MESSAGE = "The requested id should not be null";
    
    @Inject
    @MeveoJpa
    private EntityManagerWrapper entityManagerWrapper;
    private static Map<String, Class> entitiesByName = new HashMap<>();
    
    static {
        entitiesByName = ReflectionUtils.getClassesAnnotatedWith(Entity.class).stream().collect(Collectors.toMap(clazz -> clazz.getSimpleName().toLowerCase(), clazz -> clazz));
    }
    
    public GenericResponseDto findByClassNameAndId(String entityName, Long id, GenericRequestDto requestedDto) {
        requireNonEmpty(entityName, ENTITY_DOES_NOT_EXISTS_EXCEPTION, ENTITY_NAME_ERROR_MESSAGE);
        requireNonNull(id, MISSING_PARAMETER, EMPTY_ID_ERROR_MESSAGE);
        List<String> fields = Optional.ofNullable(requestedDto).map(GenericRequestDto::getFields).orElse(Collections.emptyList());
        return load(entityName, id, fields);
    }
    
    private GenericResponseDto load(String entityName, Long id, List<String> requestedFields) {
        Class entityClass = entitiesByName.get(entityName.toLowerCase());
        
        Object result = find(entityClass, id);
        return buildGenericResponse(result, requestedFields);
    }
    
    Object find(Class entityClass, Long id) {
        Object entity = entityManagerWrapper.getEntityManager().find(entityClass, id);
        return Optional.ofNullable(entity).orElseThrow(() -> new EntityDoesNotExistsException(entityClass.getSimpleName(), id
                .toString()));
    }
    
    GenericResponseDto buildGenericResponse(Object result, List<String> fields) {
        Hibernate.initialize(result);
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
        return JacksonUtil.toString(property);
    }
    
}
