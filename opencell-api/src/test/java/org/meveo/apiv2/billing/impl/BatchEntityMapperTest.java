package org.meveo.apiv2.billing.impl;

import org.junit.Before;
import org.junit.Test;
import org.meveo.apiv2.billing.ImmutableBatchEntity;
import org.meveo.model.billing.BatchEntity;
import org.meveo.model.billing.WalletOperationStatusEnum;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * A test class for batch entity mapper.
 *
 * @author Abdellatif BARI
 * @since 15.1.0
 */
public class BatchEntityMapperTest {

    private BatchEntityMapper batchEntityMapper;
    private final String batchEntityCode = "code1";
    private final String batchEntityDescription = "description1";
    private final String batchEntityTargetJob = "MarkWOToRerateJob";
    private final String batchEntityTargetEntity = "WalletOperation";
    private final Boolean batchEntityNotify = false;
    Map<String, Object> filters = new HashMap<>();

    @Before
    public void setUp() {
        batchEntityMapper = new BatchEntityMapper();
        filters.put("status", WalletOperationStatusEnum.TO_RERATE);
    }

    @Test
    public void test_map_entity_to_resource() {
        BatchEntity entity = buildBatchEntity();
        org.meveo.apiv2.billing.BatchEntity resource = batchEntityMapper.toResource(entity);
        assertNotNull(resource);
        assertEquals(resource.getId(), (Long) 1L);
        assertEquals(resource.getCode(), batchEntityCode);
        assertEquals(resource.getDescription(), batchEntityDescription);
        assertEquals(resource.getTargetJob(), batchEntityTargetJob);
        assertEquals(resource.getTargetEntity(), batchEntityTargetEntity);
        assertEquals(resource.getFilters(), filters);
        assertEquals(resource.getNotify(), batchEntityNotify);
    }

    private BatchEntity buildBatchEntity() {
        BatchEntity batchEntity = new BatchEntity();
        batchEntity.setId(1L);
        batchEntity.setCode(batchEntityCode);
        batchEntity.setDescription(batchEntityDescription);
        batchEntity.setTargetJob(batchEntityTargetJob);
        batchEntity.setTargetEntity(batchEntityTargetEntity);
        batchEntity.setFilters(filters);
        batchEntity.setNotify(batchEntityNotify);
        return batchEntity;
    }

    @Test
    public void test_map_resource_to_entity() {
        org.meveo.apiv2.billing.BatchEntity resource = buildBatchResource();
        BatchEntity batchEntity = batchEntityMapper.toEntity(resource);
        assertNotNull(batchEntity);
        assertEquals(batchEntity.getCode(), batchEntityCode);
        assertEquals(batchEntity.getDescription(), batchEntityDescription);
        assertEquals(batchEntity.getTargetJob(), batchEntityTargetJob);
        assertEquals(batchEntity.getTargetEntity(), batchEntityTargetEntity);
        assertEquals(batchEntity.getFilters(), filters);
        assertEquals(batchEntity.isNotify(), batchEntityNotify);
    }

    private org.meveo.apiv2.billing.BatchEntity buildBatchResource() {
        ImmutableBatchEntity resource = ImmutableBatchEntity.builder()
                .id(1L)
                .code(batchEntityCode)
                .description(batchEntityDescription)
                .targetJob(batchEntityTargetJob)
                .targetEntity(batchEntityTargetEntity)
                .filters(filters)
                .notify(batchEntityNotify)
                .build();
        return resource;
    }
}