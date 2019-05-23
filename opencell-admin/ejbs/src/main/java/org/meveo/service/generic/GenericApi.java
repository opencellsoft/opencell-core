package org.meveo.service.generic;

import org.meveo.api.dto.generic.GenericRequestDto;
import org.meveo.api.dto.response.generic.GenericResponseDto;

public interface GenericApi {
    GenericResponseDto findBy(String requestedModelName, Long id, GenericRequestDto requestedDto);
}
