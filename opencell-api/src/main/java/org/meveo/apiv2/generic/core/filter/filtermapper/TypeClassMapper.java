package org.meveo.apiv2.generic.core.filter.filtermapper;


import org.meveo.apiv2.generic.core.GenericHelper;
import org.meveo.apiv2.generic.core.filter.FilterMapper;

public class TypeClassMapper extends FilterMapper {

    public TypeClassMapper(String property, Object value) {
        super(property, value);
    }

    @Override
    public Class mapStrategy(Object value) {
        if(!GenericHelper.entitiesByName.containsKey(((String) value).toLowerCase())){
            throw new IllegalArgumentException("Invalid argument : type_class");
        }
        return GenericHelper.entitiesByName.get(((String) value).toLowerCase());
    }
}