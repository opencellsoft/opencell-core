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

package org.meveo.api.rest.catalog;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Hidden;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.catalog.BusinessOfferModelDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.catalog.GetBusinessOfferModelResponseDto;
import org.meveo.api.dto.response.module.MeveoModuleDtosResponse;
import org.meveo.api.rest.IBaseRs;

/**
 * @author Edward P. Legaspi
 **/
@Path("/catalog/businessOfferModel")
@Tag(name = "BusinessOfferModel", description = "@%BusinessOfferModel")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface BusinessOfferModelRs extends IBaseRs {

    /**
     * Create a new business offer model.
     * 
     * @param postData The business offer model's data
     * @return Request processing status
     */
    @POST
    @Path("/")
	@Operation(
			summary=" Create a new business offer model.  ",
			description=" Create a new business offer model.  ",
			operationId="    POST_BusinessOfferModel_create",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(BusinessOfferModelDto postData);

    /**
     * Update an existing business offer model.
     * 
     * @param postData The business offer model's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Update an existing business offer model.  ",
			description=" Update an existing business offer model.  ",
			operationId="    PUT_BusinessOfferModel_update",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(BusinessOfferModelDto postData);

    /**
     * Remove an existing business offer model with a given code.
     * 
     * @param businessOfferModelCode The business offer model's code
     * @param loadOfferServiceTemplate if true loads the services
     * @param loadOfferProductTemplate if true loads the products
     * @param loadServiceChargeTemplate if true load the service charges
     * @param loadProductChargeTemplate if true load the product charges
     * @return A business offer model
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Remove an existing business offer model with a given code.  ",
			description=" Remove an existing business offer model with a given code.  ",
			operationId="    GET_BusinessOfferModel_search",
			responses= {
				@ApiResponse(description=" A business offer model ",
						content=@Content(
									schema=@Schema(
											implementation= GetBusinessOfferModelResponseDto.class
											)
								)
				)}
	)
    GetBusinessOfferModelResponseDto find(@QueryParam("businessOfferModelCode") String businessOfferModelCode,
            @QueryParam("loadOfferServiceTemplate") @DefaultValue("false") boolean loadOfferServiceTemplate, @QueryParam("loadOfferProductTemplate") @DefaultValue("false") boolean loadOfferProductTemplate,
            @QueryParam("loadServiceChargeTemplate") @DefaultValue("false") boolean loadServiceChargeTemplate, @QueryParam("loadProductChargeTemplate") @DefaultValue("false") boolean loadProductChargeTemplate);


    /**
     * Remove an existing business offer model with a given code.
     * 
     * @param businessOfferModelCode The business offer model's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{businessOfferModelCode}")
	@Operation(
			summary=" Remove an existing business offer model with a given code.  ",
			description=" Remove an existing business offer model with a given code.  ",
			operationId="    DELETE_BusinessOfferModel_{businessOfferModelCode}",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus remove(@PathParam("businessOfferModelCode") String businessOfferModelCode);

    /**
     * Create new or update an existing business offer model.
     * 
     * @param postData The business offer model's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Create new or update an existing business offer model.  ",
			description=" Create new or update an existing business offer model.  ",
			operationId="    POST_BusinessOfferModel_createOrUpdate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdate(BusinessOfferModelDto postData);

    
    
    /**
     * List business offer models.
     *
     * @param query Search criteria
     * @param fields Data retrieval options/fieldnames separated by a comma
     * @param offset Pagination - from record number
     * @param limit Pagination - number of records to retrieve
     * @param sortBy Sorting - field to sort by - a field from a main entity being searched. See Data model for a list of fields.
     * @param sortOrder Sorting - sort order.
     * @return A list of business offer models
     */
    @GET
    @Path("/list")
	@Operation(
			summary=" List business offer models. ",
			description=" List business offer models. ",
			operationId="    GET_BusinessOfferModel_list",
			responses= {
				@ApiResponse(description=" A list of business offer models ",
						content=@Content(
									schema=@Schema(
											implementation= MeveoModuleDtosResponse.class
											)
								)
				)}
	)
    MeveoModuleDtosResponse listGet(@QueryParam("query") String query, @QueryParam("fields") String fields, @QueryParam("offset") Integer offset,
            @QueryParam("limit") Integer limit, @DefaultValue("code") @QueryParam("sortBy") String sortBy, @DefaultValue("ASCENDING") @QueryParam("sortOrder") SortOrder sortOrder);
    
    /**
     * List business offer models.
     * 
     * @param pagingAndFiltering Pagination and filtering criteria
     * @return A list of business offer models
     */
    @POST
    @Path("/list")
	@Operation(
			summary=" List business offer models.  ",
			description=" List business offer models.  ",
			operationId="    POST_BusinessOfferModel_list",
			responses= {
				@ApiResponse(description=" A list of business offer models ",
						content=@Content(
									schema=@Schema(
											implementation= MeveoModuleDtosResponse.class
											)
								)
				)}
	)
    MeveoModuleDtosResponse listPost(PagingAndFiltering pagingAndFiltering);

    /**
     * Install business offer model module.
     * @param moduleDto business offer model
     * @return Request processing status
     */
    @PUT
    @Path("/install")
	@Operation(
			summary=" Install business offer model module. ",
			description=" Install business offer model module. ",
			operationId="    PUT_BusinessOfferModel_install",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus install(BusinessOfferModelDto moduleDto);
}
