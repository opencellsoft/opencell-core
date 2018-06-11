package org.meveo.service.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.dto.CustomEntityInstanceDto;
import org.meveo.api.dto.CustomFieldDto;
import org.meveo.api.dto.CustomFieldValueDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.EntityReferenceDto;
import org.meveo.api.dto.LanguageDescriptionDto;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IEntity;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.crm.Provider;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.crm.custom.CustomFieldValue;
import org.meveo.model.customEntities.CustomEntityInstance;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.custom.CustomEntityInstanceService;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;

/**
 * The Class EntityToDtoConverter.
 * 
 * @lastModifiedVersion 5.0.2
 */
@Stateless
public class EntityToDtoConverter {

    /** The logger. */
    @Inject
    private Logger logger;

    /** The custom field template service. */
    @Inject
    private CustomFieldTemplateService customFieldTemplateService;

    /** The custom entity instance service. */
    @Inject
    private CustomEntityInstanceService customEntityInstanceService;

    /** The app provider. */
    @Inject
    @ApplicationProvider
    protected Provider appProvider;

    /**
     * Gets the custom fields DTO.
     *
     * @param entity the entity
     * @return the custom fields DTO
     */
    public CustomFieldsDto getCustomFieldsDTO(ICustomFieldEntity entity) {
        return getCustomFieldsDTO(entity, false);
    }

    /**
     * Gets the custom fields DTO.
     *
     * @param entity the entity
     * @param includeInheritedCF the include inherited CF
     * @return the custom fields DTO
     */
    public CustomFieldsDto getCustomFieldsDTO(ICustomFieldEntity entity, boolean includeInheritedCF) {
        return getCustomFieldsDTO(entity, includeInheritedCF, false);
    }

    /**
     * Gets the custom fields DTO.
     *
     * @param entity the entity
     * @param includeInheritedCF the include inherited CF
     * @param mergeMapValues the merge map values
     * @return the custom fields DTO
     */
    public CustomFieldsDto getCustomFieldsDTO(ICustomFieldEntity entity, boolean includeInheritedCF, boolean mergeMapValues) {
        return getCustomFieldsDTO(entity, null, includeInheritedCF, mergeMapValues);
    }

    /**
     * Gets the custom fields DTO.
     *
     * @param entity entity
     * @param cfValuesByCode List of custom field values by code. If null, value from entity.getCFValues() will be used.
     * @param includeInheritedCF If true, also returns the inherited cfs
     * @param mergeMapValues If true, merge the map values between instance cf and parent. Use to show a single list of values.
     * @return Custom fields values as DTOs
     */
    public CustomFieldsDto getCustomFieldsDTO(ICustomFieldEntity entity, Map<String, List<CustomFieldValue>> cfValuesByCode, boolean includeInheritedCF, boolean mergeMapValues) {
        return getCustomFieldsDTO(entity, cfValuesByCode, CustomFieldInheritanceEnum.getInheritCF(includeInheritedCF, mergeMapValues));
    }

    /**
     * Gets the custom fields DTO.
     *
     * @param entity the entity
     * @param inheritCF the inherit CF
     * @return the custom fields DTO
     */
    public CustomFieldsDto getCustomFieldsDTO(ICustomFieldEntity entity, CustomFieldInheritanceEnum inheritCF) {
        return getCustomFieldsDTO(entity, null, inheritCF);
    }

