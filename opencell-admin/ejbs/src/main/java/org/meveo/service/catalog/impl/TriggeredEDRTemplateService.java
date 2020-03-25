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
package org.meveo.service.catalog.impl;

import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.catalog.TriggeredEDRTemplate;
import org.meveo.service.base.BusinessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Charge Template service implementation.
 * 
 */
@Stateless
public class TriggeredEDRTemplateService extends BusinessService<TriggeredEDRTemplate> {

    public synchronized void duplicate(TriggeredEDRTemplate entity) throws BusinessException {
        entity = refreshOrRetrieve(entity);
        String code = findDuplicateCode(entity);

        // Detach and clear ids of entity and related entities
        detach(entity);
        entity.setId(null);
        entity.setCode(code);
        create(entity);
    }

    @Override
    public void create(TriggeredEDRTemplate edrTemplate) throws BusinessException {

        edrTemplate.setSubscriptionEl(StringUtils.stripToNull(edrTemplate.getSubscriptionEl()));
        edrTemplate.setConditionEl(StringUtils.stripToNull(edrTemplate.getConditionEl()));
        edrTemplate.setQuantityEl(StringUtils.stripToNull(edrTemplate.getQuantityEl()));
        edrTemplate.setParam1El(StringUtils.stripToNull(edrTemplate.getParam1El()));
        edrTemplate.setParam2El(StringUtils.stripToNull(edrTemplate.getParam2El()));
        edrTemplate.setParam3El(StringUtils.stripToNull(edrTemplate.getParam3El()));
        edrTemplate.setParam4El(StringUtils.stripToNull(edrTemplate.getParam4El()));

        if (edrTemplate.getQuantityEl() == null) {
            Logger log = LoggerFactory.getLogger(this.getClass());
            log.error("edrTemplate QuantityEL must be set for triggeredEDRTemplate {}", edrTemplate.getId());
        }

        if (edrTemplate.getParam1El() == null) {
            Logger log = LoggerFactory.getLogger(this.getClass());
            log.error("edrTemplate param1El must be set for triggeredEDRTemplate {}", edrTemplate.getId());
        }

        super.create(edrTemplate);
    }

    @Override
    public TriggeredEDRTemplate update(TriggeredEDRTemplate edrTemplate) throws BusinessException {

        edrTemplate.setSubscriptionEl(StringUtils.stripToNull(edrTemplate.getSubscriptionEl()));
        edrTemplate.setConditionEl(StringUtils.stripToNull(edrTemplate.getConditionEl()));
        edrTemplate.setQuantityEl(StringUtils.stripToNull(edrTemplate.getQuantityEl()));
        edrTemplate.setParam1El(StringUtils.stripToNull(edrTemplate.getParam1El()));
        edrTemplate.setParam2El(StringUtils.stripToNull(edrTemplate.getParam2El()));
        edrTemplate.setParam3El(StringUtils.stripToNull(edrTemplate.getParam3El()));
        edrTemplate.setParam4El(StringUtils.stripToNull(edrTemplate.getParam4El()));

        if (edrTemplate.getQuantityEl() == null) {
            Logger log = LoggerFactory.getLogger(this.getClass());
            log.error("edrTemplate QuantityEL must be set for triggeredEDRTemplate {}", edrTemplate.getId());
        }

        if (edrTemplate.getParam1El() == null) {
            Logger log = LoggerFactory.getLogger(this.getClass());
            log.error("edrTemplate param1El must be set for triggeredEDRTemplate {}", edrTemplate.getId());
        }

        edrTemplate = super.update(edrTemplate);
        return edrTemplate;
    }
}