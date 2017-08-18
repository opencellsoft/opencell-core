package org.meveo.service.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.dto.CustomEntityInstanceDto;
import org.meveo.api.dto.CustomFieldDto;
import org.meveo.api.dto.CustomFieldValueDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.EntityReferenceDto;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.crm.custom.CustomFieldValue;
import org.meveo.model.customEntities.CustomEntityInstance;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.custom.CustomEntityInstanceService;

@Stateless
public class EntityToDtoConverter {

    @Inject
    private CustomFieldTemplateService customFieldTemplateService;

    @Inject
    private CustomEntityInstanceService customEntityInstanceService;

    @Inject
    protected CustomFieldInstanceService customFieldInstanceService;

    public CustomFieldsDto getCustomFieldsDTO(ICustomFieldEntity entity, boolean includeInheritedCF) {
        CustomFieldsDto customFieldsDto = getCustomFieldsDTO(entity);
        if (includeInheritedCF && (customFieldsDto == null || customFieldsDto.getCustomField().isEmpty())) {
            if (customFieldsDto == null) {
                customFieldsDto = new CustomFieldsDto();
            }
            ICustomFieldEntity[] parentEntities = entity.getParentCFEntities();
            if (parentEntities != null) {
                for (ICustomFieldEntity iCustomFieldEntity : parentEntities) {
                    CustomFieldsDto inheritedCustomFieldsDto = getCustomFieldsDTO(iCustomFieldEntity, includeInheritedCF);
                    if (inheritedCustomFieldsDto != null) {
                        customFieldsDto.getCustomField().addAll(inheritedCustomFieldsDto.getCustomField());
                    }
                }
            }
        }
        return customFieldsDto;

    }

    public CustomFieldsDto getCustomFieldsDTO(ICustomFieldEntity entity) {
        if (entity.getCfValues() == null) {
            return null;
        }
        Map<String, List<CustomFieldValue>> cfValuesByCode = entity.getCfValues().getValuesByCode();
        return getCustomFieldsDTO(entity, cfValuesByCode);
    }

