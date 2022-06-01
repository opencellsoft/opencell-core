package org.meveo.api;

import org.slf4j.Logger;

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

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = Stream.of(ApiLegacySwaggerGeneration.class)
                .collect(Collectors.toSet());
        log.info("Opencell OpenAPI definition is accessible in /api/rest/v0/openapi.{type:json|yaml}");

        return resources;
    }
}
