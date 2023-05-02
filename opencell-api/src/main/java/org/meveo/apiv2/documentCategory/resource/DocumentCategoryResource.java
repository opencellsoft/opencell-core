package org.meveo.apiv2.documentCategory.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.meveo.apiv2.documentCategory.DocumentCategoryDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/documentCategory")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface DocumentCategoryResource {
	
	@POST
	@Path("")
	@Operation(
		summary = "Create a document category",
		tags = { "documentCategory", "file_type"},
		responses = {
            @ApiResponse(responseCode = "200", description = "the document category successfully created"),
            @ApiResponse(responseCode = "400", description = "An error happened when trying to create a document category")
		}
	)
	public Response create(DocumentCategoryDto postData);

	@PUT
	@Path("/{id}")
	@Operation(
		summary = "Update a document category",
		tags = { "documentCategory", "file_type"},
		responses = {
            @ApiResponse(responseCode = "200", description = "the document category successfully created"),
            @ApiResponse(responseCode = "404", description = "The file  type does not exists"),
            @ApiResponse(responseCode = "400", description = "An error happened when trying to create a document category")
		}
	)
	public Response update(@PathParam("id") Long id, DocumentCategoryDto postData);

	@DELETE
	@Path("/{id}")
	@Operation(
		summary = "Delete a document category",
		tags = { "documentCategory", "file_type"},
		responses = {
            @ApiResponse(responseCode = "200", description = "the document category successfully created"),
            @ApiResponse(responseCode = "404", description = "The document category does not exists"),
            @ApiResponse(responseCode = "400", description = "An error happened when trying to create a document category")
		}
	)
	public Response delete(@PathParam("id") Long id);


}