    public CustomFieldsDto getCustomFieldsDTO(ICustomFieldEntity entity, Map<String, List<CustomFieldValue>> cfValuesByCode) {

        if (cfValuesByCode == null || cfValuesByCode.isEmpty()) {
            return null;
        }

        Map<String, CustomFieldTemplate> cfts = customFieldTemplateService.findByAppliesTo(entity);

        CustomFieldsDto dto = new CustomFieldsDto();
        for (Entry<String, List<CustomFieldValue>> cfValueInfo : cfValuesByCode.entrySet()) {
            String cfCode = cfValueInfo.getKey();
            // Return only those values that have cft
            if (!cfts.containsKey(cfCode)) {
                continue;
            }
            for (CustomFieldValue cfValue : cfValueInfo.getValue()) {
                dto.getCustomField().add(customFieldToDTO(cfCode, cfValue, cfts.get(cfCode)));
            }
        }
        if (dto.isEmpty()) {
            return null;
        }
        return dto;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public CustomFieldDto customFieldToDTO(String cfCode, Object value, boolean isChildEntityTypeField) {

        CustomFieldDto dto = new CustomFieldDto();
        dto.setCode(cfCode);
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

    @SuppressWarnings("unchecked")
    private CustomFieldDto customFieldToDTO(String cfCode, CustomFieldValue cfValue, CustomFieldTemplate cft) {

        boolean isChildEntityTypeField = cft.getFieldType() == CustomFieldTypeEnum.CHILD_ENTITY;

        CustomFieldDto dto = customFieldToDTO(cfCode, cfValue.getValue(), isChildEntityTypeField);
        dto.setCode(cfCode);
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

        if (cft.getStorageType() == CustomFieldStorageTypeEnum.MATRIX && !dto.getMapValue().isEmpty() && !dto.getMapValue().containsKey(CustomFieldValue.MAP_KEY)) {
            dto.getMapValue().put(CustomFieldValue.MAP_KEY,
                new CustomFieldValueDto(StringUtils.concatenate(CustomFieldValue.MATRIX_COLUMN_NAME_SEPARATOR, cft.getMatrixColumnCodes())));
        }

        if (cfValue.getEntityReferenceValue() != null) {
            dto.setEntityReferenceValue(new EntityReferenceDto(cfValue.getEntityReferenceValue()));
        }

        return dto;
    }

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
                    dto.setValue(CustomEntityInstanceDto.toDTO(cei, getCustomFieldsDTO(cei)));

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

    public CustomFieldsDto getCustomFieldsWithInheritedDTO(ICustomFieldEntity entity, boolean includeInheritedCF) {
        CustomFieldsDto customFieldsDto = getCustomFieldsDTO(entity);
        if (customFieldsDto == null) {
            customFieldsDto = new CustomFieldsDto();
        }

        if (!customFieldsDto.isEmpty() && includeInheritedCF) {
            ICustomFieldEntity[] parentEntities = entity.getParentCFEntities();
            if (parentEntities != null) {
                for (ICustomFieldEntity iCustomFieldEntity : parentEntities) {
                    CustomFieldsDto inheritedCustomFieldsDto = getCustomFieldsDTO(iCustomFieldEntity, includeInheritedCF);
                    if (inheritedCustomFieldsDto != null) {
                        for (CustomFieldDto cfDto : customFieldsDto.getCustomField()) {
                            for (CustomFieldDto inheritedCFDto : inheritedCustomFieldsDto.getCustomField()) {
                                if (cfDto.getCode().equals(inheritedCFDto.getCode())) {
                                    customFieldsDto.getInheritedCustomField().add(inheritedCFDto);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (customFieldsDto.isEmpty()) {
            return null;
        }

        return customFieldsDto;
    }

    /**
     * @param entity
     * @param includeInheritedCF
     * @return
     */
    public CustomFieldsDto getMergedCustomFieldsWithInheritedDTO(ICustomFieldEntity entity, boolean includeInheritedCF) {
        CustomFieldsDto customFieldsDto = getCustomFieldsDTO(entity);
        if (customFieldsDto == null) {
            customFieldsDto = new CustomFieldsDto();
        }
        ICustomFieldEntity[] parentEntities = entity.getParentCFEntities();

        List<CustomFieldDto> customFieldList = customFieldsDto.getCustomField();
        if (!customFieldsDto.isEmpty() && includeInheritedCF) {
            if (parentEntities != null) {
                for (ICustomFieldEntity iCustomFieldEntity : parentEntities) {
                    CustomFieldsDto inheritedCustomFieldsDto = getCustomFieldsDTO(iCustomFieldEntity, includeInheritedCF);
                    List<CustomFieldDto> inheritedCustomFieldList = inheritedCustomFieldsDto.getCustomField();
                    if (inheritedCustomFieldsDto != null) {
                        for (CustomFieldDto cfDto : customFieldList) {

                            for (CustomFieldDto inheritedCFDto : inheritedCustomFieldList) {
                                if (cfDto.getCode().equals(inheritedCFDto.getCode())) {
                                    customFieldsDto.getInheritedCustomField().add(inheritedCFDto);
                                }
                            }
                        }

                        for (CustomFieldDto inheritedCFDto : inheritedCustomFieldList) {
                            boolean found = false;
                            for (CustomFieldDto cfDto : customFieldList) {
                                if (cfDto.getCode().equals(inheritedCFDto.getCode())) {
                                    found = true;

                                    Map<String, CustomFieldValueDto> mapValue = cfDto.getMapValue();
                                    Map<String, CustomFieldValueDto> inheritedMapValue = inheritedCFDto.getMapValue();
                                    if (inheritedMapValue != null) {
                                        Set<Entry<String, CustomFieldValueDto>> entrySet = inheritedMapValue.entrySet();
                                        for (Entry<String, CustomFieldValueDto> entry : entrySet) {
                                            CustomFieldValueDto customFieldValueDto = mapValue.get(entry.getKey());
                                            if (customFieldValueDto == null) {
                                                mapValue.put(entry.getKey(), entry.getValue());
                                            }
                                        }
                                    }

                                    /**
                                     * List<CustomFieldValueDto> listValue = cfDto.getListValue(); List<CustomFieldValueDto> inheritedListValue = inheritedCFDto.getListValue(); for
                                     * (CustomFieldValueDto customFieldValueDto : inheritedListValue) {
                                     * 
                                     * }
                                     */
                                }
                            }

                            if (!found) {
                                customFieldList.add(inheritedCFDto);
                            }
                        }

                    }

                }
            }
        } else if (includeInheritedCF && (customFieldsDto == null || customFieldList.isEmpty())) {
            if (parentEntities != null) {
                for (ICustomFieldEntity iCustomFieldEntity : parentEntities) {
                    CustomFieldsDto inheritedCustomFieldsDto = getCustomFieldsDTO(iCustomFieldEntity, includeInheritedCF);
                    if (inheritedCustomFieldsDto != null) {
                        customFieldList.addAll(inheritedCustomFieldsDto.getCustomField());
                    }
                }
            }
        }
        if (customFieldsDto.isEmpty()) {
            return null;
        }

        return customFieldsDto;
    }
}
