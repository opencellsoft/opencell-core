package org.meveo.apiv2.dunning.service;

import static java.util.Optional.empty;
import static java.util.Optional.of;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.apiv2.dunning.DunningCollectionPlanInput;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.dunning.DunningCollectionPlan;
import org.meveo.model.dunning.DunningPolicy;
import org.meveo.model.dunning.DunningPolicyLevel;
import org.meveo.service.payments.impl.DunningCollectionPlanService;
import org.meveo.service.payments.impl.DunningPolicyLevelService;
import org.meveo.service.payments.impl.DunningPolicyService;

public class DunningCollectionPlanApiService implements ApiService<DunningCollectionPlan> {

    @Inject
    private DunningCollectionPlanService dunningCollectionPlanService;

    @Inject
    private DunningPolicyService dunningPolicyService;

    @Inject
    private DunningPolicyLevelService dunningPolicyLevelService;

    @Override
    public List<DunningCollectionPlan> list(Long offset, Long limit, String sort, String orderBy, String filter) {
        return null;
    }

    @Override
    public Long getCount(String filter) {
        return null;
    }

    @Override
    public Optional<DunningCollectionPlan> findById(Long id) {
        DunningCollectionPlan dunningCollectionPlan = dunningCollectionPlanService.findById(id);
        if (dunningCollectionPlan == null) {
            throw new NotFoundException("Collection plan with id" + id + "does not exits");
        }
        return of(dunningCollectionPlan);
    }

    @Override
    public DunningCollectionPlan create(DunningCollectionPlan baseEntity) {
        return null;
    }

    @Override
    public Optional<DunningCollectionPlan> update(Long id, DunningCollectionPlan baseEntity) {
        return empty();
    }

    @Override
    public Optional<DunningCollectionPlan> patch(Long id, DunningCollectionPlan baseEntity) {
        return empty();
    }

    @Override
    public Optional<DunningCollectionPlan> delete(Long id) {
        try {
            dunningCollectionPlanService.remove(id);
            return empty();
        } catch (Exception exception) {
            throw new BusinessException(exception.getMessage());
        }
    }

    @Override
    public Optional<DunningCollectionPlan> findByCode(String code) {
        return empty();
    }

    public Optional<DunningCollectionPlan> switchCollectionPlan(Long collectionPlanId, DunningCollectionPlanInput dunningCollectionPlanInput) {
        DunningCollectionPlan collectionPlan = dunningCollectionPlanService.findById(collectionPlanId);
        if (collectionPlan == null) {
            throw new NotFoundException("Dunning collection plan with id " + collectionPlanId + " does not exits");
        }
        DunningPolicy policy = dunningPolicyService.findById(dunningCollectionPlanInput.getDunningPolicy().getId());
        if (policy == null) {
            throw new NotFoundException("Policy with id " + dunningCollectionPlanInput.getDunningPolicy().getId() + " does not exits");
        }
        DunningPolicyLevel policyLevel = dunningPolicyLevelService.findById(dunningCollectionPlanInput.getPolicyLevel().getId());
        if (policyLevel == null) {
            throw new NotFoundException("Policy level with id " + dunningCollectionPlanInput.getPolicyLevel().getId() + " does not exits");
        }
        return of(dunningCollectionPlanService.switchCollectionPlan(collectionPlan, policy, policyLevel));
    }
}