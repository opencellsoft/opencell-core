package org.meveo.apiv2.generic;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URISyntaxException;

@Path("")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface GenericResourceAPIv1 {

    @GET
    @Path("/{segments:.*}")
    Response getAllEntitiesOrGetAnEntity() throws URISyntaxException;

    @POST
    @Path("/{segments:.*}")
    Response postRequest( String postData ) throws URISyntaxException;

    @PUT
    @Path("/{segments:.*}")
    Response putRequest( String postData ) throws URISyntaxException, IOException;

    @DELETE
    @Path("/{segments:.*}")
    Response deleteAnEntity() throws URISyntaxException;

}
