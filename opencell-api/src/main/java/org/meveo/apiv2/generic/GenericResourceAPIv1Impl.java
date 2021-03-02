package org.meveo.apiv2.generic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.api.dto.account.AccessDto;
import org.meveo.api.dto.account.AccountHierarchyDto;
import org.meveo.api.dto.billing.ActivateSubscriptionRequestDto;
import org.meveo.api.dto.billing.OperationSubscriptionRequestDto;
import org.meveo.api.dto.billing.TerminateSubscriptionRequestDto;
import org.meveo.api.dto.billing.UpdateServicesRequestDto;
import org.meveo.apiv2.GenericOpencellRestfulAPIv1;
import org.meveo.apiv2.generic.core.GenericHelper;
import org.meveo.util.Inflector;

import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.interceptor.Interceptors;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
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
    private static final String METHOD_UPDATE = "/";
    private static final String METHOD_DELETE = "/";

    private static final String FORWARD_SLASH = "/";
    private static final String BLANK_SPACE = " ";
    private static final String BLANK_SPACE_ENCODED = "%20";
    private static final String QUERY_PARAM_SEPARATOR = "?";
    private static final String QUERY_PARAM_VALUE_SEPARATOR = "=";
    private static final String PAIR_QUERY_PARAM_SEPARATOR = "&";
    private static final String DTO_SUFFIX = "dto";

    // static final string for services
    private static final String ENABLE_SERVICE = "enable";
    private static final String DISABLE_SERVICE = "disable";
    private static final String ACTIVATION_SERVICE = "activation";
    private static final String SUSPENSION_SERVICE = "suspension";
    private static final String TERMINATION_SERVICE = "termination";
    private static final String UPDATING_SERVICE = "services";

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
    public Response getAllEntitiesOrGetAnEntity() throws URISyntaxException, JsonProcessingException {
        String getPath = GenericOpencellRestfulAPIv1.API_VERSION + uriInfo.getPath();

        segmentsOfPathAPIv2 = uriInfo.getPathSegments();
        StringBuilder suffixPathBuilder = new StringBuilder();
        for (int i = 0; i < segmentsOfPathAPIv2.size() - 1; i++ )
            suffixPathBuilder.append( FORWARD_SLASH + segmentsOfPathAPIv2.get(i).getPath() );
        String pathGetAnEntity = GenericOpencellRestfulAPIv1.API_VERSION + suffixPathBuilder.toString();

        URI redirectURI;

        if ( GenericOpencellRestfulAPIv1.MAP_NEW_PATH_AND_IBASE_RS_PATH.containsKey( getPath ) ) {
            pathIBaseRS = GenericOpencellRestfulAPIv1.MAP_NEW_PATH_AND_IBASE_RS_PATH.get( getPath );
            if ( pathIBaseRS.equals( "/billing/wallet/operation" ) )
                entityClassName = "WalletOperation";
            else if ( pathIBaseRS.equals( "/catalog/pricePlan" ) )
                entityClassName = "PricePlanMatrix";
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
                            + queryParamsMap.get( aKey ).get(0).replaceAll( BLANK_SPACE, BLANK_SPACE_ENCODED )
                            + PAIR_QUERY_PARAM_SEPARATOR );
                }

                if ( pathIBaseRS.contains( "oneShotChargeTemplate" ) || pathIBaseRS.contains( "/account/customer" )
                    || pathIBaseRS.contains( "/billing/subscription" ) || pathIBaseRS.contains( "/billing/ratedTransaction" )
                    || pathIBaseRS.contains( "/billing/wallet" ) || pathIBaseRS.contains( "/catalog/offerTemplate" ) )
                    redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                            + API_REST + pathIBaseRS + METHOD_GET_ALL_BIS
                            + queryParams.substring( 0, queryParams.length() - 1 ).replaceAll( BLANK_SPACE, BLANK_SPACE_ENCODED ) );
                else
                    redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                            + API_REST + pathIBaseRS + METHOD_GET_ALL
                            + queryParams.substring( 0, queryParams.length() - 1 ).replaceAll( BLANK_SPACE, BLANK_SPACE_ENCODED ) );
            }
            else {
                if ( pathIBaseRS.contains( "oneShotChargeTemplate" ) || pathIBaseRS.contains( "/account/customer" )
                    || pathIBaseRS.contains( "/billing/subscription" ) || pathIBaseRS.contains( "/billing/ratedTransaction" )
                    || pathIBaseRS.contains( "/billing/wallet" ) || pathIBaseRS.contains( "/catalog/offerTemplate" ) )
                    redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                            + API_REST + pathIBaseRS + METHOD_GET_ALL_BIS );
                else
                    redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                            + API_REST + pathIBaseRS + METHOD_GET_ALL );
            }
            return Response.temporaryRedirect( redirectURI ).build();
        }
        else if ( GenericOpencellRestfulAPIv1.MAP_NEW_PATH_AND_IBASE_RS_PATH.containsKey( pathGetAnEntity ) ) {
            pathIBaseRS = GenericOpencellRestfulAPIv1.MAP_NEW_PATH_AND_IBASE_RS_PATH.get( pathGetAnEntity );
            entityCode = segmentsOfPathAPIv2.get( segmentsOfPathAPIv2.size() - 1 ).getPath();

            // special handle for customerCategory
            if ( pathIBaseRS.equals("/account/customer/category") ) {
                redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                        + API_REST + pathIBaseRS + FORWARD_SLASH + entityCode);
            }
            else {
                entityClassName = pathIBaseRS.split( FORWARD_SLASH )[ pathIBaseRS.split( FORWARD_SLASH ).length - 1 ];
                redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                        + API_REST + pathIBaseRS + QUERY_PARAM_SEPARATOR + entityClassName + "Code=" + entityCode);
            }
            return Response.temporaryRedirect( redirectURI ).build();
        }
        else if ( GenericOpencellRestfulAPIv1.MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.containsKey( getPath ) ) {
            pathIBaseRS = GenericOpencellRestfulAPIv1.MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.get( getPath );
            queryParams = new StringBuilder( QUERY_PARAM_SEPARATOR );

            String originalPattern = GenericOpencellRestfulAPIv1.MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.getPattern().toString();
            int indexCodeRegex = originalPattern.indexOf( GenericOpencellRestfulAPIv1.CODE_REGEX );
            String aSmallPattern;
            String smallString = null;
            while ( indexCodeRegex >= 0 ) {
                aSmallPattern = originalPattern.substring( 0,
                        indexCodeRegex + GenericOpencellRestfulAPIv1.CODE_REGEX.length() );

                Matcher matcher = Pattern.compile( aSmallPattern ).matcher( getPath );
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

            // If smallString differs from the string getPath, the request is to retrieve all entities, so we add paging and filtering
            // Otherwise, if smallString is exactly the string getPath, the request is to retrieve a particular entity
            if ( ! smallString.equals( getPath ) ) {
                queryParamsMap = uriInfo.getQueryParameters();
                GenericPagingAndFilteringUtils.getInstance().constructPagingAndFiltering(queryParamsMap);
                entityClassName = getPath.split( FORWARD_SLASH )[ getPath.split( FORWARD_SLASH ).length - 1 ];
                Class entityClass = GenericHelper.getEntityClass( Inflector.getInstance().singularize( entityClassName ) );
                GenericPagingAndFilteringUtils.getInstance().generatePagingConfig(entityClass);
            }

            redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                    + API_REST + pathIBaseRS + queryParams.substring( 0, queryParams.length() - 1 ) );

            return Response.temporaryRedirect( redirectURI ).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
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
                    + queryParamsMap.get( aKey ).get(0).replaceAll( BLANK_SPACE, BLANK_SPACE_ENCODED )
                    + PAIR_QUERY_PARAM_SEPARATOR );
        }

        if ( GenericOpencellRestfulAPIv1.MAP_NEW_PATH_AND_IBASE_RS_PATH.containsKey( postPath ) ) {
            pathIBaseRS = GenericOpencellRestfulAPIv1.MAP_NEW_PATH_AND_IBASE_RS_PATH.get( postPath );

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

        return Response.status(Response.Status.NOT_FOUND).build();
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

            // Handle the special endpoint: activation of a subscription
            if ( segmentsOfPathAPIv2.get( segmentsOfPathAPIv2.size() - 1 ).getPath().equals(ACTIVATION_SERVICE) ) {
                redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                        + API_REST + pathIBaseRS );
                ActivateSubscriptionRequestDto aDto = new ActivateSubscriptionRequestDto();
                aDto.setSubscriptionCode( segmentsOfPathAPIv2.get(segmentsOfPathAPIv2.size() - 2).toString() );

                return AuthenticationFilter.httpClient.target( redirectURI ).request()
                        .put( Entity.entity(aDto, MediaType.APPLICATION_JSON) );
            }
            // Handle the special endpoint: suspension of a subscription
            else if ( segmentsOfPathAPIv2.get( segmentsOfPathAPIv2.size() - 1 ).getPath().equals(SUSPENSION_SERVICE) ) {
                redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                        + API_REST + pathIBaseRS );
                OperationSubscriptionRequestDto aDto = new OperationSubscriptionRequestDto();
                aDto.setSubscriptionCode( segmentsOfPathAPIv2.get(segmentsOfPathAPIv2.size() - 2).toString() );

                return AuthenticationFilter.httpClient.target( redirectURI ).request()
                        .put( Entity.entity(aDto, MediaType.APPLICATION_JSON) );
            }
            // Handle the special endpoint: termination of a subscription
            else if ( segmentsOfPathAPIv2.get( segmentsOfPathAPIv2.size() - 1 ).getPath().equals(TERMINATION_SERVICE) ) {
                redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                        + API_REST + pathIBaseRS );
                Object aDto = new ObjectMapper().readValue( jsonDto, TerminateSubscriptionRequestDto.class );
                ((TerminateSubscriptionRequestDto) aDto).setSubscriptionCode( segmentsOfPathAPIv2.get(segmentsOfPathAPIv2.size() - 2).toString() );

                return AuthenticationFilter.httpClient.target( redirectURI ).request()
                        .put( Entity.entity(aDto, MediaType.APPLICATION_JSON) );
            }
            // Handle the special endpoint: update existing services of a subscription
            else if ( segmentsOfPathAPIv2.get( segmentsOfPathAPIv2.size() - 1 ).getPath().equals(UPDATING_SERVICE) ) {
                redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                        + API_REST + pathIBaseRS );
                Object aDto = new ObjectMapper().readValue( jsonDto, UpdateServicesRequestDto.class );
                ((UpdateServicesRequestDto) aDto).setSubscriptionCode( segmentsOfPathAPIv2.get(segmentsOfPathAPIv2.size() - 2).toString() );

                return AuthenticationFilter.httpClient.target( redirectURI ).request()
                        .put( Entity.entity(aDto, MediaType.APPLICATION_JSON) );
            }
        }
        else if ( GenericOpencellRestfulAPIv1.MAP_NEW_PATH_AND_IBASE_RS_PATH.containsKey( pathUpdateAnEntity ) ) {
            pathIBaseRS = GenericOpencellRestfulAPIv1.MAP_NEW_PATH_AND_IBASE_RS_PATH.get( pathUpdateAnEntity );
            entityClassName = pathIBaseRS.split( FORWARD_SLASH )[ pathIBaseRS.split( FORWARD_SLASH ).length - 1 ];

            Class entityDtoClass = GenericHelper.getEntityDtoClass( entityClassName.toLowerCase() + DTO_SUFFIX );
            entityCode = segmentsOfPathAPIv2.get( segmentsOfPathAPIv2.size() - 1 ).getPath();

            Object aDto = new ObjectMapper().readValue( jsonDto, entityDtoClass );
            if ( aDto instanceof BusinessEntityDto ) {
                ((BusinessEntityDto) aDto).setCode(entityCode);
            }
            else if ( aDto instanceof AccountHierarchyDto) {
                ((AccountHierarchyDto) aDto).setCustomerCode(entityCode);
            }
            else if ( aDto instanceof AccessDto) {
                ((AccessDto) aDto).setCode(entityCode);
            }

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
        StringBuilder suffixPathBuilder = new StringBuilder();
        for (int i = 0; i < segmentsOfPathAPIv2.size() - 1; i++ )
            suffixPathBuilder.append( FORWARD_SLASH + segmentsOfPathAPIv2.get(i).getPath() );
        String deletePath = GenericOpencellRestfulAPIv1.API_VERSION + suffixPathBuilder.toString();

        if ( GenericOpencellRestfulAPIv1.MAP_NEW_PATH_AND_IBASE_RS_PATH.containsKey( deletePath ) ) {
            pathIBaseRS = GenericOpencellRestfulAPIv1.MAP_NEW_PATH_AND_IBASE_RS_PATH.get( deletePath );
            entityCode = segmentsOfPathAPIv2.get( segmentsOfPathAPIv2.size() - 1 ).getPath();
            URI redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                    + API_REST + pathIBaseRS + METHOD_DELETE
                    + entityCode);
            return Response.temporaryRedirect( redirectURI ).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PreDestroy
    public void destroy() {
        AuthenticationFilter.httpClient.close();
    }
}
