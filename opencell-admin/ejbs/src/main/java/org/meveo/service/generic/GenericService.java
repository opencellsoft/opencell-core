package org.meveo.service.generic;

import org.meveo.admin.exception.WrongModelNameException;
import org.meveo.admin.exception.WrongRequestedModelIdException;
import org.meveo.api.dto.generic.GenericRequestDto;
import org.meveo.api.dto.response.generic.GenericResponseDto;
import org.meveo.commons.utils.PersistenceUtils;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.BaseEntity;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Entity;
import javax.persistence.Query;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Stateless
public class GenericService implements GenericApi {

    private static final String SELECTOR_DEFAULT_SEPARATOR = ".";
    private static final String SELECTOR_DEFAULT_SEPARATOR_REGEX = "\\.";
    private static final String ID_PARAMETER = "id";
    private static final String RECORDS_REQUEST = "select %s from %s %s where %s.id = :id";

    @Inject
    @MeveoJpa
    private EntityManagerWrapper entityManagerWrapper;

    @Override
    public GenericResponseDto findBy(String requestedModelName, Long id, GenericRequestDto requestedDto) {
        return Optional.ofNullable(id).map(entityId -> load(requestedModelName, entityId, requestedDto)).orElseThrow(() -> new WrongRequestedModelIdException(id));
    }

    private GenericResponseDto load(String requestedModelName, Long id, GenericRequestDto requestedDto) {
        Class entityClass = loadModelClassNameBy(requestedModelName);
        List<String> requestedFields = getOrInitFields(requestedDto);
        String recordQuery = buildRecordRequest(entityClass);

        Query query = getQuery(id, recordQuery);
        Object result = query.getSingleResult();
        return buildGenericRespose(result, requestedFields);
    }

    Class loadModelClassNameBy(String entityName) {
        return Optional.ofNullable(entityName).filter(StringUtils::isNotBlank).flatMap(
                name -> ReflectionUtils.getClassesAnnotatedWith(Entity.class).stream()
                        .filter(clazz -> clazz.getSimpleName().toLowerCase().equals(name.toLowerCase())).findFirst())
                .orElseThrow(() -> new WrongModelNameException(entityName));
    }

    List<String> getOrInitFields(GenericRequestDto requestedDto) {
        return Optional.ofNullable(requestedDto).map(GenericRequestDto::getFields).orElse(Collections.emptyList());
    }

    String buildRecordRequest(Class entityClass) {
        String entityName = entityClass.getSimpleName();
        String selector = entityName.toLowerCase();
        return format(RECORDS_REQUEST, selector, entityName, selector, selector);
    }

    GenericResponseDto buildGenericRespose(Object result, List<String> fields) {
        Map<String, String> values = Optional.ofNullable(result).map(r -> {
            PersistenceUtils.initializeAllProperties(r);
            return getAllNonStaticFieldValues(r, fields);
        }).orElse(new HashMap<>());

        return new GenericResponseDto(values);
    }

     private Map<String, String> getAllNonStaticFieldValues(Object result, List<String> fields) {
        return ReflectionUtils.getAllFields(result.getClass()).stream().filter(field -> !Modifier.isStatic(field.getModifiers()))
                .filter(field -> fields.isEmpty() || fields.contains(field.getName().toLowerCase()))
                .collect(Collectors.toMap(Field::getName, field -> toJson(extractValueFromField(result, field))));
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
        return property.toString();

    }

    String extractFieldName(String fieldName) {
        return fieldName.contains(SELECTOR_DEFAULT_SEPARATOR) ? fieldName.split(SELECTOR_DEFAULT_SEPARATOR_REGEX)[0] : fieldName;
    }


    private Query getQuery(Long id, String recordQuery) {
        Query query = entityManagerWrapper.getEntityManager().createQuery(recordQuery);
        query.setParameter(ID_PARAMETER, id);
        return query;
    }
}
