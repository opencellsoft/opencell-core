package org.meveo.api.restful;

import io.swagger.v3.jaxrs2.SwaggerSerializers;
import io.swagger.v3.jaxrs2.integration.JaxrsAnnotationScanner;
import io.swagger.v3.oas.integration.GenericOpenApiContext;
import io.swagger.v3.oas.integration.OpenApiContextLocator;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.integration.api.OpenApiContext;
import io.swagger.v3.oas.models.OpenAPI;
import org.apache.commons.collections.map.HashedMap;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.meveo.api.dto.billing.ActivateSubscriptionRequestDto;
import org.meveo.api.dto.billing.CancelBillingRunRequestDto;
import org.meveo.api.dto.billing.OperationSubscriptionRequestDto;
import org.meveo.api.dto.billing.TerminateSubscriptionRequestDto;
import org.meveo.api.dto.billing.UpdateServicesRequestDto;
import org.meveo.api.dto.billing.ValidateBillingRunRequestDto;
import org.meveo.api.dto.invoice.CancelInvoiceRequestDto;
import org.meveo.api.dto.invoice.InvoiceDto;
import org.meveo.api.dto.invoice.ValidateInvoiceRequestDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.restful.annotation.GetAllEntities;
import org.meveo.api.restful.services.Apiv1ConstantDictionary;
import org.meveo.api.restful.services.Apiv1GetService;
import org.meveo.api.restful.swagger.ApiRestSwaggerGeneration;
import org.meveo.api.restful.util.RegExHashMap;
import org.meveo.apiv2.generic.exception.BadRequestExceptionMapper;
import org.meveo.apiv2.generic.exception.EJBTransactionRolledbackExceptionMapper;
import org.meveo.apiv2.generic.exception.IllegalArgumentExceptionMapper;
import org.meveo.apiv2.generic.exception.MeveoExceptionMapper;
import org.meveo.apiv2.generic.exception.NotFoundExceptionMapper;
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
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class is used to construct the HashMap for URL Redirection at deployment
 *
 * @author Thang Nguyen
 */

@ApplicationPath(GenericOpencellRestfulAPIv1.REST_PATH)
public class GenericOpencellRestfulAPIv1 extends Application {
    public static Map<Object,String> MAP_RESTFUL_PATH_AND_IBASE_RS_PATH = new HashMap<>();
    public static RegExHashMap<Object,String> MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH = new RegExHashMap<>();
    public static Map<String,Class> MAP_SPECIAL_IBASE_RS_PATH_AND_DTO_CLASS = new HashMap<>();
    public static long API_LIST_DEFAULT_LIMIT;
    public static final String REST_PATH = "/api/rest/v1";
    public static Map RESTFUL_ENTITIES_MAP = new LinkedHashMap();

