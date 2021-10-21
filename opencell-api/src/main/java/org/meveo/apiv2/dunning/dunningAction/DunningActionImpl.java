package org.meveo.apiv2.dunning.dunningAction;

import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.apiv2.dunning.DunningAction;
import org.meveo.apiv2.dunning.ImmutableDunningAction;
import org.meveo.service.payments.impl.DunningActionService;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.Collections;

public class DunningActionImpl implements DunningActionResource{

    @Inject
    private DunningActionService dunningActionService;

    @Override
    public Response getDunningAction(String code) {
        if(code == null || code.isBlank()){
            throw new InvalidParameterException("dunning action code is required.");
        }
        org.meveo.model.payments.DunningAction dunningAction = dunningActionService.findByCode(code);
        if(dunningAction == null) {
            throw new EntityDoesNotExistsException("dunning action with code "+code+" does not exist.");
        }
        return Response.ok()
                    .entity(ImmutableDunningAction.builder().build().toDunningAction(dunningAction)).build();
    }

    @Override
    public Response createDunningAction(DunningAction dunningAction) {
        dunningActionService.create(dunningAction.toEntity());
        return Response.ok().build();
    }

}
