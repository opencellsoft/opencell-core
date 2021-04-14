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
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.BaseEntityDto;
import org.meveo.api.dto.catalog.CounterTemplateDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.AccumulatorCounterTypeEnum;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.catalog.CounterTemplateLevel;
import org.meveo.model.catalog.CounterTypeEnum;
import org.meveo.service.catalog.impl.CalendarService;
import org.meveo.service.catalog.impl.CounterTemplateService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.math.BigDecimal;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class CounterTemplateApi extends BaseCrudApi<CounterTemplate, CounterTemplateDto> {

    @Inject
    private CounterTemplateService counterTemplateService;

    @Inject
    private CalendarService calendarService;

    @Override
    public CounterTemplate create(CounterTemplateDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            addGenericCodeIfAssociated(CounterTemplate.class.getName(), postData);
        }
        if (StringUtils.isBlank(postData.getCalendar())) {
            missingParameters.add("calendar");
        }

        handleMissingParametersAndValidate(postData);
        if (counterTemplateService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(CounterTemplate.class, postData.getCode());
        }
        CounterTemplate counterTemplate = new CounterTemplate();
         counterTemplate = fromDto(counterTemplate, postData);
        counterTemplateService.create(counterTemplate);

        return counterTemplate;
    }



    @Override
    public CounterTemplate update(CounterTemplateDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getCalendar())) {
            missingParameters.add("calendar");
        }

        handleMissingParametersAndValidate(postData);

        CounterTemplate counterTemplate = counterTemplateService.findByCode(postData.getCode());
        if (counterTemplate == null) {
            throw new EntityDoesNotExistsException(CounterTemplate.class, postData.getCode());
        }

        counterTemplate = fromDto(counterTemplate, postData);
        counterTemplate = counterTemplateService.update(counterTemplate);

        return counterTemplate;
    }

    private CounterTemplate fromDto(CounterTemplate counterTemplate, CounterTemplateDto postData) {

        Calendar calendar = calendarService.findByCode(postData.getCalendar());
        if (calendar == null) {
            throw new EntityDoesNotExistsException(Calendar.class, postData.getCalendar());
        }


        counterTemplate.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());
        counterTemplate.setDescription(postData.getDescription());
        counterTemplate.setUnityDescription(postData.getUnity());
        if (postData.getType() != null) {
            counterTemplate.setCounterType(postData.getType());
        }
        counterTemplate.setCeiling(postData.getCeiling());
        if (postData.isDisabled() != null) {
            counterTemplate.setDisabled(postData.isDisabled());
        }
        counterTemplate.setCalendar(calendar);
        counterTemplate.setCalendarCodeEl(postData.getCalendarCodeEl());
        if (postData.getCounterLevel() != null) {
            counterTemplate.setCounterLevel(postData.getCounterLevel());
        }
        counterTemplate.setCeilingExpressionEl(postData.getCeilingExpressionEl());
        counterTemplate.setNotificationLevels(postData.getNotificationLevels());
        Boolean isAccumulator = postData.getAccumulator() != null && postData.getAccumulator();
        counterTemplate.setAccumulator(isAccumulator);
        if (isAccumulator) {
            counterTemplate.setCeilingExpressionEl(null);
            counterTemplate.setCeiling(BigDecimal.ZERO);
            counterTemplate.setAccumulatorType(postData.getAccumulatorType());
            if(postData.getAccumulatorType() != null && postData.getAccumulatorType().equals(AccumulatorCounterTypeEnum.MULTI_VALUE)) {
                counterTemplate.setFilterEl(postData.getFilterEl());
                counterTemplate.setKeyEl(postData.getKeyEl());
                counterTemplate.setValueEl(postData.getValueEl());
            }
        }

        return counterTemplate;
    }

    @Override
    public CounterTemplateDto find(String code) throws EntityDoesNotExistsException, MissingParameterException, InvalidParameterException, MeveoApiException {
        if (StringUtils.isBlank(code)) {
            missingParameters.add("counterTemplateCode");
            handleMissingParameters();
        }
        CounterTemplate counterTemplate = counterTemplateService.findByCode(code);
        if (counterTemplate == null) {
            throw new EntityDoesNotExistsException(CounterTemplate.class, code);
        }

        return new CounterTemplateDto(counterTemplate);
    }

    /**
     * Check if any parameters are missing and throw and exception.
     *
     * @param dto base data transfer object.
     * @throws MeveoApiException meveo api exception.
     */
    @Override
    protected void handleMissingParametersAndValidate(BaseEntityDto dto) throws MeveoApiException {
        validate(dto);
        handleMissingParameters(dto);
        CounterTemplateDto counterTemplateDto = (CounterTemplateDto) dto;
        if (counterTemplateDto.getAccumulator() != null) {
            if (counterTemplateDto.getAccumulator() && counterTemplateDto.getType().equals(CounterTypeEnum.NOTIFICATION)) {
                log.error("The counter type is invalid if the counter is accumulator counter, deactivate the accumulator or change the counter type");
                throw new InvalidParameterException("The counter type is invalid if the counter is accumulator counter, deactivate the accumulator or change the counter type");
            }
            if (!counterTemplateDto.getAccumulator() && (counterTemplateDto.getType().equals(CounterTypeEnum.USAGE_AMOUNT))) {
                log.error("The accumulator should be activated if the following counter type are used : {}, {} or {}", CounterTypeEnum.USAGE_AMOUNT.getLabel(),
                        CounterTypeEnum.USAGE_AMOUNT.getLabel(), CounterTypeEnum.USAGE_AMOUNT.getLabel());
                throw new InvalidParameterException("The accumulator must be activated to use the counter type " + counterTemplateDto.getType());
            }
            if (!counterTemplateDto.getAccumulator() && counterTemplateDto.getType().equals(CounterTypeEnum.USAGE)
                    && (counterTemplateDto.getCounterLevel().equals(CounterTemplateLevel.CA) || counterTemplateDto.getCounterLevel().equals(CounterTemplateLevel.CUST))) {
                log.error("The accumulator should be activated if the following counter level are used : {}, {}", CounterTemplateLevel.CA.getLabel(),
                        CounterTemplateLevel.CUST.getLabel());
                throw new InvalidParameterException("The accumulator must be activated to use the counter level " + counterTemplateDto.getCounterLevel());
            }
        }
    }
}