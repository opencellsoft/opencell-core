package org.meveo.apiv2.services.generic.filter.filtermapper;

import org.meveo.api.dto.EntityReferenceDto;
import org.meveo.apiv2.services.generic.GenericApiAlteringService;
import org.meveo.apiv2.services.generic.filter.FilterMapper;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.crm.custom.CustomFieldValue;
import org.meveo.model.crm.custom.CustomFieldValues;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;

import javax.persistence.EntityManager;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;

public class CustomFieldMapper extends FilterMapper {
    private final Function<Class, PersistenceService> serviceFunction;
    private final Class clazz;

    public CustomFieldMapper(String property, Object value, Class clazz, Function<Class, PersistenceService> serviceFunction) {
        super(property, value);
        this.serviceFunction = serviceFunction;
        this.clazz = clazz;
    }

    @Override
    public Object map() {
        Object resultedCustomFieldValuesList = super.map();
        CustomFieldValues customFieldValues = new CustomFieldValues();
        if(resultedCustomFieldValuesList instanceof List){
            ((List) resultedCustomFieldValuesList)
                    .forEach(o -> customFieldValues.setValuesByCode(((CustomFieldValues)o).getValuesByCode()));
        }
        return resultedCustomFieldValuesList;
    }

    @Override
    public CustomFieldValues mapStrategy(Object value) {
        Map<String, CustomFieldTemplate> customFieldTemplates = ((CustomFieldTemplateService) serviceFunction
                .apply(CustomFieldTemplate.class))
                .findByAppliesTo(clazz.getSimpleName());
            return (CustomFieldValues) ((Map) value).keySet()
                    .stream()
                    .filter(customFieldTemplates::containsKey)
                    .map(key -> toCustomFieldValues((String) key, toCustomFieldValue(customFieldTemplates.get(key).getFieldType(), ((Map) ((List) ((Map) value).get(key)).get(0)).get("value"))))
                    .findFirst()
                    .orElse(null);
    }

    private CustomFieldValues toCustomFieldValues(String key, Object value) {
        CustomFieldValues customFieldValues = new CustomFieldValues();
        customFieldValues.getValuesByCode().put(key, Arrays.asList((CustomFieldValue) value));
        return customFieldValues;
    }

    private CustomFieldValue toCustomFieldValue(CustomFieldTypeEnum fieldType, Object value) {
        CustomFieldValue customFieldValue = new CustomFieldValue();
        switch (fieldType) {
            case DATE:
                customFieldValue.setDateValue(new Date((Long) value));
                break;
            case LONG:
                customFieldValue.setLongValue(Integer.toUnsignedLong((Integer) value));
                break;
            case DOUBLE:
                customFieldValue.setDoubleValue((Double) value);
                break;
            case BOOLEAN:
                customFieldValue.setBooleanValue((Boolean) value);
                break;
            case CHILD_ENTITY:
            case ENTITY:
                Map<String, String> entityRefDto = (Map<String, String>) value;
                EntityReferenceWrapper entityReferenceWrapper = new EntityReferenceWrapper();
                entityReferenceWrapper.setClassname(entityRefDto.get("classname"));
                entityReferenceWrapper.setCode(entityRefDto.get("code"));
                customFieldValue.setEntityReferenceValue(entityReferenceWrapper);
                break;
            default:
                customFieldValue.setStringValue((String) value);
                break;
        }
        return customFieldValue;
    }
}
