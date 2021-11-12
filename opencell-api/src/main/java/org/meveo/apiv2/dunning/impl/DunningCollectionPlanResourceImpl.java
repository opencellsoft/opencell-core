package org.meveo.apiv2.dunning.impl;

import static org.meveo.apiv2.generic.common.LinkGenerator.getUriBuilderFromResource;

import org.meveo.apiv2.dunning.DunningCollectionPlanInput;
import org.meveo.apiv2.dunning.ImmutableDunningCollectionPlan;
import org.meveo.apiv2.dunning.resource.DunningCollectionPlanResource;
import org.meveo.apiv2.dunning.resource.DunningSettingResource;
import org.meveo.apiv2.dunning.service.DunningCollectionPlanApiService;
import org.meveo.apiv2.generic.common.LinkGenerator;
import org.meveo.model.dunning.DunningCollectionPlan;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

public class DunningCollectionPlanResourceImpl implements DunningCollectionPlanResource {

    private final DunningCollectionPlanMapper collectionPlanMapper = new DunningCollectionPlanMapper();

    @Inject
    private DunningCollectionPlanApiService dunningCollectionPlanApiService;

    @Override
    public Response switchCollectionPlan(Long collectionPlanId, DunningCollectionPlanInput dunningCollectionPlanInput) {
        DunningCollectionPlan newCollectionPlan = dunningCollectionPlanApiService.switchCollectionPlan(collectionPlanId, dunningCollectionPlanInput).get();
        return Response.created(getUriBuilderFromResource(DunningCollectionPlanResource.class, newCollectionPlan.getId()).build())
                .entity(toResourceOrderWithLink(collectionPlanMapper.toResource(newCollectionPlan)))
                .build();
    }

    private org.meveo.apiv2.dunning.DunningCollectionPlan toResourceOrderWithLink(org.meveo.apiv2.dunning.DunningCollectionPlan dunningCollectionPlan) {
        return ImmutableDunningCollectionPlan.copyOf(dunningCollectionPlan)
                .withLinks(
                        new LinkGenerator.SelfLinkGenerator(DunningSettingResource.class)
                                .withId(dunningCollectionPlan.getId())
                                .withGetAction().withPostAction().withPutAction().withPatchAction().withDeleteAction()
                                .build()
                );
    }
}