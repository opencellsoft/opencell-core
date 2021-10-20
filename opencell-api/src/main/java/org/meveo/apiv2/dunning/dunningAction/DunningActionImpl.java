package org.meveo.apiv2.dunning.dunningAction;

import org.meveo.apiv2.dunning.DunningAction;
import org.meveo.service.payments.impl.DunningActionService;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

public class DunningActionImpl implements DunningActionResource{

    @Inject
    private DunningActionService dunningActionService;

    @Override
    public Response createDunningAction(DunningAction dunningAction) {
        dunningActionService.create(dunningAction.toEntity());
        return Response.ok().build();
    }
}
