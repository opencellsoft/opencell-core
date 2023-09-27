package org.meveo.apiv2.generic.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.assertj.core.util.VisibleForTesting;
import org.meveo.admin.util.pagination.FilterOperatorEnum;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.generics.filter.FactoryFilterMapper;
import org.meveo.api.generics.filter.FilterMapper;
import org.meveo.apiv2.generic.GenericPagingAndFiltering;
import org.meveo.apiv2.generic.ImmutableGenericPagingAndFiltering;
import org.meveo.model.IEntity;
import org.meveo.service.base.PersistenceService;

public class GenericRequestMapper {
    private final Class entityClass;
    private final Function<Class, PersistenceService> serviceFunction;
    public GenericRequestMapper(Class entityClass, Function<Class, PersistenceService> serviceFunction) {
        this.entityClass = entityClass;
        this.serviceFunction = serviceFunction;
    }

    public PaginationConfiguration mapTo(GenericPagingAndFiltering genericPagingAndFiltering) {
        PaginationConfiguration paginationConfiguration;
        GenericPagingAndFiltering genericPagingAndFilteringBuilt;

        if (genericPagingAndFiltering == null) {
            genericPagingAndFilteringBuilt = ImmutableGenericPagingAndFiltering.builder().build();
            paginationConfiguration = getPaginationConfiguration(genericPagingAndFilteringBuilt);
            setPaginationLimit(paginationConfiguration, genericPagingAndFilteringBuilt);
        } else {
            paginationConfiguration = getPaginationConfiguration(genericPagingAndFiltering);
            setPaginationLimit(paginationConfiguration, genericPagingAndFiltering);
        }
        return paginationConfiguration;
    }

    private void setPaginationLimit(PaginationConfiguration paginationConfiguration, GenericPagingAndFiltering genericPagingAndFiltering) {
        paginationConfiguration.setLimit(genericPagingAndFiltering.getLimit() != null ? genericPagingAndFiltering.getLimit().intValue() : null);
    }

    private PaginationConfiguration getPaginationConfiguration(GenericPagingAndFiltering genericPagingAndFiltering) {
        return new PaginationConfiguration(genericPagingAndFiltering.getOffset().intValue(), genericPagingAndFiltering.getLimitOrDefault(GenericHelper.getDefaultLimit()).intValue(),
                evaluateFilters(genericPagingAndFiltering.getFilters(), entityClass), genericPagingAndFiltering.getFullTextFilter(),
                computeFetchFields(genericPagingAndFiltering), genericPagingAndFiltering.getGroupBy(), genericPagingAndFiltering.getHaving(), genericPagingAndFiltering.getJoinType(),
                genericPagingAndFiltering.getIsFilter(), genericPagingAndFiltering.getSortBy(), PagingAndFiltering.SortOrder.valueOf(genericPagingAndFiltering.getSortOrder()));
    }
    private List<String> computeFetchFields(GenericPagingAndFiltering genericPagingAndFiltering) {
        List<String> sortByFetchList = Stream.of(genericPagingAndFiltering.getSortBy().split(","))
                .filter(s -> !s.isBlank() && s.contains(".") && !s.contains("cfValues"))
                .map(s -> getFetchList(s))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
		return sortByFetchList;
    }
    
   private List<String> getFetchList(String fetchProperty){
    	List<String> result = new ArrayList<String>();
    	final String[] split = fetchProperty.split("\\.");
    	String current="";
    	for(String str:split) {
    		if(!current.isEmpty()) {
    			result.add(current);
    			current=current.concat(".");
    		}
    		current=current.concat(str);
    	}
    	return result;
    }
	   
    public Map<String, Object> evaluateFilters(Map<String, Object> filters, Class<? extends IEntity> entity) {
        return Stream.of(filters.keySet().toArray())
                .map(key -> {
                    String keyObject = (String) key;
                    if(keyObject.matches("\\$filter[0-9]+$")) {
                    	return Collections.singletonMap(keyObject, evaluateFilters((Map<String, Object>)filters.get(key), entity));
                    } else if(!keyObject.startsWith("SQL") && !"$FILTER".equalsIgnoreCase(keyObject) && !"$OPERATOR".equalsIgnoreCase(keyObject)){

                    	String fieldName = keyObject.contains(" ") ? keyObject.substring(keyObject.indexOf(" ")).trim() : keyObject;
                    	String[] fields=fieldName.split(" ");
                    	FilterMapper filterMapper=null;
                    	for(String field:fields) {
                    		filterMapper=new FactoryFilterMapper().create(field, filters.get(key), (String) filters.get("cetCode"), serviceFunction, entity);
                    	}
                    	return Collections.singletonMap(keyObject, filterMapper.map());
                    } else if ("$OPERATOR".equalsIgnoreCase(keyObject)) {
                    	String filterOperator = (String) filters.get(keyObject);
                    	try {
	                        FilterOperatorEnum enumValue = FilterOperatorEnum.valueOf(filterOperator);
	                        return Collections.singletonMap(keyObject, enumValue);
                    	} catch (IllegalArgumentException e) {
                    		throw new IllegalArgumentException("Invalid $operator value. Accepted value : 'OR', 'AND'", e);
                    	}
                    }
                    return Collections.singletonMap(keyObject, filters.get(key));
                })
                .flatMap (map -> map.entrySet().stream())
                .filter(stringObjectEntry -> Objects.nonNull(stringObjectEntry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}