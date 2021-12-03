package org.meveo.service.payments.impl;

import static org.meveo.model.dunning.DunningLevelInstanceStatusEnum.DONE;
import static org.meveo.model.dunning.DunningLevelInstanceStatusEnum.TO_BE_DONE;
import static org.meveo.model.shared.DateUtils.addDaysToDate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.audit.logging.AuditLog;
import org.meveo.model.billing.Invoice;
import org.meveo.model.dunning.DunningAction;
import org.meveo.model.dunning.DunningActionInstance;
import org.meveo.model.dunning.DunningActionInstanceStatusEnum;
import org.meveo.model.dunning.DunningCollectionPlan;
import org.meveo.model.dunning.DunningCollectionPlanStatus;
import org.meveo.model.dunning.DunningLevelInstance;
import org.meveo.model.dunning.DunningLevelInstanceStatusEnum;
import org.meveo.model.dunning.DunningPauseReason;
import org.meveo.model.dunning.DunningPolicy;
import org.meveo.model.dunning.DunningPolicyLevel;
import org.meveo.model.dunning.DunningStopReason;
import org.meveo.model.payments.DunningCollectionPlanStatusEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.audit.logging.AuditLogService;
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
    private AuditLogService auditLogService;

    public DunningCollectionPlan switchCollectionPlan(DunningCollectionPlan oldCollectionPlan, DunningPolicy policy, DunningPolicyLevel selectedPolicyLevel) {
        DunningStopReason stopReason = dunningStopReasonsService.findByStopReason("Changement de politique de recouvrement");
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
        trackOperation("SWITCH", new Date(), oldCollectionPlan);
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
            actionInstance.setCode(action.getCode());
            actionInstance.setActionType(action.getActionType());
            actionInstance.setActionMode(action.getActionMode());
            actionInstance.setActionOwner(action.getAssignedTo());
            actionInstance.setActionStatus(DunningActionInstanceStatusEnum.TO_BE_DONE);
            actionInstance.setCollectionPlan(collectionPlan);
            actionInstance.setDunningLevelInstance(levelInstance);
            actionInstance.setCode(action.getCode());
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
		trackOperation("PAUSE", new Date(), collectionPlanToPause);
		return collectionPlanToPause; 
	}
	
	public DunningCollectionPlan stopCollectionPlan(DunningCollectionPlan collectionPlanToStop, DunningStopReason dunningStopReason) {
		collectionPlanToStop = dunningCollectionPlanService.refreshOrRetrieve(collectionPlanToStop);
		collectionPlanToStop = refreshLevelInstances(collectionPlanToStop);
		DunningCollectionPlanStatus collectionPlanStatus = dunningCollectionPlanStatusService.findByStatus(DunningCollectionPlanStatusEnum.STOPPED);
		collectionPlanToStop.setStatus(collectionPlanStatus);
		collectionPlanToStop.setCloseDate(new Date());
		collectionPlanToStop.setStopReason(dunningStopReason);
		update(collectionPlanToStop);
		trackOperation("STOP", new Date(), collectionPlanToStop);
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
		trackOperation("RESUME", new Date(), collectionPlanToResume);
		return collectionPlanToResume;
	}
	
    @Override
    public void remove(DunningCollectionPlan entity) throws BusinessException {
    	trackOperation("DELETE", new Date(), entity);
    	super.remove(entity);
    }
    
    public AuditLog trackOperation(String operationType, Date operationDate, DunningCollectionPlan dunningCollectionPlan) {
        AuditLog auditLog = new AuditLog();
        final DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy 'at' HH'h'mm");
        String userName;
        
        if (currentUser.getFullNameOrUserName() != null && currentUser.getFullNameOrUserName().length() > 0) {
        	userName = currentUser.getFullNameOrUserName();
        } else if (currentUser.getEmail() != null && currentUser.getEmail().length() > 0) {
        	userName = currentUser.getEmail();
        } else {
        	userName = currentUser.getUserName();
        }
        
        auditLog.setEntity(DunningCollectionPlan.class.getSimpleName());
        auditLog.setCreated(operationDate);
        auditLog.setActor(currentUser.getUserName());
        auditLog.setAction(operationType);
        StringBuilder parameters = new StringBuilder()
                .append(formatter.format(operationDate)).append(" - ")
                .append(userName).append(" - ")
                .append(" apply ")
                .append(operationType)
                .append(" to collection Plan id=")
                .append(dunningCollectionPlan.getId());
        auditLog.setParameters(parameters.toString());
        auditLog.setOrigin("DunningCollectionPlan: " + dunningCollectionPlan.getId());
        auditLogService.create(auditLog);
        return auditLog;
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
}