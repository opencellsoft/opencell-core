package org.meveo.api.restful;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Parameter;
import org.meveo.api.restful.pagingFiltering.PagingAndFilteringRest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
