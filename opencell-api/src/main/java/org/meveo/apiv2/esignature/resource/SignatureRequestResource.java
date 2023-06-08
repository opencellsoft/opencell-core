package org.meveo.apiv2.esignature.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.apiv2.admin.Seller;
import org.meveo.apiv2.esignature.SigantureRequest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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
			operationId="    POST_Seller_create",
			responses= {
					@ApiResponse(description=" action status ",
							content=@Content(
									schema=@Schema(
											implementation= Response.class
									)
							)
					)}
	)
	Response sigantureRequest(SigantureRequest sigantureRequest);
}
