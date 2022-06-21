package org.meveo.api.restful.swagger.service;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.PathParameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.meveo.api.restful.GenericOpencellRestfulAPIv1;
import org.meveo.api.restful.swagger.ApiRestSwaggerGeneration;

import java.util.List;

/**
 * @author Thang Nguyen
 */
public class Apiv1SwaggerGetOperation {

    public String setGet(PathItem pathItem, Operation getOp, String aRFPath) {
        String[] aRFPathSplit = aRFPath.split(ApiRestSwaggerGeneration.FORWARD_SLASH);
        StringBuilder getAnEntityRFPathBuilder = new StringBuilder();
        for (int i = 1; i < aRFPathSplit.length - 1; i++ ) {
            getAnEntityRFPathBuilder.append(ApiRestSwaggerGeneration.FORWARD_SLASH).append( aRFPathSplit[i] );
        }
        String getAnEntityRFPath = getAnEntityRFPathBuilder.toString();

        // In case of retrieving all entities
        if ( GenericOpencellRestfulAPIv1.MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.containsKey( aRFPath ) ) {
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
                    String entityCode = aRFPathSplit[ aRFPathSplit.length - 1 ];
                    entityCode = entityCode.substring( 1, entityCode.length() - 1 ); // Remove open accolade "{" and close accolade "}"

                    if (!param.getName().equals("inheritCF") && !param.getName().equals("query")
                        && !param.getName().equals("fields") && !param.getName().equals("offset")
                        && !param.getName().equals("limit") && !param.getName().equals("sortBy")
                        && !param.getName().equals("sortOrder") && !param.getName().equals("loadOfferServiceTemplate")
                        && !param.getName().equals("loadOfferProductTemplate") && !param.getName().equals("loadServiceChargeTemplate")
                        && !param.getName().equals("loadProductChargeTemplate")  && !param.getName().equals("loadServiceChargeTemplate")
                        && !param.getName().equals("loadServiceChargeTemplate") && !param.getName().equals("offerTemplateCode")
                        && !param.getName().equals("validFrom") && !param.getName().equals("validTo")
                        && !param.getName().equals("loadOfferServiceTemplate") && !param.getName().equals("loadOfferProductTemplate")
                        && !param.getName().equals("loadServiceChargeTemplate") && !param.getName().equals("loadProductChargeTemplate")
                        && !param.getName().equals("loadAllowedDiscountPlan") && !param.getName().equals("loadProductChargeTemplate")) {

                        if ( param.getName().equals( entityCode ) ) {
                            if (param.getIn().equals("query")) {
//                                PathParameter aPathParam = new PathParameter();
//                                aPathParam.setName(param.getName());
//                                Schema<String> schema = new Schema<>();
//                                schema.setType("string");
//                                aPathParam.setSchema(schema);
//                                parameters.add(aPathParam);
//                                parameters.remove(param);

                                param.setIn("path");
                                break;
                            }
                        }
                        else {
                            if (param.getIn().equals("query")) {
                                param.setIn("path");
                            }

                            aRFPath = getAnEntityRFPath + ApiRestSwaggerGeneration.FORWARD_SLASH
                                    + ApiRestSwaggerGeneration.OPEN_BRACE + param.getName()
                                    + ApiRestSwaggerGeneration.CLOSE_BRACE;
                            break;
                        }
                    }

                }
                getOp.setParameters(parameters);
            }
        }

        pathItem.setGet(getOp);

        return aRFPath;
    }
}
