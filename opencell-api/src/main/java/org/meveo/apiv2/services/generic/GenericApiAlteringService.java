package org.meveo.apiv2.services.generic;

import org.apache.commons.lang.reflect.FieldUtils;
import org.meveo.apiv2.services.generic.JsonGenericApiMapper.JsonGenericMapper;
import org.meveo.model.BaseEntity;
import org.meveo.service.base.PersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Entity;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.meveo.apiv2.ValidationUtils.checkEntityClass;
import static org.meveo.apiv2.ValidationUtils.checkEntityName;
import static org.meveo.apiv2.services.generic.GenericHelper.getEntityClass;

public class GenericApiAlteringService extends GenericApiService {
    private static final Logger logger = LoggerFactory.getLogger(GenericApiAlteringService.class);
    private List<String> forbiddenFieldsToUpdate = Arrays.asList("id", "uuid", "auditable");

    
    public Optional<String> update(String entityName, Long id, String jsonDto) {
        checkEntityName(entityName).checkId(id).checkDto(jsonDto);
        Class entityClass = getEntityClass(entityName.toLowerCase());
        checkEntityClass(entityClass);
        BaseEntity entityById = find(entityClass, id);
        JsonGenericMapper jsonGenericMapper = JsonGenericMapper.Builder.getBuilder().build();
        refreshEntityWithDotFields(JsonGenericMapper.Builder.getBuilder().build().readValue(jsonDto, Map.class), entityById, jsonGenericMapper.parseFromJson(jsonDto, entityClass));
        PersistenceService service = getPersistenceService(entityClass);
        service.enable(entityById);
        service.update(entityById);

        return Optional.ofNullable(jsonGenericMapper
                .toJson(null, entityClass, entityById));
    }


    public Long create(String entityName, String jsonDto) {
        checkEntityName(entityName).checkDto(jsonDto);
        Class entityClass = GenericHelper.getEntityClass(entityName.toLowerCase());
        checkEntityClass(entityClass);
        BaseEntity entityToCreate = JsonGenericMapper.Builder
                .getBuilder().build().parseFromJson(jsonDto, entityClass);
        refreshEntityWithDotFields(JsonGenericMapper.Builder.getBuilder().build().readValue(jsonDto, Map.class), entityToCreate, entityToCreate);
        PersistenceService service = getPersistenceService(entityClass);
        service.create(entityToCreate);
        return entityToCreate.getId();
    }

    public Optional<String> delete(String entityName, Long id) {
        checkEntityName(entityName).checkId(id);
        Class entityClass = getEntityClass(entityName.toLowerCase());
        checkEntityClass(entityClass);
        BaseEntity entity = find(entityClass, id);
        PersistenceService service = getPersistenceService(entityClass);
        service.remove(entity);
        return Optional.ofNullable(JsonGenericMapper.Builder
                .getBuilder().withNestedEntities(null).build()
                .toJson(null, entityClass, entity));
    }

    private void refreshEntityWithDotFields(Map<String,Object> readValueMap, Object fetchedEntity, Object parsedEntity) {
        readValueMap.keySet().forEach(key -> {if(!forbiddenFieldsToUpdate.contains(key)) FetchOrSetField(key, readValueMap, parsedEntity, fetchedEntity);});
    }

    private void FetchOrSetField(String fieldName, Map<String, Object> readValueMap, Object parsedEntity, Object fetchedEntity) {
        Field updatedField = FieldUtils.getField(parsedEntity.getClass(), fieldName, true);
        try {
            Object newValue = updatedField.get(parsedEntity);
            if(updatedField.getType().isAssignableFrom(List.class)){
                newValue = ((List<? extends BaseEntity>) newValue)
                        .stream()
                        .map(o -> o.getId() == null ? o : entityManagerWrapper.getEntityManager().getReference(o.getClass(), o.getId()))
                        .collect(Collectors.toCollection(ArrayList::new));
            } else if(updatedField.getType().isAnnotationPresent(Entity.class) && ((BaseEntity) newValue).getId() != null){
                newValue = entityManagerWrapper.getEntityManager().getReference(newValue.getClass(), ((BaseEntity) newValue).getId());
            } else if(readValueMap.get(fieldName) instanceof Map) {
                refreshEntityWithDotFields((Map<String, Object>) readValueMap.get(fieldName), newValue, newValue);
            }
            updatedField.set(fetchedEntity, newValue);
        } catch (IllegalAccessException e) {
            logger.error(String.format("Failed to update field %s", fieldName), e);
        }
    }

}
