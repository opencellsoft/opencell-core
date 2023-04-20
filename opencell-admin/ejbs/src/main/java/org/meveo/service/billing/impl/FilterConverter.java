package org.meveo.service.billing.impl;

import static java.lang.Double.valueOf;
import static java.lang.Enum.valueOf;
import static java.util.Optional.ofNullable;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.FilterOperatorEnum;
import org.meveo.model.BusinessEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FilterConverter {

    private Class<?> targetEntity;
    private Logger log = LoggerFactory.getLogger(getClass());

    private static final String SPLIT_REGEX = " ";

    public FilterConverter(Class<?> targetEntity) {
        this.targetEntity = targetEntity;
    }

    /**
     * Convert filters maps from String to field type
     * filters : Map of filters Map<String, String>
     * Return : converted map of filters Map<String, Object>
     */
    public Map<String, Object> convertFilters(Map<String, Object> filters) {
        Map<String, Object> convertedFilters = new HashMap<>();
        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            String[] fieldsName = entry.getKey().split(SPLIT_REGEX);
            if (fieldsName.length == 1) {
                convertedFilters.put(entry.getKey(), convert(targetEntity, entry, entry.getKey()));
            } else {
                convertedFilters.put(entry.getKey(), convert(targetEntity, entry, fieldsName[1]));
            }
        }
        return convertedFilters;
    }

    private Object convert(Class<?> entity, Map.Entry<String, Object> filterEntry, String fieldName) {
        try {
        	if("$OPERATOR".equalsIgnoreCase(fieldName)) {
        		return FilterOperatorEnum.valueOf((String)filterEntry.getValue());
        	}
        	if(fieldName.matches("\\$filter[0-9]+$")) {
            	return convertFilters((Map<String, Object>)filterEntry.getValue());
            }
            if(fieldName.equalsIgnoreCase("id")) {
            	Object value = filterEntry.getValue();
        		return Long.valueOf(value.toString());
            }
            final int index = fieldName.indexOf(".");
			if(index>0) {
            	final Field field = from(fieldName.substring(0, index), entity);
				if(field!=null && BusinessEntity.class.isAssignableFrom(field.getType())) {
        			return convert(field.getType(), filterEntry, fieldName.substring(index+1));
        		}
            }
            Field field = ofNullable(from(fieldName, entity))
                    .orElseThrow(() -> new BusinessException("No such field " + fieldName+" on entity "+entity));
            if (Number.class.isAssignableFrom(field.getType())) {
                return toNumber(entity, fieldName, (String)filterEntry.getValue());
            }
            if (field.getType().isEnum()) {
                return valueOf((Class<Enum>) field.getType(), ((String)filterEntry.getValue()).toUpperCase());
            }
            if(field.getType().isAssignableFrom(Date.class)) {
                return new SimpleDateFormat("yyyy-MM-dd").parse((String)filterEntry.getValue());
            }
            if (Boolean.class.isAssignableFrom(field.getType()) || boolean.class.isAssignableFrom(field.getType())) {
                return Boolean.valueOf((String)filterEntry.getValue());
            }
            return filterEntry.getValue();
        } catch (NoSuchFieldException | NoSuchMethodException | IllegalAccessException
                | InvocationTargetException | ParseException exception) {
            log.error(exception.getMessage());
        }
        return null;
    }

    private Field from(String fieldName, Class<?> entity) {
        Field field = null;
        while (entity != Object.class && field == null) {
            try {
                field = entity.getDeclaredField(fieldName);
            } catch (NoSuchFieldException exception) {
                entity = entity.getSuperclass();
            }
        }
        return field;
    }

    private Object toNumber(Class<?> entity, String key, String value) throws NoSuchFieldException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        Class<?> type = from(key, entity).getType();
        Method method;
        if(type.isAssignableFrom(Long.class)) {
            return Long.valueOf(value);
        }
        if(type.isAssignableFrom(BigInteger.class)) {
            return BigInteger.valueOf(Long.valueOf(value));
        }
        if(type.isAssignableFrom(Integer.class)) {
            return Integer.valueOf(value);
        }
        Double doubleValue = valueOf(value);
        method = type.getMethod("valueOf", double.class);
        return method.invoke(type, doubleValue);
    }
}
