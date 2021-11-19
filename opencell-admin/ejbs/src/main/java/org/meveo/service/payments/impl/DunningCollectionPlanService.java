package org.meveo.service.payments.impl;

import static org.meveo.model.dunning.DunningLevelInstanceStatusEnum.DONE;
import static org.meveo.model.dunning.DunningLevelInstanceStatusEnum.TO_BE_DONE;
import static org.meveo.model.shared.DateUtils.addDaysToDate;

import org.meveo.model.billing.Invoice;
import org.meveo.model.dunning.*;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.billing.impl.InvoiceService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Stateless
public class DunningCollectionPlanService extends PersistenceService<DunningCollectionPlan> {
    
    @Inject
    private DunningStopReasonsService dunningStopReasonsService;

    @Inject
    private DunningLevelInstanceService levelInstanceService;

    @Inject
    private DunningActionInstanceService actionInstanceService;

    @Inject
    private DunningCollectionPlanStatusService dunningCollectionPlanStatusService;

    @Inject
    private InvoiceService invoiceService;

    public DunningCollectionPlan switchCollectionPlan(DunningCollectionPlan oldCollectionPlan, DunningPolicy policy, DunningPolicyLevel selectedPolicyLevel) {
        DunningStopReason stopReason = dunningStopReasonsService.findByStopReason("Changement de politique de recouvrement");
        oldCollectionPlan.setCollectionPlanStopReason(stopReason);
        oldCollectionPlan.setCollectionPlanCloseDate(new Date());

        DunningCollectionPlanStatus collectionPlanStatusActif = dunningCollectionPlanStatusService.findByStatus("Actif");
        DunningCollectionPlan newCollectionPlan = new DunningCollectionPlan();
        newCollectionPlan.setCollectionPlanRelatedPolicy(policy);
        newCollectionPlan.setCollectionPlanBillingAccount(oldCollectionPlan.getCollectionPlanBillingAccount());
        newCollectionPlan.setCollectionPlanRelatedInvoice(oldCollectionPlan.getCollectionPlanRelatedInvoice());
        newCollectionPlan.setCollectionPlanCurrentDunningLevelSequence(selectedPolicyLevel.getSequence());
        newCollectionPlan.setTotalDunningLevels(policy.getTotalDunningLevels());
        newCollectionPlan.setCollectionPlanStartDate(oldCollectionPlan.getCollectionPlanStartDate());
        newCollectionPlan.setCollectionPlanStatus(collectionPlanStatusActif);
        newCollectionPlan.setCollectionPlanBalance(oldCollectionPlan.getCollectionPlanBalance());
        create(newCollectionPlan);
        if (policy.getDunningLevels() != null && !policy.getDunningLevels().isEmpty()) {
            List<DunningLevelInstance> levelInstances = new ArrayList<>();
            for (DunningPolicyLevel policyLevel : policy.getDunningLevels()) {
                DunningLevelInstance levelInstance = null;
                if (policyLevel.getSequence() <= selectedPolicyLevel.getSequence()) {
                    levelInstance = createLevelInstance(newCollectionPlan, collectionPlanStatusActif, null, policyLevel, DONE);
                } else {
                    levelInstance = createLevelInstance(newCollectionPlan, collectionPlanStatusActif, null, policyLevel, TO_BE_DONE);
                }
                levelInstances.add(levelInstance);
            }
            newCollectionPlan.setDunningLevelInstances(levelInstances);
        }

        create(newCollectionPlan);
        update(oldCollectionPlan);
        return newCollectionPlan;
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
        invoice = invoiceService.refreshOrRetrieve(invoice);
        DunningCollectionPlan collectionPlan = new DunningCollectionPlan();
        collectionPlan.setCollectionPlanRelatedPolicy(policy);
        collectionPlan.setCollectionPlanBillingAccount(invoice.getBillingAccount());
        collectionPlan.setCollectionPlanRelatedInvoice(invoice);
        collectionPlan.setCollectionPlanCurrentDunningLevelSequence(1);
        collectionPlan.setTotalDunningLevels(policy.getTotalDunningLevels());
        collectionPlan.setCollectionPlanStartDate(addDaysToDate(invoice.getDueDate(), dayOverDue));
        collectionPlan.setCollectionPlanStatus(collectionPlanStatus);
        Optional.ofNullable(invoice.getRecordedInvoice())
                .ifPresent(recordedInvoice ->
                        collectionPlan.setCollectionPlanBalance(recordedInvoice.getUnMatchingAmount()));
        create(collectionPlan);
        if(policy.getDunningLevels() != null && !policy.getDunningLevels().isEmpty()) {
            collectionPlan.setDunningLevelInstances(createLevelInstances(policy, collectionPlan,
                    collectionPlanStatus, dayOverDue));
        }
        collectionPlan.setCollectionPlanID("C"+collectionPlan.getId());
        return update(collectionPlan);
    }

    private List<DunningLevelInstance> createLevelInstances(DunningPolicy policy,
                                                            DunningCollectionPlan collectionPlan,
                                                            DunningCollectionPlanStatus collectionPlanStatus,
                                                            Integer dayOverDue) {
        List<DunningLevelInstance> levelInstances = new ArrayList<>();
        for (DunningPolicyLevel policyLevel : policy.getDunningLevels()) {
            DunningLevelInstance levelInstance;
            if (policyLevel.getSequence() == 1) {
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
        levelInstance.setSequence(policyLevel.getSequence());
        if (dayOverDue != null) {            
            levelInstance.setDaysOverdue(dayOverDue);
        }
        else {
            levelInstance.setDaysOverdue(policyLevel.getDunningLevel().getDaysOverdue());
        }
        levelInstanceService.create(levelInstance);
        if(policyLevel.getDunningLevel().getDunningActions() != null
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
            actionInstance.setCode(action.getCode());
            actionInstance.setDescription(action.getDescription());
            actionInstanceService.create(actionInstance);
            actionInstances.add(actionInstance);
        }
        return actionInstances;
    }
}