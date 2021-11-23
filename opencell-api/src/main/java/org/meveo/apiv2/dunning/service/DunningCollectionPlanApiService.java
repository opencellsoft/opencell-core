package org.meveo.apiv2.dunning.service;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import org.meveo.admin.util.ResourceBundle;
import org.meveo.apiv2.dunning.DunningCollectionPlanPause;
import org.meveo.apiv2.dunning.DunningCollectionPlanStop;
import org.meveo.apiv2.dunning.MassSwitchDunningCollectionPlan;
import org.meveo.apiv2.dunning.SwitchDunningCollectionPlan;
import org.meveo.apiv2.models.Resource;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.audit.logging.AuditLog;
import org.meveo.model.billing.Invoice;
import org.meveo.model.dunning.DunningCollectionPlan;
import org.meveo.model.dunning.DunningPauseReason;
import org.meveo.model.dunning.DunningPolicy;
import org.meveo.model.dunning.DunningPolicyLevel;
import org.meveo.model.dunning.DunningStopReason;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.audit.logging.AuditLogService;
import org.meveo.service.payments.impl.DunningCollectionPlanService;
import org.meveo.service.payments.impl.DunningPauseReasonsService;
import org.meveo.service.payments.impl.DunningPolicyLevelService;
import org.meveo.service.payments.impl.DunningPolicyService;
import org.meveo.service.payments.impl.DunningStopReasonsService;

public class DunningCollectionPlanApiService implements ApiService<DunningCollectionPlan> {

    @Inject
    private DunningCollectionPlanService dunningCollectionPlanService;

    @Inject
    private DunningPolicyService dunningPolicyService;

    @Inject
    private DunningPolicyLevelService dunningPolicyLevelService;

    @Inject
    private AuditLogService auditLogService;

    @Inject
    @CurrentUser
    private MeveoUser currentUser;

    @Inject
    private ResourceBundle resourceMessages;
    
    @Inject
	private DunningPauseReasonsService dunningPauseReasonService;
	@Inject
	private DunningStopReasonsService dunningStopReasonService;
	
