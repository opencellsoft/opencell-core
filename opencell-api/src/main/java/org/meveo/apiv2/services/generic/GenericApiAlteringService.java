package org.meveo.apiv2.services.generic;

import static org.meveo.apiv2.ValidationUtils.checkEntityClass;
import static org.meveo.apiv2.ValidationUtils.checkEntityName;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.Entity;

import org.apache.commons.lang.reflect.FieldUtils;
import org.hibernate.collection.internal.PersistentBag;
import org.meveo.api.BaseApi;
import org.meveo.api.TaxApi;
import org.meveo.api.dto.CustomFieldDto;
import org.meveo.api.dto.CustomFieldValueDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.EntityReferenceDto;
import org.meveo.apiv2.services.generic.JsonGenericApiMapper.JsonGenericMapper;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IEntity;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericApiAlteringService extends GenericApiService {
    private static final Logger logger = LoggerFactory.getLogger(GenericApiAlteringService.class);
    private List<String> forbiddenFieldsToUpdate = Arrays.asList("id", "uuid", "auditable");
    private BaseApi baseApi = (BaseApi) EjbUtils.getServiceInterface(TaxApi.class.getSimpleName());

    public Optional<String> update(String entityName, Long id, String jsonDto) {
        checkEntityName(entityName).checkId(id).checkDto(jsonDto);
        Class entityClass = getEntityClass(entityName.toLowerCase());
        checkEntityClass(entityClass);
        IEntity entityById = find(entityClass, id);
        JsonGenericMapper jsonGenericMapper = JsonGenericMapper.Builder.getBuilder().build();
        refreshEntityWithDotFields(jsonGenericMapper.readValue(jsonDto, Map.class), entityById, jsonGenericMapper.parseFromJson(jsonDto, entityById.getClass()));
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
        IEntity entityToCreate = JsonGenericMapper.Builder
                .getBuilder().build().parseFromJson(jsonDto, entityClass);
        refreshEntityWithDotFields(JsonGenericMapper.Builder.getBuilder().build().readValue(jsonDto, Map.class), entityToCreate, entityToCreate);
        PersistenceService service = getPersistenceService(entityClass);
        service.create(entityToCreate);
        return (Long) entityToCreate.getId();
    }

    public Optional<String> delete(String entityName, Long id) {
        checkEntityName(entityName).checkId(id);
        Class entityClass = getEntityClass(entityName.toLowerCase());
        checkEntityClass(entityClass);
        IEntity entity = find(entityClass, id);
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
        if("cfValues".equalsIgnoreCase(fieldName)) {
            CustomFieldTemplateService customFieldTemplateService = (CustomFieldTemplateService) getPersistenceService(CustomFieldTemplate.class);
            Map<String, CustomFieldTemplate> customFieldTemplates = customFieldTemplateService.findByAppliesTo((ICustomFieldEntity) parsedEntity);
            if (customFieldTemplates != null) {
                CustomFieldsDto customFieldsDto = getCustomFieldsDto(readValueMap, customFieldTemplates);
                baseApi.populateCustomFieldsForGenericApi(customFieldsDto, (ICustomFieldEntity) fetchedEntity, false);
            }
        } else {
            Field updatedField = FieldUtils.getField(parsedEntity.getClass(), fieldName, true);
            try {
                Object newValue = updatedField.get(parsedEntity);
                if(updatedField.getType().isAssignableFrom(List.class)){
                    newValue = getReadyToBeSavedListEntities(fetchedEntity, updatedField, (List<? extends IEntity>) newValue);
                } else if(updatedField.getType().isAnnotationPresent(Entity.class)){
                    newValue = ((IEntity) newValue).getId() != null ? entityManagerWrapper.getEntityManager().getReference(newValue.getClass(), ((IEntity) newValue).getId()) : null;
                }
                else if(readValueMap.get(fieldName) instanceof Map) {
                    refreshEntityWithDotFields((Map<String, Object>) readValueMap.get(fieldName), newValue, newValue);
                }
                updatedField.set(fetchedEntity, newValue);
            } catch (IllegalAccessException e) {
                logger.error(String.format("Failed to update field %s", fieldName), e);
            }
        }

    }

    private Object getReadyToBeSavedListEntities(Object fetchedEntity, Field updatedField, List<? extends IEntity> newValue) {
        Object listOfEntities = newValue
                .stream()
                .map(o -> o.getId() == null ? o : entityManagerWrapper.getEntityManager().getReference(getGenericClassName(updatedField.getGenericType().getTypeName()), o.getId()))
                .collect(Collectors.toCollection(ArrayList::new));
        try {
            // handels orphans-delete-all-error
            if(updatedField.get(fetchedEntity) instanceof PersistentBag){
                PersistentBag list = (PersistentBag) updatedField.get(fetchedEntity);
                list.clear();
                list.addAll((Collection) listOfEntities);
                return list;
            }
        } catch (IllegalAccessException e) {
        	logger.error("error = {}", e);
        }
        return listOfEntities;
    }

    private Class getGenericClassName(String typeName) {
        String className = typeName.substring(typeName.lastIndexOf(".") + 1, typeName.lastIndexOf(">"));
        return getEntityClass(className);
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
                customFieldDto.setDoubleValue((Double) value);
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

}
