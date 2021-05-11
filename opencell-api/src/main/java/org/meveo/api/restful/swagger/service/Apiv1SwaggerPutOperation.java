package org.meveo.api.restful.swagger.service;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.meveo.api.restful.GenericOpencellRestfulAPIv1;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thang Nguyen
 */
public class Apiv1SwaggerPutOperation {

    private static final String FORWARD_SLASH = "/";

    public void setPut(PathItem pathItem, Operation putOp, String aRFPath) {
        String[] aRFPathSplit = aRFPath.split(FORWARD_SLASH);
        StringBuilder pathUpdateAnEntityBuilder = new StringBuilder();
        for (int i = 1; i < aRFPathSplit.length - 1; i++ ) {
            pathUpdateAnEntityBuilder.append( FORWARD_SLASH ).append( aRFPathSplit[i] );
        }
        String updateAnEntityRFPath = pathUpdateAnEntityBuilder.toString();

        // In case of retrieving a particular entity, transform a queryParam to a pathParam. For this, do the following :
        // - Add a pathParam
        // - Remove the queryParam
        if ( GenericOpencellRestfulAPIv1.MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.containsKey( updateAnEntityRFPath ) ) {
            List<Parameter> parameters = putOp.getParameters() != null ? putOp.getParameters() : new ArrayList<>();

            // create a new path parameter and add it to put operation in case of updating an entity
            Parameter aPathParam = new Parameter();
            aPathParam.setIn("path");
            String entityCode = aRFPathSplit[aRFPathSplit.length - 1];
            entityCode = entityCode.substring( 1, entityCode.length() - 1 ); // Remove open accolade "{" and close accolade "}"
            aPathParam.setName( entityCode );
            aPathParam.setDescription( "Add the " + entityCode + " here" );
            parameters.add(aPathParam);

            putOp.setParameters(parameters);
        }

        pathItem.setPut(putOp);
    }
}
