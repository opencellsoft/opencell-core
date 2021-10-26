package org.meveo.apiv2.dunning.impl;

import static org.meveo.model.dunning.DunningInvoiceStatusContextEnum.FAILED_DUNNING;

import org.meveo.apiv2.dunning.DunningPolicy;
import org.meveo.apiv2.dunning.resource.DunningPolicyResource;
import org.meveo.apiv2.dunning.service.DunningPolicyApiService;
import org.meveo.apiv2.dunning.service.DunningPolicyLevelApiService;
import org.meveo.apiv2.generic.common.LinkGenerator;
import org.meveo.model.dunning.DunningPolicyLevel;
import org.meveo.service.payments.impl.DunningPolicyLevelService;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
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
        if (dunningPolicy.getDunningLevels() != null) {
            entity.setDunningLevels(dunningPolicy.getDunningLevels()
                    .stream()
                    .map(dunningPolicyLevelMapper::toEntity)
                    .collect(Collectors.toList()));
        }
        int totalDunningLevels = 0;
        int countReminderLevels = 0;
        int countEndOfDunningLevel = 0;
        int highestSequence = (dunningPolicy.getDunningLevels() != null && !dunningPolicy.getDunningLevels().isEmpty())
                ? dunningPolicy.getDunningLevels().get(0).getSequence() : 0;
        for (org.meveo.apiv2.dunning.DunningPolicyLevel dunningPolicyLevel : dunningPolicy.getDunningLevels()) {
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
        return Response.created(LinkGenerator.getUriBuilderFromResource(DunningPolicyResource.class, dunningPolicy.getId())
                .build())
                .entity(entity.getId())
                .build();
    }

    @Override
    public Response update(Long dunningPolicyId, DunningPolicy dunningPolicy) {
        org.meveo.model.dunning.DunningPolicy entity = dunningPolicyApiService.findById(dunningPolicyId)
                .orElseThrow(() -> new NotFoundException("Dunning policy with id " + dunningPolicyId + " does not exits"));
        List<DunningPolicyLevel> dunningPolicyLevelList = new ArrayList<>();
        if (dunningPolicy.getDunningLevels() != null && !dunningPolicy.getDunningLevels().isEmpty()) {
            entity.setDunningLevels(null);
            for (org.meveo.apiv2.dunning.DunningPolicyLevel dunningPolicyLevel : dunningPolicy.getDunningLevels()) {
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
        dunningPolicyApiService.update(dunningPolicyId, mapper.toUpdateEntity(dunningPolicy, entity));
        return Response
                .ok(org.meveo.apiv2.ordering.common.LinkGenerator.getUriBuilderFromResource(DunningPolicyResource.class, entity.getId()).build())
                .entity(mapper.toResource(entity))
                .build();
    }
}