package org.meveo.api.restful.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.meveo.api.restful.GenericOpencellRestfulAPIv1;
import org.meveo.api.restful.filter.AuthenticationFilter;
import org.meveo.api.restful.pagingFiltering.PagingAndFilteringRest;
import org.meveo.api.restful.util.GenericPagingAndFilteringUtils;
import org.meveo.apiv2.generic.core.GenericHelper;
import org.meveo.commons.utils.StringUtils;
import org.meveo.util.Inflector;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Thang Nguyen
 */
public class Apiv1GetService {

    private static final String METHOD_GET_ALL = "/list";
    private static final String METHOD_GET_ALL_BIS = "/listGetAll";

    private static final String FORWARD_SLASH = "/";
    private static final String QUERY_PARAM_SEPARATOR = "?";
    private static final String QUERY_PARAM_VALUE_SEPARATOR = "=";
    private static final String PAIR_QUERY_PARAM_SEPARATOR = "&";

    public static final Set<String> SET_GET_ALL = new HashSet<>();

    private List<PathSegment> segmentsOfPathAPIv1;
    private String entityCode;
    private String pathIBaseRS;
    private String entityClassName;
    private StringBuilder queryParams;
    private MultivaluedMap<String, String> queryParamsMap;

    /*
     * Function used to retrieve all entities
     */
    public Response getAllEntities(PagingAndFilteringRest pagingAndFiltering, UriInfo uriInfo, String aGetPath) throws IOException, URISyntaxException {
        URI redirectURI;

        pathIBaseRS = GenericOpencellRestfulAPIv1.MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.get( aGetPath );
        if ( pathIBaseRS.equals( Apiv1ConstantDictionary.WALLET_OPERATION ) )
            entityClassName = "WalletOperation";
        else if ( pathIBaseRS.equals( Apiv1ConstantDictionary.PRICE_PLAN ) )
            entityClassName = "pricePlanMatrix";
        else if ( pathIBaseRS.equals( Apiv1ConstantDictionary.COUNTRY_ISO ) )
            entityClassName = "country";
        else if ( pathIBaseRS.equals( Apiv1ConstantDictionary.CURRENCY_ISO ) )
            entityClassName = "currency";
        else if ( pathIBaseRS.equals( Apiv1ConstantDictionary.LANGUAGE_ISO ) )
            entityClassName = "language";
        else if ( pathIBaseRS.equals( "/job/jobReport" ) )
            entityClassName = "Job";
        else if ( pathIBaseRS.equals( Apiv1ConstantDictionary.TRIGGERED_EDR ) )
            entityClassName = "triggeredEDRTemplate";
        else if ( pathIBaseRS.equals( Apiv1ConstantDictionary.CUSTOM_ENTITY_TEMPLATE ) )
            entityClassName = "customEntityTemplate";
        else
            entityClassName = pathIBaseRS.split( FORWARD_SLASH )[ pathIBaseRS.split( FORWARD_SLASH ).length - 1 ];

        queryParamsMap = uriInfo.getQueryParameters();
        GenericPagingAndFilteringUtils.getInstance().constructPagingAndFiltering(pagingAndFiltering,queryParamsMap);
        Class entityClass = GenericHelper.getEntityClass( Inflector.getInstance().singularize(entityClassName) );
        GenericPagingAndFilteringUtils.getInstance().generatePagingConfig(entityClass);

        if ( ! queryParamsMap.isEmpty() ) {
            queryParams = new StringBuilder( QUERY_PARAM_SEPARATOR );
            for( String aKey : queryParamsMap.keySet() ){
                queryParams.append( aKey + QUERY_PARAM_VALUE_SEPARATOR
                        + queryParamsMap.get( aKey ).get(0).replace( GenericPagingAndFilteringUtils.BLANK_SPACE, GenericPagingAndFilteringUtils.BLANK_SPACE_ENCODED )
                        + PAIR_QUERY_PARAM_SEPARATOR );
            }

            if ( SET_GET_ALL.contains(pathIBaseRS) )
                redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 4 )
                        + pathIBaseRS + METHOD_GET_ALL_BIS
                        + queryParams.substring( 0, queryParams.length() - 1 )
                        .replace( GenericPagingAndFilteringUtils.BLANK_SPACE, GenericPagingAndFilteringUtils.BLANK_SPACE_ENCODED )
                        .replace( GenericPagingAndFilteringUtils.QUOTE, GenericPagingAndFilteringUtils.QUOTE_ENCODED ) );
            else
                redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 4 )
                        + pathIBaseRS + METHOD_GET_ALL
                        + queryParams.substring( 0, queryParams.length() - 1 )
                        .replace( GenericPagingAndFilteringUtils.BLANK_SPACE, GenericPagingAndFilteringUtils.BLANK_SPACE_ENCODED )
                        .replace( GenericPagingAndFilteringUtils.QUOTE, GenericPagingAndFilteringUtils.QUOTE_ENCODED ) );
        }
        else {
            if ( SET_GET_ALL.contains(pathIBaseRS) )
                redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 4 )
                        + pathIBaseRS + METHOD_GET_ALL_BIS );
            else
                redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 4 )
                        + pathIBaseRS + METHOD_GET_ALL );
        }

        Response getResponse = AuthenticationFilter.httpClient.target( redirectURI ).request().get();

        return Response.ok().entity(customizeResponse(getResponse, entityClassName)).build();
    }

    /*
     * Function used to retrieve a particular entity
     */
    public Response getEntity(UriInfo uriInfo, String getAnEntityPath) throws URISyntaxException {
        URI redirectURI;

        segmentsOfPathAPIv1 = uriInfo.getPathSegments();
        pathIBaseRS = GenericOpencellRestfulAPIv1.MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.get( getAnEntityPath );
        entityCode = segmentsOfPathAPIv1.get( segmentsOfPathAPIv1.size() - 1 ).getPath();

        if ( pathIBaseRS.equals(Apiv1ConstantDictionary.USER) ) {
            redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 4 )
                    + pathIBaseRS + QUERY_PARAM_SEPARATOR + "username=" + entityCode);
        }
        // special handle for job
        else if ( pathIBaseRS.equals("/job") ) {
            redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 4 )
                    + pathIBaseRS + QUERY_PARAM_SEPARATOR + "jobInstanceCode=" + entityCode);
        }
        // special handle for jobReport, contact, taxCategory, taxClass
        else if ( pathIBaseRS.equals("/job/jobReport") || pathIBaseRS.equals(Apiv1ConstantDictionary.CONTACT)
                || pathIBaseRS.equals(Apiv1ConstantDictionary.TAX_CATEGORY) || pathIBaseRS.equals(Apiv1ConstantDictionary.TAX_CLASS)
                || pathIBaseRS.equals(Apiv1ConstantDictionary.FILE_FORMAT) || pathIBaseRS.equals(Apiv1ConstantDictionary.EMAIL_TEMPLATE)
                || pathIBaseRS.equals(Apiv1ConstantDictionary.MEVEO_INSTANCE) ) {
            redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 4 )
                    + pathIBaseRS + QUERY_PARAM_SEPARATOR + "code=" + entityCode);
        }
        // special handle for invoice
        else if ( pathIBaseRS.equals(Apiv1ConstantDictionary.INVOICE) ) {
            redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 4 )
                    + pathIBaseRS + QUERY_PARAM_SEPARATOR + "invoiceNumber=" + entityCode);
        }
        // special handle for accountingCode
        else if ( pathIBaseRS.equals(Apiv1ConstantDictionary.ACCOUNTING_CODE) ) {
            redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 4 )
                    + pathIBaseRS + QUERY_PARAM_SEPARATOR + "accountingCode=" + entityCode);
        }
        // special handle for countryIso
        else if ( pathIBaseRS.equals(Apiv1ConstantDictionary.COUNTRY_ISO) ) {
            redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 4 )
                    + pathIBaseRS + QUERY_PARAM_SEPARATOR + "countryCode=" + entityCode);
        }
        // special handle for currencyIso
        else if ( pathIBaseRS.equals(Apiv1ConstantDictionary.CURRENCY_ISO) ) {
            redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 4 )
                    + pathIBaseRS + QUERY_PARAM_SEPARATOR + "currencyCode=" + entityCode);
        }
        // special handle for languageIso
        else if ( pathIBaseRS.equals(Apiv1ConstantDictionary.LANGUAGE_ISO) ) {
            redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 4 )
                    + pathIBaseRS + QUERY_PARAM_SEPARATOR + "languageCode=" + entityCode);
        }
        // special handle for taxMapping, paymentMethod
        else if ( pathIBaseRS.equals(Apiv1ConstantDictionary.TAX_MAPPING) || pathIBaseRS.equals(Apiv1ConstantDictionary.PAYMENT_METHOD) ) {
            redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 4 )
                    + pathIBaseRS + QUERY_PARAM_SEPARATOR + "id=" + entityCode);
        }
        // special handle for productChargeTemplate
        else if ( pathIBaseRS.equals(Apiv1ConstantDictionary.PRODUCT_CHARGE_TEMPLATE) || pathIBaseRS.equals(Apiv1ConstantDictionary.PRODUCT_TEMPLATE)
                || pathIBaseRS.equals(Apiv1ConstantDictionary.CUSTOM_ENTITY_TEMPLATE ) || pathIBaseRS.equals(Apiv1ConstantDictionary.CUSTOMER_CATEGORY ) ) {
            redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 4 )
                    + pathIBaseRS + FORWARD_SLASH + entityCode);
        }
        else {
            entityClassName = pathIBaseRS.split( FORWARD_SLASH )[ pathIBaseRS.split( FORWARD_SLASH ).length - 1 ];
            redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 4 )
                    + pathIBaseRS + QUERY_PARAM_SEPARATOR + entityClassName + "Code=" + entityCode);
        }

        return Response.temporaryRedirect( redirectURI ).build();
    }

    /*
     * Function used to handle get request with regular expressions
     */
    public Response getWithRegex(UriInfo uriInfo, String aGetPath) throws IOException, URISyntaxException {
        URI redirectURI;

        pathIBaseRS = GenericOpencellRestfulAPIv1.MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.get( aGetPath );
        queryParams = new StringBuilder( QUERY_PARAM_SEPARATOR );

        String originalPattern = GenericOpencellRestfulAPIv1.MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.getPattern().toString();
        int indexCodeRegex = originalPattern.indexOf( GenericOpencellRestfulAPIv1.CODE_REGEX );
        String aSmallPattern;
        String smallString = null;

        if ( indexCodeRegex >= 0 ) {
            while ( indexCodeRegex >= 0 ) {
                aSmallPattern = originalPattern.substring( 0,
                        indexCodeRegex + GenericOpencellRestfulAPIv1.CODE_REGEX.length() );

                Matcher matcher = Pattern.compile( aSmallPattern ).matcher( aGetPath );
                // get the first occurrence matching smallStringPattern
                if ( matcher.find() ) {
                    smallString = matcher.group(0);

                    String[] matches = Pattern.compile( GenericOpencellRestfulAPIv1.CODE_REGEX )
                            .matcher( smallString )
                            .results()
                            .map(MatchResult::group)
                            .toArray(String[]::new);

                    if ( pathIBaseRS.equals( "/catalog/pricePlan/list" ) )
                        queryParams.append( "eventCode=" + matches[matches.length - 1] + PAIR_QUERY_PARAM_SEPARATOR );
                    else
                        queryParams.append( Inflector.getInstance().singularize( matches[matches.length - 2] ) + "Code="
                                + matches[matches.length - 1] + PAIR_QUERY_PARAM_SEPARATOR );
                }

                indexCodeRegex = originalPattern.indexOf( GenericOpencellRestfulAPIv1.CODE_REGEX, indexCodeRegex + 1 );
            }

            // If smallString differs from the string aGetPath, the request is to retrieve all entities, so we add paging and filtering
            // Otherwise, if smallString is exactly the string aGetPath, the request is to retrieve a particular entity
            if ( ! smallString.equals( aGetPath ) ) {
                queryParamsMap = uriInfo.getQueryParameters();
                GenericPagingAndFilteringUtils.getInstance().constructPagingAndFiltering(queryParamsMap);

                if ( pathIBaseRS.equals( "/catalog/pricePlan/list" ) )
                    entityClassName = "PricePlanMatrix";
                else
                    entityClassName = aGetPath.split( FORWARD_SLASH )[ aGetPath.split( FORWARD_SLASH ).length - 1 ];

                Class entityClass = GenericHelper.getEntityClass( Inflector.getInstance().singularize( entityClassName ) );
                GenericPagingAndFilteringUtils.getInstance().generatePagingConfig(entityClass);
            }

            redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 4 )
                    + pathIBaseRS + queryParams.substring( 0, queryParams.length() - 1 ) );
        }
        else {
            queryParams.append( uriInfo.getRequestUri().getQuery() );

            redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 4 )
                    + pathIBaseRS + queryParams );
        }

        Response getResponse = AuthenticationFilter.httpClient.target( redirectURI ).request().get();

        return Response.ok().entity(customizeResponse(getResponse, entityClassName)).build();
    }

    /*
     * Function used to handle remaining cases
     */
    public Response handleOther(UriInfo uriInfo, String aGetPath) throws URISyntaxException {
        URI redirectURI;
        segmentsOfPathAPIv1 = uriInfo.getPathSegments();

        if ( aGetPath.matches( "/api/rest/v1/invoices/pdfInvoices/" + GenericOpencellRestfulAPIv1.CODE_REGEX ) ) {
            entityCode = segmentsOfPathAPIv1.get( segmentsOfPathAPIv1.size() - 1 ).getPath();
            redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 4 )
                    + "/invoice/getPdfInvoice" + QUERY_PARAM_SEPARATOR + "invoiceNumber=" + entityCode );
            return Response.temporaryRedirect( redirectURI ).build();
        }
        else if ( aGetPath.matches( "/api/rest/v1/invoices/xmlInvoices/" + GenericOpencellRestfulAPIv1.CODE_REGEX ) ) {
            entityCode = segmentsOfPathAPIv1.get( segmentsOfPathAPIv1.size() - 1 ).getPath();
            redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 4 )
                    + "/invoice/getXMLInvoice" + QUERY_PARAM_SEPARATOR + "invoiceNumber=" + entityCode );
            return Response.temporaryRedirect( redirectURI ).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    /*
     * This function is used to format the response from the requests get all entities
     */
    public Map<String, Object> customizeResponse(Response getResponse, String entityName ) throws IOException {
        Map<String, Object> customResponse = new LinkedHashMap<>();
        if ( getResponse.hasEntity() ) {
            Object anEntity = getResponse.getEntity();
            Map<String, Object> origResponse = new ObjectMapper().readValue( (InputStream) anEntity, Map.class );

            for (Map.Entry<String,Object> entry : origResponse.entrySet()) {
                if ( entry.getKey().equals("actionStatus") || entry.getKey().equals("paging") )
                    customResponse.put(entry.getKey(), entry.getValue());
                else if ( entry.getKey().equals( "pricePlanMatrixes" ) ) {
                    if ( entry.getValue() instanceof Map ) {
                        Map mapEntities = (Map) entry.getValue();
                        for (Object aKey : mapEntities.keySet()) {
                            customResponse.put( "pricePlanMatrices", mapEntities.get(aKey) );
                        }
                    }
                }
                else if ( entry.getKey().equals( "list" + StringUtils.capitalizeFirstLetter(entityName) )
                        || entry.getKey().equals( entityName ) || entry.getKey().equals( "dto" )
                        || entry.getKey().equals( Inflector.getInstance().pluralize(entityName) )
                        || entry.getKey().equals( Inflector.getInstance().pluralize(entityName) + "Dto" ) ) {
                    if ( entry.getValue() instanceof Map ) {
                        Map mapEntities = (Map) entry.getValue();
                        for (Object aKey : mapEntities.keySet()) {
                            if ( aKey.equals( Inflector.getInstance().singularize(entityName) ) ||
                                    aKey.equals( Inflector.getInstance().pluralize(entityName) ) ||
                                    aKey.equals( entityName ) )
                                if ( CollectionUtils.isNotEmpty((List) mapEntities.get(aKey)) )
                                    customResponse.put( Inflector.getInstance().pluralize(entityName), mapEntities.get(aKey) );
                        }
                    }
                    else if ( entry.getValue() instanceof List )
                        if ( CollectionUtils.isNotEmpty((List) entry.getValue() ) )
                            customResponse.put( Inflector.getInstance().pluralize(entityName), entry.getValue() );
                }
                else
                    customResponse.put(entry.getKey(), entry.getValue());
            }
        }

        return customResponse;
    }

}
