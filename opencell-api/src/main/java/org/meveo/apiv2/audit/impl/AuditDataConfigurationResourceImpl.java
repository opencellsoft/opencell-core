package org.meveo.apiv2.audit.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.apiv2.audit.AuditDataConfigurationDto;
import org.meveo.apiv2.audit.ImmutableAuditDataConfigurationDto;
import org.meveo.apiv2.audit.ImmutableAuditDataConfigurationListDto;
import org.meveo.apiv2.audit.resource.AuditDataConfigurationResource;
import org.meveo.apiv2.audit.service.AuditDataConfigurationApiService;
import org.meveo.apiv2.ordering.common.LinkGenerator;
import org.meveo.model.audit.AuditDataConfiguration;

@Interceptors({ WsRestApiInterceptor.class })
public class AuditDataConfigurationResourceImpl implements AuditDataConfigurationResource {

    @Inject
    private AuditDataConfigurationApiService auditDataConfigurationApi;

    private AuditDataConfigurationMapper mapper = new AuditDataConfigurationMapper();

    @Override
    public Response createAuditDataConfiguration(AuditDataConfigurationDto auditDataConfiguration) {

        AuditDataConfiguration auditDataConfigurationEntity = mapper.toEntity(auditDataConfiguration);
        auditDataConfigurationEntity = auditDataConfigurationApi.create(auditDataConfigurationEntity);
        return Response.created(LinkGenerator.getUriBuilderFromResource(AuditDataConfigurationResource.class, auditDataConfigurationEntity.getId()).build())
            .entity(toResourceOrderWithLink(mapper.toResource(auditDataConfigurationEntity))).build();
    }

    @Override
    public Response updateAuditDataConfiguration(Long id, AuditDataConfigurationDto auditDataConfiguration) {

        AuditDataConfiguration auditDataConfigurationEntity = mapper.toEntity(auditDataConfiguration);
        Optional<AuditDataConfiguration> auditDataConfigrationUpdated = auditDataConfigurationApi.update(id, auditDataConfigurationEntity);
        return Response.created(LinkGenerator.getUriBuilderFromResource(AuditDataConfigurationResource.class, auditDataConfigurationEntity.getId()).build())
            .entity(toResourceOrderWithLink(mapper.toResource(auditDataConfigrationUpdated.orElseThrow(NotFoundException::new)))).build();
    }

    private AuditDataConfigurationDto toResourceOrderWithLink(AuditDataConfigurationDto dto) {
        return ImmutableAuditDataConfigurationDto.copyOf(dto)
            .withLinks(new LinkGenerator.SelfLinkGenerator(AuditDataConfigurationResource.class).withId(dto.getId()).withGetAction().withPostAction().withPutAction().withPatchAction().withDeleteAction().build());
    }

    @Override
    public Response find(String entityClass, Request request) {
        return auditDataConfigurationApi.findByEntityClass(entityClass).map(auditDataConfiguration -> {
            EntityTag etag = new EntityTag(Integer.toString(auditDataConfiguration.hashCode()));
            CacheControl cc = new CacheControl();
            cc.setMaxAge(1000);
            Response.ResponseBuilder builder = request.evaluatePreconditions(etag);
            if (builder != null) {
                builder.cacheControl(cc);
                return builder.build();
            }
            return Response.ok().cacheControl(cc).tag(etag).entity(toResourceOrderWithLink(mapper.toResource(auditDataConfiguration))).build();
        }).orElseThrow(NotFoundException::new);
    }

    @Override
    @Transactional
    public Response delete(String entityClass, Request request) {
        ActionStatus result = new ActionStatus();
        result.setJson(null);
        try {
            auditDataConfigurationApi.delete(entityClass).map(auditDataConfiguration -> Response.ok().entity(toResourceOrderWithLink(mapper.toResource(auditDataConfiguration))).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).entity(result).build());
            return Response.ok(result).build();
        } catch (BadRequestException e) {
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(result).build();
        }
    }

    @Override
    public Response list(Long offset, Long limit, String sort, String orderBy, Map<String, Object> filter, Request request) {
        List<AuditDataConfiguration> auditDataConfigurations = auditDataConfigurationApi.list(offset, limit, sort, orderBy, filter);
        return mapToAuditLogConfigurationListResponse(offset, limit, filter, request, auditDataConfigurations);
    }

    private Response mapToAuditLogConfigurationListResponse(Long offset, Long limit, Map<String, Object> filter, Request request, List<AuditDataConfiguration> auditDataConfigurations) {
        EntityTag etag = new EntityTag(Integer.toString(auditDataConfigurations.hashCode()));
        CacheControl cc = new CacheControl();
        cc.setMaxAge(1000);
        Response.ResponseBuilder builder = request.evaluatePreconditions(etag);
        if (builder != null) {
            builder.cacheControl(cc);
            return builder.build();
        }
        ImmutableAuditDataConfigurationDto[] dtoList = auditDataConfigurations.stream().map(auditDataConfig -> toResourceOrderWithLink(mapper.toResource(auditDataConfig)))
            .toArray(ImmutableAuditDataConfigurationDto[]::new);

        Long count = auditDataConfigurationApi.getCount(filter);
        ImmutableAuditDataConfigurationListDto listDto = ImmutableAuditDataConfigurationListDto.builder().addData(dtoList).offset(offset).limit(limit).total(count).build()
            .withLinks(new LinkGenerator.PaginationLinkGenerator(AuditDataConfiguration.class).offset(offset).limit(limit).total(count).build());
        return Response.ok().cacheControl(cc).tag(etag).entity(listDto).build();
    }
}
