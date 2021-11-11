package org.meveo.api.restful.swagger;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.swagger.v3.jaxrs2.integration.resources.BaseOpenApiResource;
import io.swagger.v3.oas.integration.GenericOpenApiContextBuilder;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.meveo.api.restful.GenericOpencellRestfulAPIv1;
import org.meveo.api.restful.constant.MapRestUrlAndStandardUrl;
import org.meveo.api.restful.swagger.service.Apiv1SwaggerDeleteOperation;
import org.meveo.api.restful.swagger.service.Apiv1SwaggerGetOperation;
import org.meveo.api.restful.swagger.service.Apiv1SwaggerPostOperation;
import org.meveo.api.restful.swagger.service.Apiv1SwaggerPutOperation;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.util.Collections;
import java.util.Map;

/**
 * This class is used to generate Swagger documentation for RESTful endpoints of APIv1
 *
 * @author Thang Nguyen
 */
@Path("/openapiv1.{type:json|yaml}")
public class ApiRestSwaggerGeneration extends BaseOpenApiResource {

    @Inject
    private Apiv1SwaggerPostOperation postOperation;

    @Inject
    private Apiv1SwaggerGetOperation getOperation;

    @Inject
    private Apiv1SwaggerPutOperation putOperation;

    @Inject
    private Apiv1SwaggerDeleteOperation deleteOperation;

    private static final String FORWARD_SLASH = "/";

    @GET
    @Produces({MediaType.APPLICATION_JSON, "application/json"})
    @io.swagger.v3.oas.annotations.Operation(hidden = true)
    public Response getOpenApi(@Context HttpHeaders headers, @Context UriInfo uriInfo, @PathParam("type") String type) {

//        Map<String, PathItem> MAP_SWAGGER_PATHS = new LinkedHashMap<>(GenericOpencellRestfulAPIv1.API_STD_SWAGGER.getPaths());
        String jsonApiStd = new Gson().toJson(GenericOpencellRestfulAPIv1.API_STD_SWAGGER.getPaths());
        Map<String, PathItem> MAP_SWAGGER_PATHS = new Gson().fromJson(jsonApiStd, new TypeToken<Map<String, PathItem>>(){}.getType());


        OpenAPI oasRestApi = new OpenAPI().info(new Info().title("Opencell RESTful APIv1"));
        SwaggerConfiguration oasRestConfig = new SwaggerConfiguration()
                .openAPI(oasRestApi)
                .prettyPrint(true)
                .readAllResources(false);

        setOpenApiConfiguration(oasRestConfig);

        try {
            GenericOpenApiContextBuilder ctxBuilder = new GenericOpenApiContextBuilder();
            ctxBuilder.setCtxId( "Apiv1-rest-id" );
            oasRestApi = ctxBuilder.openApiConfiguration(oasRestConfig).buildContext(true).read();
        } catch (OpenApiConfigurationException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        // Populate paths of oasRestApi
        Paths paths = new Paths();
        oasRestApi.setPaths(paths);

        // Populate tags, components, extensions and servers of oasRestApi
//        oasRestApi.setTags(GenericOpencellRestfulAPIv1.API_STD_SWAGGER.getTags());
        oasRestApi.setComponents(GenericOpencellRestfulAPIv1.API_STD_SWAGGER.getComponents());
        oasRestApi.setServers(Collections.singletonList(new Server().url("/opencell")));

        for ( Map.Entry<String, String> mapPathEntry : MapRestUrlAndStandardUrl.MAP_RESTFUL_URL_AND_STANDARD_URL.entrySet() ) {
            String aStdPath = mapPathEntry.getKey();
            String aRFPath = mapPathEntry.getValue();
            PathItem pathItem = oasRestApi.getPaths().containsKey(aRFPath) ? oasRestApi.getPaths().get(aRFPath) : new PathItem();

            for (Map.Entry<String, PathItem> mapSwaggerEntry : MAP_SWAGGER_PATHS.entrySet()) {
                String anOldPath = mapSwaggerEntry.getKey();
                PathItem pathItemInOldSwagger = mapSwaggerEntry.getValue();
                String[] splitStdPath = aStdPath.split(MapRestUrlAndStandardUrl.SEPARATOR);
                if ( splitStdPath[1].equals( anOldPath ) ) {

                    // set operations for pathItem (a pathItem can have many operations CRUD)
                    switch (splitStdPath[0]) {
                        case MapRestUrlAndStandardUrl.GET :
                            if ( pathItemInOldSwagger.getGet() != null )
                                getOperation.setGet(pathItem, pathItemInOldSwagger.getGet(), aRFPath);
                            break;
                        case MapRestUrlAndStandardUrl.POST :
                            if ( pathItemInOldSwagger.getPost() != null )
                                postOperation.setPost(pathItem, pathItemInOldSwagger.getPost(), aRFPath);
                            break;
                        case MapRestUrlAndStandardUrl.PUT :
                            if ( pathItemInOldSwagger.getPut() != null )
                                putOperation.setPut(pathItem, pathItemInOldSwagger.getPut(), aRFPath);
                            break;
                        case MapRestUrlAndStandardUrl.DELETE :
                            if ( pathItemInOldSwagger.getDelete() != null )
                                deleteOperation.setDelete(pathItem, pathItemInOldSwagger.getDelete(), aRFPath);
                            break;
                    }
                    break;
                }
            }

            paths.addPathItem(aRFPath, pathItem);
        }

        oasRestApi.setPaths(paths);

        return Response.ok().entity(oasRestApi).build();
    }
}
