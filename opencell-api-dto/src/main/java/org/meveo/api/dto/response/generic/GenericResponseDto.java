package org.meveo.api.dto.response.generic;

import java.util.Map;

public class GenericResponseDto {
    private Map<String, String> value;

    public GenericResponseDto(Map<String, String> value) {
        this.value = value;
    }

    public Map<String, String> getValue() {
        return value;
    }
}
