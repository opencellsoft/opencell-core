package org.meveo.api.restful;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Parameter;
import org.meveo.api.restful.pagingFiltering.PagingAndFilteringRest;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.net.URISyntaxException;

@Path("")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface GenericResourceAPIv1 {

    @GET
    @Path("/{segments:.*}")
    Response getRequest(PagingAndFilteringRest pagingAndFiltering) throws URISyntaxException, IOException;

    @POST
    @Path("/{segments:.*}")
    Response postRequest( String postData ) throws URISyntaxException, JsonProcessingException, IOException;

    @PUT
    @Path("/{segments:.*}")
    Response putRequest( String postData ) throws URISyntaxException, IOException;

    @DELETE
    @Path("/{segments:.*}")
    Response deleteRequest() throws URISyntaxException, IOException;

    @POST
    @Path("/{segments:.*}/creationOrUpdate")
    Response postCreationOrUpdate( String postData ) throws JsonProcessingException, URISyntaxException;

    @GET
    @Path("/restEndpoints")
    Response getListRestEndpoints();

    @GET
    @Path("/restEndpoints/{entityName}")
    Response getListRestEndpointsForEntity(@Parameter(description = "the entity name", required = true) @PathParam("entityName") String entityName);

    @GET
    @Path("/version")
    Response getApiVersion();

}
