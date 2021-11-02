package org.meveo.apiv2.dunning.impl;

import static org.meveo.apiv2.ordering.common.LinkGenerator.*;
import static org.meveo.model.dunning.DunningInvoiceStatusContextEnum.FAILED_DUNNING;

import org.meveo.apiv2.dunning.DunningPolicy;
import org.meveo.apiv2.dunning.DunningPolicyInput;
import org.meveo.apiv2.dunning.ImmutableDunningPolicy;
import org.meveo.apiv2.dunning.resource.DunningPolicyResource;
import org.meveo.apiv2.dunning.service.DunningPolicyApiService;
import org.meveo.apiv2.dunning.service.DunningPolicyLevelApiService;
import org.meveo.apiv2.generic.common.LinkGenerator;
import org.meveo.apiv2.report.ImmutableSuccessResponse;
import org.meveo.model.dunning.DunningPolicyLevel;
import org.meveo.service.payments.impl.DunningPolicyLevelService;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DunningPolicyResourceImpl implements DunningPolicyResource {

    @Inject
    private DunningPolicyApiService dunningPolicyApiService;

    @Inject
    private DunningPolicyLevelApiService policyLevelApiService;

    @Inject
    private DunningPolicyLevelService dunningPolicyLevelService;

    private final DunningPolicyMapper mapper = new DunningPolicyMapper();

    private final DunningPolicyLevelMapper policyLevelMapper = new DunningPolicyLevelMapper();

    private DunningPolicyLevelMapper dunningPolicyLevelMapper = new DunningPolicyLevelMapper();

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
        org.meveo.model.dunning.DunningPolicy entity = mapper.toEntity(dunningPolicy);
        org.meveo.model.dunning.DunningPolicy savedEntity = dunningPolicyApiService.create(entity);
        if (dunningPolicy.getDunningPolicyLevels() != null) {
            entity.setDunningLevels(dunningPolicy.getDunningPolicyLevels()
                    .stream()
                    .map(dunningPolicyLevelMapper::toEntity)
                    .collect(Collectors.toList()));
        }
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
                if (!dunningPolicyLevelEntity.getCollectionPlanStatus().getContext().equals("Failed dunning")
                        && !dunningPolicyLevelEntity.getInvoiceDunningStatuses().getContext().equals(FAILED_DUNNING)) {
                    throw new BadRequestException("Dunning level creation fails");
                }
                if (dunningPolicyLevelEntity.getSequence() < highestSequence) {
                    throw new BadRequestException("sequence must be high");
                }
                highestSequence = dunningPolicyLevelEntity.getSequence();
                countEndOfDunningLevel++;
            }
            dunningPolicyApiService.validateActiveDunning(dunningPolicyLevelEntity);
            policyLevelApiService.create(dunningPolicyLevelEntity);
        }
        dunningPolicyApiService.validateLevelsNumber(countReminderLevels, countEndOfDunningLevel, totalDunningLevels);
        savedEntity.setTotalDunningLevels(totalDunningLevels);
        dunningPolicyApiService.updateTotalLevels(savedEntity);
        return Response.ok(LinkGenerator.getUriBuilderFromResource(DunningPolicyResource.class, dunningPolicy.getId())
                .build())
                .entity(entity.getId())
                .build();
    }

    @Override
    public Response update(Long dunningPolicyId, DunningPolicyInput dunningPolicy) {
        org.meveo.model.dunning.DunningPolicy entity = dunningPolicyApiService.findById(dunningPolicyId)
                .orElseThrow(() -> new NotFoundException("Dunning policy with id " + dunningPolicyId + " does not exits"));
        StringBuilder updatedField = new StringBuilder();
        List<DunningPolicyLevel> dunningPolicyLevelList = new ArrayList<>();
        if (dunningPolicy.getDunningPolicyLevels() != null && !dunningPolicy.getDunningPolicyLevels().isEmpty()) {
            updatedField.append("dunningLevels;");
            entity.setDunningLevels(null);
            for (org.meveo.apiv2.dunning.DunningPolicyLevel dunningPolicyLevel : dunningPolicy.getDunningPolicyLevels()) {
                DunningPolicyLevel policyLevel = new DunningPolicyLevel();
                policyLevel.setId(dunningPolicyLevel.getId());
                DunningPolicyLevel level = dunningPolicyLevelService.refreshOrRetrieve(policyLevel);
                if (level != null) {
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
        return Response
                .ok(getUriBuilderFromResource(DunningPolicyResource.class, entity.getId()).build())
                .entity(mapper.toResource(entity))
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