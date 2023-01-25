package org.meveo.api.generics.filter.filtermapper;

import org.meveo.api.generics.filter.FilterMapper;
import org.meveo.service.base.PersistenceService;

import java.util.function.Function;

public class ReferenceMapper extends FilterMapper {

    private final Function<Class, PersistenceService> serviceFunction;
    private final Class entity;

    public ReferenceMapper(String property, Object value, Class entity, Function<Class, PersistenceService> serviceFunction) {
        super(property, value);
        this.entity = entity;
        this.serviceFunction = serviceFunction;
    }

    @Override
    public Object mapStrategy(Object value) {
        return serviceFunction
                .apply(entity)
                .getEntityManager()
                .getReference(entity, Long.valueOf(value.toString()));
    }
}
