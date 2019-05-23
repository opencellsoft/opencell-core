package org.meveo.apiv2;

import org.meveo.api.dto.generic.GenericRequestDto;
import org.meveo.api.dto.response.generic.GenericResponseDto;
import org.meveo.service.generic.GenericApi;

import javax.inject.Inject;

public class GenericResourceImpl implements GenericResource {

    @Inject
    private GenericApi genericApi;

    @Override
    public GenericResponseDto get(String entityName, Long id, GenericRequestDto requestDto) {
        return genericApi.findBy(entityName, id, requestDto);
    }
}
