package org.meveo.apiv2.billing.impl;

import org.meveo.api.exception.MissingParameterException;
import org.meveo.apiv2.billing.ImmutableBatchEntity;
import org.meveo.apiv2.ordering.ResourceMapper;
import org.meveo.model.billing.BatchEntity;

/**
 * A Batch entity mapper.
 *
 * @author Abdellatif BARI
 * @since 15.1.0
 */
public class BatchEntityMapper extends ResourceMapper<org.meveo.apiv2.billing.BatchEntity, BatchEntity> {

    @Override
    protected org.meveo.apiv2.billing.BatchEntity toResource(BatchEntity entity) {
        return ImmutableBatchEntity.builder()
                .id(entity.getId())
                .name(entity.getName())
                .targetJob(entity.getTargetJob())
                .targetEntity(entity.getTargetEntity())
                .filters(entity.getFilters())
                .notify(entity.isNotify())
                .build();
    }

    @Override
    protected BatchEntity toEntity(org.meveo.apiv2.billing.BatchEntity resource) {
        BatchEntity batchEntity = new BatchEntity();
        batchEntity.setName(resource.getName());
        batchEntity.setTargetJob(resource.getTargetJob());
        batchEntity.setTargetEntity(resource.getTargetEntity());
        if (resource.getFilters().isEmpty()) {
            throw new MissingParameterException("filters");
        }
        batchEntity.setFilters(resource.getFilters());
        if (resource.getNotify() != null) {
            batchEntity.setNotify(resource.getNotify());
        }
        return batchEntity;
    }
}
