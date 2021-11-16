package org.meveo.apiv2.dunning.impl;

import org.meveo.apiv2.dunning.*;
import org.meveo.apiv2.dunning.resource.DunningCollectionPlanResource;
import org.meveo.apiv2.dunning.service.DunningCollectionPlanApiService;
import org.meveo.apiv2.generic.common.LinkGenerator;
import org.meveo.model.dunning.DunningCollectionPlan;
import org.meveo.model.dunning.DunningPolicy;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

import static java.lang.Long.valueOf;
import static java.util.stream.Collectors.toList;
import static org.meveo.apiv2.models.ImmutableResource.builder;

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

    @Override
    public Response checkMassSwitch(DunningMassSwitchInput massSwitchInput) {
        DunningPolicy policy = new DunningPolicy();
        policy.setId(massSwitchInput.getPolicy().getId());
        List<DunningCollectionPlan> collectionPlans =
                collectionPlanMapper.toEntities(massSwitchInput.getCollectionPlans());
        Map<String, List<Long>> massCheckResult =
                dunningCollectionPlanApiService.checkMassSwitch(policy, collectionPlans).get();
        return Response.ok()
                    .entity(buildMassCheckResponse(massCheckResult, massSwitchInput.getCollectionPlans().size()))
                    .build();
    }

    private MassSwitchResult buildMassCheckResponse(Map<String, List<Long>> massCheckResult, long total) {
        CheckSwitchResult canBeSwitched = ImmutableCheckSwitchResult.builder()
                .total(valueOf(massCheckResult.get("canBESwitched").size()))
                .collectionPlans(massCheckResult.get("canBESwitched")
                        .stream()
                        .map(id -> builder().id(id).build())
                        .collect(toList()))
                .build();
        CheckSwitchResult canNotBeSwitched = ImmutableCheckSwitchResult.builder()
                .total(valueOf(massCheckResult.get("canNotBESwitched").size()))
                .collectionPlans(massCheckResult.get("canNotBESwitched")
                        .stream()
                        .map(id -> builder().id(id).build())
                        .collect(toList()))
                .build();
        return ImmutableMassSwitchResult.builder()
                .total(total)
                .canBeSwitched(canBeSwitched)
                .canNotBeSwitched(canNotBeSwitched)
                .build();
    }
}