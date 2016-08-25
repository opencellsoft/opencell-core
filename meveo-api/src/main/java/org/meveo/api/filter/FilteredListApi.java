package org.meveo.api.filter;

import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.filter.FilteredListDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.model.admin.User;
import org.meveo.model.filter.Filter;
import org.meveo.service.filter.FilterService;
import org.meveo.service.index.ElasticClient;
import org.meveo.service.index.ElasticSearchClassInfo;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class FilteredListApi extends BaseApi {

    @Inject
    private FilterService filterService;

    @Inject
    private ElasticClient elasticClient;

    public String list(String filterCode, Integer firstRow, Integer numberOfRows, User currentUser) throws MeveoApiException {
        String result = "";

        Filter filter = filterService.findByCode(filterCode, currentUser.getProvider());
        if (filter == null) {
            throw new EntityDoesNotExistsException(Filter.class, filterCode);
        }

        // check if user owned the filter
        if (filter.getShared() == null || !filter.getShared()) {
            if (filter.getAuditable().getCreator().getId() != currentUser.getId()) {
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

    public String listByXmlInput(FilteredListDto postData, User currentUser) throws MeveoApiException {
        String result = "";

        try {
            Filter filter = filterService.parse(postData.getXmlInput());

            // check if user owned the filter
            if (filter.getShared() == null || !filter.getShared()) {
                if (filter.getAuditable() != null) {
                    if (filter.getAuditable().getCreator().getId() != currentUser.getId()) {
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
