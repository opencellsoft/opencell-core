package org.meveo.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.jaxrs2.integration.resources.BaseOpenApiResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * This class is used to generate Swagger documentation for Legacy endpoints of APIv0
 *
 * @author Thang Nguyen
 */
@Path("/openapi.{type:json|yaml}")
public class ApiLegacySwaggerGeneration extends BaseOpenApiResource {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @GET
    @Produces({MediaType.APPLICATION_JSON, "application/json"})
    @io.swagger.v3.oas.annotations.Operation(hidden = true)
    public Response getOpenApi(@Context HttpHeaders headers, @Context UriInfo uriInfo, @PathParam("type") String type) {

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        try {
            return Response.ok().entity(mapper.writeValueAsString(LegacyOpencellAPIConfig.oasStandardApi)).build();
        }
        catch (JsonProcessingException e) {
            log.error("JsonProcessingException {}", e.getMessage());
        }

        return null;
    }
}
