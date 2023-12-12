package org.meveo.apiv2.dunning.impl;

import static java.util.Arrays.asList;
import static java.util.Comparator.comparing;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.meveo.apiv2.ordering.common.LinkGenerator.getUriBuilderFromResource;

import java.util.*;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.apiv2.dunning.*;
import org.meveo.apiv2.dunning.resource.DunningPolicyResource;
import org.meveo.apiv2.dunning.service.DunningPolicyApiService;
import org.meveo.apiv2.dunning.service.DunningPolicyLevelApiService;
import org.meveo.apiv2.generic.common.LinkGenerator;
import org.meveo.apiv2.report.ImmutableSuccessResponse;
import org.meveo.apiv2.report.SuccessResponse;
import org.meveo.model.dunning.DunningPolicyLevel;
import org.meveo.model.dunning.DunningPolicyRule;
import org.meveo.service.audit.logging.AuditLogService;
import org.meveo.service.payments.impl.DunningLevelService;
import org.meveo.service.payments.impl.DunningPolicyService;
import org.meveo.service.payments.impl.DunningSettingsService;

@Interceptors({ WsRestApiInterceptor.class })
public class DunningPolicyResourceImpl implements DunningPolicyResource {

    @Inject
    private DunningPolicyApiService dunningPolicyApiService;

    @Inject
    private DunningPolicyLevelApiService policyLevelApiService;

    @Inject
    private DunningLevelService levelService;

    @Inject
    private DunningPolicyService dunningPolicyService;

    @Inject
    private DunningSettingsService dunningSettingsService;
    
    @Inject
    private AuditLogService auditLogService;

    private final DunningPolicyMapper mapper = new DunningPolicyMapper();

    private final DunningPolicyLevelMapper policyLevelMapper = new DunningPolicyLevelMapper();

    private final DunningPolicyRuleMapper dunningPolicyRuleMapper = new DunningPolicyRuleMapper();

    @Override
    public Response create(DunningPolicy dunningPolicy) {
        if (dunningPolicy.getPolicyName() == null) {
            return Response
                    .status(412, "missing param : policy name")
                    .entity("Policy name is missing")
                    .build();
        }
        if (dunningPolicy.getPolicyDescription() == null) {
            return Response
                    .status(412, "missing param : policy name")
                    .entity("Policy name is missing")
                    .build();
        }
        if (dunningPolicy.getDunningPolicyLevels() == null || dunningPolicy.getDunningPolicyLevels().isEmpty()) {
            throw new BadRequestException("Policy levels are missing");
        }

        // Check the maximum number of dunning levels per dunning policy
        Integer maxNumberOfDunningLevelsByDunningPolicy = dunningSettingsService.getMaxNumberOfDunningLevels();
        if(maxNumberOfDunningLevelsByDunningPolicy != null && dunningPolicy.getDunningPolicyLevels().size() > maxNumberOfDunningLevelsByDunningPolicy) {
            throw new NotFoundException("The maximum number of dunning levels per policy is exceeded - The maximum number is " + maxNumberOfDunningLevelsByDunningPolicy);
        }

        org.meveo.model.dunning.DunningPolicy savedEntity = dunningPolicyApiService.create(mapper.toEntity(dunningPolicy));
        int totalDunningLevels = 0;
        int countReminderLevels = 0;
        int countEndOfDunningLevel = 0;
        int endOfLevelDayOverDue = -1;
        int sequence = 0;
        try {
            List<DunningPolicyLevel> dunningPolicyLevels = new ArrayList<>();
            for (org.meveo.apiv2.dunning.DunningPolicyLevel dunningPolicyLevel : dunningPolicy.getDunningPolicyLevels()) {
                DunningPolicyLevel dunningPolicyLevelEntity = policyLevelMapper.toEntity(dunningPolicyLevel);
                dunningPolicyLevelEntity.setDunningPolicy(savedEntity);
                dunningPolicyApiService.refreshPolicyLevel(dunningPolicyLevelEntity);
                totalDunningLevels++;
                if (dunningPolicyLevelEntity.getDunningLevel().isReminder()) {
                    countReminderLevels++;
                }
                if (dunningPolicyLevelEntity.getDunningLevel().isEndOfDunningLevel()) {
                    endOfLevelDayOverDue = dunningPolicyLevelEntity.getDunningLevel().getDaysOverdue();
                    countEndOfDunningLevel++;
                }
                dunningPolicyLevels.add(dunningPolicyLevelEntity);
            }
            if(totalDunningLevels == 0 || countEndOfDunningLevel > 1) {
                dunningPolicyService.remove(savedEntity);
            }
            dunningPolicyApiService.validateLevelsNumber(countReminderLevels, countEndOfDunningLevel, totalDunningLevels);
            dunningPolicyApiService.validateLevels(dunningPolicyLevels, endOfLevelDayOverDue);
            dunningPolicyLevels.sort(comparing(level -> level.getDunningLevel().getDaysOverdue()));
            for (DunningPolicyLevel level : dunningPolicyLevels) {
                level.setSequence(sequence++);
                policyLevelApiService.create(level);
            }
            savedEntity.setTotalDunningLevels(totalDunningLevels);
            dunningPolicyApiService.updateTotalLevels(savedEntity);
            ActionStatus actionStatus = new ActionStatus();
            actionStatus.setStatus(ActionStatusEnum.SUCCESS);
            actionStatus.setMessage("Entity successfully created");
            actionStatus.setEntityId(savedEntity.getId());
            return Response.created(LinkGenerator.getUriBuilderFromResource(DunningPolicyResource.class, dunningPolicy.getId())
                    .build())
                    .entity(actionStatus)
                    .build();
        } catch (Exception exception) {
            dunningPolicyService.remove(savedEntity);
            throw new BadRequestException(exception.getMessage());
        }
    }

