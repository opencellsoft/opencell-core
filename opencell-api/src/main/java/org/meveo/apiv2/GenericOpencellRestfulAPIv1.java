package org.meveo.apiv2;

import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import org.apache.commons.collections.map.HashedMap;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.meveo.api.rest.IBaseRs;
import org.meveo.apiv2.document.DocumentResourceImpl;
import org.meveo.apiv2.generic.GenericResourceAPIv1Impl;
import org.meveo.apiv2.generic.NotYetImplementedResource;
import org.meveo.apiv2.generic.VersionImpl;
import org.meveo.apiv2.generic.exception.*;
import org.meveo.apiv2.generic.services.GenericApiLoggingFilter;
import org.meveo.apiv2.ordering.resource.order.OrderResourceImpl;
import org.meveo.apiv2.ordering.resource.orderitem.OrderItemResourceImpl;
import org.meveo.apiv2.ordering.resource.product.ProductResourceImpl;
import org.meveo.commons.utils.ParamBeanFactory;
import org.reflections.Reflections;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationPath(GenericOpencellRestfulAPIv1.API_VERSION)
public class GenericOpencellRestfulAPIv1 extends Application {
    public static List<Map<String,String>> VERSION_INFO = new ArrayList<Map<String, String>>();
    public static Map<String,String> MAP_NEW_PATH_AND_PATH_IBASE_RS = new HashMap<>();
    public static long API_LIST_DEFAULT_LIMIT;
    public static final String API_VERSION = "/v1";

    private static final String GENERIC_API_REQUEST_LOGGING_CONFIG_KEY = "generic.api.request.logging";
    private static final String API_LIST_DEFAULT_LIMIT_KEY = "api.list.defaultLimit";
    private static String GENERIC_API_REQUEST_LOGGING_CONFIG;
    private static final String PATH_TO_ALL_ENTITY_RS = "org.meveo.api.rest";

    // business logic final string variables
    private static final String ACCOUNT_MANAGEMENT = "/accountManagement";
    private static final String CATALOG = "/catalog";
    private static final String BILLING = "/billing";

    // final useful strings
    private static final String FORWARD_SLASH = "/";

    @Inject
    protected Logger log;
    @Inject
    private ParamBeanFactory paramBeanFactory;