    private static final String API_LIST_DEFAULT_LIMIT_KEY = "api.list.defaultLimit";
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
        loadMapPathAndInterfaceIBaseRs();
        loadSetGetAll();
    }

    private void loadSetGetAll() {
        for ( Field field : Apiv1ConstantDictionary.class.getDeclaredFields() ) {
            if ( field.isAnnotationPresent(GetAllEntities.class) ) {
                try {
                    Apiv1GetService.SET_GET_ALL.add((String) field.get( null ));
                }
                catch ( IllegalAccessException exception ) {

                }
            }
        }
    }



    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = Stream.of(ApiRestSwaggerGeneration.class, GenericResourceAPIv1Impl.class,
                NotFoundExceptionMapper.class, BadRequestExceptionMapper.class, MeveoExceptionMapper.class,
                IllegalArgumentExceptionMapper.class, EJBTransactionRolledbackExceptionMapper.class,
                SwaggerSerializers.class)
                .collect(Collectors.toSet());
        log.info("Opencell OpenAPI definition is accessible in /v1/openapi.{type:json|yaml}");
        return resources;
    }


    private void fillUpRestfulURLsMap(String aRestfulURL, Map mapRestful) {
        baseURL = REST_PATH + aRestfulURL;
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
        baseURL = REST_PATH + aRestfulURL;
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
        fillUpRestfulURLsMap( "/catalog/businessProductModels", aMapRestful );
        fillUpRestfulURLsMap( "/catalog/businessOfferModels", aMapRestful );
        fillUpRestfulURLsMap( "/catalog/businessServiceModels", aMapRestful );
        fillUpRestfulURLsMap( "/catalog/triggeredEdrs", aMapRestful );
        fillUpRestfulURLsMap( "/communication/emailTemplates", aMapRestful );
        fillUpRestfulURLsMap( "/communication/meveoInstances", aMapRestful );

        for ( Class<? extends IBaseRs> aClass : classes ) {
            if ( aClass.isInterface() ) {
                Annotation[] arrAnnotations = aClass.getAnnotations();
                for ( Annotation anAnnotation : arrAnnotations ) {
                    if ( anAnnotation instanceof Path) {
                        if ( ((Path) anAnnotation).value().equals( "/seller" ) ) {
                            MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.put( REST_PATH + ACCOUNT_MANAGEMENT + ((Path) anAnnotation).value() + "s",
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( ACCOUNT_MANAGEMENT + "/sellers", aMapRestful );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/account/title" ) ) {
                            MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.put( REST_PATH + ACCOUNT_MANAGEMENT + "/titles",
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( ACCOUNT_MANAGEMENT + "/titles", aMapRestful );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/account/customer" ) ) {
                            MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.put( REST_PATH + ACCOUNT_MANAGEMENT + "/customers",
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( ACCOUNT_MANAGEMENT + "/customers", aMapRestful );

                            // Handling requests related to customerCategory
                            MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.put( REST_PATH + ACCOUNT_MANAGEMENT + "/customerCategories",
                                    ((Path) anAnnotation).value() + "/category" );

                            fillUpRestfulURLsMapWithSpecialURL( ACCOUNT_MANAGEMENT + "/customerCategories", aMapRestful, "customer" );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/account/customerAccount" ) ) {
                            MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.put( REST_PATH + ACCOUNT_MANAGEMENT + "/customerAccounts",
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( ACCOUNT_MANAGEMENT + "/customerAccounts", aMapRestful );

                            // Handling request get list of customerAccounts based on a customerCode
                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/accountManagement\\/customers\\/" + CODE_REGEX + "\\/customerAccounts" ) ,
                                    ((Path) anAnnotation).value() + "/list" );

                            fillUpRestfulURLsMapWithSpecialURL( "/accountManagement/customers/" + CODE_REGEX + "/customerAccounts", aMapRestful, "customerAccount" );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/account/billingAccount" ) ) {
                            MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.put( REST_PATH + ACCOUNT_MANAGEMENT + "/billingAccounts",
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( ACCOUNT_MANAGEMENT + "/billingAccounts", aMapRestful );

                            // Handling request get list of billingAccounts based on a customerAccountCode
                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/accountManagement\\/customerAccounts\\/" + CODE_REGEX + "\\/billingAccounts" ) ,
                                    ((Path) anAnnotation).value() + "/list" );

                            fillUpRestfulURLsMapWithSpecialURL( "/accountManagement/customerAccounts/" + CODE_REGEX + "/billingAccounts", aMapRestful, "billingAccount" );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/account/userAccount" ) ) {
                            MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.put( REST_PATH + ACCOUNT_MANAGEMENT + "/userAccounts", ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( ACCOUNT_MANAGEMENT + "/userAccounts", aMapRestful );

                            // Handling request get list of userAccounts based on a billingAccountCode
                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/accountManagement\\/billingAccounts\\/" + CODE_REGEX + "\\/userAccounts" ) ,
                                    ((Path) anAnnotation).value() + "/list" );

                            fillUpRestfulURLsMapWithSpecialURL( "/accountManagement/billingAccounts/" + CODE_REGEX + "/userAccounts", aMapRestful, "userAccount" );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/account/accountHierarchy" ) ) {
                            MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.put( REST_PATH + ACCOUNT_MANAGEMENT + "/accountHierarchies",
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMapWithSpecialURL( ACCOUNT_MANAGEMENT + "/accountHierarchies", aMapRestful, "accountHierarchy" );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/billing/subscription" ) ) {
                            MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.put( REST_PATH + ACCOUNT_MANAGEMENT + "/subscriptions",
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( ACCOUNT_MANAGEMENT + "/subscriptions", aMapRestful );

                            // Handling different services of subscription: activation, suspension, termination, update of existing services
                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/accountManagement\\/subscriptions\\/" + CODE_REGEX + "\\/activate" ) ,
                                    ((Path) anAnnotation).value() + "/activate" );

                            MAP_SPECIAL_IBASE_RS_PATH_AND_DTO_CLASS.put( ((Path) anAnnotation).value() + "/activate", ActivateSubscriptionRequestDto.class );

                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/accountManagement\\/subscriptions\\/" + CODE_REGEX + "\\/suspend" ) ,
                                    ((Path) anAnnotation).value() + "/suspend" );

                            MAP_SPECIAL_IBASE_RS_PATH_AND_DTO_CLASS.put( ((Path) anAnnotation).value() + "/suspend", OperationSubscriptionRequestDto.class );

                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/accountManagement\\/subscriptions\\/" + CODE_REGEX + "\\/terminate" ) ,
                                    ((Path) anAnnotation).value() + "/terminate" );

                            MAP_SPECIAL_IBASE_RS_PATH_AND_DTO_CLASS.put( ((Path) anAnnotation).value() + "/terminate", TerminateSubscriptionRequestDto.class );

                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/accountManagement\\/subscriptions\\/" + CODE_REGEX + "\\/updateServices" ) ,
                                    ((Path) anAnnotation).value() + "/updateServices" );

                            MAP_SPECIAL_IBASE_RS_PATH_AND_DTO_CLASS.put( ((Path) anAnnotation).value() + "/updateServices", UpdateServicesRequestDto.class );

                            fillUpRestfulURLsMapWithSpecialURL( ACCOUNT_MANAGEMENT + "/subscriptions", aMapRestful, "subscription" );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/account/access" ) ) {
                            MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.put( REST_PATH + ACCOUNT_MANAGEMENT + "/accesses",
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( ACCOUNT_MANAGEMENT + "/accesses", aMapRestful );

                            // Handling request get list of accesses based on a subscriptionCode
                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/accountManagement\\/subscriptions\\/" + CODE_REGEX + "\\/accesses" ) ,
                                    ((Path) anAnnotation).value() + "/list" );

                            fillUpRestfulURLsMapWithSpecialURL( ACCOUNT_MANAGEMENT + "/subscriptions/" + CODE_REGEX + "/accesses", aMapRestful, "access" );

                            // Handling request get an accessPoint based on a subscriptionCode and an accessCode
                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/accountManagement\\/subscriptions\\/" + CODE_REGEX + "\\/accesses\\/" + CODE_REGEX ) ,
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMapWithSpecialURL( ACCOUNT_MANAGEMENT + "/subscriptions/" + CODE_REGEX + "/accesses/" + CODE_REGEX, aMapRestful, "access" );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/billing/ratedTransaction" ) ) {
                            MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.put( REST_PATH + BILLING + "/ratedTransactions",
                                    ((Path) anAnnotation).value() );
                            MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.put( REST_PATH + BILLING + "/ratedTransactions/cancel",
                                    ((Path) anAnnotation).value() + "/cancelRatedTransactions" );

                            fillUpRestfulURLsMap( BILLING + "/ratedTransactions", aMapRestful );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/billing/wallet" ) ) {
                            MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.put( REST_PATH + BILLING + "/wallets",
                                    ((Path) anAnnotation).value() );
                            MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.put( REST_PATH + BILLING + "/wallets/operation",
                                    ((Path) anAnnotation).value() + "/operation" );

                            fillUpRestfulURLsMap( BILLING + "/wallets", aMapRestful );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/catalog/offerTemplate" ) ) {
                            MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.put( REST_PATH + CATALOG + "/offerTemplates",
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( CATALOG + "/offerTemplates", aMapRestful );

                            // Handling enable and disable an offerTemplate
                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/catalog\\/offerTemplates\\/" + CODE_REGEX + ENABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/catalog\\/offerTemplates\\/" + CODE_REGEX + DISABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMapWithSpecialURL( CATALOG + "/offerTemplates/" + CODE_REGEX + ENABLE_SERVICE, aMapRestful, "offerTemplate" );

                            fillUpRestfulURLsMapWithSpecialURL( CATALOG + "/offerTemplates/" + CODE_REGEX + DISABLE_SERVICE, aMapRestful, "offerTemplate" );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/catalog/oneShotChargeTemplate" ) ) {
                            MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.put( REST_PATH + CATALOG + "/oneShotChargeTemplates",
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( CATALOG + "/oneShotChargeTemplates", aMapRestful );

                            // Handling enable and disable an oneShotChargeTemplate
                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/catalog\\/oneShotChargeTemplates\\/" + CODE_REGEX + ENABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/catalog\\/oneShotChargeTemplates\\/" + CODE_REGEX + DISABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMapWithSpecialURL( CATALOG + "/oneShotChargeTemplates/" + CODE_REGEX + ENABLE_SERVICE, aMapRestful, "oneShotChargeTemplate" );

                            fillUpRestfulURLsMapWithSpecialURL( CATALOG + "/oneShotChargeTemplates/" + CODE_REGEX + DISABLE_SERVICE, aMapRestful, "oneShotChargeTemplate" );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/catalog/recurringChargeTemplate" ) ) {
                            MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.put( REST_PATH + CATALOG + "/recurringChargeTemplates",
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( CATALOG + "/recurringChargeTemplates", aMapRestful );

                            // Handling enable and disable a recurringChargeTemplate
                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/catalog\\/recurringChargeTemplates\\/" + CODE_REGEX + ENABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/catalog\\/recurringChargeTemplates\\/" + CODE_REGEX + DISABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMapWithSpecialURL( CATALOG + "/recurringChargeTemplates/" + CODE_REGEX + ENABLE_SERVICE, aMapRestful, "recurringChargeTemplate" );

                            fillUpRestfulURLsMapWithSpecialURL( CATALOG + "/recurringChargeTemplates/" + CODE_REGEX + DISABLE_SERVICE, aMapRestful, "recurringChargeTemplate" );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/catalog/usageChargeTemplate" ) ) {
                            MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.put( REST_PATH + CATALOG + "/usageChargeTemplates",
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( CATALOG + "/usageChargeTemplates", aMapRestful );

                            // Handling enable and disable a usageChargeTemplate
                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/catalog\\/usageChargeTemplates\\/" + CODE_REGEX + ENABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/catalog\\/usageChargeTemplates\\/" + CODE_REGEX + DISABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMapWithSpecialURL( CATALOG + "/usageChargeTemplates/" + CODE_REGEX + ENABLE_SERVICE, aMapRestful, "usageChargeTemplate" );

                            fillUpRestfulURLsMapWithSpecialURL( CATALOG + "/usageChargeTemplates/" + CODE_REGEX + DISABLE_SERVICE, aMapRestful, "usageChargeTemplate" );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/catalog/serviceTemplate" ) ) {
                            MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.put( REST_PATH + CATALOG + "/serviceTemplates",
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( CATALOG + "/serviceTemplates", aMapRestful );

                            // Handling enable and disable a serviceTemplate
                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/catalog\\/serviceTemplates\\/" + CODE_REGEX + ENABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/catalog\\/serviceTemplates\\/" + CODE_REGEX + DISABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMapWithSpecialURL( CATALOG + "/serviceTemplates/" + CODE_REGEX + ENABLE_SERVICE, aMapRestful, "serviceTemplate" );

                            fillUpRestfulURLsMapWithSpecialURL( CATALOG + "/serviceTemplates/" + CODE_REGEX + DISABLE_SERVICE, aMapRestful, "serviceTemplate" );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/catalog/pricePlan" ) ) {
                            MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.put( REST_PATH + CATALOG + "/pricePlans",
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( CATALOG + "/pricePlans", aMapRestful );

                            // Handling the list of pricePlans based on given charge code
                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/catalog\\/" + CODE_REGEX + "\\/pricePlans" ) ,
                                    ((Path) anAnnotation).value() + "/list" );

                            // Handling enable and disable a pricePlan
                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/catalog\\/pricePlans\\/" + CODE_REGEX + ENABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/catalog\\/pricePlans\\/" + CODE_REGEX + DISABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMapWithSpecialURL( CATALOG + "/pricePlans/" + CODE_REGEX + ENABLE_SERVICE, aMapRestful, "pricePlan" );

                            fillUpRestfulURLsMapWithSpecialURL( CATALOG + "/pricePlans/" + CODE_REGEX + DISABLE_SERVICE, aMapRestful, "pricePlan" );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/country" ) ) {
                            MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.put( REST_PATH + "/countries", ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( "/countries", aMapRestful );

                            // Handling enable and disable a trading country
                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/countries\\/" + CODE_REGEX + ENABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/countries\\/" + CODE_REGEX + DISABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMapWithSpecialURL( "/countries/" + CODE_REGEX + ENABLE_SERVICE, aMapRestful, "country" );

                            fillUpRestfulURLsMapWithSpecialURL( "/countries/" + CODE_REGEX + DISABLE_SERVICE, aMapRestful, "country" );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/currency" ) ) {
                            MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.put( REST_PATH + "/currencies", ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( "/currencies", aMapRestful );

                            // Handling enable and disable a trading currency
                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/currencies\\/" + CODE_REGEX + ENABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/currencies\\/" + CODE_REGEX + DISABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMapWithSpecialURL( "/currencies/" + CODE_REGEX + ENABLE_SERVICE, aMapRestful, "currency" );

                            fillUpRestfulURLsMapWithSpecialURL( "/currencies/" + CODE_REGEX + DISABLE_SERVICE, aMapRestful, "currency" );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/jobInstance" ) ) {
                            MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.put( REST_PATH + "/jobInstances", ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( "/jobInstances", aMapRestful );

                            // Handling enable and disable a jobInstance
                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/jobInstances\\/" + CODE_REGEX + ENABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/jobInstances\\/" + CODE_REGEX + DISABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMapWithSpecialURL( "/jobInstances/" + CODE_REGEX + ENABLE_SERVICE, aMapRestful, "jobInstance" );

                            fillUpRestfulURLsMapWithSpecialURL( "/jobInstances/" + CODE_REGEX + DISABLE_SERVICE, aMapRestful, "jobInstance" );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/language" ) ) {
                            MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.put( REST_PATH + "/languages", ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( "/languages", aMapRestful );

                            // Handling enable and disable a trading language
                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/languages\\/" + CODE_REGEX + ENABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/languages\\/" + CODE_REGEX + DISABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMapWithSpecialURL( "/languages/" + CODE_REGEX + ENABLE_SERVICE, aMapRestful, "language" );

                            fillUpRestfulURLsMapWithSpecialURL( "/languages/" + CODE_REGEX + DISABLE_SERVICE, aMapRestful, "language" );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/invoice" ) ) {
                            MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.put( REST_PATH + "/invoices",
                                    ((Path) anAnnotation).value() );

                            MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.put( REST_PATH + "/invoices/generate",
                                    ((Path) anAnnotation).value() + "/generateInvoice" );

                            MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.put( REST_PATH + "/invoices/sendByEmail",
                                    ((Path) anAnnotation).value() + "/sendByEmail" );

                            MAP_SPECIAL_IBASE_RS_PATH_AND_DTO_CLASS.put( ((Path) anAnnotation).value() + "/sendByEmail", InvoiceDto.class );

                            // Handling different services of invoice: cancellation, validation
                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/invoices\\/" + CODE_REGEX + "\\/cancel" ) ,
                                    ((Path) anAnnotation).value() + "/cancel" );

                            MAP_SPECIAL_IBASE_RS_PATH_AND_DTO_CLASS.put( ((Path) anAnnotation).value() + "/cancel", CancelInvoiceRequestDto.class );

                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/invoices\\/" + CODE_REGEX + "\\/validate" ) ,
                                    ((Path) anAnnotation).value() + "/validate" );

                            MAP_SPECIAL_IBASE_RS_PATH_AND_DTO_CLASS.put( ((Path) anAnnotation).value() + "/validate", ValidateInvoiceRequestDto.class );

                            fillUpRestfulURLsMap( "/invoices", aMapRestful );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/billing/invoicing" ) ) {
                            MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.put( REST_PATH + "/billing/invoicings/billingRuns",
                                    ((Path) anAnnotation).value() + "/createBillingRun" );

                            // Handling different services of billing: cancellation of a billing run, validation of a billing run
                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/billing/invoicings/billingRuns\\/" + CODE_REGEX + "\\/cancel" ) ,
                                    ((Path) anAnnotation).value() + "/cancelBillingRun" );

                            MAP_SPECIAL_IBASE_RS_PATH_AND_DTO_CLASS.put( ((Path) anAnnotation).value() + "/cancelBillingRun", CancelBillingRunRequestDto.class );

                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/billing/invoicings/billingRuns\\/" + CODE_REGEX + "\\/validate" ) ,
                                    ((Path) anAnnotation).value() + "/validateBillingRun" );

                            MAP_SPECIAL_IBASE_RS_PATH_AND_DTO_CLASS.put( ((Path) anAnnotation).value() + "/validateBillingRun", ValidateBillingRunRequestDto.class );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/job" ) ) {
                            MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.put( REST_PATH + "/jobs", ((Path) anAnnotation).value() );

                            MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.put( REST_PATH + "/jobs/execute", ((Path) anAnnotation).value() + "/execute" );

                            MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.put( REST_PATH + "/jobs/jobReports", ((Path) anAnnotation).value() + "/jobReport" );

                            MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.put( REST_PATH + "/jobs/timers", ((Path) anAnnotation).value() + "/timer" );

                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/jobs\\/" + CODE_REGEX + STOP_SERVICE ) ,
                                    ((Path) anAnnotation).value() + "/stop" );

                            MAP_SPECIAL_IBASE_RS_PATH_AND_DTO_CLASS.put( ((Path) anAnnotation).value() + "/stop", null );

                            fillUpRestfulURLsMap( "/jobs", aMapRestful );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/PdfInvoice" ) ) {
                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "/pdfInvoices" ), ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( ((Path) anAnnotation).value(), aMapRestful );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/billing/accountingCode" ) ) {
                            MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.put( REST_PATH + BILLING + "/accountingCodes",
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( BILLING + "/accountingCodes", aMapRestful );

                            // Handling enable and disable an accountingCode
                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + BILLING + "\\/accountingCodes\\/" + CODE_REGEX + ENABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + BILLING + "\\/accountingCodes\\/" + CODE_REGEX + DISABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMapWithSpecialURL( BILLING + "/accountingCodes/" + CODE_REGEX + ENABLE_SERVICE, aMapRestful, "accountingCode" );

                            fillUpRestfulURLsMapWithSpecialURL( BILLING + "/accountingCodes/" + CODE_REGEX + DISABLE_SERVICE, aMapRestful, "accountingCode" );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/countryIso" ) ) {
                            MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.put( REST_PATH + "/countriesIso", ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( "/countriesIso", aMapRestful );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/currencyIso" ) ) {
                            MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.put( REST_PATH + "/currenciesIso", ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( "/currenciesIso", aMapRestful );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/languageIso" ) ) {
                            MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.put( REST_PATH + "/languagesIso", ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( "/languagesIso", aMapRestful );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/payment" ) ) {
                            MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.put( REST_PATH + "/payment/paymentMethods",
                                    ((Path) anAnnotation).value() + "/paymentMethod" );

                            fillUpRestfulURLsMap( "/payment/paymentMethods", aMapRestful );

                            // Handling request get list of paymentMethods based on a customerAccountCode
                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/payment\\/customerAccounts\\/" + CODE_REGEX + "\\/paymentMethods" ) ,
                                    ((Path) anAnnotation).value() + "/paymentMethod/findByCustomerAccount" );

                            // Handling enable and disable a paymentMethod
                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/payment\\/paymentMethods\\/" + CODE_REGEX + ENABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() + "/paymentMethod" );

                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/payment\\/paymentMethods\\/" + CODE_REGEX + DISABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() + "/paymentMethod" );

                            fillUpRestfulURLsMapWithSpecialURL( "/payment/paymentMethods/" + CODE_REGEX + ENABLE_SERVICE, aMapRestful, "paymentMethod" );

                            fillUpRestfulURLsMapWithSpecialURL( "/payment/paymentMethods/" + CODE_REGEX + DISABLE_SERVICE, aMapRestful, "paymentMethod" );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/account/providerContact" ) ) {
                            MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.put( REST_PATH + ACCOUNT_MANAGEMENT + "/providerContacts", ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( ACCOUNT_MANAGEMENT + "/providerContacts", aMapRestful );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/account/businessAccountModel" ) ) {
                            MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.put( REST_PATH + ACCOUNT_MANAGEMENT + "/businessAccountModels", ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( ACCOUNT_MANAGEMENT + "/businessAccountModels", aMapRestful );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/catalog/counterTemplate" ) ) {
                            MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.put( REST_PATH + CATALOG + "/counterTemplates", ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( CATALOG + "/counterTemplates", aMapRestful );

                            // Handling enable and disable a counterTemplate
                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/catalog\\/counterTemplates\\/" + CODE_REGEX + ENABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/catalog\\/counterTemplates\\/" + CODE_REGEX + DISABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMapWithSpecialURL( CATALOG + "/counterTemplates/" + CODE_REGEX + ENABLE_SERVICE, aMapRestful, "counterTemplate" );

                            fillUpRestfulURLsMapWithSpecialURL( CATALOG + "/counterTemplates/" + CODE_REGEX + DISABLE_SERVICE, aMapRestful, "counterTemplate" );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/catalog/discountPlan" ) ) {
                            MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.put( REST_PATH + CATALOG + "/discountPlans", ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( CATALOG + "/discountPlans", aMapRestful );

                            // Handling enable and disable a discountPlan
                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/catalog\\/discountPlans\\/" + CODE_REGEX + ENABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/catalog\\/discountPlans\\/" + CODE_REGEX + DISABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMapWithSpecialURL( CATALOG + "/discountPlans/" + CODE_REGEX + ENABLE_SERVICE, aMapRestful, "discountPlan" );

                            fillUpRestfulURLsMapWithSpecialURL( CATALOG + "/discountPlans/" + CODE_REGEX + DISABLE_SERVICE, aMapRestful, "discountPlan" );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/catalog/discountPlanItem" ) ) {
                            MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.put( REST_PATH + CATALOG + "/discountPlanItems", ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( CATALOG + "/discountPlanItems", aMapRestful );

                            // Handling enable and disable a discountPlanItem
                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/catalog\\/discountPlanItems\\/" + CODE_REGEX + ENABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/catalog\\/discountPlanItems\\/" + CODE_REGEX + DISABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMapWithSpecialURL( CATALOG + "/discountPlanItems/" + CODE_REGEX + ENABLE_SERVICE, aMapRestful, "discountPlanItem" );

                            fillUpRestfulURLsMapWithSpecialURL( CATALOG + "/discountPlanItems/" + CODE_REGEX + DISABLE_SERVICE, aMapRestful, "discountPlanItem" );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/catalog/offerTemplateCategory" ) ) {
                            MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.put( REST_PATH + CATALOG + "/offerTemplateCategories", ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( CATALOG + "/offerTemplateCategories", aMapRestful );

                            // Handling enable and disable an offerTemplateCategory
                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/catalog\\/offerTemplateCategories\\/" + CODE_REGEX + ENABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/catalog\\/offerTemplateCategories\\/" + CODE_REGEX + DISABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMapWithSpecialURL( CATALOG + "/offerTemplateCategories/" + CODE_REGEX + ENABLE_SERVICE, aMapRestful, "offerTemplateCategory" );

                            fillUpRestfulURLsMapWithSpecialURL( CATALOG + "/offerTemplateCategories/" + CODE_REGEX + DISABLE_SERVICE, aMapRestful, "offerTemplateCategory" );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/catalogManagement" ) ) {
                            // Handle entity ProductChargeTemplate
                            MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.put( REST_PATH + CATALOG + "/productChargeTemplates", ((Path) anAnnotation).value() + "/productChargeTemplate" );

                            fillUpRestfulURLsMap( CATALOG + "/productChargeTemplates", aMapRestful );

                            // Handling enable and disable a productChargeTemplate
                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/catalog\\/productChargeTemplates\\/" + CODE_REGEX + ENABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() + "/productChargeTemplate" );

                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/catalog\\/productChargeTemplates\\/" + CODE_REGEX + DISABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() + "/productChargeTemplate" );

                            fillUpRestfulURLsMapWithSpecialURL( CATALOG + "/productChargeTemplates/" + CODE_REGEX + ENABLE_SERVICE, aMapRestful, "productChargeTemplate" );

                            fillUpRestfulURLsMapWithSpecialURL( CATALOG + "/productChargeTemplates/" + CODE_REGEX + DISABLE_SERVICE, aMapRestful, "productChargeTemplate" );

                            // Handle entity ProductTemplate
                            MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.put( REST_PATH + CATALOG + "/productTemplates", ((Path) anAnnotation).value() + "/productTemplate" );

                            fillUpRestfulURLsMap( CATALOG + "/productTemplates", aMapRestful );

                            // Handling enable and disable a productTemplate
                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/catalog\\/productTemplates\\/" + CODE_REGEX + ENABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() + "/productTemplate" );

                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/catalog\\/productTemplates\\/" + CODE_REGEX + DISABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() + "/productTemplate" );

                            fillUpRestfulURLsMapWithSpecialURL( CATALOG + "/productTemplates/" + CODE_REGEX + ENABLE_SERVICE, aMapRestful, "productTemplate" );

                            fillUpRestfulURLsMapWithSpecialURL( CATALOG + "/productTemplates/" + CODE_REGEX + DISABLE_SERVICE, aMapRestful, "productTemplate" );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/chart" ) ) {
                            MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.put( REST_PATH + "/charts", ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( "/charts", aMapRestful );

                            // Handling enable and disable a chart
                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/charts\\/" + CODE_REGEX + ENABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/charts\\/" + CODE_REGEX + DISABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMapWithSpecialURL( "/charts/" + CODE_REGEX + ENABLE_SERVICE, aMapRestful, "chart" );

                            fillUpRestfulURLsMapWithSpecialURL( "/charts/" + CODE_REGEX + DISABLE_SERVICE, aMapRestful, "chart" );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/entityCustomization" ) ) {
                            MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.put( REST_PATH + "/entityCustomization/entities",
                                    ((Path) anAnnotation).value() + "/entity" );

                            fillUpRestfulURLsMap( "/entityCustomization/entities", aMapRestful );

                            // Handling enable and disable a customEntity
                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/entityCustomization/entities\\/" + CODE_REGEX + ENABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() + "/entity" );

                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/entityCustomization/entities\\/" + CODE_REGEX + DISABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() + "/entity" );

                            fillUpRestfulURLsMapWithSpecialURL( "/entityCustomization/entities/" + CODE_REGEX + ENABLE_SERVICE, aMapRestful, "entity" );

                            fillUpRestfulURLsMapWithSpecialURL( "/entityCustomization/entities/" + CODE_REGEX + DISABLE_SERVICE, aMapRestful, "entity" );
                        }
                        else if ( ((Path) anAnnotation).value().equals( "/customEntityInstance" ) ) {
                            MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.put( REST_PATH + "/customEntityInstances", ((Path) anAnnotation).value() );

                            fillUpRestfulURLsMap( "/customEntityInstances", aMapRestful );

                            // Handling enable and disable a customEntityInstance
                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/customEntityInstances\\/" + CODE_REGEX + "\\/" + CODE_REGEX + ENABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() + "/entity" );

                            MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.put( Pattern.compile( REST_PATH + "\\/customEntityInstances\\/" + CODE_REGEX + "\\/" + CODE_REGEX + DISABLE_SERVICE ) ,
                                    ((Path) anAnnotation).value() + "/entity" );

                            fillUpRestfulURLsMapWithSpecialURL( "/customEntityInstances/" + CODE_REGEX + ENABLE_SERVICE, aMapRestful, "customEntityInstance" );

                            fillUpRestfulURLsMapWithSpecialURL( "/customEntityInstances/" + CODE_REGEX + DISABLE_SERVICE, aMapRestful, "customEntityInstance" );
                        }
                        else {
                            MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.put(
                                    REST_PATH + Inflector.getInstance().pluralize( ((Path) anAnnotation).value() ),
                                    ((Path) anAnnotation).value() );
                        }
                    }
                }
            }
        }
    }
}
