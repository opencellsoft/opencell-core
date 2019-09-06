package org.meveo.apiv2.services.generic;

import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.apiv2.generic.GenericPagingAndFiltering;
import org.meveo.apiv2.generic.ImmutableGenericPagingAndFiltering;

import java.util.ArrayList;

public class GenericRequestMapper {
    public PaginationConfiguration mapTo(GenericPagingAndFiltering genericPagingAndFiltering){
        if(genericPagingAndFiltering == null){
            return getPaginationConfiguration(ImmutableGenericPagingAndFiltering.builder().build());
        }
        return getPaginationConfiguration(genericPagingAndFiltering);
    }

    private PaginationConfiguration getPaginationConfiguration(GenericPagingAndFiltering genericPagingAndFiltering) {
        return new PaginationConfiguration(genericPagingAndFiltering.getOffset().intValue(), genericPagingAndFiltering.getLimit().intValue(),
                genericPagingAndFiltering.getFilters(), genericPagingAndFiltering.getFullTextFilter(),
                new ArrayList(genericPagingAndFiltering.getNestedEntities()), genericPagingAndFiltering.getSortBy(),
                org.primefaces.model.SortOrder.valueOf(genericPagingAndFiltering.getSortOrder()));
    }
}
