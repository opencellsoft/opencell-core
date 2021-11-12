package org.meveo.service.payments.impl;

import static org.meveo.model.dunning.DunningLevelInstanceStatusEnum.DONE;
import static org.meveo.model.dunning.DunningLevelInstanceStatusEnum.TO_BE_DONE;
import static org.meveo.model.shared.DateUtils.addDaysToDate;

import org.meveo.model.billing.Invoice;
import org.meveo.model.dunning.*;
import org.meveo.service.base.PersistenceService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class DunningCollectionPlanService extends PersistenceService<DunningCollectionPlan> {

    @Inject
    private DunningPolicyLevelService dunningPolicyLevelService;

    @Inject
    private DunningStopReasonsService dunningStopReasonsService;

    @Inject
    private DunningLevelInstanceService levelInstanceService;

    @Inject
    private DunningActionInstanceService actionInstanceService;

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

    public List<DunningCollectionPlan> findByInvoiceId(long invoiceID) {
        return getEntityManager()
                    .createNamedQuery("DunningCollectionPlan.findByInvoiceId", DunningCollectionPlan.class)
                    .setParameter("invoiceID", invoiceID)
                    .getResultList();
    }

    /**
     * Create a collection plan from invoice and dunning policy
     * @param invoice
     * @param policy : dunningPolicy
     * @param dayOverDue integer indicating day over due
     * @param collectionPlanStatus collection plan status object
     * @return created DunningCollectionPlan
     */
    public DunningCollectionPlan createCollectionPlanFrom(Invoice invoice, DunningPolicy policy,
                                                          Integer dayOverDue, DunningCollectionPlanStatus collectionPlanStatus) {
        DunningCollectionPlan collectionPlan = new DunningCollectionPlan();
        collectionPlan.setCollectionPlanRelatedPolicy(policy);
        collectionPlan.setCollectionPlanBillingAccount(invoice.getBillingAccount());
        collectionPlan.setCollectionPlanRelatedInvoice(invoice);
        collectionPlan.setCollectionPlanCurrentDunningLevelSequence(1);
        collectionPlan.setTotalDunningLevels(policy.getTotalDunningLevels());
        collectionPlan.setCollectionPlanStartDate(addDaysToDate(invoice.getDueDate(), dayOverDue));
        collectionPlan.setCollectionPlanStatus(collectionPlanStatus);
        collectionPlan.setCollectionPlanBalance(invoice.getRecordedInvoice().getUnMatchingAmount());
        create(collectionPlan);
        if(policy.getDunningPolicyRules() != null && !policy.getDunningPolicyRules().isEmpty()) {
            collectionPlan.setDunningLevelInstances(createLevelInstances(policy, collectionPlan,
                    collectionPlanStatus, dayOverDue));
        }
        return update(collectionPlan);
    }

    private List<DunningLevelInstance> createLevelInstances(DunningPolicy policy, DunningCollectionPlan collectionPlan,
                                                            DunningCollectionPlanStatus collectionPlanStatus, Integer dayOverDue) {
        List<DunningLevelInstance> levelInstances = new ArrayList<>();
        for (DunningPolicyLevel policyLevel : policy.getDunningLevels()) {
            DunningLevelInstance levelInstance;
            if (policyLevel.getDunningLevel().isReminder()) {
                levelInstance = levelInstanceService.findByPolicyLevelId(policyLevel.getId());
                if (levelInstance == null) {
                    levelInstance = createLevelInstance(collectionPlan, collectionPlanStatus, dayOverDue, policyLevel, DONE);
                }
            } else {
                levelInstance = createLevelInstance(collectionPlan, collectionPlanStatus, dayOverDue, policyLevel, TO_BE_DONE);
            }
            levelInstances.add(levelInstance);
        }
        return levelInstances;
    }

    private DunningLevelInstance createLevelInstance(DunningCollectionPlan collectionPlan,
                                                  DunningCollectionPlanStatus collectionPlanStatus,
                                                  Integer dayOverDue, DunningPolicyLevel policyLevel,
                                                  DunningLevelInstanceStatusEnum status) {
        DunningLevelInstance levelInstance = new DunningLevelInstance();
        levelInstance.setCollectionPlan(collectionPlan);
        levelInstance.setCollectionPlanStatus(collectionPlanStatus);
        levelInstance.setPolicyLevel(policyLevel);
        levelInstance.setLevelStatus(status);
        levelInstance.setSequence(1);
        levelInstance.setDaysOverdue(dayOverDue);
        levelInstanceService.create(levelInstance);
        if(policyLevel.getDunningLevel().getDunningActions() == null
                && !policyLevel.getDunningLevel().getDunningActions().isEmpty()) {
            levelInstance.setActions(createActions(policyLevel, collectionPlan, levelInstance));
            levelInstanceService.update(levelInstance);
        }
        return levelInstance;
    }

    private List<DunningActionInstance> createActions(DunningPolicyLevel policyLevel,
                                                      DunningCollectionPlan collectionPlan, DunningLevelInstance levelInstance) {
        List<DunningActionInstance> actionInstances = new ArrayList<>();
        for (DunningAction action : policyLevel.getDunningLevel().getDunningActions()) {
            DunningActionInstance actionInstance = new DunningActionInstance();
            actionInstance.setActionType(action.getActionType());
            actionInstance.setActionMode(action.getActionMode());
            actionInstance.setActionOwner(action.getAssignedTo());
            actionInstance.setActionStatus(TO_BE_DONE);
            actionInstance.setCollectionPlan(collectionPlan);
            actionInstance.setDunningLevelInstance(levelInstance);
            actionInstanceService.create(actionInstance);
            actionInstances.add(actionInstance);
        }
        return actionInstances;
    }
}