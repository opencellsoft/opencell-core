package org.meveo.apiv2.dunning.service;

import static java.util.Optional.empty;
import static java.util.Optional.of;

import org.meveo.admin.exception.BusinessException;
import org.meveo.apiv2.dunning.DunningCollectionPlanInput;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.dunning.DunningCollectionPlan;
import org.meveo.model.dunning.DunningPolicyLevel;
import org.meveo.model.dunning.DunningStopReason;
import org.meveo.service.payments.impl.DunningCollectionPlanService;
import org.meveo.service.payments.impl.DunningPolicyLevelService;
import org.meveo.service.payments.impl.DunningStopReasonsService;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.Optional;

public class DunningCollectionPlanApiService implements ApiService<DunningCollectionPlan> {

    @Inject
    private DunningCollectionPlanService dunningCollectionPlanService;

    @Inject
    private DunningPolicyLevelService dunningPolicyLevelService;

    @Inject
    private DunningStopReasonsService dunningStopReasonsService;

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
        if(dunningCollectionPlan == null) {
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

    public Optional<DunningCollectionPlan> renew(Long id, DunningCollectionPlanInput renewedPlan) {
        DunningCollectionPlan collectionPlan = dunningCollectionPlanService.findById(id);
        if(collectionPlan == null) {
            throw new NotFoundException("Dunning collection plan with id " + id + " does not exits");
        }
        DunningPolicyLevel policyLevel = dunningPolicyLevelService.findById(renewedPlan.getPolicyLevel().getId());
        if (policyLevel == null) {
            throw new NotFoundException("Policy level with id " + renewedPlan.getPolicyLevel().getId() + " does not exits");
        }
        DunningStopReason stopReason = dunningStopReasonsService.findById(renewedPlan.getStopReason().getId());
        if(stopReason == null) {
            throw new NotFoundException("Stop reason with id " + renewedPlan.getStopReason().getId() + " does not exits");
        }
        return of(dunningCollectionPlanService.renew(collectionPlan, policyLevel, stopReason, renewedPlan.getStopDate()));
    }
}