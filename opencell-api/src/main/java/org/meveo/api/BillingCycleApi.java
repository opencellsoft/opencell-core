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

package org.meveo.api;

import java.util.function.BiFunction;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.BillingCycleDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingEntityTypeEnum;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.ThresholdOptionsEnum;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.tax.TaxCategory;
import org.meveo.service.billing.impl.BillingCycleService;
import org.meveo.service.billing.impl.InvoiceTypeService;
import org.meveo.service.catalog.impl.CalendarService;
import org.meveo.service.script.ScriptInstanceService;

/**
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Stateless
public class BillingCycleApi extends BaseCrudApi<BillingCycle, BillingCycleDto> {

    @Inject
    private BillingCycleService billingCycleService;

    @Inject
    private CalendarService calendarService;

    @Inject
    private InvoiceTypeService invoiceTypeService;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Override
    public BillingCycle create(BillingCycleDto dto) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(dto.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(dto.getCalendar())) {
            missingParameters.add("calendar");
        }
        if (dto.getDueDateDelay() == null && StringUtils.isBlank(dto.getDueDateDelayEL()) && StringUtils.isBlank(dto.getDueDateDelayELSpark())) {
            missingParameters.add("dueDateDelayEL or dueDateDelayELSpark");
        }

        handleMissingParametersAndValidate(dto);

        if (billingCycleService.findByCode(dto.getCode()) != null) {
            throw new EntityAlreadyExistsException(BillingCycle.class, dto.getCode());
        }

        BillingCycle entity = new BillingCycle();

        dtoToEntity(entity, dto);
        billingCycleService.create(entity);

        return entity;
    }

    @Override
    public BillingCycle update(BillingCycleDto dto) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(dto.getCode())) {
            missingParameters.add("code");
        }
        handleMissingParametersAndValidate(dto);

        BillingCycle entity = billingCycleService.findByCode(dto.getCode());

        if (entity == null) {
            throw new EntityDoesNotExistsException(BillingCycle.class, dto.getCode());
        }

        if (!StringUtils.isBlank(dto.getUpdatedCode())) {
            if (billingCycleService.findByCode(dto.getUpdatedCode()) != null) {
                throw new EntityAlreadyExistsException(TaxCategory.class, dto.getUpdatedCode());
            }
        }

        dtoToEntity(entity, dto);

        entity = billingCycleService.update(entity);
        return entity;
    }

    /**
     * Populate entity with fields from DTO entity
     * 
     * @param entity Entity to populate
     * @param dto DTO entity object to populate from
     **/
    private void dtoToEntity(BillingCycle entity, BillingCycleDto dto) {

        boolean isNew = entity.getId() == null;
        if (isNew) {
            entity.setCode(dto.getCode());
        } else if (!StringUtils.isBlank(dto.getUpdatedCode())) {
            entity.setCode(dto.getUpdatedCode());
        }

        if (isNew && dto.getType() == null) {
            dto.setType(BillingEntityTypeEnum.BILLINGACCOUNT);
        }
        if (isNew && dto.getCheckThreshold() == null) {
            dto.setCheckThreshold(ThresholdOptionsEnum.AFTER_DISCOUNT);
        }

        if (dto.getCalendar() != null) {
            Calendar calendar = calendarService.findByCode(dto.getCalendar());
            if (calendar == null) {
                throw new EntityDoesNotExistsException(Calendar.class, dto.getCalendar());
            }
            entity.setCalendar(calendar);
        }

        if (dto.getInvoiceTypeCode() != null) {
            InvoiceType invoiceType = invoiceTypeService.findByCode(dto.getInvoiceTypeCode());
            if (invoiceType == null) {
                throw new EntityDoesNotExistsException(InvoiceType.class, dto.getInvoiceTypeCode());
            }
            entity.setInvoiceType(invoiceType);
        }

        if (!StringUtils.isBlank(dto.getScriptInstanceCode())) {
            ScriptInstance scriptInstance = scriptInstanceService.findByCode(dto.getScriptInstanceCode());
            if (scriptInstance == null) {
                throw new EntityDoesNotExistsException(ScriptInstance.class, dto.getScriptInstanceCode());
            }
            entity.setScriptInstance(scriptInstance);
        }

        entity.setCode(StringUtils.isBlank(dto.getUpdatedCode()) ? dto.getCode() : dto.getUpdatedCode());

        if (dto.getDescription() != null) {
            entity.setDescription(StringUtils.isEmpty(dto.getDescription()) ? null : dto.getDescription());
        }
        if(dto.getLanguageDescriptions() != null) {
           entity.setDescriptionI18n(convertMultiLanguageToMapOfValues(dto.getLanguageDescriptions(), null));
        }
        if (!StringUtils.isBlank(dto.getBillingTemplateName()) && StringUtils.isBlank(dto.getBillingTemplateNameEL())) {
            dto.setBillingTemplateNameEL(dto.getBillingTemplateName());
        }
        if (dto.getInvoiceDateDelay() != null && StringUtils.isBlank(dto.getInvoiceDateDelayEL())) {
            dto.setInvoiceDateDelayEL(dto.getInvoiceDateDelay().toString());
        }
        if (dto.getDueDateDelay() != null && StringUtils.isBlank(dto.getDueDateDelayEL())) {
            dto.setDueDateDelayEL(dto.getDueDateDelay().toString());
        }
        if (dto.getLastTransactionDateDelay() != null && StringUtils.isBlank(dto.getTransactionDateDelayEL())) {
            dto.setTransactionDateDelayEL(dto.getLastTransactionDateDelay().toString());
        }
        if (dto.getInvoiceDateProductionDelay() != null && StringUtils.isBlank(dto.getInvoiceDateProductionDelayEL())) {
            dto.setInvoiceDateProductionDelayEL(dto.getInvoiceDateProductionDelay().toString());
        }

        if (dto.getBillingTemplateNameEL() != null) {
            entity.setBillingTemplateNameEL(StringUtils.isEmpty(dto.getBillingTemplateNameEL()) ? null : dto.getBillingTemplateNameEL());
        }
        if (dto.getInvoiceDateDelayEL() != null) {
            entity.setInvoiceDateDelayEL(StringUtils.isEmpty(dto.getInvoiceDateDelayEL()) ? null : dto.getInvoiceDateDelayEL());
        }
        if (dto.getDueDateDelayEL() != null) {
            entity.setDueDateDelayEL(StringUtils.isEmpty(dto.getDueDateDelayEL()) ? null : dto.getDueDateDelayEL());
        }
        if (dto.getDueDateDelayELSpark() != null) {
            entity.setDueDateDelayELSpark(StringUtils.isEmpty(dto.getDueDateDelayELSpark()) ? null : dto.getDueDateDelayELSpark());
        }
        if (dto.getInvoiceTypeEl() != null) {
            entity.setInvoiceTypeEl(StringUtils.isEmpty(dto.getInvoiceTypeEl()) ? null : dto.getInvoiceTypeEl());
        }
        if (dto.getInvoiceTypeElSpark() != null) {
            entity.setInvoiceTypeElSpark(StringUtils.isEmpty(dto.getInvoiceTypeElSpark()) ? null : dto.getInvoiceTypeElSpark());
        }
        if (dto.getLastTransactionDateEL() != null) {
            entity.setLastTransactionDateEL(StringUtils.isEmpty(dto.getLastTransactionDateEL()) ? null : dto.getLastTransactionDateEL());
        }
        if (dto.getTransactionDateDelayEL() != null) {
            entity.setLastTransactionDateDelayEL(StringUtils.isEmpty(dto.getTransactionDateDelayEL()) ? null : dto.getTransactionDateDelayEL());
        }
        if (dto.getInvoiceDateProductionDelayEL() != null) {
            entity.setInvoiceDateProductionDelayEL(StringUtils.isEmpty(dto.getInvoiceDateProductionDelayEL()) ? null : dto.getInvoiceDateProductionDelayEL());
        }
        if (dto.isThresholdPerEntity() != null) {
        	entity.setThresholdPerEntity(dto.isThresholdPerEntity());
        }
        if (dto.getInvoicingThreshold() != null) {
            entity.setInvoicingThreshold(dto.getInvoicingThreshold());
        }
        if (dto.getReferenceDate() != null) {
            entity.setReferenceDate(dto.getReferenceDate());
        }
        if (dto.getType() != null) {
            entity.setType(dto.getType());
        }
        if (dto.getCheckThreshold() != null) {
            entity.setCheckThreshold(dto.getCheckThreshold());
        }
        if (dto.getSplitPerPaymentMethod() != null) {
            entity.setSplitPerPaymentMethod(dto.getSplitPerPaymentMethod());
        }
        if (dto.getCollectionDateDelayEl() != null) {
            entity.setCollectionDateDelayEl(dto.getCollectionDateDelayEl());
        }
        // populate customFields
        try {
            populateCustomFields(dto.getCustomFields(), entity, isNew, true);

        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }
    }

    @Override
    protected BiFunction<BillingCycle, CustomFieldsDto, BillingCycleDto> getEntityToDtoFunction() {
        return BillingCycleDto::new;
    }
}
