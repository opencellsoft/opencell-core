package org.meveo.api.dto;

import org.meveo.api.dto.response.PagingAndFiltering;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GenericPagingAndFiltering extends PagingAndFiltering {
   
    private List<String> genericFields = new ArrayList<>();
    
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
    
    
    public List<String> getGenericFields() {
        return genericFields;
    }
    
    public void setGenericFields(List<String> genericFields) {
        this.genericFields = Optional.ofNullable(genericFields).orElse(new ArrayList<>());
    }
    
}
