package org.meveo.apiv2.generic.core;

import org.assertj.core.util.VisibleForTesting;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.apiv2.generic.GenericPagingAndFiltering;
import org.meveo.apiv2.generic.ImmutableGenericPagingAndFiltering;
import org.meveo.apiv2.generic.core.filter.FactoryFilterMapper;
import org.meveo.apiv2.generic.core.filter.FilterMapper;
import org.meveo.model.IEntity;
import org.meveo.service.base.PersistenceService;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GenericRequestMapper {
    private final Class entityClass;
    private final Function<Class, PersistenceService> serviceFunction;
    public GenericRequestMapper(Class entityClass, Function<Class, PersistenceService> serviceFunction) {
        this.entityClass = entityClass;
        this.serviceFunction = serviceFunction;
    }

    public PaginationConfiguration mapTo(GenericPagingAndFiltering genericPagingAndFiltering){
        if(genericPagingAndFiltering == null){
            return getPaginationConfiguration(ImmutableGenericPagingAndFiltering.builder().build());
        }
        return getPaginationConfiguration(genericPagingAndFiltering);
    }

    private PaginationConfiguration getPaginationConfiguration(GenericPagingAndFiltering genericPagingAndFiltering) {
        return new PaginationConfiguration(genericPagingAndFiltering.getOffset().intValue(), genericPagingAndFiltering.getLimitOrDefault(GenericHelper.getDefaultLimit()).intValue(),
                evaluateFilters(genericPagingAndFiltering.getFilters(), entityClass), genericPagingAndFiltering.getFullTextFilter(),
                Collections.emptyList(), genericPagingAndFiltering.getSortBy(),
                org.primefaces.model.SortOrder.valueOf(genericPagingAndFiltering.getSortOrder()));
    }
    @VisibleForTesting
    public Map<String, Object> evaluateFilters(Map<String, Object> filters, Class<? extends IEntity> entity) {
        return Stream.of(filters.keySet().toArray())
                .map(key -> {
                    String keyObject = (String) key;
                    if(!"SQL".equalsIgnoreCase(keyObject) && !"$FILTER".equalsIgnoreCase(keyObject)){

                    	String fieldName = keyObject.contains(" ") ? keyObject.substring(keyObject.indexOf(" ")).trim() : keyObject;
                    	String[] fields=fieldName.split(" ");
                    	FilterMapper filterMapper=null;
                    	for(String field:fields) {
                    		filterMapper=new FactoryFilterMapper().create(field, filters.get(key), (String) filters.get("cetCode"), serviceFunction, entity);
                    	}
                    	return Collections.singletonMap(keyObject, filterMapper.map());
                    }
                    return Collections.singletonMap(keyObject, filters.get(key));
                })
                .flatMap (map -> map.entrySet().stream())
                .filter(stringObjectEntry -> Objects.nonNull(stringObjectEntry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}