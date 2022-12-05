package org.meveo.model.persistence;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.meveo.commons.encryption.IEncryptable;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.crm.custom.CustomFieldValue;
import org.meveo.model.crm.custom.CustomFieldValues;

import com.fasterxml.jackson.core.type.TypeReference;

import jakarta.persistence.AttributeConverter;

public class CustomFieldJsonTypeConverter implements AttributeConverter<CustomFieldValues, String>, IEncryptable {

    private static boolean ENCRYPT_CF = TRUE_STR.equalsIgnoreCase(ParamBean.getInstance().getProperty(ENCRYPT_CUSTOM_FIELDS_PROPERTY, FALSE_STR));

    @Override
    public String convertToDatabaseColumn(CustomFieldValues value) {
        if (value == null) {
            return null;
        }

        if (ENCRYPT_CF) {
            return encryptCfs(value);
        }

        return value.asJson();
    }

    @Override
    public CustomFieldValues convertToEntityAttribute(String dbData) {
        if (StringUtils.isBlank(dbData)) {
            return null;
        }

        Map<String, List<CustomFieldValue>> cfValues = JacksonUtil.fromString(dbData, new TypeReference<Map<String, List<CustomFieldValue>>>() {
        });
        return new CustomFieldValues(decryptCfs(cfValues));
    }

    public Map<String, List<CustomFieldValue>> decryptCfs(Map<String, List<CustomFieldValue>> cfValues) {
        for (Entry<String, List<CustomFieldValue>> listCfs : cfValues.entrySet()) {
            for (CustomFieldValue cf : listCfs.getValue()) {
                if (cf.getStringValue() != null && cf.getStringValue().startsWith(ENCRYPTION_CHECK_STRING)) {
                    cf.setStringValue(ENCRYPT_CF ? decrypt(cf.getStringValue()) : cf.getStringValue());
                } else if (cf.getListValue() != null) {
                    List<Object> listValues = new ArrayList<>();
                    for (Object object : cf.getListValue()) {
                        if (object instanceof String) {
                            String valueString = (String) object;
                            if (valueString.startsWith(ENCRYPTION_CHECK_STRING)) {
                                listValues.add(ENCRYPT_CF ? decrypt(valueString) : valueString);
                            } else {
                                listValues.add(valueString);
                            }
                        } else {
                            listValues.add(object);
                        }
                    }
                    cf.setListValue(listValues);
                } else if (cf.getMapValue() != null) {
                    Map<String, Object> mapValues = new LinkedHashMap<>();
                    for (Entry<String, Object> object : cf.getkeyValueMap().entrySet()) {
                        if (object.getValue() instanceof String) {
                            String valueString = (String) object.getValue();
                            if (valueString.startsWith(ENCRYPTION_CHECK_STRING)) {
                                mapValues.put(object.getKey(), ENCRYPT_CF ? decrypt(valueString) : valueString);
                            } else {
                                mapValues.put(object.getKey(), valueString);
                            }
                        } else {
                            mapValues.put(object.getKey(), object.getValue());
                        }
                    }
                    cf.setMapValue(mapValues);
                }
            }
        }
        return cfValues;
    }

    public String encryptCfs(CustomFieldValues cfValues) {
        for (Entry<String, List<CustomFieldValue>> listCfs : cfValues.getValuesByCode().entrySet()) {
            for (CustomFieldValue cf : listCfs.getValue()) {
                if (cf.getStringValue() != null && !cf.getStringValue().startsWith(ENCRYPTION_CHECK_STRING)) {
                    cf.setStringValue(encrypt(cf.getStringValue()));
                } else if (cf.getListValue() != null) {
                    List<Object> listValues = new ArrayList<>();
                    for (Object object : cf.getListValue()) {
                        if (object instanceof String) {
                            String valueString = (String) object;
                            if (!valueString.startsWith(ENCRYPTION_CHECK_STRING)) {
                                listValues.add(encrypt(valueString));
                            } else {
                                listValues.add(valueString);
                            }
                        } else {
                            listValues.add(object);
                        }
                    }
                    cf.setListValue(listValues);
                } else if (cf.getMapValue() != null) {
                    Map<String, Object> mapValues = new LinkedHashMap<>();
                    for (Entry<String, Object> object : cf.getkeyValueMap().entrySet()) {
                        if (object.getValue() instanceof String) {
                            String valueString = (String) object.getValue();
                            if (!valueString.startsWith(ENCRYPTION_CHECK_STRING)) {
                                mapValues.put(object.getKey(), encrypt(valueString));
                            } else {
                                mapValues.put(object.getKey(), valueString);
                            }
                        } else {
                            mapValues.put(object.getKey(), object.getValue());
                        }
                    }
                    cf.setMapValue(mapValues);
                }
            }
        }
        return cfValues.toString();
    }
    
    
}