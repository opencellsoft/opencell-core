package org.meveo.service.payments.impl;

import static java.lang.Math.abs;
import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.asList;
import static org.meveo.model.dunning.DunningLevelInstanceStatusEnum.DONE;
import static org.meveo.model.dunning.DunningLevelInstanceStatusEnum.TO_BE_DONE;
import static org.meveo.model.shared.DateUtils.addDaysToDate;
import static org.meveo.model.shared.DateUtils.daysBetween;
import static org.meveo.service.base.ValueExpressionWrapper.evaluateExpression;

import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Invoice;
import org.meveo.model.communication.email.EmailTemplate;
import org.meveo.model.dunning.DunningAction;
import org.meveo.model.dunning.DunningActionInstance;
import org.meveo.model.dunning.DunningActionInstanceStatusEnum;
import org.meveo.model.dunning.DunningCollectionPlan;
import org.meveo.model.dunning.DunningCollectionPlanStatus;
import org.meveo.model.dunning.DunningLevel;
import org.meveo.model.dunning.DunningLevelInstance;
import org.meveo.model.dunning.DunningLevelInstanceStatusEnum;
import org.meveo.model.dunning.DunningPauseReason;
import org.meveo.model.dunning.DunningPolicy;
import org.meveo.model.dunning.DunningPolicyLevel;
import org.meveo.model.dunning.DunningStopReason;
import org.meveo.model.payments.ActionModeEnum;
import org.meveo.model.payments.DunningCollectionPlanStatusEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.communication.impl.EmailSender;
import org.meveo.service.communication.impl.EmailTemplateService;
import org.meveo.service.communication.impl.InternationalSettingsService;

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

    @Inject
    private EmailSender emailSender;

    @Inject
    private EmailTemplateService emailTemplateService;

    @Inject
    private InternationalSettingsService internationalSettingsService;

    private static final String STOP_REASON = "Changement de politique de recouvrement";

    public DunningCollectionPlan findByPolicy(DunningPolicy dunningPolicy) {
        List<DunningCollectionPlan> result = getEntityManager()
                                                .createNamedQuery("DunningCollectionPlan.findByPolicy", entityClass)
                                                .setParameter("dunningPolicy", dunningPolicy)
                                                .getResultList();
        return result != null && !result.isEmpty() ? result.get(0) : null;
    }

    public DunningCollectionPlan switchCollectionPlan(DunningCollectionPlan oldCollectionPlan, DunningPolicy policy, DunningPolicyLevel selectedPolicyLevel) {
        DunningStopReason stopReason = dunningStopReasonsService.findByStopReason(STOP_REASON);
        policy = policyService.refreshOrRetrieve(policy);
        DunningCollectionPlanStatus collectionPlanStatusStop
        = dunningCollectionPlanStatusService.findByStatus(DunningCollectionPlanStatusEnum.STOPPED);

        oldCollectionPlan.setStopReason(stopReason);
        oldCollectionPlan.setCloseDate(new Date());
        oldCollectionPlan.setStatus(collectionPlanStatusStop);

        DunningCollectionPlanStatus collectionPlanStatusActif
                = dunningCollectionPlanStatusService.findByStatus(DunningCollectionPlanStatusEnum.ACTIVE);
        DunningCollectionPlan newCollectionPlan = new DunningCollectionPlan();
        newCollectionPlan.setRelatedPolicy(policy);
        newCollectionPlan.setBillingAccount(oldCollectionPlan.getBillingAccount());
        newCollectionPlan.setRelatedInvoice(oldCollectionPlan.getRelatedInvoice());
        newCollectionPlan.setCurrentDunningLevelSequence(selectedPolicyLevel.getSequence());
        newCollectionPlan.setTotalDunningLevels(policy.getTotalDunningLevels());
        newCollectionPlan.setStartDate(oldCollectionPlan.getStartDate());
        newCollectionPlan.setStatus(collectionPlanStatusActif);
        newCollectionPlan.setBalance(oldCollectionPlan.getBalance());
        newCollectionPlan.setInitialCollectionPlan(oldCollectionPlan);
        newCollectionPlan.setLastAction(oldCollectionPlan.getLastAction());
        newCollectionPlan.setLastActionDate(oldCollectionPlan.getLastActionDate());
        create(newCollectionPlan);
       
        if (policy.getDunningLevels() != null && !policy.getDunningLevels().isEmpty()) {
            List<DunningLevelInstance> levelInstances = new ArrayList<>();
            for (DunningPolicyLevel policyLevel : policy.getDunningLevels()) {
                DunningLevelInstance levelInstance;
                if (policyLevel.getSequence() < selectedPolicyLevel.getSequence()) {
                    levelInstance = createLevelInstance(newCollectionPlan,
                            collectionPlanStatusActif, null, policyLevel, DONE);
                } else {
                    levelInstance = createLevelInstance(newCollectionPlan,
                            collectionPlanStatusActif, null, policyLevel, TO_BE_DONE);
                    if (policyLevel.getSequence() == selectedPolicyLevel.getSequence()) {
                        DunningLevel nextLevel = findLevelBySequence(policy.getDunningLevels(), policyLevel.getSequence());
                        if(nextLevel != null
                                && nextLevel.getDunningActions() != null && !nextLevel.getDunningActions().isEmpty()) {
                            int dOverDue = Optional.ofNullable(nextLevel.getDaysOverdue()).orElse(0);
                            int i = 0;
                            while(i < nextLevel.getDunningActions().size() - 1) {
                            	if(nextLevel.getDunningActions().get(i).getActionMode().equals(ActionModeEnum.AUTOMATIC)) {
                            		break;
                            	}else {
                            		i++;
                            	}
                            }
                            newCollectionPlan.setNextAction((nextLevel.getDunningActions().get(i).getActionMode().equals(ActionModeEnum.AUTOMATIC))
                            		? nextLevel.getDunningActions().get(i).getCode()
                            				: nextLevel.getDunningActions().get(0).getCode());
                            newCollectionPlan.setNextActionDate(addDaysToDate(newCollectionPlan.getStartDate(), dOverDue));
                        }
                    }
                }
                levelInstances.add(levelInstance);
            }
            newCollectionPlan.setDunningLevelInstances(levelInstances);
            update(newCollectionPlan);
        }        
        
        newCollectionPlan.setCollectionPlanNumber("C" + newCollectionPlan.getId());
        update(newCollectionPlan);
        update(oldCollectionPlan);
        return newCollectionPlan;
    }

    public List<DunningCollectionPlan> findByInvoiceId(long invoiceID) {
        return getEntityManager()
                    .createNamedQuery("DunningCollectionPlan.findByInvoiceId", entityClass)
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
        collectionPlan.setDaysOpen(abs((int) daysBetween(new Date(), collectionPlan.getStartDate())));
        Optional<DunningPolicyLevel> policyLevel = policy.getDunningLevels().stream()
                .filter(dunningPolicyLevel -> dunningPolicyLevel.getSequence() == 0)
                .findFirst();
        if(policyLevel.isPresent()) {
            List<DunningAction> actions = policyLevel.get().getDunningLevel().getDunningActions();
            if(actions != null && !actions.isEmpty()) {
                collectionPlan.setNextAction(actions.get(0).getCode());
                collectionPlan.setNextActionDate(addDaysToDate(collectionPlan.getStartDate(),
                        collectionPlan.getDaysOpen()));
            }
        }
        Optional.ofNullable(invoice.getRecordedInvoice())
                .ifPresent(recordedInvoice ->
                        collectionPlan.setBalance(recordedInvoice.getUnMatchingAmount()));
        create(collectionPlan);
        invoice.setRelatedDunningCollectionPlan(collectionPlan);
        invoice.setDunningCollectionPlanTriggered(true);
        invoiceService.update(invoice);
        if(policy.getDunningLevels() != null && !policy.getDunningLevels().isEmpty()) {
            collectionPlan.setDunningLevelInstances(createLevelInstances(policy, collectionPlan,
                    collectionPlanStatus, dayOverDue));
        }
        collectionPlan.setCollectionPlanNumber("C" + collectionPlan.getId());
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
            levelInstance = createLevelInstance(collectionPlan, collectionPlanStatus, dayOverDue, policyLevel, TO_BE_DONE);
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
        levelInstance.setDaysOverdue(policyLevel.getDunningLevel().getDaysOverdue());
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
            if (levelInstance.getLevelStatus() == DunningLevelInstanceStatusEnum.DONE) {
            	actionInstance.setActionStatus(DunningActionInstanceStatusEnum.DONE);
            }else {
                actionInstance.setActionStatus(DunningActionInstanceStatusEnum.TO_BE_DONE);
            }
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
            LocalDate pauseDate = pauseUntil.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            LocalDate endDate = DateUtils.addDaysToDate(collectionPlanToPause.getStartDate(), dunningLevelInstance.get().getDaysOverdue())
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
			if(dunningLevelInstance.isPresent() && pauseUntil != null && pauseDate.isAfter(endDate)) {
                throw new BusinessApiException("Collection Plan cannot be paused, the pause until date is after the planned date for the last dunning level");
			}
		}
		
		DunningCollectionPlanStatus collectionPlanStatus = dunningCollectionPlanStatusService.findByStatus(DunningCollectionPlanStatusEnum.PAUSED);
		collectionPlanToPause.setStatus(collectionPlanStatus);
		collectionPlanToPause.setPausedUntilDate(pauseUntil);
		collectionPlanToPause.setPauseReason(dunningPauseReason);
		collectionPlanToPause.addPauseDuration((int) daysBetween(new Date(),collectionPlanToPause.getPausedUntilDate()));
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
		collectionPlanToStop.setDaysOpen((int) daysBetween(collectionPlanToStop.getCloseDate(), new Date()) + 1);
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
		collectionPlanToResume.addPauseDuration((int) daysBetween(collectionPlanToResume.getPausedUntilDate(), new Date()));
		update(collectionPlanToResume);
		return collectionPlanToResume;
	}
	
    @Override
    public void remove(DunningCollectionPlan entity) throws BusinessException {
    	super.remove(entity);
    }

	public List<DunningCollectionPlan> findDunningCollectionPlansToResume() {
        return getEntityManager()
                .createNamedQuery("DunningCollectionPlan.DCPtoResume", entityClass)
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

    public void sendNotification(String emailFrom, BillingAccount billingAccount, EmailTemplate emailTemplate,
                                 Map<Object, Object> params, List<File> attachments) {
        emailTemplate = emailTemplateService.refreshOrRetrieve(emailTemplate);
        if(emailTemplate != null) {
            String languageCode = billingAccount.getCustomerAccount().getTradingLanguage().getLanguage().getLanguageCode();
            String emailSubject = internationalSettingsService.resolveSubject(emailTemplate,languageCode);
            String emailContent = internationalSettingsService.resolveEmailContent(emailTemplate,languageCode);
            String htmlContent = internationalSettingsService.resolveHtmlContent(emailTemplate,languageCode);
            String emailTo = billingAccount.getContactInformation().getEmail();
            String subject = emailTemplate.getSubject() != null
                    ? evaluateExpression(emailSubject, params, String.class) : "";
            String content = emailTemplate.getTextContent() != null
                    ? evaluateExpression(emailContent, params, String.class) : "";
            String contentHtml = emailTemplate.getHtmlContent() != null
                    ? evaluateExpression(htmlContent, params, String.class) : "";
            emailSender.send(emailFrom, asList(emailFrom), asList(emailTo), null, null,
                    subject, content, contentHtml, attachments, null, false);
        } else {
            log.error("Email template not found");
        }
    }
    
    /**
     * Get Active or Paused DunningCollectionPlan by Dunning Settings id
     * @param id DunningSettings id
     * @return A list of {@link DunningCollectionPlan}
     */
    public List<DunningCollectionPlan> getActiveDunningCollectionPlan(Long id){
    	return getEntityManager()
                .createNamedQuery("DunningCollectionPlan.findActive", DunningCollectionPlan.class)
                .setParameter("id", id)
                .getResultList();
    }
    
    /**
     * Get Active or Paused DunningCollectionPlan by Dunning Settings id
     * @param id DunningSettings id
     * @return A list of {@link DunningCollectionPlan}
     */
    public List<DunningCollectionPlan> getPausedDunningCollectionPlan(Long id){
    	return getEntityManager()
                .createNamedQuery("DunningCollectionPlan.findPaused", DunningCollectionPlan.class)
                .setParameter("id", id)
                .getResultList();
    }
}