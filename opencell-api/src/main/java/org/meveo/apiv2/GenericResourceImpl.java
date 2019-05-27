package org.meveo.apiv2;

import javax.inject.Inject;

import org.meveo.api.dto.generic.GenericRequestDto;
import org.meveo.api.dto.response.generic.GenericResponseDto;

public class GenericResourceImpl implements GenericResource {

    @Inject
    private org.meveo.apiv2.services.GenericApiService genericApiService;

    @Override
    public GenericResponseDto get(String entityName, Long id, GenericRequestDto requestDto) {
        return genericApiService.findByClassNameAndId(entityName, id, requestDto);
    }
}
