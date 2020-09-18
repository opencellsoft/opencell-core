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
import org.meveo.api.dto.FilterDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.filter.Filter;
import org.meveo.service.filter.FilterService;

/**
 * @author Tyshan Shi
 * 
 **/
@Stateless
public class FilterApi extends BaseCrudApi<Filter, FilterDto> {

    @Inject
    private FilterService filterService;

    @Override
    public Filter create(FilterDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getInputXml()) && StringUtils.isBlank(postData.getPollingQuery())) {
            missingParameters.add("inputXml or pollingQuery");
        }

        handleMissingParametersAndValidate(postData);

        Filter filter = mapDtoToFilter(postData, null);
        filterService.create(filter);

        return filter;
    }

    @Override
    public Filter update(FilterDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getInputXml()) && StringUtils.isBlank(postData.getPollingQuery())) {
            missingParameters.add("inputXml or pollingQuery");
        }

        handleMissingParametersAndValidate(postData);

        Filter filter = filterService.findByCode(postData.getCode());

        if (filter == null) {
            throw new EntityDoesNotExistsException(Filter.class, postData.getCode());
        }

        filter = mapDtoToFilter(postData, filter);
        filter = filterService.update(filter);

        return filter;
    }

    private Filter mapDtoToFilter(FilterDto dto, Filter filterToUpdate) {
        Filter filter = filterToUpdate;

        if (filter == null) {
            filter = new Filter();
            filter.setCode(dto.getCode());
            filter.clearUuid();

            if (dto.isDisabled() != null) {
                filter.setDisabled(dto.isDisabled());
            }
        }

        filter.setCode(StringUtils.isBlank(dto.getUpdatedCode()) ? dto.getCode() : dto.getUpdatedCode());
        filter.setDescription(dto.getDescription());
        filter.setInputXml(dto.getInputXml());
        filter.setPollingQuery(dto.getPollingQuery());
        filter.setShared(dto.getShared());

        return filter;
    }

    @Override
    public FilterDto find(String code) throws EntityDoesNotExistsException, MissingParameterException, InvalidParameterException, MeveoApiException {
        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        Filter filter = filterService.findByCode(code);

        if (filter == null) {
            throw new EntityDoesNotExistsException(Filter.class, code);
        }

        return new FilterDto(filter);
    }
}