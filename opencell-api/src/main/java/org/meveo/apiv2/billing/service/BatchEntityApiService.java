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
package org.meveo.apiv2.billing.service;

import org.meveo.api.exception.ActionForbiddenException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.BatchEntity;
import org.meveo.model.billing.BatchEntityStatusEnum;
import org.meveo.service.billing.impl.BatchEntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static org.meveo.commons.utils.StringUtils.isBlank;

/**
 * A batch entity API service.
 *
 * @author Abdellatif BARI
 * @since 15.1.0
 */
public class BatchEntityApiService implements ApiService<BatchEntity> {

    private static final Logger log = LoggerFactory.getLogger(BatchEntity.class);

    @Inject
    private BatchEntityService service;

    @Override
    public List<BatchEntity> list(Long offset, Long limit, String sort, String orderBy, String filter) {
        return new ArrayList<>();
    }

    @Override
    public Long getCount(String filter) {
        return null;
    }

    @Override
    public Optional<BatchEntity> findById(Long id) {
        return ofNullable(service.findById(id));
    }

    @Override
    public BatchEntity create(BatchEntity baseEntity) {
        if (baseEntity != null && StringUtils.isBlank(baseEntity.getCode())) {
            baseEntity.setCode(service.getBatchEntityCode(null));
        }
        service.create(baseEntity);
        return baseEntity;
    }

    @Override
    public Optional<BatchEntity> update(Long id, BatchEntity baseEntity) {
        Optional<BatchEntity> batchEntityOptional = findById(id);
        if (batchEntityOptional.isEmpty()) {
            throw new EntityDoesNotExistsException(BatchEntity.class, id);
        }
        BatchEntity batchEntity = batchEntityOptional.get();
        if (!isBlank(baseEntity.getCode())) {
            batchEntity.setCode(baseEntity.getCode());
        }
        if (!isBlank(baseEntity.getTargetJob())) {
            batchEntity.setTargetJob(baseEntity.getTargetJob());
        }
        if (!isBlank(baseEntity.getTargetEntity())) {
            batchEntity.setTargetEntity(baseEntity.getTargetEntity());
        }
        if (!isBlank(baseEntity.getFilters())) {
            batchEntity.setFilters(baseEntity.getFilters());
        }
        if (!isBlank(baseEntity.isNotify())) {
            batchEntity.setNotify(baseEntity.isNotify());
        }
        batchEntity = service.update(batchEntity);
        return ofNullable(batchEntity);
    }

    @Override
    public Optional<BatchEntity> patch(Long id, BatchEntity baseEntity) {
        return Optional.empty();
    }

    @Override
    public Optional<BatchEntity> delete(Long id) {
        Optional<BatchEntity> batchEntity = findById(id);
        if (batchEntity.isEmpty()) {
            throw new EntityDoesNotExistsException(BatchEntity.class, id);
        }
        try {
            service.remove(batchEntity.get());
        } catch (Exception e) {
            throw new BadRequestException(e);
        }
        return batchEntity;
    }

    @Override
    public Optional<BatchEntity> findByCode(String code) {
        return Optional.empty();
    }

    /**
     * Check if the batch entity is eligible to update.
     *
     * @param id the batch entity id
     * @return true if the batch entity is eligible to update
     */
    public boolean isEligibleToUpdate(Long id) {
        BatchEntity batchEntity = findById(id).orElseThrow(NotFoundException::new);
        if (!BatchEntityStatusEnum.OPEN.equals(batchEntity.getStatus())) {
            throw new ActionForbiddenException("Can only edit batch entity in statuses OPEN. current batch entity status is :" +
                    batchEntity.getStatus().name());
        }
        return true;
    }

    /**
     * Cancel the batch entity
     *
     * @param id the batch entity id
     */
    public void cancel(Long id) {
        service.cancel(id);
    }
}