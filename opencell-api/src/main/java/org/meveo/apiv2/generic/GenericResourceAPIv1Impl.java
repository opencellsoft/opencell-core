package org.meveo.apiv2.generic;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.meveo.api.dto.*;
import org.meveo.api.dto.account.AccessDto;
import org.meveo.api.dto.account.AccountHierarchyDto;
import org.meveo.api.dto.billing.*;
import org.meveo.api.dto.invoice.CancelInvoiceRequestDto;
import org.meveo.api.dto.invoice.ValidateInvoiceRequestDto;
import org.meveo.apiv2.GenericOpencellRestfulAPIv1;
import org.meveo.apiv2.generic.core.GenericHelper;
import org.meveo.util.Inflector;

import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.interceptor.Interceptors;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Thang Nguyen
 */
@RequestScoped
@Interceptors({ AuthenticationFilter.class })
public class GenericResourceAPIv1Impl implements GenericResourceAPIv1 {

    private static final String METHOD_GET_ALL = "/list";
    private static final String METHOD_GET_ALL_BIS = "/listGetAll";
    private static final String METHOD_CREATE = "/";
    private static final String METHOD_CREATE_BIS = "/create";
    private static final String METHOD_UPDATE = "/";
    private static final String METHOD_DELETE = "/";

    private static final String FORWARD_SLASH = "/";
    private static final String QUERY_PARAM_SEPARATOR = "?";
    private static final String QUERY_PARAM_VALUE_SEPARATOR = "=";
    private static final String PAIR_QUERY_PARAM_SEPARATOR = "&";
    private static final String DTO_SUFFIX = "dto";

    // static final string for services
    private static final String ENABLE_SERVICE = "enable";
    private static final String DISABLE_SERVICE = "disable";

    private static final String API_REST = "api/rest";

    private List<PathSegment> segmentsOfPathAPIv2;
    private String entityCode;
    private String pathIBaseRS;
    private String entityClassName;
    private StringBuilder queryParams;
    private MultivaluedMap<String, String> queryParamsMap;

    @Context
    private UriInfo uriInfo;

    @Context
    private HttpHeaders headers;