	private static final String NO_DUNNING_FOUND = "No Dunning Plan collection found with id : %s";

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
        DunningCollectionPlan dunningCollectionPlan = dunningCollectionPlanService.findById(id);
        if (dunningCollectionPlan != null) {
            dunningPolicyService.remove(id);
            trackOperation("delete", new Date(), dunningCollectionPlan);
            return of(dunningCollectionPlan);
        } else {
            return empty();
        }
    }

    @Override
    public Optional<DunningCollectionPlan> findByCode(String code) {
        return empty();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Optional<DunningCollectionPlan> switchCollectionPlan(Long collectionPlanId, SwitchDunningCollectionPlan switchDunningCollectionPlan) {
        DunningCollectionPlan oldCollectionPlan = dunningCollectionPlanService.findById(collectionPlanId);
        if (oldCollectionPlan == null) {
            throw new NotFoundException("Dunning collection plan with id " + collectionPlanId + " does not exits");
        }
        DunningPolicy policy = dunningPolicyService.findById(switchDunningCollectionPlan.getDunningPolicy().getId());
        if (policy == null) {
            throw new NotFoundException("Policy with id " + switchDunningCollectionPlan.getDunningPolicy().getId() + " does not exits");
        }
        DunningPolicyLevel policyLevel = dunningPolicyLevelService.findById(switchDunningCollectionPlan.getPolicyLevel().getId());
        if (policyLevel == null) {
            throw new NotFoundException("Policy level with id " + switchDunningCollectionPlan.getPolicyLevel().getId() + " does not exits");
        }
        Optional<DunningCollectionPlan> optional = of(dunningCollectionPlanService.switchCollectionPlan(oldCollectionPlan, policy, policyLevel));
        trackOperation("switch", new Date(), oldCollectionPlan);
        return optional;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void massSwitchCollectionPlan(MassSwitchDunningCollectionPlan massSwitchDunningCollectionPlan) {

        DunningPolicy policy = dunningPolicyService.findById(massSwitchDunningCollectionPlan.getDunningPolicy().getId());
        if (policy == null) {
            throw new NotFoundException("Policy with id " + massSwitchDunningCollectionPlan.getDunningPolicy().getId() + " does not exits");
        }
        DunningPolicyLevel policyLevel = dunningPolicyLevelService.findById(massSwitchDunningCollectionPlan.getPolicyLevel().getId());
        if (policyLevel == null) {
            throw new NotFoundException("Policy level with id " + massSwitchDunningCollectionPlan.getPolicyLevel().getId() + " does not exits");
        }

        List<Resource> collectionPlanList = massSwitchDunningCollectionPlan.getCollectionPlanList();
        if (collectionPlanList != null) {
            for (Resource collectionPlanResource : collectionPlanList) {
                DunningCollectionPlan collectionPlan = dunningCollectionPlanService.findById(collectionPlanResource.getId());
                if (collectionPlan == null) {
                    throw new NotFoundException("Dunning collection plan with id " + collectionPlanResource.getId() + " does not exits");
                }
                dunningCollectionPlanService.switchCollectionPlan(collectionPlan, policy, policyLevel);
            }
        }
    }

    public AuditLog trackOperation(String operationType, Date operationDate, DunningCollectionPlan dunningCollectionPlan) {
        final DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy 'at' HH'h'mm");
        AuditLog auditLog = new AuditLog();
        auditLog.setEntity(DunningCollectionPlan.class.getSimpleName());
        auditLog.setCreated(operationDate);
        auditLog.setActor(currentUser.getUserName());
        auditLog.setAction(operationType);
        StringBuilder parameters = new StringBuilder()
                .append(formatter.format(operationDate)).append(" - ")
                .append(currentUser.getUserName()).append(" - ")
                .append(" apply ")
                .append(operationType)
                .append(" to collection Plan id=")
                .append(dunningCollectionPlan.getId());
        auditLog.setParameters(parameters.toString());
        auditLog.setOrigin("DunningCollectionPlan: " + dunningCollectionPlan.getId());
        auditLogService.create(auditLog);
        return auditLog;
    }

    public Optional<Map<String, List<Long>>> checkMassSwitch(DunningPolicy policy, List<DunningCollectionPlan> collectionPlans) {
        List<Invoice> eligibleInvoice = dunningPolicyService.findEligibleInvoicesForPolicy(policy);
        List<Long> canBeSwitched = new ArrayList<>();
        List<Long> canNotBeSwitched = new ArrayList<>();
        Map<String, List<Long>> massSwitchResult = new HashMap<>();
        if (eligibleInvoice != null && !eligibleInvoice.isEmpty()) {
            for (DunningCollectionPlan collectionPlan : collectionPlans) {
                collectionPlan = dunningCollectionPlanService.findById(collectionPlan.getId());
                if (collectionPlan == null) {
                    throw new NotFoundException("Collection plan does not exits");
                }
                for (Invoice invoice : eligibleInvoice) {
                    if (invoice.getId() == collectionPlan.getCollectionPlanRelatedInvoice().getId()) {
                        canBeSwitched.add(collectionPlan.getId());
                    } else {
                        canNotBeSwitched.add(collectionPlan.getId());
                    }
                }
            }
        } else {
            canNotBeSwitched.addAll(collectionPlans.stream().map(DunningCollectionPlan::getId).collect(toList()));
        }
        massSwitchResult.put("canBESwitched", canBeSwitched);
        massSwitchResult.put("canNotBESwitched", canNotBeSwitched);
        return of(massSwitchResult);
    }

    public List<DunningPolicy> availableDunningPolicies(Long collectionPlanID) {
        DunningCollectionPlan collectionPlan = dunningCollectionPlanService.findById(collectionPlanID);
        if (collectionPlan == null) {
            throw new NotFoundException(resourceMessages.getString("error.collectionPlan.availablePolicies.collectionPlanNotFound", collectionPlanID));
        }
        Invoice invoice = ofNullable(collectionPlan.getCollectionPlanRelatedInvoice())
                .orElseThrow(() -> new NotFoundException("No invoice found for collection plan : " + collectionPlanID));
        return dunningPolicyService.availablePoliciesForSwitch(invoice);
    }
    
    public Optional<DunningCollectionPlan> pauseCollectionPlan(DunningCollectionPlanPause dunningCollectionPlan,
			Long id, DunningCollectionPlanPause dunningCollectionPlanInput) {
		var collectionPlanToPause = findById(id).orElseThrow(() -> new NotFoundException(NO_DUNNING_FOUND + id));
		DunningPauseReason dunningPauseReason = dunningPauseReasonService.findById(dunningCollectionPlanInput.getDunningPauseReason().getId());
		if(dunningPauseReason == null) {
			throw new NotFoundException("dunning Pause Reason with id " + dunningCollectionPlanInput.getDunningPauseReason().getId() + " does not exits");
		}
		collectionPlanToPause = dunningCollectionPlanService.pauseCollectionPlan(dunningCollectionPlan.getForcePause(), dunningCollectionPlan.getPauseUntil(), collectionPlanToPause, dunningPauseReason);
		trackOperation("PAUSE", new Date(), collectionPlanToPause);
		return of(collectionPlanToPause);
	}
	
	
	public Optional<DunningCollectionPlan> stopCollectionPlan(DunningCollectionPlanStop dunningCollectionPlan, Long id, DunningCollectionPlanStop dunningCollectionPlanInput) {
		var collectionPlanToStop = findById(id).orElseThrow(() -> new BadRequestException(NO_DUNNING_FOUND + id));
		DunningStopReason dunningStopReason = dunningStopReasonService.findById(dunningCollectionPlanInput.getDunningStopReason().getId());
		if(dunningStopReason == null) {
			throw new NotFoundException("dunning Pause Reason with id " + dunningCollectionPlanInput.getDunningStopReason().getId() + " does not exits");
		}
		collectionPlanToStop = dunningCollectionPlanService.stopCollectionPlan(collectionPlanToStop, dunningStopReason);
		trackOperation("STOP", new Date(), collectionPlanToStop);
		return of(collectionPlanToStop);
	}
	
	public Optional<DunningCollectionPlan> resumeCollectionPlan(Long id) {
		var collectionPlanToResume = findById(id).orElseThrow(() -> new BadRequestException(NO_DUNNING_FOUND + id));
		collectionPlanToResume = dunningCollectionPlanService.resumeCollectionPlan(collectionPlanToResume);
		trackOperation("RESUME", new Date(), collectionPlanToResume);
		return of(collectionPlanToResume);
	}
}