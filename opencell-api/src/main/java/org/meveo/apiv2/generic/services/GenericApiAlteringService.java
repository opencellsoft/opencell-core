package org.meveo.apiv2.generic.services;

import org.apache.commons.lang.reflect.FieldUtils;
import org.hibernate.collection.internal.PersistentBag;
import org.hibernate.collection.internal.PersistentSet;
import org.meveo.api.BaseApi;
import org.meveo.api.TaxApi;
import org.meveo.api.dto.CustomFieldDto;
import org.meveo.api.dto.CustomFieldValueDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.EntityReferenceDto;
import org.meveo.apiv2.generic.core.GenericHelper;
import org.meveo.apiv2.generic.core.mapper.JsonGenericMapper;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IEntity;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.ws.rs.NotFoundException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.meveo.apiv2.generic.ValidationUtils.checkDto;
import static org.meveo.apiv2.generic.ValidationUtils.checkId;

@Stateless
public class GenericApiAlteringService {

    private static final Logger logger = LoggerFactory.getLogger(GenericApiAlteringService.class);
    private List<String> forbiddenFieldsToUpdate = Arrays.asList("id", "uuid", "auditable");
    private BaseApi baseApi = (BaseApi) EjbUtils.getServiceInterface(TaxApi.class.getSimpleName());

    @Inject
    private GenericApiPersistenceDelegate persistenceDelegate;

    @Inject
    @MeveoJpa
    private EntityManagerWrapper entityManagerWrapper;

    public String update(String entityName, Long id, String jsonDto) {
        checkId(id).checkDto(jsonDto);
        Class entityClass = GenericHelper.getEntityClass(entityName);
        IEntity iEntity = PersistenceServiceHelper.getPersistenceService(entityClass).findById(id);

        if (iEntity == null) {
            throw new NotFoundException("entity " + entityName + " with id " + id + " not found.");
        }
        JsonGenericMapper jsonGenericMapper = JsonGenericMapper.Builder.getBuilder().build();
        refreshEntityWithDotFields(jsonGenericMapper.readValue(jsonDto, Map.class), iEntity, jsonGenericMapper.parseFromJson(jsonDto, iEntity.getClass()));
        IEntity updatedEntity = persistenceDelegate.update(entityClass, iEntity);
        return jsonGenericMapper.toJson(null, entityClass, updatedEntity);
    }


    public Optional<Long> create(String entityName, String jsonDto) {
        checkDto(jsonDto);
        Class entityClass = GenericHelper.getEntityClass(entityName);
        IEntity entityToCreate = JsonGenericMapper.Builder
                .getBuilder().build().parseFromJson(jsonDto, entityClass);
        refreshEntityWithDotFields(JsonGenericMapper.Builder.getBuilder().build().readValue(jsonDto, Map.class), entityToCreate, entityToCreate);
        persistenceDelegate.create(entityClass, entityToCreate);
        return Optional.ofNullable((Long) entityToCreate.getId());
    }

    public String delete(String entityName, Long id) {
        checkId(id);
        Class entityClass = GenericHelper.getEntityClass(entityName);
        IEntity iEntity = PersistenceServiceHelper.getPersistenceService(entityClass).findById(id);
        if (iEntity == null) {
            throw new NotFoundException("entity " + entityName + " with id " + id + " not found.");
        }
        persistenceDelegate.remove(entityClass, iEntity);
        return JsonGenericMapper.Builder.getBuilder().withNestedEntities(null).build().toJson(null, entityClass, iEntity);
    }

    public void refreshEntityWithDotFields(Map<String, Object> readValueMap, Object fetchedEntity, Object parsedEntity) {
        readValueMap.keySet().forEach(key -> {
            if (!forbiddenFieldsToUpdate.contains(key))
                FetchOrSetField(key, readValueMap, parsedEntity, fetchedEntity);
        });
    }

