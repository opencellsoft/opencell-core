package org.meveo.api.dto;

import org.meveo.api.dto.response.PagingAndFiltering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class GenericPagingAndFiltering extends PagingAndFiltering {
   
    private Set<String> genericFields = new HashSet<>();
    private String nestedEntities = new String();
    
    public GenericPagingAndFiltering(String encodedQuery, String fields, Integer offset, Integer limit, String sortBy, SortOrder sortOrder) {
        super(encodedQuery, fields, offset, limit, sortBy, sortOrder);
    }
    
    public GenericPagingAndFiltering(String fullTextFilter, Map<String, Object> filters, String fields, Integer offset, Integer limit, String sortBy, SortOrder sortOrder) {
        super(fullTextFilter, filters, fields, offset, limit, sortBy, sortOrder);
    }
    
    public GenericPagingAndFiltering() {
    }
    
    public String getSortByOrDefault(String defaultSortingProp) {
        return Optional.ofNullable(getSortBy()).orElse(defaultSortingProp);
    }
    
    public SortOrder getSortOrderOrDefault(SortOrder defaultSortOrder) {
        return Optional.ofNullable(getSortOrder()).orElse(defaultSortOrder);
    }
    
    
    public Set<String> getGenericFields() {
        return genericFields;
    }
    
    public void setGenericFields(Set<String> genericFields) {
        this.genericFields = Optional.ofNullable(genericFields).orElse(new HashSet<>());
    }
    
    public String getNestedEntities() {
        return nestedEntities;
    }

    public void setNestedEntities(String nestedEntities) {
        super.setFields(nestedEntities);
        this.nestedEntities = nestedEntities;
    }

}
