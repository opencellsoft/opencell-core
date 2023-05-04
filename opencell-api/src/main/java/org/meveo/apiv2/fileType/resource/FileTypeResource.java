package org.meveo.apiv2.fileType.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.meveo.apiv2.fileType.FileTypeDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/fileType")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface FileTypeResource {
	
	@POST
	@Path("")
	@Operation(
		summary = "Create a file type",
		tags = { "fileType", "file_type"},
		responses = {
            @ApiResponse(responseCode = "200", description = "the file type successfully created"),
            @ApiResponse(responseCode = "400", description = "An error happened when trying to create a file type")
		}
	)
	public Response create(FileTypeDto postData);

	@PUT
	@Path("/{id}")
	@Operation(
		summary = "Update a file type",
		tags = { "fileType", "file_type"},
		responses = {
            @ApiResponse(responseCode = "200", description = "the file type successfully created"),
            @ApiResponse(responseCode = "404", description = "The file  type does not exists"),
            @ApiResponse(responseCode = "400", description = "An error happened when trying to create a file type")
		}
	)
	public Response update(@PathParam("id") Long id, FileTypeDto postData);

	@DELETE
	@Path("/{id}")
	@Operation(
		summary = "Delete a file type",
		tags = { "fileType", "file_type"},
		responses = {
            @ApiResponse(responseCode = "200", description = "the file type successfully created"),
            @ApiResponse(responseCode = "404", description = "The file type does not exists"),
            @ApiResponse(responseCode = "400", description = "An error happened when trying to create a file type")
		}
	)
	public Response delete(@PathParam("id") Long id);

}
