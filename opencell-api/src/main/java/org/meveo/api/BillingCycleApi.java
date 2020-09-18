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

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.BillingCycleDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingEntityTypeEnum;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.ThresholdOptionsEnum;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.model.scripts.ScriptInstance;
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
public class BillingCycleApi extends BaseApi {

    @Inject
    private BillingCycleService billingCycleService;

    @Inject
    private CalendarService calendarService;

    @Inject
    private InvoiceTypeService invoiceTypeService;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    @SuppressWarnings("checkstyle:WhitespaceAround")
    public void create(BillingCycleDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getCalendar())) {
            missingParameters.add("calendar");
        }
        if (postData.getInvoiceDateDelay() == null) {
            missingParameters.add("invoiceDateDelay");
        }
        if (postData.getDueDateDelay() == null && StringUtils.isBlank(postData.getDueDateDelayEL()) && StringUtils.isBlank(postData.getDueDateDelayELSpark())) {
            missingParameters.add("dueDateDelay, dueDateDelayEL or dueDateDelayELSpark");
        }

        handleMissingParametersAndValidate(postData);

        if (billingCycleService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(BillingCycle.class, postData.getCode());
        }

        Calendar calendar = calendarService.findByCode(postData.getCalendar());
        if (calendar == null) {
            throw new EntityDoesNotExistsException(Calendar.class, postData.getCalendar());
        }

        InvoiceType invoiceType = null;
        if (!StringUtils.isBlank(postData.getInvoiceTypeCode())) {
            invoiceType = invoiceTypeService.findByCode(postData.getInvoiceTypeCode());
            if (invoiceType == null) {
                throw new EntityDoesNotExistsException(InvoiceType.class, postData.getInvoiceTypeCode());
            }
        }

        ScriptInstance scriptInstance = null;
        if (!StringUtils.isBlank(postData.getScriptInstanceCode())) {
            scriptInstance = scriptInstanceService.findByCode(postData.getScriptInstanceCode());
            if (scriptInstance == null) {
                throw new EntityDoesNotExistsException(ScriptInstance.class, postData.getScriptInstanceCode());
            }
        }

        BillingCycle billingCycle = new BillingCycle();
        billingCycle.setCode(postData.getCode());
        billingCycle.setDescription(postData.getDescription());
        billingCycle.setBillingTemplateName(postData.getBillingTemplateName());
        billingCycle.setBillingTemplateNameEL(postData.getBillingTemplateNameEL());
        billingCycle.setInvoiceDateDelay(postData.getInvoiceDateDelay());
        billingCycle.setDueDateDelay(postData.getDueDateDelay());
        billingCycle.setDueDateDelayEL(postData.getDueDateDelayEL());
        billingCycle.setDueDateDelayELSpark(postData.getDueDateDelayELSpark());
        billingCycle.setCalendar(calendar);
        billingCycle.setTransactionDateDelay(postData.getTransactionDateDelay());
        billingCycle.setInvoiceDateProductionDelay(postData.getInvoiceDateProductionDelay());
        billingCycle.setInvoicingThreshold(postData.getInvoicingThreshold());
        billingCycle.setThresholdPerEntity(postData.isThresholdPerEntity());
        billingCycle.setInvoiceType(invoiceType);
        billingCycle.setScriptInstance(scriptInstance);
        billingCycle.setInvoiceTypeEl(postData.getInvoiceTypeEl());
        billingCycle.setInvoiceTypeElSpark(postData.getInvoiceTypeElSpark());
        billingCycle.setReferenceDate(postData.getReferenceDate());

        if (postData.getType() == null) {
            billingCycle.setType(BillingEntityTypeEnum.BILLINGACCOUNT);
        } else {
            billingCycle.setType(postData.getType());
        }

        if (postData.getCheckThreshold() == null && postData.getInvoicingThreshold() != null) {
            billingCycle.setCheckThreshold(ThresholdOptionsEnum.AFTER_DISCOUNT);
        } else {
            billingCycle.setCheckThreshold(postData.getCheckThreshold());
        }
        
        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), billingCycle, true, true);

        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        billingCycleService.create(billingCycle);
    }

    public void update(BillingCycleDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        handleMissingParametersAndValidate(postData);

        BillingCycle billingCycle = billingCycleService.findByCode(postData.getCode());

        if (billingCycle == null) {
            throw new EntityDoesNotExistsException(BillingCycle.class, postData.getCode());
        }

        if (postData.getCalendar() != null) {
            Calendar calendar = calendarService.findByCode(postData.getCalendar());
            if (calendar == null) {
                throw new EntityDoesNotExistsException(Calendar.class, postData.getCalendar());
            }
            billingCycle.setCalendar(calendar);
        }

        if (postData.getInvoiceTypeCode() != null) {
            InvoiceType invoiceType = invoiceTypeService.findByCode(postData.getInvoiceTypeCode());
            if (invoiceType == null) {
                throw new EntityDoesNotExistsException(InvoiceType.class, postData.getInvoiceTypeCode());
            }
            billingCycle.setInvoiceType(invoiceType);
        }

        if (!StringUtils.isBlank(postData.getScriptInstanceCode())) {
            ScriptInstance scriptInstance = scriptInstanceService.findByCode(postData.getScriptInstanceCode());
            if (scriptInstance == null) {
                throw new EntityDoesNotExistsException(ScriptInstance.class, postData.getScriptInstanceCode());
            }
            billingCycle.setScriptInstance(scriptInstance);
        }

        billingCycle.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());

        if (postData.getDescription() != null) {
            billingCycle.setDescription(postData.getDescription());
        }
        if (postData.getBillingTemplateName() != null) {
            billingCycle.setBillingTemplateName(postData.getBillingTemplateName());
        }
        if (postData.getBillingTemplateNameEL() != null) {
            billingCycle.setBillingTemplateNameEL(postData.getBillingTemplateNameEL());
        }
        if (postData.getInvoiceDateDelay() != null) {
            billingCycle.setInvoiceDateDelay(postData.getInvoiceDateDelay());
        }
        if (postData.getDueDateDelay() != null) {
            billingCycle.setDueDateDelay(postData.getDueDateDelay());
        }
        if (postData.getDueDateDelayEL() != null) {
            billingCycle.setDueDateDelayEL(postData.getDueDateDelayEL());
        }
        if (postData.getDueDateDelayELSpark() != null) {
            billingCycle.setDueDateDelayELSpark(postData.getDueDateDelayELSpark());
        }
        if (postData.getInvoiceTypeEl() != null) {
            billingCycle.setInvoiceTypeEl(postData.getInvoiceTypeEl());
        }
        if (postData.getInvoiceTypeElSpark() != null) {
            billingCycle.setInvoiceTypeElSpark(postData.getInvoiceTypeElSpark());
        }
        if (postData.getTransactionDateDelay() != null) {
            billingCycle.setTransactionDateDelay(postData.getTransactionDateDelay());
        }
        if (postData.getInvoiceDateProductionDelay() != null) {
            billingCycle.setInvoiceDateProductionDelay(postData.getInvoiceDateProductionDelay());
        }
        if (postData.getInvoicingThreshold() != null) {
            billingCycle.setInvoicingThreshold(postData.getInvoicingThreshold());
        }
        if (postData.isThresholdPerEntity() != null) {
            billingCycle.setThresholdPerEntity(postData.isThresholdPerEntity());
        }
        if (postData.getReferenceDate() != null) {
            billingCycle.setReferenceDate(postData.getReferenceDate());
        }
        if (postData.getType() != null) {
            billingCycle.setType(postData.getType());
        }
        if (postData.getCheckThreshold() != null) {
            billingCycle.setCheckThreshold(postData.getCheckThreshold());
        }

        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), billingCycle, true, true);

        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        billingCycle = billingCycleService.update(billingCycle);
    }

    public BillingCycleDto find(String billingCycleCode) throws MeveoApiException {

        if (StringUtils.isBlank(billingCycleCode)) {
            missingParameters.add("billingCycleCode");
            handleMissingParameters();
        }

        BillingCycleDto result = new BillingCycleDto();

        BillingCycle billingCycle = billingCycleService.findByCode(billingCycleCode);
        if (billingCycle == null) {
            throw new EntityDoesNotExistsException(BillingCycle.class, billingCycleCode);
        }

        result = new BillingCycleDto(billingCycle, entityToDtoConverter.getCustomFieldsDTO(billingCycle, CustomFieldInheritanceEnum.INHERIT_NO_MERGE));

        return result;
    }

    public void remove(String billingCycleCode) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(billingCycleCode)) {
            missingParameters.add("billingCycleCode");
            handleMissingParameters();
        }

        BillingCycle billingCycle = billingCycleService.findByCode(billingCycleCode);
        if (billingCycle == null) {
            throw new EntityDoesNotExistsException(BillingCycle.class, billingCycleCode);
        }

        billingCycleService.remove(billingCycle);
    }

    public void createOrUpdate(BillingCycleDto postData) throws MeveoApiException, BusinessException {

        BillingCycle billingCycle = billingCycleService.findByCode(postData.getCode());
        if (billingCycle == null) {
            create(postData);
        } else {
            update(postData);
        }
    }

}
