package org.meveo.apiv2.services.generic;

import org.apache.commons.lang.reflect.FieldUtils;
import org.meveo.api.BaseApi;
import org.meveo.api.TaxApi;
import org.meveo.api.dto.CustomFieldDto;
import org.meveo.api.dto.CustomFieldValueDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.EntityReferenceDto;
import org.meveo.apiv2.services.generic.JsonGenericApiMapper.JsonGenericMapper;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.model.BaseEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Entity;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static org.meveo.apiv2.ValidationUtils.checkEntityClass;
import static org.meveo.apiv2.ValidationUtils.checkEntityName;

public class GenericApiAlteringService extends GenericApiService {
    private static final Logger logger = LoggerFactory.getLogger(GenericApiAlteringService.class);
    private List<String> forbiddenFieldsToUpdate = Arrays.asList("id", "uuid", "auditable");
    private BaseApi baseApi = (BaseApi) EjbUtils.getServiceInterface(TaxApi.class.getSimpleName());
    
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
                parseSingleValuedCf(customFieldDto, fieldType, effectiveValue);
                break;
            case LIST:
                customFieldDto.setListValue(new ArrayList<>());
                List listValues = (List) effectiveValue;
                for(Object obj: listValues){
                    CustomFieldValueDto customFieldValueDto = new CustomFieldValueDto();
                    customFieldValueDto.setValue(getConvertedType(fieldType, obj));
                    customFieldDto.getListValue().add(customFieldValueDto);
                }
                break;
            case MAP:
                Map<String, Object> mapValues = (Map) effectiveValue;
                customFieldDto.setMapValue(new LinkedHashMap<String, CustomFieldValueDto>());
                for(String key: mapValues.keySet()){
                    CustomFieldValueDto customFieldValueDto = new CustomFieldValueDto();
                    customFieldValueDto.setValue(getConvertedType(fieldType, mapValues.get(key)));
                    customFieldDto.getMapValue().put(key, customFieldValueDto);
                }
                break;
            case MATRIX:
                Map<Object, Object> matrixValues = (Map) effectiveValue;
                customFieldDto.setMapValue(new LinkedHashMap<String, CustomFieldValueDto>());
                for(Object key: matrixValues.keySet()){
                    CustomFieldValueDto customFieldValueDto = new CustomFieldValueDto();
                    customFieldValueDto.setValue(getConvertedType(fieldType, matrixValues.get(key)));
                    customFieldDto.getMapValue().put(key.toString(), customFieldValueDto);
                }
                break;
        }
    }

    private void parseSingleValuedCf(CustomFieldDto customFieldDto, CustomFieldTypeEnum fieldType, Object value) {
        switch (fieldType) {
            case DATE:
                customFieldDto.setDateValue((Date) getConvertedType(fieldType, value));
                break;
            case LONG:
                customFieldDto.setLongValue((Long) getConvertedType(fieldType, value));
                break;
            case DOUBLE:
                customFieldDto.setDoubleValue((Double) getConvertedType(fieldType, value));
                break;
            case BOOLEAN:
                customFieldDto.setBooleanValue((Boolean) getConvertedType(fieldType, value));
                break;
            case CHILD_ENTITY:
            case ENTITY:
                customFieldDto.setEntityReferenceValue((EntityReferenceDto) getConvertedType(fieldType, value));
            case LIST:
                if(!((List)value).isEmpty()){
                    customFieldDto.setStringValue((String) getConvertedType(fieldType, ((Map)((List)value).get(0)).get("value")));
                }
                break;
            default:
                customFieldDto.setStringValue((String) getConvertedType(fieldType, value));
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
