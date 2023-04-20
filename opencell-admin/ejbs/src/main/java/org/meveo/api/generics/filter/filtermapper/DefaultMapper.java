package org.meveo.api.generics.filter.filtermapper;


import org.meveo.api.generics.filter.FilterMapper;

public class DefaultMapper extends FilterMapper {
    public DefaultMapper(String property, Object value) {
        super(property,value);
    }

    @Override
    public Object mapStrategy(Object value) {
        return value;
    }
}
