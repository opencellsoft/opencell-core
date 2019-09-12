package org.meveo.apiv2.services.generic;

import org.apache.commons.lang.reflect.FieldUtils;
import org.meveo.apiv2.services.generic.JsonGenericApiMapper.JsonGenericMapper;
import org.meveo.model.BaseEntity;
import org.meveo.model.persistence.JacksonUtil;
import org.meveo.service.base.PersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Entity;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.meveo.apiv2.ValidationUtils.checkEntityClass;
import static org.meveo.apiv2.ValidationUtils.checkEntityName;

public class GenericApiAlteringService extends GenericApiService {
    private static final Logger logger = LoggerFactory.getLogger(GenericApiAlteringService.class);
    private List<String> forbiddenFieldsToUpdate = Arrays.asList("id", "code", "uuid", "auditable",  "status");

    
    public Optional<String> update(String entityName, Long id, String jsonDto) {
        checkEntityName(entityName).checkId(id).checkDto(jsonDto);
        Class entityClass = entitiesByName.get(entityName.toLowerCase());
        checkEntityClass(entityClass);
        BaseEntity entityById = find(entityClass, id);
        BaseEntity parsedEntity = (BaseEntity) JacksonUtil.fromString(jsonDto, entityClass);
        updateEntityWithDtoFields(jsonDto, entityById, parsedEntity);
        PersistenceService service = getPersistenceService(entityClass);
        service.enable(entityById);
        service.update(entityById);

        return Optional.ofNullable(new JsonGenericMapper().toJson(null, entityClass, entityById));
    }

    public Long create(String entityName, String dto) {
        checkEntityName(entityName).checkDto(dto);
        Class entityClass = entitiesByName.get(entityName.toLowerCase());
        checkEntityClass(entityClass);
        BaseEntity entityToCreate = (BaseEntity) JacksonUtil.fromString(dto, entityClass);
        updateEntityWithDtoFields(dto, entityToCreate, entityToCreate);
        PersistenceService service = getPersistenceService(entityClass);
        service.create(entityToCreate);
        return entityToCreate.getId();
    }

    public Optional<String> delete(String entityName, Long id) {
        checkEntityName(entityName).checkId(id);
        Class entityClass = entitiesByName.get(entityName.toLowerCase());
        checkEntityClass(entityClass);
        BaseEntity entity = find(entityClass, id);
        PersistenceService service = getPersistenceService(entityClass);
        service.remove(entity);
        return Optional.ofNullable(new JsonGenericMapper().toJson(null, entityClass, entity));
    }

    private void updateEntityWithDtoFields(String dto, BaseEntity entity, BaseEntity entityToUpdate) {
        Iterable<String> fieldNames = () -> JacksonUtil.toJsonNode(dto).fieldNames();
        StreamSupport.stream(fieldNames.spliterator(), false)
                .filter(((Predicate<String>) forbiddenFieldsToUpdate::contains).negate())
                .forEach(fieldName -> FetchOrSetField(fieldName, entityToUpdate, entity));
    }
    private void FetchOrSetField(String fieldName, BaseEntity entityToUpdate, BaseEntity entity) {
        Field updatedField = FieldUtils.getField(entityToUpdate.getClass(), fieldName, true);
        try {
            Object value = updatedField.get(entityToUpdate);
            if(updatedField.getType().isAssignableFrom(List.class)){
                value = ((List<? extends BaseEntity>) value)
                        .parallelStream()
                        .map(o -> o.getId() == null ? o : entityManagerWrapper.getEntityManager().getReference(o.getClass(), o.getId()))
                        .collect(Collectors.toCollection(ArrayList::new));
            } else if(updatedField.getType().isAnnotationPresent(Entity.class) && ((BaseEntity) value).getId() != null){
                value = entityManagerWrapper.getEntityManager().getReference(value.getClass(), ((BaseEntity) value).getId());
            }
            updatedField.set(entity, value);
        } catch (IllegalAccessException e) {
            logger.error(String.format("Failed to update field %s", fieldName), e);
        }
    }

}
