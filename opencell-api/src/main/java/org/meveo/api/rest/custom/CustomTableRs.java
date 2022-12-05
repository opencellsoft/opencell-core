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

package org.meveo.api.rest.custom;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Hidden;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.custom.CustomTableDataDto;
import org.meveo.api.dto.custom.CustomTableDataResponseDto;
import org.meveo.api.dto.custom.CustomTableWrapperDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.rest.IBaseRs;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * Rest API for custom table data management
 * 
 * @author Andrius Karpavicius
 **/
@Path("/customTable")
@Tag(name = "CustomTable", description = "@%CustomTable")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface CustomTableRs extends IBaseRs {

    /**
     * Append data to a custom table
     *
     * @param dto Custom table data
     * @return Request processing status
     */
    @POST
    @Path("/")
	@Operation(
			summary=" Append data to a custom table ",
			description=" Append data to a custom table ",
			operationId="    POST_CustomTable_create",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus append(CustomTableDataDto dto);

    /**
     * Update existing data in a custom table
     * 
     * @param dto Custom table data. 'id' field is used to identify an existing record.
     * @return Request processing status
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Update existing data in a custom table  ",
			description=" Update existing data in a custom table  ",
			operationId="    PUT_CustomTable_update",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(CustomTableDataDto dto);

    /**
     * Remove an existing data from a custom table.
     * 
     * @param dto Custom table data. 'id' field is used to identify an existing record. If no 'id' values are passed, will delete all the records in a table.
     * @return Request processing status
     */
    @DELETE
    @Path("/")
	@Operation(
			summary=" Remove an existing data from a custom table.  ",
			description=" Remove an existing data from a custom table.  ",
			operationId="    DELETE_CustomTable_delete",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus remove(CustomTableDataDto dto);

    /**
     * Remove existing data from a custom table given search parameters
     * 
     * @param customTableCode Custom table code - can be either db table's name or a custom entity template code
     * @param pagingAndFiltering Paging and search criteria
     * @return Custom table data
     */
    @DELETE
    @Path("/{customTableCode}")
	@Operation(
			summary=" Remove existing data from a custom table given search parameters  ",
			description=" Remove existing data from a custom table given search parameters  ",
			operationId="    DELETE_CustomTable_{customTableCode}",
			responses= {
				@ApiResponse(description=" Custom table data ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus remove(@PathParam("customTableCode") String customTableCode, PagingAndFiltering pagingAndFiltering);

    /**
     * Search in custom tables
     * 
     * @param customTableCode Custom table code - can be either db table's name or a custom entity template code
     * @param pagingAndFiltering Paging and search criteria
     * @return Custom table data
     */
    @POST
    @Path("/list/{customTableCode}")
	@Operation(
			summary=" Search in custom tables  ",
			description=" Search in custom tables  ",
			operationId="    POST_CustomTable_list_{customTableCode}",
			responses= {
				@ApiResponse(description=" Custom table data ",
						content=@Content(
									schema=@Schema(
											implementation= CustomTableDataResponseDto.class
											)
								)
				)}
	)
    CustomTableDataResponseDto list(@PathParam("customTableCode") String customTableCode, PagingAndFiltering pagingAndFiltering);

    /**
     * Append or update data in a custom table
     * 
     * @param dto Custom table data. 'id' field is used to identify an existing record. Presence of 'id' field will be treated as update operation.
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Append or update data in a custom table  ",
			description=" Append or update data in a custom table  ",
			operationId="    POST_CustomTable_createOrUpdate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdate(CustomTableDataDto dto);

    /**
     * Mark records as enabled in a custom table. Applies only to those custom tables that contain a field 'disabled'
     * 
     * @param dto Custom table data. 'id' field is used to identify an existing record.
     * @return Request processing status
     */
    @POST
    @Path("/enable")
	@Operation(
			summary=" Mark records as enabled in a custom table. Applies only to those custom tables that contain a field 'disabled'  ",
			description=" Mark records as enabled in a custom table. Applies only to those custom tables that contain a field 'disabled'  ",
			operationId="    POST_CustomTable_enable",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus enable(CustomTableDataDto dto);

    /**
     * Mark records as disabled in a custom table. Applies only to those custom tables that contain a field 'disabled'
     *
     * @param dto Custom table data. 'id' field is used to identify an existing record.
     * @return Request processing status
     */
    @POST
    @Path("/disable")
	@Operation(
			summary=" Mark records as disabled in a custom table. Applies only to those custom tables that contain a field 'disabled' ",
			description=" Mark records as disabled in a custom table. Applies only to those custom tables that contain a field 'disabled' ",
			operationId="    POST_CustomTable_disable",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus disable(CustomTableDataDto dto);

    /**
     * Search in custom tables using CustomTableWrapper
     *
     * @param customTableWrapperDto Custom table wrapper dto
     * @return Custom table data
     */
    @POST
    @Path("/listFromWrapper")
	@Operation(
			summary=" Search in custom tables using CustomTableWrapper ",
			description=" Search in custom tables using CustomTableWrapper ",
			operationId="    POST_CustomTable_listFromWrapper",
			responses= {
				@ApiResponse(description=" Custom table data ",
						content=@Content(
									schema=@Schema(
											implementation= CustomTableDataResponseDto.class
											)
								)
				)}
	)
    CustomTableDataResponseDto listFromWrapper(CustomTableWrapperDto customTableWrapperDto);

}
