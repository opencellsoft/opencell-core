package org.meveo.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.jaxrs2.integration.resources.BaseOpenApiResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

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
