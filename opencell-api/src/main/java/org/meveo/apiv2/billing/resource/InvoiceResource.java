package org.meveo.apiv2.billing.resource;

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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import org.meveo.apiv2.billing.BasicInvoice;
import org.meveo.apiv2.billing.Invoice;
import org.meveo.apiv2.billing.InvoiceInput;
import org.meveo.apiv2.billing.InvoiceLineInput;
import org.meveo.apiv2.billing.InvoiceLinesToRemove;
import org.meveo.apiv2.billing.InvoiceLinesInput;
import org.meveo.apiv2.billing.Invoices;
import org.meveo.apiv2.models.ApiException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/billing/invoices")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface InvoiceResource {

	/**
	 * @param offset
	 * @param limit
	 * @param sort
	 * @param orderBy
	 * @param filter
	 * @param request
	 * @return
	 */

	@GET
	@Operation(summary = "Return a list of invoices", tags = {
			"Invoices" }, description = "Returns a list of invoices with pagination feature or non integers will simulate API error conditions", responses = {
					@ApiResponse(headers = {
							@Header(name = "ETag", description = "a pseudo-unique identifier that represents the version of the data sent back.", schema = @Schema(type = "integer", format = "int64")) }, description = "list of invoices", content = @Content(schema = @Schema(implementation = Invoices.class))),
					@ApiResponse(responseCode = "304", description = "Lists invoices with filtering, sorting, paging."),
					@ApiResponse(responseCode = "400", description = "Invalid parameters supplied", content = @Content(schema = @Schema(implementation = ApiException.class))) })
	Response getInvoices(@Parameter(description = "The offset of the list") @DefaultValue("0") @QueryParam("offset") Long offset,
						@Parameter(description = "The limit per page") @DefaultValue("50") @QueryParam("limit") Long limit, 
						@Parameter(description = "The sort by field") @QueryParam("sort") String sort,
						@Parameter(description = "The order by") @QueryParam("orderBy") String orderBy, 
						@Parameter(description = "The filter") @QueryParam("filter") String filter, @Context Request request);

	@GET
	@Path("/{id}")
	@Operation(summary = "Return an invoice", tags = {
			"Invoices" }, description = "Returns the invoice data", responses = { @ApiResponse(headers = {
					@Header(name = "ETag", description = "a pseudo-unique identifier that represents the version of the data sent back", schema = @Schema(type = "integer", format = "int64")) }, description = "the searched invoice", content = @Content(schema = @Schema(implementation = Invoice.class))),
					@ApiResponse(responseCode = "404", description = "invoice not found", content = @Content(schema = @Schema(implementation = ApiException.class))) })
	Response getInvoice(@Parameter(description = "id of the Invoice", required = true) @PathParam("id") Long id,
			@Context Request request);

	@GET
	@Path("/{invoiceType}/{invoiceNumber}")
	@Operation(summary = "Return an invoice", tags = {
			"Invoices" }, description = "Returns the invoice data", responses = { @ApiResponse(headers = {
					@Header(name = "ETag", description = "a pseudo-unique identifier that represents the version of the data sent back", schema = @Schema(type = "integer", format = "int64")) }, description = "the searched invoice", content = @Content(schema = @Schema(implementation = Invoice.class))),
					@ApiResponse(responseCode = "404", description = "invoice not found", content = @Content(schema = @Schema(implementation = ApiException.class))) })
	Response getInvoice(
			@Parameter(description = "type of the Invoice", required = true) @PathParam("invoiceType") Long invoiceTypeId,
			@Parameter(description = "invoice number", required = true) @PathParam("invoiceNumber") String invoiceNumber,
			@Context Request request);

	@GET
	@Path("/{id}/pdf")
	@Operation(summary = "Returns the invoice PDF", tags = {
			"Invoices" }, description = "Returns the invoice pdf if exists. feberation may be forced using 'generateIfMissing' parameter", responses = {
					@ApiResponse(headers = {
							@Header(name = "ETag", description = "a pseudo-unique identifier that represents the version of the data sent back", schema = @Schema(type = "integer", format = "int64")) }, description = "the invoice pdf", content = @Content(schema = @Schema(implementation = Invoice.class))),
					@ApiResponse(responseCode = "404", description = "invoice not found", content = @Content(schema = @Schema(implementation = ApiException.class))) })
	Response fetchPdfInvoice(
			@Parameter(description = "id of the Invoice", required = true) @PathParam("id") @NotNull Long id,
			@QueryParam("generateIfMissing") Boolean generateIfMissing, @Context Request request);

	@POST
	@Path("/basicInvoices")
	@Operation(summary = "Create a new basic invoice", tags = {
			"Invoices" }, description = "Create a new advanced payment invoice", 
					responses = {
					@ApiResponse(responseCode = "200", description = "the basicInvoice successfully created, and the id is returned in the response"),
					@ApiResponse(responseCode = "400", description = "bad request when basicInvoice information contains an error") })
	Response createBasicInvoices(
			@Parameter(description = "the advanced Payment Invoice object", required = true) BasicInvoice basicInvoice);
	
	@POST
	@Path("/invoices/{id}/invoiceLines")
	@Operation(summary = "Create invoice lines",  description = "Create invoice lines", 
					responses = {
					@ApiResponse(responseCode = "200", description = "invoice lines successfully created"),
					@ApiResponse(responseCode = "403", description = "error when creating invoice lines") })
	Response addInvoiceLines(@Parameter(description = "id of the Invoice", required = true) @PathParam("id") Long id,
			@Parameter(description = "invoice lines to create", required = true)  InvoiceLinesInput invoiceLinesInput);

	/**
	 * @param id
	 * @param invoiceLinesInput
	 * @param lineId
	 * @return
	 */
	@PUT
	@Path("/invoices/{id}/invoiceLines/{lineId}")
	@Operation(summary = "Update invoice line",  description = "Update invoice line", 
	responses = {
	@ApiResponse(responseCode = "200", description = "invoice lines successfully updated"),
	@ApiResponse(responseCode = "403", description = "error when updating invoice line") })
	Response updateInvoiceLine(@Parameter(description = "id of the Invoice", required = true) @PathParam("id") Long id,
			@Parameter(description = "id of the InvoiceLine", required = true) @PathParam("lineId") Long lineId,
			@Parameter(description = "invoice lines to update", required = true)  InvoiceLineInput invoiceLineInput);
	
	/**
	 * @param id
	 * @param lineId
	 * @return
	 */
	@DELETE
	@Path("/invoices/{id}/invoiceLines/{lineId}")
	@Operation(summary = "Remove invoice line",  description = "Remove invoice line", 
	responses = {
	@ApiResponse(responseCode = "200", description = "invoice line successfully removed"),
	@ApiResponse(responseCode = "403", description = "error when removing invoice line")})
	Response removeInvoiceLine(@Parameter(description = "id of the Invoice", required = true) @PathParam("id") Long id,
			@Parameter(description = "id of the InvoiceLine", required = true) @PathParam("lineId") Long lineId,
			@Parameter(description = "invoice lines to remove", required = false)  InvoiceLinesToRemove invoiceLineToRemove);
	
	/**
	 * @param id
	 * @param invoiceLinesInput
	 * @return
	 */
	@DELETE
	@Path("/invoices/{id}/invoiceLines")
	@Operation(summary = "Remove invoice lines",  description = "Remove invoice line", 
	responses = {
	@ApiResponse(responseCode = "200", description = "invoice lines successfully removed"),
	@ApiResponse(responseCode = "403", description = "error when removing invoice lines")})
	Response removeInvoiceLines(@Parameter(description = "id of the Invoice", required = true) @PathParam("id") Long id,
			@Parameter(description = "invoice lines to remove", required = false)  InvoiceLinesToRemove invoiceLineToRemove);
	
	/**
	 * @param id
	 * @return
	 */
	@PUT
	@Path("/invoices/{id}/rebuild")
	@Operation(summary = "Rebuild invoice",  description = "Rebuild invoice", 
	responses = {
	@ApiResponse(responseCode = "200", description = "invoice successfully rebuilded"),
	@ApiResponse(responseCode = "403", description = "error when rebuilding invoice") })
	Response rebuildInvoiceLine(@Parameter(description = "id of the Invoice", required = true) @PathParam("id") Long id);
	
	/**
	 * @param id
	 * @return
	 */
	@PUT
	@Path("/invoices/{id}/rejection")
	@Operation(summary = "Reject invoice",  description = "Reject invoice", 
	responses = {
	@ApiResponse(responseCode = "200", description = "invoice successfully rejected"),
	@ApiResponse(responseCode = "403", description = "error when rejecting invoice") })
	Response rejectInvoiceLine(@Parameter(description = "id of the Invoice", required = true) @PathParam("id") Long id);
	
	
	/**
	 * @param id
	 * @return
	 */
	@PUT
	@Path("/invoices/{id}/validation")
	@Operation(summary = "Validate invoice",  description = "Validate invoice", 
	responses = {
	@ApiResponse(responseCode = "200", description = "invoice successfully validated"),
	@ApiResponse(responseCode = "403", description = "error when validating invoice") })
	Response validateInvoiceLine(@Parameter(description = "id of the Invoice", required = true) @PathParam("id") Long id);
	
	
	/**
	 * @param id
	 * @return
	 */
	@PUT
	@Path("/invoices/{id}/cancellation")
	@Operation(summary = "Cancel invoice",  description = "Cancel invoice", 
	responses = {
	@ApiResponse(responseCode = "200", description = "invoice successfully canceled"),
	@ApiResponse(responseCode = "403", description = "error when canceling invoice") })
	Response cancelInvoiceLine(@Parameter(description = "id of the Invoice", required = true) @PathParam("id") Long id);
	
	@POST
	@Path("/invoices")
	@Operation(summary = "Create a new invoice", tags = {
			"Invoices" }, description = "Create a new invoice", 
					responses = {
					@ApiResponse(responseCode = "200", description = "the Invoice is successfully created"),
					@ApiResponse(responseCode = "400", description = "bad request when Invoice information contains an error") })
	Response create( @Parameter(description = "the Invoice input object", required = true) InvoiceInput input);
	
	@PUT
	@Path("/invoices/{id}")
	@Operation(summary = "Create a new invoice", tags = {
			"Invoices" }, description = "Create a new invoice", 
					responses = {
					@ApiResponse(responseCode = "200", description = "the Invoice is successfully created"),
					@ApiResponse(responseCode = "400", description = "bad request when Invoice information contains an error") })
	Response update(@Parameter(description = "id of the Invoice", required = true) @PathParam("id") Long id, @Parameter(description = "the Invoice object", required = true) Invoice input);

}