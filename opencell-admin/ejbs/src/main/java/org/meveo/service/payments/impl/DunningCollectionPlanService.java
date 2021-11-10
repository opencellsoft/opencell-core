package org.meveo.service.payments.impl;

import static java.util.Optional.ofNullable;
import static org.meveo.model.shared.DateUtils.addDaysToDate;

import org.meveo.model.dunning.DunningCollectionPlan;
import org.meveo.model.dunning.DunningPolicyLevel;
import org.meveo.model.dunning.DunningStopReason;
import org.meveo.service.base.PersistenceService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Date;

@Stateless
public class DunningCollectionPlanService extends PersistenceService<DunningCollectionPlan> {

    @Inject
    private DunningPolicyLevelService dunningPolicyLevelService;

    @Inject
    private DunningStopReasonsService dunningStopReasonsService;

    public DunningCollectionPlan renew(DunningCollectionPlan collectionPlan,
                                       DunningPolicyLevel policyLevel, DunningStopReason stopReason, Date stopDate) {
        collectionPlan = refreshOrRetrieve(collectionPlan);
        policyLevel = dunningPolicyLevelService.refreshOrRetrieve(policyLevel);
        stopReason = dunningStopReasonsService.refreshOrRetrieve(stopReason);
        collectionPlan.setCollectionPlanStopReason(stopReason);
        collectionPlan.setCollectionPlanCloseDate(stopDate);
        DunningCollectionPlan renewedCollectionPlan = from(collectionPlan, policyLevel);
        create(renewedCollectionPlan);
        update(collectionPlan);
        return renewedCollectionPlan;
    }

    private DunningCollectionPlan from(DunningCollectionPlan closedCollectionPlan, DunningPolicyLevel policyLevel) {
        DunningCollectionPlan renewedCollectionPlan = new DunningCollectionPlan();
        /*renewedCollectionPlan.setCollectionPlanDaysOpen(closedCollectionPlan.getCollectionPlanDaysOpen());
        renewedCollectionPlan.setCollectionPlanAgedBalance(closedCollectionPlan.getCollectionPlanAgedBalance());
        renewedCollectionPlan.setCollectionPlanDaysOpen(closedCollectionPlan.getCollectionPlanDaysOpen());
        ofNullable(closedCollectionPlan.getCollectionPlanAssignedAgent())
                .ifPresent(assignedAgent -> renewedCollectionPlan.setCollectionPlanAssignedAgent(assignedAgent));
        ofNullable(closedCollectionPlan.getCollectionPlanBillingAccount())
                .ifPresent(billingAccount -> renewedCollectionPlan.setCollectionPlanBillingAccount(billingAccount));
        renewedCollectionPlan.setCollectionPlanDisputedBalance(closedCollectionPlan.getCollectionPlanDisputedBalance());
        ofNullable(closedCollectionPlan.getCollectionPlanPaymentMethod())
                .ifPresent(paymentMethod -> renewedCollectionPlan.setCollectionPlanPaymentMethod(paymentMethod));
        renewedCollectionPlan.setCollectionPlanDueBalance(closedCollectionPlan.getCollectionPlanDueBalance());
        renewedCollectionPlan.setCollectionPlanLastUpdate(closedCollectionPlan.getCollectionPlanLastUpdate());
        renewedCollectionPlan.setCollectionPlanPausedUntilDate(closedCollectionPlan.getCollectionPlanPausedUntilDate());
        ofNullable(renewedCollectionPlan.getCollectionPlanPauseReason())
                .ifPresent(pauseReason -> renewedCollectionPlan.setCollectionPlanPauseReason(pauseReason));
        renewedCollectionPlan.setCollectionPlanPausedUntilDate(closedCollectionPlan.getCollectionPlanPausedUntilDate());
        renewedCollectionPlan.setCollectionPlanCurrentDunningLevelSequence(policyLevel.getSequence());
        renewedCollectionPlan.setCollectionPlanCurrentDunningLevel(policyLevel.getDunningLevel());
        renewedCollectionPlan.setCollectionPlanStatus(policyLevel.getCollectionPlanStatus());
        renewedCollectionPlan.setCollectionPlanRelatedPolicy(policyLevel.getDunningPolicy());
        renewedCollectionPlan.setCollectionPlanStartDate(addDaysToDate(closedCollectionPlan.getCollectionPlanStartDate(),
                policyLevel.getDunningLevel().getDaysOverdue()));*/
        return renewedCollectionPlan;
    }
}