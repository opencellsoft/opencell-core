package org.meveo.service.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.dto.CustomEntityInstanceDto;
import org.meveo.api.dto.CustomFieldDto;
import org.meveo.api.dto.CustomFieldValueDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.EntityReferenceDto;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
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

	public CustomFieldsDto getCustomFieldsDTO(ICustomFieldEntity entity) {
		Map<String, List<CustomFieldInstance>> customFields = customFieldInstanceService.getCustomFieldInstances(entity);

		return getCustomFieldsDTO(entity, customFields);
	}

    public CustomFieldsDto getCustomFieldsDTO(ICustomFieldEntity entity, Map<String, List<CustomFieldInstance>> customFields) {       

        Map<String, CustomFieldTemplate> cfts = customFieldTemplateService.findByAppliesTo(entity);

        Set<String> childEntityTypeFields = new HashSet<>();
        for (CustomFieldTemplate cft : cfts.values()) {
            if (cft.getFieldType() == CustomFieldTypeEnum.CHILD_ENTITY) {
                childEntityTypeFields.add(cft.getCode());
            }
        }
        
        return customFieldsToDTO(customFields, childEntityTypeFields);
    }

    private CustomFieldsDto customFieldsToDTO(Map<String, List<CustomFieldInstance>> customFields, Set<String> childEntityTypeFields) {
        if (customFields == null || customFields.isEmpty()) {
            return null;
        }
        CustomFieldsDto dto = new CustomFieldsDto();
        for (List<CustomFieldInstance> cfis : customFields.values()) {
            for (CustomFieldInstance cfi : cfis) {
                dto.getCustomField().add(customFieldToDTO(cfi, childEntityTypeFields));
            }
        }
        return dto;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public CustomFieldDto customFieldToDTO(String code, Object value, boolean isChildEntityTypeField) {

        CustomFieldDto dto = new CustomFieldDto();
        dto.setCode(code);
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

    private CustomFieldDto customFieldToDTO(CustomFieldInstance cfi, Set<String> childEntityTypeFields) {

        CustomFieldDto dto = new CustomFieldDto();
        dto.setCode(cfi.getCode());
        dto.setValuePeriodStartDate(cfi.getPeriodStartDate());
        dto.setValuePeriodEndDate(cfi.getPeriodEndDate());
        if (cfi.getPriority() > 0) {
            dto.setValuePeriodPriority(cfi.getPriority());
        }
        dto.setStringValue(cfi.getCfValue().getStringValue());
        dto.setDateValue(cfi.getCfValue().getDateValue());
        dto.setLongValue(cfi.getCfValue().getLongValue());
        dto.setDoubleValue(cfi.getCfValue().getDoubleValue());
        dto.setListValue(customFieldValueToDTO(cfi.getCfValue().getListValue(), childEntityTypeFields.contains(cfi.getCode())));
        dto.setMapValue(customFieldValueToDTO(cfi.getCfValue().getMapValue()));
        if (cfi.getCfValue().getEntityReferenceValue() != null) {
            dto.setEntityReferenceValue(new EntityReferenceDto(cfi.getCfValue().getEntityReferenceValue()));
        }

        return dto;
    }

    private List<CustomFieldValueDto> customFieldValueToDTO(List<Object> listValue, boolean isChildEntityTypeField) {

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
}
