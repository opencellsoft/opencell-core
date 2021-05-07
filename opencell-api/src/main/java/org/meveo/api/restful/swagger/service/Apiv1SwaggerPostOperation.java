package org.meveo.api.restful.swagger.service;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;

/**
 * @author Thang Nguyen
 */
public class Apiv1SwaggerPostOperation {

    public void setPost(PathItem pathItem, Operation postOp, String aRFPath) {
        pathItem.setPost(postOp);
    }
}
