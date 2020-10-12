package org.meveo.apiv2.generic.core.filter.filtermapper;


import org.meveo.apiv2.generic.core.filter.FilterMapper;

import java.math.BigDecimal;
import java.math.BigInteger;

public class NumberMapper extends FilterMapper {
    private final Class clazz;

    public NumberMapper(String property, Object value,  Class clazz) {
        super(property, value);
        this.clazz = clazz;
    }

    @Override
    public Object mapStrategy(Object value) {
        if(clazz.isAssignableFrom(Long.class) || "id".equalsIgnoreCase(property)){
            return Long.valueOf(value.toString());
        }
        if(clazz.isAssignableFrom(BigDecimal.class)){
            return BigDecimal.valueOf(Double.valueOf(value.toString()));
        }
        if(clazz.isAssignableFrom(BigInteger.class)){
            return BigInteger.valueOf(Long.valueOf(value.toString()));
        }
        return value;
    }
}

