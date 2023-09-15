package org.meveo.apiv2.dunning.impl;

import static java.lang.Long.valueOf;
import static java.util.stream.Collectors.toList;
import static org.meveo.apiv2.models.ImmutableResource.builder;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;

import org.meveo.admin.util.ResourceBundle;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.apiv2.dunning.AvailablePoliciesInput;
import org.meveo.apiv2.dunning.CheckSwitchResult;
import org.meveo.apiv2.dunning.DunningActionInstanceInput;
import org.meveo.apiv2.dunning.DunningCollectionPlanPause;
import org.meveo.apiv2.dunning.DunningCollectionPlanStop;
import org.meveo.apiv2.dunning.DunningLevelInstanceInput;
import org.meveo.apiv2.dunning.DunningMassSwitchInput;
import org.meveo.apiv2.dunning.ImmutableCheckSwitchResult;
import org.meveo.apiv2.dunning.ImmutableDunningActionInstanceSuccessResponse;
import org.meveo.apiv2.dunning.ImmutableDunningCollectionPlan;
import org.meveo.apiv2.dunning.ImmutableDunningLevelInstanceSuccessResponse;
import org.meveo.apiv2.dunning.ImmutableMassOperationSuccessResponse;
import org.meveo.apiv2.dunning.ImmutableMassSwitchResult;
import org.meveo.apiv2.dunning.ImmutableSwitchCollectionSuccessResponse;
import org.meveo.apiv2.dunning.MassPauseDunningCollectionPlan;
import org.meveo.apiv2.dunning.MassStopDunningCollectionPlan;
import org.meveo.apiv2.dunning.MassSwitchDunningCollectionPlan;
import org.meveo.apiv2.dunning.MassSwitchResult;
import org.meveo.apiv2.dunning.RemoveActionInstanceInput;
import org.meveo.apiv2.dunning.RemoveLevelInstanceInput;
import org.meveo.apiv2.dunning.SwitchDunningCollectionPlan;
import org.meveo.apiv2.dunning.UpdateLevelInstanceInput;
import org.meveo.apiv2.dunning.resource.DunningCollectionPlanResource;
import org.meveo.apiv2.dunning.service.DunningCollectionPlanApiService;
import org.meveo.apiv2.generic.common.LinkGenerator;
import org.meveo.apiv2.report.ImmutableSuccessResponse;
import org.meveo.model.dunning.DunningActionInstance;
import org.meveo.model.dunning.DunningCollectionPlan;
import org.meveo.model.dunning.DunningLevelInstance;
import org.meveo.model.dunning.DunningPolicy;

@Interceptors({ WsRestApiInterceptor.class })
public class DunningCollectionPlanResourceImpl implements DunningCollectionPlanResource {

    private final DunningCollectionPlanMapper collectionPlanMapper = new DunningCollectionPlanMapper();
    private final DunningLevelInstanceMapper levelInstanceMapper = new DunningLevelInstanceMapper();
    private final DunningActionInstanceMapper actionInstanceMapper = new DunningActionInstanceMapper();

    @Inject
    private DunningCollectionPlanApiService dunningCollectionPlanApiService;
    
