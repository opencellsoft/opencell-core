package org.meveo.apiv2;

import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import org.apache.commons.collections.map.HashedMap;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.meveo.api.dto.billing.*;
import org.meveo.api.dto.invoice.CancelInvoiceRequestDto;
import org.meveo.api.dto.invoice.InvoiceDto;
import org.meveo.api.dto.invoice.ValidateInvoiceRequestDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.apiv2.document.DocumentResourceImpl;
import org.meveo.apiv2.generic.GenericResourceAPIv1Impl;
import org.meveo.apiv2.generic.NotYetImplementedResource;
import org.meveo.apiv2.generic.RegExHashMap;
import org.meveo.apiv2.generic.exception.*;
import org.meveo.apiv2.generic.services.GenericApiLoggingFilter;
import org.meveo.apiv2.ordering.resource.order.OrderResourceImpl;
import org.meveo.apiv2.ordering.resource.orderitem.OrderItemResourceImpl;
import org.meveo.apiv2.ordering.resource.product.ProductResourceImpl;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.StringUtils;
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
import java.util.regex.Matcher;
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
    public static Map<String,Class> MAP_SPECIAL_IBASE_RS_PATH_AND_DTO_CLASS = new HashMap<>();
    public static long API_LIST_DEFAULT_LIMIT;
    public static final String API_VERSION = "/v1";
    public static Map RESTFUL_ENTITIES_MAP = new LinkedHashMap();

    private static final String GENERIC_API_REQUEST_LOGGING_CONFIG_KEY = "generic.api.request.logging";
    private static final String API_LIST_DEFAULT_LIMIT_KEY = "api.list.defaultLimit";
    private static String GENERIC_API_REQUEST_LOGGING_CONFIG;
    private static final String PATH_TO_ALL_ENTITY_RS = "org.meveo.api.rest";

    // business logic final string variables
    private static final String ACCOUNT_MANAGEMENT = "/accountManagement";
    private static final String CATALOG = "/catalog";
    private static final String BILLING = "/billing";
    private static final String STOP_SERVICE = "/stop";
    private static final String ENABLE_SERVICE = "/enable";
    private static final String DISABLE_SERVICE = "/disable";

    // final useful strings
    private static final String FORWARD_SLASH = "/";
    public static final String CODE_REGEX = "[^\\/^$]+";
    private String entityName;
    private String upperEntityName;
    private String endpoint;
    private String baseURL;
    private List<String> listOfURLs;

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
        Set<Class<?>> resources = Stream.of(GenericResourceAPIv1Impl.class, NotYetImplementedResource.class,
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

    private void fillUpRestfulURLsMap(String aRestfulURL, Map mapRestful) {
        baseURL = API_VERSION + aRestfulURL;
        entityName = Inflector.getInstance().singularize(aRestfulURL.substring( aRestfulURL.lastIndexOf( FORWARD_SLASH ) + 1 ));
        upperEntityName = StringUtils.capitalizeFirstLetter(entityName);

        if ( ! mapRestful.containsKey( upperEntityName ) )
            listOfURLs = new ArrayList<>();
        else
            listOfURLs = (List) mapRestful.get( upperEntityName );

        endpoint = "POST - Create an entity of " + upperEntityName + " : " + baseURL;
        listOfURLs.add(endpoint);
        endpoint = "GET - Retrieve all entities of " + upperEntityName + " : " + baseURL;
        listOfURLs.add(endpoint);
        endpoint = "GET - Retrieve an entity of " + upperEntityName + " : " + baseURL + FORWARD_SLASH + "{" + entityName + "Code}";
        listOfURLs.add(endpoint);
        endpoint = "PUT - Update an entity of " + upperEntityName + " : "+ baseURL + FORWARD_SLASH + "{" + entityName + "Code}";
        listOfURLs.add(endpoint);
        endpoint = "DELETE - Delete an entity of " + upperEntityName + " : " + baseURL + FORWARD_SLASH + "{" + entityName + "Code}";
        listOfURLs.add(endpoint);

        mapRestful.put(upperEntityName, listOfURLs);

        fillUpRestfulEntitiesMap( mapRestful );
    }

    private void fillUpRestfulURLsMapWithSpecialURL(String aRestfulURL, Map mapRestful, String entityName) {
        baseURL = API_VERSION + aRestfulURL;
        upperEntityName = StringUtils.capitalizeFirstLetter(entityName);

        if ( ! mapRestful.containsKey( upperEntityName ) )
            listOfURLs = new ArrayList<>();
        else
            listOfURLs = (List) mapRestful.get( upperEntityName );

        if ( aRestfulURL.split( FORWARD_SLASH )[ aRestfulURL.split( FORWARD_SLASH ).length - 1 ].equals( "enable" ) ) {
            endpoint = "POST - Enable an entity of " + entityName + " : " + baseURL;
            endpoint = endpoint.replace( CODE_REGEX, "{" + entityName + "Code}" );
            listOfURLs.add(endpoint);
        }
        else if ( aRestfulURL.split( FORWARD_SLASH )[ aRestfulURL.split( FORWARD_SLASH ).length - 1 ].equals( "disable" ) ) {
            endpoint = "POST - Disable an entity of " + entityName + " : " + baseURL;
            endpoint = endpoint.replace( CODE_REGEX, "{" + entityName + "Code}" );
            listOfURLs.add(endpoint);
        }
        else if ( aRestfulURL.contains( CODE_REGEX ) ) {
            int indexCodeRegex = aRestfulURL.indexOf( CODE_REGEX );
            String aSmallPattern;
            String smallString = null;

            if ( indexCodeRegex >= 0 ) {
                String parentEntityName;
                while ( indexCodeRegex >= 0 ) {
                    aSmallPattern = aRestfulURL.substring( 0, indexCodeRegex + CODE_REGEX.length() );
                    Matcher matcher = Pattern.compile( Pattern.quote(aSmallPattern) ).matcher( aRestfulURL );
                    // get the first occurrence matching smallStringPattern
                    if ( matcher.find() ) {
                        smallString = matcher.group(0);
                    }
                    indexCodeRegex = aRestfulURL.indexOf( CODE_REGEX, indexCodeRegex + 1 );
                }

                String[] anArray = aRestfulURL.split( Pattern.quote(CODE_REGEX) );
                parentEntityName = Inflector.getInstance().singularize(anArray[0].split(FORWARD_SLASH)[ anArray[0].split(FORWARD_SLASH).length - 1 ]);
                // If smallString differs from the string aGetPath, the request is to retrieve all entities, so we add paging and filtering
                // Otherwise, if smallString is exactly the string aRestfulURL, the request is to retrieve a particular entity
                if ( ! smallString.equals( aRestfulURL ) ) {
                    endpoint = "GET - Search for all " + upperEntityName + " based on a given code of a " + parentEntityName + " : " + baseURL;
                    endpoint = endpoint.replace( CODE_REGEX, "{" + parentEntityName + "Code}" );
                }
                else {
                    endpoint = "GET - Search for a particular entity of " + upperEntityName + " based on a given code of a " + parentEntityName + " : " + baseURL;
                    endpoint = endpoint.replaceFirst( Pattern.quote(CODE_REGEX), "{" + parentEntityName + "Code}" )
                                        .replace( CODE_REGEX, "{" + entityName + "Code}" );
                }
                listOfURLs.add(endpoint);
            }
        }
        else if ( aRestfulURL.equals(ACCOUNT_MANAGEMENT + "/customers/categories") ) {
            endpoint = "GET - Search for a customer category with a given code : " + baseURL + FORWARD_SLASH + "{" + entityName + "Code}";
            listOfURLs.add(endpoint);
        }
        else if ( aRestfulURL.equals(ACCOUNT_MANAGEMENT + "/accountHierarchies") ) {
            endpoint = "POST - Create an AccountHierarchy : " + baseURL + FORWARD_SLASH + "{" + entityName + "Code}";
            listOfURLs.add(endpoint);

            endpoint = "PUT - Update an AccountHierarchy : " + baseURL + FORWARD_SLASH + "{" + entityName + "Code}";
            listOfURLs.add(endpoint);
        }
        else if ( aRestfulURL.equals(ACCOUNT_MANAGEMENT + "/subscriptions") ) {
            endpoint = "PUT - Activate a subscription : " + baseURL + FORWARD_SLASH + "{" + entityName + "Code}" + "/activation";
            listOfURLs.add(endpoint);

            endpoint = "PUT - Suspend a subscription : " + baseURL + FORWARD_SLASH + "{" + entityName + "Code}" + "/suspension";
            listOfURLs.add(endpoint);

            endpoint = "PUT - Update services of a subscription : " + baseURL + FORWARD_SLASH + "{" + entityName + "Code}" + "/services";
            listOfURLs.add(endpoint);

            endpoint = "PUT - Terminate a subscription : " + baseURL + FORWARD_SLASH + "{" + entityName + "Code}" + "/termination";
            listOfURLs.add(endpoint);
        }

        mapRestful.put(upperEntityName, listOfURLs);
    }

    private void fillUpRestfulEntitiesMap(Map mapOfEntityAndURLs) {
        RESTFUL_ENTITIES_MAP.putAll(mapOfEntityAndURLs);
    }

    private void loadMapPathAndInterfaceIBaseRs() {
        Reflections reflections = new Reflections( PATH_TO_ALL_ENTITY_RS );
        Set<Class<? extends IBaseRs>> classes = reflections.getSubTypesOf(IBaseRs.class);
        Map<String, List<String>> aMapRestful = new HashMap<>();

        // handling normal cases
        fillUpRestfulURLsMap( "/billingCycles", aMapRestful );
        fillUpRestfulURLsMap( "/invoiceCategories", aMapRestful );
        fillUpRestfulURLsMap( "/invoiceSequences", aMapRestful );
        fillUpRestfulURLsMap( "/invoiceSubCategories", aMapRestful );
        fillUpRestfulURLsMap( "/invoiceTypes", aMapRestful );
        fillUpRestfulURLsMap( "/users", aMapRestful );
        fillUpRestfulURLsMap( "/calendars", aMapRestful );
        fillUpRestfulURLsMap( "/catalog/unitOfMeasures", aMapRestful );
        fillUpRestfulURLsMap( "/contacts", aMapRestful );
        fillUpRestfulURLsMap( "/taxes", aMapRestful );
        fillUpRestfulURLsMap( "/taxCategories", aMapRestful );
        fillUpRestfulURLsMap( "/taxClasses", aMapRestful );
        fillUpRestfulURLsMap( "/taxMappings", aMapRestful );
        fillUpRestfulURLsMap( "/payment/creditCategories", aMapRestful );

        for ( Class<? extends IBaseRs> aClass : classes ) {
            if ( aClass.isInterface() ) {
                Annotation[] arrAnnotations = aClass.getAnnotations();
                for ( Annotation anAnnotation : arrAnnotations ) {
                    if ( anAnnotation instanceof Path) {
                        if ( ((Path) anAnnotation).value().equals( "/seller" ) ) {
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + ACCOUNT_MANAGEMENT + ((Path) anAnnotation).value() + "s",
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( ACCOUNT_MANAGEMENT + "/sellers",
                                    aMapRestful );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/account/title" ) ) {
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + ACCOUNT_MANAGEMENT + "/titles",
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( ACCOUNT_MANAGEMENT + "/titles",
                                    aMapRestful );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/account/customer" ) ) {
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + ACCOUNT_MANAGEMENT + "/customers",
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( ACCOUNT_MANAGEMENT + "/customers", aMapRestful );

                            // Handling requests related to customerCategory
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + ACCOUNT_MANAGEMENT + "/customerCategories",
                                    ((Path) anAnnotation).value() );
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + ACCOUNT_MANAGEMENT + "/customerBrands",
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMapWithSpecialURL( ACCOUNT_MANAGEMENT + "/customerCategories", aMapRestful, "customer" );
                            fillUpRestfulURLsMapWithSpecialURL( ACCOUNT_MANAGEMENT + "/customerBrands", aMapRestful, "customer" );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/account/customerAccount" ) ) {
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + ACCOUNT_MANAGEMENT + "/customerAccounts",
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( ACCOUNT_MANAGEMENT + "/customerAccounts", aMapRestful );

                            // Handling request get list of customerAccounts based on a customerCode
                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( API_VERSION + "\\/accountManagement\\/customers\\/" + CODE_REGEX + "\\/customerAccounts" ) ,
                                    ((Path) anAnnotation).value() + "/list" );

                            fillUpRestfulURLsMapWithSpecialURL( "/accountManagement/customers/" + CODE_REGEX + "/customerAccounts", aMapRestful, "customerAccount" );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/account/billingAccount" ) ) {
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + ACCOUNT_MANAGEMENT + "/billingAccounts",
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( ACCOUNT_MANAGEMENT + "/billingAccounts", aMapRestful );

                            // Handling request get list of billingAccounts based on a customerAccountCode
                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( API_VERSION + "\\/accountManagement\\/customerAccounts\\/" + CODE_REGEX + "\\/billingAccounts" ) ,
                                    ((Path) anAnnotation).value() + "/list" );

                            fillUpRestfulURLsMapWithSpecialURL( "/accountManagement/customerAccounts/" + CODE_REGEX + "/billingAccounts", aMapRestful, "billingAccount" );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/account/userAccount" ) ) {
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + ACCOUNT_MANAGEMENT + "/userAccounts", ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( ACCOUNT_MANAGEMENT + "/userAccounts", aMapRestful );

                            // Handling request get list of userAccounts based on a billingAccountCode
                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( API_VERSION + "\\/accountManagement\\/billingAccounts\\/" + CODE_REGEX + "\\/userAccounts" ) ,
                                    ((Path) anAnnotation).value() + "/list" );

                            fillUpRestfulURLsMapWithSpecialURL( "/accountManagement/billingAccounts/" + CODE_REGEX + "/userAccounts", aMapRestful, "userAccount" );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/account/accountHierarchy" ) ) {
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + ACCOUNT_MANAGEMENT + "/accountHierarchies",
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMapWithSpecialURL( ACCOUNT_MANAGEMENT + "/accountHierarchies", aMapRestful, "accountHierarchy" );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/billing/subscription" ) ) {
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + ACCOUNT_MANAGEMENT + "/subscriptions",
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( ACCOUNT_MANAGEMENT + "/subscriptions", aMapRestful );

                            // Handling different services of subscription: activation, suspension, termination, update of existing services
                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( API_VERSION + "\\/accountManagement\\/subscriptions\\/" + CODE_REGEX + "\\/activation" ) ,
                                    ((Path) anAnnotation).value() + "/activate" );

                            MAP_SPECIAL_IBASE_RS_PATH_AND_DTO_CLASS.put( ((Path) anAnnotation).value() + "/activate", ActivateSubscriptionRequestDto.class );

                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( API_VERSION + "\\/accountManagement\\/subscriptions\\/" + CODE_REGEX + "\\/suspension" ) ,
                                    ((Path) anAnnotation).value() + "/suspend" );

                            MAP_SPECIAL_IBASE_RS_PATH_AND_DTO_CLASS.put( ((Path) anAnnotation).value() + "/suspend", OperationSubscriptionRequestDto.class );

                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( API_VERSION + "\\/accountManagement\\/subscriptions\\/" + CODE_REGEX + "\\/termination" ) ,
                                    ((Path) anAnnotation).value() + "/terminate" );

                            MAP_SPECIAL_IBASE_RS_PATH_AND_DTO_CLASS.put( ((Path) anAnnotation).value() + "/terminate", TerminateSubscriptionRequestDto.class );

                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( API_VERSION + "\\/accountManagement\\/subscriptions\\/" + CODE_REGEX + "\\/services" ) ,
                                    ((Path) anAnnotation).value() + "/updateServices" );

                            MAP_SPECIAL_IBASE_RS_PATH_AND_DTO_CLASS.put( ((Path) anAnnotation).value() + "/updateServices", UpdateServicesRequestDto.class );

                            fillUpRestfulURLsMapWithSpecialURL( ACCOUNT_MANAGEMENT + "/subscriptions", aMapRestful, "subscription" );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/account/access" ) ) {
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + ACCOUNT_MANAGEMENT + "/accesses",
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( ACCOUNT_MANAGEMENT + "/accesses", aMapRestful );

                            // Handling request get list of accesses based on a subscriptionCode
                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( API_VERSION + "\\/accountManagement\\/subscriptions\\/" + CODE_REGEX + "\\/accesses" ) ,
                                    ((Path) anAnnotation).value() + "/list" );

                            fillUpRestfulURLsMapWithSpecialURL( ACCOUNT_MANAGEMENT + "/subscriptions/" + CODE_REGEX + "/accesses", aMapRestful, "access" );

                            // Handling request get an accessPoint based on a subscriptionCode and an accessCode
                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( API_VERSION + "\\/accountManagement\\/subscriptions\\/" + CODE_REGEX + "\\/accesses\\/" + CODE_REGEX ) ,
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMapWithSpecialURL( ACCOUNT_MANAGEMENT + "/subscriptions/" + CODE_REGEX + "/accesses/" + CODE_REGEX, aMapRestful, "access" );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/billing/ratedTransaction" ) ) {
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + BILLING + "/ratedTransactions",
                                    ((Path) anAnnotation).value() );
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + BILLING + "/ratedTransactions/cancellation",
                                    ((Path) anAnnotation).value() + "/cancelRatedTransactions" );

                            fillUpRestfulURLsMap( BILLING + "/ratedTransactions", aMapRestful );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/billing/wallet" ) ) {
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + BILLING + "/wallets",
                                    ((Path) anAnnotation).value() );
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + BILLING + "/wallets/operation",
                                    ((Path) anAnnotation).value() + "/operation" );

                            fillUpRestfulURLsMap( BILLING + "/wallets", aMapRestful );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/catalog/offerTemplate" ) ) {
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + CATALOG + "/offerTemplates",
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( CATALOG + "/offerTemplates", aMapRestful );

                            // Handling enable and disable an offerTemplate
                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( API_VERSION + "\\/catalog\\/offerTemplates\\/" + CODE_REGEX + ENABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( API_VERSION + "\\/catalog\\/offerTemplates\\/" + CODE_REGEX + DISABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMapWithSpecialURL( CATALOG + "/offerTemplates/" + CODE_REGEX + ENABLE_SERVICE, aMapRestful, "offerTemplate" );

                            fillUpRestfulURLsMapWithSpecialURL( CATALOG + "/offerTemplates/" + CODE_REGEX + DISABLE_SERVICE, aMapRestful, "offerTemplate" );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/catalog/oneShotChargeTemplate" ) ) {
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + CATALOG + "/oneShotChargeTemplates",
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( CATALOG + "/oneShotChargeTemplates", aMapRestful );

                            // Handling enable and disable an oneShotChargeTemplate
                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( API_VERSION + "\\/catalog\\/oneShotChargeTemplates\\/" + CODE_REGEX + ENABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( API_VERSION + "\\/catalog\\/oneShotChargeTemplates\\/" + CODE_REGEX + DISABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMapWithSpecialURL( CATALOG + "/oneShotChargeTemplates/" + CODE_REGEX + ENABLE_SERVICE, aMapRestful, "oneShotChargeTemplate" );

                            fillUpRestfulURLsMapWithSpecialURL( CATALOG + "/oneShotChargeTemplates/" + CODE_REGEX + DISABLE_SERVICE, aMapRestful, "oneShotChargeTemplate" );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/catalog/recurringChargeTemplate" ) ) {
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + CATALOG + "/recurringChargeTemplates",
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( CATALOG + "/recurringChargeTemplates", aMapRestful );

                            // Handling enable and disable a recurringChargeTemplate
                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( API_VERSION + "\\/catalog\\/recurringChargeTemplates\\/" + CODE_REGEX + ENABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( API_VERSION + "\\/catalog\\/recurringChargeTemplates\\/" + CODE_REGEX + DISABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMapWithSpecialURL( CATALOG + "/recurringChargeTemplates/" + CODE_REGEX + ENABLE_SERVICE, aMapRestful, "recurringChargeTemplate" );

                            fillUpRestfulURLsMapWithSpecialURL( CATALOG + "/recurringChargeTemplates/" + CODE_REGEX + DISABLE_SERVICE, aMapRestful, "recurringChargeTemplate" );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/catalog/usageChargeTemplate" ) ) {
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + CATALOG + "/usageChargeTemplates",
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( CATALOG + "/usageChargeTemplates", aMapRestful );

                            // Handling enable and disable a usageChargeTemplate
                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( API_VERSION + "\\/catalog\\/usageChargeTemplates\\/" + CODE_REGEX + ENABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( API_VERSION + "\\/catalog\\/usageChargeTemplates\\/" + CODE_REGEX + DISABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMapWithSpecialURL( CATALOG + "/usageChargeTemplates/" + CODE_REGEX + ENABLE_SERVICE, aMapRestful, "usageChargeTemplate" );

                            fillUpRestfulURLsMapWithSpecialURL( CATALOG + "/usageChargeTemplates/" + CODE_REGEX + DISABLE_SERVICE, aMapRestful, "usageChargeTemplate" );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/catalog/serviceTemplate" ) ) {
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + CATALOG + "/serviceTemplates",
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( CATALOG + "/serviceTemplates", aMapRestful );

                            // Handling enable and disable a serviceTemplate
                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( API_VERSION + "\\/catalog\\/serviceTemplates\\/" + CODE_REGEX + ENABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( API_VERSION + "\\/catalog\\/serviceTemplates\\/" + CODE_REGEX + DISABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMapWithSpecialURL( CATALOG + "/serviceTemplates/" + CODE_REGEX + ENABLE_SERVICE, aMapRestful, "serviceTemplate" );

                            fillUpRestfulURLsMapWithSpecialURL( CATALOG + "/serviceTemplates/" + CODE_REGEX + DISABLE_SERVICE, aMapRestful, "serviceTemplate" );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/catalog/pricePlan" ) ) {
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + CATALOG + "/pricePlans",
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( CATALOG + "/pricePlans", aMapRestful );

                            // Handling the list of pricePlans based on given charge code
                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( API_VERSION + "\\/catalog\\/" + CODE_REGEX + "\\/pricePlans" ) ,
                                    ((Path) anAnnotation).value() + "/list" );

                            // Handling enable and disable a pricePlan
                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( API_VERSION + "\\/catalog\\/pricePlans\\/" + CODE_REGEX + ENABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( API_VERSION + "\\/catalog\\/pricePlans\\/" + CODE_REGEX + DISABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMapWithSpecialURL( CATALOG + "/pricePlans/" + CODE_REGEX + ENABLE_SERVICE, aMapRestful, "pricePlan" );

                            fillUpRestfulURLsMapWithSpecialURL( CATALOG + "/pricePlans/" + CODE_REGEX + DISABLE_SERVICE, aMapRestful, "pricePlan" );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/country" ) ) {
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + "/countries", ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( "/countries", aMapRestful );

                            // Handling enable and disable a trading country
                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( API_VERSION + "\\/countries\\/" + CODE_REGEX + ENABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( API_VERSION + "\\/countries\\/" + CODE_REGEX + DISABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMapWithSpecialURL( "/countries/" + CODE_REGEX + ENABLE_SERVICE, aMapRestful, "country" );

                            fillUpRestfulURLsMapWithSpecialURL( "/countries/" + CODE_REGEX + DISABLE_SERVICE, aMapRestful, "country" );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/currency" ) ) {
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + "/currencies", ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( "/currencies", aMapRestful );

                            // Handling enable and disable a trading currency
                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( API_VERSION + "\\/currencies\\/" + CODE_REGEX + ENABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( API_VERSION + "\\/currencies\\/" + CODE_REGEX + DISABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMapWithSpecialURL( "/currencies/" + CODE_REGEX + ENABLE_SERVICE, aMapRestful, "currency" );

                            fillUpRestfulURLsMapWithSpecialURL( "/currencies/" + CODE_REGEX + DISABLE_SERVICE, aMapRestful, "currency" );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/jobInstance" ) ) {
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + "/jobInstances", ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( "/jobInstances", aMapRestful );

                            // Handling enable and disable a jobInstance
                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( API_VERSION + "\\/jobInstances\\/" + CODE_REGEX + ENABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( API_VERSION + "\\/jobInstances\\/" + CODE_REGEX + DISABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMapWithSpecialURL( "/jobInstances/" + CODE_REGEX + ENABLE_SERVICE, aMapRestful, "jobInstance" );

                            fillUpRestfulURLsMapWithSpecialURL( "/jobInstances/" + CODE_REGEX + DISABLE_SERVICE, aMapRestful, "jobInstance" );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/language" ) ) {
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + "/languages", ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( "/languages", aMapRestful );

                            // Handling enable and disable a trading language
                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( API_VERSION + "\\/languages\\/" + CODE_REGEX + ENABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( API_VERSION + "\\/languages\\/" + CODE_REGEX + DISABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMapWithSpecialURL( "/languages/" + CODE_REGEX + ENABLE_SERVICE, aMapRestful, "language" );

                            fillUpRestfulURLsMapWithSpecialURL( "/languages/" + CODE_REGEX + DISABLE_SERVICE, aMapRestful, "language" );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/invoice" ) ) {
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + "/invoices",
                                    ((Path) anAnnotation).value() );

                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + "/invoices/generation",
                                    ((Path) anAnnotation).value() + "/generateInvoice" );

                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + "/invoices/emailSending",
                                    ((Path) anAnnotation).value() + "/sendByEmail" );

                            MAP_SPECIAL_IBASE_RS_PATH_AND_DTO_CLASS.put( ((Path) anAnnotation).value() + "/sendByEmail", InvoiceDto.class );

                            // Handling different services of invoice: cancellation, validation
                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( API_VERSION + "\\/invoices\\/" + CODE_REGEX + "\\/cancellation" ) ,
                                    ((Path) anAnnotation).value() + "/cancel" );

                            MAP_SPECIAL_IBASE_RS_PATH_AND_DTO_CLASS.put( ((Path) anAnnotation).value() + "/cancel", CancelInvoiceRequestDto.class );

                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( API_VERSION + "\\/invoices\\/" + CODE_REGEX + "\\/validation" ) ,
                                    ((Path) anAnnotation).value() + "/validate" );

                            MAP_SPECIAL_IBASE_RS_PATH_AND_DTO_CLASS.put( ((Path) anAnnotation).value() + "/validate", ValidateInvoiceRequestDto.class );

                            fillUpRestfulURLsMap( "/invoices", aMapRestful );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/billing/invoicing" ) ) {
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + "/billing/invoicings/creation",
                                    ((Path) anAnnotation).value() + "/createBillingRun" );

                            // Handling different services of billing: cancellation of a billing run, validation of a billing run
                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( API_VERSION + "\\/billing/invoicings\\/" + CODE_REGEX + "\\/cancellation" ) ,
                                    ((Path) anAnnotation).value() + "/cancelBillingRun" );

                            MAP_SPECIAL_IBASE_RS_PATH_AND_DTO_CLASS.put( ((Path) anAnnotation).value() + "/cancelBillingRun", CancelBillingRunRequestDto.class );

                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( API_VERSION + "\\/billing/invoicings\\/" + CODE_REGEX + "\\/validation" ) ,
                                    ((Path) anAnnotation).value() + "/validateBillingRun" );

                            MAP_SPECIAL_IBASE_RS_PATH_AND_DTO_CLASS.put( ((Path) anAnnotation).value() + "/validateBillingRun", ValidateBillingRunRequestDto.class );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/job" ) ) {
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + "/jobs", ((Path) anAnnotation).value() );

                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + "/jobs/execution", ((Path) anAnnotation).value() + "/execute" );

                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + "/jobs/jobReports", ((Path) anAnnotation).value() + "/jobReport" );

                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + "/jobs/timers", ((Path) anAnnotation).value() + "/timer" );

                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( API_VERSION + "\\/jobs\\/" + CODE_REGEX + STOP_SERVICE ) ,
                                    ((Path) anAnnotation).value() + "/stop" );

                            MAP_SPECIAL_IBASE_RS_PATH_AND_DTO_CLASS.put( ((Path) anAnnotation).value() + "/stop", null );

                            fillUpRestfulURLsMap( "/jobs", aMapRestful );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/PdfInvoice" ) ) {
                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( API_VERSION + "/pdfInvoices" ), ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( ((Path) anAnnotation).value(), aMapRestful );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/billing/accountingCode" ) ) {
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + BILLING + "/accountingCodes",
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( BILLING + "/accountingCodes", aMapRestful );

                            // Handling enable and disable an accountingCode
                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( API_VERSION + BILLING + "\\/accountingCodes\\/" + CODE_REGEX + ENABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( API_VERSION + BILLING + "\\/accountingCodes\\/" + CODE_REGEX + DISABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMapWithSpecialURL( BILLING + "/accountingCodes/" + CODE_REGEX + ENABLE_SERVICE, aMapRestful, "accountingCode" );

                            fillUpRestfulURLsMapWithSpecialURL( BILLING + "/accountingCodes/" + CODE_REGEX + DISABLE_SERVICE, aMapRestful, "accountingCode" );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/countryIso" ) ) {
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + "/countriesIso", ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( "/countriesIso", aMapRestful );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/currencyIso" ) ) {
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + "/currenciesIso", ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( "/currenciesIso", aMapRestful );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/languageIso" ) ) {
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + "/languagesIso", ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( "/languagesIso", aMapRestful );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/payment" ) ) {
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + "/payment/paymentMethods",
                                    ((Path) anAnnotation).value() + "/paymentMethod" );

                            fillUpRestfulURLsMap( "/payment/paymentMethods", aMapRestful );

                            // Handling request get list of paymentMethods based on a customerAccountCode
                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( API_VERSION + "\\/payment\\/customerAccounts\\/" + CODE_REGEX + "\\/paymentMethods" ) ,
                                    ((Path) anAnnotation).value() + "/paymentMethod/findByCustomerAccount" );

                            // Handling enable and disable a paymentMethod
                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( API_VERSION + "\\/payment\\/paymentMethods\\/" + CODE_REGEX + ENABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() + "/paymentMethod" );

                            MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( API_VERSION + "\\/payment\\/paymentMethods\\/" + CODE_REGEX + DISABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() + "/paymentMethod" );

                            fillUpRestfulURLsMapWithSpecialURL( "/payment/paymentMethods/" + CODE_REGEX + ENABLE_SERVICE, aMapRestful, "paymentMethod" );

                            fillUpRestfulURLsMapWithSpecialURL( "/payment/paymentMethods/" + CODE_REGEX + DISABLE_SERVICE, aMapRestful, "paymentMethod" );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/account/providerContact" ) ) {
                            MAP_NEW_PATH_AND_IBASE_RS_PATH.put( API_VERSION + ACCOUNT_MANAGEMENT + "/providerContacts", ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( ACCOUNT_MANAGEMENT + "/providerContacts", aMapRestful );
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
