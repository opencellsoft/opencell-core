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

package org.meveo.api.catalog;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.catalog.RecurringChargeTemplateDto;
import org.meveo.api.exception.*;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.LevelEnum;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.service.catalog.impl.CalendarService;
import org.meveo.service.catalog.impl.RecurringChargeTemplateService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.function.BiFunction;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class RecurringChargeTemplateApi extends ChargeTemplateApi<RecurringChargeTemplate, RecurringChargeTemplateDto> {

    @Inject
    private RecurringChargeTemplateService recurringChargeTemplateService;

    @Inject
    private CalendarService calendarService;

    @Override
    public RecurringChargeTemplate create(RecurringChargeTemplateDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            addGenericCodeIfAssociated(RecurringChargeTemplate.class.getName(), postData);
        }
        if (StringUtils.isBlank(postData.getInvoiceSubCategory())) {
            missingParameters.add("invoiceSubCategory");
        }
        if (StringUtils.isBlank(postData.getCalendar())) {
            missingParameters.add("calendar");
        }
        if (StringUtils.isBlank(postData.getTaxClassCode())) {
            missingParameters.add("taxClassCode");
        }

        handleMissingParametersAndValidate(postData);

        // check if code already exists
        if (recurringChargeTemplateService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(RecurringChargeTemplate.class, postData.getCode());
        }

        RecurringChargeTemplate chargeTemplate = dtoToEntity(postData, null);

        recurringChargeTemplateService.create(chargeTemplate);
        return chargeTemplate;
    }

    @Override
    public RecurringChargeTemplate update(RecurringChargeTemplateDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (postData.getInvoiceSubCategory() != null && StringUtils.isBlank(postData.getInvoiceSubCategory())) {
            missingParameters.add("invoiceSubCategory");
        }
        if (postData.getTaxClassCode() != null && StringUtils.isBlank(postData.getTaxClassCode())) {
            missingParameters.add("taxClassCode");
        }
        if (postData.getCalendar() != null && StringUtils.isBlank(postData.getCalendar())) {
            missingParameters.add("calendar");
        }

        handleMissingParametersAndValidate(postData);

        // check if code already exists
        RecurringChargeTemplate chargeTemplate = recurringChargeTemplateService.findByCode(postData.getCode());
        if (chargeTemplate == null) {
            throw new EntityDoesNotExistsException(RecurringChargeTemplate.class, postData.getCode());
        }

        chargeTemplate = dtoToEntity(postData, chargeTemplate);

        chargeTemplate = recurringChargeTemplateService.update(chargeTemplate);

        return chargeTemplate;
    }

    /**
     * Convert/update DTO object to an entity object
     *
     * @param postData DTO object
     * @param chargeTemplate Entity object to update
     * @return A new or updated entity object
     * @throws MeveoApiException General API exception
     * @throws BusinessException General exception
     */
    private RecurringChargeTemplate dtoToEntity(RecurringChargeTemplateDto postData, RecurringChargeTemplate chargeTemplate) throws MeveoApiException, BusinessException {

        boolean isNew = chargeTemplate == null;

        if (isNew) {
            chargeTemplate = new RecurringChargeTemplate();
            chargeTemplate.setCode(postData.getCode());
        } else {
            chargeTemplate.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());
        }

        super.dtoToEntity(postData, chargeTemplate, isNew);

        if (postData.getCalendar() != null) {
            Calendar calendar = calendarService.findByCode(postData.getCalendar());
            if (calendar == null) {
                throw new EntityDoesNotExistsException(Calendar.class, postData.getCalendar());
            }

            chargeTemplate.setCalendar(calendar);
        }

        if (postData.getDurationTermInMonth() != null) {
            chargeTemplate.setDurationTermInMonth(postData.getDurationTermInMonth());
        }
        if (postData.getSubscriptionProrata() != null) {
            chargeTemplate.setSubscriptionProrata(postData.getSubscriptionProrata());
        }
        if (postData.getTerminationProrata() != null) {
            chargeTemplate.setTerminationProrata(postData.getTerminationProrata());
        }
        if (postData.getApplyInAdvance() != null) {
            chargeTemplate.setApplyInAdvance(postData.getApplyInAdvance());
        }
        if (postData.getShareLevel() != null) {
            chargeTemplate.setShareLevel(LevelEnum.getValue(postData.getShareLevel()));
        }
        if (postData.getDurationTermInMonthEl() != null) {
            chargeTemplate.setDurationTermInMonthEl(StringUtils.getDefaultIfEmpty(postData.getDurationTermInMonthEl(), null));
        }
        if (postData.getSubscriptionProrataEl() != null) {
            chargeTemplate.setSubscriptionProrataEl(StringUtils.getDefaultIfEmpty(postData.getSubscriptionProrataEl(), null));
        }
        if (postData.getTerminationProrataEl() != null) {
            chargeTemplate.setTerminationProrataEl(StringUtils.getDefaultIfEmpty(postData.getTerminationProrataEl(), null));
        }
        if (postData.getApplyInAdvanceEl() != null) {
            chargeTemplate.setApplyInAdvanceEl(StringUtils.getDefaultIfEmpty(postData.getApplyInAdvanceEl(), null));
        }
        if (postData.getCalendarCodeEl() != null) {
            chargeTemplate.setCalendarCodeEl(StringUtils.getDefaultIfEmpty(postData.getCalendarCodeEl(), null));
        }
        if (postData.getApplyTerminatedChargeToDateEL() != null) {
            chargeTemplate.setApplyTerminatedChargeToDateEL(StringUtils.getDefaultIfEmpty(postData.getApplyTerminatedChargeToDateEL(), null));
        }

        return chargeTemplate;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.meveo.api.ApiService#find(java.lang.String)
     */
    @Override
    public RecurringChargeTemplateDto find(String code) throws EntityDoesNotExistsException, MissingParameterException, InvalidParameterException, MeveoApiException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("recurringChargeTemplateCode");
            handleMissingParameters();
        }

        // check if code already exists
        RecurringChargeTemplate chargeTemplate = recurringChargeTemplateService.findByCode(code, Arrays.asList("invoiceSubCategory", "calendar"));
        if (chargeTemplate == null) {
            throw new EntityDoesNotExistsException(RecurringChargeTemplate.class, code);
        }

        RecurringChargeTemplateDto result = new RecurringChargeTemplateDto(chargeTemplate, entityToDtoConverter.getCustomFieldsDTO(chargeTemplate, CustomFieldInheritanceEnum.INHERIT_NO_MERGE));

        return result;
    }

    @Override
    protected BiFunction<RecurringChargeTemplate, CustomFieldsDto, RecurringChargeTemplateDto> getEntityToDtoFunction() {
        return RecurringChargeTemplateDto::new;
    }
}