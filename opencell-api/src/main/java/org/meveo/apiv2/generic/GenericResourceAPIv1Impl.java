package org.meveo.apiv2.generic;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.resteasy.client.jaxrs.BasicAuthentication;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.api.dto.account.AccountHierarchyDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.apiv2.GenericOpencellRestfulAPIv1;
import org.meveo.apiv2.generic.core.GenericHelper;
import org.meveo.apiv2.generic.core.GenericRequestMapper;
import org.meveo.apiv2.generic.services.PersistenceServiceHelper;

import javax.enterprise.context.RequestScoped;
import javax.interceptor.Interceptors;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * @author Thang Nguyen
 */
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class GenericResourceAPIv1Impl implements GenericResourceAPIv1 {

    private static final String METHOD_GET_ALL = "/list";
    private static final String METHOD_GET_AN_ENTITY = "/";
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

    ResteasyClient httpClient;

    private List<PathSegment> segmentsOfPathAPIv2;
    private String entityCode;
    private String pathIBaseRS;
    private String entityClassName;
    private StringBuilder queryParams;
    private static PaginationConfiguration paginationConfig;
    private MultivaluedMap<String, String> queryParamsMap;

    @Context
    private UriInfo uriInfo;

    public GenericResourceAPIv1Impl(){
        BasicAuthentication basicAuthentication = new BasicAuthentication("opencell.admin", "opencell.admin");
        httpClient = new ResteasyClientBuilder().build();
        httpClient.register(basicAuthentication);
    }

    public static PaginationConfiguration getPaginationConfiguration(){
        return paginationConfig;
    }

    /*
     * This request is used to retrieve all entities, or also a particular entity
     */
    @Override
    public Response getAllEntitiesOrGetAnEntity() throws URISyntaxException {
        String getPath = GenericOpencellRestfulAPIv1.API_VERSION + uriInfo.getPath();
        
        segmentsOfPathAPIv2 = uriInfo.getPathSegments();
        StringBuilder suffixPathBuilder = new StringBuilder();
        for (int i = 0; i < segmentsOfPathAPIv2.size() - 1; i++ )
            suffixPathBuilder.append( FORWARD_SLASH + segmentsOfPathAPIv2.get(i).getPath() );
        String pathGetAnEntity = GenericOpencellRestfulAPIv1.API_VERSION + suffixPathBuilder.toString();

        URI redirectURI;

        if ( GenericOpencellRestfulAPIv1.MAP_NEW_PATH_AND_IBASE_RS_PATH.containsKey( getPath ) ) {
            pathIBaseRS = GenericOpencellRestfulAPIv1.MAP_NEW_PATH_AND_IBASE_RS_PATH.get( getPath );
            entityClassName = pathIBaseRS.split( FORWARD_SLASH )[ pathIBaseRS.split( FORWARD_SLASH ).length - 1 ];

            queryParamsMap = uriInfo.getQueryParameters();
//            Class entityClass = GenericHelper.getEntityClass(entityClassName);
            GenericRequestMapper genericRequestMapper = new GenericRequestMapper( this.getClass(), PersistenceServiceHelper.getPersistenceService() );
            paginationConfig = genericRequestMapper.mapTo( GenericPagingAndFilteringUtils.constructImmutableGenericPagingAndFiltering(queryParamsMap) );

            if ( ! queryParamsMap.isEmpty() ) {
                queryParams = new StringBuilder( QUERY_PARAM_SEPARATOR );
                for( String aKey : queryParamsMap.keySet() ){
                    queryParams.append( aKey + QUERY_PARAM_VALUE_SEPARATOR
                            + queryParamsMap.get( aKey ).get(0).replaceAll( BLANK_SPACE, BLANK_SPACE_ENCODED )
                            + PAIR_QUERY_PARAM_SEPARATOR );
                }

                redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                        + API_REST + GenericOpencellRestfulAPIv1.MAP_NEW_PATH_AND_IBASE_RS_PATH.get( getPath )
                        + METHOD_GET_ALL + queryParams.substring( 0, queryParams.length() - 1 ) );
System.out.println( "GET ALL ENTITIES 1 : " + redirectURI.toString() );
            }
            else {
                redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                        + API_REST + GenericOpencellRestfulAPIv1.MAP_NEW_PATH_AND_IBASE_RS_PATH.get( getPath )
                        + METHOD_GET_ALL );
System.out.println( "GET ALL ENTITIES 2 : " + redirectURI.toString() );
            }
            return httpClient.target( redirectURI ).request().get();
        }
        else if ( GenericOpencellRestfulAPIv1.MAP_NEW_PATH_AND_IBASE_RS_PATH.containsKey( pathGetAnEntity ) ) {
            pathIBaseRS = GenericOpencellRestfulAPIv1.MAP_NEW_PATH_AND_IBASE_RS_PATH.get( pathGetAnEntity );
            entityCode = segmentsOfPathAPIv2.get( segmentsOfPathAPIv2.size() - 1 ).getPath();
            entityClassName = pathIBaseRS.split( FORWARD_SLASH )[ pathIBaseRS.split( FORWARD_SLASH ).length - 1 ];
            redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                    + API_REST + pathIBaseRS + METHOD_GET_AN_ENTITY + "?"
                    + entityClassName + "Code=" + entityCode);
