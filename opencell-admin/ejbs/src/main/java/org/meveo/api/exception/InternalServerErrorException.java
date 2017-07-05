package org.meveo.api.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class InternalServerErrorException extends WebApplicationException {
     public  InternalServerErrorException(String message) {
         super(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
             .entity(message).type(MediaType.APPLICATION_JSON_TYPE).build());
     }
}