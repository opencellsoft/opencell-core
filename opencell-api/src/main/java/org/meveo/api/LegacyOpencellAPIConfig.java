package org.meveo.api;

import org.apache.commons.collections.map.HashedMap;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationPath("/api/rest/v0")
public class LegacyOpencellAPIConfig extends Application {
    public static List<Map<String, String>> VERSION_INFO = new ArrayList<>();

    @Inject
    protected Logger log;

    @PostConstruct
    public void init() {
        loadVersionInformation();
    }

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = Stream.of(ApiLegacySwaggerGeneration.class)
                .collect(Collectors.toSet());
        log.info("Opencell OpenAPI definition is accessible in /api/rest/v0/openapi.{type:json|yaml}");

        return resources;
    }

    private void loadVersionInformation() {
        try {
            Enumeration<URL> resources = getClass().getClassLoader().getResources("version.json");
            JSONParser parser = new JSONParser();
            resources.asIterator().forEachRemaining(url -> {
                try {
                    Object obj = parser.parse(new String(url.openStream().readAllBytes()));
                    JSONObject jsonObject = (JSONObject) obj;

                    Map<String, String> versionInfo = new HashedMap();
                    versionInfo.put("name", (String) jsonObject.get("name"));
                    versionInfo.put("version", (String) jsonObject.get("version"));
                    versionInfo.put("commit", (String) jsonObject.get("commit"));
                    if(jsonObject.get("commitDate") != null) {
                        versionInfo.put("commitDate", (String) jsonObject.get("commitDate"));
                    }

                    VERSION_INFO.add(versionInfo);
                } catch (ParseException | IOException e) {
                    log.warn(e.toString());
                    log.error("error = {}", e);
                }
            });
        } catch (IOException e) {
            log.warn("There was a problem loading version information");
            log.error("error = {}", e);
        }
    }
}