    private void FetchOrSetField(String fieldName, Map<String, Object> readValueMap, Object parsedEntity, Object fetchedEntity) {
        if ("cfValues".equalsIgnoreCase(fieldName)) {
            CustomFieldTemplateService customFieldTemplateService = (CustomFieldTemplateService) PersistenceServiceHelper.getPersistenceService(CustomFieldTemplate.class);
            Map<String, CustomFieldTemplate> customFieldTemplates = customFieldTemplateService.findByAppliesTo((ICustomFieldEntity) parsedEntity);
            if (customFieldTemplates != null) {
                CustomFieldsDto customFieldsDto = getCustomFieldsDto(readValueMap, customFieldTemplates);
                baseApi.populateCustomFieldsForGenericApi(customFieldsDto, (ICustomFieldEntity) fetchedEntity, false);
            }
        } else {
            Field updatedField = FieldUtils.getField(parsedEntity.getClass(), fieldName, true);
            if (updatedField != null) {
                try {
                    Object newValue = updatedField.get(parsedEntity);
                    if(updatedField.getType().isAssignableFrom(List.class)){
                        newValue = getReadyToBeSavedListEntities(fetchedEntity, updatedField, (List<? extends IEntity>) newValue);
                    } else if(updatedField.getType().isAssignableFrom(Set.class)){
                        newValue = getReadyToBeSavedListEntities(fetchedEntity, updatedField, (Set<? extends IEntity>) newValue);
                    } else if(updatedField.getType().isAnnotationPresent(Entity.class)){
                        newValue = fetchEntityById(newValue.getClass(), ((IEntity) newValue).getId());
                    }
                    else if(readValueMap.get(fieldName) instanceof Map) {
                        refreshEntityWithDotFields((Map<String, Object>) readValueMap.get(fieldName), newValue, newValue);
                    }
                    updatedField.set(fetchedEntity, newValue);
                    //throw new IllegalArgumentException("a field name or value does not exist in " + parsedEntity.getClass());
                } catch (IllegalAccessException e) {
                    logger.error(String.format("Failed to update field %s", fieldName), e);
                }
            }
        }
    }

    private Object fetchEntityById(Class<?> clazz, Object id) {
        return id != null ? entityManagerWrapper.getEntityManager().getReference(clazz, id) : null;
    }

