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

package org.meveo.api.rest;

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
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ProviderDto;
import org.meveo.api.dto.ProvidersDto;
import org.meveo.api.dto.response.GetCustomerAccountConfigurationResponseDto;
import org.meveo.api.dto.response.GetCustomerConfigurationResponseDto;
import org.meveo.api.dto.response.GetInvoicingConfigurationResponseDto;
import org.meveo.api.dto.response.GetProviderResponse;
import org.meveo.api.dto.response.GetTradingConfigurationResponseDto;

/**
 * Web service for managing Provider.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/provider")
@Tag(name = "Provider", description = "@%Provider")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface ProviderRs extends IBaseRs {

    /**
     * Create provider. Deprecated in v. 4.5. Use updateProvider() instead.
     * 
     * @param postData Provider data to be created
     * @return action status
     */
    @Deprecated
    @POST
    @Path("/")
	@Operation(
			summary=" Create provider. Deprecated in v. 4.5. Use updateProvider() instead.  ",
			description=" Create provider. Deprecated in v. 4.5. Use updateProvider() instead.  ",
			deprecated=true,
			operationId="    POST_Provider_create",
			responses= {
				@ApiResponse(description=" action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(ProviderDto postData);

    /**
     * Retrieve provider information.
     * 
     * @return Provider information
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Retrieve provider information.  ",
			description=" Retrieve provider information.  ",
			operationId="    GET_Provider_search",
			responses= {
				@ApiResponse(description=" Provider information ",
						content=@Content(
									schema=@Schema(
											implementation= GetProviderResponse.class
											)
								)
				)}
	)
    GetProviderResponse find();

    /**
     * Update provider.
     * 
     * @param postData Provider data
     * @return Request processing status
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Update provider.  ",
			description=" Update provider.  ",
			operationId="    PUT_Provider_update",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(ProviderDto postData);

    /**
     * Returns list of trading countries, currencies and languages.
     * 
     * @param providerCode An optional Provider code. If not passed, a current user's provider will be retrieved
     * @return trading configuration.
     */
    @GET
    @Path("/getTradingConfiguration")
	@Operation(
			summary=" Returns list of trading countries, currencies and languages.  ",
			description=" Returns list of trading countries, currencies and languages.  ",
			operationId="    GET_Provider_getTradingConfiguration",
			responses= {
				@ApiResponse(description=" trading configuration. ",
						content=@Content(
									schema=@Schema(
											implementation= GetTradingConfigurationResponseDto.class
											)
								)
				)}
	)
    GetTradingConfigurationResponseDto findTradingConfiguration(@QueryParam("providerCode") String providerCode);

    /**
     * Returns list of invoicing configuration (calendars, taxes, invoice categories, invoice sub categories, billing cycles and termination reasons.
     * 
     * @param providerCode An optional Provider code. If not passed, a current user's provider will be retrieved
     * @return invoicing configuration
     */
    @GET
    @Path("/getInvoicingConfiguration")
	@Operation(
			summary=" Returns list of invoicing configuration (calendars, taxes, invoice categories, invoice sub categories, billing cycles and termination reasons",
			description=" Returns list of invoicing configuration (calendars, taxes, invoice categories, invoice sub categories, billing cycles and termination reasons.  ",
			operationId="    GET_Provider_getInvoicingConfiguration",
			responses= {
				@ApiResponse(description=" invoicing configuration ",
						content=@Content(
									schema=@Schema(
											implementation= GetInvoicingConfigurationResponseDto.class
											)
								)
				)}
	)
    GetInvoicingConfigurationResponseDto findInvoicingConfiguration(@QueryParam("providerCode") String providerCode);

    /**
     * Returns list of customer brands, categories and titles.
     * 
     * @param providerCode An optional Provider code. If not passed, a current user's provider will be retrieved
     * @return customer configuration
     */
    @GET
    @Path("/getCustomerConfiguration")
	@Operation(
			summary=" Returns list of customer brands, categories and titles.  ",
			description=" Returns list of customer brands, categories and titles.  ",
			operationId="    GET_Provider_getCustomerConfiguration",
			responses= {
				@ApiResponse(description=" customer configuration ",
						content=@Content(
									schema=@Schema(
											implementation= GetCustomerConfigurationResponseDto.class
											)
								)
				)}
	)
    GetCustomerConfigurationResponseDto findCustomerConfiguration(@QueryParam("providerCode") String providerCode);

    /**
     * Returns list of payment method and credit categories.
     * 
     * @param providerCode An optional Provider code. If not passed, a current user's provider will be retrieved
     * @return customer account configuration
     */
    @GET
    @Path("/getCustomerAccountConfiguration")
	@Operation(
			summary=" Returns list of payment method and credit categories.  ",
			description=" Returns list of payment method and credit categories.  ",
			operationId="    GET_Provider_getCustomerAccountConfiguration",
			responses= {
				@ApiResponse(description=" customer account configuration ",
						content=@Content(
									schema=@Schema(
											implementation= GetCustomerAccountConfigurationResponseDto.class
											)
								)
				)}
	)
    GetCustomerAccountConfigurationResponseDto findCustomerAccountConfiguration(@QueryParam("providerCode") String providerCode);

    /**
     * Create or update a provider if it doesn't exists. Deprecated in v. 4.5. Use updateProvider() instead.
     * 
     * @param postData Provider data
     * @return action status
     */
    @Deprecated
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Create or update a provider if it doesn't exists. Deprecated in v. 4.5. Use updateProvider() instead.  ",
			description=" Create or update a provider if it doesn't exists. Deprecated in v. 4.5. Use updateProvider() instead.  ",
			deprecated=true,
			operationId="    POST_Provider_createOrUpdate",
			responses= {
				@ApiResponse(description=" action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdate(ProviderDto postData);

    /**
     * Update a provider CF.
     *
     * @param postData provider to be updated
     * @return action status
     */
    @PUT
    @Path("/updateProviderCF")
	@Operation(
			summary=" Update a provider CF. ",
			description=" Update a provider CF. ",
			operationId="    PUT_Provider_updateProviderCF",
			responses= {
				@ApiResponse(description=" action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus updateProviderCF(ProviderDto postData);

    /**
     * Find a provider Cf with a given provider code.
     *
     * @param providerCode provider's code
     * @return provider if exists
     */
    @GET
    @Path("/findProviderCF")
	@Operation(
			summary=" Find a provider Cf with a given provider code. ",
			description=" Find a provider Cf with a given provider code. ",
			operationId="    GET_Provider_findProviderCF",
			responses= {
				@ApiResponse(description=" provider if exists ",
						content=@Content(
									schema=@Schema(
											implementation= GetProviderResponse.class
											)
								)
				)}
	)
    GetProviderResponse findProviderCF(@QueryParam("providerCode") String providerCode);

    /**
     * Register a new tenant
     * 
     * @param postData Tenant/Provider data
     * @return Action status
     */
    @POST
    @Path("/createTenant")
	@Operation(
			summary=" Register a new tenant  ",
			description=" Register a new tenant  ",
			operationId="    POST_Provider_createTenant",
			responses= {
				@ApiResponse(description=" Action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    public ActionStatus createTenant(ProviderDto postData);

    /**
     * List tenants
     * 
     * @return A list of Tenant/provider data
     */
    @GET
    @Path("/listTenants")
	@Operation(
			summary=" List tenants  ",
			description=" List tenants  ",
			operationId="    GET_Provider_listTenants",
			responses= {
				@ApiResponse(description=" A list of Tenant/provider data ",
						content=@Content(
									schema=@Schema(
											implementation= ProvidersDto.class
											)
								)
				)}
	)
    public ProvidersDto listTenants();

    /**
     * Remove a tenant
     * 
     * @param providerCode Tenant/provider code
     * @return Action status
     */
    @DELETE
    @Path("/{providerCode}")
	@Operation(
			summary=" Remove a tenant  ",
			description=" Remove a tenant  ",
			operationId="    DELETE_Provider_{providerCode}",
			responses= {
				@ApiResponse(description=" Action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    public ActionStatus removeTenant(@PathParam("providerCode") String providerCode);
}
