package org.meveo.api.rest;

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
import org.meveo.api.dto.response.GetCalendarResponse;
import org.meveo.api.dto.response.ListCalendarResponse;

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
    @Path("/")
    @POST
    public ActionStatus create(CalendarDto postData);

    /**
     * Update calendar.
     * 
     * @param postData
     * @return Request processing status
     */
    @Path("/")
    @PUT
    public ActionStatus update(CalendarDto postData);

    /**
     * Search for calendar with a given code.
     * 
     * @param calendarCode The calendar's code
     * @return
     */
    @Path("/")
    @GET
    public GetCalendarResponse find(@QueryParam("calendarCode") String calendarCode);

    /**
     * Retrieve a list of all calendars.
     * 
     * @return
     */
    @Path("/list")
    @GET
    public ListCalendarResponse list();

    /**
     * Remove calendar with a given code.
     * 
     * @param calendarCode The calendar's code
     * @return
     */
    @Path("/{calendarCode}")
    @DELETE
    public ActionStatus remove(@PathParam("calendarCode") String calendarCode);

    /**
     * Create new or update an existing calendar with a given code
     * 
     * @param postData The calendars data
     * @return Request processing status
     */
    @Path("/createOrUpdate")
    @POST
    public ActionStatus createOrUpdate(CalendarDto postData);

}