System.out.println( "GET AN ENTITY : " + redirectURI.toString() );
            return httpClient.target( redirectURI ).request().get();
        }
        // Handle the special endpoints: get an access point based on a subscriptionCode and an accessCode
        else if ( GenericOpencellRestfulAPIv1.MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.containsKey( getPath ) ) {
            pathIBaseRS = GenericOpencellRestfulAPIv1.MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.get( getPath );
            redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                    + API_REST + pathIBaseRS + QUERY_PARAM_SEPARATOR
                    + "subscriptionCode=" + segmentsOfPathAPIv2.get(segmentsOfPathAPIv2.size() - 3).toString()
                    + PAIR_QUERY_PARAM_SEPARATOR
                    + "accessCode=" + segmentsOfPathAPIv2.get(segmentsOfPathAPIv2.size() - 1).toString() );

System.out.println( "GET redirectURI an AccessPoint : " + redirectURI.toString() );
            return httpClient.target( redirectURI ).request().get();
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
System.out.println( "POST redirectURI CREATE AN ENTITY : " + redirectURI.toString() );

            return httpClient.target( redirectURI )
                    .request(MediaType.APPLICATION_JSON)
                    .post( Entity.entity(jsonDto, MediaType.APPLICATION_JSON) );
        }
        else if ( GenericOpencellRestfulAPIv1.MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.containsKey( postPath ) ) {
            pathIBaseRS = GenericOpencellRestfulAPIv1.MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.get( postPath );
            
            // Handle the generic special endpoint: enable a service
            if ( segmentsOfPathAPIv2.get( segmentsOfPathAPIv2.size() - 1 ).getPath().equals(ENABLE_SERVICE) ) {
                redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                        + API_REST + pathIBaseRS
                        + FORWARD_SLASH + segmentsOfPathAPIv2.get( segmentsOfPathAPIv2.size() - 2 ).getPath()
                        + FORWARD_SLASH + ENABLE_SERVICE + queryParams.substring( 0, queryParams.length() - 1 ) );
                System.out.println( "POST redirectURI ENABLE A SERVICE : " + redirectURI.toString() );
            }
            // Handle the generic special endpoint: disable a service
            else if ( segmentsOfPathAPIv2.get( segmentsOfPathAPIv2.size() - 1 ).getPath().equals(DISABLE_SERVICE) ) {
                redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                        + API_REST + pathIBaseRS
                        + FORWARD_SLASH + segmentsOfPathAPIv2.get( segmentsOfPathAPIv2.size() - 2 ).getPath()
                        + FORWARD_SLASH + DISABLE_SERVICE + queryParams.substring( 0, queryParams.length() - 1 ) );
                System.out.println( "POST redirectURI DISABLE A SERVICE : " + redirectURI.toString() );
            }

            return httpClient.target( redirectURI )
                    .request(MediaType.APPLICATION_JSON)
                    .post( Entity.entity(jsonDto, MediaType.APPLICATION_JSON) );
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

System.out.println( "PUT redirectURI ACTIVATION : " + redirectURI.toString() );
                return httpClient.target( redirectURI )
                        .request(MediaType.APPLICATION_JSON)
                        .put( Entity.json(segmentsOfPathAPIv2.get(segmentsOfPathAPIv2.size() - 2).toString()) );
            }
            // Handle the special endpoint: suspension of a subscription
            else if ( segmentsOfPathAPIv2.get( segmentsOfPathAPIv2.size() - 1 ).getPath().equals(SUSPENSION_SERVICE) ) {
                redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                        + API_REST + pathIBaseRS );

System.out.println( "PUT redirectURI SUSPENSION : " + redirectURI.toString() );
                return httpClient.target( redirectURI )
                        .request(MediaType.APPLICATION_JSON)
                        .put( Entity.entity(jsonDto, MediaType.APPLICATION_JSON) );
            }
            // Handle the special endpoint: termination of a subscription
            else if ( segmentsOfPathAPIv2.get( segmentsOfPathAPIv2.size() - 1 ).getPath().equals(TERMINATION_SERVICE) ) {
                redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                        + API_REST + pathIBaseRS );

System.out.println( "PUT redirectURI TERMINATION : " + redirectURI.toString() );
                return httpClient.target( redirectURI )
                        .request(MediaType.APPLICATION_JSON)
                        .put( Entity.entity(jsonDto, MediaType.APPLICATION_JSON) );
            }
            // Handle the special endpoint: update existing services of a subscription
            else if ( segmentsOfPathAPIv2.get( segmentsOfPathAPIv2.size() - 1 ).getPath().equals(UPDATING_SERVICE) ) {
                redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                        + API_REST + pathIBaseRS );

System.out.println( "PUT redirectURI UPDATING SERVICES : " + redirectURI.toString() );
                return httpClient.target( redirectURI )
                        .request(MediaType.APPLICATION_JSON)
                        .put( Entity.entity(jsonDto, MediaType.APPLICATION_JSON) );
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

            redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                    + API_REST + pathIBaseRS + METHOD_UPDATE );
System.out.println( "PUT redirectURI UPDATE AN ENTITY : " + redirectURI.toString() );

            return httpClient.target( redirectURI )
                    .request(MediaType.APPLICATION_JSON)
                    .put( Entity.entity(aDto, MediaType.APPLICATION_JSON) );
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
System.out.println( "DELETE redirectURI : " + redirectURI.toString() );
            return httpClient.target( redirectURI ).request().delete();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
