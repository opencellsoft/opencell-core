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

import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.model.billing.BatchEntity;
import org.meveo.model.billing.BatchEntityStatusEnum;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.base.NativePersistenceService;
import org.meveo.service.base.PersistenceService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;
import java.util.HashMap;
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

    /**
     * Create the new batch entity
     *
     * @param filters
     * @param targetJob
     * @param targetEntity
     */
    public void create(Map<String, Object> filters, String targetJob, String targetEntity) {
        BatchEntity batchEntity = new BatchEntity();
        batchEntity.setName(targetJob + "_" + targetEntity);
        batchEntity.setTargetJob(targetJob);
        batchEntity.setTargetEntity(targetEntity);
        batchEntity.setFilters(filters);
        batchEntity.setNotify(true);
        create(batchEntity);
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

                PaginationConfiguration paginationConfiguration = new PaginationConfiguration(batchEntity.getFilters());

                Map<String, Object> toUpdateFields = new HashMap<>();
                toUpdateFields.put("status", WalletOperationStatusEnum.TO_RERATE);
                toUpdateFields.put("updated", new Date());

                nativePersistenceService.update("WalletOperation", toUpdateFields, paginationConfiguration);
                batchEntity.setStatus(BatchEntityStatusEnum.SUCCESS);
                update(batchEntity);
                jobExecutionResult.registerSucces();
            } catch (Exception e) {
                log.error("Failed to process the entity batch : " + batchEntity.getName(), e);
                batchEntity.setStatus(BatchEntityStatusEnum.FAILURE);
                update(batchEntity);
                jobExecutionResult.registerError(e.getMessage());
            }
        }
    }

}

