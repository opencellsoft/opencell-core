package org.meveo.apiv2.dunning.impl;

import static java.util.Arrays.asList;
import static org.meveo.apiv2.ordering.common.LinkGenerator.*;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.apiv2.dunning.*;
import org.meveo.apiv2.dunning.resource.DunningPolicyResource;
import org.meveo.apiv2.dunning.service.DunningPolicyApiService;
import org.meveo.apiv2.dunning.service.DunningPolicyLevelApiService;
import org.meveo.apiv2.generic.common.LinkGenerator;
import org.meveo.apiv2.models.Resource;
import org.meveo.apiv2.report.ImmutableSuccessResponse;
import org.meveo.apiv2.report.SuccessResponse;
import org.meveo.model.dunning.DunningPolicyLevel;
import org.meveo.service.payments.impl.DunningPolicyLevelService;
import org.meveo.service.payments.impl.DunningPolicyService;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DunningPolicyResourceImpl implements DunningPolicyResource {

    @Inject
    private DunningPolicyApiService dunningPolicyApiService;

    @Inject
    private DunningPolicyLevelApiService policyLevelApiService;

    @Inject
    private DunningPolicyLevelService dunningPolicyLevelService;

    @Inject
    private DunningPolicyService dunningPolicyService;

    private final DunningPolicyMapper mapper = new DunningPolicyMapper();

    private final DunningPolicyLevelMapper policyLevelMapper = new DunningPolicyLevelMapper();

    private DunningPolicyRuleMapper dunningPolicyRuleMapper = new DunningPolicyRuleMapper();

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
        org.meveo.model.dunning.DunningPolicy entity = mapper.toEntity(dunningPolicy);
        org.meveo.model.dunning.DunningPolicy savedEntity = dunningPolicyApiService.create(entity);
        int totalDunningLevels = 0;
        int countReminderLevels = 0;
        int countEndOfDunningLevel = 0;
        int highestSequence = (dunningPolicy.getDunningPolicyLevels() != null
                && !dunningPolicy.getDunningPolicyLevels().isEmpty())
                ? dunningPolicy.getDunningPolicyLevels().get(0).getSequence() : 0;
        for (org.meveo.apiv2.dunning.DunningPolicyLevel dunningPolicyLevel : dunningPolicy.getDunningPolicyLevels()) {
            DunningPolicyLevel dunningPolicyLevelEntity = policyLevelMapper.toEntity(dunningPolicyLevel);
            dunningPolicyLevelEntity.setDunningPolicy(savedEntity);
            dunningPolicyApiService.refreshPolicyLevel(dunningPolicyLevelEntity);
            if (!dunningPolicyLevelEntity.getDunningLevel().isReminder()) {
                totalDunningLevels++;
            } else {
                countReminderLevels++;
            }
            if (dunningPolicyLevelEntity.getDunningLevel().isEndOfDunningLevel()) {
                if (dunningPolicyLevelEntity.getSequence() < highestSequence) {
                    throw new BadRequestException("End of dunning level sequence must be the highest");
                }
                highestSequence = dunningPolicyLevelEntity.getSequence();
                countEndOfDunningLevel++;
            }
            policyLevelApiService.create(dunningPolicyLevelEntity);
        }
        if(countReminderLevels == 0 || totalDunningLevels == 0 || countEndOfDunningLevel > 1) {
            dunningPolicyService.remove(savedEntity);
        }
        dunningPolicyApiService.validateLevelsNumber(countReminderLevels, countEndOfDunningLevel, totalDunningLevels);
        savedEntity.setTotalDunningLevels(totalDunningLevels);
        dunningPolicyApiService.updateTotalLevels(savedEntity);
        ActionStatus actionStatus = new ActionStatus();
        actionStatus.setStatus(ActionStatusEnum.SUCCESS);
        actionStatus.setMessage("Entity successfully created");
        actionStatus.setEntityId(entity.getId());
        return Response.ok(LinkGenerator.getUriBuilderFromResource(DunningPolicyResource.class, dunningPolicy.getId())
                .build())
                .entity(actionStatus)
                .build();
    }

    @Override
    public Response update(Long dunningPolicyId, DunningPolicyInput dunningPolicy) {
        org.meveo.model.dunning.DunningPolicy entity = dunningPolicyService.findById(dunningPolicyId, asList("dunningLevels"));
        if (entity == null) {
            throw new NotFoundException("Dunning policy with id " + dunningPolicyId + " does not exits");
        }
        StringBuilder updatedField = new StringBuilder();
        List<DunningPolicyLevel> dunningPolicyLevelList = new ArrayList<>();
        if (dunningPolicy.getDunningPolicyLevels() != null && !dunningPolicy.getDunningPolicyLevels().isEmpty()) {
            updatedField.append("dunningLevels;");
            entity.getDunningLevels().clear();
            for (Resource resource : dunningPolicy.getDunningPolicyLevels()) {
                DunningPolicyLevel level = dunningPolicyLevelService.findById(resource.getId());
                if (level != null) {
                    level.setDunningPolicy(entity);
                    dunningPolicyLevelList.add(level);
                }
            }
        }
        if (!dunningPolicyLevelList.isEmpty()) {
            entity.setDunningLevels(dunningPolicyLevelList);
        }
        org.meveo.model.dunning.DunningPolicy policy =
                dunningPolicyApiService.update(dunningPolicyId, mapper.toUpdateEntity(dunningPolicy, entity, updatedField)).get();
        dunningPolicyApiService.trackOperation("update", new Date(), updatedField.toString(), policy.getPolicyName());
        ActionStatus actionStatus = new ActionStatus();
        actionStatus.setStatus(ActionStatusEnum.SUCCESS);
        actionStatus.setMessage("Entity successfully updated");
        actionStatus.setEntityId(entity.getId());
        return Response
                .ok(getUriBuilderFromResource(DunningPolicyResource.class, entity.getId()).build())
                .entity(actionStatus)
                .build();
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

    public Response removePolicyRule(Long policyRuleID) {
        org.meveo.model.dunning.DunningPolicyRule dunningPolicyRule =
                dunningPolicyApiService.removePolicyRule(policyRuleID)
                        .orElseThrow(() -> new NotFoundException("Policy rule with id " + policyRuleID + " does not exists"));
        return Response.ok(ImmutableSuccessResponse.builder()
                .status("SUCCESS")
                .message("Policy rule with id " + dunningPolicyRule.getId() + " is successfully deleted")
                .build()).build();
    }

    @Override
    public Response addPolicyRule(Long dunningPolicyId, DunningPolicyRules policyRules) {
        org.meveo.model.dunning.DunningPolicy dunningPolicy = dunningPolicyApiService.findById(dunningPolicyId)
                .orElseThrow(() -> new NotFoundException("Dunning policy with id " + dunningPolicyId + "does not exits"));
        validateResource(policyRules);
        dunningPolicyApiService.removePolicyRuleWithPolicyId(dunningPolicy.getId());
        for (PolicyRule policyRule : policyRules.getPolicyRules()) {
            org.meveo.model.dunning.DunningPolicyRule dunningPolicyRuleEntity =
                    dunningPolicyRuleMapper.toEntity(policyRule);
            dunningPolicyRuleEntity.setDunningPolicy(dunningPolicy);
            dunningPolicyApiService.addPolicyRule(dunningPolicyRuleEntity, policyRule.getRuleLines());
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

    private void validateResource(DunningPolicyRules policyRules) {
        if(policyRules.getPolicyRules() != null && !policyRules.getPolicyRules().isEmpty()) {
            if(policyRules.getPolicyRules().get(0).getRuleJoint() != null) {
                throw new BadRequestException("First policy rule should have a null ruleJoint");
            }
            if (policyRules.getPolicyRules().get(0).getRuleLines() != null
                    && !policyRules.getPolicyRules().get(0).getRuleLines().isEmpty()
                    && policyRules.getPolicyRules().get(0).getRuleLines().get(0).getRuleLineJoint() != null) {
                throw new BadRequestException("First policy rule line should have a null ruleJoint");
            }
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