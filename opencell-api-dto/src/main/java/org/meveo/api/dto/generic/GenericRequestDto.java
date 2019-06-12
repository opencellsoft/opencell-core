package org.meveo.api.dto.generic;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.commons.utils.StringUtils;

public class GenericRequestDto {
    private List<String> fields;
    private PagingAndFiltering pagingAndFiltering;

    public GenericRequestDto() {
        this.fields = new ArrayList<>();
    }

    public List<String> getFields() {
        return this.fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields == null ? Collections.emptyList() : fields.stream().filter(StringUtils::isNotBlank).map(String::toLowerCase).collect(Collectors.toList());
    }
    
    public PagingAndFiltering getPagingAndFiltering() {
        return pagingAndFiltering;
    }
    
    public void setPagingAndFiltering(PagingAndFiltering pagingAndFiltering) {
        this.pagingAndFiltering = pagingAndFiltering == null ? new PagingAndFiltering() : pagingAndFiltering;
    }
}
