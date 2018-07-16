package org.meveo.api.filter;

import java.util.List;
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
import org.meveo.service.index.ElasticClient;
import org.meveo.service.index.ElasticSearchClassInfo;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 **/
@Stateless
@Deprecated
public class FilteredList4_3Api extends BaseApi {

    @Inject
    private FilterService filterService;

    @Inject
    private ElasticClient elasticClient;

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
    
    public Filter getFilterFromDto(FilterDto filter) throws MeveoApiException {
    	Filter result = null;
        if(StringUtils.isBlank(filter.getCode())&&StringUtils.isBlank(filter.getInputXml())){
        	throw new MissingParameterException("code or inputXml");
        }
        if(!StringUtils.isBlank(filter.getCode())){
        	result = filterService.findByCode(filter.getCode());
        	if(result==null && StringUtils.isBlank(filter.getInputXml())){
                throw new EntityDoesNotExistsException(Filter.class, filter.getCode());
        	}
        	 // check if user own the filter
            if (result != null && (result.getShared() == null || !result.getShared())) {
                if (!result.getAuditable().isCreator(currentUser)) {
                    throw new MeveoApiException("INVALID_FILTER_OWNER");
                }
            }
        }
        if (result == null) {
        	result = filterService.parse(filter.getInputXml());
        } 
        return result;
    }

    public String listByFilter(FilterDto filter,int firstRow, int numberOfRows)  throws MeveoApiException {
        String result = "";
        Filter filterEntity = getFilterFromDto(filter);
        try {
			result = filterService.filteredList(filterEntity, firstRow, numberOfRows);
        } catch (BusinessException e) {
            throw new MeveoApiException(e.getMessage());
        }
        return result;    	
    }
    
    
    
    public String search(String[] classnamesOrCetCodes, String query, Integer from, Integer size) throws MissingParameterException, BusinessException {

        if (classnamesOrCetCodes == null || classnamesOrCetCodes.length == 0) {
            missingParameters.add("classnamesOrCetCodes");
        }

        handleMissingParameters();

        List<ElasticSearchClassInfo> classInfo = elasticClient.getSearchScopeInfo(classnamesOrCetCodes, false);

        return elasticClient.search(query, null, from, size, null, null, null, classInfo);
    }

    public String search(String[] classnamesOrCetCodes, Map<String, String> queryValues, Integer from, Integer size) throws MissingParameterException,
            BusinessException {

        if (classnamesOrCetCodes == null || classnamesOrCetCodes.length == 0) {
            missingParameters.add("classnamesOrCetCodes");
        }

        handleMissingParameters();

        List<ElasticSearchClassInfo> classInfo = elasticClient.getSearchScopeInfo(classnamesOrCetCodes, false);

        return elasticClient.search(queryValues, from, size, null, null, null, classInfo);
    }
}
