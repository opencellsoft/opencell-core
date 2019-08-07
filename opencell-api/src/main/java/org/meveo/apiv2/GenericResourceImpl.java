package org.meveo.apiv2;

import org.meveo.api.dto.GenericPagingAndFiltering;
import org.meveo.api.dto.generic.GenericRequestDto;
import org.meveo.apiv2.common.LinkGenerator;
import org.meveo.apiv2.generic.ImmutableGenericPaginatedResource;
import org.meveo.apiv2.services.generic.GenericApiCreateService;
import org.meveo.apiv2.services.generic.GenericApiLoadService;
import org.meveo.apiv2.services.generic.GenericApiUpdateService;
import org.meveo.apiv2.services.generic.JsonGenericApiMapper.JsonGenericMapper;
import org.meveo.model.BaseEntity;

import javax.inject.Inject;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.Map;

public class GenericResourceImpl implements GenericResource {
    @Inject
    private GenericApiLoadService loadService;
    
    @Inject
    private GenericApiUpdateService updateService;
    
    @Inject
    private GenericApiCreateService createService;

    @Override
    public Response getAll(String entityName, GenericPagingAndFiltering searchConfig) {
        Class entityClass = loadService.getEntityClass(entityName);
        return Response.ok().entity(loadService.findPaginatedRecords(entityClass, searchConfig))
                .links(buildPaginatedResourceLink(entityName)).build();
    }
    
    @Override
    public Response get(String entityName, Long id, GenericRequestDto requestDto) {
        Class entityClass = loadService.getEntityClass(entityName);
        return Response.ok().entity(loadService.findByClassNameAndId(entityClass, id, requestDto))
                .links(buildSingleResourceLink(entityName, id)).build();
    }
    
    @Override
    public Response update(String entityName, Long id, String dto) {
        updateService.update(entityName, id, dto);
        return Response.ok().links(buildSingleResourceLink(entityName, id)).build();
    }
    
    @Override
    public Response create(String entityName, String dto) {
        Long entityId = createService.create(entityName, dto);
        return Response.ok().links(buildSingleResourceLink(entityName, entityId)).build();
    }

    private Link buildPaginatedResourceLink(String entityName) {
        return new LinkGenerator.SelfLinkGenerator(GenericResource.class)
                .withGetAction().withPostAction()
                .withDeleteAction().build(entityName);
    }
    private Link buildSingleResourceLink(String entityName, Long entityId) {
        return new LinkGenerator.SelfLinkGenerator(GenericResource.class)
                .withGetAction().withPostAction().withId(entityId)
                .withDeleteAction().build(entityName);
    }
}
