package org.meveo.service.payments.impl;

import static java.lang.System.currentTimeMillis;
import static org.meveo.model.dunning.DunningLevelInstanceStatusEnum.DONE;
import static org.meveo.model.dunning.DunningLevelInstanceStatusEnum.TO_BE_DONE;
import static org.meveo.model.shared.DateUtils.addDaysToDate;

import java.util.*;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.billing.Invoice;
import org.meveo.model.dunning.*;
import org.meveo.model.payments.DunningCollectionPlanStatusEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.billing.impl.InvoiceService;

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
    private DunningCollectionPlanService dunningCollectionPlanService;

    @Inject
    private InvoiceService invoiceService;

    @Inject
    private DunningPolicyService policyService;

    private static final String STOP_REASON = "Changement de politique de recouvrement";

    public DunningCollectionPlan switchCollectionPlan(DunningCollectionPlan oldCollectionPlan, DunningPolicy policy, DunningPolicyLevel selectedPolicyLevel) {
        DunningStopReason stopReason = dunningStopReasonsService.findByStopReason(STOP_REASON);
        policy = policyService.refreshOrRetrieve(policy);
        oldCollectionPlan.setStopReason(stopReason);
        oldCollectionPlan.setCloseDate(new Date());

        DunningCollectionPlanStatus collectionPlanStatusActif = dunningCollectionPlanStatusService.findByStatus(DunningCollectionPlanStatusEnum.ACTIVE);
        DunningCollectionPlan newCollectionPlan = new DunningCollectionPlan();
        newCollectionPlan.setRelatedPolicy(policy);
        newCollectionPlan.setBillingAccount(oldCollectionPlan.getBillingAccount());
        newCollectionPlan.setRelatedInvoice(oldCollectionPlan.getRelatedInvoice());
        newCollectionPlan.setCurrentDunningLevelSequence(selectedPolicyLevel.getSequence());
        newCollectionPlan.setTotalDunningLevels(policy.getTotalDunningLevels());
        newCollectionPlan.setStartDate(oldCollectionPlan.getStartDate());
        newCollectionPlan.setStatus(collectionPlanStatusActif);
        newCollectionPlan.setBalance(oldCollectionPlan.getBalance());
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
        collectionPlan.setRelatedPolicy(policy);
        collectionPlan.setBillingAccount(invoice.getBillingAccount());
        collectionPlan.setRelatedInvoice(invoice);
        collectionPlan.setCurrentDunningLevelSequence(1);
        collectionPlan.setTotalDunningLevels(policy.getTotalDunningLevels());
        collectionPlan.setStartDate(addDaysToDate(invoice.getDueDate(), dayOverDue));
        collectionPlan.setStatus(collectionPlanStatus);
        if(policy.getDunningLevels() != null && !policy.getDunningLevels().isEmpty()) {
            DunningLevel firstLevel = findLevelBySequence(policy.getDunningLevels(), 1);
            DunningLevel reminderLevel = findLevelBySequence(policy.getDunningLevels(), 0);
            if(firstLevel != null
                    && firstLevel.getDunningActions() != null && !firstLevel.getDunningActions().isEmpty()) {
                int dOverDue = Optional.ofNullable(firstLevel.getDaysOverdue()).orElse(0);
                collectionPlan.setNextAction(firstLevel.getDunningActions().get(0).getCode());
                collectionPlan.setNextActionDate(addDaysToDate(collectionPlan.getStartDate(), dOverDue));
            }
            if (reminderLevel != null
                    && reminderLevel.getDunningActions() != null && !reminderLevel.getDunningActions().isEmpty()) {
                int dOverDue = Optional.ofNullable(reminderLevel.getDaysOverdue()).orElse(0);
                collectionPlan.setLastAction(reminderLevel.getDunningActions().get(0).getCode());
                collectionPlan.setLastActionDate(addDaysToDate(collectionPlan.getStartDate(), dOverDue));
            }
        }
        Optional.ofNullable(invoice.getRecordedInvoice())
                .ifPresent(recordedInvoice ->
                        collectionPlan.setBalance(recordedInvoice.getUnMatchingAmount()));
        create(collectionPlan);
        if(policy.getDunningLevels() != null && !policy.getDunningLevels().isEmpty()) {
            collectionPlan.setDunningLevelInstances(createLevelInstances(policy, collectionPlan,
                    collectionPlanStatus, dayOverDue));
        }
        collectionPlan.setCollectionPlanNumber("C"+collectionPlan.getId());
        return update(collectionPlan);
    }

    private DunningLevel findLevelBySequence(List<DunningPolicyLevel> policyLevels, int sequence) {
        return policyLevels.stream()
                        .filter(policyLevel -> policyLevel.getSequence() == sequence)
                        .map(DunningPolicyLevel::getDunningLevel)
                        .findFirst()
                        .orElse(null);
    }

    private List<DunningLevelInstance> createLevelInstances(DunningPolicy policy,
                                                            DunningCollectionPlan collectionPlan,
                                                            DunningCollectionPlanStatus collectionPlanStatus,
                                                            Integer dayOverDue) {
        List<DunningLevelInstance> levelInstances = new ArrayList<>();
        for (DunningPolicyLevel policyLevel : policy.getDunningLevels()) {
            DunningLevelInstance levelInstance;
            if (policyLevel.getDunningLevel().isReminder()) {
                levelInstance = levelInstanceService.findByLevelId(policyLevel.getDunningLevel().getId());
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
        levelInstance.setLevelStatus(status);
        levelInstance.setSequence(policyLevel.getSequence());
        levelInstance.setDunningLevel(policyLevel.getDunningLevel());
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
            actionInstance.setDunningAction(action);
            actionInstance.setActionType(action.getActionType());
            actionInstance.setActionMode(action.getActionMode());
            actionInstance.setActionOwner(action.getAssignedTo());
            actionInstance.setActionStatus(DunningActionInstanceStatusEnum.TO_BE_DONE);
            actionInstance.setCollectionPlan(collectionPlan);
            actionInstance.setDunningLevelInstance(levelInstance);
            actionInstance.setCode(action.getCode() + "_" + currentTimeMillis());
            actionInstance.setDescription(action.getDescription());
            actionInstanceService.create(actionInstance);
            actionInstances.add(actionInstance);
        }
        return actionInstances;
    }
    
    public DunningCollectionPlan pauseCollectionPlan(boolean forcePause, Date pauseUntil,
			DunningCollectionPlan collectionPlanToPause, DunningPauseReason dunningPauseReason) {
    	collectionPlanToPause = dunningCollectionPlanService.refreshOrRetrieve(collectionPlanToPause);
		collectionPlanToPause = refreshLevelInstances(collectionPlanToPause);
		DunningCollectionPlanStatus dunningCollectionPlanStatus = dunningCollectionPlanStatusService.refreshOrRetrieve(collectionPlanToPause.getStatus());
		if(!dunningCollectionPlanStatus.getStatus().equals(DunningCollectionPlanStatusEnum.ACTIVE)) {
			throw new BusinessApiException("Collection Plan with id "+collectionPlanToPause.getId()+" cannot be paused, the collection plan status is not active");
		}

		if(dunningCollectionPlanStatus.getStatus().equals(DunningCollectionPlanStatusEnum.STOPPED)) {
			throw new BusinessApiException("Collection Plan with id "+collectionPlanToPause.getId()+" cannot be paused, the collection plan status is not stoped");
		}

		if(!forcePause) {
			Optional<DunningLevelInstance> dunningLevelInstance = collectionPlanToPause.getDunningLevelInstances()
					.stream().max(Comparator.comparing(DunningLevelInstance::getId));
			if(dunningLevelInstance.isPresent() && pauseUntil != null && pauseUntil.after(DateUtils.addDaysToDate(collectionPlanToPause.getStartDate(), dunningLevelInstance.get().getDaysOverdue()))) {
				throw new BusinessApiException("Collection Plan with id "+collectionPlanToPause.getId()+" cannot be paused, the pause until date is after the planned trigger date of the last level");
			}
		}
		
		DunningCollectionPlanStatus collectionPlanStatus = dunningCollectionPlanStatusService.findByStatus(DunningCollectionPlanStatusEnum.PAUSED);
		collectionPlanToPause.setStatus(collectionPlanStatus);
		collectionPlanToPause.setPausedUntilDate(pauseUntil);
		collectionPlanToPause.setPauseReason(dunningPauseReason);
		collectionPlanToPause.addPauseDuration((int)DateUtils.daysBetween(new Date(),collectionPlanToPause.getPausedUntilDate()));
		update(collectionPlanToPause);
		return collectionPlanToPause; 
	}
	
	public DunningCollectionPlan stopCollectionPlan(DunningCollectionPlan collectionPlanToStop, DunningStopReason dunningStopReason) {
		collectionPlanToStop = dunningCollectionPlanService.refreshOrRetrieve(collectionPlanToStop);
		collectionPlanToStop = refreshLevelInstances(collectionPlanToStop);

		DunningCollectionPlanStatus dunningCollectionPlanStatus = dunningCollectionPlanStatusService.refreshOrRetrieve(collectionPlanToStop.getStatus());

		if(dunningCollectionPlanStatus.getStatus().equals(DunningCollectionPlanStatusEnum.SUCCESS)) {
			throw new BusinessApiException("Collection Plan with id "+collectionPlanToStop.getId()+" cannot be stoped, the collection plan status is success");
		}
		if(dunningCollectionPlanStatus.getStatus().equals(DunningCollectionPlanStatusEnum.FAILED)) {
			throw new BusinessApiException("Collection Plan with id "+collectionPlanToStop.getId()+" cannot be stoped, the collection plan status is failed");
		}
		
		DunningCollectionPlanStatus collectionPlanStatus = dunningCollectionPlanStatusService.findByStatus(DunningCollectionPlanStatusEnum.STOPPED);
		collectionPlanToStop.setStatus(collectionPlanStatus);
		collectionPlanToStop.setCloseDate(new Date());
		collectionPlanToStop.setStopReason(dunningStopReason);
		update(collectionPlanToStop);
		return collectionPlanToStop; 
	}

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public DunningCollectionPlan resumeCollectionPlan(DunningCollectionPlan collectionPlanToResume) {
		return resumeCollectionPlan(collectionPlanToResume, true);
	}
	
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public DunningCollectionPlan resumeCollectionPlan(DunningCollectionPlan collectionPlanToResume, boolean validate) {
    	collectionPlanToResume = retrieveIfNotManaged(collectionPlanToResume);
    	collectionPlanToResume = refreshLevelInstances(collectionPlanToResume);
    	DunningCollectionPlanStatus dunningCollectionPlanStatus = dunningCollectionPlanStatusService.refreshOrRetrieve(collectionPlanToResume.getStatus());
		if(validate) {
			if(!dunningCollectionPlanStatus.getStatus().equals(DunningCollectionPlanStatusEnum.PAUSED)) {
				throw new BusinessApiException("Collection Plan with id "+collectionPlanToResume.getId()+" cannot be resumed, the collection plan is not paused");
			}
			if(collectionPlanToResume.getPausedUntilDate() != null && collectionPlanToResume.getPausedUntilDate().before(new Date())) {
				throw new BusinessApiException("Collection Plan with id "+collectionPlanToResume.getId()+" cannot be resumed, the field pause until is in the past");
			}
		}
		
		Optional<DunningLevelInstance> dunningLevelInstance = collectionPlanToResume.getDunningLevelInstances()
				.stream().max(Comparator.comparing(DunningLevelInstance::getId));
		if(dunningLevelInstance.isEmpty()) {
			throw new BusinessApiException("No dunning level instances found for the collection plan with id "+collectionPlanToResume.getId());
		}
		DunningCollectionPlanStatus collectionPlanStatus=null;
		if(collectionPlanToResume.getPausedUntilDate() != null && collectionPlanToResume.getPausedUntilDate().after(DateUtils.addDaysToDate(collectionPlanToResume.getStartDate(), dunningLevelInstance.get().getDaysOverdue()))) {
			collectionPlanStatus = dunningCollectionPlanStatusService.findByStatus(DunningCollectionPlanStatusEnum.FAILED);
		} else {
			collectionPlanStatus = dunningCollectionPlanStatusService.findByStatus(DunningCollectionPlanStatusEnum.ACTIVE);
			collectionPlanToResume.setPauseReason(null);
		}
		collectionPlanToResume.setStatus(collectionPlanStatus);
		collectionPlanToResume.addPauseDuration((int)DateUtils.daysBetween(collectionPlanToResume.getPausedUntilDate(), new Date()));
		update(collectionPlanToResume);
		return collectionPlanToResume;
	}
	
    @Override
    public void remove(DunningCollectionPlan entity) throws BusinessException {
    	super.remove(entity);
    }

	public List<DunningCollectionPlan> findDunningCollectionPlansToResume() {
        return getEntityManager()
                .createNamedQuery("DunningCollectionPlan.DCPtoResume", DunningCollectionPlan.class)
                .setParameter("resumeDate", new Date())
                .getResultList();
	}
	
	private DunningCollectionPlan refreshLevelInstances(DunningCollectionPlan dunningCollectionPlan) {
		List<DunningLevelInstance> dunningLevelInstances = new ArrayList<DunningLevelInstance>();
		for (DunningLevelInstance levelInstance : dunningCollectionPlan.getDunningLevelInstances()) {
		    levelInstance = levelInstanceService.findById(levelInstance.getId());
		    dunningLevelInstances.add(levelInstance);
		}
		
		dunningCollectionPlan.setDunningLevelInstances(dunningLevelInstances);
		return dunningCollectionPlan;
	}

	public List<Long> getActiveCollectionPlansIds() {
        return getEntityManager()
                .createNamedQuery("DunningCollectionPlan.activeCollectionPlansIds", Long.class)
                .getResultList();
    }
}