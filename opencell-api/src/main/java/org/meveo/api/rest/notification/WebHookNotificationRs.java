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

package org.meveo.api.rest.notification;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.notification.WebHookDto;
import org.meveo.api.dto.response.notification.GetWebHookNotificationResponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 * @author Edward P. Legaspi
 **/
@Path("/notification/webhook")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface WebHookNotificationRs extends IBaseRs {

    /**
     * Create a new web hook notification
     * 
     * @param postData The web hook notification's data
     * @return Request processing status
     */
    @POST
    @Path("/")
    ActionStatus create(WebHookDto postData);

    /**
     * Update an existing web hook notification
     * 
     * @param postData The web hook notification's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
    ActionStatus update(WebHookDto postData);

    /**
     * Find a web hook notification with a given code
     * 
     * @param notificationCode The web hook notification's code
     * @return  WebHook Notification Response data
     */
    @GET
    @Path("/")
    GetWebHookNotificationResponseDto find(@QueryParam("notificationCode") String notificationCode);

    /**
     * Remove an existing web hook notification with a given code
     * 
     * @param notificationCode The web hook notification's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{notificationCode}")
    ActionStatus remove(@PathParam("notificationCode") String notificationCode);

    /**
     * Create new or update an existing web hook notification with a given code
     * 
     * @param postData The web hook notification's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(WebHookDto postData);

    /**
     * Enable a Webhook notification with a given code
     * 
     * @param code Webhook notification code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
    ActionStatus enable(@PathParam("code") String code);

    /**
     * Disable a Webhook notification with a given code
     * 
     * @param code Webhook notification code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
    ActionStatus disable(@PathParam("code") String code);

}