    @PostConstruct
    public void init() {
        API_LIST_DEFAULT_LIMIT = paramBeanFactory.getInstance().getPropertyAsInteger(API_LIST_DEFAULT_LIMIT_KEY, 100);
        GENERIC_API_REQUEST_LOGGING_CONFIG = paramBeanFactory.getInstance().getProperty(GENERIC_API_REQUEST_LOGGING_CONFIG_KEY, "false");
        loadVersionInformation();
        loadMapPathAndInterfaceIBaseRs();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = Stream.of(VersionImpl.class, GenericResourceAPIv1Impl.class, NotYetImplementedResource.class,
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

    private void loadMapPathAndInterfaceIBaseRs() {
        Reflections reflections = new Reflections( PATH_TO_ALL_ENTITY_RS );
        Set<Class<? extends IBaseRs>> classes = reflections.getSubTypesOf(IBaseRs.class);

        for ( Class<? extends IBaseRs> aClass : classes ) {
            if ( aClass.isInterface() ) {
                Annotation[] arrAnnotations = aClass.getAnnotations();
                for ( Annotation anAnnotation : arrAnnotations ) {
                    if ( anAnnotation instanceof Path) {
                        if ( ((Path) anAnnotation).value().equals( "/seller" ) ) {
                            MAP_NEW_PATH_AND_PATH_IBASE_RS.put( API_VERSION + ACCOUNT_MANAGEMENT + ((Path) anAnnotation).value() + "s",
                                    ((Path) anAnnotation).value() );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/account/customer" ) ) {
                            MAP_NEW_PATH_AND_PATH_IBASE_RS.put( API_VERSION + ACCOUNT_MANAGEMENT + "/customers",
                                    ((Path) anAnnotation).value() );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/account/customerAccount" ) ) {
                            MAP_NEW_PATH_AND_PATH_IBASE_RS.put( API_VERSION + ACCOUNT_MANAGEMENT + "/customerAccounts",
                                    ((Path) anAnnotation).value() );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/account/billingAccount" ) ) {
                            MAP_NEW_PATH_AND_PATH_IBASE_RS.put( API_VERSION + ACCOUNT_MANAGEMENT + "/billingAccounts",
                                    ((Path) anAnnotation).value() );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/account/userAccount" ) ) {
                            MAP_NEW_PATH_AND_PATH_IBASE_RS.put( API_VERSION + ACCOUNT_MANAGEMENT + "/userAccounts",
                                    ((Path) anAnnotation).value() );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/account/accountHierarchy" ) ) {
                            MAP_NEW_PATH_AND_PATH_IBASE_RS.put( API_VERSION + ACCOUNT_MANAGEMENT + "/accountHierarchies",
                                    ((Path) anAnnotation).value() );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/billing/subscription" ) ) {
                            MAP_NEW_PATH_AND_PATH_IBASE_RS.put( API_VERSION + ACCOUNT_MANAGEMENT + "/subscriptions",
                                    ((Path) anAnnotation).value() );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/account/access" ) ) {
                            MAP_NEW_PATH_AND_PATH_IBASE_RS.put( API_VERSION + ACCOUNT_MANAGEMENT + "/accesses",
                                    ((Path) anAnnotation).value() );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/billing/ratedTransaction" ) ) {
                            MAP_NEW_PATH_AND_PATH_IBASE_RS.put( API_VERSION + BILLING + "/ratedTransactions",
                                    ((Path) anAnnotation).value() );
                            MAP_NEW_PATH_AND_PATH_IBASE_RS.put( API_VERSION + BILLING + "/ratedTransactions/cancelRatedTransactions",
                                    ((Path) anAnnotation).value() + "/cancelRatedTransactions" );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/billing/wallet" ) ) {
                            MAP_NEW_PATH_AND_PATH_IBASE_RS.put( API_VERSION + BILLING + "/wallets",
                                    ((Path) anAnnotation).value() );
                            MAP_NEW_PATH_AND_PATH_IBASE_RS.put( API_VERSION + BILLING + "/wallets/operation",
                                    ((Path) anAnnotation).value() + "/operation" );
                        }
//                        else if ( ((Path) anAnnotation).value().equals( "/catalog/offerTemplate" ) ) {
//                            MAP_NEW_PATH_AND_PATH_IBASE_RS.put( API_VERSION + CATALOG + "/offerTemplates",
//                                    ((Path) anAnnotation).value() );
//
//                            // Processing for enable and disable an offerTemplate
//                            String aCode = "^.{1,100}$";
//                System.out.println( "XAY DUNG MAP DAY NE : " + API_VERSION + CATALOG + "/offerTemplates"
//                        + FORWARD_SLASH + aCode + FORWARD_SLASH + "enable" );
//                            MAP_NEW_PATH_AND_PATH_IBASE_RS.put( API_VERSION + CATALOG + "/offerTemplates"
//                                            + FORWARD_SLASH + aCode + FORWARD_SLASH + "enable" ,
//                                    ((Path) anAnnotation).value() + FORWARD_SLASH + aCode + FORWARD_SLASH + "enable" );
//                            MAP_NEW_PATH_AND_PATH_IBASE_RS.put( API_VERSION + CATALOG + "/offerTemplates"
//                                            + FORWARD_SLASH + aCode + FORWARD_SLASH + "disable" ,
//                                    ((Path) anAnnotation).value() + FORWARD_SLASH + aCode + FORWARD_SLASH + "disable" );
//                        }
                        else {
                            MAP_NEW_PATH_AND_PATH_IBASE_RS.put( API_VERSION + ((Path) anAnnotation).value() + "s",
                                    ((Path) anAnnotation).value() );
                        }
                    }
                }
            }
        }
    }
}
