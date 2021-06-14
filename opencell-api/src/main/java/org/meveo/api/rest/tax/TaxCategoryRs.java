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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Hidden;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.tax.TaxCategoryListResponseDto;
import org.meveo.api.dto.response.tax.TaxCategoryResponseDto;
import org.meveo.api.dto.tax.TaxCategoryDto;
import org.meveo.api.rest.IBaseRs;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * 
 * @author Mohammed
 *  
 * REST interface definition of Tax category API
 **/
@Path("/taxCategory")
@Tag(name = "TaxCategory", description = "@%TaxCategory")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface TaxCategoryRs extends IBaseRs {

    /**
     * Create a new Tax category
     * 
     * @param dto The Tax category's data
     * @return Request processing status
     */
    @POST
    @Path("/")
	@Operation(
			summary=" Create a new Tax category  ",
			description=" Create a new Tax category  ",
			operationId="    POST_TaxCategory_create",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(TaxCategoryDto dto);

    /**
     * Search for a Tax category with a given code
     * 
     * @param code The Tax category's code
     * @return A Tax category's data
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Search for a Tax category with a given code  ",
			description=" Search for a Tax category with a given code  ",
			operationId="    GET_TaxCategory_search",
			responses= {
				@ApiResponse(description=" A Tax category's data ",
						content=@Content(
									schema=@Schema(
											implementation= TaxCategoryResponseDto.class
											)
								)
				)}
	)
    TaxCategoryResponseDto find(@QueryParam("code") String code);

    /**
     * Search Tax category by matching a given criteria
     * 
     * @param query Search criteria
     * @param fields Data retrieval options/fieldnames separated by a comma
     * @param offset Pagination - from record number
     * @param limit Pagination - number of records to retrieve
     * @param sortBy Sorting - field to sort by - a field from a main entity being searched. See Data model for a list of fields.
     * @param sortOrder Sorting - sort order.
     * @return List of Tax categorys
     */
    @GET
    @Path("/list")
	@Operation(
			summary=" Search Tax category by matching a given criteria  ",
			description=" Search Tax category by matching a given criteria  ",
			operationId="    GET_TaxCategory_list",
			responses= {
				@ApiResponse(description=" List of Tax categorys ",
						content=@Content(
									schema=@Schema(
											implementation= TaxCategoryListResponseDto.class
											)
								)
				)}
	)
    public TaxCategoryListResponseDto searchGet(@QueryParam("query") String query, @QueryParam("fields") String fields, @QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit,
            @DefaultValue("code") @QueryParam("sortBy") String sortBy, @DefaultValue("ASCENDING") @QueryParam("sortOrder") SortOrder sortOrder);

    /**
     * List taxCategories matching a given criteria
     *
     * @return List of taxCategories
     */
    @GET
    @Path("/listGetAll")
	@Operation(
			summary=" List taxCategories matching a given criteria ",
			description=" List taxCategories matching a given criteria ",
			operationId="    GET_TaxCategory_listGetAll",
			responses= {
				@ApiResponse(description=" List of taxCategories ",
						content=@Content(
									schema=@Schema(
											implementation= TaxCategoryListResponseDto.class
											)
								)
				)}
	)
    TaxCategoryListResponseDto listGetAll();

    /**
     * Search for Tax category by matching a given criteria
     * 
     * @param pagingAndFiltering Pagination and filtering criteria
     * @return List of Tax categorys
     */
    @POST
    @Path("/list")
	@Operation(
			summary=" Search for Tax category by matching a given criteria  ",
			description=" Search for Tax category by matching a given criteria  ",
			operationId="    POST_TaxCategory_list",
			responses= {
				@ApiResponse(description=" List of Tax categorys ",
						content=@Content(
									schema=@Schema(
											implementation= TaxCategoryListResponseDto.class
											)
								)
				)}
	)
    public TaxCategoryListResponseDto searchPost(PagingAndFiltering pagingAndFiltering);

    /**
     * Update an existing Tax category
     * 
     * @param dto The Tax category's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Update an existing Tax category  ",
			description=" Update an existing Tax category  ",
			operationId="    PUT_TaxCategory_update",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(TaxCategoryDto dto);

    /**
     * Remove an existing Tax category with a given code
     * 
     * @param code The Tax category's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{code}")
	@Operation(
			summary=" Remove an existing Tax category with a given code  ",
			description=" Remove an existing Tax category with a given code  ",
			operationId="    DELETE_TaxCategory_{code}",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    public ActionStatus remove(@PathParam("code") String code);

    /**
     * Create new or update an existing Tax category
     * 
     * @param dto The Tax category's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Create new or update an existing Tax category  ",
			description=" Create new or update an existing Tax category  ",
			operationId="    POST_TaxCategory_createOrUpdate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdate(TaxCategoryDto dto);
}
