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

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.catalog.OfferTemplateCategoryDto;
import org.meveo.api.dto.response.OfferTemplateCategoriesResponseDto;
import org.meveo.api.dto.response.catalog.GetOfferTemplateCategoryResponseDto;
import org.meveo.api.rest.IBaseRs;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/catalog/offerTemplateCategory")
@Tag(name = "OfferTemplateCategory", description = "@%OfferTemplateCategory")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface OfferTemplateCategoryRs extends IBaseRs {

    /**
     * Create a new offer template category
     * 
     * @param postData The offer template category's data
     * @return Request processing status
     */
    @POST
    @Path("/")
	@Operation(
			summary=" Create a new offer template category  ",
			description=" Create a new offer template category  ",
			operationId="    POST_OfferTemplateCategory_create",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(OfferTemplateCategoryDto postData);

    /**
     * Update an existing offer template category
     * 
     * @param postData The offer template category's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Update an existing offer template category  ",
			description=" Update an existing offer template category  ",
			operationId="    PUT_OfferTemplateCategory_update",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(OfferTemplateCategoryDto postData);

    /**
     * Find a offer template category with a given code
     * 
     * @param offerTemplateCategoryCode The offer template category's code
     * @return Return offerTemplateCategoryCodeDto containing offerTemplateCategoryCode
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Find a offer template category with a given code  ",
			description=" Find a offer template category with a given code  ",
			operationId="    GET_OfferTemplateCategory_search",
			responses= {
				@ApiResponse(description=" Return offerTemplateCategoryCodeDto containing offerTemplateCategoryCode ",
						content=@Content(
									schema=@Schema(
											implementation= GetOfferTemplateCategoryResponseDto.class
											)
								)
				)}
	)
    GetOfferTemplateCategoryResponseDto find(@QueryParam("offerTemplateCategoryCode") String offerTemplateCategoryCode);

    /**
     * Remove an existing offer template category with a given code
     * 
     * @param offerTemplateCategoryCode The offer template category's code
     * @return Request processing status
     */
    @DELETE
    @Path("/")
	@Operation(
			summary=" Remove an existing offer template category with a given code  ",
			description=" Remove an existing offer template category with a given code  ",
			operationId="    DELETE_OfferTemplateCategory_delete",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus delete(@QueryParam("offerTemplateCategoryCode") String offerTemplateCategoryCode);

    /**
     * Create new or update an existing offer template category
     * 
     * @param postData The offer template category's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Create new or update an existing offer template category  ",
			description=" Create new or update an existing offer template category  ",
			operationId="    POST_OfferTemplateCategory_createOrUpdate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdate(OfferTemplateCategoryDto postData);

    /**
     * Enable a Offer template category with a given code
     * 
     * @param code Offer template category code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
	@Operation(
			summary=" Enable a Offer template category with a given code  ",
			description=" Enable a Offer template category with a given code  ",
			operationId="    POST_OfferTemplateCategory_{code}_enable",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus enable(@PathParam("code") String code);

    /**
     * Disable a Offer template category with a given code
     * 
     * @param code Offer template category code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
	@Operation(
			summary=" Disable a Offer template category with a given code  ",
			description=" Disable a Offer template category with a given code  ",
			operationId="    POST_OfferTemplateCategory_{code}_disable",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus disable(@PathParam("code") String code);

    /**
     * List DiscountPlanItems matching a given criteria
     *
     * @return List of DiscountPlanItems
     */
    @GET
    @Path("/listGetAll")
	@Operation(
			summary=" List DiscountPlanItems matching a given criteria ",
			description=" List DiscountPlanItems matching a given criteria ",
			operationId="    GET_OfferTemplateCategory_listGetAll",
			responses= {
				@ApiResponse(description=" List of DiscountPlanItems ",
						content=@Content(
									schema=@Schema(
											implementation= OfferTemplateCategoriesResponseDto.class
											)
								)
				)}
	)
    OfferTemplateCategoriesResponseDto listGetAll();

}