    /**
     * Gets the custom fields DTO.
     *
     * @param entity the entity
     * @param cfValues the cf values
     * @param inheritCF the inherit CF
     * @return the custom fields DTO
     */
    public CustomFieldsDto getCustomFieldsDTO(ICustomFieldEntity entity, Map<String, List<CustomFieldValue>> cfValues, CustomFieldInheritanceEnum inheritCF) {

        if (cfValues == null && entity.getCfValues() != null) {
            cfValues = entity.getCfValues().getValuesByCode();
        }

        if (cfValues == null && inheritCF == CustomFieldInheritanceEnum.INHERIT_NONE) {
            return null;
        }

        CustomFieldsDto currentEntityCFs = new CustomFieldsDto();

        Map<String, CustomFieldTemplate> cfts = customFieldTemplateService.findByAppliesTo(entity);

        logger.trace("Get Custom fields for \nEntity: {}/{}\nCustom Field Values: {}", entity.getClass().getSimpleName(), ((IEntity) entity).getId(), cfValues);

        if (cfValues != null && !cfValues.isEmpty()) {
            for (Entry<String, List<CustomFieldValue>> cfValueInfo : cfValues.entrySet()) {
                String cfCode = cfValueInfo.getKey();
                // Return only those values that have cft
                if (!cfts.containsKey(cfCode)) {
                    continue;
                }
                for (CustomFieldValue cfValue : cfValueInfo.getValue()) {
                    currentEntityCFs.getCustomField().add(customFieldToDTO(cfCode, cfValue, cfts.get(cfCode)));
                }
            }
        }

        // add parent cf values if inherited
        if (inheritCF != CustomFieldInheritanceEnum.INHERIT_NONE && entity.getCfAccumulatedValues() != null) {

            for (Entry<String, List<CustomFieldValue>> cfValueInfo : entity.getCfAccumulatedValues().getValuesByCode().entrySet()) {
                String cfCode = cfValueInfo.getKey();
                // Return only those values that have cft
                if (!cfts.containsKey(cfCode)) {
                    continue;
                }
                for (CustomFieldValue cfValue : cfValueInfo.getValue()) {
                    currentEntityCFs.getInheritedCustomField().add(customFieldToDTO(cfCode, cfValue, cfts.get(cfCode)));
                }
            }
        }

        return currentEntityCFs.isEmpty() ? null : currentEntityCFs;
    }

    /**
     * Merge map values.
     *
     * @param source the source
     * @param destination the destination
     */
    @SuppressWarnings("unused")
    private void mergeMapValues(List<CustomFieldDto> source, List<CustomFieldDto> destination) {
        for (CustomFieldDto sourceCF : source) {
            // logger.trace("Source custom field: {}", sourceCF);
            boolean found = false;
            // look for a matching CF in the destination
            for (CustomFieldDto destinationCF : destination) {
                // logger.trace("Comparing to destination custom field: {}", destinationCF);
                found = destinationCF.getCode().equalsIgnoreCase(sourceCF.getCode());
                if (found) {
                    // logger.trace("Custom field matched: \n{}\n{}", sourceCF, destinationCF);
                    Map<String, CustomFieldValueDto> sourceValues = sourceCF.getMapValue();
                    if (sourceValues != null) {
                        Map<String, CustomFieldValueDto> destinationValues = destinationCF.getMapValue();
                        for (Entry<String, CustomFieldValueDto> sourceValue : sourceValues.entrySet()) {
                            CustomFieldValueDto destinationValue = destinationValues.get(sourceValue.getKey());
                            // the source value is not allowed to override the destination value, so only add
                            // the values that are on the source CF, but not on the destination CF
                            if (destinationValue == null) {
                                destinationValues.put(sourceValue.getKey(), sourceValue.getValue());
                            }
                        }
                    }
                    break;
                }
            }
            // after comparing all CFs, add the source CF that doesn't exist yet in the destination
            if (!found) {
                destination.add(sourceCF);
            }
        }
    }

