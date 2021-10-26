package org.meveo.apiv2.dunning.impl;

import org.meveo.apiv2.dunning.DunningPolicyLevel;
import org.meveo.apiv2.ordering.ResourceMapper;
import org.meveo.model.dunning.CollectionPlanStatus;
import org.meveo.model.dunning.DunningInvoiceStatus;
import org.meveo.model.dunning.DunningLevel;


public class DunningPolicyLevelMapper extends ResourceMapper<DunningPolicyLevel, org.meveo.model.dunning.DunningPolicyLevel> {

    @Override
    protected DunningPolicyLevel toResource(org.meveo.model.dunning.DunningPolicyLevel entity) {
        return null;
    }

    @Override
    protected org.meveo.model.dunning.DunningPolicyLevel toEntity(DunningPolicyLevel resource) {
        org.meveo.model.dunning.DunningPolicyLevel entity = new org.meveo.model.dunning.DunningPolicyLevel();
        entity.setSequence(resource.getSequence());
        DunningLevel dunningLevel = new DunningLevel();
        dunningLevel.setId(resource.getDunningLevelId());
        DunningInvoiceStatus invoiceDunningStatuses = new DunningInvoiceStatus();
        invoiceDunningStatuses.setId(resource.getInvoiceDunningStatusesId());
        CollectionPlanStatus collectionPlanStatus = new CollectionPlanStatus();
        collectionPlanStatus.setId(resource.getCollectionPlanStatusId());
        entity.setCollectionPlanStatus(collectionPlanStatus);
        entity.setDunningLevel(dunningLevel);
        entity.setInvoiceDunningStatuses(invoiceDunningStatuses);
        return entity;
    }
}