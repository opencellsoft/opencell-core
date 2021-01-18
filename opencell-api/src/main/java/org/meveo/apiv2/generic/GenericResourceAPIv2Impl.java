package org.meveo.apiv2.generic;

import org.jboss.resteasy.client.jaxrs.BasicAuthentication;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.apiv2.GenericOpencellRestfulAPIv2;

import javax.enterprise.context.RequestScoped;
import javax.interceptor.Interceptors;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class GenericResourceAPIv2Impl implements GenericResourceAPIv2 {

    private static final String METHOD_GET_ALL = "/list";
    private static final String METHOD_GET_AN_ENTITY = "/";
    private static final String METHOD_CREATE = "/";
    private static final String METHOD_UPDATE = "/";
    private static final String METHOD_DELETE = "/";

    private static final String FORWARD_SLASH = "/";

    private static final String API_REST = "api/rest";

    ResteasyClient httpClient;

    @Context
    private UriInfo uriInfo;

    public GenericResourceAPIv2Impl(){
        BasicAuthentication basicAuthentication = new BasicAuthentication("opencell.admin", "opencell.admin");
        httpClient = new ResteasyClientBuilder().build();
        httpClient.register(basicAuthentication);
    }

    @Override
    public Response getAllEntitiesOrGetAnEntity() throws URISyntaxException {
        List<PathSegment> segmentsOfNewPath = uriInfo.getPathSegments();
        StringBuilder suffixPathBuilder = new StringBuilder();
        for ( int i = 0; i < segmentsOfNewPath.size() - 1; i++ )
            suffixPathBuilder.append( FORWARD_SLASH + segmentsOfNewPath.get(i).getPath() );
        String pathGetAnEntity = GenericOpencellRestfulAPIv2.API_VERSION + suffixPathBuilder.toString();
        String pathGetAllEntities = GenericOpencellRestfulAPIv2.API_VERSION + uriInfo.getPath();

        URI redirectURI;

        if ( GenericOpencellRestfulAPIv2.MAP_NEW_PATH_AND_PATH_IBASE_RS.containsKey( pathGetAllEntities ) ) {
            redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                                        + API_REST + GenericOpencellRestfulAPIv2.MAP_NEW_PATH_AND_PATH_IBASE_RS.get( pathGetAllEntities )
                                        + METHOD_GET_ALL );
//            return httpClient.target( redirectURI ).request().get();
            return Response.seeOther( redirectURI ).build();
        }
        else if ( GenericOpencellRestfulAPIv2.MAP_NEW_PATH_AND_PATH_IBASE_RS.containsKey( pathGetAnEntity ) ) {
            String pathIBaseRS = GenericOpencellRestfulAPIv2.MAP_NEW_PATH_AND_PATH_IBASE_RS.get( pathGetAnEntity );
            String[] segmentsPathIBaseRS = pathIBaseRS.split( FORWARD_SLASH );
            redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                    + API_REST + pathIBaseRS + METHOD_GET_AN_ENTITY + "?"
                    + segmentsPathIBaseRS[segmentsPathIBaseRS.length - 1] + "Code=" + segmentsOfNewPath.get( segmentsOfNewPath.size() - 1 ) );
System.out.println( "new URI redirect GET AN ENTITY : " + redirectURI.getPath() );
            return Response.seeOther( redirectURI ).build();
        }

//        for ( Map.Entry<String,String> entry : GenericOpencellRestfulAPIv2.MAP_PATH_AND_INTERFACE_IBASE_RS.entrySet() )
//            System.out.println("Key = " + entry.getKey() +
//                    ", Value = " + entry.getValue());

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @Override
    public Response createAnEntity( String jsonDto ) throws URISyntaxException {
        String pathCreateAnEntity = GenericOpencellRestfulAPIv2.API_VERSION + uriInfo.getPath();

        if ( GenericOpencellRestfulAPIv2.MAP_NEW_PATH_AND_PATH_IBASE_RS.containsKey( pathCreateAnEntity ) ) {
            URI redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                    + API_REST + GenericOpencellRestfulAPIv2.MAP_NEW_PATH_AND_PATH_IBASE_RS.get( pathCreateAnEntity )
                    + METHOD_CREATE );
System.out.println( "new URI redirect createAnEntity : " + redirectURI.getPath() );

            return httpClient.target( redirectURI )
                    .request(MediaType.APPLICATION_JSON)
                    .post( Entity.entity(jsonDto, MediaType.APPLICATION_JSON) );
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @Override
    public Response updateAnEntity( String jsonDto ) throws URISyntaxException {
        List<PathSegment> segmentsOfNewPath = uriInfo.getPathSegments();
        StringBuilder suffixPathBuilder = new StringBuilder();
        for ( int i = 0; i < segmentsOfNewPath.size() - 1; i++ )
            suffixPathBuilder.append( FORWARD_SLASH + segmentsOfNewPath.get(i).getPath() );
        String pathUpdateAnEntity = GenericOpencellRestfulAPIv2.API_VERSION + suffixPathBuilder.toString();

        if ( GenericOpencellRestfulAPIv2.MAP_NEW_PATH_AND_PATH_IBASE_RS.containsKey( pathUpdateAnEntity ) ) {
            String pathIBaseRS = GenericOpencellRestfulAPIv2.MAP_NEW_PATH_AND_PATH_IBASE_RS.get( pathUpdateAnEntity );
            String[] segmentsPathIBaseRS = pathIBaseRS.split( FORWARD_SLASH );
            URI redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                    + API_REST + pathIBaseRS + METHOD_UPDATE + "?"
                    + segmentsPathIBaseRS[segmentsPathIBaseRS.length - 1] + "Code=" + segmentsOfNewPath.get( segmentsOfNewPath.size() - 1 ) );
System.out.println( "new URI redirect updateAnEntity : " + redirectURI.getPath() );

            return httpClient.target( redirectURI )
                    .request(MediaType.APPLICATION_JSON)
                    .put( Entity.entity(jsonDto, MediaType.APPLICATION_JSON) );
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @Override
    public Response deleteAnEntity( String jsonDto ) throws URISyntaxException {
        List<PathSegment> segmentsOfNewPath = uriInfo.getPathSegments();
        StringBuilder suffixPathBuilder = new StringBuilder();
        for ( int i = 0; i < segmentsOfNewPath.size() - 1; i++ )
            suffixPathBuilder.append( FORWARD_SLASH + segmentsOfNewPath.get(i).getPath() );
        String pathDeleteAnEntity = GenericOpencellRestfulAPIv2.API_VERSION + suffixPathBuilder.toString();

        if ( GenericOpencellRestfulAPIv2.MAP_NEW_PATH_AND_PATH_IBASE_RS.containsKey( pathDeleteAnEntity ) ) {
            String pathIBaseRS = GenericOpencellRestfulAPIv2.MAP_NEW_PATH_AND_PATH_IBASE_RS.get( pathDeleteAnEntity );
            URI redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                    + API_REST + pathIBaseRS + METHOD_DELETE
                    + segmentsOfNewPath.get( segmentsOfNewPath.size() - 1 ) );
System.out.println( "new URI redirect DELETE AN ENTITY : " + redirectURI.getPath() );
            return httpClient.target( redirectURI ).request().delete();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
