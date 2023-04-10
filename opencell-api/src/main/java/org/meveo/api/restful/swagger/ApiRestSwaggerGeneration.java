package org.meveo.api.restful.swagger;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.meveo.api.restful.constant.MapRestUrlAndStandardUrl;
import org.meveo.api.restful.swagger.service.Apiv1SwaggerDeleteOperation;
import org.meveo.api.restful.swagger.service.Apiv1SwaggerGetOperation;
import org.meveo.api.restful.swagger.service.Apiv1SwaggerPostOperation;
import org.meveo.api.restful.swagger.service.Apiv1SwaggerPutOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import io.swagger.v3.jaxrs2.integration.JaxrsAnnotationScanner;
import io.swagger.v3.jaxrs2.integration.resources.BaseOpenApiResource;
import io.swagger.v3.oas.integration.GenericOpenApiContext;
import io.swagger.v3.oas.integration.GenericOpenApiContextBuilder;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.integration.OpenApiContextLocator;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.integration.api.OpenApiContext;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;

/**
 * This class is used to generate Swagger documentation for RESTful endpoints of APIv1
 *
 * @author Thang Nguyen
 */
@Path("/openapi.{type:json|yaml}")
public class ApiRestSwaggerGeneration extends BaseOpenApiResource {

    @Inject
    private Apiv1SwaggerPostOperation postOperation;

    @Inject
    private Apiv1SwaggerGetOperation getOperation;

    @Inject
    private Apiv1SwaggerPutOperation putOperation;

    @Inject
    private Apiv1SwaggerDeleteOperation deleteOperation;

    private static String oasRestApiTxt;

    @GET
    @Produces({ MediaType.APPLICATION_JSON, "application/json" })
    @io.swagger.v3.oas.annotations.Operation(hidden = true)
    public Response getOpenApi(@Context HttpHeaders headers, @Context UriInfo uriInfo, @PathParam("type") String type) {

        // On first call populate the data
        if (oasRestApiTxt == null) {
            loadOpenAPI();
        }

        return Response.ok().entity(oasRestApiTxt).build();
    }

    /**
     * Load Open API definition for the swagger
     */
    private void loadOpenAPI() {
        try {

            // Get standard API endpoints in Opencell and populate MAP_SWAGGER_PATHS
            OpenAPI oasStandardApi = new OpenAPI();
            SwaggerConfiguration oasStandardConfig = new SwaggerConfiguration().openAPI(oasStandardApi).readAllResources(false)
                // scanner implementation only considering defined resourcePackages and classes
                // and ignoring resource packages and classes defined in JAX-RS Application
                .scannerClass(JaxrsAnnotationScanner.class.getName()).resourcePackages(new HashSet<>(Arrays.asList("org.meveo.api.rest")));

            OpenApiContext ctx = OpenApiContextLocator.getInstance().getOpenApiContext(OpenApiContext.OPENAPI_CONTEXT_ID_DEFAULT);
            if (ctx instanceof GenericOpenApiContext) {
                ((GenericOpenApiContext) ctx).getOpenApiScanner().setConfiguration(oasStandardConfig);
                oasStandardApi = ctx.read();
            }

//      Map<String, PathItem> MAP_SWAGGER_PATHS = new LinkedHashMap<>(GenericOpencellRestfulAPIv1.API_STD_SWAGGER.getPaths());
            String jsonApiStd = new Gson().toJson(oasStandardApi.getPaths());
            Map<String, PathItem> MAP_SWAGGER_PATHS = new Gson().fromJson(jsonApiStd, new TypeToken<Map<String, PathItem>>() {
            }.getType());

            OpenAPI oasRestApi = new OpenAPI().info(new Info().title("Opencell OpenApi definition V1").version("1.0").description("This Swagger documentation contains API v1 endpoints")
                .termsOfService("http://opencell.com/terms/").contact(new Contact().email("opencell@opencellsoft.com")).license(new License().name("Apache 2.0").url("http://www.apache.org/licenses/LICENSE-2.0.html")));

            SwaggerConfiguration oasRestConfig = new SwaggerConfiguration().openAPI(oasRestApi).prettyPrint(true).readAllResources(false);

            setOpenApiConfiguration(oasRestConfig);

            try {
                GenericOpenApiContextBuilder ctxBuilder = new GenericOpenApiContextBuilder();
                ctxBuilder.setCtxId("Apiv1-rest-id");
                oasRestApi = ctxBuilder.openApiConfiguration(oasRestConfig).buildContext(true).read();
            } catch (OpenApiConfigurationException e) {
                throw new RuntimeException(e.getMessage(), e);
            }

            // Populate paths of oasRestApi
            Paths paths = new Paths();
            oasRestApi.setPaths(paths);

            // Populate tags, components, extensions and servers of oasRestApi
            oasRestApi.components(oasStandardApi.getComponents()).servers(Collections.singletonList(new Server().url("/opencell").description("Root path")))
                .security(Collections.singletonList(new SecurityRequirement().addList("auth")));

            for (Map.Entry<String, String> mapPathEntry : MapRestUrlAndStandardUrl.MAP_RESTFUL_URL_AND_STANDARD_URL.entrySet()) {
                String aStdPath = mapPathEntry.getKey();
                String aRFPath = mapPathEntry.getValue();
                PathItem pathItem = oasRestApi.getPaths().containsKey(aRFPath) ? oasRestApi.getPaths().get(aRFPath) : new PathItem();

                for (Map.Entry<String, PathItem> mapSwaggerEntry : MAP_SWAGGER_PATHS.entrySet()) {
                    String anOldPath = mapSwaggerEntry.getKey();
                    PathItem pathItemInOldSwagger = mapSwaggerEntry.getValue();
                    String[] splitStdPath = aStdPath.split(MapRestUrlAndStandardUrl.SEPARATOR);
                    if (splitStdPath[1].equals(anOldPath)) {

                        // set operations for pathItem (a pathItem can have many operations CRUD)
                        switch (splitStdPath[0]) {
                        case MapRestUrlAndStandardUrl.GET:
                            if (pathItemInOldSwagger.getGet() != null)
                                getOperation.setGet(pathItem, pathItemInOldSwagger.getGet(), aRFPath);
                            break;
                        case MapRestUrlAndStandardUrl.POST:
                            if (pathItemInOldSwagger.getPost() != null)
                                postOperation.setPost(pathItem, pathItemInOldSwagger.getPost(), aRFPath);
                            break;
                        case MapRestUrlAndStandardUrl.PUT:
                            if (pathItemInOldSwagger.getPut() != null)
                                putOperation.setPut(pathItem, pathItemInOldSwagger.getPut(), aRFPath);
                            break;
                        case MapRestUrlAndStandardUrl.DELETE:
                            if (pathItemInOldSwagger.getDelete() != null)
                                deleteOperation.setDelete(pathItem, pathItemInOldSwagger.getDelete(), aRFPath);
                            break;
                        }
                        break;
                    }
                }

                paths.addPathItem(aRFPath, pathItem);
            }

            oasRestApi.setPaths(paths);

            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            oasRestApiTxt = mapper.writeValueAsString(oasRestApi);

        } catch (JsonProcessingException e) {
            Logger log = LoggerFactory.getLogger(this.getClass());
            log.error("Failed to create a Swagger documentation file", e);
        }
    }
}