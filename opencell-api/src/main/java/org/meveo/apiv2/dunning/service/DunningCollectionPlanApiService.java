package org.meveo.apiv2.dunning.service;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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
import org.meveo.apiv2.dunning.DunningActionInstanceInput;
import org.meveo.apiv2.dunning.DunningCollectionPlanPause;
import org.meveo.apiv2.dunning.DunningCollectionPlanStop;
import org.meveo.apiv2.dunning.DunningLevelInstanceInput;
import org.meveo.apiv2.dunning.MassStopDunningCollectionPlan;
import org.meveo.apiv2.dunning.MassSwitchDunningCollectionPlan;
import org.meveo.apiv2.dunning.RemoveActionInstanceInput;
import org.meveo.apiv2.dunning.RemoveLevelInstanceInput;
import org.meveo.apiv2.dunning.SwitchDunningCollectionPlan;
import org.meveo.apiv2.dunning.UpdateLevelInstanceInput;
import org.meveo.apiv2.models.Resource;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.audit.logging.AuditLog;
import org.meveo.model.billing.Invoice;
import org.meveo.model.dunning.DunningAction;
import org.meveo.model.dunning.DunningActionInstance;
import org.meveo.model.dunning.DunningActionInstanceStatusEnum;
import org.meveo.model.dunning.DunningAgent;
import org.meveo.model.dunning.DunningCollectionPlan;
import org.meveo.model.dunning.DunningLevelInstance;
import org.meveo.model.dunning.DunningLevelInstanceStatusEnum;
import org.meveo.model.dunning.DunningPauseReason;
import org.meveo.model.dunning.DunningPolicy;
import org.meveo.model.dunning.DunningPolicyLevel;
import org.meveo.model.dunning.DunningStopReason;
import org.meveo.model.payments.ActionModeEnum;
import org.meveo.model.payments.DunningCollectionPlanStatusEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.audit.logging.AuditLogService;
import org.meveo.service.payments.impl.DunningActionInstanceService;
import org.meveo.service.payments.impl.DunningActionService;
import org.meveo.service.payments.impl.DunningAgentService;
import org.meveo.service.payments.impl.DunningCollectionPlanService;
import org.meveo.service.payments.impl.DunningCollectionPlanStatusService;
import org.meveo.service.payments.impl.DunningLevelInstanceService;
import org.meveo.service.payments.impl.DunningLevelService;
import org.meveo.service.payments.impl.DunningPauseReasonsService;
import org.meveo.service.payments.impl.DunningPolicyLevelService;
import org.meveo.service.payments.impl.DunningPolicyService;
import org.meveo.service.payments.impl.DunningStopReasonsService;

public class DunningCollectionPlanApiService implements ApiService<DunningCollectionPlan> {

    @Inject
    @CurrentUser
    private MeveoUser currentUser;

    @Inject
    private ResourceBundle resourceMessages;

    @Inject
    private DunningCollectionPlanService dunningCollectionPlanService;

    @Inject
    private DunningPolicyService dunningPolicyService;

    @Inject
    private DunningPolicyLevelService dunningPolicyLevelService;

    @Inject
    private DunningActionService dunningActionService;

    @Inject
    private DunningActionInstanceService dunningActionInstanceService;
    
    @Inject
    private DunningAgentService dunningAgentService;

    @Inject
    private DunningLevelService dunningLevelService;

    @Inject
    private DunningLevelInstanceService dunningLevelInstanceService;

    @Inject
	private DunningPauseReasonsService dunningPauseReasonService;

	@Inject
	private DunningStopReasonsService dunningStopReasonService;
	
	@Inject
    private DunningCollectionPlanStatusService dunningCollectionPlanStatusService;
	
    @Inject
    private AuditLogService auditLogService;
	
