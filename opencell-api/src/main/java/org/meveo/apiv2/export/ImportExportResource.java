package org.meveo.apiv2.export;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.*;

@Path("/importExport")
@Consumes({ MULTIPART_FORM_DATA, APPLICATION_JSON })
@Produces({ APPLICATION_JSON, APPLICATION_XML })
public interface ImportExportResource {

	@POST
	@Path("/exportData")
	@Operation(summary = "export data", tags = {"export data"},
			description = "export data",
			responses = {
			@ApiResponse(responseCode = "200", description = "the entity successfully created, and the id is returned in the response"),
			@ApiResponse(responseCode = "400", description = "bad request when entity information contains an error")
	})
	Response exportData(@Parameter(required = true, description = "Export configuration") ExportConfig exportConfig);

}
