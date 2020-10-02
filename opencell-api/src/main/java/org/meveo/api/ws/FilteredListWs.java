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

package org.meveo.api.ws;

import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.elasticsearch.search.sort.SortOrder;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.FilterDto;
import org.meveo.api.dto.response.billing.FilteredListResponseDto;

@WebService
@Deprecated
public interface FilteredListWs extends IBaseWs {

    /**
     * Execute a filter to retrieve a list of entities
     * 
     * @param filter - if the code is set we lookup the filter in DB, else we parse the inputXml to create a transient filter
     * @param from Pagination - starting record
     * @param size Pagination - number of records per page
     * @param parameters - filter parameters
     * @return FilteredListResponseDto
     */
    @WebMethod
    public FilteredListResponseDto listByFilter(@WebParam(name = "filter") FilterDto filter, @WebParam(name = "from") Integer from, @WebParam(name = "size") Integer size,
            @WebParam(name = "parameters") Map<String, String> parameters);

    /**
     * Execute a search in Elastic Search on all fields (_all field)
     * 
     * @param classnamesOrCetCodes Entity classes to match - full class name
     * @param query Query - words (will be joined by AND) or query expression (+word1 - word2)
     * @param from Pagination - starting record
     * @param size Pagination - number of records per page
     * @param sortField Pagination - field used to sort the results
     * @param sortOrder Pagination - ASC or DESC order of the results
     * @return FilteredListResponseDto
     */
    @WebMethod
    public FilteredListResponseDto search(@WebParam(name = "classnamesOrCetCodes") String[] classnamesOrCetCodes, @WebParam(name = "query") String query,
            @WebParam(name = "from") Integer from, @WebParam(name = "size") Integer size, @WebParam(name = "sortField") String sortField,
            @WebParam(name = "sortOrder") SortOrder sortOrder);

    /**
     * Execute a search in Elastic Search on given fields for given values
     * 
     * @param classnamesOrCetCodes Entity classes to match - full class name
     * @param query Fields and values to match in a form of a map
     * @param from Pagination - starting record
     * @param size Pagination - number of records per page
     * @param sortField Pagination - field used to sort the results
     * @param sortOrder Pagination - ASC or DESC order of the results
     * @return FilteredListResponseDto
     */
    @WebMethod
    public FilteredListResponseDto searchByField(@WebParam(name = "classnamesOrCetCodes") String[] classnamesOrCetCodes, @WebParam(name = "query") Map<String, String> query,
            @WebParam(name = "from") Integer from, @WebParam(name = "size") Integer size, @WebParam(name = "sortField") String sortField,
            @WebParam(name = "sortOrder") SortOrder sortOrder);

    /**
     * Clean and reindex Elastic Search repository
     * 
     * @return ActionStatus
     */
    public ActionStatus reindex();
}