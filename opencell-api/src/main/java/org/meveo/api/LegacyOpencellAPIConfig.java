package org.meveo.api;

import io.swagger.v3.jaxrs2.integration.JaxrsOpenApiContextBuilder;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.integration.api.OpenApiContext;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationPath("/api/rest/v0")
public class LegacyOpencellAPIConfig extends Application {

    @Inject
    protected Logger log;

    public static OpenAPI oasStandardApi;

    @PostConstruct
    public void init() {
        loadOpenAPI();
    }

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = Stream.of(ApiLegacySwaggerGeneration.class)
                .collect(Collectors.toSet());
        log.info("Opencell OpenAPI definition is accessible in /api/rest/v0/openapi.{type:json|yaml}");

        return resources;
    }

    private void loadOpenAPI() {
        try {
            OpenApiContext ctx = new JaxrsOpenApiContextBuilder<>()
                    .ctxId("apiv0")
                    .configLocation("/openapi-configuration-apiv0.json")
                    .buildContext(true);
            oasStandardApi = ctx.read();

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
            oasStandardApi.setSecurity(null);
        } catch (OpenApiConfigurationException e) {
            log.error("OpenApiConfigurationException : {}", e.getMessage());
        }
    }
}
