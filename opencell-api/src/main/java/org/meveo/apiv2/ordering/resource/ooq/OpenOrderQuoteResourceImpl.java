package org.meveo.apiv2.ordering.resource.ooq;

import org.meveo.apiv2.ordering.services.OpenOrderQuoteApi;
import org.meveo.model.ordering.OpenOrderQuoteStatusEnum;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

public class OpenOrderQuoteResourceImpl implements OpenOrderQuoteResource {

    @Inject
    private OpenOrderQuoteApi openOrderQuoteApi;

    @Override
    public Response changeStatus(String code, OpenOrderQuoteStatusEnum status) {
        return Response
                .ok()
                .entity(openOrderQuoteApi.changeStatus(code, status))
                .build();
    }
}