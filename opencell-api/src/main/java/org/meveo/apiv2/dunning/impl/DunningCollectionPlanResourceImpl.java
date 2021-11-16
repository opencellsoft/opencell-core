package org.meveo.apiv2.dunning.impl;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.meveo.apiv2.dunning.ImmutableSwitchCollectionSuccessResponse;
import org.meveo.apiv2.dunning.MassSwitchDunningCollectionPlan;
import org.meveo.apiv2.dunning.SwitchDunningCollectionPlan;
import org.meveo.apiv2.dunning.resource.DunningCollectionPlanResource;
import org.meveo.apiv2.dunning.service.DunningCollectionPlanApiService;
import org.meveo.model.dunning.DunningCollectionPlan;

public class DunningCollectionPlanResourceImpl implements DunningCollectionPlanResource {

    private final DunningCollectionPlanMapper collectionPlanMapper = new DunningCollectionPlanMapper();

    @Inject
    private DunningCollectionPlanApiService dunningCollectionPlanApiService;

    @Override
    public Response switchCollectionPlan(Long collectionPlanId, SwitchDunningCollectionPlan switchDunningCollectionPlan) {
        DunningCollectionPlan newCollectionPlan = dunningCollectionPlanApiService.switchCollectionPlan(collectionPlanId, switchDunningCollectionPlan).get();
        return Response.ok(ImmutableSwitchCollectionSuccessResponse.builder()
            .status("SUCCESS")
            .newCollectionPlan(collectionPlanMapper.toResource(newCollectionPlan))
            .build()).build();
    }
    
    @Override
    public Response massSwitchCollectionPlan(MassSwitchDunningCollectionPlan massSwitchDunningCollectionPlan) {
        dunningCollectionPlanApiService.massSwitchCollectionPlan(massSwitchDunningCollectionPlan);
        return Response.ok(ImmutableSwitchCollectionSuccessResponse.builder()
            .status("SUCCESS")
            .build()).build();
    }
}