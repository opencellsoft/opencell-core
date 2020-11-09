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

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.catalog.ServiceTemplateDto;
import org.meveo.api.dto.cpq.OfferContextDTO;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.catalog.GetListServiceTemplateResponseDto;
import org.meveo.api.dto.response.catalog.GetServiceTemplateResponseDto;
import org.meveo.api.dto.response.cpq.GetListServiceResponseDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

/**
 * Web service for managing {@link org.meveo.model.catalog.ServiceTemplate}.
 * 
 * @author Edward P. Legaspi
 * @author Youssef IZEM
 * @lastModifiedVersion 5.4
 **/
@Path("/catalog/serviceTemplate")
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
    ActionStatus create(ServiceTemplateDto postData);

    /**
     * Update an existing service template.
     * 
     * @param postData The service template's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
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
    ActionStatus remove(@PathParam("serviceTemplateCode") String serviceTemplateCode);

    /**
     * Create new or update an existing service template
     * 
     * @param postData The service template's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(ServiceTemplateDto postData);

    /**
     * Enable a Service template with a given code
     * 
     * @param code Service template code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
    ActionStatus enable(@PathParam("code") String code);

    /**
     * Disable a Service template with a given code
     * 
     * @param code Service template code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
    ActionStatus disable(@PathParam("code") String code);
    
    /**
     * Gets a service template list widh .
     * 
     * @param pagingAndFiltering PagingAndFiltering config.
     * @return Return serviceTemplate list
     */
    @POST
    @Path("/list")
    GetListServiceTemplateResponseDto list(PagingAndFiltering pagingAndFiltering);
    
    @POST
    @Path("/cpq/list")
    @Operation(summary = "Lists services matching the customer, seller, and quote contexts",
    tags = { "serviceTemplates" },
    description ="if billingAccountCode/offer/product are given, this API returns all available services for an offer taking into account the customer and quote context",
    responses = {
            @ApiResponse(responseCode="200", description = "All prducts successfully retrieved",content = @Content(schema = @Schema(implementation = GetListServiceResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "billingAccountCode does not exist"),
            @ApiResponse(responseCode = "404", description = "offerCode does not exist"),
            @ApiResponse(responseCode = "404", description = "productCode does not exist"),
            @ApiResponse(responseCode = "404", description = "selected service does not exist")
    })
    public Response listPost(@Parameter(description = "The Offer context", required = false) OfferContextDTO quoteContext,
    		@Parameter(description = "Pagination and filtering criteria", required = false) PagingAndFiltering pagingAndFiltering);
}