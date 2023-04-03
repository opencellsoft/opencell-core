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
import org.meveo.api.dto.DatePeriodDto;
import org.meveo.api.dto.catalog.ConvertedPricePlanInputDto;
import org.meveo.api.dto.catalog.ConvertedPricePlanVersionDto;
import org.meveo.api.dto.catalog.PricePlanMatrixColumnDto;
import org.meveo.api.dto.catalog.PricePlanMatrixDto;
import org.meveo.api.dto.catalog.PricePlanMatrixVersionDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.catalog.GetPricePlanResponseDto;
import org.meveo.api.dto.response.catalog.GetPricePlanVersionResponseDto;
import org.meveo.api.dto.response.catalog.PricePlanMatrixLinesDto;
import org.meveo.api.dto.response.catalog.PricePlanMatrixesResponseDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.model.cpq.enums.VersionStatusEnum;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

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
    @Operation(summary = "create a price plan ",
            tags = { "Price Plan" },
            description ="create a price plan matrix",
            responses = {
                    @ApiResponse(responseCode="200", description = "the price plan is successfully created"),
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
    @Operation(summary = "Update an existing price plan matrix",
    tags = { "Price Plan" },
    description ="Update an existing price plan matrix",
    responses = {
            @ApiResponse(responseCode="200", description = "the price plan is successfully updated"),
            @ApiResponse(responseCode = "400", description = "Internat error")
    })
    ActionStatus update(PricePlanMatrixDto postData);

    /**
     * Find a price plan matrix with a given code
     *
     * @param pricePlanCode The price plan's code
     * @return pricePlanMatrixDto Returns pricePlanMatrixDto containing pricePlan
     */
    @GET
    @Path("/")
    @Operation(summary = "find a price plan matrix with a given code",
    tags = { "Price Plan" },
    description ="Find a price plan matrix with a given code",
    responses = {
            @ApiResponse(responseCode="200", description = "the price plan is successfully retreived",content = @Content(schema = @Schema(implementation = GetPricePlanResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Internat error")
    })
    GetPricePlanResponseDto find(@Parameter(description = "The price plan code", required = true) @QueryParam("pricePlanCode") String pricePlanCode,
                                 @Parameter(description = "Indicate if returning pricePlanMatrixLine") @DefaultValue("true")
                                 @QueryParam("returnPricePlanMatrixLine") boolean returnPricePlanMatrixLine);

    /**
     * Remove an existing price plan matrix with a given code
     *
     * @param pricePlanCode The price plan's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{pricePlanCode}")
    @Operation(summary = "Remove an existing price plan matrix with a given code",
    tags = { "Price Plan" },
    description ="Remove an existing price plan matrix with a given code",
    responses = {
            @ApiResponse(responseCode="200", description = "the price plan is successfully removed",content = @Content(schema = @Schema(implementation = ActionStatus.class))),
            @ApiResponse(responseCode = "400", description = "Internat error")
    })
    ActionStatus remove(@Parameter(description = "The price plan code", required = true) @PathParam("pricePlanCode") String pricePlanCode);

    /**
     * List price plan matrix.
     *
     * @param eventCode The charge's code linked to price plan.
     * @return Return pricePlanMatrixes
     */
    @GET
    @Path("/list")
    @Operation(summary = "List price plan matrix",
    tags = { "Price Plan" },
    description ="List price plan matrix",
    responses = {
            @ApiResponse(responseCode="200", description = "price plans are successfully retreived",content = @Content(schema = @Schema(implementation = PricePlanMatrixesResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Internat error")
    })
    PricePlanMatrixesResponseDto listPricePlanByEventCode(@Parameter(description = "The event code", required = true) @QueryParam("eventCode") String eventCode);

    /**
     * List PricePlanMatrixes
     *
     * @return List of PricePlanMatrixes
     */
    @GET
    @Path("/listGetAll")
    @Operation(summary = "List all price plans.",
            tags = { "Price Plan" })
    PricePlanMatrixesResponseDto listGetAll();

    /**
     * Create new or update an existing price plan matrix
     *
     * @param postData The price plan matrix's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    @Operation(summary = "Create new or update an existing price plan matrix ",
    tags = { "Price Plan" },
    description ="Create new or update an existing price plan matrix",
    responses = {
            @ApiResponse(responseCode="200", description = "the price plan is successfully created/updated"),
            @ApiResponse(responseCode = "400", description = "Internat error")
    })
    ActionStatus createOrUpdate(PricePlanMatrixDto postData);

    /**
     * Enable a Price plan with a given code
     *
     * @param code Price plan code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
    @Operation(summary = "Enable a Price plan with a given code",
    tags = { "Price Plan" },
    description ="Enable a Price plan with a given code",
    responses = {
            @ApiResponse(responseCode="200", description = "the price plan is successfully enabled"),
            @ApiResponse(responseCode = "400", description = "Internat error")
    })
    ActionStatus enable(@Parameter(description = "The price plan code", required = true) @PathParam("code") String code);

    /**
     * Disable a Price plan with a given code
     *
     * @param code Price plan code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
    @Operation(summary = "Disable a Price plan with a given code",
    tags = { "Price Plan" },
    description ="Disable a Price plan with a given code",
    responses = {
            @ApiResponse(responseCode="200", description = "the price plan is successfully disabled"),
            @ApiResponse(responseCode = "400", description = "Internat error")
    })
    ActionStatus disable(@Parameter(description = "The price plan code", required = true) @PathParam("code") String code);


    @POST
    @Path("/pricePlanMatrixVersion")
    @Operation(summary = "create or update a price plan version",
            tags = { "Price Plan" },
            description ="create a price plan version if it doesn't exist or update an existing price plan version",
            responses = {
                    @ApiResponse(responseCode="200", description = "the price plan version successfully created or updated",
                            content = @Content(schema = @Schema(implementation = GetPricePlanVersionResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Unkonw product to attach to product version"),
                    @ApiResponse(responseCode = "400", description = "the product verion with product code and current version in param does not exist ")
            })
    Response createOrUpdateMatrixPricePlanVersion(PricePlanMatrixVersionDto pricePlanMatrixVersionDto);
    
    @PUT
    @Path("/pricePlanMatrixVersion")
    @Operation(summary = "update a price plan version",
            tags = { "Price Plan" },
            description ="update an existing price plan version",
            responses = {
                    @ApiResponse(responseCode="200", description = "the price plan version successfully updated",
                            content = @Content(schema = @Schema(implementation = GetPricePlanVersionResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Unkonw product to attach to product version"),
                    @ApiResponse(responseCode = "400", description = "the product verion with product code and current version in param does not exist ")
            })
    Response updateMatrixPricePlanVersion(PricePlanMatrixVersionDto pricePlanMatrixVersionDto);

    /**
     *
     * @param pricePlanMatrixCode
     * @param pricePlanMatrixVersion
     * @return
     */
    @DELETE
    @Path("/{pricePlanMatrixCode}/pricePlanMatrixVersions/{pricePlanMatrixVersion}")
    @Operation(summary = "remove a price plan version",
            tags = { "Price Plan"},
            description ="remove a price plan version with price plan code and current version",
            responses = {
                    @ApiResponse(responseCode="200", description = "the price plan version successfully deleted",
                            content = @Content(schema = @Schema(implementation = ActionStatus.class))),
                    @ApiResponse(responseCode = "404", description = "Unknown price plan version")
                    ,
                    @ApiResponse(responseCode = "400", description = "the price plan version with price plan code and current version in param does not exist or the price plan matrix version is attached to a price plan matrix")
            })
    Response removeMatrixPricePlanVersion(@Parameter(description = "The price plan matrix code", required = true)  @PathParam("pricePlanMatrixCode") String pricePlanMatrixCode,
    									 @Parameter(description = "The price plan matrix version", required = true)  @PathParam("pricePlanMatrixVersion")  int pricePlanMatrixVersion);

    /**
     *
     * @param pricePlanMatrixCode
     * @param status
     * @param pricePlanMatrixVersion
     * @return
     */
    @PUT
    @Path("/{pricePlanMatrixCode}/pricePlanMatrixVersions/{pricePlanMatrixVersion}/status/{status}")
    @Operation(summary = "update the price plan version status",
            tags = { "Price Plan" },
            description ="the product with status DRAFT can be change to PUBLIED or CLOSED ",
            responses = {
                    @ApiResponse(responseCode="200", description = "the price plan version successfully updated",  content = @Content(schema = @Schema(implementation = GetPricePlanVersionResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Unknown price plan version"),
                    @ApiResponse(responseCode = "400", description = "the status of the price plan matrix is already closed")
            })
    Response updatePricePlanMatrixVersionStatus(@Parameter(description = "The price plan matrix code", required = true)  @PathParam("pricePlanMatrixCode") String pricePlanMatrixCode,
    											@Parameter(description = "The price plan matrix version", required = true)  @PathParam("pricePlanMatrixVersion") int pricePlanMatrixVersion,
    											@Parameter(description = "The status of Price plan", required = true)  @PathParam("status") VersionStatusEnum status);

    @GET
    @Path("/pricePlanMatrixVersions")
    @Operation(summary = "get the price plan versions",
            tags = { "Price Plan" },
            description ="load the list of ppm versions",
            responses = {
                    @ApiResponse(responseCode="200", description = "the list of price plan version successfully updated",  content = @Content(schema = @Schema(implementation = GetPricePlanVersionResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Unknown error")
            })
    Response listPricePlanMatrixVersions(PagingAndFiltering pagingAndFiltering);

    /**
     *
     * @param pricePlanMatrixCode
     * @param pricePlanMatrixVersion
     * @return
     */
    @POST
    @Path("/{pricePlanMatrixCode}/pricePlanMatrixVersions/{pricePlanMatrixVersion}/duplication")
    @Operation(summary = "duplicate a price plan matrix version",
            tags = { "Price Plan" },
            description ="duplicate a product version",
            responses = {
                    @ApiResponse(responseCode="200", description = "the price plan version successfully duplicated"),
                    @ApiResponse(responseCode = "404", description = "the price plan version with price plan code and current version in param does not exist "),
                    @ApiResponse(responseCode = "409", description = "Warning : Unable to confirm - An other period is overlapping.")
            })
    Response duplicatePricePlanVersion(@Parameter(description = "The price plan matrix code", required = true)  @PathParam("pricePlanMatrixCode") String pricePlanMatrixCode,
			   @Parameter(description = "The price plan matrix version to be duplicated", required = true)  @PathParam("pricePlanMatrixVersion") int pricePlanMatrixVersion,DatePeriodDto validity);


/**
    
    
    /**
     * Create a new price plan matrix column
     *
     * @param postData The price plan matrix column's data
     * @return Request processing status
     */
    @POST
    @Path("/{pricePlanMatrixCode}/pricePlanMatrixVersions/{pricePlanMatrixVersion}/pricePlanMatrixColumns")
    @Operation(summary = "create a price plan matrix column",
            tags = {"Price Plan"},
            description = "create a price plan matrix column",
            responses = {
                    @ApiResponse(responseCode = "201", description = "the price plan column successfully created"),
                    @ApiResponse(responseCode = "412", description = "the price plan column with code is missing"),
                    @ApiResponse(responseCode = "302", description = "the price plan column already existe with the given code"),
                    @ApiResponse(responseCode = "400", description = "Internat error")
            })
    Response create(@Parameter @PathParam("pricePlanMatrixCode") String pricePlanMatrixCode,
                    @Parameter @PathParam("pricePlanMatrixVersion") int pricePlanMatrixVersion, PricePlanMatrixColumnDto postData);

    /**
     * Update an existing price plan matrix column
     *
     * @param postData The price plan matrix column's data
     * @return Request processing status
     */
    @PUT
    @Path("/{pricePlanMatrixCode}/pricePlanMatrixVersions/{pricePlanMatrixVersion}/pricePlanMatrixColumns")
    @Operation(summary = "update a price plan matrix column",
            tags = {"Price Plan"},
            description = "update a price plan matrix column",
            responses = {
                    @ApiResponse(responseCode = "200", description = "the price plan column successfully updated"),
                    @ApiResponse(responseCode = "400", description = "Internat error")
            })
    Response update(@Parameter @PathParam("pricePlanMatrixCode") String pricePlanMatrixCode,
                    @Parameter @PathParam("pricePlanMatrixVersion") int pricePlanMatrixVersion, PricePlanMatrixColumnDto postData);

    /**
     * Find a price plan matrix column with a given code
     *
     * @param pricePlanMatrixColumnCode The price plan's code
     * @return pricePlanMatrixDto Returns pricePlanMatrixDto containing pricePlanColumn
     */
    @GET
    @Path("/pricePlanMatrixColumns/{pricePlanMatrixColumnCode}")
    @Operation(summary = "get a price plan matrix column",
            tags = { "Price Plan" },
            description ="get a price plan matrix column",
            responses = {
                    @ApiResponse(responseCode="200", description = "the price plan column successfully loaded"),
                    @ApiResponse(responseCode = "400", description = "Internat error")
            })
    Response findPricePlanMatrixColumn(@Parameter(description = "The price plan matrix column code", required = true) @PathParam("pricePlanMatrixColumnCode") String pricePlanMatrixColumnCode);

    /**
     * Remove an existing price plan matrix column with a given code
     *
     * @param pricePlanMatrixColumnCode The price plan column's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{pricePlanMatrixCode}/pricePlanMatrixVersions/{pricePlanMatrixVersion}/pricePlanMatrixColumns/{pricePlanMatrixColumnCode}")
    @Operation(summary = "delete a price plan matrix column",
            tags = { "Price Plan" },
            description ="delete a price plan matrix column",
            responses = {
                    @ApiResponse(responseCode="200", description = "the price plan column successfully deleted"),
                    @ApiResponse(responseCode = "400", description = "Internat error")
            })
    Response removePricePlanMatrixColumnCode(@Parameter @PathParam("pricePlanMatrixCode") String pricePlanMatrixCode,
            @Parameter @PathParam("pricePlanMatrixVersion") int pricePlanMatrixVersion,
            @Parameter(description = "The price plan matrix column code", required = true) @PathParam("pricePlanMatrixColumnCode") String pricePlanMatrixColumnCode);


    @DELETE
    @Path("/pricePlanMatrixLines/{pricePlanMatrixLineId}")
    @Operation(summary = "delete a price plan matrix line",
            tags = { "Price Plan" },
            description ="delete a price plan matrix line",
            responses = {
                    @ApiResponse(responseCode="200", description = "the price plan line successfully deleted"),
                    @ApiResponse(responseCode = "400", description = "Internal error")
            })
    ActionStatus removePricePlanMatrixLine(@Parameter(description = "The price plan matrix line id", required = true) @PathParam("pricePlanMatrixLineId") Long pricePlanMatrixLineId);

    @DELETE
    @Path("/pricePlanMatrixLines")
    @Operation(summary = "delete list of a price plan matrix line",
            tags = { "Price Plan" },
            description ="delete list of a price plan matrix line",
            responses = {
                    @ApiResponse(responseCode="200", description = "the price plan lines successfully deleted"),
                    @ApiResponse(responseCode = "400", description = "Internal error")
            })
    ActionStatus removePricePlanMatrixLines(PricePlanMatrixLinesDto pricePlanMatrixLinesDto);

    @GET
    @Path("/pricePlanMatrixLines/{pricePlanMatrixLineId}")
    @Operation(summary = "get a price plan matrix line",
            tags = { "Price Plan" },
            description ="get a price plan matrix line",
            responses = {
                    @ApiResponse(responseCode="200", description = "the price plan line successfully loaded"),
                    @ApiResponse(responseCode = "400", description = "Internal error")
            })
    Response getPricePlanMatrixLine(@Parameter(description = "The price plan matrix line id", required = true) @PathParam("pricePlanMatrixLineId") Long pricePlanMatrixLineId);

    /**
     * add all price plan matrix lines
     *
     */
    @POST
    @Path("/{pricePlanMatrixCode}/pricePlanMatrixVersions/{pricePlanMatrixVersion}/pricePlanMatrixLines")
    @Operation(summary = "add all price plan matrix lines",
            tags = { "Price Plan" },
            description ="add all price plan matrix lines",
            responses = {
                    @ApiResponse(responseCode="201", description = "the price plan line successfully added",content = @Content(schema = @Schema(implementation = GetPricePlanVersionResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Internat error")
            })
    Response addPricePlanMatrixLines(@Parameter @PathParam("pricePlanMatrixCode") String pricePlanMatrixCode,
                                     @Parameter @PathParam("pricePlanMatrixVersion") int pricePlanMatrixVersion, PricePlanMatrixLinesDto pricePlanMatrixLinesDto);
    

    /**
     * add all price plan matrix lines
     *
     */
    @PUT
    @Path("/{pricePlanMatrixCode}/pricePlanMatrixVersions/{pricePlanMatrixVersion}/pricePlanMatrixLines")
    @Operation(summary = "update all price plan matrix lines",
            tags = { "Price Plan" },
            description ="update all price plan matrix lines",
            responses = {
                    @ApiResponse(responseCode="201", description = "the price plan line successfully updated",content = @Content(schema = @Schema(implementation = GetPricePlanVersionResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Internat error")
            })
    Response updatePricePlanMatrixLines(@Parameter @PathParam("pricePlanMatrixCode") String pricePlanMatrixCode,
            @Parameter @PathParam("pricePlanMatrixVersion") int pricePlanMatrixVersion, PricePlanMatrixLinesDto pricePlanMatrixLinesDto);

    /**
    * add all price plan matrix lines
    *
    */
    @PUT
    @Path("/{pricePlanMatrixCode}/pricePlanMatrixVersions/{pricePlanMatrixVersion}/pricePlanMatrixLines/updateWithoutDelete")
    @Operation(summary = "Creat and update all price plan matrix lines",
        tags = { "Price Plan" },
        description ="Creat/update all price plan matrix lines",
        responses = {
            @ApiResponse(responseCode="201", description = "the price plan line successfully updated",content = @Content(schema = @Schema(implementation = GetPricePlanVersionResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Internat error")
        })
    Response updateWithoutDeletePricePlanMatrixLines(@Parameter @PathParam("pricePlanMatrixCode") String pricePlanMatrixCode,
                @Parameter @PathParam("pricePlanMatrixVersion") int pricePlanMatrixVersion, PricePlanMatrixLinesDto pricePlanMatrixLinesDto);
    
    /**
    *
    * @param pricePlanMatrixCode
    * @param pricePlanMatrixVersion
    * @return
    */
    @POST
    @Path("/{pricePlanMatrixCode}/{pricePlanMatrixVersion}/duplication")
    @Operation(summary = "duplicate a price plan matrix version",
            tags = { "Price Plan" },
            description ="duplicate a product version",
            responses = {
                    @ApiResponse(responseCode="200", description = "the price plan version successfully duplicated"),
                    @ApiResponse(responseCode = "404", description = "the price plan version with price plan code and current version in param does not exist ")
            })
    Response duplicatePricePlan(@Parameter(description = "The price plan matrix code", required = true)  @PathParam("pricePlanMatrixCode") String pricePlanMatrixCode,
    							@Parameter(description = "The price plan matrix new code", required = true) @QueryParam("pricePlanMatrixNewCode") String pricePlanMatrixNewCode,
    							@Parameter(description = "The price plan matrix version to be duplicated", required = true)  @PathParam("pricePlanMatrixVersion") int pricePlanMatrixVersion, @QueryParam("priceVersionType") String priceVersionType);
    
    
    @DELETE
    @Path("/pricePlanMatrixVersion/{pricePlanVersionId}/convertedPricePlanMatrixLines/tradingCurrency/{tradingCurrencyCode}")
    @Operation(summary = "duplicate a price plan matrix version",
    tags = { "Price Plan" },
    description ="duplicate a product version",
    responses = {
            @ApiResponse(responseCode="200", description = "delete all converted price matrix line"),
            @ApiResponse(responseCode = "404", description = "the trading courrency for plan matrix version doesn't exit")
    })
    Response deleteConvertedPricePlanMatrixLines(@Parameter(description = "the id of price plan matrix version ", required = true)  @PathParam("pricePlanVersionId") Long pricePlanVersionId,  
                                                    @Parameter(description = "The price plan matrix code", required = true) @PathParam("tradingCurrencyCode") String tradingCurrencyCode);
    

    /**
    *
    * @param pricePlanMatrixCode
    * @param pricePlanMatrixVersion
    * @return
    */
    @POST
    @Path("/convertedPricePlanVersion")
    @Operation(summary = "Create a converted price plan version",
            tags = { "Price Plan" },
            description ="Create a converted price plan version",
            responses = {
                    @ApiResponse(responseCode="200", description = "the converted price plan version successfully created"),
                    @ApiResponse(responseCode = "404", description = "the price plan version with price plan id  in param does not exist "),
                    @ApiResponse(responseCode = "404", description = "the trading currency with price plan code or id in param does not exist "),
                    @ApiResponse(responseCode = "412", description = "the trading currency is mandatory to create price plan version "),
                    @ApiResponse(responseCode = "412", description = "the price plan version is mandatory to create price plan version ")
            })
    Response createConvertedPricePlanVersion(ConvertedPricePlanVersionDto postData);

    /**
     * Update Converted Price Plan Version
     *
     * @param cppvId
     * @param postData
     * @return
     */
    @PUT
    @Path("/convertedPricePlanVersion/{id}")
    @Operation(summary = "update a converted price plan version",
            tags = { "Price Plan" },
            description ="Update a converted price plan version",
            responses = {
                    @ApiResponse(responseCode="200", description = "the converted price plan version successfully Updated"),
                    @ApiResponse(responseCode = "404", description = "the price plan version with price plan id  in param does not exist "),
                    @ApiResponse(responseCode = "404", description = "the trading currency with price plan code or id in param does not exist "),
                    @ApiResponse(responseCode = "412", description = "the trading currency is mandatory to create price plan version "),
                    @ApiResponse(responseCode = "412", description = "the price plan version is mandatory to create price plan version ")
            })
    Response updateConvertedPricePlanVersion(@Parameter(description = "The converted price plan version id", required = true)  @PathParam("id") Long cppvId, ConvertedPricePlanVersionDto postData);

    /**
	 * Delete converted price plan version
	 * 
	 * @param cppvId
	 * @return
	 */
    @DELETE
    @Path("/convertedPricePlanVersion/{id}")
    @Operation(summary = "Delete a converted price plan version",
            tags = { "Price Plan" },
            description ="Delete a converted price plan version",
            responses = {
                    @ApiResponse(responseCode="200", description = "the converted price plan version successfully deleted"),
                    @ApiResponse(responseCode = "404", description = "the price plan version with price plan id  in param does not exist "),
                    @ApiResponse(responseCode = "404", description = "the trading currency with price plan code or id in param does not exist "),
                    @ApiResponse(responseCode = "412", description = "the trading currency is mandatory to create price plan version "),
                    @ApiResponse(responseCode = "412", description = "the price plan version is mandatory to create price plan version ")
            })
    Response deleteConvertedPricePlanVersion(@Parameter(description = "ID of converted price plan to delete") @PathParam("id") Long ccpvId);

    @POST
    @Path("/convertedPricePlanMatrixLines/disable")
    Response disableAllConvertedPricePlan(@Parameter(description = "contain information about all converted price that will be disabled") ConvertedPricePlanInputDto convertedPricePlanInputDto);
    

    @POST
    @Path("/convertedPricePlanMatrixLines/enable")
    Response enableAllConvertedPricePlan(@Parameter(description = "contain information about all converted price that will be enabled") ConvertedPricePlanInputDto convertedPricePlanInputDto);
    
    @POST
    @Path("convertedPricePlanVersion/{id}/enable")
    Response enableConvertedVersionPricePlan(@PathParam("id") Long id );

    @POST
    @Path("convertedPricePlanVersion/{id}/disable")
    Response disableConvertedVersionPricePlan(@PathParam("id") Long id );

    @POST
    @Path("convertedPricePlanMatrixLines/calculate")
    Response calculateConvertedPricePlanMatrixLine(ConvertedPricePlanVersionDto postData);

}
