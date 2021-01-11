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
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.catalog.LoadPricesRequest;
import org.meveo.api.dto.catalog.PricePlanMatrixColumnDto;
import org.meveo.api.dto.catalog.PricePlanMatrixDto;
import org.meveo.api.dto.catalog.PricePlanMatrixLineDto;
import org.meveo.api.dto.catalog.PricePlanMatrixVersionDto;
import org.meveo.api.dto.response.catalog.GetPricePlanMatrixColumnResponseDto;
import org.meveo.api.dto.response.catalog.GetPricePlanResponseDto;
import org.meveo.api.dto.response.catalog.GetPricePlanVersionResponseDto;
import org.meveo.api.dto.response.catalog.PricePlanMatrixesResponseDto;
import org.meveo.api.dto.response.cpq.GetProductVersionResponse;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.PATCH;
import org.meveo.model.catalog.PricePlanMatrixColumn;
import org.meveo.model.cpq.enums.VersionStatusEnum;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Web service for managing {@link org.meveo.model.catalog.PricePlanMatrix}.
 *
 * @author Edward P. Legaspi
 **/
@Path("/catalog/pricePlan")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface PricePlanRs extends IBaseRs {

    /**
     * Create a new price plan matrix
     *
     * @param postData The price plan matrix's data
     * @return Request processing status
     */
    @POST
    @Path("/")
    @Operation(summary = "This endpoint allows to delete a price plan matrix",
            tags = { "PricePlanMatrix" },
            description ="create a price plan matrix",
            responses = {
                    @ApiResponse(responseCode="200", description = "the price plan column successfully created"),
                    @ApiResponse(responseCode = "400", description = "Internat error")
            })
    ActionStatus create(PricePlanMatrixDto postData);

    /**
     * Update an existing price plan matrix
     *
     * @param postData The price plan matrix's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
    ActionStatus update(PricePlanMatrixDto postData);

    /**
     * Find a price plan matrix with a given code
     *
     * @param pricePlanCode The price plan's code
     * @return pricePlanMatrixDto Returns pricePlanMatrixDto containing pricePlan
     */
    @GET
    @Path("/")
    GetPricePlanResponseDto find(@QueryParam("pricePlanCode") String pricePlanCode);

    /**
     * Remove an existing price plan matrix with a given code
     *
     * @param pricePlanCode The price plan's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{pricePlanCode}")
    ActionStatus remove(@PathParam("pricePlanCode") String pricePlanCode);

    /**
     * List price plan matrix.
     *
     * @param eventCode The charge's code linked to price plan.
     * @return Return pricePlanMatrixes
     */
    @GET
    @Path("/list")
    PricePlanMatrixesResponseDto listPricePlanByEventCode(@QueryParam("eventCode") String eventCode);

    /**
     * Create new or update an existing price plan matrix
     *
     * @param postData The price plan matrix's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(PricePlanMatrixDto postData);

    /**
     * Enable a Price plan with a given code
     *
     * @param code Price plan code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
    ActionStatus enable(@PathParam("code") String code);

    /**
     * Disable a Price plan with a given code
     *
     * @param code Price plan code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
    ActionStatus disable(@PathParam("code") String code);


    @POST
    @Path("/pricePlanMatrixVersion")
    @Operation(summary = "This endpoint allows to create or update a price plan version",
            tags = { "PricePlanMatrixVersion" },
            description ="create a price plan version if it doesn't exist or update an existing price plan version",
            responses = {
                    @ApiResponse(responseCode="200", description = "the price plan version successfully created or updated",
                            content = @Content(schema = @Schema(implementation = GetPricePlanVersionResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Unkonw product to attach to product version"),
                    @ApiResponse(responseCode = "400", description = "the product verion with product code and current version in param does not exist ")
            })
    Response createOrUpdateMatrixPricePlanVersion(PricePlanMatrixVersionDto pricePlanMatrixVersionDto);

    /**
     *
     * @param pricePlanMatrixCode
     * @param pricePlanMatrixVersion
     * @return
     */
    @DELETE
    @Path("/pricePlanMatrixVersion/{pricePlanMatrixCode}/{pricePlanMatrixVersion}")
    @Operation(summary = "This endpoint allows to remove a price plan version",
            tags = { "PricePlanMatrixVersion"},
            description ="remove a price plan version with price plan code and current version",
            responses = {
                    @ApiResponse(responseCode="200", description = "the price plan version successfully deleted",
                            content = @Content(schema = @Schema(implementation = ActionStatus.class))),
                    @ApiResponse(responseCode = "404", description = "Unknown price plan version")
                    ,
                    @ApiResponse(responseCode = "400", description = "the price plan version with price plan code and current version in param does not exist or the price plan matrix version is attached to a price plan matrix")
            })
    Response removeMatrixPricePlanVersion(@Parameter @PathParam("pricePlanMatrixCode") String pricePlanMatrixCode,@Parameter @PathParam("pricePlanMatrixVersion")  int pricePlanMatrixVersion);

    /**
     *
     * @param pricePlanMatrixCode
     * @param status
     * @param pricePlanMatrixVersion
     * @return
     */
    @PATCH
    @Path("/pricePlanMatrixVersion/{pricePlanMatrixCode}/{pricePlanMatrixVersion}")
    @Operation(summary = "This endpoint allows to update the price plan version status",
            tags = { "PricePlanMatrixVersion" },
            description ="the product with status DRAFT can be change to PUBLIED or CLOSED ",
            responses = {
                    @ApiResponse(responseCode="200", description = "the price plan version successfully updated",  content = @Content(schema = @Schema(implementation = GetPricePlanVersionResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Unknown price plan version"),
                    @ApiResponse(responseCode = "400", description = "the status of the price plan matrix is already closed")
            })
    Response updatePricePlanMatrixVersionStatus(@Parameter @PathParam("pricePlanMatrixCode") String pricePlanMatrixCode, @Parameter @PathParam("pricePlanMatrixVersion") int pricePlanMatrixVersion, @Parameter @QueryParam("status") VersionStatusEnum status);

    /**
     *
     * @param pricePlanMatrixCode
     * @param pricePlanMatrixVersion
     * @return
     */
    @POST
    @Path("/pricePlanMatrixVersion/duplicate/{pricePlanMatrixCode}/{pricePlanMatrixVersion}")
    @Operation(summary = "This endpoint allows to duplicate a price plan matrix version",
            tags = { "PricePlanMatrixVersion" },
            description ="duplicate a product version",
            responses = {
                    @ApiResponse(responseCode="200", description = "the price plan version successfully duplicated"),
                    @ApiResponse(responseCode = "404", description = "the price plan version with price plan code and current version in param does not exist ")
            })
    Response duplicatePricePlanVersion(@Parameter @PathParam("pricePlanMatrixCode") String pricePlanMatrixCode,
                                       @Parameter @PathParam("pricePlanMatrixVersion") int pricePlanMatrixVersion);


    /**
     * Create a new price plan matrix column
     *
     * @param postData The price plan matrix column's data
     * @return Request processing status
     */
    @POST
    @Path("/pricePlanMatrixColumn")
    @Operation(summary = "This endpoint allows to create a price plan matrix column",
            tags = { "PricePlanMatrixColumn" },
            description ="create a price plan matrix column",
            responses = {
                    @ApiResponse(responseCode="201", description = "the price plan column successfully created"),
                    @ApiResponse(responseCode = "412", description = "the price plan column with code is missing"),
                    @ApiResponse(responseCode = "302", description = "the price plan column already existe with the given code"),
                    @ApiResponse(responseCode = "400", description = "Internat error")
            })
    Response create(PricePlanMatrixColumnDto postData);

    /**
     * Update an existing price plan matrix column
     *
     * @param postData The price plan matrix column's data
     * @return Request processing status
     */
    @PUT
    @Path("/pricePlanMatrixColumn")
    @Operation(summary = "This endpoint allows to update a price plan matrix column",
            tags = { "PricePlanMatrixColumn" },
            description ="update a price plan matrix column",
            responses = {
                    @ApiResponse(responseCode="200", description = "the price plan column successfully updated"),
                    @ApiResponse(responseCode = "400", description = "Internat error")
            })
    Response update(PricePlanMatrixColumnDto postData);

    /**
     * Find a price plan matrix column with a given code
     *
     * @param pricePlanMatrixColumnCode The price plan's code
     * @return pricePlanMatrixDto Returns pricePlanMatrixDto containing pricePlanColumn
     */
    @GET
    @Path("/pricePlanMatrixColumn")
    @Operation(summary = "This endpoint allows to get a price plan matrix column",
            tags = { "PricePlanMatrixColumn" },
            description ="get a price plan matrix column",
            responses = {
                    @ApiResponse(responseCode="200", description = "the price plan column successfully loaded"),
                    @ApiResponse(responseCode = "400", description = "Internat error")
            })
    Response findPricePlanMatrixColumn(@QueryParam("pricePlanMatrixColumnCode") String pricePlanMatrixColumnCode);

    /**
     * Remove an existing price plan matrix column with a given code
     *
     * @param pricePlanMatrixColumnCode The price plan column's code
     * @return Request processing status
     */
    @DELETE
    @Path("/pricePlanMatrixColumn/{pricePlanMatrixColumnCode}")
    @Operation(summary = "This endpoint allows to delete a price plan matrix column",
            tags = { "PricePlanMatrixColumn" },
            description ="delete a price plan matrix column",
            responses = {
                    @ApiResponse(responseCode="200", description = "the price plan column successfully deleted"),
                    @ApiResponse(responseCode = "400", description = "Internat error")
            })
    Response removePricePlanMatrixColumnCode(@PathParam("pricePlanMatrixColumnCode") String pricePlanMatrixColumnCode);


    /**
     * Create a new price plan matrix column
     *
     * @param pricePlanMatrixLineDto The price plan matrix line's data
     * @return Request processing status
     */
    @POST
    @Path("/addPricePlanMatrixLine")
    @Operation(summary = "This endpoint allows to add a price plan matrix line",
            tags = { "PricePlanMatrixLine" },
            description ="add a price plan matrix line",
            responses = {
                    @ApiResponse(responseCode="201", description = "the price plan line successfully added"),
                    @ApiResponse(responseCode = "400", description = "Internat error")
            })
    Response addPricePlanMatrixLine(PricePlanMatrixLineDto pricePlanMatrixLineDto);

    /**
     * Create a new price plan matrix column
     *
     * @param pricePlanMatrixLineDto The price plan matrix line's data
     * @return Request processing status
     */
    @PUT
    @Path("/updatePricePlanMatrixLine")
    @Operation(summary = "This endpoint allows to update a price plan matrix line",
            tags = { "PricePlanMatrixLine" },
            description ="update a price plan matrix line",
            responses = {
                    @ApiResponse(responseCode="200", description = "the price plan line successfully updated"),
                    @ApiResponse(responseCode = "400", description = "Internat error")
            })
    Response updatePricePlanMatrixLine(PricePlanMatrixLineDto pricePlanMatrixLineDto);

    @DELETE
    @Path("/pricePlanMatrixLine/{pricePlanMatrixLineId}")
    @Operation(summary = "This endpoint allows to delete a price plan matrix line",
            tags = { "PricePlanMatrixLine" },
            description ="delete a price plan matrix line",
            responses = {
                    @ApiResponse(responseCode="200", description = "the price plan line successfully deleted"),
                    @ApiResponse(responseCode = "400", description = "Internal error")
            })
    ActionStatus removePricePlanMatrixLine(@PathParam("pricePlanMatrixLineId") Long pricePlanMatrixLineId);

    @GET
    @Path("/pricePlanMatrixLine/{pricePlanMatrixLineId}")
    @Operation(summary = "This endpoint allows to get a price plan matrix line",
            tags = { "PricePlanMatrixLine" },
            description ="get a price plan matrix line",
            responses = {
                    @ApiResponse(responseCode="200", description = "the price plan line successfully loaded"),
                    @ApiResponse(responseCode = "400", description = "Internal error")
            })
    Response getPricePlanMatrixLine(@PathParam("pricePlanMatrixLineId") Long pricePlanMatrixLineId);

    @GET
    @Path("/loadPrices")
    @Operation(summary = "This endpoint allows to load prices",
            tags = { "pricePlan" },
            description ="load prices",
            responses = {
                    @ApiResponse(responseCode="200", description = "the prices are successfully loaded"),
                    @ApiResponse(responseCode = "400", description = "Internal error")
            })
    Response loadPrices(LoadPricesRequest loadPricesRequest);
}