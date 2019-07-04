package org.meveo.apiv2;

import org.meveo.api.dto.GenericPagingAndFiltering;
import org.meveo.api.dto.generic.GenericRequestDto;
import org.meveo.api.dto.response.generic.GenericPaginatedResponseDto;
import org.meveo.api.dto.response.generic.GenericResponseDto;
import org.meveo.apiv2.services.GenericApiLoadService;
import org.meveo.apiv2.services.GenericApiUpdateService;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

public class GenericResourceImpl implements GenericResource {
    
    @Inject
    private GenericApiLoadService loadService;
    
    @Inject
    private GenericApiUpdateService updateService;
    
    @Override
    public Response getAll(String entityName, GenericPagingAndFiltering searchConfig) {
        GenericPaginatedResponseDto paginatedRecords = loadService.findPaginatedRecords(entityName, searchConfig);
        return Response.ok().entity(paginatedRecords).links(buildLink(entityName)).build();
    }
    
    @Override
    public Response get(String entityName, Long id, GenericRequestDto requestDto) {
        GenericResponseDto retrievedResource = loadService.findByClassNameAndId(entityName, id, requestDto);
        return Response.ok().entity(retrievedResource).links(buildLink(entityName, String.valueOf(id))).build();
    }
    
    @Override
    public Response update(String entityName, Long id, String dto) {
        updateService.update(entityName, id, dto);
        return Response.ok().links(buildLink(entityName, String.valueOf(id))).build();
    }
    
}
