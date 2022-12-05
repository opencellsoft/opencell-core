package org.meveo.apiv2.export;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.meveo.api.dto.response.utilities.ImportExportResponseDto;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import static jakarta.ws.rs.core.MediaType.*;

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

	    /**
     * Send a file to be imported. ImportExportResponseDto.executionId contains
     *
     * @param input file containing a list of object for import
     * @return As import is async process, ImportExportResponseDto.executionId contains and ID to be used to query for execution results via a call to
     *         /importExport/checkImportDataResult?id=..
     */
    @POST
    @Path("/importData")
	@Operation(
			summary=" Send a file to be imported. ImportExportResponseDto.executionId contains  ",
			description=" Send a file to be imported. ImportExportResponseDto.executionId contains  ",
			operationId="    POST_ImportExport_importData",
			responses= {
				@ApiResponse(description=" As import is async process, ImportExportResponseDto.executionId contains and ID to be used to query for execution results via a call to/importExport/checkImportDataResult?id=.. ",
						content=@Content(
									schema=@Schema(
											implementation= ImportExportResponseDto.class
											)
								)
				)}
	)
    @Consumes(MULTIPART_FORM_DATA)
    ImportExportResponseDto importData(MultipartFormDataInput input);

}