    /*
     * This request is used to retrieve all entities, or also a particular entity
     */
    @Override
    public Response getAllEntitiesOrGetAnEntity() throws URISyntaxException, IOException {
        String aGetPath = GenericOpencellRestfulAPIv1.API_VERSION + uriInfo.getPath();

        segmentsOfPathAPIv2 = uriInfo.getPathSegments();
        StringBuilder suffixPathBuilder = new StringBuilder();
        for (int i = 0; i < segmentsOfPathAPIv2.size() - 1; i++ )
            suffixPathBuilder.append( FORWARD_SLASH + segmentsOfPathAPIv2.get(i).getPath() );
        String getAnEntityPath = GenericOpencellRestfulAPIv1.API_VERSION + suffixPathBuilder.toString();

        URI redirectURI;

        // to get all entities
        if ( GenericOpencellRestfulAPIv1.MAP_NEW_PATH_AND_IBASE_RS_PATH.containsKey( aGetPath ) ) {
            pathIBaseRS = GenericOpencellRestfulAPIv1.MAP_NEW_PATH_AND_IBASE_RS_PATH.get( aGetPath );
            if ( pathIBaseRS.equals( "/billing/wallet/operation" ) )
                entityClassName = "WalletOperation";
            else if ( pathIBaseRS.equals( "/catalog/pricePlan" ) )
                entityClassName = "PricePlanMatrix";
            else if ( pathIBaseRS.equals( "/countryIso" ) )
                entityClassName = "Country";
            else if ( pathIBaseRS.equals( "/currencyIso" ) )
                entityClassName = "Currency";
            else if ( pathIBaseRS.equals( "/languageIso" ) )
                entityClassName = "Language";
            else if ( pathIBaseRS.equals( "/job/jobReport" ) )
                entityClassName = "Job";
            else
                entityClassName = pathIBaseRS.split( FORWARD_SLASH )[ pathIBaseRS.split( FORWARD_SLASH ).length - 1 ];

            queryParamsMap = uriInfo.getQueryParameters();
            GenericPagingAndFilteringUtils.getInstance().constructPagingAndFiltering(queryParamsMap);
            Class entityClass = GenericHelper.getEntityClass( Inflector.getInstance().singularize(entityClassName) );
            GenericPagingAndFilteringUtils.getInstance().generatePagingConfig(entityClass);

            if ( ! queryParamsMap.isEmpty() ) {
                queryParams = new StringBuilder( QUERY_PARAM_SEPARATOR );
                for( String aKey : queryParamsMap.keySet() ){
                    queryParams.append( aKey + QUERY_PARAM_VALUE_SEPARATOR
                            + queryParamsMap.get( aKey ).get(0).replace( GenericPagingAndFilteringUtils.BLANK_SPACE, GenericPagingAndFilteringUtils.BLANK_SPACE_ENCODED )
                            + PAIR_QUERY_PARAM_SEPARATOR );
                }

                if ( pathIBaseRS.equals( "/catalog/oneShotChargeTemplate" ) || pathIBaseRS.equals( "/catalog/recurringChargeTemplate" )
                    || pathIBaseRS.equals( "/account/customer" )
                    || pathIBaseRS.equals( "/billing/subscription" ) || pathIBaseRS.equals( "/billing/ratedTransaction" )
                    || pathIBaseRS.equals( "/billing/wallet" ) || pathIBaseRS.equals( "/catalog/offerTemplate")
                    || pathIBaseRS.equals( "/user" ) || pathIBaseRS.equals( "/invoice" )
                    || pathIBaseRS.equals( "/billing/accountingCode" ) || pathIBaseRS.equals( "/calendar" )
                    || pathIBaseRS.equals( "/catalog/unitOfMeasure" ) || pathIBaseRS.equals( "/contact" )
                    || pathIBaseRS.equals( "/countryIso" ) || pathIBaseRS.equals( "/currencyIso" )
                    || pathIBaseRS.equals( "/languageIso" ) || pathIBaseRS.equals( "/tax" )
                    || pathIBaseRS.equals( "/taxCategory" ) || pathIBaseRS.equals( "/taxClass" )
                    || pathIBaseRS.equals( "/taxMapping" ) || pathIBaseRS.equals( "/payment/creditCategory" )
                    || pathIBaseRS.equals( "/payment/paymentMethod" ) || pathIBaseRS.equals( "/account/title" )
                    || pathIBaseRS.equals( "/account/customerAccount" ) || pathIBaseRS.equals( "/account/billingAccount" )
                    || pathIBaseRS.equals( "/account/userAccount" ) || pathIBaseRS.equals( "/catalog/serviceTemplate" ) )
                    redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                            + API_REST + pathIBaseRS + METHOD_GET_ALL_BIS
                            + queryParams.substring( 0, queryParams.length() - 1 )
                            .replace( GenericPagingAndFilteringUtils.BLANK_SPACE, GenericPagingAndFilteringUtils.BLANK_SPACE_ENCODED )
                            .replace( GenericPagingAndFilteringUtils.OPEN_ACCOLADE, GenericPagingAndFilteringUtils.OPEN_ACCOLADE_ENCODED )
                            .replace( GenericPagingAndFilteringUtils.CLOSE_ACCOLADE, GenericPagingAndFilteringUtils.CLOSE_ACCOLADE_ENCODED )
                            .replace( GenericPagingAndFilteringUtils.QUOTE, GenericPagingAndFilteringUtils.QUOTE_ENCODED ) );
                else
                    redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                            + API_REST + pathIBaseRS + METHOD_GET_ALL
                            + queryParams.substring( 0, queryParams.length() - 1 )
                            .replace( GenericPagingAndFilteringUtils.BLANK_SPACE, GenericPagingAndFilteringUtils.BLANK_SPACE_ENCODED )
                            .replace( GenericPagingAndFilteringUtils.OPEN_ACCOLADE, GenericPagingAndFilteringUtils.OPEN_ACCOLADE_ENCODED )
                            .replace( GenericPagingAndFilteringUtils.CLOSE_ACCOLADE, GenericPagingAndFilteringUtils.CLOSE_ACCOLADE_ENCODED )
                            .replace( GenericPagingAndFilteringUtils.QUOTE, GenericPagingAndFilteringUtils.QUOTE_ENCODED ) );
            }
            else {
                if ( pathIBaseRS.equals( "/catalog/oneShotChargeTemplate" ) || pathIBaseRS.equals( "/catalog/recurringChargeTemplate" )
                    || pathIBaseRS.equals( "/account/customer" )
                    || pathIBaseRS.equals( "/billing/subscription" ) || pathIBaseRS.equals( "/billing/ratedTransaction" )
                    || pathIBaseRS.equals( "/billing/wallet" ) || pathIBaseRS.equals( "/catalog/offerTemplate" )
                    || pathIBaseRS.equals( "/user" ) || pathIBaseRS.equals( "/invoice" )
                    || pathIBaseRS.equals( "/billing/accountingCode" ) || pathIBaseRS.equals( "/calendar" )
                    || pathIBaseRS.equals( "/catalog/unitOfMeasure" ) || pathIBaseRS.equals( "/contact" )
                    || pathIBaseRS.equals( "/countryIso" ) || pathIBaseRS.equals( "/currencyIso" )
                    || pathIBaseRS.equals( "/languageIso" ) || pathIBaseRS.equals( "/tax" )
                    || pathIBaseRS.equals( "/taxCategory" ) || pathIBaseRS.equals( "/taxClass" )
                    || pathIBaseRS.equals( "/taxMapping" ) || pathIBaseRS.equals( "/payment/creditCategory" )
                    || pathIBaseRS.equals( "/payment/paymentMethod" ) || pathIBaseRS.equals( "/account/title" )
                    || pathIBaseRS.equals( "/account/customerAccount" ) || pathIBaseRS.equals( "/account/billingAccount" )
                    || pathIBaseRS.equals( "/account/userAccount" ) || pathIBaseRS.equals( "/catalog/serviceTemplate" ) )
                    redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                            + API_REST + pathIBaseRS + METHOD_GET_ALL_BIS );
                else
                    redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                            + API_REST + pathIBaseRS + METHOD_GET_ALL );
            }

            Response getResponse = AuthenticationFilter.httpClient.target( redirectURI ).request().get();

            return Response.ok().entity(customizeResponse(getResponse, entityClassName)).build();
        }
        else if ( GenericOpencellRestfulAPIv1.MAP_NEW_PATH_AND_IBASE_RS_PATH.containsKey( getAnEntityPath ) ) {
            pathIBaseRS = GenericOpencellRestfulAPIv1.MAP_NEW_PATH_AND_IBASE_RS_PATH.get( getAnEntityPath );
            entityCode = segmentsOfPathAPIv2.get( segmentsOfPathAPIv2.size() - 1 ).getPath();

            // special handle for customerCategory
            if ( pathIBaseRS.equals("/account/customer/category") ) {
                redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                        + API_REST + pathIBaseRS + FORWARD_SLASH + entityCode);
            }
            // special handle for user
            else if ( pathIBaseRS.equals("/user") ) {
                redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                        + API_REST + pathIBaseRS + QUERY_PARAM_SEPARATOR + "username=" + entityCode);
            }
            // special handle for job
            else if ( pathIBaseRS.equals("/job") ) {
                redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                        + API_REST + pathIBaseRS + QUERY_PARAM_SEPARATOR + "jobInstanceCode=" + entityCode);
            }
            // special handle for jobReport, contact, taxCategory, taxClass
            else if ( pathIBaseRS.equals("/job/jobReport") || pathIBaseRS.equals("/contact")
                    || pathIBaseRS.equals("/taxCategory") || pathIBaseRS.equals("/taxClass")) {
                redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                        + API_REST + pathIBaseRS + QUERY_PARAM_SEPARATOR + "code=" + entityCode);
            }
            // special handle for invoice
            else if ( pathIBaseRS.equals("/invoice") ) {
                redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                        + API_REST + pathIBaseRS + QUERY_PARAM_SEPARATOR + "invoiceNumber=" + entityCode);
            }
            // special handle for accountingCode
            else if ( pathIBaseRS.equals("/billing/accountingCode") ) {
                redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                        + API_REST + pathIBaseRS + QUERY_PARAM_SEPARATOR + "accountingCode=" + entityCode);
            }
            // special handle for countryIso
            else if ( pathIBaseRS.equals("/countryIso") ) {
                redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                        + API_REST + pathIBaseRS + QUERY_PARAM_SEPARATOR + "countryCode=" + entityCode);
            }
            // special handle for currencyIso
            else if ( pathIBaseRS.equals("/currencyIso") ) {
                redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                        + API_REST + pathIBaseRS + QUERY_PARAM_SEPARATOR + "currencyCode=" + entityCode);
            }
            // special handle for languageIso
            else if ( pathIBaseRS.equals("/languageIso") ) {
                redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                        + API_REST + pathIBaseRS + QUERY_PARAM_SEPARATOR + "languageCode=" + entityCode);
            }
            // special handle for taxMapping, paymentMethod
            else if ( pathIBaseRS.equals("/taxMapping") || pathIBaseRS.equals("/payment/paymentMethod") ) {
                redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                        + API_REST + pathIBaseRS + QUERY_PARAM_SEPARATOR + "id=" + entityCode);
            }
            else {
                entityClassName = pathIBaseRS.split( FORWARD_SLASH )[ pathIBaseRS.split( FORWARD_SLASH ).length - 1 ];
                redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                        + API_REST + pathIBaseRS + QUERY_PARAM_SEPARATOR + entityClassName + "Code=" + entityCode);
            }
            return Response.temporaryRedirect( redirectURI ).build();
        }
        // to handle get requests containing regular expressions
        else if ( GenericOpencellRestfulAPIv1.MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.containsKey( aGetPath ) ) {
            pathIBaseRS = GenericOpencellRestfulAPIv1.MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.get( aGetPath );
            queryParams = new StringBuilder( QUERY_PARAM_SEPARATOR );

            String originalPattern = GenericOpencellRestfulAPIv1.MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.getPattern().toString();
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
                    entityClassName = aGetPath.split( FORWARD_SLASH )[ aGetPath.split( FORWARD_SLASH ).length - 1 ];
                    Class entityClass = GenericHelper.getEntityClass( Inflector.getInstance().singularize( entityClassName ) );
                    GenericPagingAndFilteringUtils.getInstance().generatePagingConfig(entityClass);
                }

                redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                        + API_REST + pathIBaseRS + queryParams.substring( 0, queryParams.length() - 1 ) );
            }
            else {
                queryParams.append( uriInfo.getRequestUri().getQuery() );

                redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                        + API_REST + pathIBaseRS + queryParams );
            }

            Response getResponse = AuthenticationFilter.httpClient.target( redirectURI ).request().get();

            return Response.ok().entity(customizeResponse(getResponse, entityClassName)).build();
        }
        else {
            if ( aGetPath.matches( "/v1/invoices/pdfInvoices/" + GenericOpencellRestfulAPIv1.CODE_REGEX ) ) {
                entityCode = segmentsOfPathAPIv2.get( segmentsOfPathAPIv2.size() - 1 ).getPath();
                redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                        + API_REST + "/invoice/getPdfInvoice" + QUERY_PARAM_SEPARATOR + "invoiceNumber=" + entityCode );
                return Response.temporaryRedirect( redirectURI ).build();
            }
            else if ( aGetPath.matches( "/v1/invoices/xmlInvoices/" + GenericOpencellRestfulAPIv1.CODE_REGEX ) ) {
                entityCode = segmentsOfPathAPIv2.get( segmentsOfPathAPIv2.size() - 1 ).getPath();
                redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                        + API_REST + "/invoice/getXMLInvoice" + QUERY_PARAM_SEPARATOR + "invoiceNumber=" + entityCode );
                return Response.temporaryRedirect( redirectURI ).build();
            }

            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    // Concerns only responses for requests get all entities
    public Map<String, Object> customizeResponse( Response getResponse, String entityName ) throws IOException {
        Map<String, Object> customResponse = new LinkedHashMap<>();
        if ( getResponse.hasEntity() ) {
            Object aResponse2 = getResponse.getEntity();
            Map<String, Object> origResponse = new ObjectMapper().readValue( (InputStream) aResponse2, Map.class );

            for (Map.Entry<String,Object> entry : origResponse.entrySet()) {
                if ( entry.getKey().equals("actionStatus") || entry.getKey().equals("paging") )
                    customResponse.put(entry.getKey(), entry.getValue());
                else if ( entry.getKey().equals( Inflector.getInstance().pluralize(entityName) ) ) {
                    if ( entry.getValue() instanceof Map ) {
                        Map mapEntities = (Map) entry.getValue();
                        for (Object aKey : mapEntities.keySet()) {
                            if ( aKey.equals( Inflector.getInstance().singularize(entityName) ) ||
                                aKey.equals( Inflector.getInstance().pluralize(entityName) ) ||
                                aKey.equals( entityName ) )
                                customResponse.put( entry.getKey(), mapEntities.get(aKey) );
                        }
                    }
                }
                else
                    customResponse.put(entry.getKey(), entry.getValue());
            }
        }

        return customResponse;
    }

    @Override
    public Response postRequest( String jsonDto ) throws URISyntaxException {
        String postPath = GenericOpencellRestfulAPIv1.API_VERSION + uriInfo.getPath();
        URI redirectURI = null;
        segmentsOfPathAPIv2 = uriInfo.getPathSegments();
        queryParamsMap = uriInfo.getQueryParameters();
        queryParams = new StringBuilder( QUERY_PARAM_SEPARATOR );
        for( String aKey : queryParamsMap.keySet() ){
            queryParams.append( aKey + QUERY_PARAM_VALUE_SEPARATOR
                    + queryParamsMap.get( aKey ).get(0).replace( GenericPagingAndFilteringUtils.BLANK_SPACE, GenericPagingAndFilteringUtils.BLANK_SPACE_ENCODED )
                    + PAIR_QUERY_PARAM_SEPARATOR );
        }

        if ( GenericOpencellRestfulAPIv1.MAP_NEW_PATH_AND_IBASE_RS_PATH.containsKey( postPath ) ) {
            pathIBaseRS = GenericOpencellRestfulAPIv1.MAP_NEW_PATH_AND_IBASE_RS_PATH.get( postPath );

            if ( pathIBaseRS.equals( "/jobInstance" ) || pathIBaseRS.equals( "/job" ) )
                redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                        + API_REST + pathIBaseRS + METHOD_CREATE_BIS );
            else if ( pathIBaseRS.equals( "/invoice/sendByEmail" ) )
                redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                        + API_REST + pathIBaseRS );
            else
                redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                        + API_REST + pathIBaseRS + METHOD_CREATE );

            return Response.temporaryRedirect( redirectURI )
                    .entity( Entity.entity(jsonDto, MediaType.APPLICATION_JSON) ).build();
        }
        else if ( GenericOpencellRestfulAPIv1.MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.containsKey( postPath ) ) {
            pathIBaseRS = GenericOpencellRestfulAPIv1.MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.get( postPath );

            // Handle the generic special endpoint: enable a service
            if ( segmentsOfPathAPIv2.get( segmentsOfPathAPIv2.size() - 1 ).getPath().equals(ENABLE_SERVICE) ) {
                redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                        + API_REST + pathIBaseRS
                        + FORWARD_SLASH + segmentsOfPathAPIv2.get( segmentsOfPathAPIv2.size() - 2 ).getPath()
                        + FORWARD_SLASH + ENABLE_SERVICE + queryParams.substring( 0, queryParams.length() - 1 ) );
            }
            // Handle the generic special endpoint: disable a service
            else if ( segmentsOfPathAPIv2.get( segmentsOfPathAPIv2.size() - 1 ).getPath().equals(DISABLE_SERVICE) ) {
                redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                        + API_REST + pathIBaseRS
                        + FORWARD_SLASH + segmentsOfPathAPIv2.get( segmentsOfPathAPIv2.size() - 2 ).getPath()
                        + FORWARD_SLASH + DISABLE_SERVICE + queryParams.substring( 0, queryParams.length() - 1 ) );
            }

            return Response.temporaryRedirect( redirectURI )
                    .entity( Entity.entity(jsonDto, MediaType.APPLICATION_JSON) ).build();
        }
        else {
            if ( postPath.equals( "/v1/invoices/pdfInvoices" ) ) {
                redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                        + API_REST + "/invoice/fetchPdfInvoice" );
                return Response.temporaryRedirect( redirectURI )
                        .entity( Entity.entity(jsonDto, MediaType.APPLICATION_JSON) ).build();
            }
            else if ( postPath.equals( "/v1/invoices/xmlInvoices" ) ) {
                redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                        + API_REST + "/invoice/fetchXMLInvoice" );
                return Response.temporaryRedirect( redirectURI )
                        .entity( Entity.entity(jsonDto, MediaType.APPLICATION_JSON) ).build();
            }

            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Override
    public Response putRequest( String jsonDto ) throws URISyntaxException, IOException {
        String putPath = GenericOpencellRestfulAPIv1.API_VERSION + uriInfo.getPath();
        URI redirectURI;
        segmentsOfPathAPIv2 = uriInfo.getPathSegments();
        StringBuilder suffixPathBuilder = new StringBuilder();
        for ( int i = 0; i < segmentsOfPathAPIv2.size() - 1; i++ )
            suffixPathBuilder.append( FORWARD_SLASH + segmentsOfPathAPIv2.get(i).getPath() );
        String pathUpdateAnEntity = GenericOpencellRestfulAPIv1.API_VERSION + suffixPathBuilder.toString();

        if ( GenericOpencellRestfulAPIv1.MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.containsKey( putPath ) ) {
            pathIBaseRS = GenericOpencellRestfulAPIv1.MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.get( putPath );

            Class entityDtoClass = GenericOpencellRestfulAPIv1.MAP_SPECIAL_IBASE_RS_PATH_AND_DTO_CLASS.get( pathIBaseRS );
            Object aDto = null;

            if ( entityDtoClass != null ) {
                aDto = new ObjectMapper().readValue( jsonDto, entityDtoClass );

                // Handle the special endpoint, such as: activation, suspension, termination, update services of a subscription,
                // cancel/validate an existing invoice, cancel/validate a billing run, send an existing invoice by email
                redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                        + API_REST + pathIBaseRS );

                if ( aDto instanceof ActivateSubscriptionRequestDto )
                    ((ActivateSubscriptionRequestDto) aDto).setSubscriptionCode( segmentsOfPathAPIv2.get(segmentsOfPathAPIv2.size() - 2).toString() );
                else if ( aDto instanceof OperationSubscriptionRequestDto )
                    ((OperationSubscriptionRequestDto) aDto).setSubscriptionCode( segmentsOfPathAPIv2.get(segmentsOfPathAPIv2.size() - 2).toString() );
                else if ( aDto instanceof TerminateSubscriptionRequestDto )
                    ((TerminateSubscriptionRequestDto) aDto).setSubscriptionCode( segmentsOfPathAPIv2.get(segmentsOfPathAPIv2.size() - 2).toString() );
                else if ( aDto instanceof UpdateServicesRequestDto )
                    ((UpdateServicesRequestDto) aDto).setSubscriptionCode( segmentsOfPathAPIv2.get(segmentsOfPathAPIv2.size() - 2).toString() );
                else if ( aDto instanceof CancelInvoiceRequestDto )
                    ((CancelInvoiceRequestDto) aDto).setInvoiceId( Long.parseLong(segmentsOfPathAPIv2.get(segmentsOfPathAPIv2.size() - 2).toString()) );
                else if ( aDto instanceof CancelBillingRunRequestDto )
                    ((CancelBillingRunRequestDto) aDto).setBillingRunId( Long.parseLong(segmentsOfPathAPIv2.get(segmentsOfPathAPIv2.size() - 2).toString()) );
                else if ( aDto instanceof ValidateInvoiceRequestDto )
                    ((ValidateInvoiceRequestDto) aDto).setInvoiceId( Long.parseLong(segmentsOfPathAPIv2.get(segmentsOfPathAPIv2.size() - 2).toString()) );
                else if ( aDto instanceof ValidateBillingRunRequestDto )
                    ((ValidateBillingRunRequestDto) aDto).setBillingRunId( Long.parseLong(segmentsOfPathAPIv2.get(segmentsOfPathAPIv2.size() - 2).toString()) );
            }
            else {
                // Handle the special endpoint, such as: stop a job
                redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                        + API_REST + pathIBaseRS + FORWARD_SLASH + segmentsOfPathAPIv2.get(segmentsOfPathAPIv2.size() - 2).toString() );
            }

            return AuthenticationFilter.httpClient.target( redirectURI ).request()
                    .put( Entity.entity(aDto, MediaType.APPLICATION_JSON) );
        }
        else if ( GenericOpencellRestfulAPIv1.MAP_NEW_PATH_AND_IBASE_RS_PATH.containsKey( pathUpdateAnEntity ) ) {
            pathIBaseRS = GenericOpencellRestfulAPIv1.MAP_NEW_PATH_AND_IBASE_RS_PATH.get( pathUpdateAnEntity );
            entityClassName = pathIBaseRS.split( FORWARD_SLASH )[ pathIBaseRS.split( FORWARD_SLASH ).length - 1 ];

            if ( entityClassName.equals( "job" ) )
                entityClassName = "jobInstance";
            else if ( entityClassName.equals( "timer" ) )
                entityClassName = "timerEntity";

            Class entityDtoClass = GenericHelper.getEntityDtoClass( entityClassName.toLowerCase() + DTO_SUFFIX );
            entityCode = segmentsOfPathAPIv2.get( segmentsOfPathAPIv2.size() - 1 ).getPath();
            Object aDto = new ObjectMapper().readValue( jsonDto, entityDtoClass );
            if ( aDto instanceof BusinessEntityDto )
                ((BusinessEntityDto) aDto).setCode(entityCode);
            else if ( aDto instanceof IEntityDto )
                ((IEntityDto) aDto).setId(Long.parseLong(entityCode));
            else if ( aDto instanceof AccountHierarchyDto )
                ((AccountHierarchyDto) aDto).setCustomerCode(entityCode);
            else if ( aDto instanceof AccessDto )
                ((AccessDto) aDto).setCode(entityCode);
            else if ( aDto instanceof LanguageDto )
                ((LanguageDto) aDto).setCode(entityCode);
            else if ( aDto instanceof CountryDto )
                ((CountryDto) aDto).setCountryCode(entityCode);
            else if ( aDto instanceof CurrencyDto )
                ((CurrencyDto) aDto).setCode(entityCode);
            else if ( aDto instanceof UserDto )
                ((UserDto) aDto).setUsername(entityCode);

            redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                    + API_REST + pathIBaseRS + METHOD_UPDATE );

            return AuthenticationFilter.httpClient.target( redirectURI ).request()
                    .put( Entity.entity( aDto, MediaType.APPLICATION_JSON ) );
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @Override
    public Response deleteAnEntity() throws URISyntaxException {
        segmentsOfPathAPIv2 = uriInfo.getPathSegments();
        URI redirectURI;
        StringBuilder suffixPathBuilder = new StringBuilder();
        for (int i = 0; i < segmentsOfPathAPIv2.size() - 1; i++ )
            suffixPathBuilder.append( FORWARD_SLASH + segmentsOfPathAPIv2.get(i).getPath() );
        String deletePath = GenericOpencellRestfulAPIv1.API_VERSION + suffixPathBuilder.toString();

        if ( GenericOpencellRestfulAPIv1.MAP_NEW_PATH_AND_IBASE_RS_PATH.containsKey( deletePath ) ) {
            pathIBaseRS = GenericOpencellRestfulAPIv1.MAP_NEW_PATH_AND_IBASE_RS_PATH.get( deletePath );
            entityCode = segmentsOfPathAPIv2.get( segmentsOfPathAPIv2.size() - 1 ).getPath();

            if ( pathIBaseRS.equals( "/payment/paymentMethod" ) )
                redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                        + API_REST + pathIBaseRS + QUERY_PARAM_SEPARATOR + "id=" + entityCode);
            else
                redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                        + API_REST + pathIBaseRS + METHOD_DELETE + entityCode);
            return Response.temporaryRedirect( redirectURI ).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @Override
    public Response getListRestfulURLs() {
        return Response.ok().entity(GenericOpencellRestfulAPIv1.RESTFUL_ENTITIES_LIST).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    @PreDestroy
    public void destroy() {
        AuthenticationFilter.httpClient.close();
    }
}
