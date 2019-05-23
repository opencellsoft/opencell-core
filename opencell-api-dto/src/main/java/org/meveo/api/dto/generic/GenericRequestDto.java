package org.meveo.api.dto.generic;

import org.meveo.commons.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GenericRequestDto {
    private List<String> fields;

    public GenericRequestDto() {
        this.fields = new ArrayList<>();
    }

    public List<String> getFields() {
        return fields.stream().filter(StringUtils::isNotBlank).map(String::toLowerCase).collect(Collectors.toList());
    }

    public void setFields(List<String> fields) {
        this.fields = Optional.ofNullable(fields).orElse(new ArrayList<>());
    }
}
