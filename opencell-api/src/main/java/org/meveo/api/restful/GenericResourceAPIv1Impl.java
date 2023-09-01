package org.meveo.api.restful;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.restful.filter.AuthenticationFilter;
import org.meveo.api.restful.pagingFiltering.PagingAndFilteringRest;
import org.meveo.api.restful.services.Apiv1DeleteService;
import org.meveo.api.restful.services.Apiv1GetService;
import org.meveo.api.restful.services.Apiv1PostService;
import org.meveo.api.restful.services.Apiv1PutService;
import org.meveo.api.restful.util.GenericPagingAndFilteringUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.util.Version;

import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Thang Nguyen
 */
@RequestScoped
@Interceptors({ AuthenticationFilter.class, WsRestApiInterceptor.class })
public class GenericResourceAPIv1Impl implements GenericResourceAPIv1 {

    @Inject
    private Apiv1GetService getService;

    @Inject
    private Apiv1PostService postService;

    @Inject
    private Apiv1PutService putService;

    @Inject
    private Apiv1DeleteService deleteService;

    private static final String FORWARD_SLASH = "/";
    private static final String QUERY_PARAM_SEPARATOR = "?";
    private static final String QUERY_PARAM_VALUE_SEPARATOR = "=";
    private static final String PAIR_QUERY_PARAM_SEPARATOR = "&";

    private List<PathSegment> segmentsOfPathAPIv1;
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
    public Response getRequest( PagingAndFilteringRest pagingAndFiltering ) throws URISyntaxException, IOException {
        String aGetPath = GenericOpencellRestfulAPIv1.REST_PATH + uriInfo.getPath();

        segmentsOfPathAPIv1 = uriInfo.getPathSegments();
        StringBuilder suffixPathBuilder = new StringBuilder();
        for (int i = 0; i < segmentsOfPathAPIv1.size() - 1; i++ )
            suffixPathBuilder.append( FORWARD_SLASH + segmentsOfPathAPIv1.get(i).getPath() );
        String getAnEntityPath = GenericOpencellRestfulAPIv1.REST_PATH + suffixPathBuilder;

        // to get all entities
        if ( GenericOpencellRestfulAPIv1.MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.containsKey( aGetPath ) ) {
            return getService.getAllEntities( pagingAndFiltering, uriInfo, aGetPath );
        }
        // to get a particular entity
        else if ( GenericOpencellRestfulAPIv1.MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.containsKey( getAnEntityPath ) ) {
            return getService.getEntity( uriInfo, getAnEntityPath );
        }
        // to handle get requests containing regular expressions
        else if ( GenericOpencellRestfulAPIv1.MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.containsKey( aGetPath ) ) {
            return getService.getWithRegex( uriInfo, aGetPath );
        }
        else {
            return getService.handleOther( uriInfo, aGetPath );
        }
    }

    @Override
    public Response postRequest( String jsonDto ) throws URISyntaxException, IOException {
        String postPath = GenericOpencellRestfulAPIv1.REST_PATH + uriInfo.getPath();
        segmentsOfPathAPIv1 = uriInfo.getPathSegments();
        queryParamsMap = uriInfo.getQueryParameters();
        queryParams = new StringBuilder( QUERY_PARAM_SEPARATOR );
        for( String aKey : queryParamsMap.keySet() ){
            queryParams.append( aKey + QUERY_PARAM_VALUE_SEPARATOR
                    + queryParamsMap.get( aKey ).get(0).replace( GenericPagingAndFilteringUtils.BLANK_SPACE, GenericPagingAndFilteringUtils.BLANK_SPACE_ENCODED )
                    + PAIR_QUERY_PARAM_SEPARATOR );
        }

        if ( GenericOpencellRestfulAPIv1.MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.containsKey( postPath ) ) {
            return postService.createEntity( uriInfo, postPath, jsonDto );
        }
        else if ( GenericOpencellRestfulAPIv1.MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.containsKey( postPath ) ) {
            return postService.enableOrDisableEntity( uriInfo, postPath, jsonDto );
        }
        else {
            return postService.handleOther( uriInfo, postPath, jsonDto );
        }
    }

    @Override
    public Response putRequest( String jsonDto ) throws URISyntaxException, IOException {
        String putPath = GenericOpencellRestfulAPIv1.REST_PATH + uriInfo.getPath();
        segmentsOfPathAPIv1 = uriInfo.getPathSegments();
        StringBuilder suffixPathBuilder = new StringBuilder();
        for (int i = 0; i < segmentsOfPathAPIv1.size() - 1; i++ )
            suffixPathBuilder.append( FORWARD_SLASH + segmentsOfPathAPIv1.get(i).getPath() );
        String pathUpdateAnEntity = GenericOpencellRestfulAPIv1.REST_PATH + suffixPathBuilder;

        if ( GenericOpencellRestfulAPIv1.MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.containsKey( putPath ) ) {
            return putService.updateService( uriInfo, putPath, jsonDto );
        }
        else if ( GenericOpencellRestfulAPIv1.MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.containsKey( pathUpdateAnEntity ) ) {
            return putService.updateEntity( uriInfo, pathUpdateAnEntity, jsonDto );
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @Override
    public Response deleteRequest() throws URISyntaxException, IOException {
        return deleteService.deleteEntity(uriInfo);
    }

    @Override
    public Response postCreationOrUpdate( String jsonDto ) throws JsonProcessingException, URISyntaxException {
        return postService.postCreationOrUpdate(uriInfo, jsonDto);
    }

    @Override
    public Response getListRestEndpoints() {
        return Response.ok().entity(GenericOpencellRestfulAPIv1.RESTFUL_ENTITIES_MAP).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    @Override
    public Response getListRestEndpointsForEntity(String entityName) {
        if ( GenericOpencellRestfulAPIv1.RESTFUL_ENTITIES_MAP.containsKey( StringUtils.capitalizeFirstLetter(entityName) ) ) {
            entityName = StringUtils.capitalizeFirstLetter(entityName);
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put( entityName, GenericOpencellRestfulAPIv1.RESTFUL_ENTITIES_MAP.get( entityName ) );
            return Response.ok().entity(responseMap).type(MediaType.APPLICATION_JSON_TYPE).build();
        }
        else {
            ActionStatus notFoundStatus = new ActionStatus(ActionStatusEnum.FAIL,
                    MeveoApiErrorCodeEnum.ENTITY_DOES_NOT_EXISTS_EXCEPTION, "Entity " + entityName + " cannot be found");
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(notFoundStatus).type(MediaType.APPLICATION_JSON_TYPE).build();
        }
    }

    @Override
    public Response getApiVersion() {
        ActionStatus successfulStatus = new ActionStatus(ActionStatusEnum.SUCCESS,
                "Opencell core version " + Version.appVersion + ", Opencell Rest API version " + GenericOpencellRestfulAPIv1.REST_PATH.substring(1)
                        + ", commit " + Version.buildNumber + " , build at " + Version.build_time);

        return Response.status(Response.Status.OK).entity(successfulStatus).build();
    }

    @PreDestroy
    public void destroy() {
        AuthenticationFilter.httpClient.close();
    }
}
