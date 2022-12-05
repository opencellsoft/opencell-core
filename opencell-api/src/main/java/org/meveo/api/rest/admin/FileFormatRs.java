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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Hidden;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.admin.FileFormatDto;
import org.meveo.api.dto.admin.FileFormatListResponseDto;
import org.meveo.api.dto.admin.FileFormatResponseDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.rest.IBaseRs;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

/**
 * File format resource
 *
 * @author Abdellatif BARI
 * @since 8.0.0
 */
@Path("/admin/fileFormat")
@Tag(name = "FileFormat", description = "@%FileFormat")
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
	@Operation(
			summary=" Create a new File format ",
			description=" Create a new File format ",
			operationId="    POST_FileFormat_create",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(FileFormatDto postData);

    /**
     * Update an existing File format
     * 
     * @param dto The File format's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Update an existing File format  ",
			description=" Update an existing File format  ",
			operationId="    PUT_FileFormat_update",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(FileFormatDto dto);

    /**
     * Remove an existing File format with a given code
     *
     * @param code File format's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{code}")
	@Operation(
			summary=" Remove an existing File format with a given code ",
			description=" Remove an existing File format with a given code ",
			operationId="    DELETE_FileFormat_{code}",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus remove(@PathParam("code") String code);

    /**
     * Create new or update an existing File formats
     * 
     * @param dto The File format data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Create new or update an existing File formats  ",
			description=" Create new or update an existing File formats  ",
			operationId="    POST_FileFormat_createOrUpdate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdate(FileFormatDto dto);

    /**
     * Search for a File format with a given code
     * 
     * @param code The File format's code
     * @return A File format's data
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Search for a File format with a given code  ",
			description=" Search for a File format with a given code  ",
			operationId="    GET_FileFormat_search",
			responses= {
				@ApiResponse(description=" A File format's data ",
						content=@Content(
									schema=@Schema(
											implementation= FileFormatResponseDto.class
											)
								)
				)}
	)
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
	@Operation(
			summary=" Search File formats by matching a given criteria  ",
			description=" Search File formats by matching a given criteria  ",
			operationId="    GET_FileFormat_list",
			responses= {
				@ApiResponse(description=" List of File formats ",
						content=@Content(
									schema=@Schema(
											implementation= FileFormatListResponseDto.class
											)
								)
				)}
	)
    public FileFormatListResponseDto searchGet(@QueryParam("query") String query, @QueryParam("fields") String fields, @QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit,
            @DefaultValue("code") @QueryParam("sortBy") String sortBy, @DefaultValue("ASCENDING") @QueryParam("sortOrder") SortOrder sortOrder);

    /**
     * List fileFormats matching a given criteria
     *
     * @return List of fileFormats
     */
    @GET
    @Path("/listGetAll")
	@Operation(
			summary=" List fileFormats matching a given criteria ",
			description=" List fileFormats matching a given criteria ",
			operationId="    GET_FileFormat_listGetAll",
			responses= {
				@ApiResponse(description=" List of fileFormats ",
						content=@Content(
									schema=@Schema(
											implementation= FileFormatListResponseDto.class
											)
								)
				)}
	)
    FileFormatListResponseDto listGetAll();

    /**
     * Search for File formats by matching a given criteria
     * 
     * @param pagingAndFiltering Pagination and filtering criteria
     * @return List of File formats
     */
    @POST
    @Path("/list")
	@Operation(
			summary=" Search for File formats by matching a given criteria  ",
			description=" Search for File formats by matching a given criteria  ",
			operationId="    POST_FileFormat_list",
			responses= {
				@ApiResponse(description=" List of File formats ",
						content=@Content(
									schema=@Schema(
											implementation= FileFormatListResponseDto.class
											)
								)
				)}
	)
    public FileFormatListResponseDto searchPost(PagingAndFiltering pagingAndFiltering);

}
