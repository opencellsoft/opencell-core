package org.meveo.apiv2.generic;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.resteasy.client.jaxrs.BasicAuthentication;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.api.dto.IEntityDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.apiv2.GenericOpencellRestfulAPIv2;
import org.meveo.apiv2.generic.core.GenericHelper;

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
public class GenericResourceAPIv2Impl implements GenericResourceAPIv2 {

    private static final String METHOD_GET_ALL = "/list";
    private static final String METHOD_GET_AN_ENTITY = "/";
    private static final String METHOD_CREATE = "/";
    private static final String METHOD_UPDATE = "/";
    private static final String METHOD_DELETE = "/";

    private static final String FORWARD_SLASH = "/";
    private static final String DTO_SUFFIX = "dto";

    private static final String API_REST = "api/rest";

    ResteasyClient httpClient;

    private List<PathSegment> segmentsOfPathAPIv2;
    private String anEntityCode;
    private String pathIBaseRS;
    private String entityClassName;

    @Context
    private UriInfo uriInfo;

    public GenericResourceAPIv2Impl(){
        BasicAuthentication basicAuthentication = new BasicAuthentication("opencell.admin", "opencell.admin");
        httpClient = new ResteasyClientBuilder().build();
        httpClient.register(basicAuthentication);
    }

    @Override
    public Response getAllEntitiesOrGetAnEntity() throws URISyntaxException {
        segmentsOfPathAPIv2 = uriInfo.getPathSegments();
        StringBuilder suffixPathBuilder = new StringBuilder();
        for (int i = 0; i < segmentsOfPathAPIv2.size() - 1; i++ )
            suffixPathBuilder.append( FORWARD_SLASH + segmentsOfPathAPIv2.get(i).getPath() );
        String pathGetAnEntity = GenericOpencellRestfulAPIv2.API_VERSION + suffixPathBuilder.toString();
        String pathGetAllEntities = GenericOpencellRestfulAPIv2.API_VERSION + uriInfo.getPath();

        URI redirectURI;

        if ( GenericOpencellRestfulAPIv2.MAP_NEW_PATH_AND_PATH_IBASE_RS.containsKey( pathGetAllEntities ) ) {
            redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                                        + API_REST + GenericOpencellRestfulAPIv2.MAP_NEW_PATH_AND_PATH_IBASE_RS.get( pathGetAllEntities )
                                        + METHOD_GET_ALL );
System.out.println( "GET ALL ENTITIES : " + redirectURI.getPath() );
            return httpClient.target( redirectURI ).request().get();
        }
        else if ( GenericOpencellRestfulAPIv2.MAP_NEW_PATH_AND_PATH_IBASE_RS.containsKey( pathGetAnEntity ) ) {
            pathIBaseRS = GenericOpencellRestfulAPIv2.MAP_NEW_PATH_AND_PATH_IBASE_RS.get( pathGetAnEntity );
            anEntityCode = segmentsOfPathAPIv2.get( segmentsOfPathAPIv2.size() - 1 ).getPath();
            entityClassName = pathIBaseRS.split( FORWARD_SLASH )[ pathIBaseRS.split( FORWARD_SLASH ).length - 1 ];
            redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                    + API_REST + pathIBaseRS + METHOD_GET_AN_ENTITY + "?"
                    + entityClassName + "Code=" + anEntityCode);
System.out.println( "GET AN ENTITY : " + redirectURI.getPath() );
            return httpClient.target( redirectURI ).request().get();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @Override
    public Response createAnEntity( String jsonDto ) throws URISyntaxException {
        String pathCreateAnEntity = GenericOpencellRestfulAPIv2.API_VERSION + uriInfo.getPath();

        if ( GenericOpencellRestfulAPIv2.MAP_NEW_PATH_AND_PATH_IBASE_RS.containsKey( pathCreateAnEntity ) ) {
            URI redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                    + API_REST + GenericOpencellRestfulAPIv2.MAP_NEW_PATH_AND_PATH_IBASE_RS.get( pathCreateAnEntity )
                    + METHOD_CREATE );
System.out.println( "CREATE AN ENTITY : " + redirectURI.getPath() );

            return httpClient.target( redirectURI )
                    .request(MediaType.APPLICATION_JSON)
                    .post( Entity.entity(jsonDto, MediaType.APPLICATION_JSON) );
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @Override
    public Response updateAnEntity( String jsonDto ) throws URISyntaxException, IOException {
        segmentsOfPathAPIv2 = uriInfo.getPathSegments();
        StringBuilder suffixPathBuilder = new StringBuilder();
        for (int i = 0; i < segmentsOfPathAPIv2.size() - 1; i++ )
            suffixPathBuilder.append( FORWARD_SLASH + segmentsOfPathAPIv2.get(i).getPath() );
        String pathUpdateAnEntity = GenericOpencellRestfulAPIv2.API_VERSION + suffixPathBuilder.toString();

        if ( GenericOpencellRestfulAPIv2.MAP_NEW_PATH_AND_PATH_IBASE_RS.containsKey( pathUpdateAnEntity ) ) {
            pathIBaseRS = GenericOpencellRestfulAPIv2.MAP_NEW_PATH_AND_PATH_IBASE_RS.get( pathUpdateAnEntity );
            entityClassName = pathIBaseRS.split( FORWARD_SLASH )[ pathIBaseRS.split( FORWARD_SLASH ).length - 1 ];

            Class entityDtoClass = GenericHelper.getEntityDtoClass( entityClassName.toLowerCase() + DTO_SUFFIX );
            anEntityCode = segmentsOfPathAPIv2.get( segmentsOfPathAPIv2.size() - 1 ).getPath();

            IEntityDto anEntityDto = (IEntityDto) new ObjectMapper().readValue( jsonDto, entityDtoClass );
            if ( anEntityDto instanceof BusinessEntityDto ) {
                ((BusinessEntityDto) anEntityDto).setCode( anEntityCode );
            }

            pathIBaseRS = GenericOpencellRestfulAPIv2.MAP_NEW_PATH_AND_PATH_IBASE_RS.get( pathUpdateAnEntity );
            URI redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                    + API_REST + pathIBaseRS + METHOD_UPDATE );
System.out.println( "UPDATE AN ENTITY : " + redirectURI.getPath() );

            return httpClient.target( redirectURI )
                    .request(MediaType.APPLICATION_JSON)
                    .put( Entity.entity(anEntityDto, MediaType.APPLICATION_JSON) );
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @Override
    public Response deleteAnEntity( String jsonDto ) throws URISyntaxException {
        segmentsOfPathAPIv2 = uriInfo.getPathSegments();
        StringBuilder suffixPathBuilder = new StringBuilder();
        for (int i = 0; i < segmentsOfPathAPIv2.size() - 1; i++ )
            suffixPathBuilder.append( FORWARD_SLASH + segmentsOfPathAPIv2.get(i).getPath() );
        String pathDeleteAnEntity = GenericOpencellRestfulAPIv2.API_VERSION + suffixPathBuilder.toString();

        if ( GenericOpencellRestfulAPIv2.MAP_NEW_PATH_AND_PATH_IBASE_RS.containsKey( pathDeleteAnEntity ) ) {
            pathIBaseRS = GenericOpencellRestfulAPIv2.MAP_NEW_PATH_AND_PATH_IBASE_RS.get( pathDeleteAnEntity );
            anEntityCode = segmentsOfPathAPIv2.get( segmentsOfPathAPIv2.size() - 1 ).getPath();
            URI redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                    + API_REST + pathIBaseRS + METHOD_DELETE
                    + anEntityCode);
System.out.println( "new URI redirect DELETE AN ENTITY : " + redirectURI.getPath() );
            return httpClient.target( redirectURI ).request().delete();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
