package org.meveo.api.rest.exception;

import org.meveo.api.dto.ActionStatus;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class NotFoundException extends WebApplicationException {
     public NotFoundException(ActionStatus status) {
         super(Response.status(Response.Status.NOT_FOUND)
             .entity(status).type(MediaType.APPLICATION_JSON_TYPE).build());
     }
}