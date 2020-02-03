package org.meveo.apiv2.services.generic.filter.filtermapper;

import org.meveo.apiv2.services.generic.filter.FilterMapper;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

public class NumberMapper extends FilterMapper {
    private final Class clazz;

    public NumberMapper(String property, Object value,  Class clazz) {
        super(property, value);
        this.clazz = clazz;
    }

    @Override
    public Object mapStrategy(Object value) {
        if(clazz.isAssignableFrom(Long.class)){
            return Long.valueOf(value.toString());
        }
        return value;
    }
}
