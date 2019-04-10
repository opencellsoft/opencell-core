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

    public Filter getFilterFromDto(FilterDto filter, Map<String, String> parameters) throws MeveoApiException {
        Filter result = null;
        if (StringUtils.isBlank(filter.getCode()) && StringUtils.isBlank(filter.getInputXml())) {
            throw new MissingParameterException("code or inputXml");
        }
        if (!StringUtils.isBlank(filter.getCode())) {
            result = filterService.findByCode(filter.getCode());
            if (result == null && StringUtils.isBlank(filter.getInputXml())) {
                throw new EntityDoesNotExistsException(Filter.class, filter.getCode());
            }
            // check if user own the filter
            if (result != null && (result.getShared() == null || !result.getShared())) {
                if (!result.getAuditable().isCreator(currentUser)) {
                    throw new MeveoApiException("INVALID_FILTER_OWNER");
                }
            }
        }

        // if there are parameters we recreate a transient filter by replacing the parameter
        // values in the xml
        if (parameters != null && result != null) {
            String filterXmlInput = replaceCFParameters(result.getInputXml(), parameters);
            result = filterService.parse(filterXmlInput);
        }

        return result;
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
        if (filter.getShared() == null || !filter.getShared()) {
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
            if (filter.getShared() == null || !filter.getShared()) {
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
