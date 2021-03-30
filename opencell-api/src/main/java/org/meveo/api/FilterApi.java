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
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.FilterDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
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
            addGenericCodeIfAssociated(Filter.class.getName(), postData);
        }
        if (StringUtils.isBlank(postData.getInputXml()) && StringUtils.isBlank(postData.getPollingQuery())) {
            missingParameters.add("inputXml or pollingQuery");
        }

        handleMissingParametersAndValidate(postData);

        Filter filter = new Filter();
        dtoToEntity(postData, filter);
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

        filter = dtoToEntity(postData, filter);
        filter = filterService.update(filter);

        return filter;
    }

    private Filter dtoToEntity(FilterDto dto, Filter filter) {

        boolean isNew = filter.getId() == null;

        if (isNew) {
            filter.setCode(dto.getCode());
            if (dto.isDisabled() != null) {
                filter.setDisabled(dto.isDisabled());
            }
        } else if (!StringUtils.isBlank(dto.getUpdatedCode())) {
            filter.setCode(dto.getUpdatedCode());
        }

        if (dto.getDescription() != null) {
            filter.setDescription(StringUtils.isEmpty(dto.getDescription()) ? null : dto.getDescription());
        }
        if (dto.getInputXml() != null) {
            filter.setInputXml(StringUtils.isEmpty(dto.getInputXml()) ? null : dto.getInputXml());
        }
        if (dto.getPollingQuery() != null) {
            filter.setPollingQuery(StringUtils.isEmpty(dto.getPollingQuery()) ? null : dto.getPollingQuery());
        }

        if (dto.getEntityClass() != null) {
            filter.setEntityClass(StringUtils.isEmpty(dto.getEntityClass()) ? null : dto.getEntityClass());
        }

        if (dto.getShared() != null) {
            filter.setShared(dto.getShared());
        }

        // populate customFields
        try {
            populateCustomFields(dto.getCustomFields(), filter, isNew, true);

        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        return filter;
    }

    @Override
    protected BiFunction<Filter, CustomFieldsDto, FilterDto> getEntityToDtoFunction() {
        return FilterDto::new;
    }
}