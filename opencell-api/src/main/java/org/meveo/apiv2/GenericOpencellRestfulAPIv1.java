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
import org.meveo.apiv2.generic.RegExHashMap;
import org.meveo.apiv2.generic.VersionImpl;
import org.meveo.apiv2.generic.exception.*;
import org.meveo.apiv2.generic.services.GenericApiLoggingFilter;
import org.meveo.apiv2.ordering.resource.order.OrderResourceImpl;
import org.meveo.apiv2.ordering.resource.orderitem.OrderItemResourceImpl;
import org.meveo.apiv2.ordering.resource.product.ProductResourceImpl;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.util.Inflector;
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
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class is used to construct the HashMap for URL Redirection at deployment
 *
 * @author Thang Nguyen
 */

@ApplicationPath(GenericOpencellRestfulAPIv1.API_VERSION)
public class GenericOpencellRestfulAPIv1 extends Application {
    public static List<Map<String,String>> VERSION_INFO = new ArrayList<Map<String, String>>();
    public static Map<Object,String> MAP_NEW_PATH_AND_IBASE_RS_PATH = new HashMap<>();
    public static RegExHashMap<Object,String> MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH = new RegExHashMap<>();
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
    private static final String ENABLE_SERVICE = "/enable";
    private static final String DISABLE_SERVICE = "/disable";