    @Override
    public Response update(Long dunningPolicyId, DunningPolicyInput dunningPolicyInput) {
        org.meveo.model.dunning.DunningPolicy dunningPolicyEntity =
                dunningPolicyService.findById(dunningPolicyId, asList("dunningLevels", "minBalanceTriggerCurrency"));
        if (dunningPolicyEntity == null) {
            throw new NotFoundException("Dunning policy with id " + dunningPolicyId + " does not exits");
        }

        // Check the maximum number of dunning levels per dunning policy
        Integer maxNumberOfDunningLevelsByDunningPolicy = dunningSettingsService.getMaxNumberOfDunningLevels();
        if(dunningPolicyInput.getDunningPolicyLevels().size() > maxNumberOfDunningLevelsByDunningPolicy) {
            throw new NotFoundException("The maximum number of dunning levels per policy is exceeded - The maximum number is " + maxNumberOfDunningLevelsByDunningPolicy);
        }

        List<String> updatedFields = new ArrayList<>();
        if (checkIfPolicyLevelsAreChanged(dunningPolicyInput.getDunningPolicyLevels(), dunningPolicyEntity.getDunningLevels())) {
            dunningPolicyEntity.getDunningLevels().clear();
            List<DunningPolicyLevel> dunningPolicyLevelList = new ArrayList<>();
            for (org.meveo.apiv2.dunning.DunningPolicyLevel resource : dunningPolicyInput.getDunningPolicyLevels()) {
                ofNullable(resource.getDunningLevelId()).orElseThrow(() -> new BadRequestException("Dunning level id is required"));
                org.meveo.model.dunning.DunningLevel level = levelService.findById(resource.getDunningLevelId());
                if (level != null) {
                    DunningPolicyLevel policyLevel = new DunningPolicyLevel();
                    policyLevel.setDunningLevel(level);
                    dunningPolicyLevelList.add(policyLevel);
                } else {
                    throw new NotFoundException("Dunning level with id " + resource.getDunningLevelId() + " does not exits");
                }
            }
            updatedFields.add("dunningPolicyLevels");
            dunningPolicyEntity.setDunningLevels(dunningPolicyLevelList);
        }
        
        String operationType = "update";
        
        if(dunningPolicyInput.isActivePolicy() != null
                && dunningPolicyEntity.getIsActivePolicy() != dunningPolicyInput.isActivePolicy()) {
        	operationType = dunningPolicyInput.isActivePolicy() ? "activation" : "deactivation";
        }
        
        org.meveo.model.dunning.DunningPolicy policy =
                dunningPolicyApiService.update(dunningPolicyId,
                        mapper.toUpdateEntity(dunningPolicyInput, dunningPolicyEntity, updatedFields)).get();

        String origin = (policy != null) ? policy.getPolicyName() : "";
        auditLogService.trackOperation(operationType, new Date(), policy, origin, updatedFields);
        
        ActionStatus actionStatus = new ActionStatus();
        actionStatus.setStatus(ActionStatusEnum.SUCCESS);
        actionStatus.setMessage("Entity successfully updated");
        actionStatus.setEntityId(policy.getId());
        actionStatus.setEntityCode(policy.getPolicyName());
        return Response
                .ok(getUriBuilderFromResource(DunningPolicyResource.class, dunningPolicyEntity.getId()).build())
                .entity(actionStatus)
                .build();
    }
    