    /**
     * Custom field to DTO.
     *
     * @param cfCode the cf code
     * @param value the value
     * @param isChildEntityTypeField the is child entity type field
     * @param cft the cft
     * @return the custom field dto
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public CustomFieldDto customFieldToDTO(String cfCode, Object value, boolean isChildEntityTypeField, CustomFieldTemplate cft) {

        CustomFieldDto dto = new CustomFieldDto();
        dto.setCode(cfCode);
        dto.setDescription(cft.getDescription());
        dto.setFieldType(cft.getFieldType());
        dto.setLanguageDescriptions(LanguageDescriptionDto.convertMultiLanguageFromMapOfValues(cft.getDescriptionI18n()));
        if (value instanceof String) {
            dto.setStringValue((String) value);
        } else if (value instanceof Date) {
            dto.setDateValue((Date) value);
        } else if (value instanceof Long) {
            dto.setLongValue((Long) value);
        } else if (value instanceof Double) {
            dto.setDoubleValue((Double) value);
        } else if (value instanceof List) {
            dto.setListValue(customFieldValueToDTO((List) value, isChildEntityTypeField));
        } else if (value instanceof Map) {
            dto.setMapValue(customFieldValueToDTO((Map) value));
        } else if (value instanceof EntityReferenceWrapper) {
            dto.setEntityReferenceValue(new EntityReferenceDto((EntityReferenceWrapper) value));
        }

        return dto;
    }

    /**
     * Custom field to DTO.
     *
     * @param cfCode the cf code
     * @param cfValue the cf value
     * @param cft the cft
     * @return the custom field dto
     */
    @SuppressWarnings("unchecked")
    private CustomFieldDto customFieldToDTO(String cfCode, CustomFieldValue cfValue, CustomFieldTemplate cft) {

        boolean isChildEntityTypeField = cft.getFieldType() == CustomFieldTypeEnum.CHILD_ENTITY;

        CustomFieldDto dto = customFieldToDTO(cfCode, cfValue.getValue(), isChildEntityTypeField, cft);
        dto.setCode(cfCode);
        dto.setDescription(cft.getDescription());
        dto.setFieldType(cft.getFieldType());
        dto.setLanguageDescriptions(LanguageDescriptionDto.convertMultiLanguageFromMapOfValues(cft.getDescriptionI18n()));
        if (cfValue.getPeriod() != null) {
            dto.setValuePeriodStartDate(cfValue.getPeriod().getFrom());
            dto.setValuePeriodEndDate(cfValue.getPeriod().getTo());
        }

        if (cfValue.getPriority() > 0) {
            dto.setValuePeriodPriority(cfValue.getPriority());
        }
        dto.setStringValue(cfValue.getStringValue());
        dto.setDateValue(cfValue.getDateValue());
        dto.setLongValue(cfValue.getLongValue());
        dto.setDoubleValue(cfValue.getDoubleValue());
        dto.setListValue(customFieldValueToDTO(cfValue.getListValue(), isChildEntityTypeField));
        dto.setMapValue(customFieldValueToDTO(cfValue.getMapValue()));

        if (cft.getStorageType() == CustomFieldStorageTypeEnum.MATRIX && dto.getMapValue() != null && !dto.getMapValue().isEmpty()
                && !dto.getMapValue().containsKey(CustomFieldValue.MAP_KEY)) {
            dto.getMapValue().put(CustomFieldValue.MAP_KEY,
                new CustomFieldValueDto(StringUtils.concatenate(CustomFieldValue.MATRIX_COLUMN_NAME_SEPARATOR, cft.getMatrixColumnCodes())));
        }

        if (cfValue.getEntityReferenceValue() != null) {
            dto.setEntityReferenceValue(new EntityReferenceDto(cfValue.getEntityReferenceValue()));
        }

        return dto;
    }

    /**
     * Custom field value to DTO.
     *
     * @param listValue the list value
     * @param isChildEntityTypeField the is child entity type field
     * @return the list
     */
    private List<CustomFieldValueDto> customFieldValueToDTO(@SuppressWarnings("rawtypes") List listValue, boolean isChildEntityTypeField) {

        if (listValue == null) {
            return null;
        }
        List<CustomFieldValueDto> dtos = new ArrayList<CustomFieldValueDto>();

        for (Object listItem : listValue) {
            CustomFieldValueDto dto = new CustomFieldValueDto();
            if (listItem instanceof EntityReferenceWrapper) {
                if (isChildEntityTypeField) {
                    CustomEntityInstance cei = customEntityInstanceService.findByCodeByCet(((EntityReferenceWrapper) listItem).getClassnameCode(),
                        ((EntityReferenceWrapper) listItem).getCode());
                    if (cei == null) {
                        continue;
                    }

                    dto.setValue(new CustomEntityInstanceDto(cei, getCustomFieldsDTO(cei)));

                } else {
                    dto.setValue(new EntityReferenceDto((EntityReferenceWrapper) listItem));
                }
            } else {
                dto.setValue(listItem);
            }
            dtos.add(dto);
        }
        return dtos;
    }

    /**
     * Custom field value to DTO.
     *
     * @param mapValue the map value
     * @return the linked hash map
     */
    private LinkedHashMap<String, CustomFieldValueDto> customFieldValueToDTO(Map<String, Object> mapValue) {
        if (mapValue == null || mapValue.entrySet().size() == 0) {
            return null;
        }
        LinkedHashMap<String, CustomFieldValueDto> dtos = new LinkedHashMap<String, CustomFieldValueDto>();

        for (Map.Entry<String, Object> mapItem : mapValue.entrySet()) {
            CustomFieldValueDto dto = new CustomFieldValueDto();
            if (mapItem.getValue() instanceof EntityReferenceWrapper) {
                dto.setValue(new EntityReferenceDto((EntityReferenceWrapper) mapItem.getValue()));
            } else {
                dto.setValue(mapItem.getValue());
            }
            dtos.put(mapItem.getKey(), dto);
        }
        return dtos;
    }

}
