package org.meveo.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.jaxrs2.integration.JaxrsOpenApiContextBuilder;
import io.swagger.v3.jaxrs2.integration.resources.BaseOpenApiResource;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
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

    private static OpenAPI oasStandardApi;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @GET
    @Produces({MediaType.APPLICATION_JSON, "application/json"})
    @io.swagger.v3.oas.annotations.Operation(hidden = true)
    public Response getOpenApi(@Context HttpHeaders headers, @Context UriInfo uriInfo, @PathParam("type") String type) {

        try {
            JaxrsOpenApiContextBuilder<JaxrsOpenApiContextBuilder> ctx = new JaxrsOpenApiContextBuilder<>();
            ctx.ctxId("apiv0").configLocation("/openapi-configuration-apiv0.json");
            oasStandardApi = ctx.buildContext(true).read();

            Paths newPaths = new Paths();
            for (String aKey : oasStandardApi.getPaths().keySet()) {
                if (aKey.startsWith("/api/rest")) {
                    newPaths.put(aKey, oasStandardApi.getPaths().get(aKey));
                }
                else {
                    newPaths.put("/api/rest" + aKey, oasStandardApi.getPaths().get(aKey));
                }
            }
            oasStandardApi.setPaths(newPaths);

        } catch (OpenApiConfigurationException e) {
            log.error("OpenApiConfigurationException : {}", e.getMessage());
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        try {
            return Response.ok().entity(mapper.writeValueAsString(oasStandardApi)).build();
        }
        catch (JsonProcessingException e) {
            log.error("JsonProcessingException {}", e.getMessage());
        }

        return null;
    }
}
