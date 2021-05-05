package org.meveo.api.restful.swagger.service;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.PathParameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.meveo.api.restful.GenericOpencellRestfulAPIv1;

import java.util.List;

/**
 * @author Thang Nguyen
 */
public class Apiv1SwaggerGetOpration {

    private static final String FORWARD_SLASH = "/";

    public void setGet(PathItem pathItem, Operation getOp, String aRFPath) {
        String[] aRFPathSplit = aRFPath.split(FORWARD_SLASH);
        StringBuilder getAnEntityRFPathBuilder = new StringBuilder();
        for (int i = 1; i < aRFPathSplit.length - 1; i++ ) {
            getAnEntityRFPathBuilder.append( FORWARD_SLASH ).append( aRFPathSplit[i] );
        }
        String getAnEntityRFPath = getAnEntityRFPathBuilder.toString();

        // In case of retrieving all entities
        if ( GenericOpencellRestfulAPIv1.MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.containsKey( aRFPath ) ) {
//            ApiResponses responses = getOp.getResponses() != null ? getOp.getResponses() : new ApiResponses();
//            for ( Map.Entry<String, ApiResponse>entry : responses.entrySet() ) {
//                ApiResponse aResponse = entry.getValue();
//            }
            ApiResponses responses = new ApiResponses();
            ApiResponse successfulRequest = new ApiResponse();
            successfulRequest.setDescription("results successfully retrieved");
            responses.put( "200", successfulRequest );
            ApiResponse badRequest = new ApiResponse();
            badRequest.setDescription("bad request as URL not well formed or entity unrecognized");
            responses.put( "400", badRequest );
            getOp.setResponses(responses);
        }
        // In case of retrieving a particular entity, transform a queryParam to a pathParam. For this, do the following :
        // - Add a pathParam
        // - Remove the queryParam
        else if ( GenericOpencellRestfulAPIv1.MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.containsKey( getAnEntityRFPath ) ) {
            if ( getOp.getParameters() != null ) {
                List<Parameter> parameters = getOp.getParameters();
                for ( Parameter param : parameters ) {
                    String entityCode = aRFPath.split(FORWARD_SLASH)[ aRFPath.split(FORWARD_SLASH).length - 1 ];
                    entityCode = entityCode.substring( 1, entityCode.length() - 1 ); // Remove open accolade "{" and close accolade "}"
                    switch ( param.getIn() ) {
                        case "query" :
                            if ( param.getName().equals( entityCode ) ) {
                                PathParameter pathParameter = new PathParameter();
                                pathParameter.setName( param.getName() );
                                parameters.add(pathParameter);
                                parameters.remove(param);
                            }
                            break;
                    }
                    break;
                }
                getOp.setParameters(parameters);
            }
        }

        pathItem.setGet(getOp);
    }
}
