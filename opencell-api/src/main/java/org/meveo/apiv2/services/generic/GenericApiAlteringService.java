package org.meveo.apiv2.services.generic;

import org.apache.commons.lang.reflect.FieldUtils;
import org.meveo.apiv2.services.generic.JsonGenericApiMapper.JsonGenericMapper;
import org.meveo.model.BaseEntity;
import org.meveo.model.persistence.JacksonUtil;
import org.meveo.service.base.PersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

import static org.meveo.apiv2.ValidationUtils.checkEntityClass;
import static org.meveo.apiv2.ValidationUtils.checkEntityName;

public class GenericApiAlteringService extends GenericApiService {
    private static final Logger logger = LoggerFactory.getLogger(GenericApiAlteringService.class);
    private List<Predicate<String>> updateConditions;

    @PostConstruct
    public void init(){
        this.updateConditions = Arrays.asList(
                fieldName -> Arrays.asList("id", "code", "uuid", "auditable",  "status").contains(fieldName),
                fieldName -> entitiesByName.containsKey(fieldName.toLowerCase())
        );
    }
    
    public Optional<String> update(String entityName, Long id, String dto) {
        checkEntityName(entityName).checkId(id).checkDto(dto);
        Class entityClass = entitiesByName.get(entityName.toLowerCase());
        checkEntityClass(entityClass);
        return Optional.ofNullable(new JsonGenericMapper().toJson(null, entityClass, update(dto, id, entityClass)));
    }
    
    private BaseEntity update(String dto, Long id, Class entityClass) {
        BaseEntity entity = find(entityClass, id);
    
        BaseEntity entityToUpdate = (BaseEntity) JacksonUtil.fromString(dto, entityClass);
        updateEntityWithdtoFields(dto, entity, entityToUpdate);
        PersistenceService service = getPersistenceService(entityClass);
        service.enable(entity);
        service.update(entity);
        return entity;
    }
    
    private void updateEntityWithdtoFields(String dto, BaseEntity entity, BaseEntity entityToUpdate) {
        Iterable<String> fieldNames = () -> JacksonUtil.toJsonNode(dto).fieldNames();
        StreamSupport.stream(fieldNames.spliterator(), false).filter(this::isFieldUpdatable).forEach(fieldName -> updateField(fieldName, entityToUpdate, entity));
    }
    
    private boolean isFieldUpdatable(String fieldName) {
        return this.updateConditions.stream().noneMatch(predicate -> predicate.test(fieldName));
    }
    
    private void updateField(String fieldName, BaseEntity entityToUpdate, BaseEntity entity) {
        Field updatedField = FieldUtils.getField(entityToUpdate.getClass(), fieldName, true);
        
        try {
            Object value = updatedField.get(entityToUpdate);
            updatedField.set(entity, value);
        } catch (IllegalAccessException e) {
            logger.error(String.format("Failed to update field %s", fieldName), e);
        }
    }

    public Long create(String entityName, String dto) {
        checkEntityName(entityName).checkDto(dto);
        Class entityClass = entitiesByName.get(entityName.toLowerCase());
        checkEntityClass(entityClass);

        BaseEntity entityToCreate = (BaseEntity) JacksonUtil.fromString(dto, entityClass);
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
    
}
