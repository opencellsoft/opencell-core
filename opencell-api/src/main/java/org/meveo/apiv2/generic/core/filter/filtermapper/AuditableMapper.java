package org.meveo.apiv2.generic.core.filter.filtermapper;


import org.meveo.apiv2.generic.core.filter.FilterMapper;

import java.util.HashMap;
import java.util.Map;

public class AuditableMapper extends FilterMapper {
    public AuditableMapper(String property, Object value, Class clazz) {
        super(property, value);
    }

    @Override
    public Object mapStrategy(Object value) {
        Map valueMap = (Map) value;
        Map<String, Object> mappedValue = new HashMap<>();
        if(valueMap.containsKey("created")){
            mappedValue.put("created", new DateMapper("created", valueMap.get("created")).map());
        }
        if(valueMap.containsKey("updated")){
            mappedValue.put("updated", new DateMapper("updated", valueMap.get("updated")).map());
        }
        if(mappedValue.entrySet().isEmpty()){
            throw new IllegalArgumentException("Invalid parameter exception : " + property);
        }
        return mappedValue;
    }
}
