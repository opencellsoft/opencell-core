package org.meveo.api.rest;

import java.util.Date;

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
import org.meveo.api.dto.CalendarDto;
import org.meveo.api.dto.response.BankingDateStatusResponse;
import org.meveo.api.dto.response.GetCalendarResponse;
import org.meveo.api.dto.response.ListCalendarResponse;
import org.meveo.api.serialize.RestDateParam;

/**
 * @author Edward P. Legaspi
 **/
@Path("/calendar")
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
    ActionStatus create(CalendarDto postData);

    /**
     * Update calendar.
     * 
     * @param postData calendar infos
     * @return Request processing status
     */
    @PUT
    @Path("/")
    ActionStatus update(CalendarDto postData);

    /**
     * Search for calendar with a given code.
     * 
     * @param calendarCode The calendar's code
     * @return calendar if exists
     */
    @GET
    @Path("/")
    GetCalendarResponse find(@QueryParam("calendarCode") String calendarCode);
    
    /**
     * Gets the banking date status.
     *
     * @param date the date to check if is a working date or not
     * @return the banking date status
     */
    @GET
    @Path("/bankingDateStatus")
    BankingDateStatusResponse getBankingDateStatus(@QueryParam("date") @RestDateParam Date date);

    /**
     * Retrieve a list of all calendars.
     * 
     * @return list of all calendars
     */
    @GET 
    @Path("/list")
    ListCalendarResponse list();

    /**
     * Remove calendar with a given code.
     * 
     * @param calendarCode The calendar's code
     * @return action result
     */
    @DELETE 
    @Path("/{calendarCode}")
    ActionStatus remove(@PathParam("calendarCode") String calendarCode);

    /**
     * Create new or update an existing calendar with a given code.
     * 
     * @param postData The calendars data
     * @return Request processing status
     */
    @POST 
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(CalendarDto postData);

}
