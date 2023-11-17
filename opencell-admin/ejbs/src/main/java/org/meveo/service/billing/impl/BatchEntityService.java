/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.service.billing.impl;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.BatchEntity;
import org.meveo.model.billing.BatchEntityStatusEnum;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.base.NativePersistenceService;
import org.meveo.service.base.PersistenceService;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * BatchEntityService : A class for Batch entity persistence services.
 *
 * @author Abdellatif BARI
 * @since 15.1.0
 */
@Stateless
public class BatchEntityService extends PersistenceService<BatchEntity> {

    @Inject
    @Named
    private NativePersistenceService nativePersistenceService;

    @EJB
    BatchEntityService batchEntityService;

    /**
     * Create the new batch entity
     *
     * @param filters
     * @param targetJob
     * @param targetEntity
     */
    public void create(Map<String, Object> filters, String targetJob, String targetEntity) {
        BatchEntity batchEntity = new BatchEntity();
        batchEntity.setCode(targetJob + "_" + targetEntity);
        batchEntity.setTargetJob(targetJob);
        batchEntity.setTargetEntity(targetEntity);
        batchEntity.setFilters(filters);
        batchEntity.setNotify(true);
        create(batchEntity);
    }

    /**
     * Call BatchEntity.cancelOpenedBatchEntity Named query to cancel opened RatedTransaction.
     *
     * @param id rated batch entity to cancel
     */
    public void cancel(Long id) {
        getEntityManager().createNamedQuery("BatchEntity.cancelOpenedBatchEntity").setParameter("id", id).executeUpdate();
    }

    /**
     * Mark a multiple Wallet operations to rerate
     *
     * @param batchEntities      batch entities
     * @param jobExecutionResult Job execution result
     */
    public void markWoToRerate(List<BatchEntity> batchEntities, JobExecutionResultImpl jobExecutionResult) {
        JobInstance jobInstance = jobExecutionResult.getJobInstance();
        jobExecutionResult.addNbItemsToProcess(batchEntities.size());
        for (BatchEntity batchEntity : batchEntities) {
            try {
                batchEntity.setStatus(BatchEntityStatusEnum.PROCESSING);
                batchEntity.setJobInstance(jobInstance);

                String entityClassName = "WalletOperation";
                String tableNameAlias = "a";
                StringBuilder updateQuery = new StringBuilder("UPDATE ").append(entityClassName).append(" SET ")
                        .append("status=").append(QueryBuilder.paramToString(WalletOperationStatusEnum.TO_RERATE))
                        .append(", updated=").append(QueryBuilder.paramToString(new Date()))
                        .append(", reratingBatch.id=").append(batchEntity.getId());

                QueryBuilder queryBuilder = new QueryBuilder(updateQuery.toString());
                nativePersistenceService.update(queryBuilder, entityClassName, tableNameAlias, batchEntity.getFilters());
                batchEntity.setStatus(BatchEntityStatusEnum.SUCCESS);
                update(batchEntity);
                jobExecutionResult.registerSucces();
            } catch (Exception e) {
                log.error("Failed to process the entity batch id : " + batchEntity.getId(), e);
                batchEntityService.update(batchEntity, jobExecutionResult, e.getMessage());
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void update(BatchEntity batchEntity, JobExecutionResultImpl jobExecutionResult, String errorMessage) {
        batchEntity.setStatus(BatchEntityStatusEnum.FAILURE);
        update(batchEntity);
        jobExecutionResult.registerError(errorMessage);
    }

}

