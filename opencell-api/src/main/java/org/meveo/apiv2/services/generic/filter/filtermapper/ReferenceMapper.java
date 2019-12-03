package org.meveo.apiv2.services.generic.filter.filtermapper;

import org.meveo.apiv2.services.generic.filter.FilterMapper;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.service.base.PersistenceService;

import javax.persistence.EntityManager;
import java.lang.reflect.Field;
import java.util.Optional;
import java.util.function.Function;

public class ReferenceMapper extends FilterMapper {

    private final Function<Class, EntityManager> entityManagerResolver;
    private final Class entity;

    public ReferenceMapper(String property, Object value, Class entity, Function<Class, EntityManager> entityManagerResolver) {
        super(property, value);
        this.entity = entity;
        this.entityManagerResolver = entityManagerResolver;
    }

    @Override
    public Object mapStrategy(Object value) {
        return entityManagerResolver.apply(entity).getReference(entity, Long.valueOf(value.toString()));
    }
}
