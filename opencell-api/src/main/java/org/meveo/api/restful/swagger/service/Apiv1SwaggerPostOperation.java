package org.meveo.api.restful.swagger.service;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.meveo.api.restful.GenericOpencellRestfulAPIv1;
import org.meveo.api.restful.swagger.ApiRestSwaggerGeneration;

/**
 * @author Thang Nguyen
 */
public class Apiv1SwaggerPostOperation {

    public void setPost(PathItem pathItem, Operation postOp, String aRFPath) {
        pathItem.setPost(postOp);
    }
}
