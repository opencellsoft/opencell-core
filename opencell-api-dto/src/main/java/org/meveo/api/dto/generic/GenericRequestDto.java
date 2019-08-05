package org.meveo.api.dto.generic;


import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.commons.utils.StringUtils;

public class GenericRequestDto {
    private Set<String> fields;
    private PagingAndFiltering pagingAndFiltering;

    public GenericRequestDto() {
        this.fields = new HashSet<>();
    }

    public Set<String> getFields() {
        return this.fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields == null ? Collections.emptySet() : fields.stream().filter(StringUtils::isNotBlank).map(String::toLowerCase).collect(Collectors.toSet());
    }
    
    public PagingAndFiltering getPagingAndFiltering() {
        return pagingAndFiltering;
    }
    
    public void setPagingAndFiltering(PagingAndFiltering pagingAndFiltering) {
        this.pagingAndFiltering = pagingAndFiltering == null ? new PagingAndFiltering() : pagingAndFiltering;
    }
}