    // final useful strings
    private static final String FORWARD_SLASH = "/";
    public static final String CODE_REGEX = "[^\\/^$]+";

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
            log.info("api requests logging is enabled, to disable logging for api request, put {} to false", GENERIC_API_REQUEST_LOGGING_CONFIG_KEY);
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
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + ACCOUNT_MANAGEMENT + ((Path) anAnnotation).value() + "s",
                                    ((Path) anAnnotation).value() );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/account/customer" ) ) {
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + ACCOUNT_MANAGEMENT + "/customers",
                                    ((Path) anAnnotation).value() );

                            // Processing for requests related to customerCategory
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + ACCOUNT_MANAGEMENT + "/customers/categories",
                                    ((Path) anAnnotation).value() + "/category" );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/account/customerAccount" ) ) {
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + ACCOUNT_MANAGEMENT + "/customerAccounts",
                                    ((Path) anAnnotation).value() );

                            // Processing for request get list of customerAccounts based on a customerCode
                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( "\\/v1\\/accountManagement\\/customers\\/" + CODE_REGEX + "\\/customerAccounts" ) ,
                                    ((Path) anAnnotation).value() + "/list" );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/account/billingAccount" ) ) {
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + ACCOUNT_MANAGEMENT + "/billingAccounts",
                                    ((Path) anAnnotation).value() );

                            // Processing for request get list of billingAccounts based on a customerAccountCode
                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( "\\/v1\\/accountManagement\\/customerAccounts\\/" + CODE_REGEX + "\\/billingAccounts" ) ,
                                    ((Path) anAnnotation).value() + "/list" );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/account/userAccount" ) ) {
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + ACCOUNT_MANAGEMENT + "/userAccounts",
                                    ((Path) anAnnotation).value() );

                            // Processing for request get list of userAccounts based on a billingAccountCode
                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( "\\/v1\\/accountManagement\\/billingAccounts\\/" + CODE_REGEX + "\\/userAccounts" ) ,
                                    ((Path) anAnnotation).value() + "/list" );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/account/accountHierarchy" ) ) {
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + ACCOUNT_MANAGEMENT + "/accountHierarchies",
                                    ((Path) anAnnotation).value() );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/billing/subscription" ) ) {
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + ACCOUNT_MANAGEMENT + "/subscriptions",
                                    ((Path) anAnnotation).value() );

                            // Processing for different services of subscription: activation, suspension, termination, update of existing services
                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( "\\/v1\\/accountManagement\\/subscriptions\\/" + CODE_REGEX + "\\/activation" ) ,
                                    ((Path) anAnnotation).value() + "/activate" );

                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( "\\/v1\\/accountManagement\\/subscriptions\\/" + CODE_REGEX + "\\/suspension" ) ,
                                    ((Path) anAnnotation).value() + "/suspend" );

                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( "\\/v1\\/accountManagement\\/subscriptions\\/" + CODE_REGEX + "\\/termination" ) ,
                                    ((Path) anAnnotation).value() + "/terminate" );

                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( "\\/v1\\/accountManagement\\/subscriptions\\/" + CODE_REGEX + "\\/services" ) ,
                                    ((Path) anAnnotation).value() + "/updateServices" );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/account/access" ) ) {
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + ACCOUNT_MANAGEMENT + "/accesses",
                                    ((Path) anAnnotation).value() );

                            // Processing for request get list of accesses based on a subscriptionCode
                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( "\\/v1\\/accountManagement\\/subscriptions\\/" + CODE_REGEX + "\\/accesses" ) ,
                                    ((Path) anAnnotation).value() + "/list" );

                            // Processing for request get an accessPoint based on a subscriptionCode and an accessCode
                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( "\\/v1\\/accountManagement\\/subscriptions\\/" + CODE_REGEX + "\\/accesses\\/" + CODE_REGEX ) ,
                                    ((Path) anAnnotation).value() );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/billing/ratedTransaction" ) ) {
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + BILLING + "/ratedTransactions",
                                    ((Path) anAnnotation).value() );
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + BILLING + "/ratedTransactions/cancellation",
                                    ((Path) anAnnotation).value() + "/cancelRatedTransactions" );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/billing/wallet" ) ) {
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + BILLING + "/wallets",
                                    ((Path) anAnnotation).value() );
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + BILLING + "/wallets/operation",
                                    ((Path) anAnnotation).value() + "/operation" );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/catalog/offerTemplate" ) ) {
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + CATALOG + "/offerTemplates",
                                    ((Path) anAnnotation).value() );

                            // Processing for enable and disable an offerTemplate
                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( "\\/v1\\/catalog\\/offerTemplates\\/" + CODE_REGEX + ENABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( "\\/v1\\/catalog\\/offerTemplates\\/" + CODE_REGEX + DISABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/catalog/oneShotChargeTemplate" ) ) {
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + CATALOG + "/oneShotChargeTemplates",
                                    ((Path) anAnnotation).value() );

                            // Processing for enable and disable an oneShotChargeTemplate
                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( "\\/v1\\/catalog\\/oneShotChargeTemplates\\/" + CODE_REGEX + ENABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( "\\/v1\\/catalog\\/oneShotChargeTemplates\\/" + CODE_REGEX + DISABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/catalog/recurringChargeTemplate" ) ) {
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + CATALOG + "/recurringChargeTemplates",
                                    ((Path) anAnnotation).value() );

                            // Processing for enable and disable a recurringChargeTemplate
                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( "\\/v1\\/catalog\\/recurringChargeTemplates\\/" + CODE_REGEX + ENABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( "\\/v1\\/catalog\\/recurringChargeTemplates\\/" + CODE_REGEX + DISABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/catalog/usageChargeTemplate" ) ) {
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + CATALOG + "/usageChargeTemplates",
                                    ((Path) anAnnotation).value() );

                            // Processing for enable and disable a usageChargeTemplate
                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( "\\/v1\\/catalog\\/usageChargeTemplates\\/" + CODE_REGEX + ENABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( "\\/v1\\/catalog\\/usageChargeTemplates\\/" + CODE_REGEX + DISABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/catalog/serviceTemplate" ) ) {
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + CATALOG + "/serviceTemplates",
                                    ((Path) anAnnotation).value() );

                            // Processing for enable and disable a serviceTemplate
                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( "\\/v1\\/catalog\\/serviceTemplates\\/" + CODE_REGEX + ENABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( "\\/v1\\/catalog\\/serviceTemplates\\/" + CODE_REGEX + DISABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/catalog/pricePlan" ) ) {
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + CATALOG + "/pricePlans",
                                    ((Path) anAnnotation).value() );

                            // Processing for enable and disable a pricePlan
                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( "\\/v1\\/catalog\\/pricePlans\\/" + CODE_REGEX + ENABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( "\\/v1\\/catalog\\/pricePlans\\/" + CODE_REGEX + DISABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/country" ) ) {
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + "/countries", ((Path) anAnnotation).value() );

                            // Processing for enable and disable a trading country
                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( "\\/v1\\/countries\\/" + CODE_REGEX + ENABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( "\\/v1\\/countries\\/" + CODE_REGEX + DISABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/currency" ) ) {
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + "/currencies", ((Path) anAnnotation).value() );

                            // Processing for enable and disable a trading currency
                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( "\\/v1\\/currencies\\/" + CODE_REGEX + ENABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( "\\/v1\\/currencies\\/" + CODE_REGEX + DISABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/jobInstance" ) ) {
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + "/jobInstances", ((Path) anAnnotation).value() );

                            // Processing for enable and disable a jobInstance
                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( "\\/v1\\/jobInstances\\/" + CODE_REGEX + ENABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( "\\/v1\\/jobInstances\\/" + CODE_REGEX + DISABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/language" ) ) {
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + "/languages", ((Path) anAnnotation).value() );

                            // Processing for enable and disable a language
                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( "\\/v1\\/languages\\/" + CODE_REGEX + ENABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( "\\/v1\\/languages\\/" + CODE_REGEX + DISABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );
                        }
                        else {
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put(
                                    API_VERSION + Inflector.getInstance().pluralize( ((Path) anAnnotation).value() ),
                                    ((Path) anAnnotation).value() );
                        }
                    }
                }
            }
        }
    }
}
