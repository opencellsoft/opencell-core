package org.meveo.apiv2.audit.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.apiv2.audit.AuditDataLogDto;
import org.meveo.apiv2.audit.ImmutableAuditDataConfigurationListDto;
import org.meveo.apiv2.audit.ImmutableAuditDataLogDto;
import org.meveo.apiv2.audit.ImmutableAuditDataLogListDto;
import org.meveo.apiv2.audit.resource.AuditDataLogResource;
import org.meveo.apiv2.audit.service.AuditDataLogApiService;
import org.meveo.apiv2.ordering.common.LinkGenerator;
import org.meveo.model.audit.AuditDataConfiguration;
import org.meveo.model.audit.AuditDataLog;
import org.meveo.service.audit.logging.AuditDataLogService;

@Interceptors({ WsRestApiInterceptor.class })
public class AuditDataLogResourceImpl implements AuditDataLogResource {

    @Inject
    private AuditDataLogApiService auditDataLogApi;

    private AuditDataLogMapper mapper = new AuditDataLogMapper();

    @Override
    public Response listByEntity(String entityClass, Long entityId, String field, Request request) {

        List<AuditDataLog> auditDataLogSummaries = auditDataLogApi.list(entityClass, entityId, null, field);
        Map<String, Object> filter = new HashMap<String, Object>();
        filter.put(AuditDataLogService.SEARCH_CRITERIA_ENTITY_CLASS, entityClass);
        filter.put(AuditDataLogService.SEARCH_CRITERIA_ENTITY_ID, entityId);
        if (field != null) {
            filter.put(AuditDataLogService.SEARCH_CRITERIA_FIELD, field);
        }
        return mapToAuditDataLogSummaryListResponse(0L, 500L, filter, request, auditDataLogSummaries);
    }

    @Override
    public Response list(Long offset, Long limit, String sort, String orderBy, Map<String, Object> filter, Request request) {
        List<AuditDataLog> auditDataConfigurations = auditDataLogApi.list(offset, limit, sort == null ? "id" : sort, orderBy, filter);
        return mapToAuditDataLogSummaryListResponse(offset, limit, filter, request, auditDataConfigurations);
    }

    private Response mapToAuditDataLogSummaryListResponse(Long offset, Long limit, Map<String, Object> filter, Request request, List<AuditDataLog> auditDataLogs) {
        EntityTag etag = new EntityTag(Integer.toString(auditDataLogs.hashCode()));
        CacheControl cc = new CacheControl();
        cc.setMaxAge(1000);
        Response.ResponseBuilder builder = request.evaluatePreconditions(etag);
        if (builder != null) {
            builder.cacheControl(cc);
            return builder.build();
        }
        ImmutableAuditDataLogDto[] dtoList = auditDataLogs.stream().map(auditDataLog -> toResourceOrderWithLink(mapper.toResource(auditDataLog))).toArray(ImmutableAuditDataLogDto[]::new);

        Long count = auditDataLogApi.getCount(filter);

        ImmutableAuditDataLogListDto listDto = ImmutableAuditDataLogListDto.builder().addData(dtoList).total(count).offset(offset).limit(limit).total(count).build()
            .withLinks(new LinkGenerator.PaginationLinkGenerator(AuditDataLog.class).offset(offset).limit(limit).total(count).build());
        return Response.ok().cacheControl(cc).tag(etag).entity(listDto).build();
    }

    private AuditDataLogDto toResourceOrderWithLink(AuditDataLogDto dto) {
        return ImmutableAuditDataLogDto.copyOf(dto)
            .withLinks(new LinkGenerator.SelfLinkGenerator(AuditDataLogResource.class).withId(dto.getId()).withGetAction().withPostAction().withPutAction().withPatchAction().withDeleteAction().build());
    }

}