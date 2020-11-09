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

import javax.validation.constraints.NotNull;
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
import org.meveo.api.dto.catalog.OfferTemplateDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.catalog.GetListOfferTemplateResponseDto;
import org.meveo.api.dto.response.catalog.GetListProductsResponseDto;
import org.meveo.api.dto.response.catalog.GetOfferTemplateResponseDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.serialize.RestDateParam;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

/**
 * Web service for managing {@link org.meveo.model.catalog.OfferTemplate}.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/catalog/offerTemplate")
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
    ActionStatus create(OfferTemplateDto postData);

    /**
     * Update offer template.
     * 
     * @param postData The offer template's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
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
    public GetListOfferTemplateResponseDto listPost(PagingAndFiltering pagingAndFiltering);
    
    
    /**
     * List offerTemplates matching a given criteria
     * 
     * @param pagingAndFiltering Pagination and filtering criteria
     * @return List of offer templates
     */
    @POST
    @Path("/cpq/list")
    @Operation(summary = "List offers matching a given billing account code and filtering Criteria",
    tags = { "OfferTemplate" },
    description ="if billingAccountCode is given, this API returns all commercial offers available for a customer taking into account the customer context (filtering rules associated to the offer tags);",
    responses = {
            @ApiResponse(responseCode="200", description = "All offers successfully retrieved",content = @Content(schema = @Schema(implementation = GetListOfferTemplateResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "billingAccountCode does not exist")
    })
    public Response listPost(@Parameter(description = "The billing account code", required = false) String billingAccountCode, 
    		@Parameter(description = "Pagination and filtering criteria", required = false) PagingAndFiltering pagingAndFiltering);

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
    ActionStatus disable(@PathParam("code") String code, @QueryParam("validFrom") @RestDateParam Date validFrom, @QueryParam("validTo") @RestDateParam Date validTo);

}