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

package org.meveo.api.filter;

import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.FilterDto;
import org.meveo.api.dto.filter.FilteredListDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.filter.Filter;
import org.meveo.service.filter.FilterService;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 **/
@Stateless
public class FilteredListApi extends BaseApi {

    @Inject
    private FilterService filterService;

    public Filter getFilterFromDto(FilterDto filter) throws MeveoApiException {
        return getFilterFromDto(filter, null);
    }

    public Filter getFilterFromDto(FilterDto filterDto, Map<String, String> parameters) throws MeveoApiException {
        Filter filter = null;
        if (StringUtils.isBlank(filterDto.getCode()) && StringUtils.isBlank(filterDto.getInputXml())) {
            throw new MissingParameterException("code or inputXml");
        }
        if (!StringUtils.isBlank(filterDto.getCode())) {
            filter = filterService.findByCode(filterDto.getCode());
            if (filter == null && StringUtils.isBlank(filterDto.getInputXml())) {
                throw new EntityDoesNotExistsException(Filter.class, filterDto.getCode());
            }
            // check if user own the filter
            if (filter != null && !filter.getShared()) {
                if (!filter.getAuditable().isCreator(currentUser)) {
                    throw new MeveoApiException("INVALID_FILTER_OWNER");
                }
            }
        }

        // if there are parameters we recreate a transient filter by replacing the parameter
        // values in the xml
        if (parameters != null && filter != null) {
            String filterXmlInput = replaceCFParameters(filter.getInputXml(), parameters);
            filter = filterService.parse(filterXmlInput);
        }

        return filter;
    }

    private String replaceCFParameters(String xmlInput, Map<String, String> parameters) {
        String result = xmlInput;

        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            result = result.replaceAll("cf(.*):" + entry.getKey(), entry.getValue());
        }

        log.debug("replaced filter xml :" + result);

        return result;
    }

    public String listByFilter(FilterDto filter, Integer firstRow, Integer numberOfRows) throws MeveoApiException, BusinessException {
        return listByFilter(filter, firstRow, numberOfRows, null);
    }

    public String listByFilter(FilterDto filter, Integer firstRow, Integer numberOfRows, Map<String, String> parameters) throws MeveoApiException, BusinessException {

        String result = "";
        Filter filterEntity = getFilterFromDto(filter, parameters);
        result = filterService.filteredList(filterEntity, firstRow, numberOfRows);
        return result;
    }

    @Deprecated
    // in 4.4
    public String list(String filterCode, Integer firstRow, Integer numberOfRows) throws MeveoApiException {
        String result = "";

        Filter filter = filterService.findByCode(filterCode);
        if (filter == null) {
            throw new EntityDoesNotExistsException(Filter.class, filterCode);
        }

        // check if user owned the filter
        if (!filter.getShared()) {
            if (!filter.getAuditable().isCreator(currentUser)) {
                throw new MeveoApiException("INVALID_FILTER_OWNER");
            }
        }

        try {
            result = filterService.filteredList(filter, firstRow, numberOfRows);
        } catch (BusinessException e) {
            throw new MeveoApiException(e.getMessage());
        }

        return result;
    }

    @Deprecated
    // in 4.4
    public String listByXmlInput(FilteredListDto postData) throws MeveoApiException {
        String result = "";

        try {
            Filter filter = filterService.parse(postData.getXmlInput());

            // check if user owned the filter
            if (!filter.getShared()) {
                if (filter.getAuditable() != null) {
                    if (!filter.getAuditable().isCreator(currentUser)) {
                        throw new MeveoApiException("INVALID_FILTER_OWNER");
                    }
                }
            }

            result = filterService.filteredList(filter, postData.getFirstRow(), postData.getNumberOfRows());
        } catch (BusinessException e) {
            throw new MeveoApiException(e.getMessage());
        }

        return result;
    }

}
