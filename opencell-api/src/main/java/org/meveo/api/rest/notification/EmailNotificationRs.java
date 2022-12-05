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
import org.meveo.api.dto.notification.EmailNotificationDto;
import org.meveo.api.dto.response.notification.GetEmailNotificationResponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 * @author Edward P. Legaspi
 **/
@Path("/notification/email")
@Tag(name = "EmailNotification", description = "@%EmailNotification")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface EmailNotificationRs extends IBaseRs {

    /**
     * Create a new email notification
     * 
     * @param postData The email notification's data
     * @return Request processing status
     */
    @POST
    @Path("/")
	@Operation(
			summary=" Create a new email notification  ",
			description=" Create a new email notification  ",
			operationId="    POST_EmailNotification_create",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(EmailNotificationDto postData);

    /**
     * Update an existing email notification
     * 
     * @param postData The email notification's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Update an existing email notification  ",
			description=" Update an existing email notification  ",
			operationId="    PUT_EmailNotification_update",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(EmailNotificationDto postData);

    /**
     * Find a email notification with a given code
     * 
     * @param notificationCode The email notification's code
     * @return  Email Notification Response data
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Find a email notification with a given code  ",
			description=" Find a email notification with a given code  ",
			operationId="    GET_EmailNotification_search",
			responses= {
				@ApiResponse(description="  Email Notification Response data ",
						content=@Content(
									schema=@Schema(
											implementation= GetEmailNotificationResponseDto.class
											)
								)
				)}
	)
    GetEmailNotificationResponseDto find(@QueryParam("notificationCode") String notificationCode);

    /**
     * Remove an existing email notification with a given code
     * 
     * @param notificationCode The email notification's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{notificationCode}")
	@Operation(
			summary=" Remove an existing email notification with a given code  ",
			description=" Remove an existing email notification with a given code  ",
			operationId="    DELETE_EmailNotification_{notificationCode}",
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
     * Create new or update an existing email notification with a given code
     * 
     * @param postData The email notification's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Create new or update an existing email notification with a given code  ",
			description=" Create new or update an existing email notification with a given code  ",
			operationId="    POST_EmailNotification_createOrUpdate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdate(EmailNotificationDto postData);

    /**
     * Enable a Email notification with a given code
     * 
     * @param code Email notification code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
	@Operation(
			summary=" Enable a Email notification with a given code  ",
			description=" Enable a Email notification with a given code  ",
			operationId="    POST_EmailNotification_{code}_enable",
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
     * Disable a Email notification with a given code
     * 
     * @param code Email notification code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
	@Operation(
			summary=" Disable a Email notification with a given code  ",
			description=" Disable a Email notification with a given code  ",
			operationId="    POST_EmailNotification_{code}_disable",
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

}
