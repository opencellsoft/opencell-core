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

package org.meveo.api.rest.impl;

import org.meveo.admin.util.ResourceBundle;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.response.BaseResponse;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.rest.IBaseRs;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.crm.Provider;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.util.ApplicationProvider;
import org.meveo.util.MeveoParamBean;
import org.meveo.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

/**
 * @author Edward P. Legaspi
 **/
public abstract class BaseRs implements IBaseRs {

    protected Logger log = LoggerFactory.getLogger(this.getClass());

    @Inject
    @MeveoParamBean
    protected ParamBean paramBean;

    @Context
    protected HttpServletRequest httpServletRequest;

    // one way to get HttpServletResponse
    @Context
    protected HttpServletResponse httpServletResponse;

    @Inject
    private ResourceBundle resourceMessages;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    @Inject
    @ApplicationProvider
    protected Provider appProvider;

    protected final String RESPONSE_DELIMITER = " - ";



    public ActionStatus index() {
        return new ActionStatus(ActionStatusEnum.SUCCESS, "Opencell Rest API version " + Version.appVersion + " commit " + Version.buildNumber);
    }

    /**
     * Returns the authenticated user.
     * 
     * @return action status.
     */
    @GET
    @Path("/user")
    public ActionStatus user() {
        return new ActionStatus(ActionStatusEnum.SUCCESS, "WS User is=" + currentUser.getUserName());
    }

    protected Response.ResponseBuilder createResponseFromMeveoApiException(MeveoApiException e, ActionStatus result) {
        Response.ResponseBuilder responseBuilder = null;

        if (e instanceof EntityDoesNotExistsException) {
            responseBuilder = Response.status(Response.Status.NOT_FOUND).entity(result);
        } else if (e instanceof EntityAlreadyExistsException) {
            responseBuilder = Response.status(Response.Status.FOUND).entity(result);
        } else if (e instanceof MissingParameterException) {
            responseBuilder = Response.status(Response.Status.PRECONDITION_FAILED).entity(result);
        } else {
            responseBuilder = Response.status(Response.Status.BAD_REQUEST).entity(result);
        }

        return responseBuilder;
    }

    protected ResponseBuilder createResponseFromMeveoApiException(MeveoApiException e, BaseResponse result) {
        Response.ResponseBuilder responseBuilder = null;

        if (e instanceof EntityDoesNotExistsException) {
            responseBuilder = Response.status(Response.Status.NOT_FOUND).entity(result);
        } else if (e instanceof EntityAlreadyExistsException) {
            responseBuilder = Response.status(Response.Status.FOUND).entity(result);
        } else if (e instanceof MissingParameterException) {
            responseBuilder = Response.status(Response.Status.PRECONDITION_FAILED).entity(result);
        } else {
            responseBuilder = Response.status(Response.Status.BAD_REQUEST).entity(result);
        }

        return responseBuilder;
    }

    /**
     * Process exception and update status of response
     * 
     * @param e Exception
     * @param status Status dto to update
     */
    protected void processException(Exception e, ActionStatus status) {
        new ExceptionProcessorRs(resourceMessages).process(e, status);
    }
    

    protected Response errorResponse(MeveoApiException e, ActionStatus result) {
		if(result==null) {
			result = new ActionStatus();
		}
		result.setStatus(ActionStatusEnum.FAIL);
		result.setMessage(e.getMessage());
		 return createResponseFromMeveoApiException(e, result).build();
	}
    protected Response errorResponse(MeveoApiException e) {
		ActionStatus result = new ActionStatus();
		return errorResponse(e, result);
	}

}