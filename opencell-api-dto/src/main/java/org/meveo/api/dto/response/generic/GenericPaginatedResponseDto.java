package org.meveo.api.dto.response.generic;

import java.util.List;

public class GenericPaginatedResponseDto {
    private List<GenericResponseDto> data;
    private Integer from;
    private Integer limit;
    private Long totalElements;
    
    public GenericPaginatedResponseDto withTotalElements(long totalElements) {
        this.totalElements = totalElements;
        return this;
    }
    
    public GenericPaginatedResponseDto(List<GenericResponseDto> data) {
        this.data = data;
    }
    
    public GenericPaginatedResponseDto withFrom(Integer from) {
        this.from = from;
        return this;
    }
    
    public GenericPaginatedResponseDto withLimit(Integer limit) {
        this.limit = limit;
        return this;
    }
    
    public List<GenericResponseDto> getData() {
        return data;
    }
    
    public Integer getFrom() {
        return from;
    }
    
    public Integer getLimit() {
        return limit;
    }
    
    public Long getTotalElements() {
        return totalElements;
    }
    
}
