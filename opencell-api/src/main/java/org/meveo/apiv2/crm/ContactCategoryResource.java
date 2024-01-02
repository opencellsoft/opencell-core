package org.meveo.apiv2.crm;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/account/contactCategory")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface ContactCategoryResource {

	@POST
	@Path("")
	@Operation(
		summary = "Create a ContactCategory",
		tags = { "Contact" },
		responses = {
            @ApiResponse(responseCode = "200", description = "the contactCategory successfully created"),
            @ApiResponse(responseCode = "400", description = "An error happened when trying to create a contactCategory")
		}
	)
	public Response create(ContactCategoryDto postData);

	@PUT
	@Path("/{contactCategoryCode}")
	@Operation(
		summary = "Update a ContactCategory",
		tags = { "Contact" },
		responses = {
            @ApiResponse(responseCode = "200", description = "the contactCategory successfully created"),
            @ApiResponse(responseCode = "404", description = "The contactCategoryCode does not exists"),
            @ApiResponse(responseCode = "400", description = "An error happened when trying to create a contactCategory")
		}
	)
	public Response update(@PathParam("contactCategoryCode") String contactCategoryCode, ContactCategoryDto postData);

	@DELETE
	@Path("/{contactCategoryCode}")
	@Operation(
		summary = "Delete a ContactCategory",
		tags = { "Contact" },
		responses = {
            @ApiResponse(responseCode = "200", description = "the contactCategory successfully created"),
            @ApiResponse(responseCode = "404", description = "The contactCategoryCode does not exists"),
            @ApiResponse(responseCode = "400", description = "An error happened when trying to create a contactCategory")
		}
	)
	public Response delete(@PathParam("contactCategoryCode") String contactCategoryCode);
	
}