    @Inject
    private ResourceBundle resourceMessages;

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
        return Response.ok(ImmutableSuccessResponse.builder()
                .status("SUCCESS")
                .build()).build();
    }

    @Override
    public Response checkMassSwitch(DunningMassSwitchInput massSwitchInput) {
        DunningPolicy policy = new DunningPolicy();
        policy.setId(massSwitchInput.getPolicy().getId());
        List<DunningCollectionPlan> collectionPlans =
                collectionPlanMapper.toEntities(massSwitchInput.getCollectionPlans());
        Map<String, Set<Long>> massCheckResult =
                dunningCollectionPlanApiService.checkMassSwitch(policy, collectionPlans).get();
        return Response.ok()
                    .entity(buildMassCheckResponse(massCheckResult, massSwitchInput.getCollectionPlans().size()))
                    .build();
    }

    private MassSwitchResult buildMassCheckResponse(Map<String, Set<Long>> massCheckResult, long total) {
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

    @Override
    public Response availableDunningPolicies(AvailablePoliciesInput availablePoliciesInput) {
        if(availablePoliciesInput.getCollectionPlan() == null) {
            throw new BadRequestException(resourceMessages.getString("error.collectionPlan.availablePolicies.missingInvoice"));
        }
        List<DunningPolicy> dunningPolicies =
                dunningCollectionPlanApiService.availableDunningPolicies(availablePoliciesInput.getCollectionPlan().getId());
        return Response.ok()
                .entity(collectionPlanMapper.toAvailablePolicies(dunningPolicies))
                .build();
    }

    @Override
    public Response massPauseCollectionPlan(MassPauseDunningCollectionPlan massPauseDunningCollectionPlan) {
        dunningCollectionPlanApiService.massPauseCollectionPlan(massPauseDunningCollectionPlan);
        return Response.ok(ImmutableMassOperationSuccessResponse.builder()
                .status("SUCCESS")
                .build()).build();
    }
    
    @Override
	public Response pauseCollectionPlan(DunningCollectionPlanPause dunningCollectionPlanInput, Long id) {
		DunningCollectionPlan dunningCollectionPlan = dunningCollectionPlanApiService.pauseCollectionPlan(dunningCollectionPlanInput, id).get();
		return Response.ok(toResourceOrderWithLink(collectionPlanMapper.toResource(dunningCollectionPlan))).build();
	}

    @Override
    public Response massStopCollectionPlan(MassStopDunningCollectionPlan massStopDunningCollectionPlan) {
        dunningCollectionPlanApiService.massStopCollectionPlan(massStopDunningCollectionPlan);
        return Response.ok(ImmutableMassOperationSuccessResponse.builder()
                .status("SUCCESS")
                .build()).build();
    }	

	@Override
	public Response stopCollectionPlan(DunningCollectionPlanStop dunningCollectionPlanInput, Long id) {
		DunningCollectionPlan dunningCollectionPlan = dunningCollectionPlanApiService.stopCollectionPlan(dunningCollectionPlanInput, id).get();
		return Response.ok(toResourceOrderWithLink(collectionPlanMapper.toResource(dunningCollectionPlan))).build();
	}

	
	@Override
	public Response resumeCollectionPlan(Long id) {
		DunningCollectionPlan dunningCollectionPlan = dunningCollectionPlanApiService.resumeCollectionPlan(id).get();
		return Response.ok(toResourceOrderWithLink(collectionPlanMapper.toResource(dunningCollectionPlan))).build();
	}

	@Override
	public Response removeDunningLevelInstance(RemoveLevelInstanceInput removeLevelInstanceInput) {
	    dunningCollectionPlanApiService.removeDunningLevelInstance(removeLevelInstanceInput);
        return Response.ok(ImmutableSuccessResponse.builder()
                .status("SUCCESS")
                .build()).build();
	}

	@Override
	public Response addDunningLevelInstance(DunningLevelInstanceInput dunningLevelInstanceInput) {
	    DunningLevelInstance newDunningLevelInstance = dunningCollectionPlanApiService.addDunningLevelInstance(dunningLevelInstanceInput).get();
	    return Response.ok(ImmutableDunningLevelInstanceSuccessResponse.builder()
            .status("SUCCESS")
            .newDunningLevelInstance(levelInstanceMapper.toResource(newDunningLevelInstance))
            .build()).build();
	}

	@Override
	public Response updateDunningLevelInstance(UpdateLevelInstanceInput updateLevelInstanceInput, Long levelInstanceId) {
        DunningLevelInstance updatedDunningLevelInstance = dunningCollectionPlanApiService.updateDunningLevelInstance(updateLevelInstanceInput, levelInstanceId).get();
        return Response.ok(ImmutableDunningLevelInstanceSuccessResponse.builder()
            .status("SUCCESS")
            .newDunningLevelInstance(levelInstanceMapper.toResource(updatedDunningLevelInstance))
            .build()).build();
    }

	@Override
    public Response removeDunningActionInstance(RemoveActionInstanceInput removeActionInstanceInput) {
        dunningCollectionPlanApiService.removeDunningActionInstance(removeActionInstanceInput);
        return Response.ok(ImmutableSuccessResponse.builder()
                .status("SUCCESS")
                .build()).build();
    }

	@Override
	public Response addDunningActionInstance(DunningActionInstanceInput dunningActionInstanceInput) {
	    DunningActionInstance newDunningActionInstance = dunningCollectionPlanApiService.addDunningActionInstance(dunningActionInstanceInput).get();
	    return Response.ok(ImmutableDunningActionInstanceSuccessResponse.builder()
            .status("SUCCESS")
            .newDunningActionInstance(actionInstanceMapper.toResource(newDunningActionInstance))
            .build()).build();
	}
	
	@Override
	public Response updateDunningActionInstance(DunningActionInstanceInput dunningActionInstanceInput, Long actionInstanceId) {
        DunningActionInstance updatedDunningActionInstance = dunningCollectionPlanApiService.updateDunningActionInstance(dunningActionInstanceInput, actionInstanceId).get();
        return Response.ok(ImmutableDunningActionInstanceSuccessResponse.builder()
            .status("SUCCESS")
            .newDunningActionInstance(actionInstanceMapper.toResource(updatedDunningActionInstance))
            .build()).build();
    }

	private Object toResourceOrderWithLink(org.meveo.apiv2.dunning.DunningCollectionPlan dunningCollectionPlan) {
        return ImmutableDunningCollectionPlan.copyOf(dunningCollectionPlan)
                .withLinks(
                        new LinkGenerator.SelfLinkGenerator(DunningCollectionPlanResource.class)
                                            .withId(dunningCollectionPlan.getId())
                                            .withGetAction().withPostAction().withPutAction().withPatchAction().withDeleteAction()
                                            .build()
                        );
    }
}