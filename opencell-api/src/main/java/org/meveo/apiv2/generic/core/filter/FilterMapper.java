package org.meveo.apiv2.generic.core.filter;

import java.util.List;
import java.util.stream.Collectors;

public abstract class FilterMapper {
    protected final String property;
    protected final Object value;

    protected FilterMapper(String property, Object value) {
        this.property = property;
        this.value = value;
    }

    public Object map() {
        if(List.class.isAssignableFrom(value.getClass())){
            return ((List)value).stream()
                    .map(this::mapStrategy)
                    .collect(Collectors.toList());
        }
        return mapStrategy(value);
    }

    public abstract Object mapStrategy(Object value);
}
