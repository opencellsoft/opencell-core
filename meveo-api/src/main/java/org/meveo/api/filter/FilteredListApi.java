package org.meveo.api.filter;

import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.FilterDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.filter.Filter;
import org.meveo.service.filter.FilterService;
import org.meveo.service.index.ElasticClient;
import org.meveo.service.index.ElasticSearchClassInfo;

@Stateless
public class FilteredListApi extends BaseApi {

    @Inject
    private FilterService filterService;

    @Inject
    private ElasticClient elasticClient;
    
    public Filter getFilterFromDto(FilterDto filter, User currentUser) throws MeveoApiException {
    	Filter result = null;
        if(StringUtils.isBlank(filter.getCode())&&StringUtils.isBlank(filter.getInputXml())){
        	throw new MissingParameterException("code or inputXml");
        }
        if(!StringUtils.isBlank(filter.getCode())){
        	result = filterService.findByCode(filter.getCode(), currentUser.getProvider());
        	if(result==null && StringUtils.isBlank(filter.getInputXml())){
                throw new EntityDoesNotExistsException(Filter.class, filter.getCode());
        	}
        	 // check if user own the filter
            if (result.getShared() == null || !result.getShared()) {
                if (result.getAuditable().getCreator().getId() != currentUser.getId()) {
                    throw new MeveoApiException("INVALID_FILTER_OWNER");
                }
            }
        }
        if (result == null) {
        	result = filterService.parse(filter.getInputXml());
        } 
        return result;
    }

    public String listByFilter(FilterDto filter,int firstRow, int numberOfRows, User currentUser)  throws MeveoApiException {
        String result = "";
        Filter filterEntity = getFilterFromDto(filter,currentUser);
        try {
			result = filterService.filteredList(filterEntity, firstRow, numberOfRows,currentUser);
        } catch (BusinessException e) {
            throw new MeveoApiException(e.getMessage());
        }
        return result;    	
    }
    
    
    
    public String search(String[] classnamesOrCetCodes, String query, Integer from, Integer size, User currentUser) throws MissingParameterException, BusinessException {

        if (classnamesOrCetCodes == null || classnamesOrCetCodes.length == 0) {
            missingParameters.add("classnamesOrCetCodes");
        }

        handleMissingParameters();

        List<ElasticSearchClassInfo> classInfo = elasticClient.getSearchScopeInfo(classnamesOrCetCodes, false, currentUser);

        return elasticClient.search(query, from, size, null, null, null, currentUser, classInfo);
    }

    public String search(String[] classnamesOrCetCodes, Map<String, String> queryValues, Integer from, Integer size, User currentUser) throws MissingParameterException,
            BusinessException {

        if (classnamesOrCetCodes == null || classnamesOrCetCodes.length == 0) {
            missingParameters.add("classnamesOrCetCodes");
        }

        handleMissingParameters();

        List<ElasticSearchClassInfo> classInfo = elasticClient.getSearchScopeInfo(classnamesOrCetCodes, false, currentUser);

        return elasticClient.search(queryValues, from, size, null, null, null, currentUser, classInfo);
    }
}
