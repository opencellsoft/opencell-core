package org.meveo.apiv2;

import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import org.apache.commons.collections.map.HashedMap;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.meveo.apiv2.document.DocumentResourceImpl;
import org.meveo.apiv2.generic.GenericResourceImpl;
import org.meveo.apiv2.generic.NotYetImplementedResource;
import org.meveo.apiv2.generic.VersionImpl;
import org.meveo.apiv2.generic.core.GenericHelper;
import org.meveo.apiv2.generic.exception.*;
import org.meveo.apiv2.generic.services.GenericApiLoggingFilter;
import org.meveo.apiv2.ordering.resource.order.OrderResourceImpl;
import org.meveo.apiv2.ordering.resource.orderitem.OrderItemResourceImpl;
import org.meveo.apiv2.ordering.resource.product.ProductResourceImpl;
import org.meveo.commons.utils.ParamBeanFactory;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationPath("/api/rest/v2")
public class GenericOpencellRestful extends Application {
    private static final String GENERIC_API_REQUEST_LOGGING_CONFIG_KEY = "generic.api.request.logging";
    private static final String GENERIC_API_REQUEST_EXTRACT_LIST_CONFIG_KEY = "generic.api.extract.list";
    private static final String API_LIST_DEFAULT_LIMIT_KEY = "api.list.defaultLimit";
    private static String GENERIC_API_REQUEST_LOGGING_CONFIG;
    private static boolean GENERIC_API_REQUEST_EXTRACT_LIST;
    public static List<Map<String,String>> VERSION_INFO = new ArrayList<Map<String, String>>();
    public static Map<String,List<String>> ENTITIES_MAP = new HashMap();
    public static long API_LIST_DEFAULT_LIMIT;

    @Inject
    protected Logger log;
    @Inject
    private ParamBeanFactory paramBeanFactory;

    @PostConstruct
    public void init() {
        API_LIST_DEFAULT_LIMIT = paramBeanFactory.getInstance().getPropertyAsInteger(API_LIST_DEFAULT_LIMIT_KEY, 100);
        GENERIC_API_REQUEST_LOGGING_CONFIG = paramBeanFactory.getInstance().getProperty(GENERIC_API_REQUEST_LOGGING_CONFIG_KEY, "false");
        GENERIC_API_REQUEST_EXTRACT_LIST = Boolean.parseBoolean(paramBeanFactory.getInstance().getProperty(GENERIC_API_REQUEST_EXTRACT_LIST_CONFIG_KEY, "true"));
        loadVersionInformation();
        loadEntitiesList();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = Stream.of(VersionImpl.class, GenericResourceImpl.class, NotYetImplementedResource.class,
                NotFoundExceptionMapper.class, BadRequestExceptionMapper.class,
                MeveoExceptionMapper.class, IllegalArgumentExceptionMapper.class,
                EJBTransactionRolledbackExceptionMapper.class, OpenApiResource.class,
                DocumentResourceImpl.class, GenericJacksonProvider.class, ProductResourceImpl.class,
                OrderItemResourceImpl.class, OrderResourceImpl.class)
                .collect(Collectors.toSet());
        if(GENERIC_API_REQUEST_LOGGING_CONFIG.equalsIgnoreCase("true")){
            resources.add(GenericApiLoggingFilter.class);
            log.info("generic api requests logging is enabled, to disable logging for generic api request, put {} to false", GENERIC_API_REQUEST_LOGGING_CONFIG_KEY);
        }
        log.info("Opencell OpenAPI definition is accessible in /api/rest/v2/openapi.{type:json|yaml}");
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

                    Map<String,String> versionInfo = new HashedMap();
                    versionInfo.put("name", (String) jsonObject.get("name"));
                    versionInfo.put("version", (String) jsonObject.get("version"));
                    versionInfo.put("commit", (String) jsonObject.get("commit"));

                    VERSION_INFO.add(versionInfo);
                } catch (ParseException | IOException e) {
                    log.warn(e.toString());
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            log.warn("There was a problem loading version information");
            e.printStackTrace();
        }
    }

    private void loadEntitiesList() {
        List<String> listEntities = new ArrayList<>();
        for ( Map.Entry<String, Class> entry : GenericHelper.entitiesByName.entrySet() ) {
            listEntities.add( entry.getValue().getSimpleName() );
        }
        ENTITIES_MAP.put( "entities", listEntities );
    }

    public boolean shouldExtractList(){
        return GENERIC_API_REQUEST_EXTRACT_LIST;
    }
}
