package org.meveo.api.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class NotAllowedException extends WebApplicationException {
     public NotAllowedException(String message) {
         super(Response.status(Response.Status.METHOD_NOT_ALLOWED)
             .entity(message).type(MediaType.APPLICATION_JSON_TYPE).build());
     }
}