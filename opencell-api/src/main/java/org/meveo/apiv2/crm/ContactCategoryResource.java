package org.meveo.apiv2.crm;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

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
		tags = { "contact", "contact_category", "crm" },
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
		tags = { "contact", "contact_category", "crm" },
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
		tags = { "contact", "contact_category", "crm" },
		responses = {
            @ApiResponse(responseCode = "200", description = "the contactCategory successfully created"),
            @ApiResponse(responseCode = "404", description = "The contactCategoryCode does not exists"),
            @ApiResponse(responseCode = "400", description = "An error happened when trying to create a contactCategory")
		}
	)
	public Response delete(@PathParam("contactCategoryCode") String contactCategoryCode);
	
}
