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

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.BillingCycleDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.ReportConfig;
import org.meveo.api.exception.*;
import org.meveo.model.billing.*;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.tax.TaxCategory;
import org.meveo.service.billing.impl.BillingCycleService;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.billing.impl.InvoiceTypeService;
import org.meveo.service.catalog.impl.CalendarService;
import org.meveo.service.script.ScriptInstanceService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

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
    private BillingRunService billingRunService;

    @Inject
    private CalendarService calendarService;

    @Inject
    private InvoiceTypeService invoiceTypeService;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Override
    public BillingCycle create(BillingCycleDto dto) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(dto.getCode())) {
            addGenericCodeIfAssociated(BillingCycle.class.getName(), dto);
        }
        if (StringUtils.isBlank(dto.getCalendar())) {
            missingParameters.add("calendar");
        }
        if (dto.getDueDateDelay() == null && StringUtils.isBlank(dto.getDueDateDelayEL())) {
            missingParameters.add("dueDateDelayEL");
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
        List<BillingRun> attachedBRs = billingRunService.findBillingRunsByBillingCycle(entity);
        List<String> additionalAggregationFields = entity.getAdditionalAggregationFields();
        attachedBRs.forEach(br -> {
            br.setAdditionalAggregationFields(additionalAggregationFields);
            billingRunService.update(br);
        });

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

        if (!StringUtils.isBlank(dto.getInvoiceTypeCode())) {
            InvoiceType invoiceType = invoiceTypeService.findByCode(dto.getInvoiceTypeCode());
            if (invoiceType == null) {
                throw new EntityDoesNotExistsException(InvoiceType.class, dto.getInvoiceTypeCode());
            }
            entity.setInvoiceType(invoiceType);
        } else if ("".equals(dto.getInvoiceTypeCode())) {
        	entity.setInvoiceType(null);
        }

        if (!StringUtils.isBlank(dto.getScriptInstanceCode())) {
            ScriptInstance scriptInstance = scriptInstanceService.findByCode(dto.getScriptInstanceCode());
            if (scriptInstance == null) {
                throw new EntityDoesNotExistsException(ScriptInstance.class, dto.getScriptInstanceCode());
            }
            entity.setScriptInstance(scriptInstance);
        } else if ("".equals(dto.getScriptInstanceCode())) {
        	entity.setScriptInstance(null);
        }
        
        if (!StringUtils.isBlank(dto.getBillingRunValidationScriptCode())) {
            ScriptInstance scriptInstance = scriptInstanceService.findByCode(dto.getBillingRunValidationScriptCode());
            if (scriptInstance == null) {
                throw new EntityDoesNotExistsException(ScriptInstance.class, dto.getBillingRunValidationScriptCode());
            }
            entity.setBillingRunValidationScript(scriptInstance);
        } else if ("".equals(dto.getBillingRunValidationScriptCode())) {
        	entity.setBillingRunValidationScript(null);
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
        if (dto.getTransactionDateDelay() != null && StringUtils.isBlank(dto.getTransactionDateDelayEL())) {
            dto.setTransactionDateDelayEL(dto.getTransactionDateDelay().toString());
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
        if (dto.getInvoiceTypeEl() != null) {
            entity.setInvoiceTypeEl(StringUtils.isEmpty(dto.getInvoiceTypeEl()) ? null : dto.getInvoiceTypeEl());
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
        if (dto.getThresholdPerEntity() != null) {
        	entity.setThresholdPerEntity(dto.getThresholdPerEntity());
        }

        entity.setInvoicingThreshold(dto.getInvoicingThreshold());

        if (dto.getReferenceDate() != null) {
            entity.setReferenceDate(dto.getReferenceDate());
        }
        if (dto.getType() != null) {
            entity.setType(dto.getType());
        }

        entity.setCheckThreshold(dto.getCheckThreshold());

        if (dto.getSplitPerPaymentMethod() != null) {
            entity.setSplitPerPaymentMethod(dto.getSplitPerPaymentMethod());
        }
        if (dto.getCollectionDateDelayEl() != null) {
            entity.setCollectionDateDelayEl(dto.getCollectionDateDelayEl());
        }
        if (dto.getComputeDatesAtValidation() != null) {
            entity.setComputeDatesAtValidation(dto.getComputeDatesAtValidation());
        }
		if (dto.getPriority() != null) {
			entity.setPriority(dto.getPriority());
		}

        if (dto.getFilters() == null || dto.getFilters().isEmpty()) {
        	if (dto.getType() != null) {
	            Map filters = new LinkedHashMap();
	            switch (dto.getType()){
	                case BILLINGACCOUNT: filters.put("billingAccount.billingCycle.code", dto.getCode()); entity.setFilters(filters);break;
	                case SUBSCRIPTION: filters.put("subscription.billingCycle.code", dto.getCode()); entity.setFilters(filters); break;
	                case ORDER: filters.put("infoOrder.order.billingCycle.code", dto.getCode()); entity.setFilters(filters); break;
	            }
        	}

        } else {
            entity.setFilters(dto.getFilters());
        }
        
        if (dto.getDisableAggregation() != null) {
            entity.setDisableAggregation(dto.getDisableAggregation());
        }

        if (dto.getUseAccountingArticleLabel() != null) {
            entity.setUseAccountingArticleLabel(dto.getUseAccountingArticleLabel());
        }

        if (dto.getDateAggregation() != null) {
            entity.setDateAggregation(dto.getDateAggregation());
        }
        
        if (dto.getAggregateUnitAmounts() != null) {
            entity.setAggregateUnitAmounts(dto.getAggregateUnitAmounts());
        }
        if (dto.getIgnoreSubscriptions() != null) {
            entity.setIgnoreSubscriptions(dto.getIgnoreSubscriptions());
        }
        if (dto.getIgnoreOrders() != null) {
            entity.setIgnoreOrders(dto.getIgnoreOrders());
        }
        if (dto.getIncrementalInvoiceLines() != null) {
            entity.setIncrementalInvoiceLines(dto.getIncrementalInvoiceLines());
        }
        if (dto.getIgnoreUserAccounts() != null) {
            entity.setIgnoreUserAccounts(dto.getIgnoreUserAccounts());
        }
        if (dto.getDiscountAggregation() != null) {
            entity.setDiscountAggregation(dto.getDiscountAggregation());
        }

        entity.setApplicationEl(dto.getApplicationEl());
        if(dto.getReportConfig() != null) {
            validateReportBlockSizes(dto.getReportConfig());
            if (dto.getReportConfig().getPreReportAutoOnCreate() != null) {
                entity.setReportConfigPreReportAutoOnCreate(dto.getReportConfig().getPreReportAutoOnCreate());
            }
            if (dto.getReportConfig().getPreReportAutoOnInvoiceLinesJob() != null) {
                entity.setReportConfigPreReportAutoOnInvoiceLinesJob(dto.getReportConfig().getPreReportAutoOnInvoiceLinesJob());
            }
            if(dto.getReportConfig().getDisplayBillingAccounts() != null) {
                entity.setReportConfigDisplayBillingAccounts(dto.getReportConfig().getDisplayBillingAccounts());
            }
            if (dto.getReportConfig().getDisplaySubscriptions() != null) {
                entity.setReportConfigDisplaySubscriptions(dto.getReportConfig().getDisplaySubscriptions());
            }
            if (dto.getReportConfig().getDisplayArticles() != null) {
                entity.setReportConfigDisplayArticles(dto.getReportConfig().getDisplayArticles());
            }
            if(dto.getReportConfig().getDisplayOffers() != null) {
                entity.setReportConfigDisplayOffers(dto.getReportConfig().getDisplayOffers());
            }
            if (dto.getReportConfig().getDisplayProducts() != null) {
                entity.setReportConfigDisplayProducts(dto.getReportConfig().getDisplayProducts());
            }
            if(dto.getReportConfig().getBlockSizeArticles() != null) {
                entity.setReportConfigBlockSizeArticles(dto.getReportConfig().getBlockSizeArticles());
            }
            if (dto.getReportConfig().getBlockSizeBillingAccounts() != null) {
                entity.setReportConfigBlockSizeBillingAccounts(dto.getReportConfig().getBlockSizeBillingAccounts());
            }
            if (dto.getReportConfig().getBlockSizeSubscriptions() != null) {
                entity.setReportConfigBlockSizeSubscriptions(dto.getReportConfig().getBlockSizeSubscriptions());
            }
            if (dto.getReportConfig().getBlockSizeOffers() != null) {
                entity.setReportConfigBlockSizeOffers(dto.getReportConfig().getBlockSizeOffers());
            }
            if (dto.getReportConfig().getBlockSizeProducts() != null) {
                entity.setReportConfigBlockSizeProducts(dto.getReportConfig().getBlockSizeProducts());
            }
        }

        if(dto.getAdditionalAggregationFields() != null) {
            entity.setAdditionalAggregationFields(dto.getAdditionalAggregationFields());
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

    private void validateReportBlockSizes(ReportConfig reportConfig) {
        if (reportConfig.getBlockSizeArticles() != null
                && (reportConfig.getBlockSizeArticles() < 1 || reportConfig.getBlockSizeArticles() > 100)) {
            throw  new BusinessApiException("Articles block size should be between 1 and 100");
        }
        if (reportConfig.getBlockSizeOffers() != null
                && (reportConfig.getBlockSizeOffers() < 1 || reportConfig.getBlockSizeOffers() > 100)) {
            throw  new BusinessApiException("Offers block size should be between 1 and 100");
        }
        if (reportConfig.getBlockSizeProducts() != null
                && (reportConfig.getBlockSizeProducts() < 1 || reportConfig.getBlockSizeProducts() > 100)) {
            throw  new BusinessApiException("Products block size should be between 1 and 100");
        }
        if (reportConfig.getBlockSizeSubscriptions() != null
                && (reportConfig.getBlockSizeSubscriptions() < 1 || reportConfig.getBlockSizeSubscriptions() > 100)) {
            throw  new BusinessApiException("Subscriptions block size should be between 1 and 100");
        }
        if (reportConfig.getBlockSizeBillingAccounts() != null
                && (reportConfig.getBlockSizeBillingAccounts() < 1 || reportConfig.getBlockSizeBillingAccounts() > 100)) {
            throw  new BusinessApiException("Billing account block size should be between 1 and 100");
        }
    }

    @Override
    protected BiFunction<BillingCycle, CustomFieldsDto, BillingCycleDto> getEntityToDtoFunction() {
        return BillingCycleDto::new;
    }
}
