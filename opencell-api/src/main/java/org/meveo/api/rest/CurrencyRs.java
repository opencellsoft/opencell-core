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

import static jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA;

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

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.CurrencyDto;
import org.meveo.api.dto.billing.ExchangeRateDto;
import org.meveo.api.dto.response.GetTradingCurrencyResponse;
import org.meveo.api.dto.response.TradingCurrenciesResponseDto;
import org.meveo.api.rest.admin.impl.FileUploadForm;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.core.Response;

/**
 * Web service for managing {@link org.meveo.model.admin.Currency} and {@link org.meveo.model.billing.TradingCurrency}.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/currency")
@Tag(name = "Currency", description = "@%Currency")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.MULTIPART_FORM_DATA })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface CurrencyRs extends IBaseRs {

    /**
     * Search for list of trading currencies.
     *
     * @return list of trading currencies
     */
    @GET
    @Path("/list")
	@Operation(
			summary="Search for list of trading currencies.",
			description="Search for list of trading currencies.",
			operationId="GET_Currency_list",
			responses= {
				@ApiResponse(description="list of trading currencies",
						content=@Content(
									schema=@Schema(
											implementation= TradingCurrenciesResponseDto.class
											)
								)
				)}
	)
    TradingCurrenciesResponseDto list();

    /**
     * Creates tradingCurrency base on currency code. If the currency code does not exists, a currency record is created
     * 
     * @param postData currency to be created
     * @return action status
     */
    @POST
    @Path("/")
	@Operation(
			summary="Creates tradingCurrency base on currency code. If the currency code does not exists, a currency record is created",
			description="Creates tradingCurrency base on currency code. If the currency code does not exists, a currency record is created",
			operationId="POST_Currency_create",
			responses= {
				@ApiResponse(description="action status",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(CurrencyDto postData);

    /**
     * Search currency with a given currency code.
     * 
     * @param currencyCode currency code
     * @return currency if exists
     */
    @GET
    @Path("/")
	@Operation(
			summary="Search currency with a given currency code.",
			description="Search currency with a given currency code.",
			operationId="GET_Currency_search",
			responses= {
				@ApiResponse(description="currency if exists",
						content=@Content(
									schema=@Schema(
											implementation= GetTradingCurrencyResponse.class
											)
								)
				)}
	)
    GetTradingCurrencyResponse find(@QueryParam("currencyCode") String currencyCode);

    /**
     * Remove currency with a given currency code.
     * 
     * @param currencyCode currency code
     * @return action status
     */
    @DELETE
    @Path("/{currencyCode}")
	@Operation(
			summary="Remove currency with a given currency code.",
			description="Remove currency with a given currency code.",
			operationId="DELETE_Currency_{currencyCode}",
			responses= {
				@ApiResponse(description="action status",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus remove(@PathParam("currencyCode") String currencyCode);

    /**
     * Modify a tradingCurrency. Same input parameter as create. The currency and tradingCurrency are created if they don't exists. The operation fails if the tradingCurrency is
     * null
     * 
     * @param postData currency to be updated
     * @return action status
     */
    @PUT
    @Path("/")
	@Operation(
			summary="Modify a tradingCurrency",
			description="Modify a tradingCurrency. Same input parameter as create. The currency and tradingCurrency are created if they don't exists. The operation fails if the tradingCurrency is null",
			operationId="PUT_Currency_update",
			responses= {
				@ApiResponse(description="action status",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(CurrencyDto postData);

    /**
     * Create or Update tradingCurrency base on currency code. If the currency code does not exists, a currency record is created
     *
     * @param postData currency to be created or updated
     * @return action status
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary="Create or Update tradingCurrency base on currency code. If the currency code does not exists, a currency record is created",
			description="Create or Update tradingCurrency base on currency code. If the currency code does not exists, a currency record is created",
			operationId="POST_Currency_createOrUpdate",
			responses= {
				@ApiResponse(description="action status",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdate(CurrencyDto postData);

    /**
     * Enable a Trading currency with a given currency code
     * 
     * @param code Currency code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
	@Operation(
			summary="Enable a Trading currency with a given currency code",
			description="Enable a Trading currency with a given currency code",
			operationId="POST_Currency_{code}_enable",
			responses= {
				@ApiResponse(description="Request processing status",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus enable(@PathParam("code") String code);

    /**
     * Disable a Trading currency with a given currency code
     * 
     * @param code Currency code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
	@Operation(
			summary="Disable a Trading currency with a given currency code",
			description="Disable a Trading currency with a given currency code",
			operationId="POST_Currency_{code}_disable",
			responses= {
				@ApiResponse(description="Request processing status",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus disable(@PathParam("code") String code);

	/**
     * add functional currency to the main provider
     *
     * @param postData Currency dto
     * @return Request processing status
     */
    @POST
    @Path("/addFunctionalCurrency")
	@Operation(
			summary="API to add functional currency to the main provider",
			description="API to add functional currency to the main provider",
			operationId="ADD_FUNCTIONAL_CURRENCY",
			responses= {
				@ApiResponse(description="Request processing status",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus addFunctionalCurrency(CurrencyDto postData);

    /**
     * Add an exchange rate
     *
     * @param postData ExchangeRate dto
     * @return Request processing status
     */
    @POST
    @Path("/addExchangeRate")
    @Operation(
            summary=" API to add Exchange Rate ",
            description=" API to add Exchange Rate ",
            operationId="ADD_EXCHANGE_RATE",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Success"),
                    @ApiResponse(responseCode = "412",
                            description = "Missing parameters"),
                    @ApiResponse(responseCode = "400",
                            description = "Failed action"),
                    @ApiResponse(responseCode = "404",
                            description = "Entity does not exist")
            }
    )
    Response addExchangeRate(org.meveo.api.dto.ExchangeRateDto postData);

    @PUT
    @Path("/updateExchangeRate/{id}")
    @Operation(
            summary="API to update exchange rate",
            description="API to update exchange rate",
            operationId="UPDATE_EXCHANGE_RATE",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Success"),
                    @ApiResponse(responseCode = "404",
                            description = "Entity does not exist"),
                    @ApiResponse(responseCode = "412",
                            description = "Missing parameters"),
                    @ApiResponse(responseCode = "400",
                            description = "failed action")
            }
    )
    ActionStatus updateExchangeRate(@PathParam("id") Long id, ExchangeRateDto postData);
    
    @DELETE
    @Path("/exchangeRate/{id}")
    @Operation(summary = "API to delete an exchange Rate",
    tags = { "ExchangeRate" },
    description ="delete an existing exchange rate",
    responses = {
            @ApiResponse(responseCode="200", description = "the exchange rate successfully deleted"),
            @ApiResponse(responseCode = "400", description = "bad request when exchange rate is not found")
    })
    ActionStatus removeExchangeRateById(@Parameter(description = "id of the exchange rate", required = true) @PathParam("id") Long id);

    
	@POST
    @Path("/importExchangeRate")
    @Operation(summary = "API to import an exchange Rate from a file",
            tags = { "ImportExchangeRate" },
            description = "Import an exchange Rate from a file",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Failed action"),
                    @ApiResponse(responseCode = "404", description = "Entity does not exist."),
                    @ApiResponse(responseCode = "412", description = "Missing parameters")
            })
    @Consumes(MULTIPART_FORM_DATA)
	ActionStatus importExchangeRate(@MultipartForm FileUploadForm form);
}
