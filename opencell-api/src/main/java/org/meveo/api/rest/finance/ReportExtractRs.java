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

package org.meveo.api.rest.finance;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.finance.ReportExtractDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.finance.ReportExtractExecutionResultResponseDto;
import org.meveo.api.dto.response.finance.ReportExtractExecutionResultsResponseDto;
import org.meveo.api.dto.response.finance.ReportExtractResponseDto;
import org.meveo.api.dto.response.finance.ReportExtractsResponseDto;
import org.meveo.api.dto.response.finance.RunReportExtractDto;
import org.meveo.api.rest.IBaseRs;

/**
 * API for managing ReportExtract.
 * 
 * @author Edward P. Legaspi
 * @version %I%, %G%
 * @since 5.0
 * @lastModifiedVersion 5.1
 **/
@Path("/finance/reportExtracts")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface ReportExtractRs extends IBaseRs {

	/**
	 * Creates a report extract with the given data.
	 * 
	 * @param postData ReportExtract DTO representation
	 * @return status of the call
	 */
    @POST
    @Path("/")
    ActionStatus create(ReportExtractDto postData);

    /**
     * Updates a report extract with the given data.
     * @param postData ReportExtract DTO representation
     * @return status of the call
     */
    @POST
    @Path("/")
    ActionStatus update(ReportExtractDto postData);

    /**
     * Create / update a report extract with the given data.
     * @param postData ReportExtract DTO representation
     * @return status of the call
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(ReportExtractDto postData);

    /**
     * Delete a Report Extract with a given code. 
     *
     * @param reportExtractCode code of the ReportExtract
     * @return status of the call
     */
    @DELETE
    @Path("/")
    ActionStatus remove(String reportExtractCode);

    /**
     * Returns a paginated list of ReportExtract.
     * 
     * @param query Search criteria
     * @param fields Data retrieval options/fieldnames separated by a comma
     * @param offset Pagination - from record number
     * @param limit Pagination - number of records to retrieve
     * @param sortBy Sorting - field to sort by - a field from a main entity being searched. See Data model for a list of fields.
     * @param sortOrder Sorting - sort order.
     * @return list of ReportExtract
     */
	@GET
	@Path("/list")
	ReportExtractsResponseDto listGet(@QueryParam("query") String query, @QueryParam("fields") String fields,
			@QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit,
			@DefaultValue("code") @QueryParam("sortBy") String sortBy,
			@DefaultValue("DESCENDING") @QueryParam("sortOrder") SortOrder sortOrder);

    /**
     * Returns a list of filtered and paginated ReportExtract.
     * 
     * @param pagingAndFiltering managed filtering and paging
     * @return list of ReportExtract
     */
    @POST
    @Path("/executionHistory/list")
    ReportExtractsResponseDto listPost(PagingAndFiltering pagingAndFiltering);

    /**
     * Search for a report extract with a given code.
     * @param reportExtractCode code to search
     * @return matched report extract
     */
    @GET
    @Path("/")
    ReportExtractResponseDto find(@QueryParam("reportExtractCode") String reportExtractCode);

    /**
     * Runs a report extract with the given parameter.
     * @param postData contains the report extract code and parameters
     * @return status of the call
     */
    @POST
    @Path("/run")
    ReportExtractExecutionResultResponseDto runReport(RunReportExtractDto postData);

    /**
     * Enable a Report extract with a given code
     * 
     * @param code Report extract code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
    ActionStatus enable(@PathParam("code") String code);

    /**
     * Disable a Report extract with a given code
     * 
     * @param code Report extract code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
    ActionStatus disable(@PathParam("code") String code);
    
    /**
     * Returns a list of filtered and paginated report extract execution history.
     * 
     * @param pagingAndFiltering managed filtering and paging
     * @return list of ReportExtract run history
     */
    @POST
    @Path("/executionHistory/list")
    ReportExtractExecutionResultsResponseDto listReportExtractRunHistoryPost(PagingAndFiltering pagingAndFiltering);

    /**
     * Returns a list of filtered and paginated report extract execution history.
     * 
     * @param query Search criteria
     * @param fields Data retrieval options/fieldnames separated by a comma
     * @param offset Pagination - from record number
     * @param limit Pagination - number of records to retrieve
     * @param sortBy Sorting - field to sort by - a field from a main entity being searched. See Data model for a list of fields.
     * @param sortOrder Sorting - sort order.
     * @return list of ReportExtract run history
     */
    @GET
    @Path("/executionHistory/list")
    ReportExtractExecutionResultsResponseDto listReportExtractRunHistoryGet(@QueryParam("query") String query, @QueryParam("fields") String fields, @QueryParam("offset") Integer offset,
            @QueryParam("limit") Integer limit, @DefaultValue("id") @QueryParam("sortBy") String sortBy, @DefaultValue("DESCENDING") @QueryParam("sortOrder") SortOrder sortOrder);

    /**
     * Finds and returns the ReportExtract history of a given id.
     * 
     * @param id ReportExtract execution history
     * @return report extract execution detail
     */
    @GET
    @Path("/executionHistory")
    ReportExtractExecutionResultResponseDto findReportExtractHistory(@QueryParam("id") Long id);
    
    /**
     * Finds and returns a list of ReportExtract history for a given code.
     * 
     * @param code ReportExtract code
     * @return list of report extract execution detail
     */
    @GET
    @Path("/executionHistory")
    ReportExtractExecutionResultsResponseDto findReportExtractHistory(@QueryParam("code") String code);

}