package org.meveo.apiv2.esignature.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.apiv2.esignature.SigantureRequest;
import org.meveo.model.esignature.Operator;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/documents")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface SignatureRequestResource {
	@POST
	@Path("/signatureRequest")
	@Operation(
			summary="initiate and upload document depending on mode operator",
			description=" new version for seller.  ",
			operationId="    POST_SIGNATURE_REQUEST_steps",
			responses= {
					@ApiResponse(description=" response from yousign activation endpoint if operator yousign is used ",
							content=@Content(
									schema=@Schema(
											implementation= Response.class
									)
							)
					)}
	)
	Response sigantureRequest(SigantureRequest sigantureRequest);
	
	@GET
	@Path("/{operator}/signatureRequest/{signatureRequestId}")
	@Operation(
			summary="fetch a signature request ",
			description=" get data from signature request id ",
			operationId="    POST_SIGNATURE_REQUEST_fetch",
			responses= {
					@ApiResponse(description=" response from operator used  ",
							content=@Content(
									schema=@Schema(
											implementation= Response.class
									)
							)
					)}
	)
	Response fetchSignatureRequest(@PathParam("operator") Operator operator, @PathParam("signatureRequestId") String signatureRequestId);
	
	
	@GET
	@Path("{operator}/signatureRequest/{signatureRequestId}/documents/download")
	Response download(@PathParam("operator") Operator operator, @PathParam("signatureRequestId") String signatureRequestId);
	
}