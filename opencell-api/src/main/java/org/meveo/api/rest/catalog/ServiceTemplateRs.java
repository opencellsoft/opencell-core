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
import org.meveo.api.dto.catalog.ServiceTemplateDto;
import org.meveo.api.dto.catalog.ServiceTypeDto;
import org.meveo.api.dto.cpq.OfferContextDTO;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.catalog.GetListServiceTemplateResponseDto;
import org.meveo.api.dto.response.catalog.GetServiceTemplateResponseDto;
import org.meveo.api.dto.response.catalog.GetServiceTypeResponseDto;
import org.meveo.api.dto.response.cpq.GetListServiceResponseDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidImageData;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.rest.IBaseRs;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Web service for managing {@link org.meveo.model.catalog.ServiceTemplate}.
 * 
 * @author Edward P. Legaspi
 * @author Youssef IZEM
 * @lastModifiedVersion 5.4
 **/
@Path("/catalog/serviceTemplate")
@Tag(name = "ServiceTemplate", description = "@%ServiceTemplate")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface ServiceTemplateRs extends IBaseRs {

    /**
     * Create a new service template.
     * 
     * @param postData The service template's data
     * @return Request processing status
     */
    @POST
    @Path("/")
	@Operation(
			summary=" Create a new service template.  ",
			tags = { "ServiceTemplate" },
			description=" Create a new service template.  ",
			operationId="    POST_ServiceTemplate_create",
			responses= {
				@ApiResponse(responseCode = "200", description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				),
				@ApiResponse(
						responseCode = "412", 
						description = "one of these fields is missing : code, renewalInfo/initillyActiveForUnit, renewalInfo/endOfTermAction, "
									+ "renewalInfo/renewFor, renewalInfo/renewForUnit, renewalInfo/terminationReason", 
						content = @Content(
									schema = @Schema(
											implementation = MissingParameterException.class))),
				@ApiResponse(
						responseCode = "302", 
						description = "ServiceTemplateService already existe", 
						content = @Content(
									schema = @Schema(implementation = EntityAlreadyExistsException.class))),
				@ApiResponse(
						responseCode = "404", 
						description = "one of these entities doesn't exist : Calendar, BusinessServiceModel, OneShotChargeTemplate", 
						content = @Content(
									schema = @Schema(
												implementation = EntityDoesNotExistsException.class))),
				@ApiResponse(
						responseCode = "400", 
						description = "renewalInfo/terminationReason", 
						content = @Content(
									schema = @Schema(
												implementation = InvalidParameterException.class))),
				@ApiResponse(
						responseCode = "400", 
						description = "Failed creating/deleting image", 
						content = @Content(
									schema = @Schema(
												implementation = InvalidImageData.class)))
				}
	)
    ActionStatus create(ServiceTemplateDto postData);

    /**
     * Update an existing service template.
     * 
     * @param postData The service template's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Update an existing service template.  ",
		    tags = { "ServiceTemplate" },
			description=" Update an existing service template.  ",
			operationId="    PUT_ServiceTemplate_update",
			responses= {
				@ApiResponse(responseCode = "200", description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				),
				@ApiResponse(
						responseCode = "412", 
						description = "one of these fields is missing : code, renewalInfo/initillyActiveForUnit, renewalInfo/endOfTermAction, "
									+ "renewalInfo/renewFor, renewalInfo/renewForUnit, renewalInfo/terminationReason", 
						content = @Content(
									schema = @Schema(
											implementation = MissingParameterException.class))),
				@ApiResponse(
						responseCode = "302", 
						description = "ServiceTemplateService already existe", 
						content = @Content(
									schema = @Schema(implementation = EntityAlreadyExistsException.class))),
				@ApiResponse(
						responseCode = "404", 
						description = "one of these entities doesn't exist : Calendar, BusinessServiceModel, OneShotChargeTemplate", 
						content = @Content(
									schema = @Schema(
												implementation = EntityDoesNotExistsException.class))),
				@ApiResponse(
						responseCode = "400", 
						description = "renewalInfo/terminationReason", 
						content = @Content(
									schema = @Schema(
												implementation = InvalidParameterException.class))),
				@ApiResponse(
						responseCode = "400", 
						description = "Failed creating/deleting image", 
						content = @Content(
									schema = @Schema(
												implementation = InvalidImageData.class)))}
	)
    ActionStatus update(ServiceTemplateDto postData);

    /**
     * Find a service template with a given code.
     * 
     * @param serviceTemplateCode The service template's code
     * @param inheritCF Should inherited custom fields be retrieved. Defaults to INHERIT_NO_MERGE.
     * @return Return serviceTemplate
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Find a service template with a given code.  ",
	        tags = { "ServiceTemplate" },	
			description=" Find a service template with a given code.  ",
			operationId="    GET_ServiceTemplate_search",
			responses= {
				@ApiResponse(description=" Return serviceTemplate ",
						content=@Content(
									schema=@Schema(
											implementation= GetServiceTemplateResponseDto.class
											)
								)
				),
				@ApiResponse(
						responseCode = "412", 
						description = "serviceTemplateCode is missing", 
						content = @Content(
									schema = @Schema(
											implementation = MissingParameterException.class))),
				
				@ApiResponse(
						responseCode = "404", 
						description = "ServiceTemplate doesn't exist", 
						content = @Content(
									schema = @Schema(
												implementation = EntityDoesNotExistsException.class)))
			}
	)
    GetServiceTemplateResponseDto find(@QueryParam("serviceTemplateCode") String serviceTemplateCode,
            @DefaultValue("INHERIT_NO_MERGE") @QueryParam("inheritCF") CustomFieldInheritanceEnum inheritCF);

    /**
     * Remove service template with a given code.
     * 
     * @param serviceTemplateCode The service template's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{serviceTemplateCode}")
	@Operation(
			summary=" Remove service template with a given code.  ",
			tags = { "ServiceTemplate" },	
			description=" Remove service template with a given code.  ",
			operationId="    DELETE_ServiceTemplate_{serviceTemplateCode}",
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
						description = "serviceTemplateCode is missing", 
						content = @Content(
									schema = @Schema(
											implementation = MissingParameterException.class))),
				@ApiResponse(
						responseCode = "404", 
						description = "ServiceTemplate doesn't exist", 
						content = @Content(
									schema = @Schema(
												implementation = EntityDoesNotExistsException.class)))
			}
	)
    ActionStatus remove(@PathParam("serviceTemplateCode") String serviceTemplateCode);

    /**
     * Create new or update an existing service template
     * 
     * @param postData The service template's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Create new or update an existing service template  ",
			tags = { "ServiceTemplate" },	
			description=" Create new or update an existing service template  ",
			operationId="    POST_ServiceTemplate_createOrUpdate",
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
						description = "one of these fields is missing : code, renewalInfo/initillyActiveForUnit, renewalInfo/endOfTermAction, "
									+ "renewalInfo/renewFor, renewalInfo/renewForUnit, renewalInfo/terminationReason", 
						content = @Content(
									schema = @Schema(
											implementation = MissingParameterException.class))),
				@ApiResponse(
						responseCode = "302", 
						description = "ServiceTemplateService already existe", 
						content = @Content(
									schema = @Schema(implementation = EntityAlreadyExistsException.class))),
				@ApiResponse(
						responseCode = "404", 
						description = "one of these entities doesn't exist : Calendar, BusinessServiceModel, OneShotChargeTemplate", 
						content = @Content(
									schema = @Schema(
												implementation = EntityDoesNotExistsException.class))),
				@ApiResponse(
						responseCode = "400", 
						description = "renewalInfo/terminationReason", 
						content = @Content(
									schema = @Schema(
												implementation = InvalidParameterException.class))),
				@ApiResponse(
						responseCode = "400", 
						description = "Failed creating/deleting image", 
						content = @Content(
									schema = @Schema(
												implementation = InvalidImageData.class)))}
	)
    ActionStatus createOrUpdate(ServiceTemplateDto postData);

    /**
     * Enable a Service template with a given code
     * 
     * @param code Service template code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
	@Operation(
			summary=" Enable a Service template with a given code  ",
		    tags = { "ServiceTemplate" },
			description=" Enable a Service template with a given code  ",
			operationId="    POST_ServiceTemplate_{code}_enable",
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
    ActionStatus enable(@PathParam("code") String code);

    /**
     * Disable a Service template with a given code
     * 
     * @param code Service template code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
	@Operation(
			summary=" Disable a Service template with a given code  ",
			tags = { "ServiceTemplate" },
			description=" Disable a Service template with a given code  ",
			operationId="    POST_ServiceTemplate_{code}_disable",
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
    ActionStatus disable(@PathParam("code") String code);
    
    /**
     * Gets a service template list matching given criteria .
     * 
     * @param pagingAndFiltering PagingAndFiltering config.
     * @return Return serviceTemplate list
     */
    @POST
    @Path("/list")
	@Operation(
			summary=" Gets a service template list matching given criteria",
			description=" Gets a service template list matching given criteria",
			operationId="    POST_ServiceTemplate_list",
			responses= {
				@ApiResponse(description=" Return serviceTemplate list ",
						content=@Content(
									schema=@Schema(
											implementation= GetListServiceTemplateResponseDto.class
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
    GetListServiceTemplateResponseDto list(PagingAndFiltering pagingAndFiltering);
    
    
    
    @POST
    @Path("/cpq/list")
    @Operation(summary = "Lists services matching the customer, seller, and quote contexts",
    tags = { "Catalog browsing" },
    description ="if billingAccountCode/offer/product are given, this API returns all available services for an offer taking into account the customer and quote context",
    responses = {
            @ApiResponse(responseCode="200", description = "All prducts successfully retrieved",content = @Content(schema = @Schema(implementation = GetListServiceResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "billingAccountCode does not exist"),
            @ApiResponse(responseCode = "404", description = "offerCode does not exist"),
            @ApiResponse(responseCode = "404", description = "productCode does not exist"),
            @ApiResponse(responseCode = "404", description = "selected service does not exist")
    })
    public Response listPost(@Parameter(description = "The Offer context", required = false) OfferContextDTO quoteContext);
    
    
    
    /**
     * Create a new serviceType
     * 
     * @param postData The serviceType's data
     * @return Request processing status
     */
    @POST
    @Path("/cpq/serviceType/create")
    ActionStatus createServiceType(ServiceTypeDto postData);

    /**
     * Search for a serviceType with a given code 
     * 
     * @param serviceTypeCode The serviceType's code
     * @return A serviceType's data
     */
    @GET
    @Path("/cpq/serviceType/find")
    GetServiceTypeResponseDto findServiceType(@QueryParam("serviceTypeCode") String serviceTypeCode);
 

    /**
     * Update an existing serviceType
     * 
     * @param postData The serviceType's data
     * @return Request processing status
     */
    @PUT
    @Path("/cpq/serviceType/update")
    ActionStatus updateServiceType(ServiceTypeDto postData);

    /**
     * Remove an existing ServiceType with a given code 
     * 
     * @param serviceTypeCode The serviceType code
     * @return Request processing status
     */
    @DELETE
    @Path("/{serviceTypeCode}")
    public ActionStatus removeServiceType(@PathParam("serviceTypeCode") String serviceTypeCode);

    /**
     * Create new or update an existing ServiceType
     * 
     * @param postData The ServiceType data
     * @return Request processing status
     */
    @POST
    @Path("cpq/serviceType/createOrUpdate")
    ActionStatus createOrUpdateServiceType(ServiceTypeDto postData);
    
    
   
    
    
}