    private Object getReadyToBeSavedListEntities(Object fetchedEntity, Field updatedField, Collection<? extends IEntity> newValue) {
        Stream<Object> listOfEntities = newValue
                .stream()
                .map(o -> fetchEntityById(getGenericClassName(updatedField.getGenericType().getTypeName()), o.getId()));
        try {
            final Object field = updatedField.get(fetchedEntity);
            // handels orphans-delete-all-error
            if(field instanceof PersistentBag){
                PersistentBag list = (PersistentBag) field;
                list.clear();
                list.addAll(listOfEntities.collect(Collectors.toList()));
                return list;
            }
            if(field instanceof PersistentSet){
                PersistentSet set = (PersistentSet) field;
                set.clear();
                set.addAll(listOfEntities.collect(Collectors.toSet()));
                return set;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return newValue instanceof List ? listOfEntities.collect(Collectors.toList()) : listOfEntities.collect(Collectors.toSet());
    }

    private Class getGenericClassName(@NotNull String typeName) {
        String className = typeName.substring(typeName.lastIndexOf(".") + 1, typeName.lastIndexOf(">"));
        return GenericHelper.getEntityClass(className);
    }

    private CustomFieldsDto getCustomFieldsDto(Map<String, Object> readValueMap, Map<String, CustomFieldTemplate> customFieldTemplates) {
        CustomFieldsDto customFieldsDto = new CustomFieldsDto();
        Map<String, Object> cfsValuesByCode = (Map<String, Object>) ((Map)readValueMap.get("cfValues")).get("valuesByCode");
        for(String code : cfsValuesByCode.keySet()){
            CustomFieldTemplate cft = customFieldTemplates.get(code);
            if(cft != null){
                CustomFieldDto customFieldDto = new CustomFieldDto();
                customFieldDto.setCode(code);
                writeValueToCFDto(customFieldDto, cft.getFieldType(), cft.getStorageType(), cfsValuesByCode.get(code));
                customFieldsDto.getCustomField().add(customFieldDto);
            }
        }
        return customFieldsDto;
    }

    private void writeValueToCFDto(CustomFieldDto customFieldDto, CustomFieldTypeEnum fieldType, CustomFieldStorageTypeEnum storageType, Object value) {
        Object effectiveValue = ((Map) ((List) value).get(0)).get("value");
        switch (storageType){
            case SINGLE:
                writeSingleValueToCFDto(customFieldDto, fieldType, effectiveValue);
                break;
            case LIST:
                customFieldDto.setListValue(new ArrayList<>());
                List listValues = (List) effectiveValue;
                if(effectiveValue == null){break;}
                for(Object obj: listValues){
                    CustomFieldValueDto customFieldValueDto = new CustomFieldValueDto();
                    customFieldValueDto.setValue(getConvertedType(fieldType, obj));
                    customFieldDto.getListValue().add(customFieldValueDto);
                }
                break;
            case MAP:
                Map<String, Object> mapValues = (Map) effectiveValue;
                if(mapValues == null){break;}
                customFieldDto.setMapValue(new LinkedHashMap<String, CustomFieldValueDto>());
                for(String key: mapValues.keySet()){
                    CustomFieldValueDto customFieldValueDto = new CustomFieldValueDto();
                    customFieldValueDto.setValue(getConvertedType(fieldType, mapValues.get(key)));
                    customFieldDto.getMapValue().put(key, customFieldValueDto);
                }
                break;
            case MATRIX:
                Map<Object, Object> matrixValues = (Map) effectiveValue;
                if(matrixValues == null){break;}
                customFieldDto.setMapValue(new LinkedHashMap<String, CustomFieldValueDto>());
                for(Object key: matrixValues.keySet()){
                    CustomFieldValueDto customFieldValueDto = new CustomFieldValueDto();
                    customFieldValueDto.setValue(getConvertedType(fieldType, matrixValues.get(key)));
                    customFieldDto.getMapValue().put(key.toString(), customFieldValueDto);
                }
                break;
        }
    }

    private void writeSingleValueToCFDto(CustomFieldDto customFieldDto, CustomFieldTypeEnum fieldType, Object value) {
        switch (fieldType) {
            case DATE:
                customFieldDto.setDateValue(new Date((Long) value));
                break;
            case LONG:
                customFieldDto.setLongValue(Integer.toUnsignedLong((Integer) value));
                break;
            case DOUBLE:
                customFieldDto.setDoubleValue(Double.parseDouble(value.toString()));
                break;
            case BOOLEAN:
                customFieldDto.setBooleanValue((Boolean) value);
                break;
            case CHILD_ENTITY:
            case ENTITY:
                Map<String, String> entityRefDto = (Map<String, String>) value;
                EntityReferenceDto entityReferenceDto = new EntityReferenceDto();
                entityReferenceDto.setClassname(entityRefDto.get("classname"));
                entityReferenceDto.setCode(entityRefDto.get("code"));
                customFieldDto.setEntityReferenceValue(entityReferenceDto);
                break;
            case LIST:
                if(!((List)value).isEmpty()){
                    customFieldDto.setStringValue((String)  ((Map)((List)value).get(0)).get("value"));
                }
                break;
            default:
                customFieldDto.setStringValue((String) value);
                break;
        }
    }

    private Object getConvertedType(CustomFieldTypeEnum fieldType, Object value) {
        switch (fieldType){
        case DATE:
            return new Date((Long) value);
        case ENTITY:
            Map<String, String> entityRefDto = (Map<String, String>) value;
            EntityReferenceDto entityReferenceDto = new EntityReferenceDto();
            entityReferenceDto.setClassname(entityRefDto.get("classname"));
            entityReferenceDto.setCode(entityRefDto.get("code"));
            return entityReferenceDto;
        default:
            return value;
        }
    }

    public void addForbiddenFieldsToUpdate(List<String> fields) {
        if (forbiddenFieldsToUpdate == null) {
            forbiddenFieldsToUpdate = new ArrayList<>();
        }
        forbiddenFieldsToUpdate.addAll(fields);
    }

}
