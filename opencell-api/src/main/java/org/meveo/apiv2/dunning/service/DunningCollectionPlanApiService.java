package org.meveo.apiv2.dunning.service;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.*;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.util.ResourceBundle;
import org.meveo.api.exception.ActionForbiddenException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.apiv2.dunning.DunningActionInstanceInput;
import org.meveo.apiv2.dunning.DunningCollectionPlanPause;
import org.meveo.apiv2.dunning.DunningCollectionPlanStop;
import org.meveo.apiv2.dunning.DunningLevelInstanceInput;
import org.meveo.apiv2.dunning.MassPauseDunningCollectionPlan;
import org.meveo.apiv2.dunning.MassStopDunningCollectionPlan;
import org.meveo.apiv2.dunning.MassSwitchDunningCollectionPlan;
import org.meveo.apiv2.dunning.RemoveActionInstanceInput;
import org.meveo.apiv2.dunning.RemoveLevelInstanceInput;
import org.meveo.apiv2.dunning.SwitchDunningCollectionPlan;
import org.meveo.apiv2.dunning.UpdateLevelInstanceInput;
import org.meveo.apiv2.models.Resource;
import org.meveo.apiv2.ordering.services.ApiService;
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
        return ofNullable(dunningCollectionPlan);
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
        DunningCollectionPlan dunningCollectionPlan = findById(id).get();
        dunningCollectionPlanService.remove(dunningCollectionPlan);
        auditLogService.trackOperation("REMOVE", new Date(), dunningCollectionPlan);
        return of(dunningCollectionPlan);
    }

    @Override
    public Optional<DunningCollectionPlan> findByCode(String code) {
        return empty();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Optional<DunningCollectionPlan> switchCollectionPlan(Long collectionPlanId, SwitchDunningCollectionPlan switchDunningCollectionPlan) {
        DunningCollectionPlan oldCollectionPlan = dunningCollectionPlanService.findById(collectionPlanId);
        if (oldCollectionPlan == null) {
            throw new EntityDoesNotExistsException("Dunning collection plan with id " + collectionPlanId + " does not exits");
        }
        DunningPolicy policy = dunningPolicyService.findById(switchDunningCollectionPlan.getDunningPolicy().getId());
        if (policy == null) {
            throw new EntityDoesNotExistsException("Policy with id " + switchDunningCollectionPlan.getDunningPolicy().getId() + " does not exits");
        }
        DunningPolicyLevel policyLevel = dunningPolicyLevelService.findById(switchDunningCollectionPlan.getPolicyLevel().getId());
        if (policyLevel == null) {
            throw new EntityDoesNotExistsException("Policy level with id " + switchDunningCollectionPlan.getPolicyLevel().getId() + " does not exits");
        }
        Optional<DunningCollectionPlan> optional = of(dunningCollectionPlanService.switchCollectionPlan(oldCollectionPlan, policy, policyLevel));
        auditLogService.trackOperation("SWITCH", new Date(), oldCollectionPlan);
        return optional;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void massSwitchCollectionPlan(MassSwitchDunningCollectionPlan massSwitchDunningCollectionPlan) {
        DunningPolicy policy = dunningPolicyService.findById(massSwitchDunningCollectionPlan.getDunningPolicy().getId());
        if (policy == null) {
            throw new EntityDoesNotExistsException("Policy with id " + massSwitchDunningCollectionPlan.getDunningPolicy().getId() + " does not exits");
        }
        DunningPolicyLevel policyLevel = dunningPolicyLevelService.findById(massSwitchDunningCollectionPlan.getPolicyLevel().getId());
        if (policyLevel == null) {
            throw new EntityDoesNotExistsException("Policy level with id " + massSwitchDunningCollectionPlan.getPolicyLevel().getId() + " does not exits");
        }

        List<Resource> collectionPlanList = massSwitchDunningCollectionPlan.getCollectionPlanList();
        if (collectionPlanList != null) {
            for (Resource collectionPlanResource : collectionPlanList) {
                DunningCollectionPlan collectionPlan = dunningCollectionPlanService.findById(collectionPlanResource.getId());
                if (collectionPlan == null) {
                    throw new EntityDoesNotExistsException("Dunning collection plan with id " + collectionPlanResource.getId() + " does not exits");
                }
                dunningCollectionPlanService.switchCollectionPlan(collectionPlan, policy, policyLevel);
                auditLogService.trackOperation("SWITCH", new Date(), collectionPlan);
            }
        }
    }

    public Optional<Map<String, Set<Long>>> checkMassSwitch(DunningPolicy policy, List<DunningCollectionPlan> collectionPlans) {
        List<Invoice> eligibleInvoice = dunningPolicyService.findEligibleInvoicesForPolicy(policy);
        Set<Long> canBeSwitched = new TreeSet<>();
        Set<Long> canNotBeSwitched = new TreeSet<>();
        Map<String, Set<Long>> massSwitchResult = new HashMap<>();
        if (eligibleInvoice != null && !eligibleInvoice.isEmpty()) {
            for (DunningCollectionPlan collectionPlan : collectionPlans) {
                collectionPlan = dunningCollectionPlanService.findById(collectionPlan.getId());
                if (collectionPlan == null) {
                    throw new EntityDoesNotExistsException("Collection plan does not exits");
                }
                for (Invoice invoice : eligibleInvoice) {
                    if (invoice.getId() == collectionPlan.getRelatedInvoice().getId()) {
                        canBeSwitched.add(collectionPlan.getId());
                    }
                }
            }
            canNotBeSwitched = collectionPlans.stream()
                                    .map(DunningCollectionPlan::getId)
                                    .filter(collectionPlanId -> !canBeSwitched.contains(collectionPlanId))
                                    .collect(toSet());
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
            throw new EntityDoesNotExistsException(resourceMessages.getString("error.collectionPlan.availablePolicies.collectionPlanNotFound", collectionPlanID));
        }
        Invoice invoice = ofNullable(collectionPlan.getRelatedInvoice())
            .orElseThrow(() -> new EntityDoesNotExistsException("No invoice found for collection plan : " + collectionPlanID));
        return dunningPolicyService.availablePoliciesForSwitch(invoice);
    }

    public Optional<DunningCollectionPlan> pauseCollectionPlan(DunningCollectionPlanPause dunningCollectionPlanPause, Long id) {
        var collectionPlanToPause = findById(id).orElseThrow(() -> new EntityDoesNotExistsException(NO_DUNNING_FOUND + id));
        DunningPauseReason dunningPauseReason = dunningPauseReasonService.findById(dunningCollectionPlanPause.getDunningPauseReason().getId());
        if (dunningPauseReason == null) {
            throw new EntityDoesNotExistsException("dunning Pause Reason with id " + dunningCollectionPlanPause.getDunningPauseReason().getId() + " does not exits");
        }
        collectionPlanToPause = dunningCollectionPlanService.pauseCollectionPlan(dunningCollectionPlanPause.getForcePause(), dunningCollectionPlanPause.getPauseUntil(),
            collectionPlanToPause, dunningPauseReason);
        auditLogService.trackOperation("PAUSE Reason : " + dunningPauseReason.getPauseReason(), new Date(), collectionPlanToPause);
        return of(collectionPlanToPause);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void massPauseCollectionPlan(MassPauseDunningCollectionPlan massPauseDunningCollectionPlan) {
        DunningPauseReason pauseReason = dunningPauseReasonService.findById(massPauseDunningCollectionPlan.getDunningPauseReason().getId());
        if (pauseReason == null) {
            throw new EntityDoesNotExistsException("Dunning Pause Reason with id " + massPauseDunningCollectionPlan.getDunningPauseReason().getId() + " does not exits");
        }

        List<Resource> collectionPlanList = massPauseDunningCollectionPlan.getCollectionPlans();
        if (collectionPlanList != null) {
            for (Resource collectionPlanResource : collectionPlanList) {
                DunningCollectionPlan collectionPlan = dunningCollectionPlanService.findById(collectionPlanResource.getId());
                if (collectionPlan == null) {
                    throw new EntityDoesNotExistsException("Dunning collection plan with id " + collectionPlanResource.getId() + " does not exits");
                }
                dunningCollectionPlanService.pauseCollectionPlan(massPauseDunningCollectionPlan.getForcePause(), massPauseDunningCollectionPlan.getPauseUntil(), collectionPlan,
                    pauseReason);
                auditLogService.trackOperation("PAUSE Reason : " + pauseReason.getPauseReason(), new Date(), collectionPlan);
            }
        }
    }

    public Optional<DunningCollectionPlan> stopCollectionPlan(DunningCollectionPlanStop dunningCollectionPlanStop, Long id) {
        var collectionPlanToStop = findById(id).orElseThrow(() -> new EntityDoesNotExistsException(NO_DUNNING_FOUND + id));
        DunningStopReason dunningStopReason = dunningStopReasonService.findById(dunningCollectionPlanStop.getDunningStopReason().getId());
        if (dunningStopReason == null) {
            throw new EntityDoesNotExistsException("dunning Pause Reason with id " + dunningCollectionPlanStop.getDunningStopReason().getId() + " does not exits");
        }
        collectionPlanToStop = dunningCollectionPlanService.stopCollectionPlan(collectionPlanToStop, dunningStopReason);
        auditLogService.trackOperation("STOP Reason : " + dunningStopReason.getStopReason(), new Date(), collectionPlanToStop);
        return of(collectionPlanToStop);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void massStopCollectionPlan(MassStopDunningCollectionPlan massStopDunningCollectionPlan) {
        DunningStopReason stopReason = dunningStopReasonService.findById(massStopDunningCollectionPlan.getDunningStopReason().getId());
        if (stopReason == null) {
            throw new EntityDoesNotExistsException("Dunning Stop Reason with id " + massStopDunningCollectionPlan.getDunningStopReason().getId() + " does not exits");
        }

        List<Resource> collectionPlanList = massStopDunningCollectionPlan.getCollectionPlans();
        if (collectionPlanList != null) {
            for (Resource collectionPlanResource : collectionPlanList) {
                DunningCollectionPlan collectionPlan = dunningCollectionPlanService.findById(collectionPlanResource.getId());
                if (collectionPlan == null) {
                    throw new EntityDoesNotExistsException("Dunning collection plan with id " + collectionPlanResource.getId() + " does not exits");
                }
                dunningCollectionPlanService.stopCollectionPlan(collectionPlan, stopReason);
                auditLogService.trackOperation("STOP Reason : " + stopReason.getStopReason(), new Date(), collectionPlan);
            }
        }
    }

    public Optional<DunningCollectionPlan> resumeCollectionPlan(Long id) {
        var collectionPlanToResume = findById(id).orElseThrow(() -> new EntityDoesNotExistsException(NO_DUNNING_FOUND + id));
        collectionPlanToResume = dunningCollectionPlanService.resumeCollectionPlan(collectionPlanToResume);
        auditLogService.trackOperation("RESUME", new Date(), collectionPlanToResume);
        return of(collectionPlanToResume);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void removeDunningLevelInstance(RemoveLevelInstanceInput removeLevelInstanceInput) {
        try {
            List<Resource> levelInstanceResources = removeLevelInstanceInput.getLevels();

            if (levelInstanceResources != null) {
                for (Resource levelInstanceResource : levelInstanceResources) {

                    Long levelInstanceId = levelInstanceResource.getId();
                    DunningLevelInstance levelInstanceToRemove = dunningLevelInstanceService.findById(levelInstanceId, Arrays.asList("collectionPlan", "dunningLevel", "actions"));
                    if (levelInstanceToRemove == null) {
                        throw new EntityDoesNotExistsException("No Dunning Level Instance found with id : " + levelInstanceId);
                    }
                    // User cannot delete the end level
                    if (levelInstanceToRemove.getDunningLevel().isEndOfDunningLevel()) {
                        throw new ActionForbiddenException("Cannot delete the end level");
                    }
                    DunningCollectionPlan collectionPlan = levelInstanceToRemove.getCollectionPlan();
                    // User cannot the current dunning level instance
                    Integer currentDunningLevelSequence = collectionPlan.getCurrentDunningLevelSequence();
                    if (levelInstanceToRemove.getSequence() == currentDunningLevelSequence) {
                        throw new ActionForbiddenException("Cannot delete the current dunning level instance");
                    }
                    // If the dunningLevelInstance status is DONE or IN_PROGRESS
                    if (levelInstanceToRemove.getLevelStatus() == DunningLevelInstanceStatusEnum.DONE
                            || levelInstanceToRemove.getLevelStatus() == DunningLevelInstanceStatusEnum.IN_PROGRESS) {
                        throw new ActionForbiddenException("Cannot delete dunningLevelInstance with status DONE or IN_PROGRESS");
                    }
                    if (levelInstanceToRemove.getActions() != null) {
                        for (DunningActionInstance action : levelInstanceToRemove.getActions()) {
                            dunningActionInstanceService.remove(action);
                        }
                    }
                    dunningLevelInstanceService.remove(levelInstanceToRemove);

                    // Update DunningCollectionPlan totalDunningLevels
                    collectionPlan.setTotalDunningLevels(collectionPlan.getTotalDunningLevels() - 1);

                    // if the deleted dunningLevelInstance sequence = currentSequence + 1
                    if (currentDunningLevelSequence != null && levelInstanceToRemove.getSequence() == currentDunningLevelSequence + 1) {
                        DunningLevelInstance nextLevelInstance = dunningLevelInstanceService.findBySequence(collectionPlan, currentDunningLevelSequence + 1);
                        String nextLevelAction = null;
                        if (nextLevelInstance != null && nextLevelInstance.getActions() != null && !nextLevelInstance.getActions().isEmpty()) {
                            for (DunningActionInstance nextActionInstance : nextLevelInstance.getActions()) {
                                if (nextActionInstance.getActionMode() == ActionModeEnum.AUTOMATIC) {
                                    nextLevelAction = nextActionInstance.getCode();
                                    break;
                                }
                            }
                            if (nextLevelAction == null) {
                                nextLevelAction = nextLevelInstance.getActions().get(0).getCode();
                            }

                            collectionPlan.setNextAction(nextLevelAction);
                            collectionPlan
                                .setNextActionDate(DateUtils.addDaysToDate(collectionPlan.getStartDate(), nextLevelInstance.getDaysOverdue() + collectionPlan.getPauseDuration()));
                        }
                    }

                    dunningCollectionPlanService.update(collectionPlan);
                    // update the sequence of other levels
                    dunningLevelInstanceService.decrementSequecesGreaterThanDaysOverdue(collectionPlan, levelInstanceToRemove.getDaysOverdue());

                    auditLogService.trackOperation("REMOVE DunningLevelInstance", new Date(), levelInstanceToRemove.getCollectionPlan());
                }
            }
        } catch (MeveoApiException e) {
            throw e;
        } catch (Exception e) {
            throw new MeveoApiException(e);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void removeDunningActionInstance(RemoveActionInstanceInput removeActionInstanceInput) {

        try {
            List<Resource> actionInstanceResources = removeActionInstanceInput.getActions();

            if (actionInstanceResources != null) {
                for (Resource actionInstanceResource : actionInstanceResources) {
                    Long actionInstanceId = actionInstanceResource.getId();
                    DunningActionInstance dunningActionInstance = dunningActionInstanceService.findById(actionInstanceId, Arrays.asList("dunningLevelInstance"));
                    if (dunningActionInstance == null) {
                        throw new EntityDoesNotExistsException("No Dunning Action Instance found with id : " + actionInstanceId);
                    }

                    // 1- User cannot either modify or delete the end level!
                    DunningLevelInstance dunningLevelInstance = dunningLevelInstanceService.findById(dunningActionInstance.getDunningLevelInstance().getId(),
                        Arrays.asList("dunningLevel", "actions", "collectionPlan"));
                    if (dunningLevelInstance.getDunningLevel().isEndOfDunningLevel()) {
                        throw new ActionForbiddenException("Cannot modify or delete the end level");
                    }
                    // 2- If the dunningActionInstance status is DONE ==> it can not be deleted.
                    if (dunningActionInstance.getActionStatus() != null && dunningActionInstance.getActionStatus() == DunningActionInstanceStatusEnum.DONE) {
                        throw new ActionForbiddenException("Cannot delete an action instance with status DONE");
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
                    auditLogService.trackOperation("REMOVE DunningActionInstance", new Date(), dunningActionInstance.getCollectionPlan());
                }
            }
        } catch (MeveoApiException e) {
            throw e;
        } catch (Exception e) {
            throw new MeveoApiException(e);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Optional<DunningLevelInstance> addDunningLevelInstance(DunningLevelInstanceInput dunningLevelInstanceInput) {
        try {
            DunningLevelInstance newDunningLevelInstance = new DunningLevelInstance();

            Long collectionPlanId = dunningLevelInstanceInput.getCollectionPlan().getId();
            var collectionPlan = findById(collectionPlanId).orElseThrow(() -> new EntityDoesNotExistsException(NO_DUNNING_FOUND + collectionPlanId));
            newDunningLevelInstance.setCollectionPlan(collectionPlan);

            // 1- Can not create a new dunning level instance if :
            Long dunningLevelId = dunningLevelInstanceInput.getDunningLevel().getId();
            var dunningLevel = dunningLevelService.findById(dunningLevelId);
            // dunningLevel.isReminderLevel is TRUE
            if (dunningLevel.isReminder()) {
                throw new ActionForbiddenException("Can not create a new dunning level instance if dunningLevel.isReminderLevel is TRUE");
            }
            // dunningLevel.isEndOfDunningLevel is TRUE
            if (dunningLevel.isEndOfDunningLevel()) {
                throw new ActionForbiddenException("Can not create a new dunning level instance if dunningLevel.isEndOfDunningLevel is TRUE");
            }
            newDunningLevelInstance.setDunningLevel(dunningLevel);

            // check daysOverdue
            Integer daysOverdue = dunningLevelInstanceInput.getDaysOverdue();
            checkDaysOverdue(collectionPlan, daysOverdue);
            newDunningLevelInstance.setDaysOverdue(daysOverdue);

            if (dunningLevelInstanceInput.getLevelStatus() != null) {
                newDunningLevelInstance.setLevelStatus(dunningLevelInstanceInput.getLevelStatus());
            }

            // 2- set sequence
            Long minSequence = dunningLevelInstanceService.getMinSequenceByDaysOverdue(collectionPlan, daysOverdue);
            newDunningLevelInstance.setSequence(minSequence.intValue());

            dunningLevelInstanceService.create(newDunningLevelInstance);

            // 3- update dunningLevelInstances
            dunningLevelInstanceService.incrementSequecesGreaterThanDaysOverdue(collectionPlan, daysOverdue);

            // 4- update DunningCollectionPlan totalDunningLevels;
            collectionPlan.setTotalDunningLevels(collectionPlan.getTotalDunningLevels() + 1);
            dunningCollectionPlanService.update(collectionPlan);

            // Create actions
            createActions(newDunningLevelInstance, dunningLevelInstanceInput.getActions());

            auditLogService.trackOperation("ADD DunningLevelInstance", new Date(), collectionPlan);
            return of(newDunningLevelInstance);
        } catch (MeveoApiException e) {
            throw e;
        } catch (Exception e) {
            throw new MeveoApiException(e);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Optional<DunningLevelInstance> updateDunningLevelInstance(UpdateLevelInstanceInput updateLevelInstanceInput, Long levelInstanceId) {
        try {
            DunningLevelInstance levelInstanceToUpdate = dunningLevelInstanceService.findById(levelInstanceId, Arrays.asList("dunningLevel", "actions", "collectionPlan"));
            if (levelInstanceToUpdate == null) {
                throw new EntityDoesNotExistsException("No Dunning Level Instance found with id : " + levelInstanceId);
            }

            // 1- Can not update the dunning level instance if :
            // status is DONE
            if (levelInstanceToUpdate.getLevelStatus() == DunningLevelInstanceStatusEnum.DONE) {
                throw new ActionForbiddenException("Cannot update a DONE dunningLevelInstance");
            }
            // dunningLevel.isReminderLevel is TRUE
            if (levelInstanceToUpdate.getDunningLevel().isReminder()) {
                throw new ActionForbiddenException("Can not create a new dunning level instance if dunningLevel.isReminderLevel is TRUE");
            }

            List<String> fields = new ArrayList<>();
            DunningCollectionPlan collectionPlan = levelInstanceToUpdate.getCollectionPlan();
            Integer newDaysOverdue = updateLevelInstanceInput.getDaysOverdue();
            if (newDaysOverdue != null) {
                // check daysOverdue
                checkDaysOverdue(collectionPlan, newDaysOverdue);
                if (newDaysOverdue != levelInstanceToUpdate.getDaysOverdue()) {
                    fields.add("daysOverdue");
                }
                levelInstanceToUpdate.setDaysOverdue(updateLevelInstanceInput.getDaysOverdue());

                // 2- set sequence
                Long minSequence = dunningLevelInstanceService.getMinSequenceByDaysOverdue(collectionPlan, newDaysOverdue);
                levelInstanceToUpdate.setSequence(minSequence.intValue());

                // 3- update dunningLevelInstances
                dunningLevelInstanceService.incrementSequecesGreaterThanDaysOverdue(collectionPlan, newDaysOverdue);
            }

            if (updateLevelInstanceInput.getLevelStatus() != null) {
                if (updateLevelInstanceInput.getLevelStatus() != levelInstanceToUpdate.getLevelStatus()) {
                    fields.add("levelStatus");
                }
                levelInstanceToUpdate.setLevelStatus(updateLevelInstanceInput.getLevelStatus());
            }

            dunningLevelInstanceService.update(levelInstanceToUpdate);

            if (updateLevelInstanceInput.getActions() != null) {
                fields.add("actions");
                for (DunningActionInstance action : levelInstanceToUpdate.getActions()) {
                    dunningActionInstanceService.remove(action);
                }
                createActions(levelInstanceToUpdate, updateLevelInstanceInput.getActions());
            }

            updateCollectionPlanActions(levelInstanceToUpdate);

            auditLogService.trackOperation("UPDATE DunningLevelInstance", new Date(), levelInstanceToUpdate.getCollectionPlan(), fields);
            return of(levelInstanceToUpdate);
        } catch (MeveoApiException e) {
            throw e;
        } catch (Exception e) {
            throw new MeveoApiException(e);
        }
    }

    public Optional<DunningActionInstance> addDunningActionInstance(DunningActionInstanceInput dunningActionInstanceInput) {
        DunningActionInstance dunningActionInstance = new DunningActionInstance();
        
        if(dunningActionInstanceInput.getCode() != null) {
            DunningActionInstance dunningActionInstanceExist = dunningActionInstanceService.findByCode(dunningActionInstanceInput.getCode());
            if(dunningActionInstanceExist != null) {
                throw new EntityAlreadyExistsException("Dunning Action Instance with code : " + dunningActionInstanceInput.getCode() + " already exist");
            }
        }

        if (dunningActionInstanceInput.getDunningLevelInstance() == null || dunningActionInstanceInput.getDunningLevelInstance().getId() == null) {
            throw new ActionForbiddenException("Attribut dunningLevelInstance is mandatory");
        }
        Long dunningLevelInstanceId = dunningActionInstanceInput.getDunningLevelInstance().getId();
        DunningLevelInstance dunningLevelInstance = dunningLevelInstanceService.findById(dunningLevelInstanceId, Arrays.asList("dunningLevel"));
        if (dunningLevelInstance == null) {
            throw new EntityDoesNotExistsException("No Dunning Level found with id : " + dunningLevelInstanceId);
        } else if (dunningLevelInstance.getDunningLevel().isEndOfDunningLevel() == true) {
            throw new ActionForbiddenException("Cant not add actions at the end of dunning level with id : " + dunningLevelInstanceId);
        } else {
            dunningActionInstance.setDunningLevelInstance(dunningLevelInstance);
        }

        if (dunningActionInstanceInput.getCollectionPlan() == null || dunningActionInstanceInput.getCollectionPlan().getId() == null) {
            throw new ActionForbiddenException("Attribut collectionPlan is mandatory");
        }
        Long collectionPlanId = dunningActionInstanceInput.getCollectionPlan().getId();
        DunningCollectionPlan collectionPlan = dunningCollectionPlanService.findById(collectionPlanId);
        if (collectionPlan == null) {
            throw new EntityDoesNotExistsException("No Dunning Collection Plan found with id : " + collectionPlanId);
        }
        dunningActionInstance.setCollectionPlan(collectionPlan);

        if (dunningActionInstanceInput.getDunningAction() != null && dunningActionInstanceInput.getDunningAction().getId() != null) {
            Long dunningActionId = dunningActionInstanceInput.getDunningAction().getId();
            DunningAction dunningAction = dunningActionService.findById(dunningActionId);
            if (dunningAction == null) {
                throw new EntityDoesNotExistsException("No Dunning action found with id : " + dunningActionId);
            }
            dunningActionInstance.setDunningAction(dunningAction);
        }

        if (dunningActionInstanceInput.getActionOwner() != null && dunningActionInstanceInput.getActionOwner().getId() != null) {
            Long dunningAgentId = dunningActionInstanceInput.getActionOwner().getId();
            DunningAgent dunningAgent = dunningAgentService.findById(dunningAgentId);
            if (dunningAgent == null) {
                throw new EntityDoesNotExistsException("No Dunning agent found with id : " + dunningAgentId);
            }
            dunningActionInstance.setActionOwner(dunningAgent);
        }

        dunningActionInstance.setCode(dunningActionInstanceInput.getCode());
        dunningActionInstance.setDescription(dunningActionInstanceInput.getDescription());
        dunningActionInstance.setActionType(dunningActionInstanceInput.getActionType());
        dunningActionInstance.setActionMode(dunningActionInstanceInput.getMode());
        if (dunningActionInstanceInput.getActionStatus() != null) {
            dunningActionInstance.setActionStatus(dunningActionInstanceInput.getActionStatus());
        }
        dunningActionInstance.setActionRestult(dunningActionInstanceInput.getActionRestult());

        dunningActionInstanceService.create(dunningActionInstance);

        auditLogService.trackOperation("ADD DunningActionInstance", new Date(), collectionPlan);
        return of(dunningActionInstance);
    }

    public Optional<DunningActionInstance> updateDunningActionInstance(DunningActionInstanceInput dunningActionInstanceInput, Long actionInstanceId) {

        try {
            DunningActionInstance dunningActionInstanceToUpdate = dunningActionInstanceService.findById(actionInstanceId, Arrays.asList("dunningLevelInstance"));
            if (dunningActionInstanceToUpdate == null) {
                throw new EntityDoesNotExistsException("No Dunning Action Instance found with id : " + actionInstanceId);
            }

            if (dunningActionInstanceToUpdate.getActionStatus() == DunningActionInstanceStatusEnum.DONE) {
                throw new ActionForbiddenException("Can not update a DONE dunningActionInstace");
            }

            List<String> fields = new ArrayList<>();

            if (dunningActionInstanceInput.getDunningLevelInstance() == null || dunningActionInstanceInput.getDunningLevelInstance().getId() == null) {
                throw new ActionForbiddenException("Attribut dunningLevelInstance is mandatory");
            }
            Long dunningLevelInstanceId = dunningActionInstanceInput.getDunningLevelInstance().getId();
            DunningLevelInstance dunningLevelInstance = dunningLevelInstanceService.findById(dunningLevelInstanceId, Arrays.asList("collectionPlan", "actions"));
            if (dunningLevelInstance == null) {
                throw new EntityDoesNotExistsException("No Dunning Level found with id : " + dunningLevelInstanceId);
            }
            if (dunningActionInstanceInput.getDunningLevelInstance().getId() != dunningActionInstanceToUpdate.getDunningLevelInstance().getId()) {
                fields.add("dunningLevelInstance");
            }
            dunningActionInstanceToUpdate.setDunningLevelInstance(dunningLevelInstance);

            if (dunningActionInstanceInput.getDunningAction() != null && dunningActionInstanceInput.getDunningAction().getId() != null) {
                Long dunningActionId = dunningActionInstanceInput.getDunningAction().getId();
                DunningAction dunningAction = dunningActionService.findById(dunningActionId);
                if (dunningAction == null) {
                    throw new EntityDoesNotExistsException("No Dunning action found with id : " + dunningActionId);
                }

                if (dunningActionInstanceInput.getDunningAction().getId() != dunningActionInstanceToUpdate.getDunningAction().getId()) {
                    fields.add("dunningAction");
                }
                dunningActionInstanceToUpdate.setDunningAction(dunningAction);
            }

            if (dunningActionInstanceInput.getActionOwner() != null && dunningActionInstanceInput.getActionOwner().getId() != null) {
                Long dunningAgentId = dunningActionInstanceInput.getActionOwner().getId();
                DunningAgent dunningAgent = dunningAgentService.findById(dunningAgentId);
                if (dunningAgent == null) {
                    throw new EntityDoesNotExistsException("No Dunning agent found with id : " + dunningAgentId);
                }

                if (dunningActionInstanceInput.getActionOwner().getId() != dunningActionInstanceToUpdate.getActionOwner().getId()) {
                    fields.add("actionOwner");
                }
                dunningActionInstanceToUpdate.setActionOwner(dunningAgent);
            }

            if (dunningActionInstanceInput.getCode() != null) {
                if (dunningActionInstanceInput.getCode().equals(dunningActionInstanceToUpdate.getCode())) {
                    fields.add("code");
                }
                dunningActionInstanceToUpdate.setCode(dunningActionInstanceInput.getCode());
            }
            if (dunningActionInstanceInput.getDescription() != null) {
                if (dunningActionInstanceInput.getDescription().equals(dunningActionInstanceToUpdate.getDescription())) {
                    fields.add("description");
                }
                dunningActionInstanceToUpdate.setDescription(dunningActionInstanceInput.getDescription());
            }
            if (dunningActionInstanceInput.getActionType() != null) {
                if (dunningActionInstanceInput.getActionType() != dunningActionInstanceToUpdate.getActionType()) {
                    fields.add("actionType");
                }
                dunningActionInstanceToUpdate.setActionType(dunningActionInstanceInput.getActionType());
            }
            if (dunningActionInstanceInput.getMode() != null) {
                if (dunningActionInstanceInput.getMode() != dunningActionInstanceToUpdate.getActionMode()) {
                    fields.add("actionMode");
                }
                dunningActionInstanceToUpdate.setActionMode(dunningActionInstanceInput.getMode());
            }
            if (dunningActionInstanceInput.getActionStatus() != null) {
                if (dunningActionInstanceInput.getActionStatus() != dunningActionInstanceToUpdate.getActionStatus()) {
                    fields.add("actionStatus");
                }
                dunningActionInstanceToUpdate.setActionStatus(dunningActionInstanceInput.getActionStatus());

                // 2- If the DunningActionInstance status is changed to DONE:
                if (dunningActionInstanceInput.getActionStatus() == DunningActionInstanceStatusEnum.DONE) {

                    List<DunningActionInstance> actions = dunningLevelInstance.getActions();
                    actions.removeIf(a -> a.getId() == dunningActionInstanceToUpdate.getId());

                    // check if all actions of the dunningLevelInstance are DONE:
                    boolean remainingActionsAreDone = true;
                    for (DunningActionInstance action : actions) {
                        if (action.getActionStatus() != DunningActionInstanceStatusEnum.DONE) {
                            remainingActionsAreDone = false;
                            break;
                        }
                    }
                    if (remainingActionsAreDone) {
                        // Update the dunningLevelInstance status also to
                        dunningLevelInstance.setLevelStatus(DunningLevelInstanceStatusEnum.DONE);
                        // Update DunningCollectionPlan : currentDunningLevelSequence / lastAction / lastActionDate / nextAction /nextActionDate
                        updateCollectionPlanActions(dunningLevelInstance);
                    } else {
                        dunningLevelInstance.setLevelStatus(DunningLevelInstanceStatusEnum.IN_PROGRESS);
                    }
                    dunningLevelInstanceService.update(dunningLevelInstance);
                }
            }

            if (dunningActionInstanceInput.getActionRestult() != null) {
                if (dunningActionInstanceInput.getActionRestult().equals(dunningActionInstanceToUpdate.getActionRestult())) {
                    fields.add("actionRestult");
                }
                dunningActionInstanceToUpdate.setActionRestult(dunningActionInstanceInput.getActionRestult());
            }

            dunningActionInstanceService.update(dunningActionInstanceToUpdate);

            auditLogService.trackOperation("UPDATE DunningActionInstance", new Date(), dunningActionInstanceToUpdate.getCollectionPlan(), fields);
            return of(dunningActionInstanceToUpdate);
        } catch (MeveoApiException e) {
            throw e;
        } catch (Exception e) {
            throw new MeveoApiException(e);
        }
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    private void createActions(DunningLevelInstance dunningLevelInstance, List<DunningActionInstanceInput> actionInstanceInputs) {
        List<DunningActionInstance> actions = new ArrayList<>();

        for (DunningActionInstanceInput actionInput : actionInstanceInputs) {
            DunningActionInstance dunningActionInstance = new DunningActionInstance();
            Long dunningActionId = actionInput.getDunningAction().getId();
            DunningAction dunningAction = dunningActionService.findById(dunningActionId);
            if (dunningAction == null) {
                throw new EntityDoesNotExistsException("No Dunning action found with id : " + dunningActionId);
            }

            if (actionInput.getCode() != null) {
                dunningActionInstance.setCode(actionInput.getCode());
            } else {
                dunningActionInstance.setCode(dunningAction.getCode());
            }

            if (actionInput.getDescription() != null) {
                dunningActionInstance.setDescription(actionInput.getDescription());
            } else {
                dunningActionInstance.setDescription(dunningAction.getDescription());
            }

            if (actionInput.getActionType() != null) {
                dunningActionInstance.setActionType(actionInput.getActionType());
            } else {
                dunningActionInstance.setActionType(dunningAction.getActionType());
            }

            if (actionInput.getMode() != null) {
                dunningActionInstance.setActionMode(actionInput.getMode());
            } else {
                dunningActionInstance.setActionMode(dunningAction.getActionMode());
            }

            if (actionInput.getActionOwner() != null && actionInput.getActionOwner().getId() != null) {
                Long dunningAgentId = actionInput.getActionOwner().getId();
                DunningAgent dunningAgent = dunningAgentService.findById(dunningAgentId);
                if (dunningAgent == null) {
                    throw new EntityDoesNotExistsException("No Dunning agent found with id : " + dunningAgentId);
                }
                dunningActionInstance.setActionOwner(dunningAgent);
            } else {
                dunningActionInstance.setActionOwner(dunningAction.getAssignedTo());
            }

            dunningActionInstance.setActionRestult(actionInput.getActionRestult());
            if (dunningLevelInstance.getLevelStatus() == DunningLevelInstanceStatusEnum.DONE) {
                dunningActionInstance.setActionStatus(DunningActionInstanceStatusEnum.DONE);
            } else {
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
                    collectionPlan.setLastAction(
                        lastLevelActions.stream().sorted(Comparator.comparing(a -> a.getAuditable().getLastModified(), Comparator.reverseOrder())).findFirst().get().getCode());

                    collectionPlan.setLastActionDate(new Date());
                }

                DunningLevelInstance nextLevelInstance = dunningLevelInstanceService.findByCurrentLevelSequence(collectionPlan);
                String nextLevelAction = null;
                if (nextLevelInstance != null && nextLevelInstance.getActions() != null && !nextLevelInstance.getActions().isEmpty()) {
                    for (DunningActionInstance nextActionInstance : nextLevelInstance.getActions()) {
                        if (nextActionInstance.getActionMode() == ActionModeEnum.AUTOMATIC) {
                            nextLevelAction = nextActionInstance.getCode();
                            break;
                        }
                    }
                    if (nextLevelAction == null) {
                        nextLevelAction = nextLevelInstance.getActions().get(0).getCode();
                    }

                    collectionPlan.setNextAction(nextLevelAction);
                    collectionPlan
                        .setNextActionDate(DateUtils.addDaysToDate(collectionPlan.getStartDate(), nextLevelInstance.getDaysOverdue() + collectionPlan.getPauseDuration()));
                }
            } else {
                collectionPlan.setNextAction(null);
                collectionPlan.setNextActionDate(null);
                collectionPlan.setStatus(dunningCollectionPlanStatusService.findByStatus(DunningCollectionPlanStatusEnum.FAILED));
            }

            dunningCollectionPlanService.update(collectionPlan);
        }
    }

    private void checkDaysOverdue(DunningCollectionPlan collectionPlan, Integer newDaysOverdue) {
        // daysOverdue is already exist at one of dunningLevelInstance of the current collection Plan
        boolean daysOverdueIsAlreadyExist = dunningLevelInstanceService.checkDaysOverdueIsAlreadyExist(collectionPlan, newDaysOverdue);
        if (daysOverdueIsAlreadyExist) {
            throw new ActionForbiddenException("DaysOverdue is already exist at one of dunningLevelInstance of the current collection Plan");
        }

        DunningLevelInstance lastLevelInstance = dunningLevelInstanceService.findLastLevelInstance(collectionPlan);
        if (lastLevelInstance != null) {
            // the daysOverdue is greater than the endLevel daysOverdue of the current collectionPlan
            if (newDaysOverdue > lastLevelInstance.getDaysOverdue()) {
                throw new ActionForbiddenException("The sequence is greater than the endLevel");
            }
        }

        DunningLevelInstance currentLevelInstance = dunningLevelInstanceService.findByCurrentLevelSequence(collectionPlan);
        // the daysOverdue is less than the current dunningLevelInstance daysOverdue
        if (currentLevelInstance != null && newDaysOverdue < currentLevelInstance.getDaysOverdue()) {
            throw new ActionForbiddenException("The daysOverdue is less than the current dunningLevelInstance daysOverdue");
        }
    }
}