    private boolean checkIfPolicyLevelsAreChanged(List<org.meveo.apiv2.dunning.DunningPolicyLevel> policyLevelResources,
                                                  List<DunningPolicyLevel> policyLevelEntities) {
        
        if (policyLevelResources == null) {
            return false;
        }
        if (policyLevelResources.size() == policyLevelEntities.size()) {
            List<Long> resourceIds = policyLevelResources.stream()
                    .map(org.meveo.apiv2.dunning.DunningPolicyLevel::getDunningLevelId)
                    .collect(toList());
            List<Long> entityIds = policyLevelEntities.stream()
                    .map(DunningPolicyLevel::getDunningLevel)
                    .map(org.meveo.model.dunning.DunningLevel::getId)
                    .collect(toList());
            
            for (Long resourceId : resourceIds) {
                if (!entityIds.contains(resourceId)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    private boolean checkIfDunningRulesAreChanged(DunningPolicyRules dunningPolicyRulesJson, List<DunningPolicyRule> listDunningPolicyRulesDB) {
        if(listDunningPolicyRulesDB!=null && dunningPolicyRulesJson.getPolicyRules() != null) {
            if (listDunningPolicyRulesDB.size() != dunningPolicyRulesJson.getPolicyRules().size()){
                return true;
            }
            for(int index = 0; index < listDunningPolicyRulesDB.size(); index++) {
                List<org.meveo.model.dunning.DunningPolicyRuleLine> listDunningPolicyRuleLinesDB = dunningPolicyApiService.getPolicyRuleLineWithDunningPolicyRuled(listDunningPolicyRulesDB.get(index).getId());
                String ruleJsonRuleJoint = dunningPolicyRulesJson.getPolicyRules().get(index).getRuleJoint() != null ? dunningPolicyRulesJson.getPolicyRules().get(index).getRuleJoint().toString() : "null";
                String ruleDBRuleJoint = listDunningPolicyRulesDB.get(index).getRuleJoint() != null ? listDunningPolicyRulesDB.get(index).getRuleJoint() : "null";
                if (!ruleJsonRuleJoint.equalsIgnoreCase(ruleDBRuleJoint)) {
                    return true;
                }
                if(listDunningPolicyRuleLinesDB!=null && dunningPolicyRulesJson.getPolicyRules().get(index).getRuleLines() != null) {
                    if (listDunningPolicyRuleLinesDB.size() != dunningPolicyRulesJson.getPolicyRules().get(index).getRuleLines().size()){
                        return true;
                    }
                    for(int subIndex = 0; subIndex < listDunningPolicyRuleLinesDB.size(); subIndex++) {
                        if (subIndex < listDunningPolicyRuleLinesDB.size()) {
                            DunningPolicyRuleLine ruleJson = dunningPolicyRulesJson.getPolicyRules().get(index).getRuleLines().get(subIndex);
                            String ruleDBPolicyConditionOperator = listDunningPolicyRuleLinesDB.get(subIndex).getPolicyConditionOperator();
                            String ruleDBPolicyConditionTarget = listDunningPolicyRuleLinesDB.get(subIndex).getPolicyConditionTarget();
                            String ruleDBPolicyConditionTargetValue = listDunningPolicyRuleLinesDB.get(subIndex).getPolicyConditionTargetValue();
                            String ruleDBRuleLineJoint = listDunningPolicyRuleLinesDB.get(subIndex).getRuleLineJoint() != null ? listDunningPolicyRuleLinesDB.get(subIndex).getRuleLineJoint() : "null";
                           
                            String ruleJsonPolicyConditionOperator = ruleJson.getPolicyConditionOperator().toString();
                            String ruleJsonPolicyConditionTarget = ruleJson.getPolicyConditionTarget().toString();
                            String ruleJsonPolicyConditionTargetValue = ruleJson.getPolicyConditionTargetValue();
                            String ruleJsonRuleLineJoint = ruleJson.getRuleLineJoint() != null ? ruleJson.getRuleLineJoint().toString() : "null";
                            if (!ruleDBPolicyConditionOperator.equalsIgnoreCase(ruleJsonPolicyConditionOperator) 
                                    || !ruleDBPolicyConditionTarget.equalsIgnoreCase(ruleJsonPolicyConditionTarget) 
                                    || !ruleDBPolicyConditionTargetValue.equalsIgnoreCase(ruleJsonPolicyConditionTargetValue) 
                                    || !ruleDBRuleLineJoint.equalsIgnoreCase(ruleJsonRuleLineJoint)
                                    ) {
                                return true;
                            }
                        }
                    }
                }

            }
        }
        return false;
    }
    
    @Override
    public Response delete(Long dunningPolicyId) {
        org.meveo.model.dunning.DunningPolicy dunningPolicy = dunningPolicyApiService.delete(dunningPolicyId)
                .orElseThrow(() -> new NotFoundException("Dunning policy with id " + dunningPolicyId + " does not exists"));
        return Response.ok(ImmutableSuccessResponse.builder()
                .status("SUCCESS")
                .message("Dunning policy with name " + dunningPolicy.getPolicyName() + " is successfully deleted")
                .build()).build();
    }

    @Override
    public Response findByName(String dunningPolicyName) {
        return Response
                .ok(toResourceOrderWithLink(mapper.toResource(dunningPolicyApiService.findByName(dunningPolicyName).get())))
                .build();
    }

    @Override
    public Response archive(Long dunningPolicyId) {
        org.meveo.model.dunning.DunningPolicy entity = dunningPolicyApiService.findById(dunningPolicyId)
                .orElseThrow(() -> new NotFoundException("Dunning policy with id " + dunningPolicyId + " does not exits"));
        return Response
                .ok(getUriBuilderFromResource(DunningPolicyResource.class, entity.getId()).build())
                .entity(mapper.toResource(dunningPolicyApiService.archiveDunningPolicy(entity).get()))
                .build();
    }

    public Response deactivate(Map<String, Set<Long>> dunningPolicyIds){
        int affectedDunningPolicies = dunningPolicyService.deactivatePoliciesByIds(dunningPolicyIds.getOrDefault("dunningPolicyIds", Collections.EMPTY_SET));
        return Response
                .ok(ImmutableSuccessResponse.builder()
                .status("SUCCESS")
                .message(affectedDunningPolicies + " Dunning Policies has successfully deactivated")
                .build())
                .build();
    }

    public Response removePolicyRule(Long policyRuleID) {
        org.meveo.model.dunning.DunningPolicyRule dunningPolicyRule =
                dunningPolicyApiService.removePolicyRule(policyRuleID)
                        .orElseThrow(() -> new NotFoundException("Policy rule with id " + policyRuleID + " does not exists"));
        org.meveo.model.dunning.DunningPolicy dunningPolicy = dunningPolicyRule.getDunningPolicy();
        List<String> updatedFields = new ArrayList<>();
        String operationType = "removePolicyRule";
        updatedFields.add("PolicyRule");        
        String origine = (dunningPolicy != null) ? dunningPolicy.getPolicyName() : "";
        auditLogService.trackOperation(operationType, new Date(), dunningPolicy, origine, updatedFields);
        return Response.ok(ImmutableSuccessResponse.builder()
                .status("SUCCESS")
                .message("Policy rule with id " + dunningPolicyRule.getId() + " is successfully deleted")
                .build()).build();
    }

    @Override
    public Response addPolicyRule(Long dunningPolicyId, DunningPolicyRules policyRules) {
        List<String> updatedFields = new ArrayList<>();
        updatedFields.add("PolicyRule");
        String operationType = "updatePolicyRule";
        org.meveo.model.dunning.DunningPolicy dunningPolicy = dunningPolicyApiService.findById(dunningPolicyId)
                .orElseThrow(() -> new NotFoundException("Dunning policy with id " + dunningPolicyId + "does not exits"));
        String origine = (dunningPolicy != null) ? dunningPolicy.getPolicyName() : "";
        
        List<DunningPolicyRule> listDunningPolicyRules = dunningPolicyApiService.getPolicyRuleWithPolicyId(dunningPolicy.getId());
        if(policyRules.getPolicyRules() == null || policyRules.getPolicyRules().isEmpty()) {
            if(checkIfDunningRulesAreChanged(policyRules, listDunningPolicyRules)) {
                dunningPolicyApiService.removePolicyRuleWithPolicyId(dunningPolicy.getId());
                auditLogService.trackOperation(operationType, new Date(), dunningPolicy, origine, updatedFields);
            }            
        }
        else {
            if(checkIfDunningRulesAreChanged(policyRules, listDunningPolicyRules)) {
                validatePolicyRule(policyRules.getPolicyRules());
                dunningPolicyApiService.removePolicyRuleWithPolicyId(dunningPolicy.getId());
                for (PolicyRule policyRule : policyRules.getPolicyRules()) {
                    org.meveo.model.dunning.DunningPolicyRule dunningPolicyRuleEntity =
                            dunningPolicyRuleMapper.toEntity(policyRule);
                    dunningPolicyRuleEntity.setDunningPolicy(dunningPolicy);
                    dunningPolicyApiService.addPolicyRule(dunningPolicyRuleEntity, policyRule.getRuleLines());
                }
                auditLogService.trackOperation(operationType, new Date(), dunningPolicy, origine, updatedFields);
            }
        }

        SuccessResponse response = ImmutableSuccessResponse.builder()
                .status("SUCCESS")
                .message("Policy rules successfully added")
                .build();        
        return Response.ok(LinkGenerator.getUriBuilderFromResource(DunningPolicyResource.class, policyRules.getId())
                .build())
                .entity(response)
                .build();
    }

    private void validatePolicyRule(List<PolicyRule> policyRules) {
        if(policyRules != null && !policyRules.isEmpty()) {
            for(int index = 0; index < policyRules.size(); index++) {
                if(index > 0 && policyRules.get(index).getRuleJoint() == null) {
                    throw new BadRequestException("Policy rule ruleJoint should not be null");
                }
                if(index == 0 && policyRules.get(index).getRuleJoint() != null) {
                    throw new BadRequestException("First policy rule should have a null ruleJoint");
                }
                List<DunningPolicyRuleLine> ruleLines = policyRules.get(index).getRuleLines();
                if(ruleLines != null && !ruleLines.isEmpty()) {
                    for(int subIndex = 0; subIndex < ruleLines.size(); subIndex++) {
                        if(subIndex > 0 && ruleLines.get(subIndex).getRuleLineJoint() == null) {
                            throw new BadRequestException("Policy rule line ruleJoint should not be null");
                        }
                        if(subIndex == 0 && ruleLines.get(subIndex).getRuleLineJoint() != null) {
                            throw new BadRequestException("First policy rule line should have a null ruleJoint");
                        }
                    }
                } else {
                    throw new BadRequestException("Policy rule lines are null or empty");
                }
            }
        } else {
            throw new BadRequestException("Policy rules are null or empty");
        }
    }

    private org.meveo.apiv2.dunning.DunningPolicy toResourceOrderWithLink(org.meveo.apiv2.dunning.DunningPolicy dunningPolicy) {
        return ImmutableDunningPolicy.copyOf(dunningPolicy)
                .withLinks(
                        new LinkGenerator.SelfLinkGenerator(DunningPolicyResource.class)
                                .withId(dunningPolicy.getId())
                                .withGetAction().withPostAction().withPutAction().withPatchAction().withDeleteAction()
                                .build()
                );
    }
}