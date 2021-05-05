package org.meveo.api.restful.services;

import org.meveo.api.restful.GenericOpencellRestfulAPIv1;

import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * @author Thang Nguyen
 */
public class Apiv1DeleteService {

    private static final String METHOD_DELETE = "/";

    private static final String FORWARD_SLASH = "/";
    private static final String QUERY_PARAM_SEPARATOR = "?";

    private static final String API_REST = "api/rest";

    /*
     * Function used to delete a particular entity
     */
    public Response deleteEntity(UriInfo uriInfo) throws URISyntaxException {
        List<PathSegment> segmentsOfPathAPIv1 = uriInfo.getPathSegments();
        URI redirectURI;
        StringBuilder suffixPathBuilder = new StringBuilder();
        for (int i = 0; i < segmentsOfPathAPIv1.size() - 1; i++ )
            suffixPathBuilder.append( FORWARD_SLASH + segmentsOfPathAPIv1.get(i).getPath() );
        String deletePath = GenericOpencellRestfulAPIv1.API_VERSION + suffixPathBuilder;

        if ( GenericOpencellRestfulAPIv1.MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.containsKey( deletePath ) ) {
            String pathIBaseRS = GenericOpencellRestfulAPIv1.MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.get(deletePath);
            String entityCode = segmentsOfPathAPIv1.get(segmentsOfPathAPIv1.size() - 1).getPath();

            if ( pathIBaseRS.equals( Apiv1ConstantDictionary.PAYMENT_METHOD ) )
                redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                        + API_REST + pathIBaseRS + QUERY_PARAM_SEPARATOR + "id=" + entityCode);
            else if ( pathIBaseRS.equals( Apiv1ConstantDictionary.DISCOUNT_PLAN ) )
                redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                        + API_REST + pathIBaseRS + QUERY_PARAM_SEPARATOR + "discountPlanCode=" + entityCode);
            else if ( pathIBaseRS.equals( Apiv1ConstantDictionary.OFFER_TEMPLATE_CATEGORY ) )
                redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                        + API_REST + pathIBaseRS + QUERY_PARAM_SEPARATOR + "offerTemplateCategoryCode=" + entityCode);
            else if ( pathIBaseRS.equals( Apiv1ConstantDictionary.CHART ) )
                redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                        + API_REST + pathIBaseRS + QUERY_PARAM_SEPARATOR + "chartCode=" + entityCode);
            else
                redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                        + API_REST + pathIBaseRS + METHOD_DELETE + entityCode);
            return Response.temporaryRedirect( redirectURI ).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

}
