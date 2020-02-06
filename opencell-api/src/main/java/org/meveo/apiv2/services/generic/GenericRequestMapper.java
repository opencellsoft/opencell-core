package org.meveo.apiv2.services.generic;

import org.assertj.core.util.VisibleForTesting;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.apiv2.generic.GenericPagingAndFiltering;
import org.meveo.apiv2.generic.ImmutableGenericPagingAndFiltering;
import org.meveo.apiv2.services.generic.filter.FactoryFilterMapper;
import org.meveo.model.BaseEntity;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GenericRequestMapper {
    private final Class entityClass;
    private final Function<Class, EntityManager> entityManager;
    public GenericRequestMapper(Class entityClass, Function<Class, EntityManager> entityManager) {
        this.entityClass = entityClass;
        this.entityManager = entityManager;
    }

    public PaginationConfiguration mapTo(GenericPagingAndFiltering genericPagingAndFiltering){
        if(genericPagingAndFiltering == null){
            return getPaginationConfiguration(ImmutableGenericPagingAndFiltering.builder().build());
        }
        return getPaginationConfiguration(genericPagingAndFiltering);
    }

    private PaginationConfiguration getPaginationConfiguration(GenericPagingAndFiltering genericPagingAndFiltering) {
        return new PaginationConfiguration(genericPagingAndFiltering.getOffset().intValue(), genericPagingAndFiltering.getLimit().intValue(),
                evaluateFilters(genericPagingAndFiltering.getFilters(), entityClass), genericPagingAndFiltering.getFullTextFilter(),
                new ArrayList(genericPagingAndFiltering.getNestedEntities()), genericPagingAndFiltering.getSortBy(),
                org.primefaces.model.SortOrder.valueOf(genericPagingAndFiltering.getSortOrder()));
    }
    @VisibleForTesting
    public Map<String, Object> evaluateFilters(Map<String, Object> filters, Class<? extends BaseEntity> entity) {
        return Stream.of(filters.keySet().toArray())
                .map(key -> {
                    String keyObject = (String) key;
                    String fieldName = keyObject.contains(" ") ? keyObject.substring(keyObject.indexOf(" ")).trim() : keyObject;
                    return Collections.singletonMap(keyObject, new FactoryFilterMapper().create(fieldName, filters.get(key), entity, entityManager).map());
                })
                .flatMap (map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
