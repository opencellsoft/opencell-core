package org.meveo.apiv2.custom.query.impl;

import org.meveo.apiv2.custom.query.resource.CustomQueryResource;
import org.meveo.apiv2.custom.query.service.CustomQueryApiService;
import org.meveo.model.custom.query.CustomQuery;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

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
        if(customQueryApiService.delete(id).isEmpty()) {
            throw new NotFoundException("The query with {" + id + "} does not exists");
        }
        return null;
    }
}