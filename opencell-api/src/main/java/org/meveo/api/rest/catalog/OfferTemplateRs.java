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

import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.catalog.OfferTemplateDto;
import org.meveo.api.dto.cpq.CustomerContextDTO;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.catalog.GetListOfferTemplateResponseDto;
import org.meveo.api.dto.response.catalog.GetOfferTemplateResponseDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidImageData;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.serialize.RestDateParam;
import org.meveo.model.catalog.LifeCycleStatusEnum;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Web service for managing {@link org.meveo.model.catalog.OfferTemplate}.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/catalog/offerTemplate")
@Tag(name = "OfferTemplate", description = "@%OfferTemplate")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface OfferTemplateRs extends IBaseRs {

    /**
     * Create offer template.
     * 
     * @param postData The offer template's data
     * @return Request processing status
     */
    @POST
    @Path("/")
	@Operation(
			summary=" Create offer template.  ",
			tags = { "OfferTemplate" },
			description=" Create offer template.  ",
			operationId="    POST_OfferTemplate_create",
			responses= {
				@ApiResponse(
						responseCode = "200",
						description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
						),
				@ApiResponse(
								responseCode = "302", 
								description = "Offer template already existe", 
								content = @Content(
											schema = @Schema(implementation = EntityAlreadyExistsException.class))),
				@ApiResponse(
								responseCode = "400", 
								description = "An offer with period date from and to  already exist ", 
								content = @Content(
											schema = @Schema(implementation = InvalidParameterException.class))),
				@ApiResponse(
								responseCode = "412", 
								description = "code of Offer template is missing / imagePath is missing", 
								content = @Content(
											schema = @Schema(
													implementation = MissingParameterException.class))),
				@ApiResponse(
								responseCode = "404", 
								description = "one of these entities doesn't exist : BusinessOfferModel, OfferTemplateCategory, "
											+ "ScriptInstance, Seller, Channel, OneShotChargeTemplate, CustomerCategory", 
								content = @Content(
											schema = @Schema(
														implementation = EntityDoesNotExistsException.class))),
				@ApiResponse(
								responseCode = "400", 
								description = "Failed creating/deleting image", 
								content = @Content(
											schema = @Schema(
														implementation = InvalidImageData.class)))
				}
	)
    ActionStatus create(OfferTemplateDto postData);

    /**
     * Update offer template.
     * 
     * @param postData The offer template's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Update offer template.  ",
			tags = { "OfferTemplate" },
			description=" Update offer template.  ",
			operationId="    PUT_OfferTemplate_update",
			responses= {
				@ApiResponse(responseCode = "200", description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				),
				@ApiResponse(
								responseCode = "302", 
								description = "Offer template already existe", 
								content = @Content(
											schema = @Schema(implementation = EntityAlreadyExistsException.class))),
				@ApiResponse(
								responseCode = "400", 
								description = "An offer with period date from and to  already exist ", 
								content = @Content(
											schema = @Schema(implementation = InvalidParameterException.class))),
				@ApiResponse(
								responseCode = "412", 
								description = "code of Offer template is missing / imagePath is missing", 
								content = @Content(
											schema = @Schema(
													implementation = MissingParameterException.class))),
				@ApiResponse(
								responseCode = "404", 
								description = "one of these entities doesn't exist : BusinessOfferModel, OfferTemplateCategory, "
											+ "ScriptInstance, Seller, Channel, OneShotChargeTemplate, CustomerCategory", 
								content = @Content(
											schema = @Schema(
														implementation = EntityDoesNotExistsException.class))),
				@ApiResponse(
								responseCode = "400", 
								description = "Failed creating/deleting image", 
								content = @Content(
											schema = @Schema(
														implementation = InvalidImageData.class)))
				}
	)
    ActionStatus update(OfferTemplateDto postData);

    /**
     * Search offer template with a given code and validity dates. If no validity dates are provided, an offer template valid on a current date will be returned.
     * 
     * @param offerTemplateCode The offer template's code
     * @param validFrom Offer template validity range - from date
     * @param validTo Offer template validity range - to date
     * @param inheritCF Should inherited custom fields be retrieved. Defaults to INHERIT_NO_MERGE.
     * @param loadOfferServiceTemplate if true loads the services
     * @param loadOfferProductTemplate if true loads the products
     * @param loadServiceChargeTemplate if true load the service charges
     * @param loadProductChargeTemplate if true load the product charges
     * @param loadAllowedDiscountPlan if true load the allowed discount plans
     * @return Return offerTemplateDto containing offerTemplate
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Search offer template with a given code and validity dates",
			tags = { "OfferTemplate" },
			description=" Search offer template with a given code and validity dates. If no validity dates are provided, an offer template valid on a current date will be returned.  ",
			operationId="    GET_OfferTemplate_search",
			responses= {
				@ApiResponse(description=" Return offerTemplateDto containing offerTemplate ",
						content=@Content(
									schema=@Schema(
											implementation= GetOfferTemplateResponseDto.class
											)
								)
				),
				@ApiResponse(
						responseCode = "412", 
						description = "offerTemplateCode paramter is missing", 
						content = @Content(
									schema = @Schema(
											implementation = MissingParameterException.class))),
				@ApiResponse(
						responseCode = "404", 
						description = "Entity OfferTemplate doesn't exist", 
						content = @Content(
									schema = @Schema(
												implementation = EntityDoesNotExistsException.class)))
				}
	)
    GetOfferTemplateResponseDto find(@QueryParam("offerTemplateCode") String offerTemplateCode, @QueryParam("validFrom") @RestDateParam Date validFrom,
            @QueryParam("validTo") @RestDateParam Date validTo, @DefaultValue("INHERIT_NO_MERGE") @QueryParam("inheritCF") CustomFieldInheritanceEnum inheritCF,
            @QueryParam("loadOfferServiceTemplate") @DefaultValue("false") boolean loadOfferServiceTemplate, @QueryParam("loadOfferProductTemplate") @DefaultValue("false") boolean loadOfferProductTemplate,
            @QueryParam("loadServiceChargeTemplate") @DefaultValue("false") boolean loadServiceChargeTemplate, @QueryParam("loadProductChargeTemplate") @DefaultValue("false") boolean loadProductChargeTemplate,
            @QueryParam("loadAllowedDiscountPlan") @DefaultValue("false") boolean loadAllowedDiscountPlan);

    /**
     * List Offer templates matching filtering and query criteria or code and validity dates.
     * 
     * If neither date is provided, validity dates will not be considered. If only validFrom is provided, a search will return offers valid on a given date. If only validTo date is
     * provided, a search will return offers valid from today to a given date.
     *
     * @param code Offer template code for optional filtering. Deprecated in v. 4.8. Use query instead.
     * @param validFrom Validity range from date. Deprecated in v. 4.8. Use query instead.
     * @param validTo Validity range to date. Deprecated in v. 4.8. Use query instead.
     * @param query Search criteria
     * @param fields Data retrieval options/fieldnames separated by a comma
     * @param offset Pagination - from record number
     * @param limit Pagination - number of records to retrieve
     * @param sortBy Sorting - field to sort by - a field from a main entity being searched. See Data model for a list of fields.
     * @param sortOrder Sorting - sort order.
     * @param inheritCF Should inherited custom fields be retrieved. Defaults to INHERIT_NO_MERGE.
     * @return A list of offer templates
     */
    @GET
    @Path("/list")
	@Operation(
			summary=" List Offer templates matching filtering and query criteria or code and validity dates",
		    tags = { "OfferTemplate" },
			description=" List Offer templates matching filtering and query criteria or code and validity dates.  If neither date is provided, validity dates will not be considered. If only validFrom is provided, a search will return offers valid on a given date. If only validTo date is provided, a search will return offers valid from today to a given date. ",
			operationId="    GET_OfferTemplate_list",
			responses= {
				@ApiResponse(description=" A list of offer templates ",
						content=@Content(
									schema=@Schema(
											implementation= GetListOfferTemplateResponseDto.class
											)
								)
				),
				@ApiResponse(
						responseCode = "400", 
						description = "some field doesn't have a valid field name", 
						content = @Content(
									schema = @Schema(implementation = InvalidParameterException.class)))	
			}
	)
    public GetListOfferTemplateResponseDto listGet(@Deprecated @QueryParam("offerTemplateCode") String code, @Deprecated @QueryParam("validFrom") @RestDateParam Date validFrom,
            @Deprecated @QueryParam("validTo") @RestDateParam Date validTo, @QueryParam("query") String query, @QueryParam("fields") String fields,
            @QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit, @DefaultValue("code") @QueryParam("sortBy") String sortBy,
            @DefaultValue("ASCENDING") @QueryParam("sortOrder") SortOrder sortOrder,
            @DefaultValue("INHERIT_NO_MERGE") @QueryParam("inheritCF") CustomFieldInheritanceEnum inheritCF);

    /**
     * List offerTemplates matching a given criteria
     * 
     * @param pagingAndFiltering Pagination and filtering criteria
     * @return List of offer templates
     */
    @POST
    @Path("/list")
	@Operation(
			summary=" List offerTemplates matching a given criteria  ",
			tags = { "OfferTemplate" },
			description=" List offerTemplates matching a given criteria  ",
			operationId="    POST_OfferTemplate_list",
			responses= {
				@ApiResponse(description=" List of offer templates ",
						content=@Content(
									schema=@Schema(
											implementation= GetListOfferTemplateResponseDto.class
											)
								)
				),
				@ApiResponse(
						responseCode = "400", 
						description = "some field doesn't have a valid field name", 
						content = @Content(
									schema = @Schema(implementation = InvalidParameterException.class)))	
			}
	)
    public GetListOfferTemplateResponseDto listPost(PagingAndFiltering pagingAndFiltering);
    
    
    /**
     * List offerTemplates matching a given criteria
     * 
     * @param customerContextDTO
     * @return List of offer templates
     */
    @POST
    @Path("/cpq/list")
    @Operation(summary = "List offers matching the customer and seller contexts",
    tags = { "Catalog browsing" },
    description ="Get offers matching the customer and seller contexts, it returns offers and their products",
    responses = {
            @ApiResponse(responseCode="200", description = "All offers successfully retrieved",content = @Content(schema = @Schema(implementation = GetListOfferTemplateResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "billingAccountCode does not exist"),
			@ApiResponse(
					responseCode = "400", 
					description = "some field doesn't have a valid field name", 
					content = @Content(
								schema = @Schema(implementation = InvalidParameterException.class)))	
    })
    public Response listPost(@Parameter(description = "The customer context information", required = false) CustomerContextDTO customerContextDTO);

    /**
     * Remove offer template with a given code and validity dates. If no validity dates are provided, an offer template valid on a current date will be deleted.
     * 
     * @param offerTemplateCode The offer template's code
     * @param validFrom Offer template validity range - from date
     * @param validTo Offer template validity range - to date
     * @return Request processing status
     */
    @DELETE
    @Path("/{offerTemplateCode}")
	@Operation(
			summary=" Remove offer template with a given code and validity dates",
		    tags = { "OfferTemplate" },
			description=" Remove offer template with a given code and validity dates. If no validity dates are provided, an offer template valid on a current date will be deleted.  ",
			operationId="    DELETE_OfferTemplate_{offerTemplateCode}",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				),
				@ApiResponse(
						responseCode = "412", 
						description = "offerTemplateCode paramter is missing", 
						content = @Content(
									schema = @Schema(
											implementation = MissingParameterException.class))),
				@ApiResponse(
						responseCode = "404", 
						description = "OfferTemplate doesn't exist", 
						content = @Content(
									schema = @Schema(
												implementation = EntityDoesNotExistsException.class)))
				
			}
	)
    ActionStatus remove(@PathParam("offerTemplateCode") String offerTemplateCode, @QueryParam("validFrom") @RestDateParam Date validFrom,
            @QueryParam("validTo") @RestDateParam Date validTo);

    /**
     * Create or update offer template based on a given code.
     * 
     * @param postData The offer template's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Create or update offer template based on a given code.  ",
	        tags = { "OfferTemplate" },
			description=" Create or update offer template based on a given code.  ",
			operationId="    POST_OfferTemplate_createOrUpdate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdate(OfferTemplateDto postData);

    /**
     * Enable a Offer template with a given code
     * 
     * @param code Offer template code
     * @param validFrom Offer template validity range - from date
     * @param validTo Offer template validity range - to date
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
	@Operation(
			summary=" Enable a Offer template with a given code  ",
			tags = { "OfferTemplate" },
			description=" Enable a Offer template with a given code  ",
			operationId="    POST_OfferTemplate_{code}_enable",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				),
				@ApiResponse(
						responseCode = "412", 
						description = "code paramter is missing", 
						content = @Content(
									schema = @Schema(
											implementation = MissingParameterException.class))),
				@ApiResponse(
						responseCode = "404", 
						description = "OfferTemplate doesn't exist", 
						content = @Content(
									schema = @Schema(
												implementation = EntityDoesNotExistsException.class))),
				@ApiResponse(
						responseCode = "400", 
						description = "Internat error while enabling offer template ", 
						content = @Content(
									schema = @Schema(
												implementation = BusinessException.class)))
			}
	)
    ActionStatus enable(@PathParam("code") String code, @QueryParam("validFrom") @RestDateParam Date validFrom, @QueryParam("validTo") @RestDateParam Date validTo);

    /**
     * Disable a Offer template with a given code
     * 
     * @param code Offer template code
     * @param validFrom Offer template validity range - from date
     * @param validTo Offer template validity range - to date
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
	@Operation(
			summary=" Disable a Offer template with a given code  ",
			tags = { "OfferTemplate" },
			description=" Disable a Offer template with a given code  ",
			operationId="    POST_OfferTemplate_{code}_disable",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				),
				@ApiResponse(
						responseCode = "412", 
						description = "code paramter is missing", 
						content = @Content(
									schema = @Schema(
											implementation = MissingParameterException.class))),
				@ApiResponse(
						responseCode = "404", 
						description = "OfferTemplate doesn't exist", 
						content = @Content(
									schema = @Schema(
												implementation = EntityDoesNotExistsException.class))),
				@ApiResponse(
						responseCode = "400", 
						description = "Internat error while enabling offer template ", 
						content = @Content(
									schema = @Schema(
												implementation = BusinessException.class)))	
			}
	)
    ActionStatus disable(@PathParam("code") String code, @QueryParam("validFrom") @RestDateParam Date validFrom, @QueryParam("validTo") @RestDateParam Date validTo);
    
    @POST
    @Path("/duplicate/{offerTemplateCode}")
    Response duplicateOffer(@Parameter(description = "code of the offer that will be duplicate", required = true) @PathParam("offerTemplateCode") String offerTemplateCode,
            @Parameter(description = "copy the hierarchy of the offer") @QueryParam("duplicateHierarchy") boolean duplicateHierarchy,
            @Parameter(description = "preserve code of offer") @QueryParam("preserveCode") boolean preserveCode, 
			@Parameter(name = "date valid from") @QueryParam("validFrom") @RestDateParam Date validFrom, 
			@Parameter(name = "date valid to") @QueryParam("validTo") @RestDateParam Date validTo);
    
    @POST
    @Path("/{offerTemplateCode}/update/status")
    Response updateStatus(@Parameter(name = "offer template for updating status", required = true) @PathParam("offerTemplateCode") String offerTemplateCode,
    					  @Parameter(name = "new status", required = true) @QueryParam("status") LifeCycleStatusEnum status, 
    					  @Parameter(name = "date valid from") @QueryParam("validFrom") @RestDateParam Date validFrom, 
    					  @Parameter(name = "date valid to") @QueryParam("validTo") @RestDateParam Date validTo);

}
