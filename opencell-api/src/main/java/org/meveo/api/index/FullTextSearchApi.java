package org.meveo.api.index;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.elasticsearch.search.sort.SortOrder;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.exception.AccessDeniedException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.service.index.ElasticClient;
import org.meveo.service.index.ElasticSearchClassInfo;
import org.meveo.service.index.ReindexingStatistics;

/**
 * Provides APIs for conducting Full Text Search through Elasticsearch.
 *
 * @author Edward P. Legaspi
 * @author Tony Alejandro
 * @lastModifiedVersion 5.0
 **/
@Stateless
public class FullTextSearchApi extends BaseApi {

    @Inject
    private ElasticClient elasticClient;

    public void cleanAndReindex() throws AccessDeniedException, BusinessException {

        if (!currentUser.hasRole("superAdminManagement")) {
            throw new AccessDeniedException("Super administrator permission is required to clean and reindex full text search");
        }
        try {
            Future<ReindexingStatistics> future = elasticClient.cleanAndReindex(currentUser.unProxy());
            future.get();
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    public String search(String[] classnamesOrCetCodes, String query, Integer from, Integer size) throws MissingParameterException, BusinessException {

        if (classnamesOrCetCodes == null || classnamesOrCetCodes.length == 0) {
            missingParameters.add("classnamesOrCetCodes");
        }

        handleMissingParameters();

        List<ElasticSearchClassInfo> classInfo = elasticClient.getSearchScopeInfo(classnamesOrCetCodes, false);

        return elasticClient.search(query, null, from, size, null, null, null, classInfo);
    }

    public String search(String[] classnamesOrCetCodes, Map<String, String> queryValues, Integer from, Integer size) throws MissingParameterException, BusinessException {

        if (classnamesOrCetCodes == null || classnamesOrCetCodes.length == 0) {
            missingParameters.add("classnamesOrCetCodes");
        }

        handleMissingParameters();

        List<ElasticSearchClassInfo> classInfo = elasticClient.getSearchScopeInfo(classnamesOrCetCodes, false);

        return elasticClient.search(queryValues, from, size, null, null, null, classInfo);
    }

    /**
     *
     * @param query string term to be searched
     * @param category search by category that is directly taken from the name of the entity found in entityMapping. property of elasticSearchConfiguration.json. e.g. Customer,
     *        CustomerAccount, AccountOperation, etc. See elasticSearchConfiguration.json entityMapping keys for a list of categories.
     * @param from number the index where search results will start from, used in pagination
     * @param size number the maximum number of results to return
     * @param sortField string field used to sort the results
     * @param sortOrder ASC or DESC to indicate the order in which results will be returned
     * @return JSON in string form returned by Elasticsearch
     * @throws MissingParameterException Missing Parameter Exception
     * @throws BusinessException Business Exception
     */
    public String fullSearch(String query, String category, Integer from, Integer size, String sortField, SortOrder sortOrder) throws MissingParameterException, BusinessException {

        boolean noCategory = StringUtils.isBlank(category);
        boolean noQuery = StringUtils.isBlank(query);

        if (noCategory && noQuery) {
            missingParameters.add("category");
            missingParameters.add("query");
        }

        handleMissingParameters();

        return elasticClient.search(query, category, from, size, sortField != null ? new String[] { sortField } : null, sortOrder != null ? new SortOrder[] { sortOrder } : null,
            null, null);
    }
}
