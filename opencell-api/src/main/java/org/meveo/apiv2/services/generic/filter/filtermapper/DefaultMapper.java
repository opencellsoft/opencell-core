package org.meveo.apiv2.services.generic.filter.filtermapper;

import org.meveo.apiv2.services.generic.filter.FilterMapper;

public class DefaultMapper extends FilterMapper {
    public DefaultMapper(String property, Object value) {
        super(property,value);
    }

    @Override
    public Object mapStrategy(Object value) {
        return value;
    }
}
