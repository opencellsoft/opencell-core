/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api.rest;

import javax.ejb.Singleton;
import javax.ws.rs.NotAllowedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.api.validation.Validation;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

@Provider
@Singleton
public class JaxRsExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception e) {

        Logger log = LoggerFactory.getLogger(getClass());
        log.error("REST request failed with an error {}", e);

        if (e instanceof UnrecognizedPropertyException) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ActionStatus(ActionStatusEnum.FAIL, MeveoApiErrorCodeEnum.INVALID_PARAMETER, e.getMessage())).build();

        } else if (e instanceof NotFoundException || e instanceof NotAllowedException) {
            return Response.status(Response.Status.NOT_FOUND).build();

        } else if (e instanceof JsonParseException || e instanceof JsonMappingException) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ActionStatus(ActionStatusEnum.FAIL, MeveoApiErrorCodeEnum.INVALID_PARAMETER, e.getMessage())).build();

        }
        return buildResponse(unwrapException(e), MediaType.TEXT_PLAIN, Status.INTERNAL_SERVER_ERROR);

        // if (exception instanceof ConstraintDefinitionException) {
        // return buildResponse(unwrapException(exception), MediaType.TEXT_PLAIN, Status.INTERNAL_SERVER_ERROR);
        // }
        // if (exception instanceof ConstraintDeclarationException) {
        // return buildResponse(unwrapException(exception), MediaType.TEXT_PLAIN, Status.INTERNAL_SERVER_ERROR);
        // }
        // if (exception instanceof GroupDefinitionException) {
        // return buildResponse(unwrapException(exception), MediaType.TEXT_PLAIN, Status.INTERNAL_SERVER_ERROR);
        // }
        // if (exception instanceof ResteasyViolationException) {
        // ResteasyViolationException resteasyViolationException = ResteasyViolationException.class.cast(exception);
        // Exception e = resteasyViolationException.getException();
        // if (e != null) {
        // return buildResponse(unwrapException(e), MediaType.TEXT_PLAIN, Status.INTERNAL_SERVER_ERROR);
        // } else if (resteasyViolationException.getReturnValueViolations().size() == 0) {
        // return buildViolationReportResponse(resteasyViolationException, Status.BAD_REQUEST);
        // } else {
        // return buildViolationReportResponse(resteasyViolationException, Status.INTERNAL_SERVER_ERROR);
        // }
        // }

    }

    protected Response buildResponse(Object entity, String mediaType, Status status) {
        ResponseBuilder builder = Response.status(status).entity(entity);
        builder.type(MediaType.TEXT_PLAIN);
        builder.header(Validation.VALIDATION_HEADER, "true");
        return builder.build();
    }

    protected String unwrapException(Throwable t) {
        StringBuffer sb = new StringBuffer();
        doUnwrapException(sb, t);
        return sb.toString();
    }

    private void doUnwrapException(StringBuffer sb, Throwable t) {
        if (t == null) {
            return;
        }
        sb.append(t.toString());
        if (t.getCause() != null && t != t.getCause()) {
            sb.append('[');
            doUnwrapException(sb, t.getCause());
            sb.append(']');
        }
    }

    // protected Response buildViolationReportResponse(ResteasyViolationException exception, Status status) {
    // ResponseBuilder builder = Response.status(status);
    // builder.header(Validation.VALIDATION_HEADER, "true");
    //
    // // Check standard media types.
    // MediaType mediaType = getAcceptMediaType(exception.getAccept());
    // if (mediaType != null) {
    // builder.type(mediaType);
    // builder.entity(new ViolationReport(exception));
    // return builder.build();
    // }
    //
    // // Default media type.
    // builder.type(MediaType.TEXT_PLAIN);
    // builder.entity(exception.toString());
    // return builder.build();
    // }

    // private MediaType getAcceptMediaType(List<MediaType> accept) {
    // Iterator<MediaType> it = accept.iterator();
    // while (it.hasNext()) {
    // MediaType mt = it.next();
    // /*
    // * application/xml media type causes an exception: org.jboss.resteasy.core.NoMessageBodyWriterFoundFailure: Could not find MessageBodyWriter for response object of
    // * type: org.jboss.resteasy.api.validation.ViolationReport of media type: application/xml
    // */
    // /*
    // * if (MediaType.APPLICATION_XML_TYPE.getType().equals(mt.getType()) && MediaType.APPLICATION_XML_TYPE.getSubtype().equals(mt.getSubtype())) { return
    // * MediaType.APPLICATION_XML_TYPE; }
    // */
    // if (MediaType.APPLICATION_JSON_TYPE.getType().equals(mt.getType()) && MediaType.APPLICATION_JSON_TYPE.getSubtype().equals(mt.getSubtype())) {
    // return MediaType.APPLICATION_JSON_TYPE;
    // }
    // }
    // return null;
    // }
}