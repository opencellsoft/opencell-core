package org.meveo.apiv2;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.jaxrs2.integration.JaxrsOpenApiContextBuilder;
import io.swagger.v3.jaxrs2.integration.resources.BaseOpenApiResource;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.integration.api.OpenApiContext;
import io.swagger.v3.oas.models.OpenAPI;

/**
 * This class is used to generate Swagger documentation for endpoints of APIv2
 *
 * @author Thang Nguyen
 */
@Path("/openapi.{type:json|yaml}")
public class Apiv2SwaggerGeneration extends BaseOpenApiResource {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static String openAPIv2Txt;

    @GET
    @Produces({ MediaType.APPLICATION_JSON, "application/json" })
    @io.swagger.v3.oas.annotations.Operation(hidden = true)
    public Response getOpenApi(@Context HttpHeaders headers, @Context UriInfo uriInfo, @PathParam("type") String type) {

        if (openAPIv2Txt == null) {
            loadOpenAPI();
        }
        return Response.ok().entity(openAPIv2Txt).build();

    }

    /**
     * Load Open API definition for the swagger
     */
    private void loadOpenAPI() {
        try {
            OpenApiContext ctx = new JaxrsOpenApiContextBuilder<>().ctxId("apiv2").configLocation("/openapi-configuration-apiv2.json").buildContext(true);

            OpenAPI openAPIv2 = ctx.read();

            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            openAPIv2Txt = mapper.writeValueAsString(openAPIv2);

        } catch (OpenApiConfigurationException e) {
            log.error("Failed to create a Swagger documentation file", e);
        } catch (JsonProcessingException e) {
            log.error("Failed to create a Swagger documentation file", e);
        }
    }
}