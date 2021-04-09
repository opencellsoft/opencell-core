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

package org.meveo.api.rest.tax;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.tax.TaxMappingListResponseDto;
import org.meveo.api.dto.response.tax.TaxMappingResponseDto;
import org.meveo.api.dto.tax.TaxMappingDto;
import org.meveo.api.rest.IBaseRs;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * REST interface definition of Tax mapping API
 **/
@Path("/taxMapping")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface TaxMappingRs extends IBaseRs {

    /**
     * Create a new Tax mapping
     * 
     * @param dto The Tax mapping's data
     * @return Request processing status
     */
    @POST
    @Path("/")
    ActionStatus create(TaxMappingDto dto);

    /**
     * Search for a Tax mapping with a given id
     * 
     * @param id The Tax mapping's id
     * @return A Tax mapping's data
     */
    @GET
    @Path("/")
    TaxMappingResponseDto find(@QueryParam("id") String id);

    /**
     * Search Tax mapping by matching a given criteria
     * 
     * @param query Search criteria
     * @param fields Data retrieval options/fieldnames separated by a comma
     * @param offset Pagination - from record number
     * @param limit Pagination - number of records to retrieve
     * @param sortBy Sorting - field to sort by - a field from a main entity being searched. See Data model for a list of fields.
     * @param sortOrder Sorting - sort order.
     * @return List of Tax mappings
     */
    @GET
    @Path("/list")
    TaxMappingListResponseDto searchGet(@QueryParam("query") String query, @QueryParam("fields") String fields, @QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit,
            @DefaultValue("id") @QueryParam("sortBy") String sortBy, @DefaultValue("ASCENDING") @QueryParam("sortOrder") SortOrder sortOrder);

    /**
     * List taxMappings matching a given criteria
     *
     * @return List of taxMappings
     */
    @GET
    @Path("/listGetAll")
    TaxMappingListResponseDto listGetAll();

    /**
     * Search for Tax mapping by matching a given criteria
     * 
     * @param pagingAndFiltering Pagination and filtering criteria
     * @return List of Tax mappings
     */
    @POST
    @Path("/list")
    TaxMappingListResponseDto searchPost(PagingAndFiltering pagingAndFiltering);

    /**
     * Update an existing Tax mapping
     * 
     * @param dto The Tax mapping's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
    ActionStatus update(TaxMappingDto dto);

    /**
     * Remove an existing Tax mapping with a given id
     * 
     * @param id The Tax mapping's id
     * @return Request processing status
     */
    @DELETE
    @Path("/{id}")
    ActionStatus remove(@PathParam("id") String id);

    /**
     * Create new or update an existing Tax mapping
     * 
     * @param dto The Tax mapping's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(TaxMappingDto dto);
}