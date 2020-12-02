package org.meveo.apiv2.generic.core.filter.filtermapper;

import org.meveo.apiv2.generic.core.filter.FilterMapper;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.crm.custom.CustomFieldValue;
import org.meveo.model.crm.custom.CustomFieldValues;
import org.meveo.model.customEntities.CustomEntityInstance;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class CustomFieldMapper extends FilterMapper {
    private final Function<Class, PersistenceService> serviceFunction;
    private final Class clazz;
    private final String cetCode;

    public CustomFieldMapper(String property, Object value, Class clazz, String cetCode, Function<Class, PersistenceService> serviceFunction) {
        super(property, value);
        this.serviceFunction = serviceFunction;
        this.clazz = clazz;
        this.cetCode = cetCode;
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
        Map<String, CustomFieldTemplate> customFieldTemplates = getCFTByAppliesTo(clazz, cetCode);
        CustomFieldValues customFieldValues = new CustomFieldValues();
        ((Map) value).keySet()
                .stream()
                .filter(key -> customFieldTemplates.containsKey(resolveCfCode((String) key)))
                .map(key -> toCustomFieldValues((String) key, resolveCFValue((Map) value, customFieldTemplates, key)).getValuesByCode())
                .forEach(cfValueMap -> customFieldValues.getValuesByCode().putAll((Map<? extends String, ? extends List<CustomFieldValue>>) cfValueMap));
        return customFieldValues.getValuesByCode().isEmpty() ? null : customFieldValues;
    }

    private Map<String, CustomFieldTemplate> getCFTByAppliesTo(Class clazz, String cetCode) {
        if(CustomEntityInstance.class.equals(clazz) && (cetCode==null || cetCode.isBlank())){
            throw new IllegalArgumentException("cetCode value must be provided when filtering on CustomEntityInstance");
        }else if(CustomEntityInstance.class.equals(clazz)){
            return ((CustomFieldTemplateService) serviceFunction
                    .apply(CustomFieldTemplate.class))
                    .findByAppliesTo("CE_"+cetCode);
        }
        return ((CustomFieldTemplateService) serviceFunction
                .apply(CustomFieldTemplate.class))
                .findByAppliesTo(this.clazz.getSimpleName());
    }

    private CustomFieldValue resolveCFValue(Map value, Map<String, CustomFieldTemplate> customFieldTemplates, Object key) {
        String resolvedCode = resolveCfCode((String) key);
        Object cfValue = ((Map) ((List) value.get(key)).get(0)).get("value");
        if(((String) key).contains("List ")){
            return toCustomFieldValue(CustomFieldTypeEnum.STRING, cfValue);
        }
        return toCustomFieldValue(customFieldTemplates.get(resolvedCode).getFieldType(), cfValue);
    }

    private String resolveCfCode(String key) {
        if(key.split(" ").length > 1) {
            return key.split(" ")[1];
        }
        return key;
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
