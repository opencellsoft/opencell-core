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

package org.meveo.api.index;

import java.util.Map;
import java.util.concurrent.Future;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.sort.SortOrder;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.exception.AccessDeniedException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.service.index.ElasticClient;
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
            Future<ReindexingStatistics> future = elasticClient.cleanAndReindex(currentUser.unProxy(), true);
            future.get();
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    /**
     * Perform a full text search
     * 
     * @param classnamesOrCetCodes Entity classes or CET codes to search in
     * @param query Query - words (will be joined by AND) or query expression (+word1 - word2)
     * @param from Pagination - starting record
     * @param size Pagination - number of records per page
     * @param sortField string field used to sort the results
     * @param sortOrder ASC or DESC to indicate the order in which results will be returned
     * @return JSON in string form returned by Elastic search
     * @throws MissingParameterException Missing Parameter Exception
     * @throws BusinessException Business Exception
     */
    public String search(String[] classnamesOrCetCodes, String query, Integer from, Integer size, String sortField, SortOrder sortOrder)
            throws MissingParameterException, BusinessException {

        if (classnamesOrCetCodes == null || classnamesOrCetCodes.length == 0) {
            missingParameters.add("classnamesOrCetCodes");
        }

        handleMissingParameters();

        SearchResponse searchResult = elasticClient.search(query, from, size, sortField != null ? new String[] { sortField } : null,
            sortOrder != null ? new SortOrder[] { sortOrder } : null, null, classnamesOrCetCodes);

        if (searchResult != null) {
            return searchResult.toString();
        } else {
            return "{}";
        }
    }

    /**
     * Perform search by fields in Elastic search
     * 
     * @param classnamesOrCetCodes Entity classes or CET codes to search in
     * @param queryValues Fields and values to match
     * @param from Pagination - starting record
     * @param size Pagination - number of records per page
     * @param sortField string field used to sort the results
     * @param sortOrder ASC or DESC to indicate the order in which results will be returned
     * @return JSON in string form returned by Elastic search
     * @throws MissingParameterException Missing Parameter Exception
     * @throws BusinessException Business Exception
     */
    public String search(String[] classnamesOrCetCodes, Map<String, String> queryValues, Integer from, Integer size, String sortField, SortOrder sortOrder)
            throws MissingParameterException, BusinessException {

        if (classnamesOrCetCodes == null || classnamesOrCetCodes.length == 0) {
            missingParameters.add("classnamesOrCetCodes");
        }

        handleMissingParameters();

        SearchResponse searchResult = elasticClient.search(queryValues, from, size, null, null, null, classnamesOrCetCodes);

        if (searchResult != null) {
            return searchResult.toString();
        } else {
            return "{}";
        }
    }
}