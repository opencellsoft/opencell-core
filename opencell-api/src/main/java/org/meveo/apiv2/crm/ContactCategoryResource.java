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

@Path("/account/contactCategory")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface ContactCategoryResource {

	@POST
	@Path("")
	public Response create(ContactCategoryDto postData);

	@PUT
	@Path("/{contactCategoryCode}")
	public Response update(@PathParam("contactCategoryCode") String contactCategoryCode, ContactCategoryDto postData);

	@DELETE
	@Path("/{contactCategoryCode}")
	public Response delete(@PathParam("contactCategoryCode") String contactCategoryCode);
	
}
