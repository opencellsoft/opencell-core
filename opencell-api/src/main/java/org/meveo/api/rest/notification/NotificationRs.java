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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Hidden;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.notification.ScriptNotificationDto;
import org.meveo.api.dto.response.notification.GetScriptNotificationResponseDto;
import org.meveo.api.dto.response.notification.InboundRequestsResponseDto;
import org.meveo.api.dto.response.notification.NotificationHistoriesResponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 * REST service for managing Notification object.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/notification")
@Tag(name = "Notification", description = "@%Notification")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface NotificationRs extends IBaseRs {

    /**
     * Create a new notification
     * 
     * @param postData The notification's data
     * @return Request processing status
     */
    @POST
    @Path("/")
	@Operation(
			summary=" Create a new notification  ",
			description=" Create a new notification  ",
			operationId="    POST_Notification_create",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(ScriptNotificationDto postData);

    /**
     * Update an existing notification
     * 
     * @param postData The notification's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Update an existing notification  ",
			description=" Update an existing notification  ",
			operationId="    PUT_Notification_update",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(ScriptNotificationDto postData);

    /**
     * Find a notification with a given code
     * 
     * @param notificationCode The notification's code
     * @return Script notification information
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Find a notification with a given code  ",
			description=" Find a notification with a given code  ",
			operationId="    GET_Notification_search",
			responses= {
				@ApiResponse(description=" Script notification information ",
						content=@Content(
									schema=@Schema(
											implementation= GetScriptNotificationResponseDto.class
											)
								)
				)}
	)
    GetScriptNotificationResponseDto find(@QueryParam("notificationCode") String notificationCode);

    /**
     * Remove an existing notification with a given code
     * 
     * @param notificationCode The notification's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{notificationCode}")
	@Operation(
			summary=" Remove an existing notification with a given code  ",
			description=" Remove an existing notification with a given code  ",
			operationId="    DELETE_Notification_{notificationCode}",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus remove(@PathParam("notificationCode") String notificationCode);

    /**
     * Enable a Script type notification with a given code
     * 
     * @param code Script type notification code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
	@Operation(
			summary=" Enable a Script type notification with a given code  ",
			description=" Enable a Script type notification with a given code  ",
			operationId="    POST_Notification_{code}_enable",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus enable(@PathParam("code") String code);

    /**
     * Disable a Script type notification with a given code
     * 
     * @param code Script type notification code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
	@Operation(
			summary=" Disable a Script type notification with a given code  ",
			description=" Disable a Script type notification with a given code  ",
			operationId="    POST_Notification_{code}_disable",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus disable(@PathParam("code") String code);

    /**
     * List the notification history
     * 
     * @return Notification history list
     */
    @GET
    @Path("/listNotificationHistory")
	@Operation(
			summary=" List the notification history  ",
			description=" List the notification history  ",
			operationId="    GET_Notification_listNotificationHistory",
			responses= {
				@ApiResponse(description=" Notification history list ",
						content=@Content(
									schema=@Schema(
											implementation= NotificationHistoriesResponseDto.class
											)
								)
				)}
	)
    NotificationHistoriesResponseDto listNotificationHistory();

    /**
     * List inbound requests
     * 
     * @return A list of inbound requests
     */
    @GET
    @Path("/listInboundRequest")
	@Operation(
			summary=" List inbound requests  ",
			description=" List inbound requests  ",
			operationId="    GET_Notification_listInboundRequest",
			responses= {
				@ApiResponse(description=" A list of inbound requests ",
						content=@Content(
									schema=@Schema(
											implementation= InboundRequestsResponseDto.class
											)
								)
				)}
	)
    InboundRequestsResponseDto listInboundRequest();

    /**
     * Create new or update an existing notification with a given code
     * 
     * @param postData The notification's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Create new or update an existing notification with a given code  ",
			description=" Create new or update an existing notification with a given code  ",
			operationId="    POST_Notification_createOrUpdate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdate(ScriptNotificationDto postData);
}
