package org.meveo.api.generics.filter.filtermapper;


import java.util.HashMap;
import java.util.Map;

import org.meveo.api.generics.filter.FilterMapper;

public class AuditableMapper extends FilterMapper {
    private static final String UPDATED = "updated";
	private static final String CREATED = "created";

	public AuditableMapper(String property, Object value, Class<?> clazz) {
        super(property, value);
    }

    @Override
    public Object mapStrategy(Object value) {
        Map<String, Object> valueMap = (Map<String, Object>) value;
        Map<String, Object> mappedValue = new HashMap<>();
        if(valueMap.containsKey(CREATED)){
            mappedValue.put(CREATED, new DateMapper(CREATED, valueMap.get(CREATED)).map());
        }
        if(valueMap.containsKey(UPDATED)){
            mappedValue.put(UPDATED, new DateMapper(UPDATED, valueMap.get(UPDATED)).map());
        }
        if(mappedValue.entrySet().isEmpty()){
            throw new IllegalArgumentException("Invalid parameter exception : " + property);
        }
        return mappedValue;
    }
}
