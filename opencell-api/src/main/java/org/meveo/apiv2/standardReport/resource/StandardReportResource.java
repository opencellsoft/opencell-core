package org.meveo.apiv2.standardReport.resource;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.Date;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import io.swagger.v3.oas.annotations.Parameter;
import org.meveo.apiv2.generic.GenericPagingAndFiltering;
import org.meveo.apiv2.models.ApiException;
import org.meveo.apiv2.ordering.resource.order.Orders;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/standardReports")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public interface StandardReportResource {

    @GET
    @Path("/AgedReceivables")
    @Operation(summary = "Return aged balance", tags = {"AgedReceivables" },
            description = "Returns aged balance",
            responses = {
            		@ApiResponse(
                            headers = {
                                    @Header(name = "ETag",
                                            description = "a pseudo-unique identifier that represents the version of the data sent back.",
                                            schema = @Schema(type = "integer", format = "int64")
                                    )
                            },
                            description = "list of orders", content = @Content(schema = @Schema(implementation = Orders.class))),
            @ApiResponse(responseCode = "200", description = "aged balance list"),
            @ApiResponse(responseCode = "404", description = "No data found",
                    content = @Content(schema = @Schema(implementation = ApiException.class))) })
    Response getAgedReceivables(@DefaultValue("0") @QueryParam("offset") Long offset,
                                @DefaultValue("50") @QueryParam("limit") Long limit,
                                @QueryParam("sortOrder") String sort, @QueryParam("sortBy") String orderBy,
                                @QueryParam("customerAccountCode") String customerAccountCode,
                                @QueryParam("startDate") Date startDate,
                                @QueryParam("startDueDate") Date startDueDate,
                                @QueryParam("endDueDate") Date endDueDate,
                                @QueryParam("customerAccountDescription") String customerAccountDescription,
                                @QueryParam("sellerDescription") String sellerDescription,
                                @QueryParam("sellerCode") String sellerCode,
                                @QueryParam("invoiceNumber") String invoiceNumber,
                                @QueryParam("stepInDays") Integer stepInDays,
                                @QueryParam("numberOfPeriods") Integer numberOfPeriods,
                                @QueryParam("tradingCurrency") String tradingCurrency,
                                @QueryParam("funcCurrency") String functionalCurrency,
                                @Context Request request);

    @POST
    @Path("/AgedReceivables/export/{fileFormat}")
    @Operation(summary = "Export aged balance",
                tags = { "AgedReceivables" },
                description = "Returns aged balance",
                responses = {
                                @ApiResponse(responseCode = "200", description = "Exported aged balance ist"),
                                @ApiResponse(responseCode = "400", description = "No Data Found")
                            })
    Response exportAgedReceivables(
            @Parameter(description = "file format", required = true) @PathParam("fileFormat") String fileFormat,
            @Parameter(description = "Locale") @QueryParam("locale") String locale,
            @Parameter(description = "the AgedReceivables input object", required = true) GenericPagingAndFiltering input,
            @Context Request request);
}