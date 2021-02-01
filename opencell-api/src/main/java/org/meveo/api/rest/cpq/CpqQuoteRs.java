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

package org.meveo.api.rest.cpq;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.cpq.GetListAccountingArticlePricesResponseDto;
import org.meveo.api.dto.cpq.GetPdfQuoteRequestDto;
import org.meveo.api.dto.cpq.QuoteDTO;
import org.meveo.api.dto.cpq.QuoteOfferDTO;
import org.meveo.api.dto.cpq.QuoteVersionDto;
import org.meveo.api.dto.invoice.GetPdfInvoiceRequestDto;
import org.meveo.api.dto.invoice.GetPdfInvoiceResponseDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.cpq.CpqQuotesListResponseDto;
import org.meveo.api.dto.response.cpq.GetPdfQuoteResponseDto;
import org.meveo.api.dto.response.cpq.GetQuoteDtoResponse;
import org.meveo.api.dto.response.cpq.GetQuoteOfferDtoResponse;
import org.meveo.api.dto.response.cpq.GetQuoteVersionDtoResponse;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.model.quote.QuoteStatusEnum;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

/**
 * quote API specification implementation
 * 
 * @author Rachid.AIT
 */
@Path("/quote")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface CpqQuoteRs {

    /**
     * Place a new quote
     * 
     * @param quote Product quote information
     * @param info Http request context
     * @return Product quote information
     */
    @POST
    @Path("/{executeQuotation}")
    @Operation(summary = "Create a quote",
    tags = { "Quote management" },
    description ="",
    responses = {
            @ApiResponse(responseCode="200", description = "The quote is succeffully created",content = @Content(schema = @Schema(implementation = GetQuoteDtoResponse.class))),
            @ApiResponse(responseCode = "412", description = "the parameter quote.applicantAccountCode is missing", content = @Content(schema = @Schema(implementation = MissingParameterException.class))),
            @ApiResponse(responseCode = "302", description = "The quote already exist", content = @Content(schema = @Schema(implementation = EntityAlreadyExistsException.class))),
            @ApiResponse(responseCode = "404", description = "Applicant account is unknown", content = @Content(schema = @Schema(implementation = EntityDoesNotExistsException.class)))
    })
    public Response createQuote(@Parameter(description = "Execute quotation (false/true)", required = true) @PathParam("executeQuotation") boolean executeQuotation,@Parameter(description = "Product quote information", required = false) QuoteDTO quote, @Context UriInfo info);

    /**
     * Get details of a single quote
     * 
     * @param id Product code
     * @param info Http request context
     * @return quote response
     */
    @GET
    @Path("/{quoteCode}")
    @Operation(summary = "Get a quote by its code",
    tags = { "Quote management" },
    description ="",
    responses = {
            @ApiResponse(responseCode="200", description = "The quote is succeffully retrieved",content = @Content(schema = @Schema(implementation = GetQuoteDtoResponse.class))),
            @ApiResponse(responseCode="404", description = "The quote is missing",content = @Content(schema = @Schema(implementation = EntityDoesNotExistsException.class)))
    })
    public Response getQuote(@Parameter(description = "Product quote code", required = true) @PathParam("quoteCode") String code, @Context UriInfo info);

    /**
     * Get a list of quotes optionally filtered by some criteria
     * 
     * @param info Http request context
     * @return A list of quotes matching search criteria
     */
    @POST
    @Path("/list")
    @Operation(summary = "Get a list of quotes optionally filtered by some criteria",
    tags = { "Quote management" },
    description ="",
    responses = {
            @ApiResponse(responseCode="200", description = "quotes are succeffully retrieved",content = @Content(schema = @Schema(implementation = CpqQuotesListResponseDto.class)))
    })
    public Response findQuotes(PagingAndFiltering pagingAndFiltering, @Context UriInfo info);

    /**
     * Modify a quote
     * 
     * @param id Product quote code
     * @param quote Product quote information
     * @param info Http request context
     * @return An updated quote information
     */
    @PUT
    @Path("/{executeQuotation}")
    @Operation(summary = "Modify a quote",
    tags = { "Quote management" },
    description ="",
    responses = {
            @ApiResponse(responseCode="200", description = "The quote is succeffully updated",content = @Content(schema = @Schema(implementation = ActionStatus.class))),
            @ApiResponse(responseCode = "404", description = "The quote is missing", content = @Content(schema = @Schema(implementation = EntityDoesNotExistsException.class))),
            @ApiResponse(responseCode = "404", description = "Applicant account code is missing", content = @Content(schema = @Schema(implementation = EntityDoesNotExistsException.class)))
    })
    public Response updateQuote(@Parameter(description = "Execute quotation (false/true)", required = true) @PathParam("executeQuotation") boolean executeQuotation,@Parameter(description = "Product quote information", required = false) QuoteDTO quote, @Context UriInfo info);
    
    
    /**
     * Modify  a quote item
     * 
     * @param id Product quote code
     * @param quote Product quote information
     * @param info Http request context
     * @return An updated quote information
     */
    @PUT
    @Path("/quoteItem")
    @Operation(summary = "Modify a quote item",
    tags = { "Quote management" },
    description ="",
    responses = {
            @ApiResponse(responseCode="200", description = "The quote item is succeffully updated",content = @Content(schema = @Schema(implementation = GetQuoteOfferDtoResponse.class)))
    })
    public Response updateQuoteItem(@Parameter(description = "Product quote information", required = true) QuoteOfferDTO quoteitem, @Context UriInfo info);
    
    /**
     * Get details of a single quote
     * 
     * @param id Product code
     * @param info Http request context
     * @return quote response
     */
    @GET
    @Path("/quoteItem/{quoteCode}/{quoteVersion}")
    @Operation(summary = "Get List of quote offer by quote code and quote version number",
    tags = { "Quote management" },
    description ="",
    responses = {
            @ApiResponse(responseCode="200", description = "The quote is succeffully retrieved",content = @Content(schema = @Schema(implementation = GetQuoteDtoResponse.class))),
            @ApiResponse(responseCode="404", description = "The quote offer doesn't exist",content = @Content(schema = @Schema(implementation = EntityDoesNotExistsException.class)))
    })
    public Response findQuoteItems(@Parameter(description = "Product quote code", required = true) @PathParam("quoteCode") String quoteCode, 
    								@Parameter(description = "Product quote code", required = true) @PathParam("quoteVersion") int quoteVersion,
    								@Context UriInfo info);

    /**
     * Delete a quote.
     * 
     * @param id Product quote code
     * @param info Http request context
     * @return Response status
     */
    @DELETE
    @Path("/{quoteCode}")
    @Operation(summary = "Delete a quote.",
    tags = { "Quote management" },
    description ="",
    responses = {
            @ApiResponse(responseCode="200", description = "The quote is succeffully deleted",content = @Content(schema = @Schema(implementation = ActionStatus.class))),
            @ApiResponse(responseCode = "404", description = "Quote code doesn't exist", content = @Content(schema = @Schema(implementation = EntityDoesNotExistsException.class)))
    })
    public Response deleteQuote(@Parameter(description = "Product quote code", required = false) @PathParam("quoteCode") String code, @Context UriInfo info);

    /**
     * Place an order based on a quote.
     * 
     * @param id Product quote code
     * @param info Http request context
     * @return Response status
     */
    @POST
    @Path("/placeOrder/{quoteCode}/{quoteVersion}")
    @Operation(summary = "Place an order based on a quote",
    tags = { "Quote management" },
    description ="",
    responses = {
            @ApiResponse(responseCode="200", description = "order succeffully created from current quote",content = @Content(schema = @Schema(implementation = ActionStatus.class)))
    })
    public Response placeOrder(@Parameter(description = "quote code attached to quote version", required = false) @PathParam("quoteCode") String quoteCode, 
			@Parameter(description = "quote version number", required = false) @PathParam("quoteVersion") int quoteVersion, @Context UriInfo info);
    
    /**
     * Create a new quote item
     * 
     * @param quote Product quote information
     * @param info Http request context
     * @return Product quote information
     */
    @POST
    @Path("/quoteItem")
    @Operation(summary = "Create a quote item",
    tags = { "Quote management" },
    description ="",
    responses = {
            @ApiResponse(responseCode="200", description = "quote item is succeffully created",content = @Content(schema = @Schema(implementation = GetQuoteOfferDtoResponse.class)))
    })
    public Response createQuoteItem(@Parameter(description = "Product quote item information", required = false) QuoteOfferDTO quoteItem, @Context UriInfo info);

    
    @POST
    @Path("/quoteVersion")
    @Operation(summary = "Create a quote version",
    tags = { "Quote management" },
    description ="",
    responses = {
            @ApiResponse(responseCode="200", description = "New quote version is succeffully created",content = @Content(schema = @Schema(implementation = GetQuoteVersionDtoResponse.class))),
            @ApiResponse(responseCode = "412", description = "the quote version with code or short description  is missing"),
    })
    public Response createQuoteVersion(@Parameter(description = "Product quote version information", required = false) QuoteVersionDto quoteVersion, @Context UriInfo info);


    
    @DELETE
    @Path("/quoteVersion/{quoteCode}/{quoteVersion}")
    @Operation(summary = "Delete a quote version",
    tags = { "Quote management" },
    description ="",
    responses = {
            @ApiResponse(responseCode="200", description = "Existing quote version is succeffully deleted",content = @Content(schema = @Schema(implementation = ActionStatus.class))),
            @ApiResponse(responseCode="404", description = "No quote version was found with quoteCode and quoteVersion in parameter", 
            			content = @Content(schema = @Schema(implementation = EntityDoesNotExistsException.class)))
    })
    public Response deleteQuoteVersion(@Parameter(description = "quote code attached to quote version", required = false) @PathParam("quoteCode") String quoteCode, 
    									@Parameter(description = "quote version number", required = false) @PathParam("quoteVersion") int quoteVersion, @Context UriInfo info);
    
    /**
     * Delete a quote item.
     * 
     * @param id Product quote code
     * @param info Http request context
     * @return Response status
     */
    @DELETE
    @Path("/quoteItem/{quoteItemId}")
    @Operation(summary = "Delete a quote item",
    tags = { "Quote management" },
    description ="",
    responses = {
            @ApiResponse(responseCode="200", description = "quote item is succeffully deleted",content = @Content(schema = @Schema(implementation = ActionStatus.class)))
    })
    public Response deleteQuoteItem(@Parameter(description = "Product quote item code", required = false) @PathParam("quoteItemId") Long quoteItemId, @Context UriInfo info);
    
    
    @PUT
    @Path("/{quoteCode}/update/status")
    @Operation(summary = "this endpoint allow you to update a quote of status",
    tags = { "Quote management" },
    description ="status can not be modified if is already Cancelled or rejected",
    responses = {
            @ApiResponse(responseCode="200", description = "quote status is succeffully updated",content = @Content(schema = @Schema(implementation = ActionStatus.class))),
            @ApiResponse(responseCode = "404", description = "Quote  doesn't exist", content = @Content(schema = @Schema(implementation = EntityDoesNotExistsException.class))),
            @ApiResponse(responseCode = "400", description = "the status can not be update, because of its current status", content = @Content(schema = @Schema(implementation = BusinessException.class)))
    })
    public Response updateQuoteStatus( @PathParam("quoteCode") String quoteCode,
    							@QueryParam("status") QuoteStatusEnum status
    		);
    

    @PUT
    @Path("/{quoteCode}/{currentVersion}/update/status")
    @Operation(summary = "this endpoint allow you to update a quote version of status",
    tags = { "Quote management" },
    description ="status can not be modified if is already Closed or Published",
    responses = {
            @ApiResponse(responseCode="200", description = "quote version status is succeffully updated",content = @Content(schema = @Schema(implementation = ActionStatus.class))),
            @ApiResponse(responseCode = "404", description = "Quote version doesn't exist", content = @Content(schema = @Schema(implementation = EntityDoesNotExistsException.class))),
            @ApiResponse(responseCode = "400", description = "the status can not be update, because of its current status", content = @Content(schema = @Schema(implementation = BusinessException.class)))
    })
    public Response updateQuoteVersionStatus( @PathParam("quoteCode") String quoteCode,
    										@PathParam("currentVersion") int currentVersion,
    							@QueryParam("status") VersionStatusEnum status
    		);
    @POST
    @Path("/duplicate/{quoteCode}/{quoteversion}")
    @Operation(summary = "this endpoint allow you to duplicate a quote from quote code",
    tags = { "Quote management" },
    description ="duplicate quote and their related entities",
    responses = {
            @ApiResponse(responseCode="200", description = "quote version status is succeffully updated",content = @Content(schema = @Schema(implementation = ActionStatus.class))),
            @ApiResponse(responseCode = "404", description = "Quote version doesn't exist", content = @Content(schema = @Schema(implementation = EntityDoesNotExistsException.class)))
    })
    public Response duplicateQuote( @Parameter(required = true)	@PathParam("quoteCode") String quoteCode, @Parameter(required = true)	@PathParam("quoteversion") int quoteversion);
    /*
    @GET
    @Path("/quoteItem/{quoteOfferId}")
    @Operation(summary = "Delete a quote item",
    tags = { "Quote management" },
    description ="",
    responses = {
            @ApiResponse(responseCode="200", description = "quote item is succeffully deleted",content = @Content(schema = @Schema(implementation = GetQuoteOfferDtoResponse.class)))
    })
    public Response findQuoteItem(@Parameter(description = "", required = true) @PathParam("quoteOfferId") Long quoteOfferId); */
    

    @POST
    @Path("/quoteQuotation")
    @Operation(summary = "Get quote quotation",
    tags = { "Quotation" },
    description ="",
    responses = {
            @ApiResponse(responseCode="200", description = "quotation is succefully done!",content = @Content(schema = @Schema(implementation = ActionStatus.class)))
    })
	Response quoteQuotation(@Parameter(description = "quote code", required = false) @QueryParam("quoteCode") String quoteCode, 
			@Parameter(description = "quote version number", required = false) @QueryParam("quoteVersion") int quoteVersion);

    @GET
    @Path("/generateQuoteXml")
    @Operation(summary = "Generate the quote XML and optionnaly quote PDF if generatePdf is True ",
    tags = { "Quotation" },
    description ="",
    responses = {
            @ApiResponse(responseCode="200", description = "quote XML is succefully generated!",content = @Content(schema = @Schema(implementation = GetPdfQuoteResponseDto.class)))
    })
    GetPdfQuoteResponseDto generateQuoteXml(@QueryParam("quoteCode") String quoteCode, @QueryParam("currentVersion") int currentVersion, @QueryParam("generatePdf") boolean generatePdf);
    
    
    
    /**
     * get the quote PDF file. 
     */
    @GET
    @Path("/getQuotePDF")
    @Operation(summary = "Get the quote PDF. if generatePdf is true, the PDF is generated and override existing one if already exists",
    tags = { "Quotation" },
    description ="",
    responses = {
            @ApiResponse(responseCode="200", description = "quote PDF is succefully returned!",content = @Content(schema = @Schema(implementation = GetPdfQuoteResponseDto.class)))
    })
    GetPdfQuoteResponseDto getQuotePDF(@QueryParam("quoteCode") String quoteCode, @QueryParam("currentVersion") int currentVersion, @QueryParam("generatePdf") boolean generatePdf);;
    
}