	private static final String NO_DUNNING_FOUND = "No Dunning Plan collection found with id : ";

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
            dunningCollectionPlanService.remove(dunningCollectionPlan);
            return of(dunningCollectionPlan);
        } else {
            return empty();
        }
    }

    @Override
    public Optional<DunningCollectionPlan> findByCode(String code) {
        return empty();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
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
                    if (invoice.getId() == collectionPlan.getRelatedInvoice().getId()) {
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
        Invoice invoice = ofNullable(collectionPlan.getRelatedInvoice())
                .orElseThrow(() -> new NotFoundException("No invoice found for collection plan : " + collectionPlanID));
        return dunningPolicyService.availablePoliciesForSwitch(invoice);
    }
    
    public Optional<DunningCollectionPlan> pauseCollectionPlan(DunningCollectionPlanPause dunningCollectionPlanPause, Long id) {
		var collectionPlanToPause = findById(id).orElseThrow(() -> new NotFoundException(NO_DUNNING_FOUND + id));
		DunningPauseReason dunningPauseReason = dunningPauseReasonService.findById(dunningCollectionPlanPause.getDunningPauseReason().getId());
		if(dunningPauseReason == null) {
			throw new NotFoundException("dunning Pause Reason with id " + dunningCollectionPlanPause.getDunningPauseReason().getId() + " does not exits");
		}
		collectionPlanToPause = dunningCollectionPlanService.pauseCollectionPlan(dunningCollectionPlanPause.getForcePause(), dunningCollectionPlanPause.getPauseUntil(), collectionPlanToPause, dunningPauseReason);
		return of(collectionPlanToPause);
	}
	
	
	public Optional<DunningCollectionPlan> stopCollectionPlan(DunningCollectionPlanStop dunningCollectionPlanStop, Long id) {
		var collectionPlanToStop = findById(id).orElseThrow(() -> new NotFoundException(NO_DUNNING_FOUND + id));
		DunningStopReason dunningStopReason = dunningStopReasonService.findById(dunningCollectionPlanStop.getDunningStopReason().getId());
		if(dunningStopReason == null) {
			throw new NotFoundException("dunning Pause Reason with id " + dunningCollectionPlanStop.getDunningStopReason().getId() + " does not exits");
		}
		collectionPlanToStop = dunningCollectionPlanService.stopCollectionPlan(collectionPlanToStop, dunningStopReason);
		return of(collectionPlanToStop);
	}
	
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void massStopCollectionPlan(MassStopDunningCollectionPlan massStopDunningCollectionPlan) {
    	DunningStopReason stopReason = dunningStopReasonService.findById(massStopDunningCollectionPlan.getDunningStopReason().getId());
        if (stopReason == null) {
            throw new NotFoundException("Dunning Stop Reason with id " + massStopDunningCollectionPlan.getDunningStopReason().getId() + " does not exits");
        }

        List<Resource> collectionPlanList = massStopDunningCollectionPlan.getCollectionPlans();
        if (collectionPlanList != null) {
            for (Resource collectionPlanResource : collectionPlanList) {
                DunningCollectionPlan collectionPlan = dunningCollectionPlanService.findById(collectionPlanResource.getId());
                if (collectionPlan == null) {
                    throw new NotFoundException("Dunning collection plan with id " + collectionPlanResource.getId() + " does not exits");
                }
                dunningCollectionPlanService.stopCollectionPlan(collectionPlan, stopReason);
            }
        }
    }
    
	public Optional<DunningCollectionPlan> resumeCollectionPlan(Long id) {
		var collectionPlanToResume = findById(id).orElseThrow(() -> new NotFoundException(NO_DUNNING_FOUND + id));
		collectionPlanToResume = dunningCollectionPlanService.resumeCollectionPlan(collectionPlanToResume);
		return of(collectionPlanToResume);
	}
	
	public void removeDunningLevelInstance(RemoveLevelInstanceInput removeLevelInstanceInput) {

	    List<Long> levels = removeLevelInstanceInput.getLevels();
	    
	    if (levels != null) {
            for (Long levelInstanceId : levels) {
                DunningLevelInstance dunningLevelInstance = dunningLevelInstanceService.findById(levelInstanceId);
                if (dunningLevelInstance == null) {
                    new NotFoundException("No Dunning Level Instance found with id : " + levelInstanceId);
                }
                if (dunningLevelInstance.getDunningLevel().isEndOfDunningLevel()) {
                    new BadRequestException("Cannot modify or delete the end level");
                }
                if (dunningLevelInstance.getLevelStatus() != null && dunningLevelInstance.getLevelStatus() != DunningLevelInstanceStatusEnum.TO_BE_DONE) {
                    new BadRequestException("Cannot delete a level instance with status : " + dunningLevelInstance.getLevelStatus());
                }
                dunningLevelInstanceService.remove(dunningLevelInstance);
                trackOperation("REMOVE DunningLevelInstance", new Date(), dunningLevelInstance.getCollectionPlan());
            }
        }
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void removeDunningActionInstance(RemoveActionInstanceInput removeActionInstanceInput) {

        List<Long> actionIds = removeActionInstanceInput.getActions();
        
        if (actionIds != null) {
            for (Long actionInstanceId : actionIds) {
                DunningActionInstance dunningActionInstance = dunningActionInstanceService.findById(actionInstanceId, Arrays.asList("dunningLevelInstance"));
                if (dunningActionInstance == null) {
                    new NotFoundException("No Dunning Action Instance found with id : " + actionInstanceId);
                }
                
                // 1- User cannot either modify or delete the end level!
                DunningLevelInstance dunningLevelInstance = dunningLevelInstanceService.findById(dunningActionInstance.getDunningLevelInstance().getId(), Arrays.asList("actions"));
                if (dunningLevelInstance.getDunningLevel().isEndOfDunningLevel()) {
                    new BadRequestException("Cannot modify or delete the end level");
                }
                // 2- If the dunningActionInstance status is DONE  ==> it can not be deleted.
                if (dunningActionInstance.getActionStatus() != null && dunningActionInstance.getActionStatus() == DunningActionInstanceStatusEnum.DONE) {
                    new BadRequestException("Cannot delete an action instance with status DONE");
                }
                // 3- If the remaining DunningActionInstance of the dunningLevelInstance are DONE 
                List<DunningActionInstance> actions = dunningLevelInstance.getActions();
                actions.removeIf(a -> a.getId() == dunningActionInstance.getId());

                boolean remainingActionsAreDone = true;
                for (DunningActionInstance action : actions) {
                    if (action.getActionStatus() != DunningActionInstanceStatusEnum.DONE) {
                        remainingActionsAreDone = false;
                        break;
                    }
                }
                if (remainingActionsAreDone) {
                    // 3.1- Update the dunningLevelInstance status also to 
                    dunningLevelInstance.setLevelStatus(DunningLevelInstanceStatusEnum.DONE);
                    dunningLevelInstanceService.update(dunningLevelInstance);
                    // 3.2- Update DunningCollectionPlan : currentDunningLevelSequence / lastAction / lastActionDate / nextAction /nextActionDate
                    updateCollectionPlanActions(dunningLevelInstance);
                }
                
                dunningActionInstanceService.remove(dunningActionInstance);
                trackOperation("REMOVE DunningActionInstance", new Date(), dunningActionInstance.getCollectionPlan());
            }
        }
    }

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Optional<DunningLevelInstance> addDunningLevelInstance(DunningLevelInstanceInput dunningLevelInstanceInput) {

        DunningLevelInstance newDunningLevelInstance = new DunningLevelInstance();
        newDunningLevelInstance.setSequence(dunningLevelInstanceInput.getSequence());
        newDunningLevelInstance.setDaysOverdue(dunningLevelInstanceInput.getDaysOverdue());
        newDunningLevelInstance.setLevelStatus(dunningLevelInstanceInput.getLevelStatus());

        Long collectionPlanId = dunningLevelInstanceInput.getCollectionPlan().getId();
        var collectionPlan = findById(collectionPlanId).orElseThrow(() -> new NotFoundException(NO_DUNNING_FOUND + collectionPlanId));
        newDunningLevelInstance.setCollectionPlan(collectionPlan);

        Long dunningLevelId = dunningLevelInstanceInput.getDunningLevel().getId();
        var dunningLevel = dunningLevelService.findById(dunningLevelId);
        if (dunningLevel.isReminder()) {
            throw new BadRequestException("Can not create a new dunning level instance if dunningLevel.isReminderLevel is TRUE");
        }
        if (dunningLevel.isEndOfDunningLevel()) {
            throw new BadRequestException("Can not create a new dunning level instance if dunningLevel.isEndOfDunningLevel is TRUE");
        }
        newDunningLevelInstance.setDunningLevel(dunningLevel);

        DunningLevelInstance lastLevelInstance = dunningLevelInstanceService.findLastLevelInstance(collectionPlan);
        if (lastLevelInstance != null && newDunningLevelInstance.getSequence() > lastLevelInstance.getSequence()) {
            throw new BadRequestException("The sequence is greater than the endLevel");
        }

        dunningLevelInstanceService.create(newDunningLevelInstance);

        createActions(newDunningLevelInstance, dunningLevelInstanceInput.getActions());

        trackOperation("ADD DunningLevelInstance", new Date(), collectionPlan);
        return of(newDunningLevelInstance);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Optional<DunningLevelInstance> updateDunningLevelInstance(UpdateLevelInstanceInput updateLevelInstanceInput, Long levelInstanceId) {

	    DunningLevelInstance dunningLevelInstanceToUpdate = dunningLevelInstanceService.findById(levelInstanceId, Arrays.asList("actions"));
        if (dunningLevelInstanceToUpdate == null) {
            new NotFoundException("No Dunning Level Instance found with id : " + levelInstanceId);
        }

        if (updateLevelInstanceInput.getSequence() != null) {
            dunningLevelInstanceToUpdate.setSequence(updateLevelInstanceInput.getSequence());
        }
        if (updateLevelInstanceInput.getDaysOverdue() != null) {
            dunningLevelInstanceToUpdate.setDaysOverdue(updateLevelInstanceInput.getDaysOverdue());
        }
        if (updateLevelInstanceInput.getLevelStatus() != null) {
            if (dunningLevelInstanceToUpdate.getLevelStatus() == DunningLevelInstanceStatusEnum.DONE) {
                throw new BadRequestException("Cannot update a DONE dunningLevelInstance");
            }
            dunningLevelInstanceToUpdate.setLevelStatus(updateLevelInstanceInput.getLevelStatus());
        }
        dunningLevelInstanceService.update(dunningLevelInstanceToUpdate);
        

        if (updateLevelInstanceInput.getActions() != null) {
            for (DunningActionInstance action : dunningLevelInstanceToUpdate.getActions()) {
                dunningActionInstanceService.remove(action);
            }
            createActions(dunningLevelInstanceToUpdate, updateLevelInstanceInput.getActions());
        }

        updateCollectionPlanActions(dunningLevelInstanceToUpdate);

        trackOperation("UPDATE DunningLevelInstance", new Date(), dunningLevelInstanceToUpdate.getCollectionPlan());
        return of(dunningLevelInstanceToUpdate);
    }

	public Optional<DunningActionInstance> addDunningActionInstance(DunningActionInstanceInput dunningActionInstanceInput) {
        DunningActionInstance dunningActionInstance = new DunningActionInstance();
        
        if (dunningActionInstanceInput.getDunningLevelInstance() == null || dunningActionInstanceInput.getDunningLevelInstance().getId() == null) {
            new BadRequestException("Attribut dunningLevelInstance is mandatory");
        }
        Long dunningLevelInstanceId = dunningActionInstanceInput.getDunningLevelInstance().getId();
        DunningLevelInstance dunningLevelInstance = dunningLevelInstanceService.findById(dunningLevelInstanceId);
        if (dunningLevelInstance == null) {
            new NotFoundException("No Dunning Level found with id : " + dunningLevelInstanceId);
        }else if(dunningLevelInstance.getDunningLevel().isEndOfDunningLevel() == true) {
            new BadRequestException("Cant not add actions at the end of dunning level with id : " + dunningLevelInstanceId);
        }else {
            dunningActionInstance.setDunningLevelInstance(dunningLevelInstance);
        }

        if (dunningActionInstanceInput.getCollectionPlan() == null || dunningActionInstanceInput.getCollectionPlan().getId() == null) {
            new BadRequestException("Attribut collectionPlan is mandatory");
        }
        Long collectionPlanId = dunningActionInstanceInput.getCollectionPlan().getId();
        DunningCollectionPlan collectionPlan = dunningCollectionPlanService.findById(collectionPlanId);
        if (collectionPlan == null) {
            new NotFoundException("No Dunning Collection Plan found with id : " + collectionPlanId);
        }
        dunningActionInstance.setCollectionPlan(collectionPlan);
        

        if(dunningActionInstanceInput.getDunningAction() != null && dunningActionInstanceInput.getDunningAction().getId() != null) {
            Long dunningActionId = dunningActionInstanceInput.getDunningAction().getId();
            DunningAction dunningAction = dunningActionService.findById(dunningActionId);
            if (dunningAction == null) {
                new NotFoundException("No Dunning action found with id : " + dunningActionId);
            }
            dunningActionInstance.setDunningAction(dunningAction);
        }

        if (dunningActionInstanceInput.getActionOwner() != null && dunningActionInstanceInput.getActionOwner().getId() != null) {
            Long dunningAgentId = dunningActionInstanceInput.getActionOwner().getId();
            DunningAgent dunningAgent = dunningAgentService.findById(dunningAgentId);
            if (dunningAgent == null) {
                new NotFoundException("No Dunning agent found with id : " + dunningAgentId);
            }
            dunningActionInstance.setActionOwner(dunningAgent);
        }
        
        dunningActionInstance.setCode(dunningActionInstanceInput.getCode());
        dunningActionInstance.setDescription(dunningActionInstanceInput.getDescription());
        dunningActionInstance.setActionType(dunningActionInstanceInput.getActionType());
        dunningActionInstance.setActionMode(dunningActionInstanceInput.getMode());
        dunningActionInstance.setActionStatus(dunningActionInstanceInput.getActionStatus());
        dunningActionInstance.setActionRestult(dunningActionInstanceInput.getActionRestult());
        
        dunningActionInstanceService.create(dunningActionInstance);

        trackOperation("ADD DunningActionInstance", new Date(), collectionPlan);
        return of(dunningActionInstance);
    }

	public Optional<DunningActionInstance> updateDunningActionInstance(DunningActionInstanceInput dunningActionInstanceInput, Long actionInstanceId) {
        
	    DunningActionInstance dunningActionInstanceToUpdate = dunningActionInstanceService.findById(actionInstanceId, Arrays.asList("dunningLevelInstance"));
        if (dunningActionInstanceToUpdate == null) {
            new NotFoundException("No Dunning Action Instance found with id : " + actionInstanceId);
        }

        if (dunningActionInstanceToUpdate.getActionStatus() == DunningActionInstanceStatusEnum.DONE) {
            new BadRequestException("Can not update a DONE dunningActionInstace");
        }

        if (dunningActionInstanceInput.getDunningLevelInstance() == null || dunningActionInstanceInput.getDunningLevelInstance().getId() == null) {
            new BadRequestException("Attribut dunningLevelInstance is mandatory");
        }
        Long dunningLevelInstanceId = dunningActionInstanceInput.getDunningLevelInstance().getId();
        DunningLevelInstance dunningLevelInstance = dunningLevelInstanceService.findById(dunningLevelInstanceId);
        if (dunningLevelInstance == null) {
            new NotFoundException("No Dunning Level found with id : " + dunningLevelInstanceId);
        }
        else {
            dunningActionInstanceToUpdate.setDunningLevelInstance(dunningLevelInstance);
        }

        if(dunningActionInstanceInput.getDunningAction() != null && dunningActionInstanceInput.getDunningAction().getId() != null) {
            Long dunningActionId = dunningActionInstanceInput.getDunningAction().getId();
            DunningAction dunningAction = dunningActionService.findById(dunningActionId);
            if (dunningAction == null) {
                new NotFoundException("No Dunning action found with id : " + dunningActionId);
            }
            dunningActionInstanceToUpdate.setDunningAction(dunningAction);
        }

        if (dunningActionInstanceInput.getActionOwner() != null && dunningActionInstanceInput.getActionOwner().getId() != null) {
            Long dunningAgentId = dunningActionInstanceInput.getActionOwner().getId();
            DunningAgent dunningAgent = dunningAgentService.findById(dunningAgentId);
            if (dunningAgent == null) {
                new NotFoundException("No Dunning agent found with id : " + dunningAgentId);
            }
            dunningActionInstanceToUpdate.setActionOwner(dunningAgent);
        }

        if (dunningActionInstanceInput.getCode() != null) {            
            dunningActionInstanceToUpdate.setCode(dunningActionInstanceInput.getCode());
        }
        if (dunningActionInstanceInput.getDescription() != null) {            
            dunningActionInstanceToUpdate.setDescription(dunningActionInstanceInput.getDescription());
        }
        if (dunningActionInstanceInput.getActionType() != null) {            
            dunningActionInstanceToUpdate.setActionType(dunningActionInstanceInput.getActionType());
        }
        if (dunningActionInstanceInput.getMode() != null) {            
            dunningActionInstanceToUpdate.setActionMode(dunningActionInstanceInput.getMode());
        }
        if (dunningActionInstanceInput.getActionStatus() != null) {            
            dunningActionInstanceToUpdate.setActionStatus(dunningActionInstanceInput.getActionStatus());
        }
        if (dunningActionInstanceInput.getActionRestult() != null) {            
            dunningActionInstanceToUpdate.setActionRestult(dunningActionInstanceInput.getActionRestult());
        }

        dunningActionInstanceService.update(dunningActionInstanceToUpdate);

        trackOperation("UPDATE DunningActionInstance", new Date(), dunningActionInstanceToUpdate.getCollectionPlan());
        return of(dunningActionInstanceToUpdate);
    }

	private  void createActions(DunningLevelInstance dunningLevelInstance, List<DunningActionInstanceInput> actionInstanceInputs) {
	    List<DunningActionInstance> actions = new ArrayList<>();
        
	    for (DunningActionInstanceInput actionInput : actionInstanceInputs) {
            DunningActionInstance dunningActionInstance = new DunningActionInstance();
            Long dunningActionId = actionInput.getDunningAction().getId();
            DunningAction dunningAction = dunningActionService.findById(dunningActionId);
            if (dunningAction == null) {
                new NotFoundException("No Dunning action found with id : " + dunningActionId);
            }

            if (actionInput.getCode() != null) {
                dunningActionInstance.setCode(actionInput.getCode());
            }
            else {
                dunningActionInstance.setCode(dunningAction.getCode());
            }

            if (actionInput.getDescription() != null) {
                dunningActionInstance.setDescription(actionInput.getDescription());
            }
            else {
                dunningActionInstance.setDescription(dunningAction.getDescription());
            }

            if (actionInput.getActionType() != null) {
                dunningActionInstance.setActionType(actionInput.getActionType());
            }
            else {
                dunningActionInstance.setActionType(dunningAction.getActionType());
            }
            
            if (actionInput.getMode() != null) {
                dunningActionInstance.setActionMode(actionInput.getMode());
            }
            else {
                dunningActionInstance.setActionMode(dunningAction.getActionMode());
            }

            if (actionInput.getActionOwner() != null && actionInput.getActionOwner().getId() != null) {
                Long dunningAgentId = actionInput.getActionOwner().getId();
                DunningAgent dunningAgent = dunningAgentService.findById(dunningAgentId);
                if (dunningAgent == null) {
                    new NotFoundException("No Dunning agent found with id : " + dunningAgentId);
                }
                dunningActionInstance.setActionOwner(dunningAgent);
            }
            else {
                dunningActionInstance.setActionOwner(dunningAction.getAssignedTo());
            }

            dunningActionInstance.setActionRestult(actionInput.getActionRestult());
            if (dunningLevelInstance.getLevelStatus() == DunningLevelInstanceStatusEnum.DONE) {
                dunningActionInstance.setActionStatus(DunningActionInstanceStatusEnum.DONE);
            }
            else {
                dunningActionInstance.setActionStatus(actionInput.getActionStatus());
            }
            dunningActionInstance.setCollectionPlan(dunningLevelInstance.getCollectionPlan());
            dunningActionInstance.setDunningLevelInstance(dunningLevelInstance);
            dunningActionInstance.setDunningAction(dunningAction);
            dunningActionInstanceService.create(dunningActionInstance);
            actions.add(dunningActionInstance);
        }

        dunningLevelInstance.setActions(actions);
	}

	private void updateCollectionPlanActions(DunningLevelInstance dunningLevelInstance) {
    	if (dunningLevelInstance.getLevelStatus() == DunningLevelInstanceStatusEnum.DONE) {
            
            DunningCollectionPlan collectionPlan = dunningLevelInstance.getCollectionPlan();
            
            if (!dunningLevelInstance.getDunningLevel().isEndOfDunningLevel()) {
                
                Integer currentDunningLevelSequence = collectionPlan.getCurrentDunningLevelSequence();
                if (currentDunningLevelSequence == null) {
                    currentDunningLevelSequence = 0;
                }
                collectionPlan.setCurrentDunningLevelSequence(++currentDunningLevelSequence);
    
                List<DunningActionInstance> lastLevelActions = dunningLevelInstance.getActions();
                if (lastLevelActions != null && !lastLevelActions.isEmpty()) {
                    collectionPlan.setLastAction(lastLevelActions.stream()
                        .sorted(Comparator.comparing(a -> a.getAuditable().getLastModified(), Comparator.reverseOrder()))
                        .findFirst().get().getCode());
                    
                    collectionPlan.setLastActionDate(new Date());
                }
    
                DunningLevelInstance nextLevelInstance = dunningLevelInstanceService.findByCurrentLevelSequence(collectionPlan);
                String nextLevelAction = null;
                List<DunningActionInstance> nextLevelActions = nextLevelInstance.getActions();
                if (nextLevelActions != null && !nextLevelActions.isEmpty()) {
                    for (DunningActionInstance nextActionInstance : nextLevelActions) {
                        if (nextActionInstance.getActionMode() == ActionModeEnum.AUTOMATIC) {
                            nextLevelAction = nextActionInstance.getCode();
                            break;
                        }
                    }
                    if (nextLevelAction == null) {
                        nextLevelAction = nextLevelActions.get(0).getCode();
                    }
    
                    collectionPlan.setNextAction(nextLevelAction);
                    collectionPlan.setNextActionDate(DateUtils.addDaysToDate(collectionPlan.getStartDate(), nextLevelInstance.getDaysOverdue()));
                }
            }
            else {
                collectionPlan.setNextAction(null);
                collectionPlan.setNextActionDate(null);
                collectionPlan.setStatus(dunningCollectionPlanStatusService.findByStatus(DunningCollectionPlanStatusEnum.FAILED));
            }
    
            dunningCollectionPlanService.update(collectionPlan);
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
        auditLog.setOrigin("API");
        auditLogService.create(auditLog);
        return auditLog;
    }
}