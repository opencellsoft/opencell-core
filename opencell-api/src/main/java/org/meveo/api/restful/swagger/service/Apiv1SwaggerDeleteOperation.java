package org.meveo.api.restful.swagger.service;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;

/**
 * @author Thang Nguyen
 */
public class Apiv1SwaggerDeleteOperation {

    public void setDelete(PathItem pathItem, Operation deleteOp, String aRFPath) {
        pathItem.setDelete(deleteOp);
    }
}
