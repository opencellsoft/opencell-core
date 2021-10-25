package org.meveo.apiv2.dunning.impl;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;

import org.meveo.apiv2.dunning.DunningPolicy;
import org.meveo.apiv2.dunning.resource.DunningPolicyResource;
import org.meveo.apiv2.dunning.service.DunningPolicyApiService;
import org.meveo.apiv2.dunning.service.DunningPolicyLevelApiService;
import org.meveo.apiv2.generic.common.LinkGenerator;
import org.meveo.model.dunning.CollectionPlanStatus;
import org.meveo.model.dunning.DunningLevel;
import org.meveo.model.dunning.DunningPolicyLevel;
import org.meveo.model.dunning.InvoiceDunningStatuses;
import org.meveo.service.payments.impl.CollectionPlanStatusService;
import org.meveo.service.payments.impl.DunningLevelService;
import org.meveo.service.payments.impl.InvoiceDunningStatusesService;

public class DunningPolicyResourceImpl implements DunningPolicyResource {

    @Inject
    private DunningPolicyApiService dunningPolicyApiService;

    @Inject
    private DunningLevelService dunningLevelService;

    @Inject
    private InvoiceDunningStatusesService invoiceDunningStatusesService;

    @Inject
    private CollectionPlanStatusService collectionPlanStatusService;

    @Inject
    private DunningPolicyLevelApiService policyLevelApiService;

    private final DunningPolicyMapper mapper = new DunningPolicyMapper();

    private final DunningPolicyLevelMapper policyLevelMapper = new DunningPolicyLevelMapper();

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
        for (org.meveo.apiv2.dunning.DunningPolicyLevel dunningPolicyLevel : dunningPolicy.getDunningLevels()) {
            DunningPolicyLevel dunningPolicyLevelEntity = policyLevelMapper.toEntity(dunningPolicyLevel);
            dunningPolicyLevelEntity.setDunningPolicy(savedEntity);
            int totalDunningLevels = 0;
            int countReminderLevels = 0;
            int countEndOfDunningLevel = 0;
            int highestSequence = (dunningPolicy.getDunningLevels() != null && !dunningPolicy.getDunningLevels().isEmpty())
                    ? dunningPolicy.getDunningLevels().get(0).getSequence() : 0;
            DunningLevel dunningLevel = dunningLevelService.refreshOrRetrieve(dunningPolicyLevelEntity.getDunningLevel());
            InvoiceDunningStatuses invoiceDunningStatuses =
                    invoiceDunningStatusesService.refreshOrRetrieve(dunningPolicyLevelEntity.getInvoiceDunningStatuses());
            CollectionPlanStatus collectionPlanStatus =
                    collectionPlanStatusService.refreshOrRetrieve(dunningPolicyLevelEntity.getCollectionPlanStatus());
            if(dunningLevel == null) {
                throw new BadRequestException("Policy level creation fails dunning level does not exists");
            }
            if(invoiceDunningStatuses == null) {
                throw new BadRequestException("Policy level creation fails invoice dunning statuses does not exists");
            }
            dunningPolicyLevelEntity.setDunningLevel(dunningLevel);
            dunningPolicyLevelEntity.setInvoiceDunningStatuses(invoiceDunningStatuses);
            dunningPolicyLevelEntity.setCollectionPlanStatus(collectionPlanStatus);
            if(!dunningLevel.isReminder()) {
                totalDunningLevels++;
            } else {
                countReminderLevels++;
            }
            if(dunningLevel.isEndOfDunningLevel()) {
                if(!dunningPolicyLevelEntity.getCollectionPlanStatus().getContext().equals("Failed Dunning")
                        && !dunningPolicyLevelEntity.getInvoiceDunningStatuses().getContext().equals("Failed Dunning")) {
                    throw new BadRequestException("Dunning level creation fails");
                }
                if(dunningPolicyLevelEntity.getSequence() < highestSequence) {
                    throw new BadRequestException("sequence must be high");
                }
                highestSequence = dunningPolicyLevelEntity.getSequence();
                countEndOfDunningLevel++;
            }
            if(!dunningLevel.isEndOfDunningLevel() && !dunningLevel.isReminder()) {
                if(!dunningPolicyLevelEntity.getCollectionPlanStatus().getContext().equals("Active")
                        && !dunningPolicyLevelEntity.getInvoiceDunningStatuses().getContext().equals("Active Dunning")) {
                    throw new BadRequestException("Dunning level creation fails");
                }
            }
            if(totalDunningLevels == 0) {
                throw new BadRequestException("Policy should have at least one dunning level other the reminder level");
            }
            if (countReminderLevels > 1) {
                throw new BadRequestException("There is already a Reminder level for this policy, remove the existing level to select a new one.");
            }
            if (countEndOfDunningLevel > 1) {
                throw new BadRequestException("A policy can have only 1 level with isEndOfDunningLevel = TRUE");
            }
            policyLevelApiService.create(dunningPolicyLevelEntity);
            savedEntity.setTotalDunningLevels(totalDunningLevels);
            dunningPolicyApiService.updateTotalLevels(savedEntity);
        }
        return Response.created(LinkGenerator.getUriBuilderFromResource(DunningPolicyResource.class, dunningPolicy.getId())
                .build())
                .entity(entity.getId())
                .build();
    }
}