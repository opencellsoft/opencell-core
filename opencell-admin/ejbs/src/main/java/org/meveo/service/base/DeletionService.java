package org.meveo.service.base;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.persistence.Entity;

import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IEntity;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.customEntities.CustomEntityInstance;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.custom.CustomEntityInstanceService;
import org.meveo.service.custom.CustomEntityTemplateService;
import org.meveo.service.custom.CustomTableService;

public class DeletionService {
    private static final String CANNOT_REMOVE_ENTITY_CUSTOM_TABLE_REFERENCE_ERROR_MESSAGE = "Cannot remove entity: reference to the entity exists";
    private static final String CUSTOM_ENTITY_PREFIX = "CE_";
    private static final String CUSTOM_ENTITY_CLASS_PREFIX = "org.meveo.model.customEntities.CustomEntityTemplate - %s";
    private static Map<String, Class> entitiesByName;

    static {
        entitiesByName = ReflectionUtils.getClassesAnnotatedWith(Entity.class).stream().collect(Collectors.toMap(clazz -> clazz.getSimpleName().toLowerCase(), clazz -> clazz));
    }

    @Inject
    private CustomEntityTemplateService customEntityTemplateService;

    @Inject
    private CustomFieldTemplateService customFieldTemplateService;

    @Inject
    private CustomEntityInstanceService customEntityInstanceService;


    @Inject
    private CustomTableService customTableService;

    public void checkTableNotreferenced(String tableName, Long id){
        CustomEntityInstance customEntityInstance = new CustomEntityInstance();
        customEntityInstance.setId(id);
        customEntityInstance.setCode(id.toString());
        customEntityInstance.setCetCode(tableName);
        checkEntityIsNotreferenced(customEntityInstance);
    }

    public void checkEntityIsNotreferenced(IEntity entity) {
        String entityClass = entity instanceof CustomEntityInstance ? String.format(CUSTOM_ENTITY_CLASS_PREFIX, ((CustomEntityInstance)entity).getCetCode()) : entity.getClass().getName();
        boolean isIncluded = isIncluded(entity, entityClass);

        if (isIncluded) {
            throw new MeveoApiException(CANNOT_REMOVE_ENTITY_CUSTOM_TABLE_REFERENCE_ERROR_MESSAGE);
        }
    }

    private boolean isIncluded(IEntity entity, String entityClass) {
        return customFieldTemplateService.list().stream().filter(c -> c.getEntityClazz() != null).filter(c -> c.getEntityClazz().equalsIgnoreCase(entityClass))
                .anyMatch(customField -> isEitherIncludedInCustomTableOrInBusinessEntity(customField, entity));
    }

    boolean isEitherIncludedInCustomTableOrInBusinessEntity(CustomFieldTemplate customField, IEntity dependency) {
            CustomEntityTemplate customEntityTemplate = customEntityTemplateService.findByCode(removePrefix(customField.getAppliesTo()));

            if(customEntityTemplate == null){
                return tryWithBusinessEntity(customField, dependency);
            }
            if(customEntityTemplate.isStoreAsTable()){
                return existsAsRecordInCustomTable(customField, dependency);
            }

            String className = (dependency instanceof CustomEntityInstance) ? ((CustomEntityInstance) dependency).getCetCode() : dependency.getClass().getName();
            return getCodeAsStream(dependency)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .findFirst()
                    .map(Object::toString)
                    .map(code -> isIncludedInBusinessOrCustomEntity(customEntityTemplate, code, className))
                    .orElse(false);

    }

    private Stream<Optional<Object>> getCodeAsStream(IEntity dependency) {
        return Stream.of(ReflectionUtils.getPropertyValueOrNull(dependency, "code"), ReflectionUtils.getMethodValue(dependency, "getCode"));
    }

    private boolean existsAsRecordInCustomTable(CustomFieldTemplate customField, IEntity entity) {
        return customTableService.containsRecordOfTableByColumn(removePrefix(customField.getAppliesTo()), customField.getCode(), Long.valueOf(entity.getId().toString()));
    }

    private boolean tryWithBusinessEntity(CustomFieldTemplate customField, IEntity dependency) {
        return getCodeAsStream(dependency)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .map(Object::toString)
                .map(code -> verifyPersistenceService(customField, code))
                .orElse(false);

    }

    private Boolean verifyPersistenceService(CustomFieldTemplate customField, String code) {
        Optional<PersistenceService> persistenceService = getPersistenceService(entitiesByName.get(customField.getAppliesTo().toLowerCase()));
        return persistenceService.map(p -> p.list().stream().anyMatch(holder -> verifyExistsReferenceInCfValues((ICustomFieldEntity) holder, code, customField.getEntityClazz()))).orElse(null);
    }

    private boolean verifyExistsReferenceInCfValues(ICustomFieldEntity businessCFEntity, String code, String className) {
        return Optional.ofNullable(businessCFEntity)
                .map(ICustomFieldEntity::getCfValues)
                .filter(c -> c.containsCfValue(code, className))
                .isPresent();
    }

    private boolean isIncludedInBusinessOrCustomEntity(CustomEntityTemplate customEntityTemplate, String code, String className) {
        return customEntityInstanceService.listByCet(customEntityTemplate.getCode())
                .stream()
                .anyMatch(s -> verifyExistsReferenceInCfValues(s, code, className));
    }

    String removePrefix(String appliesTo) {
        try {
            if(appliesTo.startsWith(CUSTOM_ENTITY_PREFIX)) {
                return appliesTo.substring(CUSTOM_ENTITY_PREFIX.length());
            }
            return appliesTo;
        } catch (ArrayIndexOutOfBoundsException ex) {
            return appliesTo;
        }


    }

    Optional<PersistenceService> getPersistenceService(Class entityClass) {
        return Optional.ofNullable(entityClass).map(e -> (PersistenceService) EjbUtils.getServiceInterface(e.getSimpleName() + "Service"));
    }

}
