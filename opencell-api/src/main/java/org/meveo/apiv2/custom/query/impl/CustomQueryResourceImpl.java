package org.meveo.apiv2.custom.query.impl;

import org.meveo.apiv2.custom.CustomQueries;
import org.meveo.apiv2.custom.ImmutableCustomQueries;
import org.meveo.apiv2.custom.ImmutableCustomQuery;
import org.meveo.apiv2.custom.query.resource.CustomQueryResource;
import org.meveo.apiv2.custom.query.service.CustomQueryApiService;
import org.meveo.apiv2.ordering.common.LinkGenerator;
import org.meveo.model.custom.query.CustomQuery;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import java.util.List;

public class CustomQueryResourceImpl implements CustomQueryResource {

    @Inject
    private CustomQueryApiService customQueryApiService;

    private CustomQueryMapper mapper = new CustomQueryMapper();

    @Override
    public Response find(Long id) {
        CustomQuery customQuery = customQueryApiService.findById(id)
                .orElseThrow(() -> new NotFoundException("The query with {" + id + "} does not exists"));
        return Response.ok().entity(mapper.toResource(customQuery)).build();
    }

    @Override
    public Response delete(Long id) {
        if (customQueryApiService.delete(id).isEmpty()) {
            throw new NotFoundException("The query with {" + id + "} does not exists");
        }
        return null;
    }

    @Override
    public Response getCustomQueries(Long offset, Long limit, String sort, String orderBy,
                                     String filter, Request request) {
        List<CustomQuery> customQueryEntities = customQueryApiService.list(offset, limit, sort, orderBy, filter);
        EntityTag etag = new EntityTag(Integer.toString(customQueryEntities.hashCode()));
        CacheControl cc = new CacheControl();
        cc.setMaxAge(1000);
        Response.ResponseBuilder builder = request.evaluatePreconditions(etag);
        if (builder != null) {
            builder.cacheControl(cc);
            return builder.build();
        }
        ImmutableCustomQuery[] customQueryList = customQueryEntities
                .stream()
                .map(customQuery -> mapper.toResource(customQuery))
                .toArray(ImmutableCustomQuery[]::new);
        Long count = Long.valueOf(customQueryEntities.size());
        CustomQueries customQueries = ImmutableCustomQueries.builder()
                .addData(customQueryList)
                .offset(offset)
                .limit(limit)
                .total(count)
                .build()
                .withLinks(new LinkGenerator.PaginationLinkGenerator(CustomQueryResource.class)
                        .offset(offset).limit(limit).total(count).build());
        return Response.ok().cacheControl(cc).tag(etag).entity(customQueries).build();
    }
}