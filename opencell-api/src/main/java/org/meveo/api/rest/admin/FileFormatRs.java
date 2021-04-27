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

package org.meveo.api.rest.admin;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.admin.FileFormatDto;
import org.meveo.api.dto.admin.FileFormatListResponseDto;
import org.meveo.api.dto.admin.FileFormatResponseDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.rest.IBaseRs;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * File format resource
 *
 * @author Abdellatif BARI
 * @since 8.0.0
 */
@Path("/admin/fileFormat")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface FileFormatRs extends IBaseRs {

    /**
     * Create a new File format
     *
     * @param postData The File format's data
     * @return Request processing status
     */
    @POST
    @Path("/")
    ActionStatus create(FileFormatDto postData);

    /**
     * Update an existing File format
     * 
     * @param dto The File format's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
    ActionStatus update(FileFormatDto dto);

    /**
     * Remove an existing File format with a given code
     *
     * @param code File format's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{code}")
    ActionStatus remove(@PathParam("code") String code);

    /**
     * Create new or update an existing File formats
     * 
     * @param dto The File format data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(FileFormatDto dto);

    /**
     * Search for a File format with a given code
     * 
     * @param code The File format's code
     * @return A File format's data
     */
    @GET
    @Path("/")
    FileFormatResponseDto find(@QueryParam("code") String code);

    /**
     * Search File formats by matching a given criteria
     * 
     * @param query Search criteria
     * @param fields Data retrieval options/fieldnames separated by a comma
     * @param offset Pagination - from record number
     * @param limit Pagination - number of records to retrieve
     * @param sortBy Sorting - field to sort by - a field from a main entity being searched. See Data model for a list of fields.
     * @param sortOrder Sorting - sort order.
     * @return List of File formats
     */
    @GET
    @Path("/list")
    public FileFormatListResponseDto searchGet(@QueryParam("query") String query, @QueryParam("fields") String fields, @QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit,
            @DefaultValue("code") @QueryParam("sortBy") String sortBy, @DefaultValue("ASCENDING") @QueryParam("sortOrder") SortOrder sortOrder);

    /**
     * List fileFormats matching a given criteria
     *
     * @return List of fileFormats
     */
    @GET
    @Path("/listGetAll")
    FileFormatListResponseDto listGetAll();

    /**
     * Search for File formats by matching a given criteria
     * 
     * @param pagingAndFiltering Pagination and filtering criteria
     * @return List of File formats
     */
    @POST
    @Path("/list")
    public FileFormatListResponseDto searchPost(PagingAndFiltering pagingAndFiltering);

}
