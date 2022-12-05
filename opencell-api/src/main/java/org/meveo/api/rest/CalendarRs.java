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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Hidden;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.CalendarDto;
import org.meveo.api.dto.response.BankingDateStatusResponse;
import org.meveo.api.dto.response.GetCalendarResponse;
import org.meveo.api.dto.response.ListCalendarResponse;
import org.meveo.api.serialize.RestDateParam;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.Date;

/**
 * @author Edward P. Legaspi
 **/
@Path("/calendar")
@Tag(name = "Calendar", description = "@%Calendar")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface CalendarRs extends IBaseRs {

    /**
     * Create a new calendar.
     * 
     * @param postData The calendar's data
     * @return Request processing status
     */
    @POST
    @Path("/")
	@Operation(
			summary=" Create a new calendar.  ",
			description=" Create a new calendar.  ",
			operationId="    POST_Calendar_create",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(CalendarDto postData);

    /**
     * Update calendar.
     * 
     * @param postData calendar infos
     * @return Request processing status
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Update calendar.  ",
			description=" Update calendar.  ",
			operationId="    PUT_Calendar_update",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(CalendarDto postData);

    /**
     * Search for calendar with a given code.
     * 
     * @param calendarCode The calendar's code
     * @return calendar if exists
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Search for calendar with a given code.  ",
			description=" Search for calendar with a given code.  ",
			operationId="    GET_Calendar_search",
			responses= {
				@ApiResponse(description=" calendar if exists ",
						content=@Content(
									schema=@Schema(
											implementation= GetCalendarResponse.class
											)
								)
				)}
	)
    GetCalendarResponse find(@QueryParam("calendarCode") String calendarCode);
    
    /**
     * Gets the banking date status.
     *
     * @param date the date to check if is a working date or not
     * @return the banking date status
     */
    @GET
    @Path("/bankingDateStatus")
	@Operation(
			summary=" Gets the banking date status. ",
			description=" Gets the banking date status. ",
			operationId="    GET_Calendar_bankingDateStatus",
			responses= {
				@ApiResponse(description=" the banking date status ",
						content=@Content(
									schema=@Schema(
											implementation= BankingDateStatusResponse.class
											)
								)
				)}
	)
    BankingDateStatusResponse getBankingDateStatus(@QueryParam("date") @RestDateParam Date date);

    /**
     * Retrieve a list of all calendars.
     * 
     * @return list of all calendars
     */
    @GET 
    @Path("/list")
	@Operation(
			summary=" Retrieve a list of all calendars.  ",
			description=" Retrieve a list of all calendars.  ",
			operationId="    GET _Calendar_list",
			responses= {
				@ApiResponse(description=" list of all calendars ",
						content=@Content(
									schema=@Schema(
											implementation= ListCalendarResponse.class
											)
								)
				)}
	)
    ListCalendarResponse list();

    /**
     * List Calendars matching a given criteria
     *
     * @return List of Calendars
     */
    @GET
    @Path("/listGetAll")
	@Operation(
			summary=" List Calendars matching a given criteria ",
			description=" List Calendars matching a given criteria ",
			operationId="    GET_Calendar_listGetAll",
			responses= {
				@ApiResponse(description=" List of Calendars ",
						content=@Content(
									schema=@Schema(
											implementation= ListCalendarResponse.class
											)
								)
				)}
	)
    ListCalendarResponse listGetAll();

    /**
     * Remove calendar with a given code.
     * 
     * @param calendarCode The calendar's code
     * @return action result
     */
    @DELETE 
    @Path("/{calendarCode}")
	@Operation(
			summary=" Remove calendar with a given code.  ",
			description=" Remove calendar with a given code.  ",
			operationId="    DELETE _Calendar_{calendarCode}",
			responses= {
				@ApiResponse(description=" action result ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus remove(@PathParam("calendarCode") String calendarCode);

    /**
     * Create new or update an existing calendar with a given code.
     * 
     * @param postData The calendars data
     * @return Request processing status
     */
    @POST 
    @Path("/createOrUpdate")
	@Operation(
			summary=" Create new or update an existing calendar with a given code.  ",
			description=" Create new or update an existing calendar with a given code.  ",
			operationId="    POST _Calendar_createOrUpdate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdate(CalendarDto postData);

}
