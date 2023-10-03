package org.meveo.apiv2.billing.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.apiv2.billing.EinvoiceSetting;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("electronicInvoicing/generalSettings")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface EinvoiceResource {
	
	@POST
	@Operation(summary = "update e-invoicing settings", tags = {
			"Invoices" }, description = "only one setting is set per instance e-invoicing setting", responses = {
			@ApiResponse(responseCode = "204", description = "the e-invoice is updated"),
			@ApiResponse(responseCode = "200", description = "create a new e-invoicing setting if not exist"),
			@ApiResponse(responseCode = "400", description = "The job doesn't exist for InvoicingJob/XMLGenerationJob/PDFGenerationJob/UBLGenerationJob",
							content = @Content(schema = @Schema(implementation = EntityDoesNotExistsException.class))) })
	Response updateEinvoiceSettings(EinvoiceSetting einvoiceSetting);
}
